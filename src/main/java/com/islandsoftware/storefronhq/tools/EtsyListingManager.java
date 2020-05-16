package com.islandsoftware.storefronhq.tools;

import com.islandsoftware.storefronhq.etsy.model.ListingsResponse;
import com.islandsoftware.storefronhq.etsy.model.inventory.*;
import com.islandsoftware.storefronhq.etsy.EtsyClient;
import com.islandsoftware.storefronhq.etsy.ImaginationFabricClient;
import com.islandsoftware.storefronhq.etsy.SpindleAndRoseEtsyClient;
import com.islandsoftware.storefronhq.etsy.model.ListingsResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class EtsyListingManager
{
   private static final Logger LOGGER = LoggerFactory.getLogger(EtsyListingManager.class);

   // Note the app does not have permission to delete listings from the spindle+rose etsy store, but it does for the imaginationfabric store

   public static void main(String[] args)
   {
      try
      {
         List<String> noProducts = new ArrayList<>();
         List<String> noPropertyValues = new ArrayList<>();
         List<String> noInventoryOfferings = new ArrayList<>();
         EtsyClient client = new ImaginationFabricClient();
         Map<Long, String> id2Title = client.getId2Title();
         for (Map.Entry<Long, String> entry : id2Title.entrySet())
         {
            Long id = entry.getKey();
            String title = entry.getValue();
            InventoryResponse inventory = client.getInventory(id);
            Utils.sleep(500L);
            InventoryResults inventoryResults = inventory.getInventoryResults();
            InventoryProduct[] products = inventoryResults.getProducts();
            LOGGER.info("title={}", title);
            if (products == null || products.length == 0)
            {
               LOGGER.error("No Products for {}!!!", title);
               noProducts.add(title);
               continue;
            }
            for (InventoryProduct product : products)
            {
               InventoryPropertyValue[] propertyValues = product.getPropertyValues();
               if (propertyValues == null || propertyValues.length == 0)
               {
                  LOGGER.error("No Property Values for {}!!!", title);
                  noPropertyValues.add(title);
                  continue;
               }
               for (InventoryPropertyValue propertyValue : propertyValues)
               {
                  String name = propertyValue.getPropertyName();
                  LOGGER.debug("propertyName={}", name);
               }

               InventoryOffering[] inventoryOfferings = product.getInventoryOfferings();
               if (inventoryOfferings == null || inventoryOfferings.length == 0)
               {
                  LOGGER.error("No Inventory Offerings for {}!!!", title);
                  noInventoryOfferings.add(title);
                  continue;
               }
               for (InventoryOffering inventoryOffering : inventoryOfferings)
               {
                  Integer quantity = inventoryOffering.getQuantity();
                  LOGGER.debug("quantity={}", quantity);
               }
            }
         }

         LOGGER.info("No Products");
         for (String s : noProducts)
         {
            LOGGER.info("{}", s);
         }
         LOGGER.info("\n\n");
         LOGGER.info("No Property Values");
         for (String s : noPropertyValues)
         {
            LOGGER.info("{}", s);
         }
         LOGGER.info("\n\n");
         LOGGER.info("No Inventory Offerings");
         for (String s : noInventoryOfferings)
         {
            LOGGER.info("{}", s);
         }


         //updateInventory();
         //updatePrice();
         /*
         Set<Long> ids = sClient.getId2Title().keySet();
         for (Long id : ids)
         {
            sClient.getListing(id, null);
            break;
         }
         */

         /*
         int count = 0;
         for (Long id : ids)
         {
            LOGGER.info("deactivating {} of {} id={}", ++count, ids.size(), id);
            Map<String, String> params = new HashMap<>(1);
            params.put("state", "inactive");
            sClient.updateListing(id, params);
            Utils.sleep(600L);
         }
         */
      }
      catch (Exception e)
      {
         LOGGER.error("error", e);
      }
   }

   private static void updatePrice() throws Exception
   {
      int count = 0;
      EtsyClient client = new SpindleAndRoseEtsyClient("inactive");
      Map<Long, String> id2Title = client.getId2Title();
      for (Map.Entry<Long, String> entry : id2Title.entrySet())
      {
         Long id = entry.getKey();
         String title = entry.getValue();
         LOGGER.info("{} of {} {}", ++count, id2Title.size(), title);
         InventoryResponse inventory = client.getInventory(id);
         if (inventory == null)
         {
            setPriceOnEtsyListing(client, id, title, "inventoryResponse");
         }
         else
         {
            InventoryResults inventoryResults = inventory.getInventoryResults();
            if (inventoryResults == null)
            {
               setPriceOnEtsyListing(client, id, title, "inventoryResults");
            }
            else
            {
               InventoryProduct[] products = inventoryResults.getProducts();
               if (products == null || products.length == 0)
               {
                  setPriceOnEtsyListing(client, id, title, "inventoryProduct");
               }
               else
               {
                  boolean update = false;
                  for (InventoryProduct product : products)
                  {
                     InventoryPropertyValue[] propertyValues = product.getPropertyValues();
                     if (propertyValues == null || propertyValues.length == 0)
                     {
                        setPriceOnEtsyListing(client, id, title, "inventoryPropertyValue");
                     }
                     else
                     {
                        String value = product.getPropertyValues()[0].getValues()[0];
                        LOGGER.debug("product value={}", value);

                        InventoryOffering[] inventoryOfferings = product.getInventoryOfferings();
                        if (inventoryOfferings == null || inventoryOfferings.length == 0)
                        {
                           setPriceOnEtsyListing(client, id, title, "inventoryOfferings");
                        }
                        else
                        {
                           for (InventoryOffering inventoryOffering : inventoryOfferings)
                           {
                              InventoryOfferingPrice inventoryOfferingPrice = inventoryOffering.getInventoryOfferingPrice();
                              Integer amount = inventoryOfferingPrice.getAmount();
                              LOGGER.debug("product amount={}", amount);
                              if (value != null && value.startsWith("1/2"))
                              {
                                 inventoryOfferingPrice.setAmount(amount - 50);
                              }
                              else
                              {
                                 inventoryOfferingPrice.setAmount(amount - 100);
                              }
                              int updatedAmount = inventoryOfferingPrice.getAmount();
                              String[] formattedPrices = Utils.createFormattedPrices(updatedAmount);
                              inventoryOfferingPrice.setCurrencyFormattedRaw(formattedPrices[0]);
                              inventoryOfferingPrice.setCurrencyFormattedShort(formattedPrices[1]);
                              inventoryOfferingPrice.setCurrencyFormattedLong(formattedPrices[2]);
                              LOGGER.info("updated from {} to {} for {} {}", amount, updatedAmount, value, title);
                              update = true;

                           }
                        }
                     }
                  }
                  if (update)
                  {
                     String updatedProductJson = Utils.toJson(products);
                     LOGGER.debug("updated inventory:\n{}", updatedProductJson);
                     client.updateInventory(id, updatedProductJson);
                     Utils.sleep(500L);
                     Map<String, String> params = new HashMap<>(1);
                     params.put("shipping_template_id", String.valueOf(client.getShippingTemplateId()));
                     client.updateListing(id, params);
                  }
                  Utils.sleep(500L);
               }
            }
         }
      }
   }

   private static void setPriceOnEtsyListing(EtsyClient client, Long id, String title, String reason)
   {
      LOGGER.info("{} is null or empty, setting top level price on {}", reason, title);
      ListingsResponse listing = client.getListing(id, null);
      ListingsResult result = listing.getResults()[0];
      String price = result.getPrice();
      if (price == null)
      {
         LOGGER.info("Could not find top level price for {}", title);
      }
      else
      {
         double dprice = Double.valueOf(price);
         if (dprice <= 1.0)
         {
            LOGGER.info("Current top level price is too low {} for {}", dprice, title);
         }
         else
         {
            dprice = dprice - 1.0;
            LOGGER.info("setting top level price to {} for {}", dprice, title);
            Map<String, String> params = new HashMap<>(2);
            params.put("price", String.valueOf(dprice));
            params.put("shipping_template_id", String.valueOf(client.getShippingTemplateId()));
            client.updateListing(id, params);
         }
      }
   }

   private static void updateInventory() throws Exception
   {
      EtsyClient sClient = new SpindleAndRoseEtsyClient();
      Map<String, ListingsResult> listings = sClient.listings("inactive", "id,title");
      EtsyClient iClient = new ImaginationFabricClient();
      Map<String, Long> imagineListings = iClient.getTitle2Id();
      int count = 0;
      for (Map.Entry<String, ListingsResult> entry : listings.entrySet())
      {
         Long id = entry.getValue().getListingId();
         String title = entry.getValue().getTitle().trim();
         Long imagineId = imagineListings.get(title);
         if (imagineId != null)
         {
            InventoryResponse inventory = sClient.getInventory(id);
            if (inventory != null)
            {
               InventoryResults inventoryResults = inventory.getInventoryResults();
               if (inventoryResults != null)
               {
                  InventoryProduct[] products = inventoryResults.getProducts();
                  if (products != null && products.length > 0)
                  {
                     LOGGER.info("{} of {} {}", ++count, listings.size(), title);
                     iClient.updateInventory(imagineId, Utils.toJson(products));
                     Utils.sleep(600L);
                  }
               }
            }
         }
      }
   }
}
