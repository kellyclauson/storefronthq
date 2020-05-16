package com.islandsoftware.storefronhq.tools;

import com.islandsoftware.storefronhq.shopify.sync.model.Metafield;
import com.islandsoftware.storefronhq.shopify.sync.ShopifyClient;
import com.islandsoftware.storefronhq.shopify.sync.model.SmartCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateShopifyCollection
{
   private static final Logger LOGGER = LoggerFactory.getLogger(UpdateShopifyCollection.class);

   public static void main(String[] args)
   {
      try
      {
         //String filename = "C:\\tmp\\collections.csv";
         //toFile(getCollectionData(), filename);

         //List<SmartCollection> collections = getCollectionData();
         //Map<String, String> map = fromFile("C:\\tmp\\collections.csv");
         ShopifyClient shopifyClient = new ShopifyClient();
         List<SmartCollection> collections = shopifyClient.getSmartCollections();
         LOGGER.info("size={}", collections.size());
         for (SmartCollection collection : collections)
         {
            long id = collection.getId();
            String title = collection.getTitle();
            String newTitleTag = null;
            List<Metafield> metafields = shopifyClient.getCollectionSeoData(id);
            for (Metafield metafield : metafields)
            {
               if ("title_tag".equals(metafield.getKey()))
               {
                  String currentTitleTag = metafield.getValue();
                  newTitleTag = currentTitleTag.replace("| SpindleAndRose", "| Fabric By The Yard | SpindleAndRose");
                  break;
               }
            }

            if (newTitleTag != null && newTitleTag.length() <= 60)
            {
               LOGGER.info("setting titleTag for {} to {}", title, newTitleTag);
               shopifyClient.setCollectionTitleTag(id, newTitleTag);
               Utils.sleep(500L);
            }
            Utils.sleep(500L);
            //shopifyClient.setKeywords(id, "collections", keywords);
         }
         /*
         ShopifyClient shopifyClient = new ShopifyClient();
         String filename = "C:\\tmp\\collections.csv";
         //toFile(getCollectionData(), filename);
         int count = 0;
         Map<Long, String> titleTagMap = titleTagMapFromFile(filename);
         for (Map.Entry<Long, String> entry : titleTagMap.entrySet())
         {
            LOGGER.info("{} of {} {} {}", ++count, titleTagMap.size(), entry.getKey(), entry.getValue());
            shopifyClient.setCollectionTitleTag(entry.getKey(), entry.getValue());
            Utils.sleep(1000L);
         }
         count = 0;
         Map<Long, String> descriptionTagMap = descriptionTagMapFromFile(filename);
         for (Map.Entry<Long, String> entry : descriptionTagMap.entrySet())
         {
            LOGGER.info("{} of {} {} {}", ++count, descriptionTagMap.size(), entry.getKey(), entry.getValue());
            shopifyClient.setCollectionDescriptionTag(entry.getKey(), entry.getValue());
            Utils.sleep(1000L);
         }
         */
      }
      catch (Exception e)
      {
         LOGGER.error("error", e);
      }
   }

   private static List<SmartCollection> getCollectionData()
   {
      ShopifyClient shopifyClient = new ShopifyClient();
      List<SmartCollection> collections = shopifyClient.getSmartCollections();
      for (SmartCollection collection : collections)
      {
         List<Metafield> metafields = shopifyClient.getCollectionSeoData(collection.getId());
         for (Metafield metafield : metafields)
         {
            if ("title_tag".equals(metafield.getKey()))
            {
               collection.setTitleTag(metafield.getValue());
               collection.getKeywords().add(keywordFromTitleTag(collection.getTitleTag()));
               if (collection.getKeywords().contains("warm color fabric"))
               {
                  collection.getKeywords().add("brown fabric");
                  collection.getKeywords().add("tan fabric");
                  collection.getKeywords().add("gold fabric");
                  collection.getKeywords().add("yellow fabric");
                  collection.getKeywords().add("off white fabric");
                  collection.getKeywords().add("cream fabric");
                  collection.getKeywords().add("orange fabric");
                  collection.getKeywords().add("red fabric");
                  collection.getKeywords().add("pink fabric");
                  collection.getKeywords().add("white fabric");
               }
               if (collection.getKeywords().contains("cool color fabric"))
               {
                  collection.getKeywords().add("black fabric");
                  collection.getKeywords().add("blue fabric");
                  collection.getKeywords().add("grey fabric");
                  collection.getKeywords().add("green fabric");
                  collection.getKeywords().add("purple fabric");
               }
               if (collection.getKeywords().contains("red fabric"))
               {
                  collection.getKeywords().add("pink fabric");
               }
               if (collection.getKeywords().contains("grey fabric"))
               {
                  collection.getKeywords().add("gray fabric");
               }
               if (collection.getKeywords().contains("sateen fabric"))
               {
                  collection.getKeywords().add("satin fabric");
               }
               if (collection.getKeywords().contains("stretchy fabric"))
               {
                  collection.getKeywords().add("double gauze fabric");
               }
               if (collection.getKeywords().contains("mexican fabric"))
               {
                  collection.getKeywords().add("southwest fabric");
               }
               if (collection.getKeywords().contains("sale fabric"))
               {
                  collection.getKeywords().add("discount fabric");
               }
               if (collection.getKeywords().contains("flannel fabric"))
               {
                  collection.getKeywords().add("brushed cotton fabric");
               }
               if (collection.getKeywords().contains("solid fabric"))
               {
                  collection.getKeywords().add("tonal fabric");
               }
               if (collection.getKeywords().contains("animal fabric"))
               {
                  collection.getKeywords().add("forest animal fabric");
               }
               if (collection.getKeywords().contains("woodland fabric"))
               {
                  collection.getKeywords().add("forest animal fabric");
               }
               if (collection.getKeywords().contains("striped fabric"))
               {
                  collection.getKeywords().add("geometric fabric");
                  collection.getKeywords().add("polka dot fabric");
               }
               if (collection.getKeywords().contains("solid and striped fabric"))
               {
                  collection.getKeywords().remove("solid and striped fabric");
                  collection.getKeywords().add("solid fabric");
                  collection.getKeywords().add("striped fabric");
               }
               if (collection.getKeywords().contains("sewing accessories"))
               {
                  collection.getKeywords().add("buttons");
                  collection.getKeywords().add("ribbon");
                  collection.getKeywords().add("purse handles");
               }
               collection.setKeywords(Utils.augmentKeywords(collection.getKeywords()));
               if (collection.getKeywords().contains("sale fabric"))
               {
                  collection.getKeywords().remove("sale fabric for sale");
               }
            }
            if ("description_tag".equals(metafield.getKey()))
            {
               collection.setDescriptionTag(metafield.getValue());
            }
            LOGGER.info(metafield.getKey() + ": " + metafield.getValue());
         }
         Utils.sleep(1000L);
      }
      return collections;
   }

   private static String keywordFromTitleTag(String titleTag)
   {
      String s = titleTag.split("\\|")[0].toLowerCase();
      s = s.replace("buy", "");
      s = s.replace("online", "");
      return s.trim();
   }

   private static Map<Long, String> descriptionTagMapFromFile(String filename) throws Exception
   {
      Map<Long, String> map = new HashMap<>();
      List<String> lines = Files.readAllLines(Paths.get(filename), StandardCharsets.ISO_8859_1);
      int count = 0;
      for (String line : lines)
      {
         if (++count == 1)
         {
            continue;
         }
         String[] split = line.split(",");
         Long id = Long.parseLong(split[0].trim());
         String descriptionTag = split[4].trim().replaceAll("-", ",");
         map.put(id, descriptionTag);
      }
      return map;
   }

   private static Map<String, String> fromFile(String filename) throws Exception
   {
      Map<String, String> map = new HashMap<>();
      List<String> lines = Files.readAllLines(Paths.get(filename), StandardCharsets.ISO_8859_1);
      int count = 0;
      for (String line : lines)
      {
         if (++count == 1)
         {
            continue;
         }
         String[] split = line.split(",");
         String title = split[0].trim();
         String keywords = split[1].trim().replaceAll("\\|", ",");
         map.put(title, keywords);
      }
      return map;
   }

   private static void toFile(List<SmartCollection> collections, String filename)
   {
      StringBuilder sb = new StringBuilder();
      sb.append("Id,Title,TitleTag,Keywords,Handle,DescriptionTag\n");
      for (SmartCollection collection : collections)
      {
         sb.append(collection.getId());
         sb.append(",");
         sb.append(collection.getTitle());
         sb.append(",");
         sb.append(collection.getTitleTag());
         sb.append(",");
         sb.append(Utils.formatKeywords(collection.getKeywords(), '|'));
         sb.append(",");
         sb.append(collection.getHandle());
         sb.append(",");
         sb.append(collection.getDescriptionTag().replaceAll(",", "-"));
         sb.append("\n");
      }
      Utils.write(filename, sb.toString());
   }
}
