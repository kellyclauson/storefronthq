package com.islandsoftware.storefronhq.tools;

import com.islandsoftware.storefronhq.shopify.sync.model.Product;
import com.islandsoftware.storefronhq.GoogleSheets;
import com.islandsoftware.storefronhq.orderprocessing.ProductInfo;
import com.islandsoftware.storefronhq.shopify.sync.ShopifyClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class ShopifyTags
{
   private static final Logger LOGGER = LoggerFactory.getLogger(ShopifyTags.class);

   private static final long MONTH_IN_MILLI_SECONDS = 2592000000L;

   private static String ALICE_IN_WONDERLAND = "Alice In Wonderland";
   private static String ANIMALS = "Animals";
   private static String BABY = "Baby";
   private static String BEACH_AND_OCEAN = "Beach + Ocean";
   private static String BEARS = "Bears";
   private static String BIRDS = "Birds";
   private static String BLACK = "Black";
   private static String BLACK_AND_WHITE = "Black And White";
   private static String BLUE = "Blue";
   private static String BROWN = "Brown";
   private static String BRUSHED_COTTON = "Brushed Cotton";
   private static String CACTUS = "Cactus";
   private static String CANVAS = "Canvas";
   private static String CHRISTMAS = "Christmas";
   private static String COTTON = "100% Cotton";
   private static String COTTON_LAWN = "Cotton Lawn";
   private static String DEER = "Deer";
   private static String DOUBLE_GAUZE = "Double Gauze";
   private static String FANTASY_ANIMALS = "Fantasy Animals";
   private static String FLANNEL = "Flannel";
   private static String FLORAL_AND = "Floral +";
   private static String FRIDA = "Frida";
   private static String GEOMETRIC = "Geometric";
   private static String GOLD = "Gold";
   private static String GREEN = "Green";
   private static String GREY = "Grey";
   private static String HALLOWEEN = "Halloween";
   private static String JAPANESE = "Japanese";
   private static String KNITS = "Knits";
   private static String LAMINATE = "Laminate";
   private static String LITTLE_RED_RIDING_HOOD = "Little Red Riding Hood";
   private static String NATIVE_AMERICAN = "Native American";
   private static String NEW_ARRIVAL = "New Arrival";
   private static String OFF_WHITE = "Off White";
   private static String ORANGE = "Orange";
   private static String ORGANIC = "Organic";
   private static String OXFORD_COTTON = "Oxford Cotton";
   private static String PINK = "Pink";
   private static String POLKA_DOTS = "Polka Dots";
   private static String PURPLE = "Purple";
   private static String RED = "Red";
   private static String RIBBON_AND_THREAD = "Ribbon + Thread";
   private static String SALE = "Sale";
   private static String SATEEN_AND_SATIN = "Sateen + Satin";
   private static String SARAH_WATSS = "Sarah Watts";
   private static String SOUTHWEST = "Southwest";
   private static String STORY_BOOK = "Story Book";
   private static String STRIPED = "Striped";
   private static String TONAL_AND_SOLIDS = "Tonal + Solids";
   private static String TRAVEL = "Travel";
   private static String WHITE = "White";
   private static String WOODLAND = "Woodland";
   private static String YELLOW = "Yellow";

   private static Set<String> TAGS = new TreeSet<>();
   private static Set<String> COLLECTION_SOUTHWEST = new TreeSet<>();
   private static Set<String> COLLECTION_ANIMALS = new TreeSet<>();
   static
   {
      TAGS.add(ALICE_IN_WONDERLAND);
      TAGS.add(ANIMALS);
      TAGS.add(BABY);
      TAGS.add(BEACH_AND_OCEAN);
      TAGS.add(BEARS);
      TAGS.add(BIRDS);
      TAGS.add(BLACK);
      TAGS.add(BLACK_AND_WHITE);
      TAGS.add(BLUE);
      TAGS.add(BROWN);
      TAGS.add(BRUSHED_COTTON);
      TAGS.add(CACTUS);
      TAGS.add(CANVAS);
      TAGS.add(CHRISTMAS);
      TAGS.add(COTTON);
      TAGS.add(COTTON_LAWN);
      TAGS.add(DEER);
      TAGS.add(DOUBLE_GAUZE);
      TAGS.add(FANTASY_ANIMALS);
      TAGS.add(FLANNEL);
      TAGS.add(FLORAL_AND);
      TAGS.add(FRIDA);
      TAGS.add(GEOMETRIC);
      TAGS.add(GOLD);
      TAGS.add(GREEN);
      TAGS.add(GREY);
      TAGS.add(HALLOWEEN);
      TAGS.add(JAPANESE);
      TAGS.add(KNITS);
      TAGS.add(LAMINATE);
      TAGS.add(LITTLE_RED_RIDING_HOOD);
      TAGS.add(NATIVE_AMERICAN);
      TAGS.add(NEW_ARRIVAL);
      TAGS.add(OFF_WHITE);
      TAGS.add(ORANGE);
      TAGS.add(ORGANIC);
      TAGS.add(OXFORD_COTTON);
      TAGS.add(PINK);
      TAGS.add(POLKA_DOTS);
      TAGS.add(PURPLE);
      TAGS.add(RED);
      TAGS.add(RIBBON_AND_THREAD);
      TAGS.add(SALE);
      TAGS.add(SATEEN_AND_SATIN);
      TAGS.add(SARAH_WATSS);
      TAGS.add(SOUTHWEST);
      TAGS.add(STORY_BOOK);
      TAGS.add(STRIPED);
      TAGS.add(TONAL_AND_SOLIDS);
      TAGS.add(TRAVEL);
      TAGS.add(WHITE);
      TAGS.add(WOODLAND);
      TAGS.add(YELLOW);

      COLLECTION_SOUTHWEST.add(FRIDA);
      COLLECTION_SOUTHWEST.add(NATIVE_AMERICAN);
      COLLECTION_SOUTHWEST.add(CACTUS);

      COLLECTION_ANIMALS.add(BEARS);
      COLLECTION_ANIMALS.add(DEER);
   }

   private static boolean contains(String tag)
   {
      return TAGS.contains(tag);
   }

   public static void main(String[] args)
   {
      try
      {
         //getTagsFromShopify();
         Map<String, ProductInfo> productInfo = GoogleSheets.readProductInfo(false);
         Map<String, String> tagMap = getTagsFromFile("C:\\tmp\\titlesandtags.csv");
         Map<String, String> updatedTagMap = new TreeMap<>();
         for (Map.Entry<String, String> entry : tagMap.entrySet())
         {
            String tags = entry.getValue();
            tags = tags.replaceAll("\\|", ",");
            String invalidTags = ShopifyTags.validate(tags);
            if (invalidTags != null)
            {
               LOGGER.error("invalid tags: {}", invalidTags);
               throw new Exception("invalid tags: " + invalidTags);
            }
            tags = ShopifyTags.augmentTags(tags, entry.getKey(), productInfo.values());
            //tags = "New Arrivals," + tags;
            LOGGER.info("{}: {}", entry.getKey(), tags);
            updatedTagMap.put(entry.getKey().trim(), tags);
            //LOGGER.info("private static String {} = \"{}\";", entry.getKey().toUpperCase().replaceAll("\\+", "AND").replaceAll(" ", "_"), entry.getKey());
            //LOGGER.info("TAGS.add({});", entry.getKey().toUpperCase().replaceAll("\\+", "AND").replaceAll(" ", "_"));
         }

         ShopifyClient client = new ShopifyClient();
         Map<String, Product> productMap = client.getTitleToProductMap();
         for (String title : productMap.keySet())
         {
            if (!updatedTagMap.containsKey(title))
            {
               LOGGER.info("DID NOT SET TAGS FOR {}", title);
            }
         }


         //setShopifyTags(updatedTagMap);
         /*
         LOGGER.info("\nNumber of Tags: {}", tags.size());
         LOGGER.info("\nTags with less than 3 products:");
         for (Map.Entry<String, Integer> entry : tags.entrySet())
         {
            if (entry.getValue() < 3)
            {
               LOGGER.info("{}\t{}", entry.getValue(), entry.getKey());
            }
         }
         */
      }
      catch (Exception e)
      {
         LOGGER.error("ShopifyTags", e);
      }
   }

   public static void setShopifyTags(Map<String, String> title2tags) throws Exception
   {
      ShopifyClient client = new ShopifyClient();
      Map<String, Product> productMap = client.getTitleToProductMap();
      int count = 0;
      for (Map.Entry<String, String> entry : title2tags.entrySet())
      {
         Product product = productMap.get(entry.getKey().trim());
         if (product == null)
         {
            throw new Exception(entry.getKey() + " not found in product map");
         }
         product.setTags(entry.getValue());
         LOGGER.info("{} of {} title={} tags={}", ++count, title2tags.size(), entry.getKey(), product.getTags());
         client.updateProduct(product);
         Utils.sleep(1000L);
      }
   }

   public static void getTagsFromShopify()
   {
      ShopifyClient client = new ShopifyClient();
      Map<String, Product> title2ProductMap = client.getTitleToProductMap();
      StringBuilder sb = new StringBuilder();
      sb.append("Title,Tags\n");
      for (Product product : title2ProductMap.values())
      {
         sb.append(product.getTitle()).append(",");
         String tags = product.getTags();
         String[] split = tags.split(",");
         for (String tag : split)
         {
            sb.append(tag).append("|");
         }
         sb.append("\n");
      }
      Utils.write("C:\\tmp\\titlesandtags.csv", sb.toString());
   }

   public static Map<String, String> getTagsFromFile(String filename) throws Exception
   {
      Map<String, String> tagMap = new TreeMap<>();
      List<String> lines = Files.readAllLines(Paths.get(filename));
      int lineNumber = 0;
      for (String line : lines)
      {
         if (++lineNumber > 1)
         {
            String[] split = line.split(",");
            String  title = split[0].trim();
            String tags = split[1].trim();
            tagMap.put(title, tags);
         }
      }
      return tagMap;
   }

   /*
   public static Map<String, Integer> getTagsFromFile(String filename) throws Exception
   {
      Map<String, Integer> tagMap = new TreeMap<>();
      List<String> lines = Files.readAllLines(Paths.get(filename));
      int lineNumber = 0;
      for (String line : lines)
      {
         if (++lineNumber > 1)
         {
            String[] split = line.split(",");
            String  tags = split[1];
            String[] s = tags.split("\\|");
            for (String tag : s)
            {
               Integer count = tagMap.get(tag.trim());
               if (count == null)
               {
                  tagMap.put(tag.trim(), 1);
               }
               else
               {
                  tagMap.put(tag.trim(), count + 1);
               }
            }
         }
      }
      return tagMap;
   }
   */

   public static String augmentTags(String tags, String shopifyTitle, Collection<ProductInfo> productInfo)
   {
      LOGGER.info("augmentTags: incoming tags={}", tags);
      if (tags.equals("not set"))
      {
         tags = "";
      }
      Set<String> tagSet = new TreeSet<>();
      for (ProductInfo info : productInfo)
      {
         if (info.getShopifyTitle().equals(shopifyTitle))
         {
            LOGGER.info("augmentTags: dateAdded={}", info.getDateAdded());
            if (info.getDateAdded().getTime() > System.currentTimeMillis() - MONTH_IN_MILLI_SECONDS)
            {
               tagSet.add(NEW_ARRIVAL);
            }
            break;
         }
      }

      String[] split = tags.split(",");
      for (String tag : split)
      {
         tag = tag.trim();
         tagSet.add(tag);
         if (COLLECTION_ANIMALS.contains(tag))
         {
            LOGGER.info("augmentTags: adding {} to tag set", ANIMALS);
            tagSet.add(ANIMALS);
         }
         tagSet.add(tag);
         if (COLLECTION_SOUTHWEST.contains(tag))
         {
            LOGGER.info("augmentTags: adding {} to tag set", SOUTHWEST);
            tagSet.add(SOUTHWEST);
         }
      }
      StringBuilder sb = new StringBuilder();
      for (String tag : tagSet)
      {
         sb.append(tag).append(",");
      }
      if (sb.length() > 0)
      {
         sb.deleteCharAt(sb.length() - 1);
      }
      return sb.toString();
   }

   public static String validate(String tags)
   {
      StringBuilder sb = new StringBuilder();
      String[] split = tags.split(",");
      int count = 0;
      for (String tag : split)
      {
         if (!contains(tag.trim()))
         {
            LOGGER.error("validate: {} is not a valid tag", tag);
            sb.append(tag).append(",");
         }
      }
      if (sb.length() > 0)
      {
         sb.deleteCharAt(sb.length() - 1);
         return sb.toString();
      }
      else
      {
         return null;
      }
   }
}
