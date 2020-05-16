package com.islandsoftware.storefronhq.orderprocessing;

import com.islandsoftware.storefronhq.Constants;
import com.islandsoftware.storefronhq.etsy.EtsyClient;
import com.islandsoftware.storefronhq.etsy.SpindleAndRoseEtsyClient;
import com.islandsoftware.storefronhq.etsy.model.ListingsResult;
import com.islandsoftware.storefronhq.etsy.model.inventory.*;
import com.islandsoftware.storefronhq.shopify.sync.model.Product;
import com.islandsoftware.storefronhq.shopify.sync.model.Variant;
import com.islandsoftware.storefronhq.GoogleSheets;
import com.islandsoftware.storefronhq.shopify.sync.ShopifyClient;
import com.islandsoftware.storefronhq.tools.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

public class ProductInfoBuilder
{
   private static final String PRODUCT_INFO_FILE = "C:\\tmp\\SpindleAndRose-MasterProductList - withVariations.csv";


   private static final double SHIPPING_COST_PER_YARD = 0.48;
   private static final double RIBBON_SHIPPING_COST_PER_YARD = 0.13;
   private static final double THREAD_COST = 3.0;

   private static final double ALEXANDER_HENRY_COST = 5.65;
   private static final double ANDOVER_COST = 5.6;
   private static final double ART_GALLERY_COST = 5.6;
   private static final double ART_GALLERY_ORGANIC_COST = 6.65;
   private static final double ART_GALLERY_CANVAS_COST = 9.5;
   private static final double BIRCH_COST = 8.25;
   private static final double BLEND_COST = 5.3;
   private static final double ELLA_BLUE_COST = 5.3;
   private static final double LEWIS_AND_IRENE_COST = 5.6;
   private static final double MODA_COST = 5.65;
   private static final double MONALUNA_COST = 8.0;
   private static final double CLOUD9_COST = 6.25;
   private static final double KOKKA_COST = 7.5;
   private static final double KOKKA_DOUBLE_GAUZE_COST = 6.0;
   private static final double EE_SCHENK_COST = 5.6;
   private static final double EE_SCHENK_CANVAS_COST = 7.5;
   private static final double COTTON_AND_STEEL_COST = 5.6;
   private static final double COTTON_AND_STEEL_CANVAS_COST = 9.85;
   private static final double COTTON_AND_STEEL_GOLD_METALLIC_COST = 5.9;
   private static final double SEVEN_ISLANDS_COST = 5.6;
   private static final double SEVEN_ISLANDS_DOUBLE_GAUZE_COST = 6.0;
   private static final double SEVEN_ISLANDS_FEATHERS_COST = 8.2;
   private static final double SATEEN_COST = 9.3;
   private static final double ORBIT_COST = 2.3;
   private static final double OBIKO_COST = 7.5;

   private static final Logger LOGGER = LoggerFactory.getLogger(ProductInfoBuilder.class);

   public static void main(String[] args)
   {
      try
      {
         updateProductInfo();
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }

   }

   public static void updateProductInfo() throws Exception
   {
      Map<String, ProductInfo> productInfoMap = GoogleSheets.readProductInfo(false);
      Map<String, String> oldNameToNewName = oldNameToNewName("c:\\tmp\\updatedTitles.2.26.2019.csv");
      LOGGER.info("{} names to update", oldNameToNewName.size());
      int count = 0;
      for (Map.Entry<String, String> entry : oldNameToNewName.entrySet())
      {
         String oldName = entry.getKey();
         String newName = entry.getValue();
         ProductInfo productInfo = Utils.getFromMasterMap(oldName, productInfoMap);
         if (productInfo != null)
         {
            productInfo.setEtsyTitle(newName);
            count++;
         }
      }
      LOGGER.info("{} names updated", count);
      write(productInfoMap.values(), "C:\\tmp\\productInfo.csv");

   }

   public static Map<String, String> oldNameToNewName(String filename) throws Exception
   {
      Map<String, String> titles = new TreeMap<>();
      List<String> lines = Files.readAllLines(Paths.get(filename));
      int count = 0;
      for (String line : lines)
      {
         if (++count == 1)
         {
            continue;
         }
         String[] split = line.split(",");
         String oldTitle = split[1].trim();
         String newTitle = split[2].trim();
         titles.put(oldTitle, newTitle);
      }
      return  titles;
   }


