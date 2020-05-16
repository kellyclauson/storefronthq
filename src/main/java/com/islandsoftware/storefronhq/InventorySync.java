package com.islandsoftware.storefronhq;

import com.islandsoftware.storefronhq.shopify.sync.model.Product;
import com.islandsoftware.storefronhq.shopify.sync.model.Variant;
import com.islandsoftware.storefronhq.tools.SyncShopifyInventoryWithEtsy;
import com.islandsoftware.storefronhq.tools.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class InventorySync implements Runnable
{
   StoreSync storeSync;

   private static final Logger LOGGER = LoggerFactory.getLogger(InventorySync.class);

   public InventorySync(StoreSync storeSync)
   {
      this.storeSync = storeSync;
   }

   public void run()
   {
      try
      {
         LOGGER.info("run: begin sync");

         // update maps
         storeSync.update();

         SyncShopifyInventoryWithEtsy.sync(storeSync);

         List<Long> shopifyProductIdsNotInEtsy = storeSync.shopifyProductIdsNotInEtsy();
         LOGGER.info("run: found {} products in shopify that are not in etsy, confirming they have a quantity of 0", shopifyProductIdsNotInEtsy.size());
         for (Long id : shopifyProductIdsNotInEtsy)
         {
            String shopifyTitle = storeSync.getShopifyTitleForShopifyId(id);
            Product product = storeSync.getShopifyClient().getProduct(id);
            if (product != null)
            {
               boolean needsUpdating = false;
               List<Variant> variants = product.getVariants();
               for (Variant variant : variants)
               {
                  Integer quantity = variant.getInventoryQuantity();
                  LOGGER.info("run: quantity of variant [{}] is {} for title [{}]", variant.getTitle(), quantity, shopifyTitle);
                  if (quantity != 0)
                  {
                     String msg = shopifyTitle + " is not available in etsy but has a quantity of " + quantity + " for variation " + variant.getTitle() + " in shopify";
                     LOGGER.warn("run: {}", msg);
                     //SMSClient.alertAdmin(msg);
                     //variant.setInventoryQuantity(0);
                     //needsUpdating = true;
                  }
               }
               if (needsUpdating)
               {
                  storeSync.getShopifyClient().updateProduct(product);
               }
            }
            else
            {
               LOGGER.info("Did not get a Product response from Shopify for title={}, will wait 10 seconds for network issue to resolve before moving on", shopifyTitle);
               Utils.sleep(10000L);
            }
            Utils.sleep(600L);
         }
         LOGGER.info("run: end sync");
      }
      catch (Throwable t)
      {
         LOGGER.error("InventorySync: error during sync", t);
      }
   }
}
