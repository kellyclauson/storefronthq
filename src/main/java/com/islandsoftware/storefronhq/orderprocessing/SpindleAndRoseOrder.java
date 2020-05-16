package com.islandsoftware.storefronhq.orderprocessing;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SpindleAndRoseOrder
{
   private String channel;
   private String orderId;
   private Date date;
   private int quarter;
   private int year;
   private String shipState;
   private String shipCountry;
   private double orderValue;
   private double shipping;
   private double salesTax;
   private double discount;
   private double shippingDiscount;
   private double cardProcessingFee;
   private double refund;
   private List<ProductInfo> products;

   public SpindleAndRoseOrder()
   {
      this.products = new ArrayList<>();
   }

   public SpindleAndRoseOrder(Date date)
   {
      this.date = date;
      this.year = obtainYear(date);
      this.quarter = obtainQuarter(date);
      this.products = new ArrayList<>();
   }

   @Override
   public String toString()
   {
      return "SpindleAndRoseOrder{" +
            "channel=" + channel +
            "orderId=" + orderId +
            ", date=" + date +
            ", quarter=" + quarter +
            ", year=" + year +
            ", shipState='" + shipState + '\'' +
            ", shipCountry='" + shipCountry + '\'' +
            ", orderValue=" + orderValue +
            ", discount=" + discount +
            ", shipping=" + shipping +
            ", shippingDiscount=" + shippingDiscount +
            ", salesTax=" + salesTax +
            ", cardProcessingFee=" + cardProcessingFee +
            ", refund=" + refund +
            ", products=" + products +
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

   public String getOrderId()
   {
      return orderId;
   }

   public Date getDate()
   {
      return date;
   }

   public int getQuarter()
   {
      return quarter;
   }

   public int getYear()
   {
      return year;
   }

   public String getShipState()
   {
      return shipState;
   }

   public String getShipCountry()
   {
      return shipCountry;
   }

   public double getOrderValue()
   {
      return orderValue;
   }

   public double getShipping()
   {
      return shipping;
   }

   public double getSalesTax()
   {
      return salesTax;
   }

   public double getDiscount()
   {
      return discount;
   }

   public double getShippingDiscount()
   {
      return shippingDiscount;
   }

   public void setShippingDiscount(double shippingDiscount)
   {
      this.shippingDiscount = shippingDiscount;
   }

   public void setOrderId(String orderId)
   {
      this.orderId = orderId;
   }

   public void setDate(Date date)
   {
      this.date = date;
      if (this.year == 0)
      {
         this.year = obtainYear(date);
      }
      if (this.quarter == 0)
      {
         this.quarter = obtainQuarter(date);
      }
   }

   public void setQuarter(int quarter)
   {
      this.quarter = quarter;
   }

   public void setYear(int year)
   {
      this.year = year;
   }

   public void setShipState(String shipState)
   {
      this.shipState = shipState;
   }

   public void setShipCountry(String shipCountry)
   {
      this.shipCountry = shipCountry;
   }

   public void setOrderValue(double orderValue)
   {
      this.orderValue = orderValue;
   }

   public void setShipping(double shipping)
   {
      this.shipping = shipping;
   }

   public void setSalesTax(double salesTax)
   {
      this.salesTax = salesTax;
   }

   public void setDiscount(double discount)
   {
      this.discount = discount;
   }

   public double getCardProcessingFee()
   {
      return cardProcessingFee;
   }

   public void setCardProcessingFee(double cardProcessingFee)
   {
      this.cardProcessingFee = cardProcessingFee;
   }

   public List<ProductInfo> getProducts()
   {
      return products;
   }

   public double getRefund()
   {
      return refund;
   }

   public void setRefund(double refund)
   {
      this.refund = refund;
   }

   private int obtainYear(Date date)
   {
      Calendar cal = Calendar.getInstance();
      cal.setTime(date);
      return cal.get(Calendar.YEAR);
   }

   private int obtainQuarter(Date date)
   {
      Calendar cal = Calendar.getInstance();
      cal.setTime(date);
      int month = cal.get(Calendar.MONTH);
      if (month < 3)
      {
         return 1;
      }
      if (month < 6)
      {
         return 2;
      }
      if (month < 9)
      {
         return 3;
      }
      return 4;
   }

}