   private static Map<String, String> getEtsy2ShopifyTitleMap(String filename)
   {
      Map<String, String> etsy2Shopify = new HashMap<>();
      try (BufferedReader br = new BufferedReader(new FileReader(filename)))
      {
         String line = br.readLine();
         int i = 0;
         while (line != null)
         {
            if (i++ == 0)
            {
               // header row
            }
            else
            {
               String[] split = line.split(",");
               etsy2Shopify.put(split[0].trim(), split[1].trim());
            }
            line = br.readLine();
         }
      }
      catch (Exception e)
      {
         LOGGER.error("readProductInfo", e);
      }
      return etsy2Shopify;
   }

   private void removeVariations(Map<String, ProductInfo> products)
   {
      List<String> remove = new ArrayList<>();
      for (Entry<String, ProductInfo> entry : products.entrySet())
      {
         String v = entry.getValue().getVariation();
         if (v != null && !v.isEmpty())
         {
            if (v.startsWith("3/4 Yard") || v.startsWith("1/2 Yard") || v.startsWith("1/4 Yard") || v.startsWith("Fat Quarter"))
            {
               remove.add(entry.getKey());
            }
         }
      }
      for (String s : remove)
      {
         products.remove(s);
      }
   }

   private void update(Map<String, ProductInfo> products)
   {
      for (ProductInfo product : products.values())
      {
         /*
         if (product.getAutoUpdateCost().toLowerCase().equals("false"))
         {
            continue;
         }
         */

         String variation = product.getVariation();
         if (variation.startsWith("1 Yard") || variation.startsWith("1 Panel"))
         {
            product.setShippingCost(SHIPPING_COST_PER_YARD);
         }
         else if (variation.startsWith("3/4 Yard"))
         {
            product.setShippingCost(SHIPPING_COST_PER_YARD * 0.75);
         }
         else if (variation.startsWith("1/2 Yard"))
         {
            product.setShippingCost(SHIPPING_COST_PER_YARD / 2.0);
         }
         else if (variation.startsWith("1/4 Yard") || variation.startsWith("Fat Quarter"))
         {
            product.setShippingCost(SHIPPING_COST_PER_YARD / 4.0);
         }
         else
         {
            product.setShippingCost(SHIPPING_COST_PER_YARD);
         }

         String vendor = product.getVendor();
         if (vendor.equals(Constants.VENDOR_SPINDLE_AND_ROSE))
         {
            product.setShippingCost(0.0);
            product.setCost(0.0);
         }
         else if (vendor.equals(Constants.VENDOR_SUZY))
         {
            product.setShippingCost(0.0);
            product.setCost(0.0);
         }
         else if (vendor.equals(Constants.VENDOR_MONALUNA))
         {
            product.setCost(setCost(variation, MONALUNA_COST));
         }
         else if (vendor.equals(Constants.VENDOR_MODA))
         {
            product.setCost(setCost(variation, MODA_COST));
         }
         else if (vendor.equals(Constants.VENDOR_LEWIS_AND_IRENE))
         {
            product.setCost(setCost(variation, LEWIS_AND_IRENE_COST));
         }
         else if (vendor.equals(Constants.VENDOR_ELLA_BLUE))
         {
            product.setCost(setCost(variation, ELLA_BLUE_COST));
         }
         else if (vendor.equals(Constants.VENDOR_FIBERACTIVE))
         {
            product.setCost(THREAD_COST);
         }
         else if (vendor.equals(Constants.VENDOR_BLEND))
         {
            product.setCost(setCost(variation, BLEND_COST));
         }
         else if (vendor.equals(Constants.VENDOR_BIRCH))
         {
            product.setCost(setCost(variation, BIRCH_COST));
         }
         else if (vendor.equals(Constants.VENDOR_ART_GALLERY))
         {
            if (product.getEtsyTitle().toLowerCase().contains("organic"))
            {
               product.setCost(setCost(variation, ART_GALLERY_ORGANIC_COST));
            }
            else if (product.getEtsyTitle().toLowerCase().contains("canvas"))
            {
               product.setCost(setCost(variation, ART_GALLERY_CANVAS_COST));
            }
            else
            {
               product.setCost(setCost(variation, ART_GALLERY_COST));
            }
         }
         else if (vendor.equals(Constants.VENDOR_ANDOVER))
         {
            product.setCost(setCost(variation, ANDOVER_COST));
         }
         else if (vendor.equals(Constants.VENDOR_ALEXANDER_HENRY))
         {
            product.setCost(setCost(variation, ALEXANDER_HENRY_COST));
         }
         else if (vendor.equals(Constants.VENDOR_CLOUD9))
         {
            product.setCost(setCost(variation, CLOUD9_COST));
         }
         else if (vendor.equals(Constants.VENDOR_EE_SCHENCK))
         {
            if (product.getEtsyTitle().toLowerCase().contains("canvas"))
            {
               product.setCost(setCost(variation, EE_SCHENK_CANVAS_COST));
            }
            else
            {
               product.setCost(setCost(variation, EE_SCHENK_COST));
            }
         }
         else if (vendor.equals(Constants.VENDOR_KOKKA))
         {
            if (product.getEtsyTitle().toLowerCase().contains("double gauze"))
            {
               product.setCost(setCost(variation, KOKKA_DOUBLE_GAUZE_COST));
            }
            else
            {
               product.setCost(setCost(variation, KOKKA_COST));
            }
         }
         else if (vendor.equals(Constants.VENDOR_SEVEN_ISLANDS))
         {
            if (product.getEtsyTitle().toLowerCase().contains("double gauze"))
            {
               product.setCost(setCost(variation, SEVEN_ISLANDS_DOUBLE_GAUZE_COST));
            }
            else if (product.getEtsyTitle().toLowerCase().contains("feathers"))
            {
               product.setCost(setCost(variation, SEVEN_ISLANDS_FEATHERS_COST));
            }
            else
            {
               product.setCost(setCost(variation, SEVEN_ISLANDS_COST));
            }
         }
         else if (vendor.equals(Constants.VENDOR_COTTON_AND_STEEL))
         {
            if (product.getEtsyTitle().toLowerCase().contains("canvas"))
            {
               product.setCost(setCost(variation, COTTON_AND_STEEL_CANVAS_COST));
            }
            else if (product.getEtsyTitle().toLowerCase().contains("gold metallic"))
            {
               product.setCost(setCost(variation, COTTON_AND_STEEL_GOLD_METALLIC_COST));
            }
            else
            {
               product.setCost(setCost(variation, COTTON_AND_STEEL_COST));
            }
         }
         else if (vendor.equals(Constants.VENDOR_RENAISSANCE))
         {
            product.setShippingCost(RIBBON_SHIPPING_COST_PER_YARD);
            if (product.getEtsyTitle().toLowerCase().contains("chrysanthemums"))
            {
               product.setCost(setCost(variation, 2.41));
            }
            else if (product.getEtsyTitle().toLowerCase().contains("spirit weave"))
            {
               product.setCost(setCost(variation, 2.35));
            }
            else if (product.getEtsyTitle().toLowerCase().contains("kaffe"))
            {
               product.setCost(setCost(variation, 3.33));
            }
            else if (product.getEtsyTitle().toLowerCase().contains("circle"))
            {
               product.setCost(setCost(variation, 1.75));
            }
            else
            {
               product.setCost(setCost(variation, 2.0));
            }
         }
         else if (vendor.equals(Constants.VENDOR_ALI_EXPRESS))
         {
            product.setCost(setCost(variation, 0.64));
         }
         if (product.getEtsyTitle().toLowerCase().contains("sateen"))
         {
            product.setCost(setCost(variation, SATEEN_COST));
         }
         if (product.getEtsyTitle().toLowerCase().contains("orbit"))
         {
            product.setCost(setCost(variation, ORBIT_COST));
         }
         if (product.getEtsyTitle().toLowerCase().contains("obiko"))
         {
            product.setCost(setCost(variation, OBIKO_COST));
         }
         if (product.getEtsyTitle().toLowerCase().contains("tailored cloth") || product.getEtsyTitle().toLowerCase().contains("sarah golden"))
         {
            product.setCost(setCost(variation, 6.95));
         }
      }
   }

