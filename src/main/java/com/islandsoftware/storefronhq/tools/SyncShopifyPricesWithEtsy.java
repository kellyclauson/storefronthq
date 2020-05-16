package com.islandsoftware.storefronhq.tools;

import com.islandsoftware.storefronhq.etsy.EtsyClient;
import com.islandsoftware.storefronhq.etsy.ImaginationFabricClient;
import com.islandsoftware.storefronhq.etsy.SpindleAndRoseEtsyClient;
import com.islandsoftware.storefronhq.etsy.model.ListingsResult;
import com.islandsoftware.storefronhq.etsy.model.inventory.InventoryProduct;
import com.islandsoftware.storefronhq.etsy.model.inventory.InventoryResponse;
import com.islandsoftware.storefronhq.etsy.model.inventory.InventoryResults;
import com.islandsoftware.storefronhq.orderprocessing.ProductInfo;
import com.islandsoftware.storefronhq.shopify.sync.model.Product;
import com.islandsoftware.storefronhq.GoogleSheets;
import com.islandsoftware.storefronhq.shopify.sync.ShopifyClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class SyncShopifyPricesWithEtsy
{
   private static final Logger LOGGER = LoggerFactory.getLogger(SyncShopifyPricesWithEtsy.class);

   public static void main(String[] args)
   {
      try
      {
         Map<String, ProductInfo> productInfoMap = GoogleSheets.readProductInfo(false);
         ShopifyClient shopifyClient = new ShopifyClient();
         EtsyClient spindleEtsyClient = new SpindleAndRoseEtsyClient();
         EtsyClient imagineEtsyClient = new ImaginationFabricClient();
         syncPrices(spindleEtsyClient, productInfoMap, shopifyClient);
         syncPrices(imagineEtsyClient, productInfoMap, shopifyClient);
      }
      catch (Exception e)
      {
         LOGGER.error("main", e);
      }
   }

   private static void syncPrices(EtsyClient etsyClient, Map<String, ProductInfo> productInfoMap, ShopifyClient shopifyClient)
   {
      try
      {

         Map<String, ListingsResult> listings = etsyClient.listings();
         LOGGER.info("found {} etsy listings", listings.size());

         int count = 0;
         for (Map.Entry<String, ListingsResult> entry : listings.entrySet())
         {
            LOGGER.info("updating {} of {}", ++count, listings.size());
            Long etsyListingId = entry.getValue().getListingId();
            String etsyTitle = entry.getKey().trim();
            ProductInfo productInfo = Utils.getFromMasterMap(etsyTitle, productInfoMap);
            if (productInfo != null)
            {
               String shopifyTitle = productInfo.getShopifyTitle();
               Long shopifyProductId = shopifyClient.getTitle2Id().get(shopifyTitle);
               if (shopifyProductId == null)
               {
                  LOGGER.warn("sync: estyListingId={} etsyTitle={} is a listing in Etsy but was not found in shopify", etsyListingId, entry.getKey());
               }
               else
               {
                  Product product = shopifyClient.getProduct(shopifyProductId);
                  try
                  {
                     setPriceOnShopifyProduct(etsyClient, entry.getValue(), product);
                     shopifyClient.updateProduct(product);
                     Utils.sleep(600L);
                  }
                  catch (Exception e)
                  {
                     LOGGER.error("{}", e.getMessage());
                  }
               }
            }
            else
            {
               LOGGER.info("Not Found In Master Map, EtsyTitle={}", etsyTitle);
            }
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   private static void setPriceOnShopifyProduct(EtsyClient etsyClient, ListingsResult etsyListing, Product shopifyProduct) throws Exception
   {
      double price = 0.0;
      InventoryResponse inventory = etsyClient.getInventory(etsyListing.getListingId());
      if (inventory == null)
      {
         price = getPriceFromEtsyListing(etsyListing);
      }
      else
      {
         InventoryResults inventoryResults = inventory.getInventoryResults();
         if (inventoryResults == null)
         {
            price = getPriceFromEtsyListing(etsyListing);
         }
         else
         {
            InventoryProduct[] products = inventoryResults.getProducts();
            if (products == null || products.length == 0)
            {
               price = getPriceFromEtsyListing(etsyListing);
            }
            else
            {
               price = Utils.getBasePrice(products);
            }
         }
      }
      if (price == 0.0)
      {
         LOGGER.error("updatePrices: No price found for etsy listing {}", etsyListing.getTitle());
      }
      else
      {
         Utils.setPriceOnProductVariations(shopifyProduct, price);
      }
   }

   private static Double getPriceFromEtsyListing(ListingsResult listing)
   {
      double price = 0.0;
      String sPrice = listing.getPrice();
      if (sPrice != null)
      {
         price = Double.valueOf(sPrice);
      }
      return price;
   }
}
