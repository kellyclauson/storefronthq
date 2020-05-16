package com.islandsoftware.storefronhq.etsy.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EtsyImage
{
   @JsonProperty("url_fullxfull")
   private String url;

   @JsonProperty ("listing_image_id")
   private Long listingImageId;
   @JsonProperty ("listing_id")
   private Long listingId;
   private Integer rank;

   public String getUrl()
   {
      return url;
   }

   public void setUrl(String url)
   {
      this.url = url;
   }

   public Long getListingImageId()
   {
      return listingImageId;
   }

   public void setListingImageId(Long listingImageId)
   {
      this.listingImageId = listingImageId;
   }

   public Long getListingId()
   {
      return listingId;
   }

   public void setListingId(Long listingId)
   {
      this.listingId = listingId;
   }

   public Integer getRank()
   {
      return rank;
   }

   public void setRank(Integer rank)
   {
      this.rank = rank;
   }
}
