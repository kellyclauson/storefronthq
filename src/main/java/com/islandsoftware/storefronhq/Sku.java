package com.islandsoftware.storefronhq;

public class Sku
{
   private String sku;
   private Long etsyId;
   private String etsyTitle;
   private Long shopifyId;
   private String shopifyTitle;

   public Sku(String sku)
   {
      this.sku = sku;
   }

   public Sku(String sku, Long etsyId, String etsyTitle, Long shopifyId, String shopifyTitle)
   {
      this.sku = sku;
      this.etsyId = etsyId;
      this.etsyTitle = etsyTitle;
      this.shopifyId = shopifyId;
      this.shopifyTitle = shopifyTitle;
   }

   @Override
   public String toString()
   {
      return "sku=" + sku +
            ",etsyId=" + etsyId +
            ",etsyTitle=" + etsyTitle +
            ",shopifyId=" + shopifyId +
            ",shopifyTitle=" + shopifyTitle;
   }

   @Override
   public boolean equals(Object o)
   {
      if (this == o)
      {
         return true;
      }
      if (o == null || getClass() != o.getClass())
      {
         return false;
      }

      Sku sku1 = (Sku) o;

      return sku.equals(sku1.sku);
   }

   @Override
   public int hashCode()
   {
      return sku.hashCode();
   }

   public void setSku(String sku)
   {
      this.sku = sku;
   }

   public String getSku()
   {
      return sku;
   }

   public Long getEtsyId()
   {
      return etsyId;
   }

   public void setEtsyId(Long etsyId)
   {
      this.etsyId = etsyId;
   }

   public String getEtsyTitle()
   {
      return etsyTitle;
   }

   public void setEtsyTitle(String etsyTitle)
   {
      this.etsyTitle = etsyTitle;
   }

   public Long getShopifyId()
   {
      return shopifyId;
   }

   public void setShopifyId(Long shopifyId)
   {
      this.shopifyId = shopifyId;
   }

   public String getShopifyTitle()
   {
      return shopifyTitle;
   }

   public void setShopifyTitle(String shopifyTitle)
   {
      this.shopifyTitle = shopifyTitle;
   }
}