   private double setCost(String variation, double yardCost)
   {
      if (variation.startsWith("3/4 Yard"))
      {
         return yardCost * 0.75;
      }
      if (variation.startsWith("1/2 Yard"))
      {
         return yardCost / 2.0;
      }
      if (variation.startsWith("1/4 Yard") || variation.startsWith("Fat Quarter"))
      {
         return yardCost / 4.0;
      }
      return yardCost;
   }

   public Map<String, ProductInfo> readProductInfo()
   {
      return readProductInfo(PRODUCT_INFO_FILE);
   }

   public Map<String, ProductInfo> readProductInfo(String filename)
   {
      LOGGER.info("readProductInfo: filename={}", filename);
      Map<String, ProductInfo> products = new HashMap<>();
      try (BufferedReader br = new BufferedReader(new FileReader(filename)))
      {
         String line = br.readLine();
         int i = 0;
         while (line != null)
         {
            if (i++ == 0)
            {
               // header row
            }
            else
            {
               String[] split = line.split(",");
               ProductInfo pi = new ProductInfo();
               pi.setEtsyTitle(split[0]);
               pi.setVariation(split[1]);
               pi.setCost(Double.valueOf(split[2]));
               //pi.setAutoUpdateCost(split[3]);
               pi.setShippingCost(Double.valueOf(split[4]));
               String override = split[5];
               if (override != null && !override.isEmpty())
               {
                  pi.setPriceOverride(Double.valueOf(override));
               }
               pi.setVendor(split[6]);
               pi.setSku(split[7]);
               String key = Utils.createKey(pi.getEtsyTitle(), pi.getVariation());
               products.put(key, pi);
               Map<String, ProductInfo> variations = Utils.createProductInfoForVariations(pi);
               if (variations != null)
               {
                  products.putAll(variations);
               }
            }
            line = br.readLine();
         }
      }
      catch (Exception e)
      {
         LOGGER.error("readProductInfo", e);
      }
      /*
      StringBuilder sb = new StringBuilder();
      for (String s : products.keySet())
      {
         sb.append(s).append("\n");
      }
      Utils.write("C:\\tmp\\productkeys.txt", sb.toString());
      */
      LOGGER.info("readProductInfo: numberOfProducts={}", products.size());
      return products;
   }

