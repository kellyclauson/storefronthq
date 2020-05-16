package com.islandsoftware.storefronhq;

import com.islandsoftware.storefronhq.etsy.EtsyClient;
import com.islandsoftware.storefronhq.etsy.model.ListingsResponse;
import com.islandsoftware.storefronhq.shopify.sync.model.ShopifyOrder;
import com.islandsoftware.storefronhq.etsy.model.ListingsResult;
import com.islandsoftware.storefronhq.orderprocessing.ProductInfo;
import com.islandsoftware.storefronhq.shopify.sync.ShopifyClient;
import com.islandsoftware.storefronhq.stats.StatsCollector;
import com.islandsoftware.storefronhq.tools.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.*;

public class StoreSync
{
   public static final String TITLE_TAG_FILENAME = "/home/pi/spindleandrose/sync/conf/.titletags";

   private ShopifyClient shopifyClient;
   private EtsyClient spindleEtsyClient;
   //private EtsyClient imagineEtsyClient;

   private Map<String, ProductInfo> masterProductMapByEtsyTitle;
   private Map<String, ProductInfo> masterProductMapByShopifyTitle = new HashMap<>();
   private Set<String> titleTagSet;

   private ScheduledThreadPoolExecutor scheduler;
   private ThreadPoolExecutor executor;
   private ThreadPoolMonitor monitor;

   private StatsCollector statsCollector;

   private static final Logger LOGGER = LoggerFactory.getLogger(StoreSync.class);

   public StoreSync()
   {
   }

   public void initialize(ShopifyClient shopifyClient, EtsyClient spindleEtsyClient) throws Exception
   {
      this.shopifyClient = shopifyClient;
      this.spindleEtsyClient = spindleEtsyClient;
      //this.imagineEtsyClient = imagineEtsyClient;

      loadMasterProductMap();
      titleTagSet = Utils.setFromFile(TITLE_TAG_FILENAME);
      if (titleTagSet == null)
      {
         titleTagSet = shopifyClient.getTitleTagSet();
         Utils.toFile(titleTagSet, TITLE_TAG_FILENAME);
      }
      LOGGER.info("initialize: titleTagSet size={}", titleTagSet.size());
      statsCollector = new StatsCollector();
   }

   public EtsyClient getEtsyClientForListingId(long listingId)
   {
      //ListingsResponse listing = imagineEtsyClient.getListing(listingId, "user_id");
      ////long userId = listing.getResults()[0].getUserId();
      //if (userId == imagineEtsyClient.getUserId())
      //{
      //   return imagineEtsyClient;
      //}
      return spindleEtsyClient;
   }


   public void writeTitleTags()
   {
      Utils.toFile(titleTagSet, TITLE_TAG_FILENAME);
   }

   public synchronized void loadMasterProductMap() throws Exception
   {
      this.masterProductMapByEtsyTitle = GoogleSheets.readProductInfo();
      //masterProductMapByShopifyTitle = Utils.createShopifyTitle2ProductInfoMapFromEtsyTitle2ProductInfoMap(masterProductMapByEtsyTitle);
   }

