package com.islandsoftware.storefronhq.tools;

import com.islandsoftware.storefronhq.etsy.EtsyClient;
import com.islandsoftware.storefronhq.etsy.SpindleAndRoseEtsyClient;
import com.islandsoftware.storefronhq.etsy.model.ListingsResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class UpdateEtsyDescriptions
{
   private static final Logger LOGGER = LoggerFactory.getLogger(UpdateEtsyDescriptions.class);

   private static final String PATTERN = "##.*$";
   private static final String REPLACEMENT = "";

   public static void main(String[] args)
   {
      try
      {
         EtsyClient etsyClient = new SpindleAndRoseEtsyClient();
         Map<String, ListingsResult> listings = etsyClient.listings("description");
         int count = 0;
         for (ListingsResult result : listings.values())
         {
            LOGGER.info("updating {} of {}", ++count, listings.size());
            String description = result.getDescription();
            String newDescription = result.getTitle() + "\n\n" + description;
            LOGGER.debug("before:\n{}", description);
            LOGGER.debug("after:\n{}", newDescription);
            Map<String, String> params = new HashMap<>(1);
            params.put("description", newDescription);
            etsyClient.updateListing(result.getListingId(), params);
            Utils.sleep(1000L);
         }
         LOGGER.info("count={}", count);
      }
      catch (Exception e)
      {
         LOGGER.error("error", e);
      }
   }

   private static String removeShippingPolicy(String description)
   {
      String[] lines = description.split("\n");
      StringBuilder sb = new StringBuilder();
      boolean keep = true;
      for (int i = 0; i < lines.length; i++) {
         String line = lines[i];
         if (line.toLowerCase().contains("shipping policies"))
         {
            keep = false;
         }
         else if (line.toLowerCase().contains("thank you so much"))
         {
            keep = true;
         }
         if (keep)
         {
            sb.append(line).append("\n");
         }
      }
      return sb.toString();
   }


   private static String removeShopifyLines(String description)
   {
      String[] lines = description.split("\n");
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < lines.length; i++) {
         String line = lines[i];
         if (line.equals("Spindle+Rose"))
         {
            sb.append(line);
         }
         else if (!line.contains("FREE SHIPPING"))
         {
            sb.append(line);
            sb.append("\n");
         }
         else
         {
            i++;
         }
      }
      return sb.toString();
   }

   private static String replace(String text, String pattern, String replacement)
   {
      int i = text.indexOf(pattern);
      if (i > 0)
      {
         StringBuilder sb = new StringBuilder();
         sb.append(text.substring(0, i - 1));
         sb.append(replacement);
         return sb.toString();
      }
      LOGGER.warn("pattern {} not found", pattern);
      return null;
   }

   private static String replaceAll(String text, String pattern, String replacement)
   {
      return text.replaceAll(pattern, replacement);
   }

   private static void updateDescription(EtsyClient etsyClient, String description, String title, Long id) throws Exception
   {
      Map<String, String> params = new HashMap<>(1);
      params.put("description", description);
      LOGGER.info("updating {}", title);
      etsyClient.updateListing(id, params);
   }

   private static String formatDescription(String pattern, String handle)
   {
      return pattern.replace("HANDLE", handle);
   }
}
