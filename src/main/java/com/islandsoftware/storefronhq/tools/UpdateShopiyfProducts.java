package com.islandsoftware.storefronhq.tools;

import com.islandsoftware.storefronhq.shopify.sync.model.Product;
import com.islandsoftware.storefronhq.orderprocessing.ProductInfo;
import com.islandsoftware.storefronhq.shopify.sync.ShopifyClient;
import com.islandsoftware.storefronhq.shopify.sync.model.Variant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class UpdateShopiyfProducts
{
   private static final Logger LOGGER = LoggerFactory.getLogger(UpdateShopiyfProducts.class);
   private static double ART_GALLERY_WEIGHT = 4.4;
   private static double ALEXANDER_HENRY_WEIGHT = 5.6;
   private static double ANDOVER_WEIGHT = 5.5;
   private static double BLEND_WEIGHT = 5.4;
   private static double COTTON_AND_STEEL_WEIGHT = 5.2;
   private static double BIRCH_WEIGHT = 4.2;
   private static double BIRCH_KNIT_WEIGHT = 8.4;
   private static double BIRCH_CANVAS_WEIGHT = 7.0;

   public static void main(String[] args)
   {
      try
      {
         /*
         List<String> titles = new ArrayList<>();
         titles.add("Cherry Blossom Fabric | Small White Flowers | Dainty Floral | Mint Green | Light Green and White | Pretty Flowers | Floral Fabric | Sage");
         titles.add("Moose Fabric | Deer Fabric | Trees | Enchanted Forest Print | Floral | Psychedelic | Woodland | Unique | Modern | Art Gallery | Animals");
         titles.add("Pink Cherry Blossom Fabric | Small White Flowers | Dainty Floral | White Flowers | Pretty Flowers | Floral Fabric | Green Leaves");
         titles.add("Moose Fabric Blue | Deer Fabric | Trees | Enchanted Forest Print | Floral | Psychedelic | Woodland | Unique | Modern | Art Gallery | Animals");
         */

         /*
         Map<Long, String> id2Title = shopifyClient.getIdAndTitleMapForAllProducts();
         Map<String, ProductInfo> masterProductMap = new ProductInfoBuilder().readProductInfo();
         StringBuilder sb = new StringBuilder();
         for (Long id : id2Title.keySet())
         {
            //String title = id2Title.get(id);
            //if (!titles.contains(title.trim()))
            //{
            //   continue;
            //}

            Product product = shopifyClient.getProduct(id);
            //boolean updated;
            //updated = applyWeightFormulas(product);
            //if (updated)
            //{
            //   shopifyClient.updateProduct(product);
            //}

            sb.append(doPrice(shopifyClient, product, masterProductMap));
            Utils.sleep(1000L);
         }
         Utils.write("C:\\tmp\\shopifyprices.txt", sb.toString());
         */
      }
      catch (Exception e)
      {
         LOGGER.error("", e);
      }
   }

   private static String doPrice(ShopifyClient shopifyClient, Product product, Map<String, ProductInfo> masterProductMap) throws Exception
   {
      StringBuilder sb = new StringBuilder();
      sb.append("****************");
      sb.append("vendor=").append(product.getVendor()).append(" product=").append(product.getTitle());
      sb.append("\n");
      LOGGER.info("doPrice: vendor={} product={}", product.getVendor(), product.getTitle());
      boolean updated = false;
      List<Variant> variants = product.getVariants();
      for (Variant variant : variants)
      {
         String title = product.getTitle();
         String variation = variant.getTitle();
         String key = Utils.createKey(title, variation);
         ProductInfo productInfo = masterProductMap.get(key);
         if (productInfo == null)
         {
            throw new Exception("product not found in master list - " + key);
         }
         double cost = productInfo.getCost();
         double priceOverride = productInfo.getPriceOverride();
         sb.append("cost=").append(cost).append(" priceOverride=").append(priceOverride).append(" variation=").append(variation);
         sb.append("\n");
         LOGGER.info("doPrice: cost={} priceOverride={} variation={}", cost, priceOverride, variation);
         double updatedPrice;
         if (priceOverride > 0.0)
         {
            updatedPrice = priceOverride;
         }
         else
         {
            updatedPrice = cost * 2;
            if (variation.startsWith("Fat Quarter"))
            {
               updatedPrice = updatedPrice * 1.1;
            }
         }

         String currentPrice = variant.getPrice();
         sb.append("currentPrice=").append(currentPrice).append(" updatedPrice=").append(updatedPrice).append(" variation=").append(variation);
         sb.append("\n");
         LOGGER.info("doPrice: currentPrice={} updatedPrice={} variation={}", currentPrice, updatedPrice, variation);
         if (updatedPrice == 0)
         {
            throw new Exception("Setting price to 0 for " + variation + " of " + title);
         }
         if (!currentPrice.equals(Utils.format(updatedPrice)))
         {
            updated = true;
            variant.setPrice(Utils.format(updatedPrice));
            LOGGER.info("set price to {} for vendor={} product [{}] variant [{}]", variant.getPrice(), product.getVendor(), title, variation);
         }
         else
         {
            LOGGER.info("doPrice: do not need to update {} variation of {}", variation, title);
         }
      }
      if (updated)
      {
         shopifyClient.updateProduct(product);
      }
      return sb.toString();
   }

   private static boolean applyWeightFormulas(Product product)
   {
      boolean updated = false;
      if (product.getVendor().contains("Art Gallery"))
      {
         LOGGER.info("updating Art Gallery product");
         updateWeights(product, ART_GALLERY_WEIGHT);
         updated = true;
      }
      else if (product.getVendor().contains("Alexander Henry"))
      {
         LOGGER.info("updating Alexander Henry product");
         updateWeights(product, ALEXANDER_HENRY_WEIGHT);
         updated = true;

      }
      else if (product.getVendor().contains("Andover"))
      {
         LOGGER.info("updating Andover product");
         updateWeights(product, ANDOVER_WEIGHT);
         updated = true;
      }
      else if (product.getVendor().contains("Blend"))
      {
         LOGGER.info("updating Blend product");
         updateWeights(product, BLEND_WEIGHT);
         updated = true;
      }
      else if (product.getVendor().contains("Cotton + Steel"))
      {
         LOGGER.info("updating Cotton + Steel product");
         updateWeights(product, COTTON_AND_STEEL_WEIGHT);
         updated = true;
      }
      else if (product.getVendor().contains("Birch"))
      {
         if (product.getTitle().toLowerCase().contains("knit"))
         {
            LOGGER.info("updating Birch Knit product");
            updateWeights(product, BIRCH_KNIT_WEIGHT);
         }
         else if (product.getTitle().toLowerCase().contains("canvas"))
         {
            LOGGER.info("updating Birch Canvas product");
            updateWeights(product, BIRCH_CANVAS_WEIGHT);
         }
         else
         {
            LOGGER.info("updating Birch product");
            updateWeights(product, BIRCH_WEIGHT);
         }
         updated = true;
      }
      return updated;
   }

   private static void updateWeights(Product shopifyProduct, double weight)
   {
      List<Variant> variants = shopifyProduct.getVariants();
      for (Variant variant : variants)
      {
         String title = variant.getTitle();
         double variantWeight = Utils.calculateWeight(title, weight, shopifyProduct.getTitle());
         variant.setWeight(String.valueOf(variantWeight));
         LOGGER.info("set weight to {} for product [{}] variant [{}]", variantWeight, shopifyProduct.getTitle(), title);
      }
   }
}
