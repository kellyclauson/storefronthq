package com.islandsoftware.storefronhq.tools;

import com.islandsoftware.storefronhq.etsy.EtsyClient;
import com.islandsoftware.storefronhq.etsy.SpindleAndRoseEtsyClient;
import com.islandsoftware.storefronhq.etsy.model.ListingsResult;
import com.islandsoftware.storefronhq.etsy.model.inventory.InventoryOffering;
import com.islandsoftware.storefronhq.etsy.model.inventory.InventoryProduct;
import com.islandsoftware.storefronhq.etsy.model.inventory.InventoryResponse;
import com.islandsoftware.storefronhq.GoogleSheets;
import com.islandsoftware.storefronhq.orderprocessing.ProductInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckEtsyInventory
{
   private static final Logger LOGGER = LoggerFactory.getLogger(CheckEtsyInventory.class);
   public static void main(String[] args)
   {
      try
      {
         Utils.write("C:\\tmp\\etsyInventory.csv", fromEtsy());
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }

      //Utils.write("C:\\tmp\\soldItemsNotFoundInInventory.csv", calculateInventoryCost("c:\\tmp\\etsyInventoryCost.csv", "c:\\tmp\\EtsySoldOrderItems2019.csv"));
      //Utils.write("C:\\tmp\\adjustedInventory.csv", calculateInventoryCost("c:\\tmp\\etsyInventoryCost.csv", "c:\\tmp\\EtsySoldOrderItems2019.csv"));
      //finalCalculation("C:\\tmp\\adjustedInventory.csv");
      /*
      LocalDate date = LocalDate.now();
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
      Utils.write("C:\\tmp\\yardage-" + date.format(formatter) + ".csv", yardage());
      */
   }

   private static void finalCalculation(String adjustedInventoryFilename)
   {
      try
      {
         double inventoryCost = 0.0;
         List<String> lines = Files.readAllLines(Paths.get(adjustedInventoryFilename));
         int count = 0;
         for (String line : lines)
         {
            if (++count == 1)
            {
               continue;
            }
            AdjustedInventoryDetail detail = new AdjustedInventoryDetail(line);
            inventoryCost += ((detail.getCost() * detail.getQuantity()) + (detail.getShippingCost() * detail.getQuantity()));
         }
         LOGGER.info("Total Inventory Cost: {}", inventoryCost);
      }
      catch (Exception e)
      {
         LOGGER.error("error", e);
      }

   }

   private static String calculateInventoryCost(String inventoryFilename, String soldItemsFilename)
   {
      StringBuilder sb = new StringBuilder();
      Map<String, InventoryCost> inventory = new HashMap<>();
      try
      {
         List<String> lines = Files.readAllLines(Paths.get(inventoryFilename));
         int count = 0;
         for (String line : lines)
         {
            if (++count == 1)
            {
               continue;
            }
            InventoryCost ic = new InventoryCost(line);
            inventory.put(ic.getTitle(), ic);
         }
         LOGGER.info("Number of Inventory Items: {}", inventory.size());

         //sb.append("Title,Quantity,Variation").append("\n");
         lines = Files.readAllLines(Paths.get(soldItemsFilename));
         count = 0;
         for (String line : lines)
         {
            if (++count == 1)
            {
               continue;
            }
            //LOGGER.info("sold item line: {}", line);
            SoldItems items = new SoldItems(line);
            InventoryCost inventoryCost = inventory.get(items.getTitle());
            if (inventoryCost != null)
            {
               int soldQuantity = items.getQuantity();
               if (items.getVariation().equals("1/2"))
               {
                  inventoryCost.setHalfYardQuantity(inventoryCost.getHalfYardQuantity() + soldQuantity);
               }
               else
               {
                  inventoryCost.setHalfYardQuantity(inventoryCost.getHalfYardQuantity() + (soldQuantity * 2));
               }
            }
            else
            {
               //sb.append(items.getTitle()).append(",").append(items.getQuantity()).append(",").append(items.getVariation()).append("\n");
            }
         }

         sb.append("Title,1/2 Yard Quantity,1/2 Yard Cost,1/2 Shipping Cost").append("\n");
         for (InventoryCost inventoryCost : inventory.values())
         {
            sb.append(inventoryCost.getTitle()).append(",");
            sb.append(inventoryCost.getHalfYardQuantity()).append(",");
            sb.append(inventoryCost.getCostPerHalfYard()).append(",");
            sb.append(inventoryCost.getCostToShipHalfYard()).append("\n");
         }
      }
      catch (Exception e)
      {
         LOGGER.error("error", e);
      }
      return sb.toString();
   }

   private static String calculateInventoryCost()
   {
      StringBuilder sb = new StringBuilder();
      sb.append("Title,1 Yard Quantity,1/2 Yard Quantity,Cost Per Yard,Cost Per Half Yard,Cost To Ship 1 Yard,Cost To Ship 1/2 Yard").append("\n");
      try
      {
         Map<String, ProductInfo> products = GoogleSheets.readProductInfo(false);
         EtsyClient etsyClient = new SpindleAndRoseEtsyClient();
         Map<Long, String> id2Title = etsyClient.getId2Title();
         for (Map.Entry<Long, String> entry : id2Title.entrySet())
         {
            Long id = entry.getKey();
            String title = entry.getValue();
            sb.append(title).append(",");
            ProductInfo productInfo = Utils.getFromMasterMap(title, products);
            if (productInfo == null)
            {
               sb.append("productNotFound,productNotFound,NA,NA,NA,NA\n");
               continue;
            }

            InventoryResponse inventory = etsyClient.getInventory(id);
            try
            {
               InventoryProduct product = Utils.getProduct(inventory, "1 Yard");
               int oneYardQuantity = getQuantity(product);
               sb.append(oneYardQuantity).append(",");
               product = Utils.getProduct(inventory, "1/2 Yard");
               int halfYardQuantity = getQuantity(product);
               sb.append(halfYardQuantity).append(",");

               sb.append(productInfo.getCost()).append(",");
               sb.append(productInfo.getCost() / 2).append(",");
               sb.append(productInfo.getShippingCost()).append(",");
               sb.append(productInfo.getShippingCost() / 2).append("\n");
            }
            catch (Exception e)
            {
               sb.append("notfound,notfound,NA,NA,NA,NA\n");
            }
            Utils.sleep(500L);
         }
      }
      catch (Exception e)
      {
         LOGGER.error("calculateInventoryCost", e);
      }
      return sb.toString();
   }

   private static void toFile(Map<String, Long> titleToListingIdMap)
   {
      StringBuilder sb = new StringBuilder();
      sb.append("Title,SKU\n");
      int sku = 1000;
      for (String s : titleToListingIdMap.keySet())
      {
         sb.append(s).append(",SR-").append(++sku).append("\n");
      }
      Utils.write("C:\\tmp\\etsyTitles.csv", sb.toString());
   }

   private static String fromFile(String filename)
   {
      StringBuilder sb = new StringBuilder();
      sb.append("Title,1 Yard Quantity,1/2 Yard Quantity,Valid").append("\n");
      try
      {
         List<String> lines = Files.readAllLines(Paths.get(filename));
         int count = 0;
         for (String line : lines)
         {
            if (++count == 1)
            {
               continue;
            }
            String[] split = line.split(",");
            sb.append(split[0]).append(",");
            int oneYardQuantity = Integer.parseInt(split[1]);
            sb.append(oneYardQuantity).append(",");
            int halfYardQuantity = Integer.parseInt(split[2]);
            sb.append(halfYardQuantity).append(",");
            sb.append(isValid(oneYardQuantity, halfYardQuantity)).append("\n");
         }
      }
      catch (Exception e)
      {
         LOGGER.error("error", e);
      }
      return sb.toString();
   }

   private static String getEtsyVariationNames()
   {
      StringBuilder sb = new StringBuilder();
      sb.append("Title,Variation1,Variation2,Variation3,Variation4,Variation5").append("\n");
      try
      {
         EtsyClient etsyClient = new SpindleAndRoseEtsyClient();
         Map<String, ListingsResult> listings = etsyClient.listings();
         for (ListingsResult listing : listings.values())
         {
            Long id = listing.getListingId();
            sb.append(listing.getTitle()).append(",");
            InventoryResponse inventory = etsyClient.getInventory(id);
            try
            {
               InventoryProduct[] products = inventory.getInventoryResults().getProducts();
               for (InventoryProduct product : products)
               {
                  String value = product.getPropertyValues()[0].getValues()[0];
                  sb.append(value).append(",");
               }
               sb.append("\n");
            }
            catch (Exception e)
            {
               sb.append("notfound\n");
            }
            Utils.sleep(500L);
         }
      }
      catch (Exception e)
      {
         LOGGER.error("getEtsyVariationName", e);
      }
      return sb.toString();
   }

   private static String yardage()
   {
      StringBuilder sb = new StringBuilder();
      sb.append("Title,Variation,Price,Yards").append("\n");

      int count = 0;
      double yards = 0.0;
      EtsyClient etsyClient = new SpindleAndRoseEtsyClient();
      try
      {
         Map<Long, String> id2Title = etsyClient.getId2Title();
         for (Map.Entry<Long, String> entry : id2Title.entrySet())
         {
            double y = 0.0;
            Long id = entry.getKey();
            InventoryResponse inventory = etsyClient.getInventory(id);
            try
            {
               LOGGER.info("yardage: {} of {}", ++count, id2Title.size());
               InventoryProduct product = Utils.getProduct(inventory, "1/2 Yard");
               int quantity = getQuantity(product);
               y += ((double)quantity / 2.0);
               yards += y;
               sb.append(entry.getValue())
                  .append(",1/2 Yard,")
                  .append(product.getInventoryOfferings()[0].getInventoryOfferingPrice().getCurrencyFormattedShort())
                  .append(",")
                  .append(String.valueOf(y))
                  .append("\n");
            }
            catch (Exception e)
            {
               // maybe there is no 1/2 yard, get the 1 yard variation
               try
               {
                  InventoryProduct product = Utils.getProduct(inventory, "1 Yard");
                  int quantity = getQuantity(product);
                  y += quantity;
                  yards += y;
                  sb.append(entry.getValue())
                     .append(",1 Yard,")
                     .append(product.getInventoryOfferings()[0].getInventoryOfferingPrice().getCurrencyFormattedShort())
                     .append(",")
                     .append(String.valueOf(y))
                     .append("\n");
               }
               catch (Exception ee)
               {
                  continue;
               }
            }
            LOGGER.info("yardage={} total={}", y, yards);
            Utils.sleep(500L);
         }
         sb.append("TOTAL").append(",,,").append(String.valueOf(yards)).append("\n");
      }
      catch (Exception e)
      {
         LOGGER.error("yardage", e);
      }
      return sb.toString();
   }

   private static String fromEtsy()
   {
      StringBuilder sb = new StringBuilder();
      sb.append("Title,1 Yard Quantity,1/2 Yard Quantity,Valid").append("\n");
      EtsyClient etsyClient = new SpindleAndRoseEtsyClient();
      try
      {
         Map<Long, String> id2Title = etsyClient.getId2Title();
         for (Map.Entry<Long, String> entry : id2Title.entrySet())
         {
            Long id = entry.getKey();
            String title = entry.getValue();
            sb.append(title).append(",");
            InventoryResponse inventory = etsyClient.getInventory(id);
            try
            {
               InventoryProduct product = Utils.getProduct(inventory, "1 Yard");
               int oneYardQuantity = getQuantity(product);
               sb.append(oneYardQuantity).append(",");
               product = Utils.getProduct(inventory, "1/2 Yard");
               int halfYardQuantity = getQuantity(product);
               sb.append(halfYardQuantity).append(",");
               sb.append(isValid(oneYardQuantity, halfYardQuantity)).append("\n");
            }
            catch (Exception e)
            {
               sb.append("notfound,notfound,NA\n");
            }
            Utils.sleep(500L);
         }
      }
      catch (Exception e)
      {
         LOGGER.error("CheckEtsyInventory", e);
      }
      return sb.toString();
   }

   private static boolean isValid(int oneYardQuantity, int halfYardQuantity)
   {
      if (oneYardQuantity * 2 == halfYardQuantity)
      {
         return true;
      }
      if (oneYardQuantity * 2 == halfYardQuantity - 1)
      {
         return true;
      }
      return false;
   }

   private static int getQuantity(InventoryProduct product)
   {
      InventoryOffering[] inventoryOfferings = product.getInventoryOfferings();
      return inventoryOfferings[0].getQuantity();
   }


}

