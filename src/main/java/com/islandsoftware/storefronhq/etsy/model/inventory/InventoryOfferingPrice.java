package com.islandsoftware.storefronhq.etsy.model.inventory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class InventoryOfferingPrice
{
   @JsonProperty("amount")
   private Integer amount;
   @JsonProperty("divisor")
   private Integer divisor;
   @JsonProperty("currency_code")
   private String currencyCode;
   @JsonProperty("currency_formatted_short")
   private String currencyFormattedShort;
   @JsonProperty("currency_formatted_long")
   private String currencyFormattedLong;
   @JsonProperty("currency_formatted_raw")
   private String currencyFormattedRaw;

   public Integer getAmount()
   {
      return amount;
   }

   public void setAmount(Integer amount)
   {
      this.amount = amount;
   }

   public Integer getDivisor()
   {
      return divisor;
   }

   public void setDivisor(Integer divisor)
   {
      this.divisor = divisor;
   }

   public String getCurrencyCode()
   {
      return currencyCode;
   }

   public void setCurrencyCode(String currencyCode)
   {
      this.currencyCode = currencyCode;
   }

   public String getCurrencyFormattedShort()
   {
      return currencyFormattedShort;
   }

   public void setCurrencyFormattedShort(String currencyFormattedShort)
   {
      this.currencyFormattedShort = currencyFormattedShort;
   }

   public String getCurrencyFormattedLong()
   {
      return currencyFormattedLong;
   }

   public void setCurrencyFormattedLong(String currencyFormattedLong)
   {
      this.currencyFormattedLong = currencyFormattedLong;
   }

   public String getCurrencyFormattedRaw()
   {
      return currencyFormattedRaw;
   }

   public void setCurrencyFormattedRaw(String currencyFormattedRaw)
   {
      this.currencyFormattedRaw = currencyFormattedRaw;
   }

   /*
   "price":{
   "amount":712,
         "divisor":100,
         "currency_code":"USD",
         "currency_formatted_short":"$7.12",
         "currency_formatted_long":"$7.12 USD",
         "currency_formatted_raw":"7.12"
   }
   */
}
