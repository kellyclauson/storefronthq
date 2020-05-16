package com.islandsoftware.storefronhq.shopify.update;

import java.util.Date;

public class QuickenTransaction
{
   private Date date;
   private String description;

   @Override
   public boolean equals(Object o)
   {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      QuickenTransaction that = (QuickenTransaction) o;

      if (!date.equals(that.date)) return false;
      return description.equals(that.description);
   }

   @Override
   public String toString()
   {
      return "QuickenTransaction{" +
         "date=" + date +
         ", description='" + description + '\'' +
         '}';
   }

   @Override
   public int hashCode()
   {
      int result = date.hashCode();
      result = 31 * result + description.hashCode();
      return result;
   }

   public Date getDate()
   {
      return date;
   }

   public void setDate(Date date)
   {
      this.date = date;
   }

   public String getDescription()
   {
      return description;
   }

   public void setDescription(String description)
   {
      this.description = description;
   }
}