class InventoryCost
{
   private String title;
   private int yardQuantity;
   private int halfYardQuantity;
   private double costPerYard;
   private double costPerHalfYard;
   private double costToShipYard;
   private double costToShipHalfYard;

   public InventoryCost(String line)
   {
      String[] split = line.split("\\,");
      title = split[0].trim();
      yardQuantity = Integer.parseInt(split[1]);
      halfYardQuantity = Integer.parseInt(split[2]);
      costPerYard = Double.parseDouble(split[3]);
      costPerHalfYard = Double.parseDouble(split[4]);
      costToShipYard = Double.parseDouble(split[5]);
      costToShipHalfYard = Double.parseDouble(split[6]);
   }

   public String getTitle()
   {
      return title;
   }

   public void setTitle(String title)
   {
      this.title = title;
   }

   public int getYardQuantity()
   {
      return yardQuantity;
   }

   public void setYardQuantity(int yardQuantity)
   {
      this.yardQuantity = yardQuantity;
   }

   public int getHalfYardQuantity()
   {
      return halfYardQuantity;
   }

   public void setHalfYardQuantity(int halfYardQuantity)
   {
      this.halfYardQuantity = halfYardQuantity;
   }

   public double getCostPerYard()
   {
      return costPerYard;
   }

   public void setCostPerYard(double costPerYard)
   {
      this.costPerYard = costPerYard;
   }

