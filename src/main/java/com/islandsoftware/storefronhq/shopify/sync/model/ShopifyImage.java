package com.islandsoftware.storefronhq.shopify.sync.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShopifyImage
{
   @JsonProperty ("id")
   private long id;
   @JsonProperty ("position")
   private int position;
   @JsonProperty ("src")
   private String src;
   @JsonProperty ("alt")
   private String altText;

   @Override
   public String toString()
   {
      return "ShopifyImage{" +
            "id=" + id +
            ", position='" + position + '\'' +
            ", src='" + src + '\'' +
            ", altText='" + altText + '\'' +
            '}';
   }

   public long getId()
   {
      return id;
   }

   public void setId(long id)
   {
      this.id = id;
   }

   public String getSrc()
   {
      return src;
   }

   public void setSrc(String src)
   {
      this.src = src;
   }

   public String getAltText()
   {
      return altText;
   }

   public void setAltText(String altText)
   {
      this.altText = altText;
   }

   public int getPosition()
   {
      return position;
   }

   public void setPosition(int position)
   {
      this.position = position;
   }
}
