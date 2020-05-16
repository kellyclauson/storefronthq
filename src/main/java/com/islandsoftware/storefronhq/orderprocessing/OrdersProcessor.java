package com.islandsoftware.storefronhq.orderprocessing;

import com.islandsoftware.storefronhq.GoogleSheets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.NumberFormat;
import java.util.*;


public class OrdersProcessor
{
   private static final Logger LOGGER = LoggerFactory.getLogger(OrdersProcessor.class);
   private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance();
   private static final NumberFormat PERCENT_FORMAT = NumberFormat.getPercentInstance();
   private static final String ETSY_FILE = "C:\\tmp\\EtsySoldOrders2017-12.csv";
   private static final String ETSY_ORDER_ITEMS_FILE = "C:\\tmp\\EtsySoldOrderItems2017-12.csv";
   //private static final String SHOPIFY_FILE = "C:\\tmp\\orders_export.csv";
   private static final String SHOPIFY_FILE = "C:\\tmp\\orders_export2017-12.csv";
   //private static final String ETSY_FILE = "C:\\tmp\\EtsySoldOrders2018-1.csv";
   //private static final String ETSY_ORDER_ITEMS_FILE = "C:\\tmp\\EtsySoldOrderItems2018-1.csv";
   //private static final String SHOPIFY_FILE = "C:\\tmp\\ShopifyOrdersJan2018.csv";

   private static final double JAN_ETSY_FEES = 1397.18;
   private static final double JAN_SHOPIFY_FEES = 82.13 + 71.0;

