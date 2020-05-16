package com.islandsoftware.storefronhq;

import com.islandsoftware.storefronhq.tools.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class SkuManager
{
   private static final Logger LOGGER = LoggerFactory.getLogger(SkuManager.class);

   private static Map<String, String> vendor2Sku = new HashMap<>();
   static
   {
      vendor2Sku.put(Constants.VENDOR_ALEXANDER_HENRY, Constants.SKU_ALEXANDER_HENRY);
      vendor2Sku.put(Constants.VENDOR_ALI_EXPRESS, Constants.SKU_ALI_EXPRESS);
      vendor2Sku.put(Constants.VENDOR_ANDOVER, Constants.SKU_ANDOVER);
      vendor2Sku.put(Constants.VENDOR_ART_GALLERY, Constants.SKU_ART_GALLERY);
      vendor2Sku.put(Constants.VENDOR_BIRCH, Constants.SKU_BIRCH);
      vendor2Sku.put(Constants.VENDOR_BLEND, Constants.SKU_BLEND);
      vendor2Sku.put(Constants.VENDOR_CLOUD9, Constants.SKU_CLOUD9);
      vendor2Sku.put(Constants.VENDOR_COTTON_AND_STEEL, Constants.SKU_COTTON_AND_STEEL);
      vendor2Sku.put(Constants.VENDOR_EE_SCHENCK, Constants.SKU_EE_SCHENCK);
      vendor2Sku.put(Constants.VENDOR_ELLA_BLUE, Constants.SKU_ELLA_BLUE);
      vendor2Sku.put(Constants.VENDOR_FIBERACTIVE, Constants.SKU_FIBERACTIVE);
      vendor2Sku.put(Constants.VENDOR_KOKKA, Constants.SKU_KOKKA);
      vendor2Sku.put(Constants.VENDOR_LEWIS_AND_IRENE, Constants.SKU_LEWIS_AND_IRENE);
      vendor2Sku.put(Constants.VENDOR_MODA, Constants.SKU_MODA);
      vendor2Sku.put(Constants.VENDOR_MONALUNA, Constants.SKU_MONALUNA);
      vendor2Sku.put(Constants.VENDOR_RENAISSANCE, Constants.SKU_RENAISSANCE);
      vendor2Sku.put(Constants.VENDOR_SEVEN_ISLANDS, Constants.SKU_SEVEN_ISLANDS);
      vendor2Sku.put(Constants.VENDOR_SPINDLE_AND_ROSE, Constants.SKU_SPINDLE_AND_ROSE);
      vendor2Sku.put(Constants.VENDOR_SUZY, Constants.SKU_SUZY);
   }

   public static void main(String[] args)
   {
      try
      {
         //setSkus();
      }
      catch (Exception e)
      {
         LOGGER.error("SkuManager", e);
      }
   }

   /*
   public static void setSkus() throws Exception
   {
      List<Sku> skus = new ArrayList<>();
      Map<String, Integer> skuCountMap = new HashMap<>();
      ShopifyClient shopifyClient = new ShopifyClient();
      Map<Long, String> products = shopifyClient.getId2Title();
      EtsyClient etsyClient = new SpindleAndRoseEtsyClient();
      Map<String, Long> etsyTitleToListingIdMap = etsyClient.getTitle2Id();
      Map<String, ProductInfo> productInfoMap = GoogleSheets.readProductInfo(false);
      Map<String, ProductInfo> shopifyTitle2ProductInfoMap = Utils.createShopifyTitle2ProductInfoMapFromEtsyTitle2ProductInfoMap(productInfoMap);
      int count = 0;
      for (Long id : products.keySet())
      {
         LOGGER.info("{} of {} title={}", ++count, products.size(), products.get(id));
         Product product = shopifyClient.getProduct(id);
         String vendor = product.getVendor();
         String skuPrefix = vendor2Sku.get(vendor);
         if (skuPrefix == null)
         {
            throw new Exception(vendor + " is not a recognized vendor, title=" + product.getTitle());
         }
         Integer skuCount = skuCountMap.get(skuPrefix);
         if (skuCount == null)
         {
            skuCountMap.put(skuPrefix, 1000);
         }
         else
         {
            skuCountMap.put(skuPrefix, skuCount + 1);
         }

         ProductInfo productInfo = Utils.getFromMasterMap(product.getTitle(), shopifyTitle2ProductInfoMap);
         if (productInfo == null)
         {
            throw new Exception("Could not find productInfo for shopify title " + product.getTitle());
         }
         String etsyTitle = productInfo.getEtsyTitle();
         Long etsyId = etsyTitleToListingIdMap.get(etsyTitle.trim());
         if (etsyId == null)
         {
            throw new Exception("Could not find etsyId for etsy title " + etsyTitle);
            //LOGGER.error("COULD NOT FIND ETSY ID FOR ETSY TITLE " + etsyTitle);
         }

         Sku sku = new Sku(skuPrefix + "-" + skuCountMap.get(skuPrefix));
         sku.setShopifyId(id);
         sku.setShopifyTitle(product.getTitle());
         sku.setEtsyTitle(etsyTitle);
         sku.setEtsyId(etsyId);
         LOGGER.info("sku={}", sku);
         skus.add(sku);
         Utils.sleep(500L);


         List<Variant> variants = product.getVariants();
         if (variants != null && variants.size() > 0)
         {
            for (Variant variant : variants)
            {
               variant.setSku(sku + ":" + variant.getTitle());
            }
            client.updateProduct(product);
         }
         else
         {
            LOGGER.warn("No variants found, cannot set sku for {}", product.getTitle());
         }
      }
      LOGGER.info("Number of skus={}", skus.size());
      toFile(skus);
   }
   */

   private static void toFile(List<Sku> skus)
   {
      StringBuilder sb = new StringBuilder();
      sb.append("Sku,ShopifyId,ShopifyTitle,EtsyId,EtsyTitle\n");
      for (Sku sku : skus)
      {
         sb.append(sku.getSku()).append(",").append(sku.getShopifyId()).append(",").append(sku.getShopifyTitle()).append(",").append(sku.getEtsyId()).append(",").append(sku.getEtsyTitle()).append("\n");
      }
      Utils.write("C:\\tmp\\skus.csv", sb.toString());
   }

}
