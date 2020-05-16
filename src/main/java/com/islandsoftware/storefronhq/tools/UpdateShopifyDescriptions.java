package com.islandsoftware.storefronhq.tools;

import com.islandsoftware.storefronhq.shopify.sync.ShopifyClient;
import com.islandsoftware.storefronhq.shopify.sync.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class UpdateShopifyDescriptions
{
   private static final Logger LOGGER = LoggerFactory.getLogger(UpdateShopifyDescriptions.class);

   public static void main(String[] args)
   {
      try
      {
         ShopifyClient shopifyClient = new ShopifyClient(30000L);
         Map<Long, String> products = shopifyClient.getId2Title();
         int count = 0;
         for (Long id : products.keySet())
         {
            count++;
            Product product = null;
            try
            {
               product = shopifyClient.getProduct(id, "id,title,body_html");
            }
            catch (Exception e)
            {
               LOGGER.error("Something went wrong getting product, wait awhile and try again", e);
               Utils.sleep(10000L);
               shopifyClient = new ShopifyClient(30000L);
               product = shopifyClient.getProduct(id, "id,title,body_html");
            }
            while (product == null)
            {
               LOGGER.error("Something went wrong getting product, wait awhile and try again");
               Utils.sleep(10000L);
               shopifyClient = new ShopifyClient(30000L);
               product = shopifyClient.getProduct(id, "id,title,body_html");
            }
            LOGGER.info("updating count={} of {} for product={}", count, products.size(), product.getTitle());
            String bodyHtml = product.getBodyHtml();
            LOGGER.debug("before:\n description={}", bodyHtml);
            if (bodyHtml.toLowerCase().contains("h1"))
            {
               String updated = replaceHeaders(bodyHtml);
               LOGGER.debug("after:\n description={}", updated);
               product.setBodyHtml(updated);
               try
               {
                  shopifyClient.updateProduct(product);
               }
               catch (Exception e)
               {
                  LOGGER.error("Something went wrong updating product, wait awhile and try again", e);
                  Utils.sleep(10000L);
                  shopifyClient = new ShopifyClient(30000L);
                  shopifyClient.updateProduct(product);
               }
            }
            else
            {
               LOGGER.info("already updated");
            }
            Utils.sleep(600L);
         }
      }
      catch (Exception e)
      {
         LOGGER.error("error", e);
      }
   }

   public static String replaceHeaders(String text)
   {
      String s = text.replaceAll("h2", "h3");
      s = s.replaceAll("h1", "h2");
      return s;
   }

   public static String replaceHeaders(String text, Seo seo)
   {
      String updated = remove(text, "<h1", "</h1>");
      updated = remove(updated, "<h2", "</h2>");
      updated = remove(updated, "<h3", "</h3>");
      updated = add(updated, seo.getH1(), seo.getH2());
      return updated;
   }

   private static String add(String text, String h1, String h2)
   {
      StringBuilder sb = new StringBuilder();
      sb.append("<h1 style=\"text-align: center;\"><span style=\"color: #000000;\">").append(h1.trim()).append("</span></h1>").append("\n");
      sb.append("<h2 style=\"text-align: center;\"><span style=\"color: #0b5394;\">").append(h2.trim()).append("</span></h2>").append("\n");
      sb.append(text);
      return sb.toString();
   }

   private static String remove(String text, String start, String end)
   {
      int i = text.indexOf(start);
      if (i > -1)
      {
         int j = text.indexOf(end);
         if (j > -1)
         {
            StringBuilder sb = new StringBuilder();
            sb.append(text.substring(0, i));
            sb.append(text.substring(j + end.length(), text.length()));
            return sb.toString();
         }
      }
      return text;
   }
}
