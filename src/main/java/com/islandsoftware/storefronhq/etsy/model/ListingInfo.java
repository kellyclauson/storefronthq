package com.islandsoftware.storefronhq.etsy.model;

import java.util.List;

public class ListingInfo
{
   private List<VariationOption> options;
   private String weight;

   public List<VariationOption> getOptions()
   {
      return options;
   }

   public void setOptions(List<VariationOption> options)
   {
      this.options = options;
   }

   public String getWeight()
   {
      return weight;
   }

   public void setWeight(String weight)
   {
      this.weight = weight;
   }
}
