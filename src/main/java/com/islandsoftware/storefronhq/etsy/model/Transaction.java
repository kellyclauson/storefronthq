package com.islandsoftware.storefronhq.etsy.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringEscapeUtils;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Transaction
{
   @JsonProperty("transaction_id")
   private long transactionId;

   @JsonProperty("listing_id")
   private long listingId;

   private String title;

   private int quantity;

   private TransactionVariation[] variations;

   public long getTransactionId()
   {
      return transactionId;
   }

   public void setTransactionId(long transactionId)
   {
      this.transactionId = transactionId;
   }

   public long getListingId()
   {
      return listingId;
   }

   public void setListingId(long listingId)
   {
      this.listingId = listingId;
   }

   public String getTitle()
   {
      return title;
   }

   public void setTitle(String title)
   {
      this.title = StringEscapeUtils.unescapeXml(title);
   }

   public int getQuantity()
   {
      return quantity;
   }

   public void setQuantity(int quantity)
   {
      this.quantity = quantity;
   }

   public TransactionVariation[] getVariations()
   {
      return variations;
   }

   public void setVariations(TransactionVariation[] variations)
   {
      this.variations = variations;
   }
}
