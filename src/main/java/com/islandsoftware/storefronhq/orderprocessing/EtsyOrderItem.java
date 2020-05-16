package com.islandsoftware.storefronhq.orderprocessing;

public class EtsyOrderItem
{
   private String orderId;
   private String title;
   private int quantity;
   private String variation;
   private double price;

   public String getOrderId()
   {
      return orderId;
   }

   public void setOrderId(String orderId)
   {
      this.orderId = orderId;
   }

   public String getTitle()
   {
      return title;
   }

   public void setTitle(String title)
   {
      this.title = title;
   }

   public int getQuantity()
   {
      return quantity;
   }

   public void setQuantity(int quantity)
   {
      this.quantity = quantity;
   }

   public String getVariation()
   {
      return variation;
   }

   public void setVariation(String variation)
   {
      this.variation = variation;
   }

   public double getPrice()
   {
      return price;
   }

   public void setPrice(double price)
   {
      this.price = price;
   }
}
