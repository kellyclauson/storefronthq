package com.islandsoftware.storefronhq.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class SearchAnalyticsAndVolumeBuilder
{
   private static final Logger LOGGER = LoggerFactory.getLogger(SearchAnalyticsAndVolume.class);

   public static void main(String[] args)
   {
      SearchAnalyticsAndVolumeBuilder builder = new SearchAnalyticsAndVolumeBuilder();
      Map<String, SearchAnalyticsAndVolume> analyticsInfo = builder.readAnalyticsInfo("C:\\tmp\\spindleandrose-com_SearchAnalytics.csv");
      //Utils.write("C:\\tmp\\queries.txt", builder.outputQuerySets(analyticsInfo.keySet(), 100));
      Set<String> notFound = new TreeSet<>();
      for (int i = 1; i <= 10; i++)
      {
         String filename = "C:\\tmp\\KeywordPlanner." + i + ".csv";
         notFound.addAll(builder.readVolumeInfo(filename, analyticsInfo));
      }
      /*
      StringBuilder sb = new StringBuilder();
      for (String s : notFound)
      {
         sb.append(s).append("\n");
      }
      Utils.write("C:\\tmp\\queriesNotFound.txt", sb.toString());
      */
      builder.writeSearchAnalyticsAndVolumeInfo(analyticsInfo);
   }

   public void writeSearchAnalyticsAndVolumeInfo(Map<String, SearchAnalyticsAndVolume> analyticsAndVolumeMap)
   {
      StringBuilder sb = new StringBuilder();
      sb.append("Query").append(",");
      sb.append("Clicks").append(",");
      sb.append("Impressions").append(",");
      sb.append("CTR").append(",");
      sb.append("Avg Position").append(",");
      sb.append("Avg Monthly Searches").append(",");
      sb.append("Competition").append(",");
      sb.append("SuggestedBid").append("\n");
      for (SearchAnalyticsAndVolume saav : analyticsAndVolumeMap.values())
      {
         sb.append(saav.getQuery()).append(",");
         sb.append(saav.getClicks()).append(",");
         sb.append(saav.getImpressions()).append(",");
         sb.append(saav.getCtr()).append(",");
         sb.append(saav.getAvgPosition()).append(",");
         sb.append(saav.getMonthlySearches()).append(",");
         sb.append(saav.getCompetition()).append(",");
         sb.append(saav.getSuggestedBid()).append("\n");
      }
      Utils.write("C:\\tmp\\lowHangingFruit.csv", sb.toString());
   }

   public Set<String> readVolumeInfo(String filename, Map<String, SearchAnalyticsAndVolume> analyticsInfo)
   {
      LOGGER.info("readVolumeInfo: filename={}", filename);
      Set<String> notFound = new HashSet<>();
      try (BufferedReader br = new BufferedReader(new FileReader(filename)))
      {
         String line = br.readLine();
         int i = 0;
         while (line != null)
         {
            if (i++ == 0)
            {
               // header row
            }
            else
            {
               LOGGER.debug("{}", line);
               String[] split = line.split(",");
               String keyword = split[1].trim();
               LOGGER.debug("keyword={}", keyword);
               SearchAnalyticsAndVolume saav = analyticsInfo.get(keyword);
               if (saav == null)
               {
                  LOGGER.info("Could not find {} in analytics map", keyword);
                  notFound.add(keyword);
               }
               else
               {
                  saav.setMonthlySearches(split[3].equals("") ? 0 : Integer.parseInt(split[3]));
                  saav.setCompetition(split[4].equals("") ? 0.0 : Double.parseDouble(split[4]));
                  saav.setSuggestedBid(split[5].equals("") ? 0.0 : Double.parseDouble(split[5]));
               }
            }
            line = br.readLine();
         }
      }
      catch (Exception e)
      {
         LOGGER.error("readVolumeInfo", e);
      }
      return notFound;
   }

   public Map<String, SearchAnalyticsAndVolume> readAnalyticsInfo(String filename)
   {
      LOGGER.info("readAnalyticsInfo: filename={}", filename);
      Map<String, SearchAnalyticsAndVolume> analytics = new HashMap<>();
      try (BufferedReader br = new BufferedReader(new FileReader(filename)))
      {
         String line = br.readLine();
         int i = 0;
         while (line != null)
         {
            if (i++ == 0)
            {
               // header row
            }
            else
            {
               String[] split = line.split(",");
               SearchAnalyticsAndVolume saav = new SearchAnalyticsAndVolume();
               if (split[0].contains("%") || split[0].contains("."))
               {
                  // google keyword planner volume search does not like special characters
               }
               else
               {
                  saav.setQuery(split[0].trim());
                  saav.setClicks(Integer.parseInt(split[1]));
                  saav.setImpressions(Integer.parseInt(split[2]));
                  saav.setCtr(Double.parseDouble(split[3].replace("%", "")));
                  saav.setAvgPosition(Double.parseDouble(split[4]));
                  analytics.put(saav.getQuery(), saav);
               }
            }
            line = br.readLine();
         }
      }
      catch (Exception e)
      {
         LOGGER.error("readAnalyticsInfo", e);
      }
      LOGGER.info("readAnalyticsInfo: numberOfQueries={}", analytics.size());
      return analytics;
   }
   
   public String outputQuerySets(Collection<String> queries, int setSize)
   {
      StringBuilder sb = new StringBuilder();
      int count = 0;
      int setCount = 0;
      for (String query : queries)
      {
         if (count % setSize == 0)
         {
            sb.append("\n");
            sb.append("set # ").append(setCount + 1).append("****************");
            setCount++;
         }
         sb.append("\n");
         sb.append(query);
         count++;
      }
      sb.append("\n");
      sb.append(setCount).append("sets of ").append(setSize);
      return sb.toString();
   }

}
