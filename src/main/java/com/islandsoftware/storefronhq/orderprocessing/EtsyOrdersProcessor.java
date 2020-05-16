package com.islandsoftware.storefronhq.orderprocessing;

import com.islandsoftware.storefronhq.tools.UpdateEtsyTitles;
import com.islandsoftware.storefronhq.tools.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.*;

public class EtsyOrdersProcessor extends OrdersProcessor
{
   private static final Logger LOGGER = LoggerFactory.getLogger(EtsyOrdersProcessor.class);

   private static final String ETSY_ORDER_DATE_FORMAT = "MM/dd/yy";
   private static final SimpleDateFormat ETSY_DATE_FORMATTER = new SimpleDateFormat(ETSY_ORDER_DATE_FORMAT);

   public List<SpindleAndRoseOrder> readEtsyOrders(String ordersFile, String orderItemsFile, Map<String, ProductInfo> products) throws Exception
   {
      return readEtsyOrders(ordersFile, orderItemsFile, products, true);
   }

   public List<SpindleAndRoseOrder> readEtsyOrders(String ordersFile, String orderItemsFile, Map<String, ProductInfo> products, boolean failOnProductNotFound) throws Exception
   {
      Map<Long, String> newTitles = UpdateEtsyTitles.fromFileById("C:\\tmp\\etsyNewTitleAndTags.csv");
      Map<String, Long> originalTitles = UpdateEtsyTitles.fromFile("C:\\tmp\\etsyOriginalTitleAndTags.csv");
      Map<String, String> originalToNew = Utils.buildEtsyOriginalTitleToNewTitleMap(originalTitles, newTitles);

      Map<String, List<EtsyOrderItem>> orderItems = readOrderItems(orderItemsFile);

      Map<String, Integer> map = null;
      List<SpindleAndRoseOrder> orders = new ArrayList<>();
      Map<String, ProductInfo> productsToAdd = new HashMap<>();
      try (BufferedReader br = new BufferedReader(new FileReader(ordersFile)))
      {
         String line = br.readLine();
         int i = 0;
         while(line != null)
         {
            if (i++ == 0)
            {
               // header row
               map = createHeader2ColumnNumberMap(line);
            }
            else
            {
               line = removeQuotesAndCommasWithinQuotes(line);
               String[] split = line.split(",");
               String orderId = split[map.get("Order ID")];
               Date date = ETSY_DATE_FORMATTER.parse(split[map.get("Sale Date")]);
               String state = split[map.get("Ship State")];
               String country = split[map.get("Ship Country")];
               double value = Double.valueOf(split[map.get("Order Value")]);
               double discount = Double.valueOf(split[map.get("Discount Amount")]);
               double shipping = Double.valueOf(split[map.get("Shipping")]);
               double shippingDiscount = Double.valueOf(split[map.get("Shipping Discount")]);
               double salesTax = Double.valueOf(split[map.get("Sales Tax")]);
               double cardProcessingFee = 0.0;
               double refund = 0.0;
               String fee = split[map.get("Card Processing Fees")];
               if (fee != null && !fee.isEmpty())
               {
                  cardProcessingFee = Double.valueOf(fee);
               }
               String adjustedCardProcessingFee = split[map.get("Adjusted Card Processing Fees")];
               if (adjustedCardProcessingFee != null && !adjustedCardProcessingFee.isEmpty())
               {
                  cardProcessingFee = Double.valueOf(adjustedCardProcessingFee);

                  // etsy refunded amount = order net AA - adjusted net order amount AD + (card processing fees Z - adjusted card processing fees AC)
                  String adjustedNetOrderAmountStr = split[map.get("Adjusted Net Order Amount")];
                  if (adjustedNetOrderAmountStr != null && !adjustedNetOrderAmountStr.isEmpty())
                  {
                     double origCardProcessingFee = Double.valueOf(fee);
                     double adjustedNetOrderAmount = Double.valueOf(adjustedNetOrderAmountStr);
                     double orderNet = Double.valueOf(split[map.get("Order Net")]);
                     refund = orderNet - adjustedNetOrderAmount + origCardProcessingFee - cardProcessingFee;
                  }
               }

               SpindleAndRoseOrder order = new SpindleAndRoseOrder(date);
               order.setChannel("Etsy");
               order.setOrderId(orderId);
               order.setShipState(state);
               order.setShipCountry(country);
               order.setOrderValue(value);
               order.setDiscount(discount);
               order.setShipping(shipping);
               order.setShippingDiscount(shippingDiscount);
               order.setSalesTax(salesTax);
               order.setRefund(refund);
               order.setCardProcessingFee(cardProcessingFee);
               List<EtsyOrderItem> etsyOrderItems = orderItems.get(orderId);
               for (EtsyOrderItem etsyOrderItem : etsyOrderItems)
               {
                  for (int q = 0; q < etsyOrderItem.getQuantity(); q++)
                  {
                     if (etsyOrderItem.getTitle().contains("Last Piece"))
                     {
                        etsyOrderItem.setVariation("Last Piece");
                     }
                     if (etsyOrderItem.getTitle().contains("Custom Listing"))
                     {
                        etsyOrderItem.setVariation("Default Title");
                     }
                     String key = Utils.createKey(etsyOrderItem.getTitle(), etsyOrderItem.getVariation());
                     ProductInfo pi = products.get(key);
                     if (pi == null)
                     {
                        // title may have changed
                        String newTitle = originalToNew.get(etsyOrderItem.getTitle().replaceAll(" ", ""));
                        if (newTitle != null)
                        {
                           key = Utils.createKey(newTitle, etsyOrderItem.getVariation());
                           pi = products.get(key);
                           if (pi == null)
                           {
                              if (failOnProductNotFound)
                              {
                                 throw new Exception("Could not find " + key + " in product map");
                              }
                              LOGGER.error("Could not find {} in product map", key);
                              pi = new ProductInfo();
                              pi.setDateAdded(new Date());
                              pi.setShippingCost(0.48);
                              pi.setCost(5.30);
                              //pi.setAutoUpdateCost("true");
                              pi.setSku("not set");
                              pi.setEtsyTitle(etsyOrderItem.getTitle());
                              pi.setVariation(etsyOrderItem.getVariation());
                              pi.setShopifyTitle("Not In Shopify");
                              pi.setVendor("not set");
                              productsToAdd.put(Utils.createKey(etsyOrderItem.getTitle(), etsyOrderItem.getVariation()), pi);
                           }
                           else
                           {
                              LOGGER.debug("found {} in product map by new title", key);
                              order.getProducts().add(pi);
                           }
                        }
                        else
                        {
                           LOGGER.info("could not find a new title for {}", etsyOrderItem.getTitle());
                           pi = new ProductInfo();
                           pi.setDateAdded(new Date());
                           pi.setShippingCost(0.48);
                           pi.setCost(5.30);
                           //pi.setAutoUpdateCost("true");
                           pi.setSku("not set");
                           pi.setEtsyTitle(etsyOrderItem.getTitle());
                           pi.setVariation(etsyOrderItem.getVariation());
                           pi.setShopifyTitle("Not In Shopify");
                           pi.setVendor("not set");
                           productsToAdd.put(Utils.createKey(etsyOrderItem.getTitle(), etsyOrderItem.getVariation()), pi);
                        }
                     }
                     else
                     {
                        LOGGER.debug("found {} in product map", key);
                        order.getProducts().add(pi);
                     }
                  }
               }
               orders.add(order);
            }
            line = br.readLine();
         }
      }
      new ProductInfoBuilder().write(productsToAdd.values(), "C:\\tmp\\productsToAdd.csv");
      return orders;
   }

