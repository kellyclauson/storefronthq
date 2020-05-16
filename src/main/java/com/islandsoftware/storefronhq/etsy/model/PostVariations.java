package com.islandsoftware.storefronhq.etsy.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostVariations
{
   @JsonProperty("variations")
   private Variation[] variations;

   public Variation[] getVariations()
   {
      return variations;
   }

   public void setVariations(Variation[] variations)
   {
      this.variations = variations;
   }
}
