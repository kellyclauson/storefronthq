package com.islandsoftware.storefronhq.tools;

import com.islandsoftware.storefronhq.shopify.sync.ShopifyClient;
import com.islandsoftware.storefronhq.shopify.sync.model.Redirect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopifyRedirect
{
   private static final Logger LOGGER = LoggerFactory.getLogger(ShopifyRedirect.class);

   public static void main(String[] args)
   {
      try
      {
         //List<Redirect> redirects = getRedirects();
         //toFile(redirects, "C:\\tmp\\redirects.csv");

         List<Redirect> redirects = fromFile("C:\\tmp\\redirects.csv");
         Map<String, Redirect> redirectMap = new HashMap<>();
         for (Redirect redirect : redirects)
         {
            redirectMap.put(redirect.getPath(), redirect);
         }
         List<Redirect> fixesNeeded = new ArrayList<>();
         List<Redirect> errors = fromCrawlErrorsFile("C:\\tmp\\CrawlErrors.csv");
         for (Redirect error : errors)
         {
            Redirect redirect = redirectMap.get(error.getPath());
            if (redirect == null)
            {
               error.setTarget("set me");
               fixesNeeded.add(error);
            }
            else
            {
               LOGGER.info("redirect exists for {}", error.getPath());
            }
         }
         setTarget(fixesNeeded);
         //toFile(fixesNeeded, "C:\\tmp\\redirects-needed.csv");
         postRedirects(fixesNeeded);
      }
      catch (Exception e)
      {
         LOGGER.error("ShopifyRedirect", e);
      }
   }

   private static void setTarget(List<Redirect> fixesNeeded)
   {
      for (Redirect redirect : fixesNeeded)
      {
         String path = redirect.getPath();
         if (path.contains("thread") || path.contains("ribbon") || path.contains("button"))
         {
            redirect.setTarget("/collections/notions");
         }
         else if (path.contains("mexican") || path.contains("southwest") || path.contains("frida"))
         {
            redirect.setTarget("/collections/mexican-southwest");
         }
         else if (path.contains("henry"))
         {
            redirect.setTarget("/collections/alexander-henry-fabric");
         }
         else if (path.contains("steel"))
         {
            redirect.setTarget("/collections/cotton-steel-fabrics");
         }
         else if (path.contains("organic"))
         {
            redirect.setTarget("/collections/organic-cotton");
         }
         else if (path.contains("christmas"))
         {
            redirect.setTarget("/collections/christmas-fabric");
         }
         else if (path.contains("halloween"))
         {
            redirect.setTarget("/collections/halloween-fabrics");
         }
         else if (path.contains("alice"))
         {
            redirect.setTarget("/collections/alice-in-wonderland");
         }
         else if (path.contains("animal") || path.contains("dog") ||
               path.contains("cat") || path.contains("owl") || path.contains("bird") ||
               path.contains("bunny") || path.contains("rabbit") || path.contains("squirrel"))
         {
            redirect.setTarget("/collections/animal-fabric");
         }
         else if (path.contains("blend"))
         {
            redirect.setTarget("/collections/blend-fabrics");
         }
         else if (path.contains("floral") || path.contains("flower"))
         {
            redirect.setTarget("/collections/floral");
         }
         else if (path.contains("kokka") || path.contains("japanese") || path.contains("island"))
         {
            redirect.setTarget("/collections/japanese-fabric");
         }
         else if (path.contains("beach") || path.contains("ocean") || path.contains("sea"))
         {
            redirect.setTarget("/collections/beach-ocean");
         }
         else if (path.contains("woodland"))
         {
            redirect.setTarget("/collections/woodland");
         }
         else
         {
            redirect.setTarget("/collections/all-fabric");
         }
      }

   }

   private static List<Redirect> updatesFromFile(String filename) throws Exception
   {
      List<Redirect> redirects = new ArrayList<>();
      List<String> lines = Files.readAllLines(Paths.get(filename));
      int count = 0;
      for (String line : lines)
      {
         ++count;
         if (count == 1)
         {
            continue;
         }
         String[] split = line.split(",");
         String status = split[3];
         if (!status.equals("delete") && !status.equals("good"))
         {
            Redirect redirect = new Redirect();
            redirect.setId(Long.parseLong(split[0].trim()));
            redirect.setPath(split[1].trim());
            redirect.setTarget(split[3].trim());
            redirects.add(redirect);
         }
      }
      return redirects;
   }

   private static void toFile(List<Redirect> redirects, String filename)
   {
      StringBuilder sb = new StringBuilder();
      sb.append("Id,OldUrl,NewUrl\n");
      for (Redirect redirect : redirects)
      {
         sb.append(redirect.getId());
         sb.append(",");
         sb.append(redirect.getPath());
         sb.append(",");
         sb.append(redirect.getTarget());
         sb.append("\n");
      }
      Utils.write(filename, sb.toString());
   }

   private static List<Redirect> fromCrawlErrorsFile(String filename) throws Exception
   {
      List<Redirect> redirects = new ArrayList<>();
      List<String> lines = Files.readAllLines(Paths.get(filename));
      int count = 0;
      for (String line : lines)
      {
         ++count;
         if (count == 1)
         {
            continue;
         }
         String[] split = line.split(",");
         String responseCode = split[1].trim();
         if ("404".equals(responseCode))
         {
            String url = split[0];
            Redirect redirect = new Redirect();
            redirect.setPath(extractPath(url));
            redirects.add(redirect);
         }
      }
      return redirects;
   }

   private static String extractPath(String url)
   {
      String host = "https://spindleandrose.com";
      return url.substring(host.length());
   }

   private static List<Redirect> deletesFromFile(String filename) throws Exception
   {
      List<Redirect> redirects = new ArrayList<>();
      List<String> lines = Files.readAllLines(Paths.get(filename));
      int count = 0;
      for (String line : lines)
      {
         ++count;
         if (count == 1)
         {
            continue;
         }
         String[] split = line.split(",");
         if ("delete".equals(split[3]))
         {
            Redirect redirect = new Redirect();
            redirect.setId(Long.parseLong(split[0].trim()));
            redirect.setPath(split[1].trim());
            redirect.setTarget(split[2].trim());
            redirects.add(redirect);
         }
      }
      return redirects;
   }

   private static List<Redirect> fromFile(String filename) throws Exception
   {
      List<Redirect> redirects = new ArrayList<>();
      List<String> lines = Files.readAllLines(Paths.get(filename));
      int count = 0;
      for (String line : lines)
      {
         ++count;
         if (count == 1)
         {
            continue;
         }
         Redirect redirect = new Redirect();
         String[] split = line.split(",");
         redirect.setId(Long.parseLong(split[0]));
         redirect.setPath(split[1].trim());
         redirect.setTarget(split[2].trim());
         redirects.add(redirect);
      }
      return redirects;
   }

   public static void postRedirects(List<Redirect> redirects)
   {
      ShopifyClient shopifyClient = new ShopifyClient();
      for (Redirect redirect : redirects)
      {
         LOGGER.info("postRedirects: {}", redirect);
         try
         {
            shopifyClient.addRedirect(redirect);
         }
         catch (Exception e)
         {
            LOGGER.error("Could not create redirect for {}", redirect, e.getMessage());
         }
         Utils.sleep(1000L);
      }
   }

   public static List<Redirect> getRedirects()
   {
      ShopifyClient shopifyClient = new ShopifyClient();
      List<Redirect> redirects = shopifyClient.getRedirects();
      return redirects;
   }
}