   public static void main(String[] args)
   {
      try
      {
         List<SpindleAndRoseOrder> orders = getOrders();
         LOGGER.info("Total Number of orders = {}", orders.size());
         //LOGGER.info("Average order value = {}", averageOrderRevenue(orders));
         //reportByQuarter(orders);
         //reportRevenue(etsyOrders, shopifyOrders, orders);
         //reportSalesByCountry();

         /*
         double etsyRevenue = sumOrderValue(etsyOrders);
         double shopifyRevenue = sumOrderValue(shopifyOrders);
         double totalRevenue = sumOrderValue(orders);
         double etsyProfit = calculateProfit(etsyOrders, JAN_ETSY_FEES);
         double shopifyProfit = calculateProfit(shopifyOrders, JAN_SHOPIFY_FEES);
         double totalProfit = calculateProfit(orders, JAN_ETSY_FEES + JAN_SHOPIFY_FEES);

         LOGGER.info("etsy revenue={} profit={} percent={}", CURRENCY_FORMAT.format(etsyRevenue), CURRENCY_FORMAT.format(etsyProfit), PERCENT_FORMAT.format(etsyProfit / etsyRevenue));
         LOGGER.info("shopify revenue={} profit={} percent={}", CURRENCY_FORMAT.format(shopifyRevenue), CURRENCY_FORMAT.format(shopifyProfit), PERCENT_FORMAT.format(shopifyProfit / shopifyRevenue));
         LOGGER.info("total revenue={} profit={} percent={}", CURRENCY_FORMAT.format(totalRevenue), CURRENCY_FORMAT.format(totalProfit), PERCENT_FORMAT.format(totalProfit / totalRevenue));
         */
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   public static List<SpindleAndRoseOrder> getOrders() throws Exception
   {
      Map<String, ProductInfo> products = GoogleSheets.readProductInfo();
      //Map<String, ProductInfo> shopifyProducts = Utils.createShopifyTitle2ProductInfoMapFromEtsyTitle2ProductInfoMap(products);
      boolean failOnProductNotFound = false;
      List<SpindleAndRoseOrder> etsyOrders = new EtsyOrdersProcessor().readEtsyOrders(ETSY_FILE, ETSY_ORDER_ITEMS_FILE, products, failOnProductNotFound);
      LOGGER.info("Number of Etsy orders = {}", etsyOrders.size());
      //Collection<SpindleAndRoseOrder> shopifyOrders = new ShopifyOrdersProcessor().readShopifyOrders(SHOPIFY_FILE, shopifyProducts, products, failOnProductNotFound);
      //LOGGER.info("Number of Shopify orders = {}", shopifyOrders.size());
      List<SpindleAndRoseOrder> orders = new ArrayList<>();
      orders.addAll(etsyOrders);
      //orders.addAll(shopifyOrders);
      LOGGER.info("Total Number of orders = {}", orders.size());
      return orders;
   }

   private static double averageOrderRevenue(List<SpindleAndRoseOrder> orders) throws Exception
   {
      double total = 0.0;
      for (SpindleAndRoseOrder order : orders)
      {
         total += order.getOrderValue();
      }
      return total / (double)orders.size();
   }

   private static double calculateProfit(Collection<SpindleAndRoseOrder> orders, double fees) throws Exception
   {
      double total = 0.0;
      for (SpindleAndRoseOrder order : orders)
      {
         LOGGER.info("****************");
         double value = order.getOrderValue();
         total += value;
         double shipping = order.getShipping();
         total += shipping;
         double cardFee = order.getCardProcessingFee();
         total -= cardFee;
         double refund = order.getRefund();
         total -= refund;
         List<ProductInfo> products = order.getProducts();
         LOGGER.info("value={} shipping={} cardFee={} refund={}", value, shipping, cardFee, refund);
         double totalCost = 0.0;
         for (ProductInfo product : products)
         {
            double cost = product.getCost();
            totalCost += cost;
            total -= cost;
            double shipCost = product.getShippingCost();
            totalCost += shipCost;
            total -= shipCost;
            LOGGER.info("cost={} shipCost={} product={}", cost, shipCost, product.getEtsyTitle());
         }
         LOGGER.info("totalCost={} total={}", totalCost, total);
      }
      total -= fees;
      return total;
   }

   private static void reportByQuarter(List<SpindleAndRoseOrder> orders) throws Exception
   {
      for (int quarter = 1; quarter < 5; quarter++)
      {
         double totalOrderValue = sumOrderValue(orders, quarter);
         double totalShippingValue = sumShipping(orders, quarter);
         double salesTax = sumSalesTax(orders, quarter);
         double grossSales = totalOrderValue + totalShippingValue;
         double totalOrderValueExceptColorado = sumOrderValue(orders, quarter, "CO");
         LOGGER.info("Quarter={} grossSales={} revenue={} serviceSales={}, salesOutOfTaxingArea={}, salesTax={}",
               quarter,
               CURRENCY_FORMAT.format(grossSales),
               CURRENCY_FORMAT.format(totalOrderValue),
               CURRENCY_FORMAT.format(totalShippingValue),
               CURRENCY_FORMAT.format(totalOrderValueExceptColorado),
               CURRENCY_FORMAT.format(salesTax));
      }
   }

   private static void reportRevenue(Collection<SpindleAndRoseOrder> etsyOrders, Collection<SpindleAndRoseOrder> shopifyOrders, Collection<SpindleAndRoseOrder> orders) throws Exception
   {
      double etsyRevenue = sumOrderValue(etsyOrders);
      double shopifyRevenue = sumOrderValue(shopifyOrders);
      double totalRevenue = sumOrderValue(orders);

      double etsyShipping = sumShipping(etsyOrders);
      double shopifyShipping = sumShipping(shopifyOrders);
      double totalShipping = sumShipping(orders);

      double totalDiscount = sumDiscount(orders);
      double totalShippingDiscount = sumShippingDiscount(orders);

      LOGGER.info("");
      LOGGER.info("Etsy Gross Payments Received:\t\t{}", CURRENCY_FORMAT.format(etsyRevenue + etsyShipping));
      LOGGER.info("Shopify Gross Payments Recieved:\t{}", CURRENCY_FORMAT.format(shopifyRevenue + shopifyShipping));
      LOGGER.info("Total Gross Payments Received:\t\t{}", CURRENCY_FORMAT.format(totalRevenue + totalShipping));
      LOGGER.info("");

      LOGGER.info("");
      LOGGER.info("Etsy Revenue:\t\t{}", CURRENCY_FORMAT.format(etsyRevenue));
      LOGGER.info("Shopify Revenue:\t{}", CURRENCY_FORMAT.format(shopifyRevenue));
      LOGGER.info("Total Revenue:\t\t{}", CURRENCY_FORMAT.format(totalRevenue));
      LOGGER.info("");
      //LOGGER.info("Total Order Discounts for {}:\t{}", year, CURRENCY_FORMAT.format(totalDiscount));
      //LOGGER.info("Total Shipping Discounts for {}:\t{}", year, CURRENCY_FORMAT.format(totalShippingDiscount));
      //LOGGER.info("Total Discounts for {}:\t\t\t{}", year, CURRENCY_FORMAT.format(totalDiscount + totalShippingDiscount));
      //LOGGER.info("");
      //LOGGER.info("Cost of Goods Sold {}:\t{}", year, CURRENCY_FORMAT.format(totalAnnualRevenue / 1.965));
      double cogs = calculateCostOfGoodsSold(orders);
      double grossProfit = totalRevenue - cogs;
      LOGGER.info("Cost of Goods Sold:\t{}", CURRENCY_FORMAT.format(cogs));
      LOGGER.info("Gross Profit: \t\t{}", CURRENCY_FORMAT.format(grossProfit));
      LOGGER.info("Cost of Goods Sold Minus Shipping:\t{}", CURRENCY_FORMAT.format(calculateCostOfGoodsSoldWithoutCostOfShipping(orders)));
      LOGGER.info("Gross Profit Ratio: {}", PERCENT_FORMAT.format(grossProfit / totalRevenue));

      LOGGER.info("");
      double etsyOperationalProfit = etsyRevenue - calculateCostOfGoodsSold(etsyOrders) - JAN_ETSY_FEES + sumShipping(etsyOrders);
      double etsyOperationalProfitRatio = etsyOperationalProfit / etsyRevenue;
      double shopifyOperationalProfit = shopifyRevenue - calculateCostOfGoodsSold(shopifyOrders) - JAN_SHOPIFY_FEES + sumShipping(shopifyOrders);
      double shopifyOperationalProfitRatio = shopifyOperationalProfit / shopifyRevenue;
      double operationalProfit = totalRevenue - cogs - JAN_SHOPIFY_FEES - JAN_ETSY_FEES + sumShipping(orders);
      double operationalProfitRatio = operationalProfit / totalRevenue;
      LOGGER.info("Etsy Operational Profit: \t\t\t{}", CURRENCY_FORMAT.format(etsyOperationalProfit));
      LOGGER.info("Etsy Operational Profit Ratio: \t\t{}", PERCENT_FORMAT.format(etsyOperationalProfitRatio));
      LOGGER.info("Shopify Operational Profit: \t\t{}", CURRENCY_FORMAT.format(shopifyOperationalProfit));
      LOGGER.info("Shopify Operational Profit Ratio: \t{}", PERCENT_FORMAT.format(shopifyOperationalProfitRatio));
      LOGGER.info("Toal Operational Profit: \t\t\t{}", CURRENCY_FORMAT.format(operationalProfit));
      LOGGER.info("Toal Operational Profit Ratio: \t\t{}", PERCENT_FORMAT.format(operationalProfitRatio));

   }

   private static double calculateCostOfGoodsSold(Collection<SpindleAndRoseOrder> orders)
   {
      double costOfGoodsSold = 0.0;
      for (SpindleAndRoseOrder order : orders)
      {
         List<ProductInfo> products = order.getProducts();
         for (ProductInfo product : products)
         {
            costOfGoodsSold += product.getCost();
            costOfGoodsSold += product.getShippingCost();
         }
      }
      return costOfGoodsSold;
   }

   private static double calculateCostOfGoodsSoldWithoutCostOfShipping(Collection<SpindleAndRoseOrder> orders)
   {
      double costOfGoodsSold = 0.0;
      for (SpindleAndRoseOrder order : orders)
      {
         List<ProductInfo> products = order.getProducts();
         for (ProductInfo product : products)
         {
            costOfGoodsSold += product.getCost();
         }
      }
      return costOfGoodsSold;
   }

   private static double sumShippingDiscount(Collection<SpindleAndRoseOrder> orders)
   {
      double sum = 0.0;
      for (SpindleAndRoseOrder order : orders)
      {
         sum += order.getShippingDiscount();
      }
      return sum;
   }

   private static double sumDiscount(Collection<SpindleAndRoseOrder> orders)
   {
      double sum = 0.0;
      for (SpindleAndRoseOrder order : orders)
      {
         sum += order.getDiscount();
      }
      return sum;
   }

   /*
   private static void reportSalesByCountry() throws Exception
   {
      //Map<String, ProductInfo> products = new ProductInfoBuilder().readProductInfo();
      Map<String, ProductInfo> products = GoogleSheets.readProductInfo();
      List<SpindleAndRoseOrder> etsyOrders = new EtsyOrdersProcessor().readEtsyOrders(ETSY_FILE, ETSY_ORDER_ITEMS_FILE, products);
      Collection<SpindleAndRoseOrder> shopifyOrders = new ShopifyOrdersProcessor().readShopifyOrders(SHOPIFY_FILE, products);
      List<SpindleAndRoseOrder> orders = new ArrayList<>();
      orders.addAll(etsyOrders);
      orders.addAll(shopifyOrders);

      Map<String, Integer> map = new HashMap<>();
      for (SpindleAndRoseOrder order : orders)
      {
         String country = order.getShipCountry();
         if (country.trim().equals("US"))
         {
            country = "United States";
         }
         Integer count = map.get(country);
         if (count != null)
         {
            Integer newCount = count + 1;
            map.put(country, newCount);
         }
         else
         {
            map.put(country, 1);
         }
      }
      LOGGER.info("Sales By Country:");
      for (Map.Entry<String, Integer> entry : map.entrySet())
      {
         LOGGER.info("Country={} Count={}", entry.getKey(), entry.getValue());
      }
   }
   */

   private static double sumOrderValue(Collection<SpindleAndRoseOrder> orders)
   {
      double sum = 0.0;
      for (SpindleAndRoseOrder order : orders)
      {
         sum += order.getOrderValue();
      }
      return sum;
   }

   private static double sumOrderValue(List<SpindleAndRoseOrder> orders, int quarter)
   {
      double sum = 0.0;
      for (SpindleAndRoseOrder order : orders)
      {
         if (order.getQuarter() == quarter)
         {
            sum += order.getOrderValue();
         }
      }
      return sum;
   }

   private static double sumSalesTax(List<SpindleAndRoseOrder> orders, int quarter)
   {
      double sum = 0.0;
      for (SpindleAndRoseOrder order : orders)
      {
         if (order.getQuarter() == quarter)
         {
            sum += order.getSalesTax();
         }
      }
      return sum;
   }

   private static double sumOrderValue(List<SpindleAndRoseOrder> orders, int quarter, String excludeState)
   {
      double sum = 0.0;
      for (SpindleAndRoseOrder order : orders)
      {
         if (order.getQuarter() == quarter && !order.getShipState().equals(excludeState))
         {
            sum += order.getOrderValue();
         }
      }
      return sum;
   }

   private static double sumShipping(Collection<SpindleAndRoseOrder> orders)
   {
      double sum = 0.0;
      for (SpindleAndRoseOrder order : orders)
      {
         sum += order.getShipping();
      }
      return sum;
   }

   private static double sumShipping(Collection<SpindleAndRoseOrder> orders, int quarter)
   {
      double sum = 0.0;
      for (SpindleAndRoseOrder order : orders)
      {
         if (order.getQuarter() == quarter)
         {
            sum += order.getShipping();
         }
      }
      return sum;
   }

   protected String removeQuotesAndCommasWithinQuotes(String line)
   {
      LOGGER.debug("removeQuotesAndCommaWithinQuotes: incoming line={}", line);
      StringBuilder sb = new StringBuilder();
      char[] chars = line.toCharArray();
      boolean insideQuote = false;
      for (char c : chars)
      {
         if (c == '\"')
         {
            if (insideQuote)
            {
               insideQuote = false;
            }
            else
            {
               insideQuote = true;
            }
         }
         else if (c == ',' && insideQuote)
         {
            // remove this comma
         }
         else
         {
            sb.append(c);
         }
      }
      LOGGER.debug("removeQuotesAndCommaWithinQuotes: outgoing line={}", sb.toString());
      return sb.toString();
   }

   protected Map<String, Integer> createHeader2ColumnNumberMap(String line)
   {
      Map<String, Integer> map = new HashMap<>();
      line = removeQuotesAndCommasWithinQuotes(line);
      String[] split = line.split(",");
      int columnNumber = 0;
      for (String name : split)
      {
         map.put(name,columnNumber);
         columnNumber++;
      }
      return map;
   }
}
