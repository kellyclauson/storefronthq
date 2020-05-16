package com.islandsoftware.storefronhq.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class KeywordAnalytics
{
   private static final Logger LOGGER = LoggerFactory.getLogger(KeywordAnalytics.class);

   public static void main(String[] args)
   {
      try
      {
         List<Seo> seoList = SeoCreator.readSeo("C:\\tmp\\seo.csv");
         Map<String, Keyword> keywordCount = keywordCount(seoList);
         writeCountMap(keywordCount, "C:\\tmp\\keywordcount.csv");
         Set<String> tracked = Utils.lowerCaseSetFromFile("C:\\tmp\\keywordsinmoz.csv");
         for (String keyword : keywordCount.keySet())
         {
            if (!tracked.contains(keyword.toLowerCase()))
            {
               LOGGER.info("{} is NOT a tracked keyword but is used {} times", keyword, keywordCount.get(keyword).getTotalCount());
            }
         }
         LOGGER.info("\n\n");
         Set<String> lowerCaseKeywords = new TreeSet<>();
         for (String keyword : keywordCount.keySet())
         {
            lowerCaseKeywords.add(keyword.toLowerCase());
         }
         for (String keyword : tracked)
         {
            if (!lowerCaseKeywords.contains(keyword))
            {
               LOGGER.info("{} is NOT being used", keyword);
            }

         }
      }
      catch (Exception e)
      {
         LOGGER.error("Error", e);
      }
   }

   private static Map<String, Keyword> keywordCount(List<Seo> seoList)
   {
      Map<String, Keyword> count = new TreeMap<>();
      for (Seo seo : seoList)
      {
         String titleTag = seo.getTitleTag().getTitleTag();
         String[] split = titleTag.split("\\|");
         for (String s : split)
         {
            String keyword = s.trim();
            if (!keyword.equals(seo.getShopifyTitle().trim()))
            {
               Keyword k = count.get(keyword);
               if (k == null)
               {
                  k = new Keyword();
                  k.setQuery(keyword);
                  k.setTitleTagCount(1);
                  count.put(keyword, k);
               }
               else
               {
                  k.setTitleTagCount(k.getTitleTagCount() + 1);
               }
            }
         }
         String h1 = seo.getH1();
         if (h1.contains("|"))
         {
            split = h1.split("\\|");
            for (String s : split)
            {
               String keyword = s.trim();
               Keyword k = count.get(keyword);
               if (k == null)
               {
                  k = new Keyword();
                  k.setQuery(keyword);
                  k.setH1Count(1);
                  count.put(keyword, k);
               }
               else
               {
                  k.setH1Count(k.getH1Count() + 1);
               }
            }
         }
         else
         {
            String keyword = h1.trim();
            Keyword k = count.get(keyword);
            if (k == null)
            {
               k = new Keyword();
               k.setQuery(keyword);
               k.setH1Count(1);
               count.put(keyword, k);
            }
            else
            {
               k.setH1Count(k.getH1Count() + 1);
            }
         }
         String h2 = seo.getH2();
         if (h2.contains("|"))
         {
            split = h2.split("\\|");
            for (String s : split)
            {
               String keyword = s.trim();
               Keyword k = count.get(keyword);
               if (k == null)
               {
                  k = new Keyword();
                  k.setQuery(keyword);
                  k.setH2Count(1);
                  count.put(keyword, k);
               }
               else
               {
                  k.setH2Count(k.getH2Count() + 1);
               }
            }
         }
         else
         {
            String keyword = h2.trim();
            Keyword k = count.get(keyword);
            if (k == null)
            {
               k = new Keyword();
               k.setQuery(keyword);
               k.setH2Count(1);
               count.put(keyword, k);
            }
            else
            {
               k.setH2Count(k.getH2Count() + 1);
            }
         }
      }
      return count;
   }

   private static void writeCountMap(Map<String, Keyword> countMap, String filename)
   {
      StringBuilder sb = new StringBuilder();
      sb.append("Keyword,TitleTagCount, H1Count, H2Count, TotalCount\n");
      for (Keyword keyword : countMap.values())
      {
         sb.append(keyword.getQuery()).append(",");
         sb.append(keyword.getTitleTagCount()).append(",");
         sb.append(keyword.getH1Count()).append(",");
         sb.append(keyword.getH2Count()).append(",");
         sb.append(keyword.getTotalCount()).append("\n");
      }
      Utils.write(filename, sb.toString());
   }
}
