package com.islandsoftware.storefronhq;

import com.islandsoftware.storefronhq.etsy.EtsyClient;
import com.islandsoftware.storefronhq.etsy.model.inventory.InventoryResponse;
import com.islandsoftware.storefronhq.shopify.sync.model.LineItems;
import com.islandsoftware.storefronhq.shopify.sync.model.Product;
import com.islandsoftware.storefronhq.shopify.sync.model.ShopifyOrder;
import com.islandsoftware.storefronhq.stats.OrderStats;
import com.islandsoftware.storefronhq.orderprocessing.ProductInfo;
import com.islandsoftware.storefronhq.stats.OrderItem;
import com.islandsoftware.storefronhq.tools.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ProcessShopifyOrderThread implements Runnable
{
   private ShopifyOrder order;
   private StoreSync storeSync;

   private static final Logger LOGGER = LoggerFactory.getLogger(ProcessShopifyOrderThread.class);

   public ProcessShopifyOrderThread(ShopifyOrder order, StoreSync storeSync)
   {
      this.order = order;
      this.storeSync = storeSync;
   }

   public void run()
   {
      LOGGER.info("run: executing {}", this.toString());
      LineItems[] lineItems = order.getLineItems();
      long orderId = order.getId();
      String totalPrice = order.getTotalPrice();
      String subTotal = order.getSubTotal();
      String tax = order.getTax();
      double weightInOunces = Utils.gramsToOunces(order.getWeight());
      String country = order.getShippingAddress().getCountry();
      LOGGER.info("orderId={} totalPrice={} subTotal={} tax={} weightInOunces={} country={}",
         orderId, totalPrice, subTotal, tax, weightInOunces, country);
      double totalCost = 0.0;
      double totalShippingCost = 0.0;
      List<OrderItem> orderItems = new ArrayList<>();
      int unitsSold = 0;
      for (LineItems lineItem : lineItems)
      {
         OrderItem orderItem = new OrderItem();
         orderItem.setOrderId(orderId);
         String shopifyTitle = lineItem.getTitle();
         orderItem.setTitle(shopifyTitle);
         String variation = lineItem.getVariantTitle();
         orderItem.setVariation(variation);
         int purchasedQuantity = lineItem.getQuantity();
         orderItem.setQuantity(purchasedQuantity);
         unitsSold += purchasedQuantity;
         long productId = lineItem.getProductId();
         String key = Utils.createKey(shopifyTitle, variation);
         ProductInfo productInfo = storeSync.getMasterProductMapByShopifyTitle().get(key);
         if (productInfo == null)
         {
            String msg = "Not found in Master Product Map: " + key;
            LOGGER.error(msg);
            SMSClient.alertAdmin(msg);
            continue;
         }
         else
         {
            totalCost += productInfo.getCost() * purchasedQuantity;
            totalShippingCost += productInfo.getShippingCost() * purchasedQuantity;
         }

         Product product = storeSync.getShopifyClient().getProduct(productId);
         try
         {
            if (Utils.updateInventory(product, lineItem.getVariantId(), variation, purchasedQuantity))
            {
               storeSync.getShopifyClient().updateProduct(product);
            }
         }
         catch (Exception e)
         {
            LOGGER.error("run: Could not update shopify product", e);
            SMSClient.alertAdmin("ProcessShopifyOrder.run: Could not update shopify product: " + e.getMessage());
         }

         try
         {
            String etsyTitle = productInfo.getEtsyTitle();
            Long listingId = storeSync.getEtsyListingIdForEtsyTitle(etsyTitle);
            if (listingId != null)
            {
               LOGGER.info("run: found etsyListingId {} for etsyTitle [{}]", listingId, etsyTitle);
               LOGGER.info("run: updating etsyTitle [{}]", etsyTitle);
               double numberOfYards = purchasedQuantity;
               if (Utils.isPartialYard(variation))
               {
                  numberOfYards = Utils.calculateNumberOfBaseUnits(variation, purchasedQuantity);
                  LOGGER.info("run: variation and quantity results in {} yards purchased", numberOfYards);
               }
               EtsyClient etsyClient = storeSync.getEtsyClientForListingId(listingId);
               InventoryResponse inventory = etsyClient.getInventory(listingId);
               if (Utils.updateEtsyInventory(etsyTitle, inventory, numberOfYards))
               {
                  // sold out
                  String msg = "ProcessShopifyOrder: setting inactive " + etsyTitle;
                  LOGGER.info(msg);
                  SMSClient.alertAll(msg);
                  etsyClient.updateListingState(listingId, "inactive");
               }
               else
               {
                  etsyClient.updateInventory(listingId, Utils.toJson(inventory.getInventoryResults().getProducts()));
               }
               LOGGER.info("run: update complete for etsyListingId={}, etsyTitle=[{}]", listingId, etsyTitle);
            }
            else
            {
               LOGGER.error("ProcessShopifyOrder: could not find estyListingId for etsyTitle [{}]", etsyTitle);
               SMSClient.alertAdmin("ProcessShopifyOrder: could not find etsyListingId for etsyTtile [" + etsyTitle + "]");
            }
         }
         catch (Exception e)
         {
            LOGGER.error("run: Could not update etsy listing", e);
            SMSClient.alertAdmin("ProcessShopifyOrder.run: Could not update etsy listing: " + e.getMessage());
         }
         orderItems.add(orderItem);
      }
      OrderStats orderStats = Utils.calculateProfit("Shopify", orderId, totalPrice, subTotal, tax, totalCost, totalShippingCost, weightInOunces, unitsSold, country, orderItems);
      storeSync.getStatsCollector().collect(orderStats);
   }

   @Override
   public String toString()
   {
      return "ProcessShopifyOrderThread{" +
            ", orderId=" + order.getId() +
            '}';
   }
}
