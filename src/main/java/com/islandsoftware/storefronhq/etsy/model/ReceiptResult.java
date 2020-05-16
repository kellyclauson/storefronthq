package com.islandsoftware.storefronhq.etsy.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ReceiptResult
{
   @JsonProperty("order_id")
   private long orderId;

   @JsonProperty("receipt_id")
   private long receiptId;

   @JsonProperty("country_id")
   private int countryId;

   @JsonProperty("total_price")
   private String totalPrice;

   @JsonProperty("total_shipping_cost")
   private String totalShippingCost;

   @JsonProperty("Transactions")
   private Transaction[] transactions;

   public long getReceiptId()
   {
      return receiptId;
   }

   public void setReceiptId(long receiptId)
   {
      this.receiptId = receiptId;
   }

   public long getOrderId()
   {
      return orderId;
   }

   public int getCountryId()
   {
      return countryId;
   }

   public void setCountryId(int countryId)
   {
      this.countryId = countryId;
   }

   public void setOrderId(long orderId)
   {
      this.orderId = orderId;
   }

   public String getTotalPrice()
   {
      return totalPrice;
   }

   public void setTotalPrice(String totalPrice)
   {
      this.totalPrice = totalPrice;
   }

   public String getTotalShippingCost()
   {
      return totalShippingCost;
   }

   public void setTotalShippingCost(String totalShippingCost)
   {
      this.totalShippingCost = totalShippingCost;
   }

   public Transaction[] getTransactions()
   {
      return transactions;
   }

   public void setTransactions(Transaction[] transactions)
   {
      this.transactions = transactions;
   }
}