   public void startSchedulers()
   {
      LOGGER.info("creating thread pool...");
      executor = new ThreadPoolExecutor(10, 10, Long.MAX_VALUE, TimeUnit.NANOSECONDS, new ArrayBlockingQueue<>(1000), new RejectedEventHandler());
      monitor = new ThreadPoolMonitor(executor, 30000L);
      LOGGER.info("starting thread pool monitor...");
      Thread monitorThread = new Thread(monitor);
      monitorThread.start();
      int threadPoolSize = 5;
      LOGGER.info("creating scheduler with threadPoolSize={}", threadPoolSize);
      scheduler = new ScheduledThreadPoolExecutor(threadPoolSize);
      EtsyReceiptPoller spindleEtsyReceiptPoller = new EtsyReceiptPoller(spindleEtsyClient, "spindle", shopifyClient, this);
      scheduler.scheduleAtFixedRate(spindleEtsyReceiptPoller, 60000L, 60000L, TimeUnit.MILLISECONDS);
      //EtsyReceiptPoller imagineEtsyReceiptPoller = new EtsyReceiptPoller(imagineEtsyClient, "imagine", shopifyClient, this);
      //scheduler.scheduleAtFixedRate(imagineEtsyReceiptPoller, 90000L, 60000L, TimeUnit.MILLISECONDS);
      //InventorySync inventorySync = new InventorySync(this);
      // sync every 6 hours
      //scheduler.scheduleAtFixedRate(inventorySync, 360L, 360L, TimeUnit.MINUTES);
      // look for products every hour
      // a reload of the master product list from google sheets follows create products
      //CreateProductsThread createProductsThread = new CreateProductsThread(this);
      //scheduler.scheduleAtFixedRate(createProductsThread, 1L, 1L, TimeUnit.HOURS);
      // look for products to remove from the new arrivals collection once a day
      //NewArrivalsThread newArrivalsThread = new NewArrivalsThread();
      //scheduler.scheduleAtFixedRate(newArrivalsThread, 24L, 24L, TimeUnit.HOURS);
      // send alert messages
      SendAlertMessageThread sendAlertMessageThread = new SendAlertMessageThread();
      scheduler.scheduleAtFixedRate(sendAlertMessageThread, 35L, 60L, TimeUnit.SECONDS);
   }

   public synchronized void update()
   {
      LOGGER.info("update: begin");
      shopifyClient.update();
      spindleEtsyClient.update();
      //imagineEtsyClient.update();
      LOGGER.info("update: end");
   }

   public void handleShopifyOrder(ShopifyOrder order)
   {
      executor.execute(new ProcessShopifyOrderThread(order, this));
   }

   public void shutdown()
   {
      LOGGER.info("shutting down thread pool monitor");
      monitor.shutdown();
      LOGGER.info("shutting down thread pool");
      executor.shutdown();
      LOGGER.info("shutting down scheduler");
      scheduler.shutdown();
      //imagineEtsyClient.shutdown();
      spindleEtsyClient.shutdown();
   }

   public synchronized int getShopifyTitle2IdSize()
   {
      return shopifyClient.getTitle2Id().size();
   }

   public synchronized int getShopifyId2TitleSize()
   {
      return shopifyClient.getId2Title().size();
   }

   public synchronized int getEtsyId2TitleSize(String storeName)
   {
      return spindleEtsyClient.getId2Title().size();
      /*
      if (storeName.equalsIgnoreCase("spindle"))
      {
         return spindleEtsyClient.getId2Title().size();
      }
      return imagineEtsyClient.getId2Title().size();
      */
   }

   public synchronized int getEtsyTitle2ListingIdSize(String storeName)
   {
      return spindleEtsyClient.getTitle2Id().size();
      /*
      if (storeName.equalsIgnoreCase("spindle"))
      {
         return spindleEtsyClient.getTitle2Id().size();
      }
      return imagineEtsyClient.getTitle2Id().size();
      */
   }

   public synchronized Long getEtsyListingIdForEtsyTitle(String etsyTitle, String storeName)
   {
      return spindleEtsyClient.getTitle2Id().get(etsyTitle);
      /*
      if (storeName.equalsIgnoreCase("spindle"))
      {
         return spindleEtsyClient.getTitle2Id().get(etsyTitle);
      }
      return imagineEtsyClient.getTitle2Id().get(etsyTitle);
      */
   }

   public synchronized Long getEtsyListingIdForEtsyTitle(String etsyTitle)
   {
      /*
      Long id = imagineEtsyClient.getTitle2Id().get(etsyTitle);
      if (id != null)
      {
         return id;
      }
      */
      return spindleEtsyClient.getTitle2Id().get(etsyTitle);
   }

   public synchronized String getShopifyTitleForShopifyId(Long shopifyId)
   {
      return shopifyClient.getId2Title().get(shopifyId);
   }

