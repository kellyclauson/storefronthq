package com.islandsoftware.storefronhq.tools;

import com.islandsoftware.storefronhq.shopify.sync.model.Product;
import com.islandsoftware.storefronhq.etsy.model.ListingsResult;
import com.islandsoftware.storefronhq.shopify.sync.model.Variant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SyncShopifyWeightsWithEtsy
{
   private static final Logger LOGGER = LoggerFactory.getLogger(SyncShopifyWeightsWithEtsy.class);

   public static void main(String[] args)
   {
      try
      {
         /*
         ShopifyClient shopifyClient = new ShopifyClient();
         EtsyClient spindleEtsyClient = new SpindleAndRoseEtsyClient();
         StoreSync storeSync = new StoreSync();
         storeSync.initialize(shopifyClient, spindleEtsyClient);

         Map<String, ListingsResult> listings = spindleEtsyClient.listings();
         LOGGER.info("found {} etsy listings", listings.size());

         for (Map.Entry<String, ListingsResult> entry : listings.entrySet())
         {
            Long etsyListingId = entry.getValue().getListingId();
            Long shopifyProductId = storeSync.getShopifyIdForShopifyTitle(entry.getValue().getTitle().trim());
            if (shopifyProductId == null)
            {
               LOGGER.warn("sync: estyListingId={} etsyTitle={} is a listing in Etsy but was not found in shopify", etsyListingId, entry.getKey());
            }
            else
            {
               Product product = shopifyClient.getProduct(shopifyProductId);
               try
               {
                  updateWeights(product, entry.getValue());
                  shopifyClient.updateProduct(product);
               }
               catch (Exception e)
               {
                  LOGGER.error("{}", e.getMessage());
               }
            }
            Utils.sleep(600L);
         }
         */
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   private static void updateWeights(Product shopifyProduct, ListingsResult etsyListing)
   {
      String itemWeight = etsyListing.getItemWeight();
      if (itemWeight != null)
      {
         double weight = Utils.adjustWeight(Double.parseDouble(itemWeight));
         shopifyProduct.setWeight(Double.parseDouble(itemWeight));
         LOGGER.info("found product weight of {} for product [{}]", itemWeight, shopifyProduct.getTitle());

         List<Variant> variants = shopifyProduct.getVariants();
         if (variants != null)
         {
            for (Variant variant : variants)
            {
               String title = variant.getTitle();
               double variantWeight = Utils.calculateWeight(title, weight, shopifyProduct.getTitle());
               variant.setWeight(String.valueOf(variantWeight));
               LOGGER.info("set weight to {} for product [{}] variant [{}]", variantWeight, shopifyProduct.getTitle(), title);
            }
         }
         else
         {
            LOGGER.warn("no variants, setting product weight to {} for product [{}]", weight, shopifyProduct.getTitle());
         }
      }
      else
      {
         LOGGER.error("no weight found for etsy listing [{}]", etsyListing.getTitle());
      }
   }
}
