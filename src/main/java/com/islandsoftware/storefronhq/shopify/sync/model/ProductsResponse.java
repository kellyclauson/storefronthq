package com.islandsoftware.storefronhq.shopify.sync.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ProductsResponse
{
   @JsonProperty("products")
   private List<Product> products;

   @Override
   public String toString()
   {
      return "ProductsResponse{" +
            "products=" + products +
            '}';
   }

   public List<Product> getProducts()
   {
      return products;
   }
}
