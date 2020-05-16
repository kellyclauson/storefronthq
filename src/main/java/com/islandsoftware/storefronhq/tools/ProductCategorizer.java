package com.islandsoftware.storefronhq.tools;

import com.islandsoftware.storefronhq.GoogleSheets;
import com.islandsoftware.storefronhq.orderprocessing.ProductInfo;
import com.islandsoftware.storefronhq.shopify.sync.ShopifyClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ProductCategorizer
{
   private static final Logger LOGGER = LoggerFactory.getLogger(ProductCategorizer.class);

   public static void main(String[] args)
   {
      try
      {
         ShopifyClient shopifyClient = new ShopifyClient();
         Map<Long, String> id2Title = shopifyClient.getId2Title();
         Map<String, ProductInfo> masterProductMap = GoogleSheets.readProductInfo();
         //byKeyword(id2Title.values());
         byVendor(id2Title.values(), masterProductMap);

         /*
         String[] wonderland = new String[] {"alice in wonderland", "wonderlandia"};
         Set<String> matches = byKeyword(id2Title.values(), wonderland);
         LOGGER.info("{} matches for {}", matches.size(), wonderland);
         for (String title : matches)
         {
            LOGGER.info("{}", title);
         }
         String[] folk = new String[] {"folklorico", "frida", "skulls"};
         matches = byKeyword(id2Title.values(), folk);
         LOGGER.info("{} matches for {}", matches.size(), folk);
         for (String title : matches)
         {
            LOGGER.info("{}", title);
         }
         String[] xmas = new String[] {"christmas"};
         matches = byKeyword(id2Title.values(), xmas);
         LOGGER.info("{} matches for {}", matches.size(), xmas);
         for (String title : matches)
         {
            LOGGER.info("{}", title);
         }
         */
      }
      catch (Exception e)
      {
         LOGGER.error("", e);
      }
   }

   private static void byVendor(Collection<String> titles, Map<String, ProductInfo> masterProductMap)
   {

      Map<String, Integer> vendors = new HashMap<>();
      for (String title : titles)
      {
         String key = Utils.createKeyDefaultVariation(title);
         ProductInfo productInfo = masterProductMap.get(key);
         if (productInfo != null)
         {
            String vendor = productInfo.getVendor();
            Integer count = vendors.get(vendor);
            if (count == null)
            {
               vendors.put(vendor, 1);
            }
            else
            {
               vendors.put(vendor, count + 1);
            }
         }
      }
      Map<Integer, List<String>> sorted = sort(vendors);
      for (Map.Entry<Integer, List<String>> entry : sorted.entrySet())
      {
         LOGGER.info("count=[{}]\tvendor={}", entry.getKey(), entry.getValue());
      }
   }

   private static Map<Integer, List<String>> sort(Map<String, Integer> unsorted)
   {
      Map<Integer, List<String>> sorted = new TreeMap<>();
      for (Map.Entry<String, Integer> entry : unsorted.entrySet())
      {
         List<String> words = sorted.get(entry.getValue());
         if (words == null)
         {
            words = new ArrayList<>();
            sorted.put(entry.getValue(), words);
         }
         words.add(entry.getKey());
      }
      return sorted;
   }

   private static void byKeyword(Collection<String> titles)
   {
      Map<String, Integer> keywords = new HashMap<>();
      for (String title : titles)
      {
         String[] split = title.split("\\|");
         for (String s : split)
         {
            Integer count = keywords.get(s.trim());
            if (count == null)
            {
               keywords.put(s.trim(), 1);
            }
            else
            {
               keywords.put(s.trim(), count + 1);
            }
         }
      }
      Map<Integer, List<String>> sorted = sort(keywords);
      for (Map.Entry<Integer, List<String>> entry : sorted.entrySet())
      {
         LOGGER.info("count=[{}]\tkeyword={}", entry.getKey(), entry.getValue());
      }
   }

   private static Set<String> byKeyword(Collection<String> titles, String... keywords)
   {
      Set<String> matches = new HashSet<>();
      for (String title : titles)
      {
         for (String keyword : keywords)
         {
            if (title.toLowerCase().contains(keyword.toLowerCase()))
            {
               matches.add(title);
            }
         }
      }
      return matches;
   }
}
