package com.islandsoftware.storefronhq.tools;

import com.islandsoftware.storefronhq.etsy.EtsyClient;
import com.islandsoftware.storefronhq.etsy.SpindleAndRoseEtsyClient;
import com.islandsoftware.storefronhq.shopify.sync.ShopifyClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class BuildEtsyToShopifyIdMap
{
   private static final Logger LOGGER = LoggerFactory.getLogger(BuildEtsyToShopifyIdMap.class);

   public static void main(String[] args)
   {
      Map<Long, Long> map = buildEtsyId2ShopifyIdMap();
      writeEtsyId2ShopifyIdMap(map);
      Map<Long, Long> etsyId2ShopifyIdMap = readEtsyId2ShopifyIdMap();
      for (Entry<Long, Long> entry : etsyId2ShopifyIdMap.entrySet())
      {
         LOGGER.info("EtsyId={}, ShopifyId={}", entry.getKey(), entry.getValue());
      }
   }

   public static Map<Long, Long> buildEtsyId2ShopifyIdMap()
   {
      LOGGER.info("buildEtsyId2ShopifyMap begin");
      Map<Long, Long> etsyId2ShopifyId = new HashMap<>();
      EtsyClient etsyClient = new SpindleAndRoseEtsyClient();
      Map<String, Long> etsyMap = etsyClient.getTitle2Id();
      Map<Long, String> etsyListingIdToTitleMap = etsyClient.getId2Title();
      ShopifyClient shopifyClient = new ShopifyClient();
      Map<String, Long> shopifyMap = shopifyClient.getTitle2Id();
      for (String title : etsyMap.keySet())
      {
         etsyId2ShopifyId.put(etsyMap.get(title), shopifyMap.get(title));
      }
      checkEtsyId2ShopifyIdMap(etsyId2ShopifyId, etsyListingIdToTitleMap);
      LOGGER.info("buildEtsyId2ShopifyMap complete");
      return etsyId2ShopifyId;
   }

   public static void writeEtsyId2ShopifyIdMap(Map<Long, Long> etsyId2ShopifyIdMap)
   {
      LOGGER.info("writeEtsyId2ShopifyMap begin");
      List<String> lines = new ArrayList<>();
      for (Entry<Long, Long> entry : etsyId2ShopifyIdMap.entrySet())
      {
         if (entry.getValue() != null)
         {
            lines.add("EtsyId=" + entry.getKey() + ",ShopifyId=" + entry.getValue());
         }
      }
      try
      {
         Files.write(Paths.get("etsyId2ShopifyId.txt"), lines);
      }
      catch (IOException e)
      {
         LOGGER.error("writeEtsyId2ShopifyIdMap", e);
      }
      LOGGER.info("writeEtsyId2ShopifyMap complete");
   }

   public static Map<Long, Long> readEtsyId2ShopifyIdMap()
   {
      LOGGER.info("readEtsyId2ShopifyMap begin");
      Map<Long, Long> etsyId2ShopifyIdMap = new HashMap<>();
      try
      {
         List<String> lines = Files.readAllLines(Paths.get("etsyId2ShopifyId.txt"));
         for (String line : lines)
         {
            String[] split = line.split(",");
            String etsy = split[0];
            String shopify = split[1];

            String[] etsySplit = etsy.split("=");
            Long etsyId = Long.valueOf(etsySplit[1]);

            String[] shopifySplit = shopify.split("=");
            Long shopifyId = Long.valueOf(shopifySplit[1]);

            etsyId2ShopifyIdMap.put(etsyId, shopifyId);
         }
         LOGGER.info("readEtsyId2ShopifyMap complete");
         return etsyId2ShopifyIdMap;
      }
      catch (IOException e)
      {
         LOGGER.error("readEtsyId2ShopifyIdMap", e);
         return null;
      }
   }

   private static void checkEtsyId2ShopifyIdMap(Map<Long, Long> etsyId2ShopifyId, Map<Long, String> etsyListingIdToTitleMap)
   {
      for (Entry<Long, Long> entry : etsyId2ShopifyId.entrySet())
      {
         if (entry.getValue() == null)
         {
            LOGGER.warn("Etsy title not found in Shopify {}", etsyListingIdToTitleMap.get(entry.getKey()));
         }
      }
   }

}
