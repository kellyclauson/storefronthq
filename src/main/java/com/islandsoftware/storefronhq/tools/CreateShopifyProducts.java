package com.islandsoftware.storefronhq.tools;

import com.islandsoftware.storefronhq.StoreSync;
import com.islandsoftware.storefronhq.etsy.EtsyClient;
import com.islandsoftware.storefronhq.etsy.model.EtsyImage;
import com.islandsoftware.storefronhq.etsy.model.ListingsResponse;
import com.islandsoftware.storefronhq.etsy.model.ListingsResult;
import com.islandsoftware.storefronhq.etsy.model.inventory.InventoryProduct;
import com.islandsoftware.storefronhq.etsy.model.inventory.InventoryResponse;
import com.islandsoftware.storefronhq.shopify.sync.model.Product;
import com.islandsoftware.storefronhq.GoogleSheets;
import com.islandsoftware.storefronhq.SMSClient;
import com.islandsoftware.storefronhq.shopify.sync.model.ShopifyImage;
import com.islandsoftware.storefronhq.shopify.sync.model.ShopifyOption;
import com.islandsoftware.storefronhq.shopify.sync.model.Variant;
import com.islandsoftware.storefronhq.orderprocessing.ProductInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class CreateShopifyProducts
{
   private static final Logger LOGGER = LoggerFactory.getLogger(CreateShopifyProducts.class);

   public static void main(String[] args)
   {
      try
      {
         /*
         ShopifyClient shopifyClient = new ShopifyClient();
         EtsyClient spindleEtsyClient = new SpindleAndRoseEtsyClient();
         StoreSync storeSync = new StoreSync();
         storeSync.initialize(shopifyClient, spindleEtsyClient);
         createProducts(storeSync);
         */
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   public static void createProducts(StoreSync storeSync)
   {
      LOGGER.info("createProducts: begin");
      List<ListingsResult> listings = storeSync.etsyListingsNotInShopify();
      LOGGER.info("createProducts: found {} etsy listings not in shopify", listings.size());
      int numberOfTitleTagsBefore = storeSync.getTitleTagSet().size();
      try
      {
         Map<String, Keyword> queryVolume = GoogleSheets.readQueryVolume();
         for (ListingsResult listing : listings)
         {
            String etsyTitle = listing.getTitle().trim();
            try
            {
               if (etsyTitle.startsWith("Custom Listing") || etsyTitle.startsWith("Custom Order") || etsyTitle.startsWith("Last Piece") || etsyTitle.startsWith("Gift Certificate") || etsyTitle.toLowerCase().contains("thread"))
               {
                  LOGGER.info("createProducts: {} is a custom listing in etsy or a gift certificate, OR thread, skipping...", etsyTitle);
                  continue;
               }
               ProductInfo productInfo = Utils.getFromMasterMap(etsyTitle, storeSync.getMasterProductMapByEtsyTitle());
               if (productInfo == null)
               {
                  LOGGER.error("Could not find new product in Master Product List {}", etsyTitle);
                  SMSClient.alertAll("FAILURE! Cannot create Shopify product because " + etsyTitle + " was not found in Master Product List");
                  continue;
               }

               EtsyClient etsyClient = storeSync.getEtsyClientForListingId(listing.getListingId());
               InventoryResponse inventory = etsyClient.getInventory(listing.getListingId());
               InventoryProduct baseVariation = Utils.getBaseProduct(etsyTitle, inventory);
               if (baseVariation.getInventoryOfferings()[0].getQuantity() < 1)
               {
                  LOGGER.info("The base quantity for {} on Etsy is 0, not creating product on Shopify", etsyTitle);
                  continue;
               }

               String shopifyTitle = productInfo.getShopifyTitle();
               Long shopifyProductId = storeSync.getShopifyIdForShopifyTitle(shopifyTitle);
               if (shopifyProductId != null)
               {
                  LOGGER.info("createProducts: {} already exists in shopify with productId {}, skipping...", shopifyTitle, shopifyProductId);
                  continue;
               }

               Seo seo = SeoCreator.createSeo(etsyTitle, shopifyTitle, productInfo.getVendor(), queryVolume, false, storeSync.getTitleTagSet());
               if (!seo.getTitleTag().equals(SeoCreator.DEFAULT_TITLE_TAG))
               {
                  storeSync.getTitleTagSet().add(seo.getTitleTag().getTitleTag());
                  storeSync.writeTitleTags();
               }

               LOGGER.info("createProducts: calling Etsy.getListing with Images");
               ListingsResponse response = etsyClient.getListing(listing.getListingId(), "Images");
               LOGGER.info("createProducts: received response from Etsy.getListing with Images");
               String productType = "Fabric";
               if (response.getResults()[0].getTitle().contains("Panel"))
               {
                  productType = "Panel";
               }
               else if (response.getResults()[0].getTitle().contains("Ribbon"))
               {
                  productType = "Ribbon";
               }
               else if (response.getResults()[0].getTitle().contains("Button"))
               {
                  productType = "Button";
               }
               else if (response.getResults()[0].getTitle().contains("Gift Certificate"))
               {
                  productType = "Gift Certificate";
               }
               else if (response.getResults()[0].getTitle().contains("Thread"))
               {
                  productType = "Thread";
               }

               LOGGER.info("createProducts: will create a Shopify product for Etsy listingId={}, productType={}, title={}", listing.getListingId(), productType, shopifyTitle);

               Product product = new Product();
               product.setProductType(productType);
               product.setTitle(shopifyTitle);
               product.setHandle(Utils.generateHandle(shopifyTitle, productType));
               product.setVendor(productInfo.getVendor());
               String tags = productInfo.getTags();
               if (!StringUtils.isEmpty(tags) && !tags.equals("not set"))
               {
                  tags = tags.replaceAll("\\|", ",");
                  String invalidTags = ShopifyTags.validate(tags);
                  if (invalidTags != null)
                  {
                     SMSClient.alertAll("FAILURE! Invalid tags: " + invalidTags + ". Cannot create Shopify product for " + shopifyTitle + " due to invalid tags!");
                     continue;
                  }
               }
               tags = ShopifyTags.augmentTags(tags, shopifyTitle, storeSync.getMasterProductMapByShopifyTitle().values());
               product.setTags(tags);

               double itemWeight = Double.valueOf(response.getResults()[0].getItemWeight());

               String description = response.getResults()[0].getDescription();
               description = formatDescription(description);
               description = UpdateShopifyDescriptions.replaceHeaders(description, seo);
               product.setBodyHtml(description);
               product.setMetaFieldsGlobalTitleTag(seo.getTitleTag().getTitleTag());
               product.setMetaFieldsGlobalDescriptionTag(productInfo.getMetaDescription().replaceAll("-", ","));
               EtsyImage[] etsyImages = response.getResults()[0].getImages();
               List<ShopifyImage> shopifyImages = new ArrayList<>(etsyImages.length);
               int imageCount = 0;
               for (EtsyImage etsyImage : etsyImages)
               {
                  imageCount++;
                  ShopifyImage image = new ShopifyImage();
                  image.setPosition(imageCount);
                  image.setAltText(productInfo.getImageAltText() + " - view " + imageCount);
                  image.setSrc(etsyImage.getUrl());
                  shopifyImages.add(image);
               }
               product.setImages(shopifyImages.toArray(new ShopifyImage[0]));

               List<String> optionValues = new ArrayList<>();
               List<Variant> variants = new ArrayList<>();
               if (productType.equals("Fabric") && Utils.isHalfAndWholeYardOnly(inventory))
               {
                  // create all variations
                  LOGGER.info("This listing has half yard and one yard variations only on Etsy, creating all variations for Shopify product");
                  variants.addAll(Utils.createShopifyVariantsFromOneYardEtsyProduct(shopifyTitle, itemWeight, baseVariation));
                  for (Variant variant : variants)
                  {
                     optionValues.add(variant.getTitle());
                  }
               }
               else
               {
                  // create only the variations present on etsy
                  LOGGER.info("This listing is not half yard and one yard only on Etsy, will create the same variations for Shopify product as found on Etsy");
                  variants.addAll(Utils.createShopifyVariantsFromEtsyProducts(shopifyTitle, itemWeight, inventory.getInventoryResults().getProducts()));
                  for (Variant variant : variants)
                  {
                     optionValues.add(variant.getTitle());
                  }
               }
               ShopifyOption shopifyOption = new ShopifyOption();
               shopifyOption.setName("Length");
               shopifyOption.setValues(optionValues.toArray(new String[0]));
               product.setOptions(new ShopifyOption[]{shopifyOption});
               product.setVariants(Utils.reorder(variants));

            /*
               LOGGER.info("createProducts: {} does not contain variations", response.getResults()[0].getEtsyTitle());
               Variant variant = new Variant();
               variant.setEtsyTitle("Default Title");
               variant.setPrice(String.valueOf(product.getPrice()));
               variant.setInventoryManagement("shopify");
               variant.setInventoryQuantity(product.getInventory());
               variant.setWeight(String.valueOf(product.getWeight()));
               product.setVariants(Arrays.asList(variant));
            */

               long productId = storeSync.getShopifyClient().createProduct(product);
               LOGGER.info("created product {} productId={}", product.getTitle(), productId);
               SMSClient.alertAll("Created new shopify product: " + product.getTitle());
               if (productId > 0)
               {
                  String keywords = Utils.formatKeywords(seo.getKeywords(), ',');
                  LOGGER.info("{} keywords={}", product.getTitle(), keywords);
                  storeSync.getShopifyClient().setKeywords(productId, "products", keywords);
               }
            }
            catch (Exception e)
            {
               LOGGER.error("error", e);
               SMSClient.alertAdmin("Error creating new shopify product: " + e.getMessage() + " for etsy title: " + etsyTitle);
            }
         }
      }
      catch (Exception e)
      {
         LOGGER.error("createProducts", e);
         SMSClient.alertAdmin("Create products cannot proceed: " + e.getMessage());
      }

      storeSync.update();
      int numberOfTitleTagsAfter = storeSync.getTitleTagSet().size();
      if (numberOfTitleTagsAfter > numberOfTitleTagsBefore)
      {
         Utils.toFile(storeSync.getTitleTagSet(), StoreSync.TITLE_TAG_FILENAME);
      }
      LOGGER.info("createProducts: end");
   }

   /*
   private static void addShopifyUrlToEtsyListings(StoreSync storeSync, Map<Long, String> listingId2Handle)
   {
      LOGGER.info("addShopifyUrlToEtsyListings: etsy listingId to shopify handle map for new products {}", listingId2Handle);
      for (Map.Entry<Long, String> entry : listingId2Handle.entrySet())
      {
         EtsyClient etsyClient = storeSync.getSpindleEtsyClient();
         ListingsResponse listing = etsyClient.getListing(entry.getKey(), null);
         String description = extractDescription(listing);
         if (description == null)
         {
            SMSClient.alertAdmin("Could not obtain description for etsy listing with id " + entry.getKey() + " shopify handle is " + entry.getValue());
         }
         else
         {
            String formattedDescription = formatDescription(description, entry.getValue());
            Map<String, String> params = new HashMap<>(1);
            params.put("description", formattedDescription);
            etsyClient.updateListing(entry.getKey(), params);
         }
      }
   }
   */

   private static String extractDescription(ListingsResponse listing)
   {
      String description = null;
      if (listing != null)
      {
         ListingsResult[] results = listing.getResults();
         if (results != null && results.length > 0)
         {
            description = results[0].getDescription();
         }
      }
      return description;
   }

   private static String formatDescription(String description)
   {
      LOGGER.info("incoming description\n{}", description);
      String updated = description.replaceAll("##.*\n", "");
      int i = updated.indexOf("~~~~~");
      if (i > 0)
      {
         updated = updated.substring(0, i - 1);
      }
      i = updated.indexOf("*Care");
      if (i > 0)
      {
         updated = updated.substring(0, i - 1);
      }
      i = updated.indexOf("*Wash");
      if (i > 0)
      {
         updated = updated.substring(0, i - 1);
      }

      StringBuilder sb = new StringBuilder();
      String[] split = updated.split("\n");
      int count = 0;
      int bulletCount = 0;
      for (String s : split)
      {
         count++;
         if (!s.trim().isEmpty())
         {
            if (count == 1)
            {
               String[] firstLine = s.split("-");
               sb.append("<h2><span style=\"color: #64aacb;\">").append(firstLine[0].trim()).append("</span></h2>").append("\n");
               if (firstLine.length > 1)
               {
                  sb.append("<h3><span style=\"color: #f5989d;\">").append(firstLine[1].trim()).append("</span></h3>").append("\n");
               }
               if (firstLine.length > 2)
               {
                  sb.append("<h4><span style=\"color: #b4a7d6;\">").append(firstLine[2].trim()).append("</span></h4>").append("\n");
               }
            }
            else if (s.startsWith("-"))
            {
               bulletCount++;
               if (bulletCount == 1)
               {
                  sb.append("<ul>").append("\n");
               }
               sb.append("<li>").append(s.substring(1).trim()).append("\n");
            }
            else
            {
               sb.append("<p>").append(s).append("</p>").append("\n");
            }
         }
      }
      if (bulletCount > 0)
      {
         sb.append("</ul>");
      }
      LOGGER.info("updated description\n{}", sb.toString());
      return sb.toString();
   }

   private static String formatDescription(String description, String handle)
   {
      String formatted = description.replace(PATTERN, REPLACEMENT);
      return formatted.replace("HANDLE", handle);
   }

   private static final String PATTERN = "## We offer this fabric by the 1/2 yard. To order 1 yard, select a quantity of 2 and so on. I will cut your fabric in one continuous piece.\n";
   private static final String REPLACEMENT = "## We offer this fabric by the 1/2 yard. To order 1 yard, select a quantity of 2 and so on. I will cut your fabric in one continuous piece.\n" +
         "\n" +
         "## Please visit www.spindleandrose.com for free shipping on US orders of $50 or more AND to find additional cuts of fabric including 1/4 yard, 1/2 yard, 3/4 yard, 1 yard, and Fat Quarters!\n" +
         "\n" +
         "## Find this product with all of the options described above at:\n" +
         "www.spindleandrose.com/products/HANDLE\n";
}
