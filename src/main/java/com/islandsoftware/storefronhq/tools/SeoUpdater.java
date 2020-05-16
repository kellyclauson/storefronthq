package com.islandsoftware.storefronhq.tools;

import com.islandsoftware.storefronhq.shopify.sync.ShopifyClient;
import com.islandsoftware.storefronhq.shopify.sync.model.Metafield;
import com.islandsoftware.storefronhq.shopify.sync.model.MetafieldList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class SeoUpdater
{
   private static final Logger LOGGER = LoggerFactory.getLogger(SeoUpdater.class);

   public static void main(String[] args)
   {

      try
      {
         //ShopifyClient client = new ShopifyClient();
         //Map<Long, String> map = client.getIdAndTitleMapForAllProducts();
         /*
         client.removeProductMetafield(9229116035L, 3091046334562L);
         Utils.sleep(1000L);
         client.removeProductMetafield(9229116035L, 3091044597858L);
         */

         //Map<String, ProductInfo> etsyMap = GoogleSheets.readProductInfo(false);
         //Map<String, ProductInfo> shopifyMap = Utils.createShopifyTitle2ProductInfoMapFromEtsyTitle2ProductInfoMap(etsyMap);
         /*
         Set<String> keywords = new TreeSet<>();
         keywords.addAll(keywordsFromFile("C:\\tmp\\seo.csv"));
         keywords.addAll(keywordsFromFile("C:\\tmp\\collections.csv"));
         keywords.addAll(keywordsFromFile2("C:\\tmp\\currentkeywords.csv"));
         LOGGER.info("total number of keywords={}", keywords.size());
         for (String keyword : keywords)
         {
            LOGGER.info("{}", keyword);
         }
         */
         Set<String> keywordsInMoz = keywordsFromFile2("C:\\tmp\\keywordsinmoz.csv");
         Set<String> rankedKeywords = keywordsFromFile2("C:\\tmp\\rankedkeywords.csv");
         Set<String> lowVolumeKeywords = keywordsFromFile2("C:\\tmp\\lowvolumekeywords.csv");

         int count = 0;
         for (String keyword : lowVolumeKeywords)
         {
            if (keywordsInMoz.contains(keyword))
            {
               LOGGER.info("[{}] -> low volume keyword in Moz, count={}", keyword, ++count);
            }
         }

         Set<String> addToMoz = new TreeSet<>();
         count = 0;
         for (String rankedKeyword : rankedKeywords)
         {
            if (!keywordsInMoz.contains(rankedKeyword))
            {
               LOGGER.info("[{}] -> add to Moz, count={}", rankedKeyword, ++count);
               addToMoz.add(rankedKeyword);
            }
         }
         LOGGER.info("\n\n");
         Set<String> removeFromMoz = new TreeSet<>();
         count = 0;
         for (String keyword : keywordsInMoz)
         {
            if (!rankedKeywords.contains(keyword) && !lowVolumeKeywords.contains(keyword))
            {
               LOGGER.info("[{}] -> remove from Moz, count={}", keyword, ++count);
               removeFromMoz.add(keyword);
            }
         }
         Utils.toFile(addToMoz, "C:\\tmp\\addToMoz.txt");
         Utils.toFile(removeFromMoz, "C:\\tmp\\removeFromMoz.txt");

         /*
         int count = 0;
         for (Map.Entry<Long, String> entry : map.entrySet())
         {
            Long id = entry.getKey();
            String shopifyTitle = entry.getValue();
            String keywords = title2Keyword.get(shopifyTitle);
            LOGGER.info("{} of {} :: {} :: {}", ++count, map.size(), shopifyTitle, keywords);
            client.setKeywords(id, "products", keywords);
            Utils.sleep(1000L);

            //ProductInfo productInfo = Utils.getFromMasterMap(shopifyTitle, shopifyMap);
            //if (productInfo == null)
            //{
            //   throw new Exception("[" + shopifyTitle + "]" + " NOT FOUND IN MASTER LIST");
            //}
            //String description = productInfo.getMetaDescription();
            //description = description.replaceAll("-", ",");
            //LOGGER.info("{}\t\t: {}", shopifyTitle, description);
            //if (description.toLowerCase().equals("not required"))
            // {
            //   LOGGER.info("{} Not Required", shopifyTitle);
            //}
            if (shopifyTitle.equals("Gotas De Amor Blue"))
            {
               client.setProductKeywords(id, "alexander henry fabric,mexican fabric,skull fabric");
               //LOGGER.info("productId={}", id);
               //MetafieldList seoData = client.getProductSeoData(id);
            }
            //Utils.sleep(1000L);
         }
         */

         /*
         Map<String, Long> title2IdMap = client.getTitle2IdMap(map);
         Map<String, String> title2titleTag = titleTagsFromFile("C:\\tmp\\seo.csv");
         for (Map.Entry<String, String> entry : title2titleTag.entrySet())
         {
            Long id = title2IdMap.get(entry.getKey());
            if (id == null)
            {
               LOGGER.error("Could not find id for {}", entry.getKey());
            }
            else
            {
               LOGGER.info("{} {}", entry.getKey(), entry.getValue());
               client.setProductTitleTag(id, entry.getValue());
               Utils.sleep(1000L);
            }
         }
         */

         /*
         Map<String, List<Metafield>> metadataForAllProducts = getMetadataForAllProducts();
         titleTagsToFile(metadataForAllProducts, "C:\\tmp\\product-metadata.csv");
         */

         /*
         Map<String, Set<String>> duplicateTitleTags = findDuplicateTitleTags(metadataForAllProducts);
         for (Map.Entry<String, Set<String>> entry : duplicateTitleTags.entrySet())
         {
            LOGGER.info("{} duplicated in {}", entry.getKey(), entry.getValue());
         }
         Map<String, String> title2titleTag = titleTagsFromFile("C:\\tmp\\product-metadata.csv");
         Map<String, List<String>> duplicateTitleTags = findDuplicates(title2titleTag);
         //duplicateTitleTagsToFile(duplicateTitleTags, "C:\\tmp\\duplicate-titletags.csv");
         Map<String, String> updatedTitleTags = SeoCreator.updateTitleTags(duplicateTitleTags);
         toFile(updatedTitleTags, "C:\\tmp\\titletags-to-update.csv");
         */
      }
      catch (Exception e)
      {
         LOGGER.error("Error", e);
      }
   }

   private static Map<String,List<String>> findDuplicates(Map<String, String> title2titleTag)
   {
      Map<String, List<String>> tag2titles = new HashMap<>();
      for (Map.Entry<String, String> entry : title2titleTag.entrySet())
      {
         List<String> titles = tag2titles.get(entry.getValue());
         if (titles == null)
         {
            titles = new ArrayList<>();
            tag2titles.put(entry.getValue(), titles);
         }
         titles.add(entry.getKey());
      }
      Map<String, List<String>> duplicates = new HashMap<>();
      for (Map.Entry<String, List<String>> entry : tag2titles.entrySet())
      {
         if (entry.getValue().size() > 1)
         {
            duplicates.put(entry.getKey(), entry.getValue());
         }
      }
      return duplicates;
   }

   private static Set<String> keywordsFromFile2(String filename) throws Exception
   {
      Set<String> keywords = new TreeSet<>();
      List<String> lines = Files.readAllLines(Paths.get(filename), StandardCharsets.ISO_8859_1);
      for (String line : lines)
      {
         keywords.add(line.trim().toLowerCase());
      }
      return keywords;
   }

   private static Set<String> keywordsFromFile(String filename) throws Exception
   {
      Set<String> keywords = new TreeSet<>();
      List<String> lines = Files.readAllLines(Paths.get(filename), StandardCharsets.ISO_8859_1);
      int count = 0;
      for (String line : lines)
      {
         if (++count == 1)
         {
            continue;
         }
         String[] split = line.split(",");
         String[] split1 = split[1].trim().split("\\|");
         for (String s : split1)
         {
            keywords.add(s.trim());
         }
      }
      return keywords;
   }

   /*
   private static Map<String, String> keywordsFromFile(String filename) throws Exception
   {
      Map<String, String> title2Keywords = new HashMap<>();
      List<String> lines = Files.readAllLines(Paths.get(filename), StandardCharsets.ISO_8859_1);
      int count = 0;
      for (String line : lines)
      {
         if (++count == 1)
         {
            continue;
         }
         String[] split = line.split(",");
         title2Keywords.put(split[0].trim(), split[1].trim().replaceAll("\\|", ","));
      }
      return title2Keywords;
   }
   */

   private static void duplicateTitleTagsToFile(Map<String, List<String>> duplicateTitleTags, String filename)
   {
      StringBuilder sb = new StringBuilder();
      sb.append("TitleTag,Title1,Title2,Title3,Title4,Title5,Title6,Title7,Title8,Title9,Title10\n");
      for (Map.Entry<String, List<String>> entry : duplicateTitleTags.entrySet())
      {
         sb.append(entry.getKey()).append(",");
         for (String title : entry.getValue())
         {
            sb.append(title).append(",");

         }
         sb.append("\n");
      }
      Utils.write(filename, sb.toString());
   }

   private static void titleTagsToFile(Map<String, List<Metafield>> metadataForAllProducts, String filename)
   {
      StringBuilder sb = new StringBuilder();
      sb.append("ShopifyTitle,TitleTag,DescriptionTag\n");
      for (Map.Entry<String, List<Metafield>> entry : metadataForAllProducts.entrySet())
      {
         sb.append(entry.getKey()).append(",");
         String titleTag = null;
         String descriptionTag = null;
         for (Metafield metafield : entry.getValue())
         {
            if ("title_tag".equals(metafield.getKey()))
            {
               titleTag = metafield.getValue().trim();
            }
            if ("description_tag".equals(metafield.getKey()))
            {
               descriptionTag = metafield.getValue().trim().replace(",", "-");
            }
         }
         sb.append(titleTag).append(",");
         sb.append(descriptionTag).append("\n");
      }
      Utils.write(filename, sb.toString());
   }

   private static Map<String, List<Metafield>> getMetadataForAllProducts()
   {
      Map<String, List<Metafield>> metafieldMap = new TreeMap<>();
      ShopifyClient shopifyClient = new ShopifyClient();
      Map<Long, String> products = shopifyClient.getId2Title();
      int count = 0;
      for (Map.Entry<Long, String> entry : products.entrySet())
      {
         String title = entry.getValue();
         LOGGER.info("{} of {} title={}", ++count, products.size(), title);
         MetafieldList productSeoData = shopifyClient.getProductSeoData(entry.getKey());
         metafieldMap.put(title, productSeoData.getMetafields());
         Utils.sleep(1000L);
      }
      return metafieldMap;
   }

   private static Map<String, Set<String>> findDuplicateTitleTags(Map<String, List<Metafield>> metafieldMap)
   {
      Map<String, String> titleTags = new HashMap<>();
      Map<String, Set<String>> duplicateTitleTags = new TreeMap<>();
      for (Map.Entry<String, List<Metafield>> entry : metafieldMap.entrySet())
      {
         for (Metafield metafield : entry.getValue())
         {
            if ("title_tag".equals(metafield.getKey()))
            {
               String shopifyTitle = titleTags.put(metafield.getValue(), entry.getKey());
               if (shopifyTitle != null)
               {
                  Set<String> titles = duplicateTitleTags.get(metafield.getValue());
                  if (titles == null)
                  {
                     titles = new HashSet<>();
                     duplicateTitleTags.put(metafield.getValue(), titles);
                  }
                  titles.add(entry.getKey());
               }
            }
         }
      }
      return duplicateTitleTags;
   }

}
