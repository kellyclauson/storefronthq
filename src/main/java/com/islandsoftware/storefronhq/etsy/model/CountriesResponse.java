package com.islandsoftware.storefronhq.etsy.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CountriesResponse
{
   private int count;
   @JsonProperty("results")
   private Country[] countries;

   public int getCount()
   {
      return count;
   }

   public void setCount(int count)
   {
      this.count = count;
   }

   public Country[] getCountries()
   {
      return countries;
   }

   public void setCountries(Country[] countries)
   {
      this.countries = countries;
   }
}
