package com.islandsoftware.storefronhq;

import com.islandsoftware.storefronhq.etsy.EtsyClient;
import com.islandsoftware.storefronhq.etsy.model.*;
import com.islandsoftware.storefronhq.etsy.model.inventory.InventoryProduct;
import com.islandsoftware.storefronhq.etsy.model.inventory.InventoryResponse;
import com.islandsoftware.storefronhq.etsy.model.inventory.InventoryResults;
import com.islandsoftware.storefronhq.orderprocessing.ProductInfo;
import com.islandsoftware.storefronhq.shopify.sync.ShopifyClient;
import com.islandsoftware.storefronhq.stats.OrderItem;
import com.islandsoftware.storefronhq.stats.OrderStats;
import com.islandsoftware.storefronhq.tools.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class EtsyReceiptPoller implements Runnable
{
   private EtsyClient etsyClient;
   private ShopifyClient shopifyClient;
   private String etsyStoreName;
   private StoreSync storeSync;
   private long lastSuccess;

   private static final String LAST_SUCCESS_FILE = "/home/pi/spindleandrose/sync/data/lastsuccess";
   private static final Logger LOGGER = LoggerFactory.getLogger(EtsyReceiptPoller.class);

   public EtsyReceiptPoller(EtsyClient etsyClient, String etsyStoreName, ShopifyClient shopifyClient, StoreSync storeSync)
   {
      this.etsyClient = etsyClient;
      this.shopifyClient = shopifyClient;
      this.etsyStoreName = etsyStoreName;
      this.storeSync = storeSync;
      setLastSuccess();
   }

   public void run()
   {
      long since = lastSuccess / 1000;
      LOGGER.debug("run: [{}] Looking for receipts since {}", etsyStoreName, since);
      try
      {
         ReceiptsResponse receipts = etsyClient.receipts(since);
         if (receipts != null)
         {
            lastSuccess = System.currentTimeMillis();
            writeLastSuccess();
            int count = receipts.getCount();
            if (count > 0)
            {
               LOGGER.info("run: [{}] found {} new receipts from Etsy", etsyStoreName, count);
            }
            else
            {
               LOGGER.debug("run: [{}] found {} new receipts from Etsy", etsyStoreName, count);
            }
            ReceiptResult[] results = receipts.getResults();
            if (results != null)
            {
               for (ReceiptResult result : results)
               {
                  String orderValue = result.getTotalPrice();
                  String chargeForShipping = result.getTotalShippingCost();
                  int countryId = result.getCountryId();
                  String country = etsyClient.getCountryId2Country().get(countryId).getName();
                  LOGGER.info("run: [{}] orderId={} totalPrice={} totalShippingCost={} country={}", etsyStoreName, result.getReceiptId(), orderValue, chargeForShipping, countryId);
                  List<OrderItem> orderItems = new ArrayList<>();
                  Transaction[] transactions = result.getTransactions();
                  if (transactions != null)
                  {
                     LOGGER.info("run: [{}] number of transactions: {}", etsyStoreName, transactions.length);
                     int transactionCount = 0;
                     double totalCost = 0.0;
                     double totalShippingCost = 0.0;
                     int unitsSold = 0;
                     double weightInOunces = 0.0;
                     for (Transaction transaction : transactions)
                     {
                        transactionCount++;
                        LOGGER.info("[{}] Begin Transaction {}", etsyStoreName, transactionCount);

                        OrderItem orderItem = new OrderItem();
                        String title = transaction.getTitle().trim();
                        orderItem.setTitle(title);
                        if (title.toLowerCase().startsWith("custom listing") || title.toLowerCase().startsWith("custom order"))
                        {
                           SMSClient.alertAlexa("Don't forget to adjust inventory for " + title);
                           unitsSold ++;
                           CustomListing customListing = getCustomListing(title);
                           if (customListing == null)
                           {
                              String msg = "Could not get custom listing for title " + title;
                              LOGGER.error(etsyStoreName + ":" + msg);
                              SMSClient.alertAdmin(msg);
                           }
                           else
                           {
                              LOGGER.info("[{}] found custom listing {}", etsyStoreName, customListing);
                              weightInOunces += customListing.getWeight();
                              totalCost += customListing.getOurCost();
                              totalShippingCost += customListing.getCostToShipToUs();
                           }
                           orderItem.setQuantity(1);
                           orderItem.setVariation("Custom");
                           continue;
                        }

                        int purchasedQuantity = transaction.getQuantity();
                        orderItem.setQuantity(purchasedQuantity);
                        unitsSold += purchasedQuantity;
                        String length;
                        LOGGER.info("run: [{}] title={}", etsyStoreName, title);
                        LOGGER.info("run: [{}] purchasedQuantity={}", etsyStoreName, purchasedQuantity);
                        TransactionVariation[] variations = transaction.getVariations();
                        if (variations == null || variations.length == 0)
                        {
                           LOGGER.info("run: [{}] No variation, could be a private listing or type ribbon or thread...", etsyStoreName);
                           // no need to make a correction to etsy but lets see if shopify needs to do anything
                           //updateShopify(title, purchasedQuantity);
                           String key = Utils.createKey(title, "Default Title");
                           ProductInfo productInfo = findInMasterList(key);
                           if (productInfo != null)
                           {
                              totalCost += productInfo.getCost() * purchasedQuantity;
                              totalShippingCost += productInfo.getShippingCost() * purchasedQuantity;
                           }
                           Long id = transaction.getListingId();
                           weightInOunces += (getListingWeight(id) * purchasedQuantity);
                        }
                        else
                        {
                           LOGGER.debug("run: [{}] number of variations: {}", etsyStoreName, variations.length);
                           if (variations.length > 1)
                           {
                              LOGGER.error("run: [{}] Unexpected number of variations {}", etsyStoreName, variations.length);
                              SMSClient.alertAdmin("EtsyReceiptPoller.run: Unexpected number of variations " + variations.length + " for title " + title);
                              for (TransactionVariation variation : variations)
                              {
                                 LOGGER.error("run: [{}] unexpected variation value {}", etsyStoreName, variation.getFormattedValue());
                              }
                           }
                           else
                           {
                              length = variations[0].getFormattedValue();
                              LOGGER.info("run: [{}] variation={}", etsyStoreName, length);
                              orderItem.setVariation(length);

                              if (length.startsWith("1/2"))
                              {
                                 weightInOunces += (3.0 * purchasedQuantity);
                              }
                              else
                              {
                                 weightInOunces += (6.0 * purchasedQuantity);
                              }

                              if (title.toLowerCase().contains("last piece"))
                              {
                                 ProductInfo productInfo = findLastPieceInMasterList(title);
                                 if (productInfo != null)
                                 {
                                    totalCost += productInfo.getCost() * purchasedQuantity;
                                    totalShippingCost += productInfo.getShippingCost() * purchasedQuantity;
                                 }
                                 Long id = transaction.getListingId();
                                 weightInOunces += getListingWeight(id);
                              }
                              else if (Utils.isValidVariationTitle(length))
                              {
                                 try
                                 {
                                    updateEtsy(title, length, purchasedQuantity);
                                       /*
                                    Long listingId = storeSync.getEtsyListingIdForEtsyTitle(title, etsyStoreName);
                                    if (listingId != null)
                                    {
                                       InventoryResponse inventory = etsyClient.getInventory(listingId);
                                       int baseQuantity = Utils.getBaseQuantity(inventory);
                                       boolean soldOut = false;
                                       if (!etsyClient.isListingActive(listingId))
                                       {
                                          soldOut = true;
                                       }

                                    }
                                       */
                                    // updateShopify(title, length, purchasedQuantity, soldOut ? 0 : baseQuantity, Utils.isBaseQuantityHalfYard(inventory));
                                    String key = Utils.createKey(title, length);
                                    ProductInfo productInfo = findInMasterList(key);
                                    if (productInfo != null)
                                    {
                                       totalCost += productInfo.getCost() * purchasedQuantity;
                                       totalShippingCost += productInfo.getShippingCost() * purchasedQuantity;
                                    }
                                 }
                                 catch (Exception e)
                                 {
                                    LOGGER.error("run: [{}] error updating inventory", etsyStoreName, e);
                                    SMSClient.alertAdmin("EtsyReceiptPoller.run: error updating inventory: " + e.getMessage());
                                 }
                              }
                              else
                              {
                                 LOGGER.error("run: [{}] Invalid variation title [{}] Cannot update inventory", etsyStoreName, length);
                                 SMSClient.alertAdmin("EtsyReceiptPoller.run: Invalid variation title [" + length + "] Cannot update inventory");
                              }
                           }
                        }
                        LOGGER.info("[{}] End Transaction {} weightInOunces={}", etsyStoreName, transactionCount, weightInOunces);
                        checkEtsyQuantity(transaction.getTitle());
                        orderItem.setOrderId(result.getReceiptId());
                        orderItems.add(orderItem);
                     }
                     OrderStats orderStats = Utils.calculateProfit("Etsy:" + etsyStoreName, result.getReceiptId(), orderValue, chargeForShipping, totalCost, totalShippingCost, weightInOunces, unitsSold, country, orderItems);
                     storeSync.getStatsCollector().collect(orderStats);
                  }
               }
            }
         }
      }
      catch (Exception e)
      {
         LOGGER.error("run: [{}] Error processing Etsy order", etsyStoreName, e);
         SMSClient.alertAdmin("EtsyReceiptPoller.run: " + etsyStoreName + " Error processing Etsy order: " + e.getMessage());
      }
   }

   private CustomListing getCustomListing(String title)
   {
      try
      {
         LOGGER.info("getCustomListing: [{}] title={}", etsyStoreName, title);
         return GoogleSheets.getCustomListingMap().get(title);
      }
      catch (Exception e)
      {
         LOGGER.error("getCustomListing: [{}] error", etsyStoreName, e);
         return null;
      }
   }

   private void checkEtsyQuantity(String title)
   {
      Long listingId = storeSync.getEtsyListingIdForEtsyTitle(title, etsyStoreName);
      if (listingId != null)
      {
         try
         {
            InventoryResponse inventory = etsyClient.getInventory(listingId);
            int quantity = Utils.getBaseQuantity(inventory);
            LOGGER.info("checkEtsyQuantity: [{}] baseQuantity={} for title={}", etsyStoreName, quantity, title);
            if (quantity <= 4)
            {
               SMSClient.alertAlexa("Base quantity of " + title + " is " + quantity);
            }
         }
         catch (Exception e)
         {
            LOGGER.error("checkEtsyQuantity: [{}]", etsyStoreName, e);
            SMSClient.alertAdmin("Error getting base quantity from etsy for " + title);
         }
      }
      else
      {
         LOGGER.warn("checkEtsyQuantity: [{}] could not find listingId for title [{}]", etsyStoreName, title);
      }
   }

   private boolean updateEtsy(String title, String purchasedVariation, int purchasedQuantity) throws Exception
   {
      LOGGER.info("updateEtsy: [{}] title={} purchasedVariation={} purchasedQuantity={}", etsyStoreName, title, purchasedVariation, purchasedQuantity);
      //boolean soldOut = false;
      int totalRemainingQuantity = 0;
      Long listingId = storeSync.getEtsyListingIdForEtsyTitle(title, etsyStoreName);
      LOGGER.info("updateEtsy: [{}] listingId={}", etsyStoreName, listingId);
      if (listingId == null)
      {
         LOGGER.error("updateEtsy: [{}] could not find etsy listingId for title [{}]", etsyStoreName, title);
         SMSClient.alertAdmin("could not find etsy listingId for " + title);
      }
      else
      {
         LOGGER.info("[{}] Getting etsy inventory for listingId={}", etsyStoreName, listingId);
         InventoryResponse inventory = etsyClient.getInventory(listingId);
         // we need to update the quantity of the variations that were not part of the transaction
         boolean updated = false;
         InventoryResults inventoryResults = inventory.getInventoryResults();
         InventoryProduct[] products = inventoryResults.getProducts();
         for (InventoryProduct product : products)
         {
            if (product.getPropertyValues()[0].getPropertyName().equals("Length"))
            {
               Long propertyId = product.getPropertyValues()[0].getPropertyId();
               if (propertyId != 506L)
               {
                  LOGGER.warn("updateEtsy: [{}] found Length property with invalid id of {} for {}, setting it to 506", etsyStoreName, propertyId, title);
                  product.getPropertyValues()[0].setPropertyId(506L);
                  // SMSClient.alertAdmin("found Length property with invalid id of " + propertyId + " for " + title + ", reset to 506");
               }
            }
            String value = product.getPropertyValues()[0].getValues()[0];
            LOGGER.info("[{}] Examining variation value={} of listingId={}", etsyStoreName, value, listingId);
            if (purchasedVariation.equals(value))
            {
               // this variation was updated by etsy
               int quantity = product.getInventoryOfferings()[0].getQuantity();
               totalRemainingQuantity += quantity;
               LOGGER.info("[{}] {} is the purchased variation, no need to adjust this one, quantity is now {}", etsyStoreName, value, quantity);
            }
            else
            {
               // actual yardage purchased
               double numberOfYardsPurchased = Utils.calculateNumberOfBaseUnits(purchasedVariation, purchasedQuantity);
               LOGGER.info("[{}] number of yards purchased={}", etsyStoreName, numberOfYardsPurchased);
               if (value.startsWith("1/2 Yard"))
               {
                  Integer quantity = product.getInventoryOfferings()[0].getQuantity();
                  LOGGER.info("[{}] {} currrent quantity={}", etsyStoreName, value, quantity);
                  Integer newQuantity = quantity - (int)Math.ceil(numberOfYardsPurchased * 2.0);
                  totalRemainingQuantity += newQuantity;
                  LOGGER.info("[{}] {} new quantity={} totalRemainingQuantity={}", etsyStoreName, value, newQuantity, totalRemainingQuantity);
                  product.getInventoryOfferings()[0].setQuantity(newQuantity);
                  updated = true;
               }
               else if (value.startsWith("1 Yard"))
               {
                  // let's set the 1 yard quantity based on the 1/2 yard quantity
                  InventoryProduct halfYard = Utils.getProduct(inventory, "1/2");
                  int halfYardQuantity = halfYard.getInventoryOfferings()[0].getQuantity();
                  InventoryProduct oneYard = Utils.getProduct(inventory, "1 Yard");
                  int oneYardQuantity = (int)Math.floor((double)halfYardQuantity / 2.0);
                  totalRemainingQuantity += oneYardQuantity;
                  LOGGER.info("updateEtsy: [{}] setting 1 Yard quantity to {} due to 1/2 quantity of {} totalRemainingQuantity={} for {}", etsyStoreName, oneYardQuantity, halfYardQuantity, totalRemainingQuantity, title);
                  oneYard.getInventoryOfferings()[0].setQuantity(oneYardQuantity);
                  updated = true;
               }
               else
               {
                  LOGGER.error("[{}] Unexpected variation value: {} for listing {}", etsyStoreName, value, title);
                  SMSClient.alertAdmin("EtsyReceiptPoller.updateEtsy: Unexpected variation value=" + value + " for listing=" + title);
               }
            }
         }
         if (updated)
         {
            if (totalRemainingQuantity == 0)
            {
               String msg = "updateEtsy: " + etsyStoreName + " setting inactive " + title;
               LOGGER.info(msg);
               SMSClient.alertAll(msg);
               etsyClient.updateListingState(listingId, "inactive");
            }
            else
            {
               String updatedProductJson = Utils.toJson(products);
               etsyClient.updateInventory(listingId, updatedProductJson);
            }
         }
      }
      return totalRemainingQuantity == 0;
   }

   /*
   private void updateShopify(String etsyTitle, String variation, int purchasedQuantity, int baseQuantity, boolean isBaseQuantityHalfYard) throws Exception
   {
      LOGGER.info("updateShopify: [{}] etsyTitle={} variation={} purchasedQuantity={}, baseQuantity={} isBaseQuantityHalfYard={}",
         etsyStoreName, etsyTitle, variation, purchasedQuantity, baseQuantity, isBaseQuantityHalfYard);
      ProductInfo productInfo = Utils.getFromMasterMap(etsyTitle, storeSync.getMasterProductMapByEtsyTitle());
      if (productInfo == null)
      {
         LOGGER.warn("updateShopify: {} not found in master map", etsyTitle);
         SMSClient.alertAdmin("EtsyReceiptPoller.updateShopify: " + etsyTitle + " not found in master map");
      }
      else
      {
         String shopifyTitle = productInfo.getShopifyTitle();
         Long productId = storeSync.getShopifyIdForShopifyTitle(shopifyTitle);
         LOGGER.info("updateShopify: etsyTitle={} shopifyTitle={}", etsyTitle, shopifyTitle);
         if (productId == null)
         {
            LOGGER.warn("updateShopify: {} not found in shopifyId map", shopifyTitle);
            SMSClient.alertAdmin("EtsyReceiptPoller.updateShopify: " + shopifyTitle + " not found in shopifyId map");
         }
         else
         {
            Product product = shopifyClient.getProduct(productId);
            if (product.getProductType().equals("Gift Certificate"))
            {
               LOGGER.info("updateShopify: Ignoring Gift Certificate");
            }
            else
            {
               if (Utils.updateInventory(product, baseQuantity, isBaseQuantityHalfYard))
               {
                  shopifyClient.updateProduct(product);
               }
            }
         }
      }
   }

   private void updateShopify(String etsyTitle, int purchasedQuantity)
   {
      LOGGER.info("updateShopify: etsyTitle={} purchasedQuantity={}", etsyTitle, purchasedQuantity);
      ProductInfo productInfo = Utils.getFromMasterMap(etsyTitle, storeSync.getMasterProductMapByEtsyTitle());
      if (productInfo == null)
      {
         LOGGER.warn("updateShopify: {} not found in master map", etsyTitle);
         SMSClient.alertAdmin("EtsyReceiptPoller.updateShopify: " + etsyTitle + " not found in master map");
      }
      else
      {
         String shopifyTitle = productInfo.getShopifyTitle();
         LOGGER.info("updateShopify: etsyTitle={} shopifyTitle={}", etsyTitle, shopifyTitle);
         Long productId = storeSync.getShopifyIdForShopifyTitle(shopifyTitle);
         if (productId == null)
         {
            LOGGER.warn("updateShopify: {} not found in shopifyId map", shopifyTitle);
            SMSClient.alertAdmin("EtsyReceiptPoller.updateShopify: " + shopifyTitle + " not found in shopifyId map");
         }
         else
         {
            Product product = shopifyClient.getProduct(productId);
            if (product.getProductType().equals("Gift Certificate"))
            {
               LOGGER.info("updateShopify: Ignoring Gift Certificate");
            }
            else
            {
               List<Variant> variants = product.getVariants();
               if (variants == null || variants.size() != 1)
               {
                  LOGGER.error("updateShopify: Expected only the default variant for [{}] but received {}", shopifyTitle, variants);
                  SMSClient.alertAdmin("EtsyReceiptPoller.updateShopify: Unexpected variant for [" + shopifyTitle + "] variants=" + variants);
               }
               else
               {
                  // no variations on this product so find the default one
                  Variant variant = variants.get(0);
                  if ("Default Title".equalsIgnoreCase(variant.getTitle()))
                  {
                     Integer currentInventory = variant.getInventoryQuantity();
                     LOGGER.info("updateShopify: currentInventory is {} for [{}]", currentInventory, shopifyTitle);
                     int updatedInventory = currentInventory - purchasedQuantity;
                     LOGGER.info("updateShopify: updatedInventory is {}", updatedInventory);
                     variant.setInventoryQuantity(updatedInventory);
                     shopifyClient.updateProduct(product);
                  }
                  else
                  {
                     LOGGER.error("updateShopify: Expected default variant for [{}] but received {}", shopifyTitle, variant.getTitle());
                     SMSClient.alertAdmin("EtsyReceiptPoller.updateShopify: Expected default variant for [" + shopifyTitle + "] but received " + variant.getTitle());
                  }
               }
            }
         }
      }
   }
   */

   private ProductInfo findInMasterList(String key)
   {
      ProductInfo productInfo = storeSync.getMasterProductMapByEtsyTitle().get(key);
      if (productInfo == null)
      {
         String msg = "Not found in Master Product Map: " + key;
         LOGGER.error(msg);
         SMSClient.alertAdmin(msg);
      }
      return productInfo;
   }

   private ProductInfo findLastPieceInMasterList(String title)
   {
      Collection<ProductInfo> productInfos = storeSync.getMasterProductMapByEtsyTitle().values();
      for (ProductInfo productInfo : productInfos)
      {
         String etsyTitle = productInfo.getEtsyTitle().toLowerCase().replaceAll(" ", "");
         if (etsyTitle.equals(title.toLowerCase().replaceAll(" ", "")))
         {
            return productInfo;
         }
      }
      String msg = "findLastPieceInMasterList: Not found in Master Product Map: " + title;
      LOGGER.error(msg);
      SMSClient.alertAdmin(msg);
      return null;
   }

   private double getListingWeight(Long id)
   {
      LOGGER.info("getListingWeight: id={}", id);
      double weight = 0.0;
      if (id != 0L)
      {
         ListingsResponse listing = etsyClient.getListing(id, "item_weight");
         if (listing != null)
         {
            String itemWeight = listing.getResults()[0].getItemWeight();
            if (itemWeight != null)
            {
               weight = Double.valueOf(weight);
            }
         }
      }
      LOGGER.info("getListingWeight: weight={}", weight);
      return weight;
   }

   private void writeLastSuccess()
   {
      Utils.write(LAST_SUCCESS_FILE, String.valueOf(lastSuccess));
   }

   private void setLastSuccess()
   {
      try
      {
         List<String> lines = Files.readAllLines(Paths.get(LAST_SUCCESS_FILE));
         lastSuccess = Long.valueOf(lines.get(0).trim());
      }
      catch (Exception e)
      {
         LOGGER.error("readLastSuccess", e);
         lastSuccess = System.currentTimeMillis();
      }
      LOGGER.info("lastSuccess={}", lastSuccess);
   }
}
