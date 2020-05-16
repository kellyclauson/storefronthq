package com.islandsoftware.storefronhq.tools;

import com.islandsoftware.storefronhq.shopify.sync.ShopifyClient;
import com.islandsoftware.storefronhq.shopify.sync.model.Product;
import com.islandsoftware.storefronhq.shopify.sync.model.Variant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckShopifyVariants
{
   private static final Logger LOGGER = LoggerFactory.getLogger(CheckShopifyVariants.class);

   public static void main(String[] args)
   {
      checkAll();
   }


   private static void checkAll()
   {
      ShopifyClient shopifyClient = new ShopifyClient();
      Map<Long, String> products = shopifyClient.getId2Title();
      for (Long id : products.keySet())
      {
         Utils.sleep(500L);
         Product product = shopifyClient.getProduct(id);
         List<Variant> variants = product.getVariants();
         if (variants == null || variants.isEmpty())
         {
            LOGGER.info("no variants for product {}", product.getTitle());
         }
         else
         {
            boolean updated = false;
            for (Variant variant : variants)
            {
               if (fix(variant))
               {
                  updated = true;
               }
            }
            if (updated)
            {
               LOGGER.info("updating product {}", product);
               shopifyClient.updateProduct(product);
            }
            //priceCheck(product.getEtsyTitle(), variants);
            //weightCheck(product.getEtsyTitle(), product.getProductType(), variants);
            //quantityCheck(product.getEtsyTitle(), variants);
         }
      }
   }

   private static boolean fix(Variant variant)
   {
      boolean updated = false;
      String title = variant.getTitle();
      int begin = title.indexOf("(");
      int end = title.indexOf(")");
      if (begin > -1 && end > begin)
      {
         //LOGGER.info("title before=[{}]", title);
         String newTitle = title.substring(0, begin).trim();
         LOGGER.info("title after=[{}]", newTitle);
         variant.setTitle(newTitle);
         updated = true;
      }

      title = variant.getOption1();
      begin = title.indexOf("(");
      end = title.indexOf(")");
      if (begin > -1 && end > begin)
      {
         //LOGGER.info("option before=[{}]", title);
         String newTitle = title.substring(0, begin).trim();
         LOGGER.info("option after=[{}]", newTitle);
         variant.setOption1(newTitle);
         updated = true;
      }

      return updated;
   }

   private static void quantityCheck(String productTitle, List<Variant> variants)
   {
      for (Variant variant : variants)
      {
         if (!"shopify".equalsIgnoreCase(variant.getInventoryManagement()))
         {
            LOGGER.error("inventory management not set for variant [{}] product [{}]", variant.getTitle(), productTitle);
         }
         Integer quantity = variant.getInventoryQuantity();
         if (quantity == null || quantity == 0)
         {
            LOGGER.error("no quantity for product [{}] variant [{}]", productTitle, variant.getTitle());
         }
      }
   }

   private static void priceCheck(String productTitle, List<Variant> variants)
   {
      double totalPrice = 0.0;
      Map<String, Double> prices = new HashMap<>(variants.size());
      for (Variant variant : variants)
      {
         Double price = Double.parseDouble(variant.getPrice());
         if (price == 0)
         {
            LOGGER.error("no price for product [{}] variant [{}]", productTitle, variant.getTitle());
         }
         else if (price > 20.0)
         {
            LOGGER.info("price is {} for product [{}] variant [{}]", price, productTitle, variant.getTitle());
         }
         else
         {
            prices.put(variant.getTitle(), price);
            totalPrice += price;
         }
      }
      if (!prices.isEmpty() && prices.size() > 1)
      {
         Double next = prices.values().iterator().next();
         if (totalPrice / prices.size() == next)
         {
            LOGGER.error("need to update prices for product {} current prices: {}", productTitle, prices.values());
         }
      }
   }

   private static void weightCheck(String productTitle, String productType, List<Variant> variants)
   {
      if (productType.equalsIgnoreCase("Thread") || productType.equalsIgnoreCase("Gift Certificate"))
      {
         for (Variant variant : variants)
         {
            double v = Double.parseDouble(variant.getWeight());
            if (v != 1.0)
            {
               LOGGER.error("invalid weight of {} for variant [{}] product [{}] with product type {}", v, variant.getTitle(), productTitle, productType);
            }
         }
      }
      else if (productType.equalsIgnoreCase("Panel"))
      {
         for (Variant variant : variants)
         {
            double v = Double.parseDouble(variant.getWeight());
            if (v < 5.0 || v > 6.0)
            {
               LOGGER.error("invalid weight of {} for variant [{}] product [{}] with product type {}", v, variant.getTitle(), productTitle, productType);
            }
         }

      }
      else
      {
         double totalWeight = 0.0;
         Map<String, Double> weights = new HashMap<>(variants.size());
         for (Variant variant : variants)
         {
            Double weight = Double.parseDouble(variant.getWeight());
            if (weight == 0)
            {
               LOGGER.error("no weight for product [{}] variant [{}]", productTitle, variant.getTitle());
            }
            else
            {
               weights.put(variant.getTitle(), weight);
               totalWeight += weight;
            }
         }
         if (!weights.isEmpty() && weights.size() > 1)
         {
            Double next = weights.values().iterator().next();
            if (totalWeight / weights.size() == next)
            {
               LOGGER.error("need to update weights for product {} current weights: {}", productTitle, weights.values());
            }
         }
      }
   }
}
