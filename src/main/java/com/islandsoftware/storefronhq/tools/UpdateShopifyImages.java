package com.islandsoftware.storefronhq.tools;

import com.islandsoftware.storefronhq.shopify.sync.model.Product;
import com.islandsoftware.storefronhq.shopify.sync.ShopifyClient;
import com.islandsoftware.storefronhq.shopify.sync.model.Metafield;
import com.islandsoftware.storefronhq.shopify.sync.model.MetafieldList;
import com.islandsoftware.storefronhq.shopify.sync.model.ShopifyImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class UpdateShopifyImages
{
   private static final String IMAGE_MAP_FILE = "C:\\tmp\\imagemap.csv";
   private static final Logger LOGGER = LoggerFactory.getLogger(UpdateShopifyImages.class);

   public static void main(String[] args)
   {
      try
      {
         // to update a single product - pass in title and base alt text
         updateImages("Snow Leopard Fabric | Cute Cat Fabric | Black and White | Cotton and Steel | White Cats with Black Spots | Unique | Animal Fabric", "White cats with black spots on black cotton from Cotton And Steel Fabrics");

         //Map<String, List<ShopifyImage>> imageMap = buildImageMap();
         //writeImageMap(imageMap, IMAGE_MAP_FILE);

         //Map<String, List<ShopifyImage>> imageMap = readImageMap(IMAGE_MAP_FILE);
         //replicateAltText(imageMap);
         //updateImages(imageMap);

         /*
         imageMap = readImageMap(IMAGE_MAP_FILE);
         for (Map.Entry<String, List<ShopifyImage>> entry : imageMap.entrySet())
         {
            LOGGER.info("{} {}", entry.getKey(), entry.getValue());
         }
         */
      }
      catch (Exception e)
      {
         LOGGER.error("error", e);
      }
   }

   public static void updateImages(String title, String altText) throws Exception
   {
      ShopifyClient shopifyClient = new ShopifyClient();
      Map<String, Long> title2IdMap = shopifyClient.getTitle2Id();
      Long id = title2IdMap.get(title);
      if (id == null)
      {
         throw new Exception("Cannot find Shopify Id for " + title);
      }
      Map<String, List<ShopifyImage>> imageMap = new HashMap<>();
      Product product = shopifyClient.getProduct(id, "title,images");
      ShopifyImage[] images = product.getImages();
      List<ShopifyImage> imageList = new ArrayList<>();
      imageMap.put(product.getTitle().trim().replaceAll(" ", ""), imageList);
      for (ShopifyImage image : images)
      {
         image.setAltText(altText);
         imageList.add(image);
      }
      replicateAltText(imageMap);
      for (ShopifyImage image : images)
      {
         shopifyClient.updateImage(id, image);
         Utils.sleep(1000L);
      }
   }

   private static void updateImages(Map<String, List<ShopifyImage>> imageMap)
   {
      ShopifyClient shopifyClient = new ShopifyClient();
      Map<Long, String> id2Title = shopifyClient.getId2Title();
      int count = 0;
      for (Map.Entry<Long, String> entry : id2Title.entrySet())
      {
         String title = entry.getValue();
         LOGGER.info("{} of {} title={}", ++count, id2Title.size(), title);
         List<ShopifyImage> images = imageMap.get(title.trim().replaceAll(" ", ""));
         for (ShopifyImage image : images)
         {
            shopifyClient.updateImage(entry.getKey(), image);
            Utils.sleep(1000L);
         }
      }
   }

   private static void replicateAltText(Map<String, List<ShopifyImage>> imageMap)
   {
      for (List<ShopifyImage> images : imageMap.values())
      {
         String text = "";
         for (ShopifyImage image : images)
         {
            if (image.getPosition() == 1)
            {
               text = image.getAltText();
            }
         }
         for (ShopifyImage image : images)
         {
            image.setAltText(text + " - view " + image.getPosition());
         }
      }
   }

   public static Map<String, List<ShopifyImage>> readImageMap(String filename) throws Exception
   {
      Map<String, List<ShopifyImage>> imageMap = new TreeMap<>();
      List<String> lines = Files.readAllLines(Paths.get(filename));
      int count = 0;
      for (String line : lines)
      {
         if (++count == 1)
         {
            // header
            continue;
         }
         String[] split = line.split(",");
         List<ShopifyImage> images = new ArrayList<>();
         for (int i = 0; i < split.length; i++)
         {
            if (i == 0)
            {
               String title = split[i];
               imageMap.put(title.trim().replaceAll(" ", ""), images);
            }
            else if (i % 2 == 1)
            {
               ShopifyImage image = new ShopifyImage();
               String[] postionAndId = split[i].split(":");
               image.setPosition(Integer.parseInt(postionAndId[0]));
               image.setId(Long.parseLong(postionAndId[1]));
               image.setAltText(split[i + 1]);
               images.add(image);
            }
         }
      }
      return imageMap;
   }


   private static void writeImageMap(Map<String, List<ShopifyImage>> imageMap, String filename)
   {
      StringBuilder sb = new StringBuilder();
      sb.append("Title,Image1Id,Image1Text,Image2Id,Image2Text,Image3Id,Image3Text,Image4Id,Image4Text,Image5Id,Image5Text,Image6Id,Image6Text,Image7Id,Image7Text,Image8Id,Image8Text,Image9Id,Image9Text,Image10Id,Image10Text").append("\n");
      for (Map.Entry<String, List<ShopifyImage>> entry : imageMap.entrySet())
      {
         sb.append(entry.getKey()).append(",");
         int size = imageMap.size();
         int count = 0;
         for (ShopifyImage image : entry.getValue())
         {
            sb.append(image.getPosition());
            sb.append(":");
            sb.append(image.getId());
            sb.append(",");
            if (image.getAltText() == null)
            {
               sb.append("not set");
            }
            else
            {
               sb.append(image.getAltText().trim().replaceAll(",", "-"));
            }
            if (++count < size)
            {
               sb.append(",");
            }
         }
         sb.append("\n");
      }
      Utils.write(filename, sb.toString());
   }

   private static Map<String, List<ShopifyImage>> buildImageMap() throws Exception
   {
      Map<String, List<ShopifyImage>> imageMap = new TreeMap<>();
      ShopifyClient shopifyClient = new ShopifyClient();
      Map<Long, String> id2Title = shopifyClient.getId2Title();
      int count = 0;
      for (Long id : id2Title.keySet())
      {
         Product product = shopifyClient.getProduct(id, "title,images");
         LOGGER.info("count={} of {} for product={}", ++count, id2Title.size(), product.getTitle());
         ShopifyImage[] images = product.getImages();
         List<ShopifyImage> imageList = new ArrayList<>();
         imageMap.put(product.getTitle(), imageList);
         for (ShopifyImage image : images)
         {
            long imageId = image.getId();
            MetafieldList imageSeoData = shopifyClient.getImageMetafields(imageId);
            List<Metafield> metafields = imageSeoData.getMetafields();
            for (Metafield metafield : metafields)
            {
               if ("alt".equals(metafield.getKey()))
               {
                  image.setAltText(metafield.getValue());
               }
            }
            imageList.add(image);
            Utils.sleep(1000L);
         }
      }
      return imageMap;
   }
}
