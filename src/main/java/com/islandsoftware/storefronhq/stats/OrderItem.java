package com.islandsoftware.storefronhq.stats;

public class OrderItem
{
   private long orderId;
   private String title;
   private String variation;
   private int quantity;

   @Override
   public boolean equals(Object o)
   {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      OrderItem orderItem = (OrderItem) o;

      if (!title.equals(orderItem.title)) return false;
      return variation.equals(orderItem.variation);
   }

   @Override
   public int hashCode()
   {
      int result = title.hashCode();
      result = 31 * result + variation.hashCode();
      return result;
   }

   @Override
   public String toString()
   {
      return "OrderItem{" +
         "orderId='" + orderId + '\'' +
         "title='" + title + '\'' +
         ", variation='" + variation + '\'' +
         ", quantity=" + quantity +
         '}';
   }

   public long getOrderId()
   {
      return orderId;
   }

   public void setOrderId(long orderId)
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

   public String getVariation()
   {
      return variation;
   }

   public void setVariation(String variation)
   {
      this.variation = variation;
   }

   public int getQuantity()
   {
      return quantity;
   }

   public void setQuantity(int quantity)
   {
      this.quantity = quantity;
   }
}
