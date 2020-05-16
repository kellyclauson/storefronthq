package com.islandsoftware.storefronhq.tools;

import com.islandsoftware.storefronhq.etsy.EtsyClient;
import com.islandsoftware.storefronhq.etsy.SpindleAndRoseEtsyClient;
import com.islandsoftware.storefronhq.etsy.model.ListingsResult;
import com.islandsoftware.storefronhq.shopify.sync.ShopifyClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class FindInvalidCharacters
{
   private static final Logger LOGGER = LoggerFactory.getLogger(FindInvalidCharacters.class);

   public static void main(String[] args)
   {
      ShopifyClient shopifyClient = new ShopifyClient();
      EtsyClient etsyClient = new SpindleAndRoseEtsyClient();

      Map<Long, String> shopifyProducts = shopifyClient.getId2Title();
      Map<String, ListingsResult> etsyProducts = etsyClient.listings();

      LOGGER.info("Etsy titles with invalid characters");
      for (String title : etsyProducts.keySet())
      {
         if (containsBadCharacters(title))
         {
            LOGGER.info(title);
         }
      }
      LOGGER.info("");
      LOGGER.info("Shopify titles with invalid characters");
      for (Map.Entry<Long, String> entry : shopifyProducts.entrySet())
      {
         String title = entry.getValue();
         if (containsBadCharacters(title))
         {
            LOGGER.info(title);
            /* uncomment to fix shopify products
            LOGGER.info("before: {}", title);
            String after = title.replaceAll("//", "|");
            after = after.replaceAll("/", "|");
            LOGGER.info("after: {}", after);
            Product product = shopifyClient.getProduct(entry.getKey());
            product.setEtsyTitle(after);
            shopifyClient.updateProduct(product);
            try{Thread.sleep(1000L);}catch(Exception e){}
            */
         }

      }

   }

   private static boolean containsBadCharacters(String title)
   {
      if (title.contains(","))
      {
         return true;
      }
      if (title.contains("/"))
      {
         return true;
      }
      if (title.contains("'"))
      {
         return true;
      }
      return false;
   }
}