   public double getCostPerHalfYard()
   {
      return costPerHalfYard;
   }

   public void setCostPerHalfYard(double costPerHalfYard)
   {
      this.costPerHalfYard = costPerHalfYard;
   }

   public double getCostToShipYard()
   {
      return costToShipYard;
   }

   public void setCostToShipYard(double costToShipYard)
   {
      this.costToShipYard = costToShipYard;
   }

   public double getCostToShipHalfYard()
   {
      return costToShipHalfYard;
   }

   public void setCostToShipHalfYard(double costToShipHalfYard)
   {
      this.costToShipHalfYard = costToShipHalfYard;
   }
}

class AdjustedInventoryDetail
{
   private String title;
   private int quantity;
   private double cost;
   private double shippingCost;

   public AdjustedInventoryDetail(String line)
   {
      String[] split = line.split("\\,");
      title = split[0].trim();
      quantity = Integer.parseInt(split[1]);
      cost = Double.parseDouble(split[2]);
      shippingCost = Double.parseDouble(split[3]);
   }

   public String getTitle()
   {
      return title;
   }

   public void setTitle(String title)
   {
      this.title = title;
   }

   public int getQuantity()
   {
      return quantity;
   }

   public void setQuantity(int quantity)
   {
      this.quantity = quantity;
   }

   public double getCost()
   {
      return cost;
   }

   public void setCost(double cost)
   {
      this.cost = cost;
   }

   public double getShippingCost()
   {
      return shippingCost;
   }

   public void setShippingCost(double shippingCost)
   {
      this.shippingCost = shippingCost;
   }
}

class SoldItems
{
   private String title;
   private int quantity;
   private String variation;

   public SoldItems(String line)
   {
      String[] split = line.split("\\,");
      title = split[0].trim();
      quantity = Integer.parseInt(split[1]);
      variation = split[2].trim().contains("1/2") ? "1/2" : "1";
   }

   public String getTitle()
   {
      return title;
   }

   public void setTitle(String title)
   {
      this.title = title;
   }

   public int getQuantity()
   {
      return quantity;
   }

   public void setQuantity(int quantity)
   {
      this.quantity = quantity;
   }

   public String getVariation()
   {
      return variation;
   }

   public void setVariation(String variation)
   {
      this.variation = variation;
   }
}