   public List<ProductInfo> buildProductInfoFromShopify() throws Exception
   {
      Map<String, ProductInfo> productInfoMap = readProductInfo();
      //Map<String, ProductInfo> productInfoMap = new HashMap<>();

      ShopifyClient shopifyClient = new ShopifyClient();
      Map<Long, String> id2Title = shopifyClient.getId2Title();
      List<ProductInfo> products = new ArrayList<>();
      products.addAll(productInfoMap.values());
      int count = 0;
      for (Long id : id2Title.keySet())
      {
         String title = id2Title.get(id);
         LOGGER.info("examining {} of {}, title={}", ++count, id2Title.size(), title);
         Product product = shopifyClient.getProduct(id);
         List<Variant> variants = product.getVariants();
         if (variants != null && variants.size() > 0)
         {
            for (Variant variant : variants)
            {
               String variation = variant.getTitle();
               String key = Utils.createKey(title, variation);
               if (productInfoMap.containsKey(key))
               {
                  LOGGER.debug("already in product map, key={}", key);
               }
               else
               {
                  LOGGER.info("adding product={}", key);
                  ProductInfo pi = new ProductInfo();
                  pi.setEtsyTitle(title);
                  pi.setVendor(product.getVendor());
                  pi.setVariation(variation);
                  pi.setCost(0.0);
                  //pi.setAutoUpdateCost("true");
                  if (variation.startsWith("1 Yard") || variation.startsWith("1 Panel") || variation.equals("Default Title"))
                  {
                     pi.setSku(variant.getSku());
                  }
                  if (variation.startsWith("1 Yard") || variation.startsWith("1 Panel"))
                  {
                     pi.setShippingCost(SHIPPING_COST_PER_YARD);
                  }
                  else if (variation.startsWith("3/4 Yard"))
                  {
                     pi.setShippingCost(SHIPPING_COST_PER_YARD * 0.75);
                  }
                  else if (variation.startsWith("1/2 Yard"))
                  {
                     pi.setShippingCost(SHIPPING_COST_PER_YARD / 2.0);
                  }
                  else if (variation.startsWith("1/4 Yard") || variation.startsWith("Fat Quarter"))
                  {
                     pi.setShippingCost(SHIPPING_COST_PER_YARD / 4.0);
                  }
                  else
                  {
                     pi.setShippingCost(0.0);
                  }
                  products.add(pi);
               }
            }
         }
         else
         {
            LOGGER.warn("No variations for product {}", title);
         }
         Utils.sleep(600L);
      }
      return products;
   }