   private Map<String, List<EtsyOrderItem>> readOrderItems(String orderItemsFile) throws Exception
   {
      Map<String, Integer> map = null;
      Map<String, List<EtsyOrderItem>> orderItems = new HashMap<>();
      try (BufferedReader br = new BufferedReader(new FileReader(orderItemsFile)))
      {
         String line = br.readLine();
         int i = 0;
         while (line != null)
         {
            if (i++ == 0)
            {
               // header row
               map = createHeader2ColumnNumberMap(line);
               LOGGER.info("readOrderItems: header2ColumnNumberMap={}", map);
            }
            else
            {
               line = removeQuotesAndCommasWithinQuotes(line);
               String[] split = line.split(",");
               String title = split[map.get("Item Name")];
               String orderId = split[map.get("Order ID")];
               int quantity = Integer.parseInt(split[map.get("Quantity")]);
               double price = Double.parseDouble(split[map.get("Price")]);
               String variation = split[map.get("Variations")];
               if (variation == null || variation.isEmpty())
               {
                  variation = "Default Title";
               }
               else
               {
                  variation = variation.split(":")[1];
                  variation = variation.replace("inches", "").trim();
                  variation = Utils.removeTextInParens(variation);
               }
               List<EtsyOrderItem> etsyOrderItems = orderItems.get(orderId);
               if (etsyOrderItems == null)
               {
                  etsyOrderItems = new ArrayList<>();
                  orderItems.put(orderId, etsyOrderItems);
               }
               EtsyOrderItem etsyOrderItem = new EtsyOrderItem();
               etsyOrderItem.setTitle(title);
               etsyOrderItem.setVariation(variation);
               etsyOrderItem.setQuantity(quantity);
               etsyOrderItem.setOrderId(orderId);
               etsyOrderItem.setPrice(price);
               etsyOrderItems.add(etsyOrderItem);
            }
            line = br.readLine();
         }
      }
      return orderItems;
   }
}

/*
  etsy refunded amount = order net AA - adjusted net order amount AD + (card processing fees Z - adjusted card processing fees AC)


  Sale Date,
  Order ID,
  Buyer User ID,
  Full Name,
  First Name,
  Last Name,
  Number of Items,
  Payment Method,
  Date Shipped,
  Street 1,
  Street 2,
  Ship City,
  Ship State,
  Ship Zipcode,
  Ship Country,
  Currency,
  Order Value,
  Coupon Code,
  Coupon Details,
  Discount Amount,
  Shipping Discount,
  Shipping,
  Sales Tax,
  Order Total,
  Status,
  Card Processing Fees,
  Order Net,
  Adjusted Order Total,
  Adjusted Card Processing Fees,
  Adjusted Net Order Amount,
  Buyer,
  Order Type,
  Payment Type,
  InPerson Discount,
  InPerson Location

 */
