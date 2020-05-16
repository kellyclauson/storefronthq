package com.islandsoftware.storefronhq.shopify.sync.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class SmartCollectionList
{
   @JsonProperty("smart_collections")
   private List<SmartCollection> collections;

   public List<SmartCollection> getCollections()
   {
      return collections;
   }

   public void setCollections(List<SmartCollection> collections)
   {
      this.collections = collections;
   }
}
