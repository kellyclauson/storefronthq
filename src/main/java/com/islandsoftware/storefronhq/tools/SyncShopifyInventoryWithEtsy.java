package com.islandsoftware.storefronhq.tools;

import com.islandsoftware.storefronhq.StoreSync;
import com.islandsoftware.storefronhq.etsy.EtsyClient;
import com.islandsoftware.storefronhq.etsy.model.inventory.InventoryResponse;
import com.islandsoftware.storefronhq.shopify.sync.model.Product;
import com.islandsoftware.storefronhq.SMSClient;
import com.islandsoftware.storefronhq.etsy.model.ListingsResult;
import com.islandsoftware.storefronhq.orderprocessing.ProductInfo;
import com.islandsoftware.storefronhq.shopify.sync.ShopifyClient;
import com.islandsoftware.storefronhq.shopify.sync.model.Variant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class SyncShopifyInventoryWithEtsy
{
   private static final Logger LOGGER = LoggerFactory.getLogger(SyncShopifyInventoryWithEtsy.class);

   public static void main(String[] args)
   {
      try
      {
         /*
         ShopifyClient shopifyClient = new ShopifyClient();
         EtsyClient spindleEtsyClient = new SpindleAndRoseEtsyClient();
         StoreSync storeSync = new StoreSync();
         storeSync.initialize(shopifyClient, spindleEtsyClient);
         sync(storeSync);
         */
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   public static void sync(StoreSync storeSync)
   {
      Map<String, ProductInfo> masterProductMapByEtsyTitle = storeSync.getMasterProductMapByEtsyTitle();
      Map<String, Long> shopifyTitle2Id = storeSync.getShopifyTitle2Id();
      sync(storeSync.getShopifyClient(), storeSync.getSpindleEtsyClient(), masterProductMapByEtsyTitle, shopifyTitle2Id);
      //sync(storeSync.getShopifyClient(), storeSync.getImagineEtsyClient(), masterProductMapByEtsyTitle, shopifyTitle2Id);
   }

   private static void sync(ShopifyClient shopifyClient, EtsyClient etsyClient, Map<String, ProductInfo> masterProductMapByEtsyTitle, Map<String, Long> shopifyTitle2Id)
   {
      LOGGER.info("sync: begin {}", etsyClient.getStoreName());
      Map<String, ListingsResult> listings = etsyClient.listings();
      LOGGER.info("sync: found {} {} etsy listings", listings.size(), etsyClient.getStoreName());
      int count = 0;
      for (Map.Entry<String, ListingsResult> entry : listings.entrySet())
      {
         LOGGER.info("sync: listing {} of {} etsyTitle={}", ++count, listings.size(), entry.getKey());
         if (entry.getKey().toLowerCase().contains("thread"))
         {
            continue;
         }
         ProductInfo productInfo = Utils.getFromMasterMap(entry.getKey(), masterProductMapByEtsyTitle);
         if (productInfo == null)
         {
            LOGGER.warn("sync: etsyTitle={} not found in master list", entry.getKey());
            SMSClient.alertAdmin("etsy listing not found in master list: " + entry.getKey());
         }
         else
         {
            int quantity = 0;
            boolean isBaseQuantityHalfYard = true;
            try
            {
               InventoryResponse inventory = etsyClient.getInventory(entry.getValue().getListingId());
               if (!productInfo.getShopifyTitle().equalsIgnoreCase("Not In Shopify"))
               {
                  quantity = Utils.getBaseQuantity(inventory);
                  isBaseQuantityHalfYard = Utils.isBaseQuantityHalfYard(inventory);
               }
            }
            catch (Exception e)
            {
               LOGGER.error("error getting quantity for {}", entry.getValue().getTitle(), e);
               if (!e.getMessage().toLowerCase().contains("temporary"))
               {
                  SMSClient.alertAdmin(e.getMessage() + " " + entry.getValue().getTitle());
               }
            }
            if (!productInfo.getShopifyTitle().equalsIgnoreCase("Not In Shopify"))
            {
               if ((isBaseQuantityHalfYard && quantity > 1) || (!isBaseQuantityHalfYard && quantity > 0))
               {
                  Long shopifyProductId = shopifyTitle2Id.get(productInfo.getShopifyTitle());
                  if (shopifyProductId == null)
                  {
                     LOGGER.warn("sync: {} was not found in shopifyId2Title map for etsy title {}", productInfo.getShopifyTitle(), entry.getKey());
                     SMSClient.alertAdmin("not found in shopifyId2Title map: " + productInfo.getShopifyTitle() + " for etsy title " + entry.getKey());
                  }
                  else
                  {
                     Product product = shopifyClient.getProduct(shopifyProductId);
                     if (product == null)
                     {
                        LOGGER.warn("sync: shopifyId {} shopifyTitle {} not found in shopify", shopifyProductId, productInfo.getShopifyTitle());
                     }
                     else
                     {
                        try
                        {
                           updateProduct(shopifyClient, product, quantity, isBaseQuantityHalfYard);
                        }
                        catch (Exception e)
                        {
                           LOGGER.error("error updating {}", entry.getKey(), e);
                           SMSClient.alertAdmin(e.getMessage() + " " + entry.getValue().getTitle());
                        }
                     }
                  }
               }
               else
               {
                  LOGGER.info("shopifyTitle {} is not set or not active for etsyTitle {}", productInfo.getShopifyTitle(), entry.getKey());
               }
            }
         }
         Utils.sleep(600L);
      }
      //SMSClient.alertAdmin("inventory sync complete " + Utils.millisToDateString(System.currentTimeMillis()));
      LOGGER.info("sync: end {}", etsyClient.getStoreName());
   }


   private static void updateProduct(ShopifyClient shopifyClient, Product product, int quantity, boolean isQuantityHalfYard)
   {
      try
      {
         boolean managementUpdated = setInventoryManagement(product);
         boolean inventoryUpdated = Utils.updateInventory(product, quantity, isQuantityHalfYard);
         if (managementUpdated || inventoryUpdated)
         {
            shopifyClient.updateProduct(product);
         }
         else
         {
            LOGGER.info("updateProduct: not updating product {} with type {}", product.getTitle(), product.getProductType());
         }
      }
      catch (Exception e)
      {
         LOGGER.error("udpateProduct: {}", e.getMessage());
         SMSClient.alertAdmin(e.getMessage());
      }
   }


   private static boolean setInventoryManagement(Product product)
   {
      boolean updated = false;
      List<Variant> variants = product.getVariants();
      for (Variant variant : variants)
      {
         if (!"shopify".equalsIgnoreCase(variant.getInventoryManagement()))
         {
            LOGGER.info("setting inventory management variant [{}] product [{}]", variant.getTitle(), product.getTitle());
            variant.setInventoryManagement("shopify");
            updated = true;
         }
      }
      return updated;
   }
}
