package com.islandsoftware.storefronhq.orderprocessing;

import java.util.Date;

public class ProductInfo
{
   private Date dateAdded;
   private String etsyTitle;
   private String shopifyTitle;
   private String variation;
   private String sku;
   private String vendor;
   private double cost;
   private double shippingCost;
   private double priceOverride;
   //private String autoUpdateCost = "false";
   private String imageAltText;
   private String metaDescription;
   private String tags;

   @Override
   public String toString()
   {
      return "ProductInfo{" +
         "dateAdded='" + dateAdded + '\'' +
         "etsyTitle='" + etsyTitle + '\'' +
         ", shopifyTitle='" + shopifyTitle + '\'' +
         ", variation='" + variation + '\'' +
         ", sku='" + sku + '\'' +
         ", cost=" + cost +
         ", shippingCost=" + shippingCost +
         ", priceOverride=" + priceOverride +
         ", vendor=" + vendor +
         ", tags=" + tags +
         //", autoUpdateCost=" + autoUpdateCost +
         ", imageAltText=" + imageAltText +
         ", metaDescription=" + metaDescription +
         '}';
   }

   public Date getDateAdded()
   {
      return dateAdded;
   }

   public void setDateAdded(Date dateAdded)
   {
      this.dateAdded = dateAdded;
   }

   public String getEtsyTitle()
   {
      return etsyTitle;
   }

   public void setEtsyTitle(String etsyTitle)
   {
      this.etsyTitle = etsyTitle;
   }

   public String getShopifyTitle()
   {
      return shopifyTitle;
   }

   public void setShopifyTitle(String shopifyTitle)
   {
      this.shopifyTitle = shopifyTitle;
   }

   public String getVariation()
   {
      return variation;
   }

   public void setVariation(String variation)
   {
      this.variation = variation;
   }

   public String getSku()
   {
      return sku;
   }

   public void setSku(String sku)
   {
      this.sku = sku;
   }

   public double getCost()
   {
      return cost;
   }

   public void setCost(double cost)
   {
      this.cost = cost;
   }

   public double getShippingCost()
   {
      return shippingCost;
   }

   public void setShippingCost(double shippingCost)
   {
      this.shippingCost = shippingCost;
   }

   public double getPriceOverride()
   {
      return priceOverride;
   }

   public void setPriceOverride(double priceOverride)
   {
      this.priceOverride = priceOverride;
   }

   public String getVendor()
   {
      return vendor;
   }

   public void setVendor(String vendor)
   {
      this.vendor = vendor;
   }

   /*
   public String getAutoUpdateCost()
   {
      return autoUpdateCost;
   }

   public void setAutoUpdateCost(String autoUpdateCost)
   {
      this.autoUpdateCost = autoUpdateCost;
   }
   */

   public String getImageAltText()
   {
      return imageAltText;
   }

   public void setImageAltText(String imageAltText)
   {
      this.imageAltText = imageAltText;
   }

   public String getMetaDescription()
   {
      return metaDescription;
   }

   public void setMetaDescription(String metaDescription)
   {
      this.metaDescription = metaDescription;
   }

   public String getTags()
   {
      return tags;
   }

   public void setTags(String tags)
   {
      this.tags = tags;
   }
}