   private List<ProductInfo> buildProductInfoFromEtsyListings() throws Exception
   {
      Map<String, ProductInfo> productInfoMap = readProductInfo();
      List<ProductInfo> products = new ArrayList<>();
      products.addAll(productInfoMap.values());

      EtsyClient etsyClient = new SpindleAndRoseEtsyClient();
      Map<String, ListingsResult> listings = etsyClient.listings();
      int count = 0;
      for (ListingsResult result : listings.values())
      {
         String title = result.getTitle();
         LOGGER.info("examining {} of {}, title={}", ++count, listings.size(), title);

         InventoryResponse inventory = etsyClient.getInventory(result.getListingId());
         InventoryResults inventoryResults = inventory.getInventoryResults();
         InventoryProduct[] inventoryProducts = inventoryResults.getProducts();
         for (InventoryProduct inventoryProduct : inventoryProducts)
         {
            String variation = "Default Title";
            if (inventoryProduct.getPropertyValues().length > 0)
            {
               InventoryPropertyValue propertyValue = inventoryProduct.getPropertyValues()[0];
               variation = propertyValue.getValues()[0];
            }
            String key = Utils.createKey(title, variation);
            if (productInfoMap.containsKey(key))
            {
               LOGGER.debug("already in product map, key={}", key);
            }
            else
            {
               LOGGER.info("adding product={}", key);
               double price;
               if (inventoryProduct.getInventoryOfferings().length > 0)
               {
                  InventoryOffering inventoryOffering = inventoryProduct.getInventoryOfferings()[0];
                  Integer amount = inventoryOffering.getInventoryOfferingPrice().getAmount();
                  price = (double)amount / 100.0;
               }
               else
               {
                  price = Double.valueOf(result.getPrice());
               }
               ProductInfo pi = new ProductInfo();
               pi.setEtsyTitle(title);
               pi.setVendor("not set");
               pi.setVariation(variation);
               pi.setCost(0.0);
               //pi.setAutoUpdateCost("true");
               if (variation.startsWith("1 Yard") || variation.startsWith("1 Panel"))
               {
                  pi.setShippingCost(SHIPPING_COST_PER_YARD);
               }
               else if (variation.startsWith("1/2 Yard"))
               {
                  pi.setShippingCost(SHIPPING_COST_PER_YARD / 2.0);
               }
               else
               {
                  pi.setShippingCost(0.0);
               }
               LOGGER.info("added product={}", pi);
               products.add(pi);
            }
         }
      }
      return products;
   }

   public static void write(Collection<ProductInfo> products, String file)
   {
      SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
      StringBuilder sb = new StringBuilder();
      //sb.append("DateAdded,EtsyTitle,ShopifyTitle,Variation,Cost,ShippingCost,PriceOverride,Vendor,Tags,ImageAltText,MetaDescription\n");
      sb.append("DateAdded,EtsyTitle,Variation,Cost,ShippingCost,PriceOverride,Vendor\n");
      for (ProductInfo product : products)
      {
         sb.append(formatter.format(product.getDateAdded())).append(",");
         String variation = product.getVariation();
         sb.append(product.getEtsyTitle()).append(",");
         //sb.append(product.getShopifyTitle()).append(",");
         sb.append(variation).append(",");
         sb.append(product.getCost()).append(",");
         sb.append(product.getShippingCost()).append(",");
         sb.append(product.getPriceOverride()).append(",");
         sb.append(product.getVendor() == null ? "not set" : product.getVendor());
         //sb.append(product.getTags() == null ? "not set," : product.getTags()).append(",");
         //sb.append(product.getImageAltText() == null ? "NA," : product.getImageAltText()).append(",");
         //sb.append(product.getMetaDescription() == null ? "NA" : product.getMetaDescription());
         sb.append("\n");
      }
      Utils.write(file, sb.toString());
   }
}
