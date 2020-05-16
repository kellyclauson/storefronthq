package com.islandsoftware.storefronhq.shopify.sync.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LineItems
{
   @JsonProperty ("product_id")
   private long productId;
   private String title;
   @JsonProperty ("variant_title")
   private String variantTitle;
   @JsonProperty ("variant_id")
   private long variantId;
   private int quantity;

   public long getProductId()
   {
      return productId;
   }

   public void setProductId(long productId)
   {
      this.productId = productId;
   }

   public String getTitle()
   {
      return title;
   }

   public void setTitle(String title)
   {
      this.title = title;
   }

   public String getVariantTitle()
   {
      return variantTitle;
   }

   public void setVariantTitle(String variantTitle)
   {
      this.variantTitle = variantTitle;
   }

   public long getVariantId()
   {
      return variantId;
   }

   public void setVariantId(long variantId)
   {
      this.variantId = variantId;
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
