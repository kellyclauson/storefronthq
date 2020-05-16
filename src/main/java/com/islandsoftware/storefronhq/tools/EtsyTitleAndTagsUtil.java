package com.islandsoftware.storefronhq.tools;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class EtsyTitleAndTagsUtil
{
   public static void toFile(Map<Long, EtsyTitleAndTags> titleAndTagsMap, String filename)
   {
      StringBuilder sb = new StringBuilder();
      sb.append("Id,Title,Tags\n");
      for (Map.Entry<Long, EtsyTitleAndTags> entry : titleAndTagsMap.entrySet())
      {
         Long id = entry.getKey();
         EtsyTitleAndTags titleAndTags = entry.getValue();
         sb.append(id).append(",").append(titleAndTags.getTitle()).append(",").append(toString(titleAndTags.getTags(), "|")).append("\n");
      }
      Utils.write(filename, sb.toString());
   }

   public static Map<Long, EtsyTitleAndTags> fromFile(String filename) throws Exception
   {
      Map<Long, EtsyTitleAndTags> titleAndTagsMap = new HashMap<>();
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
         String tagList = split[2].trim();
         String[] tags = tagList.split("\\|");
         Set<String> tagSet = new HashSet<>();
         for (String tag : tags)
         {
            tagSet.add(tag.trim());
         }
         EtsyTitleAndTags titleAndTags = new EtsyTitleAndTags();
         titleAndTags.setId(id);
         titleAndTags.setTitle(title);
         titleAndTags.setTags(tagSet);
         titleAndTagsMap.put(id, titleAndTags);
      }
      return titleAndTagsMap;
   }

   public static String toString(Set<String> tags, String delimiter)
   {
      String tagList = "";
      int count = 0;
      for (String tag : tags)
      {
         if (++count == 1)
         {
            tagList = tag;
         }
         else
         {
            tagList = tagList + delimiter + tag;
         }
      }
      return tagList;
   }
}
