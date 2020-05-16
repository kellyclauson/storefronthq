package com.islandsoftware.storefronhq.stats;

import java.util.List;

public class OrderStats
{
   private String channel;
   private long time;
   private long orderId;
   private double orderValue;
   private double orderShippingValue;
   private double ourCost;
   private double costToShipToUs;
   private double weightInOunces;
   private double weightWithPackaging;
   private int unitsSold;
   private double fee;
   private double costToShipToCustomer;
   private double costOfGoods;
   private double revenue;
   private double profit;
   private double profitRatio;
   private double feeRatio;
   private String country;
   private List<OrderItem> orderItems;

   @Override
   public String toString()
   {
      return "OrderStats{" +
         "channel='" + channel + '\'' +
         ", time=" + time +
         ", orderId=" + orderId +
         ", orderValue=" + orderValue +
         ", orderShippingValue=" + orderShippingValue +
         ", ourCost=" + ourCost +
         ", costToShipToUs=" + costToShipToUs +
         ", orderItems=" + orderItems +
         ", weightInOunces=" + weightInOunces +
         ", weightWithPackaging=" + weightWithPackaging +
         ", unitsSold=" + unitsSold +
         ", fee=" + fee +
         ", costToShipToCustomer=" + costToShipToCustomer +
         ", costOfGoods=" + costOfGoods +
         ", revenue=" + revenue +
         ", profit=" + profit +
         ", profitRatio=" + profitRatio +
         ", feeRatio=" + feeRatio +
         ", country=" + country +
         '}';
   }

   public String getChannel()
   {
      return channel;
   }

   public void setChannel(String channel)
   {
      this.channel = channel;
   }

   public long getTime()
   {
      return time;
   }

   public void setTime(long time)
   {
      this.time = time;
   }

   public long getOrderId()
   {
      return orderId;
   }

   public void setOrderId(long orderId)
   {
      this.orderId = orderId;
   }

   public double getOrderValue()
   {
      return orderValue;
   }

   public void setOrderValue(double orderValue)
   {
      this.orderValue = orderValue;
   }

   public double getOrderShippingValue()
   {
      return orderShippingValue;
   }

   public void setOrderShippingValue(double orderShippingValue)
   {
      this.orderShippingValue = orderShippingValue;
   }

   public double getOurCost()
   {
      return ourCost;
   }

   public void setOurCost(double ourCost)
   {
      this.ourCost = ourCost;
   }

   public double getCostToShipToUs()
   {
      return costToShipToUs;
   }

   public void setCostToShipToUs(double costToShipToUs)
   {
      this.costToShipToUs = costToShipToUs;
   }

   public double getWeightInOunces()
   {
      return weightInOunces;
   }

   public void setWeightInOunces(double weightInOunces)
   {
      this.weightInOunces = weightInOunces;
   }

   public double getWeightWithPackaging()
   {
      return weightWithPackaging;
   }

   public void setWeightWithPackaging(double weightWithPackaging)
   {
      this.weightWithPackaging = weightWithPackaging;
   }

   public int getUnitsSold()
   {
      return unitsSold;
   }

   public void setUnitsSold(int unitsSold)
   {
      this.unitsSold = unitsSold;
   }

   public double getFee()
   {
      return fee;
   }

   public void setFee(double fee)
   {
      this.fee = fee;
   }

   public double getCostToShipToCustomer()
   {
      return costToShipToCustomer;
   }

   public void setCostToShipToCustomer(double costToShipToCustomer)
   {
      this.costToShipToCustomer = costToShipToCustomer;
   }

   public double getCostOfGoods()
   {
      return costOfGoods;
   }

   public void setCostOfGoods(double costOfGoods)
   {
      this.costOfGoods = costOfGoods;
   }

   public double getRevenue()
   {
      return revenue;
   }

   public void setRevenue(double revenue)
   {
      this.revenue = revenue;
   }

   public double getProfit()
   {
      return profit;
   }

   public void setProfit(double profit)
   {
      this.profit = profit;
   }

   public double getProfitRatio()
   {
      return profitRatio;
   }

   public void setProfitRatio(double profitRatio)
   {
      this.profitRatio = profitRatio;
   }

   public double getFeeRatio()
   {
      return feeRatio;
   }

   public void setFeeRatio(double feeRatio)
   {
      this.feeRatio = feeRatio;
   }

   public String getCountry()
   {
      return country;
   }

   public void setCountry(String country)
   {
      this.country = country;
   }

   public List<OrderItem> getOrderItems()
   {
      return orderItems;
   }

   public void setOrderItems(List<OrderItem> orderItems)
   {
      this.orderItems = orderItems;
   }
}
