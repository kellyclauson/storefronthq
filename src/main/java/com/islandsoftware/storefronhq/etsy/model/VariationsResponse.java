package com.islandsoftware.storefronhq.etsy.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VariationsResponse
{
   private int count;
   private List<VariationsResult> results;
   Pagination pagination;

   public int getCount()
   {
      return count;
   }

   public void setCount(int count)
   {
      this.count = count;
   }

   public List<VariationsResult> getResults()
   {
      return results;
   }

   public void setResults(List<VariationsResult> results)
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
