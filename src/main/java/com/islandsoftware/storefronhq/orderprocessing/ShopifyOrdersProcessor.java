package com.islandsoftware.storefronhq.orderprocessing;

import com.islandsoftware.storefronhq.tools.UpdateEtsyTitles;
import com.islandsoftware.storefronhq.tools.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ShopifyOrdersProcessor extends OrdersProcessor
{
   private static final Logger LOGGER = LoggerFactory.getLogger(EtsyOrdersProcessor.class);

   private static final String SHOPIFY_ORDER_DATE_FORMAT = "yyyy-MM-dd";
   private static final String SHOPIFY_ORDER_DATE_FORMAT_2 = "MM/dd/yyyy";
   private static final SimpleDateFormat SHOPIFY_DATE_FORMATTER = new SimpleDateFormat(SHOPIFY_ORDER_DATE_FORMAT);
   private static final double CARD_FEE_RATE = 0.026;
   private static final double CARD_FEE_CONSTANT = 0.30;

   /*
   public Collection<SpindleAndRoseOrder> readShopifyOrders(String file, Map<String, ProductInfo> products) throws Exception
   {
      return readShopifyOrders(file, products, true);
   }
   */

   public Collection<SpindleAndRoseOrder> readShopifyOrders(String file, Map<String, ProductInfo> products, Map<String, ProductInfo> etsyProducts, boolean failOnProductNotFound) throws Exception
   {
      Map<Long, String> newTitles = UpdateEtsyTitles.fromFileById("C:\\tmp\\etsyNewTitleAndTags.csv");
      Map<String, Long> originalTitles = UpdateEtsyTitles.fromFile("C:\\tmp\\etsyOriginalTitleAndTags.csv");
      Map<String, String> originalToNew = Utils.buildEtsyOriginalTitleToNewTitleMap(originalTitles, newTitles);

      Map<String, Integer> map = null;
      Map<String, SpindleAndRoseOrder> orders = new HashMap<>();
      Map<String, ProductInfo> productsToAdd = new HashMap<>();
      try (BufferedReader br = new BufferedReader(new FileReader(file)))
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
               if (line.trim().equals("") || !line.startsWith("#"))
               {
                  // empty line or continuation of order on previous line
               }
               else
               {
                  line = removeQuotesAndCommasWithinQuotes(line);
                  String[] split = line.split(",");
                  LOGGER.debug("{}" , line);
                  String orderName = (split[map.get("Name")]);
                  SpindleAndRoseOrder order = orders.get(orderName);

                  if (order == null)
                  {
                     order = new SpindleAndRoseOrder();
                     order.setChannel("Shopify");
                     String fullDate = split[map.get("Created at")];
                     String[] datePieces = fullDate.split(" ");
                     Date date = SHOPIFY_DATE_FORMATTER.parse(datePieces[0]);
                     order.setDate(date);
                     String state = split[map.get("Billing Province")];
                     String country = split[map.get("Billing Country")];
                     double value = Double.valueOf(split[map.get("Subtotal")]);
                     double discount = Double.valueOf(split[map.get("Discount Amount")]);
                     double shipping = Double.valueOf(split[map.get("Shipping")]);
                     double salesTax = Double.valueOf(split[map.get("Taxes")]);
                     String refund = split[map.get("Refunded Amount")];
                     if (refund != null && !refund.isEmpty())
                     {
                        order.setRefund(Double.valueOf(refund));
                     }
                     String orderId = (split[map.get("Id")]);
                     order.setOrderId(orderId);
                     order.setShipState(state);
                     order.setShipCountry(country);
                     order.setOrderValue(value);
                     order.setDiscount(discount);
                     order.setShipping(shipping);
                     order.setSalesTax(salesTax);
                     order.setCardProcessingFee(((value - discount + shipping) * CARD_FEE_RATE) + CARD_FEE_CONSTANT);
                     orders.put(orderName, order);
                  }
                  // there are more than 1 entries making up this order
                  int quantity = Integer.valueOf(split[map.get("Lineitem quantity")]);
                  String productNameAndVariation = split[map.get("Lineitem name")];
                  productNameAndVariation = Utils.removeTextInParens(productNameAndVariation);
                  String[] titleAndVariation = Utils.splitOnLastDash(productNameAndVariation);
                  String title = titleAndVariation[0].trim();
                  String variation;
                  if (titleAndVariation.length == 1)
                  {
                     productNameAndVariation = productNameAndVariation + " - Default Title";
                     variation = "Default Title";
                  }
                  else
                  {
                     variation = titleAndVariation[1].trim();
                  }
                  double lineItemPrice = Double.parseDouble(split[map.get("Lineitem price")]);
                  for (int q = 0; q < quantity; q++)
                  {
                     ProductInfo pi = findProductInfo(title, variation, products, etsyProducts, originalToNew);
                     if (pi == null)
                     {
                        if (failOnProductNotFound)
                        {
                           throw new Exception("ShopifyOrdersProcessor: Could not find " + productNameAndVariation + " in product map");
                        }
                        LOGGER.error("Could not find {} in product map", productNameAndVariation);
                        pi = new ProductInfo();
                        pi.setDateAdded(new Date());
                        pi.setShippingCost(0.48);
                        pi.setCost(5.30);
                        pi.setEtsyTitle(title);
                        pi.setVariation(variation);
                        pi.setVendor(vendor(title));
                        productsToAdd.put(Utils.createKey(pi.getEtsyTitle(), pi.getVariation()), pi);
                     }
                     else
                     {
                        LOGGER.debug("found {} in product map", productNameAndVariation);
                        order.getProducts().add(pi);
                     }
                  }
               }
            }
            line = br.readLine();
         }
      }
      new ProductInfoBuilder().write(productsToAdd.values(), "C:\\tmp\\shopifyProductsToAdd.csv");
      return orders.values();
   }

   private ProductInfo findProductInfo(String title, String variation, Map<String, ProductInfo> byShopifyTitle, Map<String, ProductInfo> byEtsyTitle, Map<String, String> originalToNew)
   {
      String key = Utils.createKey(title, variation);
      ProductInfo pi = byShopifyTitle.get(key);
      if (pi == null)
      {
         pi = byEtsyTitle.get(key);
         if (pi == null)
         {
            String newEtsyTitle = originalToNew.get(title.replaceAll(" ", ""));
            if (newEtsyTitle != null)
            {
               key = Utils.createKey(newEtsyTitle, variation);
               pi = byEtsyTitle.get(key);
            }
         }
      }
      return pi;
   }

   private String vendor(String title)
   {
      if (title.toLowerCase().contains("art g"))
      {
         return "Art Gallery";
      }
      if (title.toLowerCase().contains("andover"))
      {
         return "Andover Fabrics";
      }
      if (title.toLowerCase().contains("alexander h"))
      {
         return "Alexander Henry Fabric";
      }
      if (title.toLowerCase().contains("moda"))
      {
         return "Moda United Notions";
      }
      if (title.toLowerCase().contains("kokka"))
      {
         return "Kokka";
      }
      if (title.toLowerCase().contains("cotton and steel") || title.toLowerCase().contains("cotton + steel"))
      {
         return "Cotton + Steel";
      }
      if (title.toLowerCase().contains("charlie harper") || title.toLowerCase().contains("birch"))
      {
         return "Birch Organic Fabrics";
      }
      return "not set";
   }
}
