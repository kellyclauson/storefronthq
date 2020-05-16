package com.islandsoftware.storefronhq.tools;

import com.islandsoftware.storefronhq.etsy.EtsyClient;
import com.islandsoftware.storefronhq.etsy.ImaginationFabricClient;
import com.islandsoftware.storefronhq.etsy.SpindleAndRoseEtsyClient;
import com.islandsoftware.storefronhq.etsy.model.EtsyImage;
import com.islandsoftware.storefronhq.etsy.model.EtsyImageResponse;
import com.islandsoftware.storefronhq.etsy.model.ListingsResponse;
import com.islandsoftware.storefronhq.etsy.model.ListingsResult;
import com.islandsoftware.storefronhq.etsy.model.inventory.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class ImaginationFabricListings
{
   private static final Logger LOGGER = LoggerFactory.getLogger(ImaginationFabricListings.class);

   private EtsyClient sClient;
   private EtsyClient iClient;
   private Map<String, String> title2section;
   private Map<String, Long> sectionMap;
   private Map<String, Long> title2sectionIdMap;

   public ImaginationFabricListings() throws Exception
   {
      sClient = new SpindleAndRoseEtsyClient();
      iClient = new ImaginationFabricClient();
      //title2section = getTitle2SectionMap("C:\\tmp\\etsyTitlesAndSections-test.csv");
      //sectionMap = iClient.getShopSectionMap();
      //title2sectionIdMap = getTitle2SectionIdMap(title2section, sectionMap);
   }

   public static void main(String[] args)
   {
      try
      {
         //shopifyProductIdsNotInEtsy();
         ImaginationFabricListings main = new ImaginationFabricListings();
         main.doit();
         //main.findUserId();
         //main.createListings();
         //main.uploadImages();
         //main.addInventory();
         //main.activate();
      }
      catch (Exception e)
      {
         LOGGER.error("error", e);
      }
   }

   private void findUserId()
   {
      Map<Long, String> id2Title = sClient.getId2Title();
      for (Map.Entry<Long, String> entry : id2Title.entrySet())
      {
         ListingsResponse listing = sClient.getListing(entry.getKey(), "user_id");
         LOGGER.info("SpindleAndRose: userId={} title={}", listing.getResults()[0].getUserId(), entry.getValue());
         break;
      }
      id2Title = iClient.getId2Title();
      for (Map.Entry<Long, String> entry : id2Title.entrySet())
      {
         ListingsResponse listing = iClient.getListing(entry.getKey(), "user_id");
         LOGGER.info("ImaginationFabric: userId={} title={}", listing.getResults()[0].getUserId(), entry.getValue());
         break;
      }
   }

   private void doit() throws Exception
   {
      List<String> moving = new ArrayList<>();
      //List<String> noVariations = new ArrayList<>();
      long spindleShippingTemplateId = sClient.getShippingTemplateId();
      Map<String, Long> spindleTitle2Id = sClient.getTitle2Id();
      //Map<Long, String> imagineId2Title = iClient.getId2Title();
      int count = 0;
      int successCount = 0;
      for (Map.Entry<String, Long> entry : spindleTitle2Id.entrySet())
      {
         Long id = entry.getValue();
         String title = entry.getKey().trim();
         Utils.sleep(500L);
         ListingsResponse listing = sClient.getListing(id, "shipping_template_id");
         if (listing == null)
         {
            throw new Exception("could not find " + title);
         }
         Long shippingTemplateId = listing.getResults()[0].getShippingTemplateId();
         if (shippingTemplateId == null)
         {
            //throw new Exception("could not find shipping template for  " + title);
            LOGGER.info("could not find shipping template for  " + title);
         }
         else if (shippingTemplateId != spindleShippingTemplateId)
         {
            Utils.sleep(500L);
            LOGGER.info("moving to paid shipping {}", title);
            sClient.updateShippingTemplateId(id, spindleShippingTemplateId);
            moving.add(title);
         }
      }
      LOGGER.info("moved {} titles to paid shipping", moving.size());
      for (String s : moving)
      {
         LOGGER.info("moved to paid shipping {}", s);
      }
   }

   private Map<String, String> getParams(String state)
   {
      Map<String, String> params = new HashMap<>(1);
      params.put("state", state);
      return params;
   }

   /*
   private static void shopifyProductIdsNotInEtsy()
   {
      try
      {
         ShopifyClient shopifyClient = new ShopifyClient();
         Map<Long, String> shopifyId2Title = shopifyClient.getId2Title();

         Map<String, ProductInfo> masterProductMapByEtsyTitle = GoogleSheets.readProductInfo();
         Map<String, ProductInfo> masterProductMapByShopifyTitle = Utils.createShopifyTitle2ProductInfoMapFromEtsyTitle2ProductInfoMap(masterProductMapByEtsyTitle);

         EtsyClient etsyClient = new SpindleAndRoseEtsyClient();
         Map<String, Long> etsyTitle2ListingId = etsyClient.getTitle2Id();

         List<Long> notInEtsy = new ArrayList<>();
         for (Map.Entry<Long, String> entry : shopifyId2Title.entrySet())
         {
            String shopifyTitle = entry.getValue();
            ProductInfo pi = Utils.getFromMasterMap(shopifyTitle, masterProductMapByShopifyTitle);
            if (pi != null)
            {
               String etsyTitle = pi.getEtsyTitle().trim();
               if (!etsyTitle2ListingId.containsKey(etsyTitle))
               {
                  LOGGER.info("etsy title {} not in etsyTitle2ListingId map, maps to shopify title={}", etsyTitle, shopifyTitle);
                  notInEtsy.add(entry.getKey());
               }
            }
            else
            {
               String msg = "Shopify title " + shopifyTitle + " is in shopifyId2Title map but not in masterProductMapByShopifyTitle";
               LOGGER.error(msg);
            }
         }

         for (Map.Entry<String, Long> entry : etsyTitle2ListingId.entrySet())
         {
            LOGGER.info("{} {}", entry.getKey(), entry.getValue());
         }

         for (Long id : notInEtsy)
         {
            LOGGER.info("{}", id);
         }
      }
      catch (Exception e)
      {
         LOGGER.error("notInEtsy", e);
      }
   }
   */

   private static String getExtension(String filename)
   {
      int i = filename.lastIndexOf(".");
      return filename.substring(i + 1, i + 4);
   }

   private void activate() throws Exception
   {
      int count = 0;
      Map<String, ListingsResult> listings = iClient.listings("draft", null);
      for (Map.Entry<String, ListingsResult> entry : listings.entrySet())
      {
         String title = entry.getKey().trim();
         LOGGER.info("activating {} of {} {}", ++count, listings.size(), title);
         Map<String, String> params = new HashMap<>(2);
         params.put("state", "active");
         params.put("should_auto_renew", "true");
         iClient.updateListing(entry.getValue().getListingId(), params);
         Utils.sleep(600L);
      }
   }

   private void addInventory() throws Exception
   {
      int count = 0;
      Map<String, ListingsResult> listings = iClient.listings("draft", null);
      Map<String, Long> titleToListingIdMap = sClient.getTitle2Id();
      for (Map.Entry<String, ListingsResult> entry : listings.entrySet())
      {
         String title = entry.getKey().trim();
         LOGGER.info("processing imagination fabric {} of {}", ++count, listings.size());
         Long id = titleToListingIdMap.get(title);
         if (id == null)
         {
            throw new Exception("could not find spindle+rose listing id for " + title);
         }
         LOGGER.info("getting inventory, listing {}", title);
         InventoryResponse inventory = sClient.getInventory(id);
         InventoryResults inventoryResults = inventory.getInventoryResults();
         InventoryProduct[] products = inventoryResults.getProducts();
         if (products[0].getPropertyValues() == null || products[0].getPropertyValues().length == 0)
         {
            ListingsResponse listing = sClient.getListing(id, null);
            ListingsResult result = listing.getResults()[0];
            Integer quantity = result.getQuantity();
            String price = result.getPrice();
            double dprice = Double.valueOf(price);
            dprice = dprice - 1.0;
            Map<String, String> params = new HashMap<>(2);
            params.put("quantity", String.valueOf(quantity));
            params.put("price", String.valueOf(dprice));
            iClient.updateListing(entry.getValue().getListingId(), params);
         }
         else
         {
            for (InventoryProduct product : products)
            {
               String value = product.getPropertyValues()[0].getValues()[0];
               LOGGER.info("product value={}", value);

               InventoryOffering[] inventoryOfferings = product.getInventoryOfferings();
               for (InventoryOffering inventoryOffering : inventoryOfferings)
               {
                  InventoryOfferingPrice inventoryOfferingPrice = inventoryOffering.getInventoryOfferingPrice();
                  Integer amount = inventoryOfferingPrice.getAmount();
                  LOGGER.info("product amount={}", amount);
                  if (value != null && value.startsWith("1/2"))
                  {
                     inventoryOfferingPrice.setAmount(amount - 50);
                     LOGGER.info("updated product amount={}", inventoryOfferingPrice.getAmount());
                  }
                  else
                  {
                     inventoryOfferingPrice.setAmount(amount - 100);
                     LOGGER.info("updated product amount={}", inventoryOfferingPrice.getAmount());
                  }
                  int updatedAmount = inventoryOfferingPrice.getAmount();
                  String[] formattedPrices = Utils.createFormattedPrices(updatedAmount);
                  inventoryOfferingPrice.setCurrencyFormattedRaw(formattedPrices[0]);
                  inventoryOfferingPrice.setCurrencyFormattedShort(formattedPrices[1]);
                  inventoryOfferingPrice.setCurrencyFormattedLong(formattedPrices[2]);
               }
            }
            String updatedProductJson = Utils.toJson(products);
            LOGGER.debug("updated inventory:\n{}", updatedProductJson);
            iClient.updateInventory(entry.getValue().getListingId(), updatedProductJson);

         }
         Utils.sleep(1000L);
      }
   }


   private void uploadImages() throws Exception
   {
      int count = 0;
      Map<String, ListingsResult> listings = iClient.listings("draft", null);
      Map<String, Long> titleToListingIdMap = sClient.getTitle2Id();
      for (Map.Entry<String, ListingsResult> entry : listings.entrySet())
      {
         String title = entry.getKey().trim();
         LOGGER.info("processing imagination fabric {} of {}", ++count, listings.size());
         Long id = titleToListingIdMap.get(title);
         if (id == null)
         {
            throw new Exception("could not find spindle+rose listing id for " + title);
         }
         LOGGER.info("getting images, listing {}", title);
         EtsyImageResponse response = sClient.getImages(id);
         EtsyImage[] images = response.getResults();
         for (EtsyImage image : images)
         {
            int rank = image.getRank();
            String extension = getExtension(image.getUrl());
            String filename = "C:\\tmp\\images\\image" + rank + "." + extension;
            storeImage(image.getUrl(), filename);
            iClient.uploadImage(entry.getValue().getListingId(), rank, filename, extension);
            Utils.sleep(1000L);
         }
      }
   }

   private static void storeImage(String fromLocation, String filename) throws Exception
   {
      URL url = new URL(fromLocation);
      ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
      FileOutputStream fileOutputStream = new FileOutputStream(filename);
      FileChannel fileChannel = fileOutputStream.getChannel();
      fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
   }

   private String findSectionNameById(Long id)
   {
      for (Map.Entry<String, Long> entry : sectionMap.entrySet())
      {
         if (entry.getValue().equals(id))
         {
            return entry.getKey();
         }

      }
      return "Not Found";
   }

   private void createListings() throws Exception
   {
      int count = 0;
      Map<String, ListingsResult> listings = sClient.listings();
      for (Map.Entry<String, ListingsResult> entry : listings.entrySet())
      {
         String title = entry.getKey().trim();
         LOGGER.info("processing spindle+rose listing {} of {}", ++count, listings.size());
         if (title2section.keySet().contains(title))
         {
            LOGGER.info("adding to imagination fabric, listing {}", title);
            Long sectionId = title2sectionIdMap.get(title);
            if (sectionId == null)
            {
               sectionId = 0L;
            }
            LOGGER.info("adding to section {}", findSectionNameById(sectionId));
            ListingsResult listing = entry.getValue();
            iClient.createListing(1, title, toString(listing.getTags()), listing.getDescription(), 1.00, toString(listing.getMaterials(), true), sectionId, listing.getCategoryId(), listing.getTaxonomyId());
            Utils.sleep(1000L);
         }
      }
   }

   private Map<String, String> getTitle2SectionMap(String filename) throws Exception
   {
      Map<String, String> map = new HashMap<>();
      List<String> lines = Files.readAllLines(Paths.get(filename));
      int count = 0;
      for (String line : lines)
      {
         if (++count == 1)
         {
            continue;
         }
         String[] split = line.split(",");
         String title = split[0].trim();
         String section = split[1].trim();
         map.put(title, section);
      }
      return map;
   }

   private Map<String, Long> getTitle2SectionIdMap(Map<String, String> title2section, Map<String, Long> sectionName2Id) throws Exception
   {
      Map<String, Long> title2sectionId = new HashMap<>();
      for (Map.Entry<String, String> entry : title2section.entrySet())
      {
         Long id = sectionName2Id.get(entry.getValue());
         if (id == null)
         {
            throw new Exception(entry.getValue() + " not found in section name to id map");
         }
         title2sectionId.put(entry.getKey().trim(), id);
      }
      return title2sectionId;
   }

   private String toString(String[] array)
   {
      return toString(array, false);
   }

   private String toString(String[] array, boolean removeMultipleWords)
   {
      String list = "";
      int count = 0;
      for (String s : array)
      {
         if (removeMultipleWords)
         {
            if (s.contains(" "))
            {
               continue;
            }
         }
         if (++count == 1)
         {
            list = s;
         }
         else
         {
            list = list + "," + s;
         }
      }
      return list;
   }


}