   public StatsCollector getStatsCollector()
   {
      return statsCollector;
   }

   public EtsyClient getSpindleEtsyClient()
   {
      return spindleEtsyClient;
   }

   /*
   public EtsyClient getImagineEtsyClient()
   {
      return imagineEtsyClient;
   }
   */

   public ShopifyClient getShopifyClient()
   {
      return shopifyClient;
   }

   public synchronized Map<String, ProductInfo> getMasterProductMapByEtsyTitle()
   {
      return masterProductMapByEtsyTitle;
   }

   public synchronized Map<String, ProductInfo> getMasterProductMapByShopifyTitle()
   {
      return masterProductMapByShopifyTitle;
   }

   public Set<String> getTitleTagSet()
   {
      return titleTagSet;
   }

   public synchronized Long getShopifyIdForShopifyTitle(String title)
   {
      return shopifyClient.getTitle2Id().get(title);
   }

   public Map<String, Long> getShopifyTitle2Id()
   {
      return shopifyClient.getTitle2Id();
   }

   public Map<Long, String> getSpindleEtsyId2Title()
   {
      return spindleEtsyClient.getId2Title();
   }

   public synchronized List<Long> shopifyProductIdsNotInEtsy()
   {
      List<Long> notInEtsy = new ArrayList<>();

      for (Entry<Long, String> entry : shopifyClient.getId2Title().entrySet())
      {
         String shopifyTitle = entry.getValue();
         ProductInfo pi = Utils.getFromMasterMap(shopifyTitle, masterProductMapByShopifyTitle);
         if (pi != null)
         {
            String etsyTitle = pi.getEtsyTitle().trim();
            if (!spindleEtsyClient.getTitle2Id().containsKey(etsyTitle))
            {
               LOGGER.info("etsy title {} not in etsyTitle2ListingId map, maps to shopify title={}", etsyTitle, shopifyTitle);
               notInEtsy.add(entry.getKey());
            }
         }
         else
         {
            String msg = "Shopify title " + shopifyTitle + " is in shopifyId2Title map but not in masterProductMapByShopifyTitle";
            LOGGER.error(msg);
            SMSClient.alertAdmin(msg);
         }
      }

      return notInEtsy;
   }

   public synchronized List<ListingsResult> etsyListingsNotInShopify()
   {
      LOGGER.info("etsyListingsNotInShopify: begin");
      List<ListingsResult> listingsNotInShopify = new ArrayList<>();

      Map<Long, String> allEtsyStores = new HashMap<>();
      allEtsyStores.putAll(spindleEtsyClient.getId2Title());
      //allEtsyStores.putAll(imagineEtsyClient.getId2Title());

      for (Entry<Long, String> entry : allEtsyStores.entrySet())
      {
         String etsyTitle = entry.getValue();
         ProductInfo pi = Utils.getFromMasterMap(etsyTitle, masterProductMapByEtsyTitle);
         if (pi != null)
         {
            String shopifyTitle = pi.getShopifyTitle();
            if (!shopifyClient.getTitle2Id().containsKey(shopifyTitle))
            {
               /*
               ListingsResponse listing = imagineEtsyClient.getListing(entry.getKey(), null);
               if (listing == null)
               {
                  listing = spindleEtsyClient.getListing(entry.getKey(), null);
               }
               */
               ListingsResponse listing = spindleEtsyClient.getListing(entry.getKey(), null);
               listingsNotInShopify.add(listing.getResults()[0]);
            }
         }
         else
         {
            if (!etsyTitle.toLowerCase().contains("thread"))
            {
               String msg = "Etsy title " + etsyTitle + " is in spindleEtsyId2Title map but not in masterProductMapByEtsyTitle";
               LOGGER.error(msg);
               SMSClient.alertAdmin(msg);
            }
         }
      }
      LOGGER.info("etsyListingsNotInShopify: end");
      return listingsNotInShopify;
   }
}
