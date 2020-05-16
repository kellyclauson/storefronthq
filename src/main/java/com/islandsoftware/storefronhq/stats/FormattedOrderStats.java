package com.islandsoftware.storefronhq.stats;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.islandsoftware.storefronhq.tools.Utils;

import java.util.List;

@JsonPropertyOrder({
   "channel",
   "time",
   "revenue",
   "profit",
   "profitRatio",
   "fee",
   "feeRatio",
   "orderValue",
   "orderShippingValue",
   "costOfGoods",
   "ourCost",
   "costToShipToUs",
   "costToShipToCustomer",
   "unitsSold",
   "weightInOunces",
   "weightWithPackaging",
   "country",
   "orderId"
})
public class FormattedOrderStats
{
   private String channel;
   private String time;
   private long orderId;
   private String orderValue;
   private String orderShippingValue;
   private String ourCost;
   private String costToShipToUs;
   private double weightInOunces;
   private double weightWithPackaging;
   private int unitsSold;
   private String fee;
   private String costToShipToCustomer;
   private String costOfGoods;
   private String revenue;
   private String profit;
   private String profitRatio;
   private String feeRatio;
   private String country;
   private List<OrderItem> orderItems;

   public FormattedOrderStats(OrderStats orderStats)
   {
      channel = orderStats.getChannel();
      time = Utils.millisToDateString(orderStats.getTime());
      orderId = orderStats.getOrderId();
      orderValue = Utils.currencyFormat(orderStats.getOrderValue());
      orderShippingValue = Utils.currencyFormat(orderStats.getOrderShippingValue());
      ourCost = Utils.currencyFormat(orderStats.getOurCost());
      costToShipToUs = Utils.currencyFormat(orderStats.getCostToShipToUs());
      weightInOunces = orderStats.getWeightInOunces();
      weightWithPackaging = orderStats.getWeightWithPackaging();
      unitsSold = orderStats.getUnitsSold();
      fee = Utils.currencyFormat(orderStats.getFee());
      costToShipToCustomer = Utils.currencyFormat(orderStats.getCostToShipToCustomer());
      costOfGoods = Utils.currencyFormat(orderStats.getCostOfGoods());
      revenue = Utils.currencyFormat(orderStats.getRevenue());
      profit =  Utils.currencyFormat(orderStats.getProfit());
      profitRatio = Utils.percentageFormat(orderStats.getProfitRatio());
      feeRatio = Utils.percentageFormat(orderStats.getFeeRatio());
      country = orderStats.getCountry();
      orderItems = orderStats.getOrderItems();
   }

   @Override
   public String toString()
   {
      return "{" +
         "channel='" + channel + '\'' +
         ", time='" + time + '\'' +
         ", revenue='" + revenue + '\'' +
         ", profit='" + profit + '\'' +
         ", profitRatio='" + profitRatio + '\'' +
         ", fee='" + fee + '\'' +
         ", feeRatio='" + feeRatio + '\'' +
         ", country='" + country + '\'' +
         ", orderId=" + orderId +
         ", orderValue='" + orderValue + '\'' +
         ", orderShippingValue='" + orderShippingValue + '\'' +
         ", ourCost='" + ourCost + '\'' +
         ", costToShipToUs='" + costToShipToUs + '\'' +
         ", weightInOunces=" + weightInOunces +
         ", weightWithPackaging=" + weightWithPackaging +
         ", unitsSold=" + unitsSold +
         ", costToShipToCustomer='" + costToShipToCustomer + '\'' +
         ", costOfGoods='" + costOfGoods + '\'' +
         ", orderItems='" + orderItems + '\'' +
         '}';
   }

   public String getChannel()
   {
      return channel;
   }

   public String getTime()
   {
      return time;
   }

   public long getOrderId()
   {
      return orderId;
   }

   public String getOrderValue()
   {
      return orderValue;
   }

   public String getOrderShippingValue()
   {
      return orderShippingValue;
   }

   public String getOurCost()
   {
      return ourCost;
   }

   public String getCostToShipToUs()
   {
      return costToShipToUs;
   }

   public double getWeightInOunces()
   {
      return weightInOunces;
   }

   public double getWeightWithPackaging()
   {
      return weightWithPackaging;
   }

   public int getUnitsSold()
   {
      return unitsSold;
   }

   public String getFee()
   {
      return fee;
   }

   public String getCostToShipToCustomer()
   {
      return costToShipToCustomer;
   }

   public String getCostOfGoods()
   {
      return costOfGoods;
   }

   public String getRevenue()
   {
      return revenue;
   }

   public String getProfit()
   {
      return profit;
   }

   public String getProfitRatio()
   {
      return profitRatio;
   }

   public String getFeeRatio()
   {
      return feeRatio;
   }

   public String getCountry()
   {
      return country;
   }

   public List<OrderItem> getOrderItems()
   {
      return orderItems;
   }
}
