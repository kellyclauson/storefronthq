package com.islandsoftware.storefronhq.shopify.sync.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ShippingAddress
{
   @JsonProperty("country")
   private String country;

   @JsonProperty("country_code")
   private String countryCode;

   public String getCountry()
   {
      return country;
   }

   public void setCountry(String country)
   {
      this.country = country;
   }

   public String getCountryCode()
   {
      return countryCode;
   }

   public void setCountryCode(String countryCode)
   {
      this.countryCode = countryCode;
   }
}
