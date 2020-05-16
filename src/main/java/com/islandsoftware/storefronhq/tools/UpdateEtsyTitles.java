package com.islandsoftware.storefronhq.tools;

import com.islandsoftware.storefronhq.etsy.EtsyClient;
import com.islandsoftware.storefronhq.etsy.SpindleAndRoseEtsyClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class UpdateEtsyTitles
{
   private static final Logger LOGGER = LoggerFactory.getLogger(UpdateEtsyTitles.class);

   public static void main(String[] args)
   {

   }

   public static void titleUpdates()
   {
      try
      {
         //Map<String, Long> newTitles = fromFile("C:\\tmp\\etsyNewTitleAndTags.csv");
         Map<Long, EtsyTitleAndTags> originalTitleAndTagsMap = EtsyTitleAndTagsUtil.fromFile("C:\\tmp\\etsyOriginalTitleAndTags.csv");
         EtsyClient etsyClient = new SpindleAndRoseEtsyClient();
         Map<Long, String> id2Title = etsyClient.getId2Title();
         Map<Long, String> notFound = new HashMap<>();
         Map<Long, String> toBeUpdated = new HashMap<>();
         for (Map.Entry<Long, String> entry : id2Title.entrySet())
         {
            Long id = entry.getKey();
            EtsyTitleAndTags original = originalTitleAndTagsMap.get(id);
            if (original == null)
            {
               notFound.put(id, entry.getValue());
            }
            else
            {
               toBeUpdated.put(id, entry.getValue());
            }
         }
         LOGGER.info("Found {} titles to update", toBeUpdated.size());

         /*
         StringBuilder sb = new StringBuilder();
         sb.append("Id,CurrentTitle\n");
         for (Map.Entry<Long, String> entry : notFound.entrySet())
         {
            sb.append(entry.getKey()).append(",").append(entry.getValue()).append("\n");
         }
         Utils.write("C:\\tmp\\notUpdatedTitles.2.26.2019.csv", sb.toString());
         */

         int count = 0;
         for (Map.Entry<Long, String> entry : toBeUpdated.entrySet())
         {
            Long id = entry.getKey();
            EtsyTitleAndTags titleAndTags = originalTitleAndTagsMap.get(id);
            Map<String, String> params = new HashMap<>();
            params.put("title", titleAndTags.getTitle());
            params.put("tags", EtsyTitleAndTagsUtil.toString(titleAndTags.getTags(), ","));
            LOGGER.info("count={}, id={} {}", ++count, id, params);
            LOGGER.info("will update title from {} to {}", entry.getValue(), titleAndTags.getTitle());
            etsyClient.updateListing(id, params);
            Utils.sleep(1000L);
            //sb.append(entry.getKey()).append(",").append(entry.getValue()).append(",").append(original.getTitle()).append(",").append(EtsyTitleAndTagsUtil.toString(original.getTags(), "|")).append("\n");
         }
         //Utils.write("C:\\tmp\\updatedTitles.2.26.2019.csv", sb.toString());

         /*
         StringBuilder sb = new StringBuilder();
         for (Map.Entry<String, ListingsResult> entry : listings.entrySet())
         {
            LOGGER.info("{}", entry.getKey());
            Long id = newTitles.get(entry.getKey());
            sb.append(entry.getKey()).append(",");
            if (id == null)
            {
               sb.append("Not Found").append("\n");
            }
            else
            {
               String originalTitle = originalTitles.get(id);
               if (originalTitle == null)
               {
                  sb.append("Not Found").append("\n");
               }
               else
               {
                  sb.append(originalTitle).append("\n");
               }
            }
         }
         Utils.write("C:\\tmp\\etsyTitles.csv", sb.toString());
         */
         //Map<Long, EtsyTitleAndTags> titleAndTagsMap = new HashMap<>();
         //Map<Long, EtsyTitleAndTags> originalTitleAndTagsMap = new HashMap<>();
         /*
         for (String title : listings.keySet())
         {
            Long id = listings.get(title).getListingId();
            String originalTags[] = listings.get(title).getTags();
            EtsyTitleAndTags original = new EtsyTitleAndTags();
            original.setTitle(title);
            original.setTags(new HashSet<>(Arrays.asList(originalTags)));
            originalTitleAndTagsMap.put(id, original);

            if (title.toLowerCase().contains("last piece"))
            {
               continue;
            }
            String newTitle = titles.get(title);
            if (newTitle == null)
            {
               throw new Exception("Not Found In Title Map: " + title);
               //LOGGER.warn("Not Found In Title Map: " + title);
            }
            else
            {
               if (newTitle.length() > 130)
               {
                  throw new Exception("Title Too Long: " + newTitle);
               }
               Set<String> tags = createTags(newTitle);
               EtsyTitleAndTags titleAndTags = new EtsyTitleAndTags();
               titleAndTags.setTitle(newTitle);
               titleAndTags.setTags(tags);
               titleAndTagsMap.put(id, titleAndTags);
            }
         }
         */

         // store original titles and tags
         //toFile(originalTitleAndTagsMap, "C:\\tmp\\etsyOriginalTitleAndTags.csv");

         // store original titles and tags
         //toFile(titleAndTagsMap, "C:\\tmp\\etsyNewTitleAndTags.csv");

         /*
         int count = 0;
         for (Map.Entry<Long, EtsyTitleAndTags> entry : titleAndTagsMap.entrySet())
         {
            EtsyTitleAndTags titleAndTags = entry.getValue();
            Long id = entry.getKey();
            Map<String, String> params = new HashMap<>();
            params.put("title", titleAndTags.getTitle());
            params.put("tags", EtsyTitleAndTagsUtil.toString(titleAndTags.getTags(), ","));
            LOGGER.info("count={}, id={} {}", ++count, id, params);
            etsyClient.updateListing(id, params);
            Utils.sleep(1000L);
         }
         */
      }
      catch (Exception e)
      {
         LOGGER.error("error", e);
      }
   }

   private static Set<String> createTags(String title) throws Exception
   {
      Set<String> tags = new HashSet<>();
      String[] split = title.split("\\|");
      int count = 0;
      for (String s : split)
      {
         String tag = s.trim();
         if (tag.length() > 20)
         {
            if (tag.equalsIgnoreCase("fabric that looks like wood"))
            {
               tag = "Looks Like Wood";
            }
            if (tag.contains("Fabric"))
            {
               tag = tag.replace(" Fabric", "");
            }
            if (tag.contains("Thread"))
            {
               tag = tag.replace(" Thread", "");
            }
            if (tag.equalsIgnoreCase("our lady of guadalupe"))
            {
               tag = "Lady Of Guadalupe";
            }
            if (tag.equalsIgnoreCase("animal print material"))
            {
               tag = "Animal Material";
            }
            if (tag.equalsIgnoreCase("organic cotton sewing"))
            {
               tags.add("Sewing");
               tag = "Organic Cotton";
            }
            if (tag.equalsIgnoreCase("baby blanket material"))
            {
               tags.add("Baby Blanket");
               tag = "Material";
            }
            if (tag.equalsIgnoreCase("little red riding hood"))
            {
               tags.add("Little");
               tag = "Red Riding Hood";
            }
            if (tag.length() > 20)
            {
               throw new Exception("Tag too long: " + tag);
            }
         }
         tags.add(tag);
      }
      if (title.contains("Fabric"))
      {
         tags.add("Fabric");
      }
      if (title.contains("Thread"))
      {
         tags.add("Thread");
      }
      return tags;
   }

   public static Map<String, Long> fromFile(String filename) throws Exception
   {
      Map<String, Long> titles = new TreeMap<>();
      List<String> lines = Files.readAllLines(Paths.get(filename));
      int count = 0;
      for (String line : lines)
      {
         if (++count == 1)
         {
            continue;
         }
         String[] split = line.split(",");
         Long id = Long.parseLong(split[0].trim());
         String title = split[1].trim();
         titles.put(title, id);
      }
      return  titles;
   }

   public static Map<Long, String> fromFileById(String filename) throws Exception
   {
      Map<Long, String> titles = new TreeMap<>();
      List<String> lines = Files.readAllLines(Paths.get(filename));
      int count = 0;
      for (String line : lines)
      {
         if (++count == 1)
         {
            continue;
         }
         String[] split = line.split(",");
         Long id = Long.parseLong(split[0].trim());
         String title = split[1].trim();
         titles.put(id, title);
      }
      return  titles;
   }
}
