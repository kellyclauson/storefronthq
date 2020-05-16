package com.islandsoftware.storefronhq.tools;

import com.islandsoftware.storefronhq.shopify.sync.model.Product;
import com.islandsoftware.storefronhq.GoogleSheets;
import com.islandsoftware.storefronhq.orderprocessing.ProductInfo;
import com.islandsoftware.storefronhq.shopify.sync.ShopifyClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;

public class NewArrivals
{
   private static final long MONTH_IN_MILLI_SECONDS = 2592000000L;
   private static final Logger LOGGER = LoggerFactory.getLogger(NewArrivals.class);

   public static void main(String[] args)
   {
      try
      {
         remove();
      }
      catch (Exception e)
      {
         LOGGER.error("main", e);
      }
   }

   public static void remove() throws Exception
   {
      remove(MONTH_IN_MILLI_SECONDS * 2L);
   }

   public static void remove(long olderThan) throws Exception
   {
      long expired = System.currentTimeMillis() - olderThan;
      LOGGER.info("remove: olderThan={} expiredTime={}", olderThan, expired);
      ShopifyClient client = new ShopifyClient();
      Map<String, Product> title2ProductMap = client.getTitleToProductMap();
      Map<String, ProductInfo> products = GoogleSheets.readProductInfo(false);
      for (ProductInfo productInfo : products.values())
      {
         String title = productInfo.getShopifyTitle();
         Date dateAdded = productInfo.getDateAdded();
         long dateAddedTime = dateAdded.getTime();
         LOGGER.debug("{} dateAdded={}", title, dateAddedTime);
         if (dateAddedTime < expired)
         {
            Product product = title2ProductMap.get(title);
            if (product != null)
            {
               String tags = product.getTags();
               if (tags != null && tags.contains("New Arrival"))
               {
                  LOGGER.info("removing {} from New Arrival collection", title);
                  String updatedTags;
                  if (tags.contains("New Arrival,"))
                  {
                     updatedTags = StringUtils.remove(tags, "New Arrival,");
                  }
                  else
                  {
                     updatedTags = StringUtils.remove(tags, "New Arrival");
                  }
                  product.setTags(updatedTags);
                  client.updateProduct(product);
                  Utils.sleep(1000L);
               }
            }
         }
      }
   }
}
