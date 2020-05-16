package com.islandsoftware.storefronhq.etsy.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ListingsResponse
{
   private int count;
   private ListingsResult[] results;
   Pagination pagination;

   public int getCount()
   {
      return count;
   }

   public void setCount(int count)
   {
      this.count = count;
   }

   public ListingsResult[] getResults()
   {
      return results;
   }

   public void setResults(ListingsResult[] results)
   {
      this.results = results;
   }

   public Pagination getPagination()
   {
      return pagination;
   }

   public void setPagination(Pagination pagination)
   {
      this.pagination = pagination;
   }
}
