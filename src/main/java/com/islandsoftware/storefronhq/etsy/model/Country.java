package com.islandsoftware.storefronhq.etsy.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Country
{
   @JsonProperty("country_id")
   private int countryId;
   @JsonProperty("iso_country_code")
   private String countryCode;
   @JsonProperty("name")
   private String name;

   public int getCountryId()
   {
      return countryId;
   }

   public void setCountryId(int countryId)
   {
      this.countryId = countryId;
   }

   public String getCountryCode()
   {
      return countryCode;
   }

   public void setCountryCode(String countryCode)
   {
      this.countryCode = countryCode;
   }

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }
}
