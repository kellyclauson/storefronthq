package com.islandsoftware.storefronhq.shopify.sync.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Variant
{
   private Long id;
   @JsonProperty("product_id")
   private String productId;
   private String title;
   private String price;
   private String sku;
   private String position;
   private String grams;
   @JsonProperty("inventory_policy")
   private String inventoryPolicy;
   @JsonProperty("compare_at_price")
   private String compareAtPrice;
   @JsonProperty("fulfillment_service")
   private String fulfillmentService;
   @JsonProperty("inventory_management")
   private String inventoryManagement;

   private String option1;
   private String option2;
   private String option3;
   @JsonProperty("created_at")
   private String createdAt;
   @JsonProperty("updated_at")
   private String upadtedAt;
   private String taxable;
   private String barcode;
   private String image_id;

   @JsonProperty("inventory_quantity")
   private Integer inventoryQuantity;
   private String weight;
   @JsonProperty("weight_unit")
   private String weightUnit;

   @JsonProperty("old_inventory_quantity")
   private String oldInventoryQuantity;
   @JsonProperty("requires_shipping")
   private String requiresShipping;

   public Long getId()
   {
      return id;
   }

   public void setId(Long id)
   {
      this.id = id;
   }

   public String getProductId()
   {
      return productId;
   }

   public void setProductId(String productId)
   {
      this.productId = productId;
   }

   public String getTitle()
   {
      return title;
   }

   public void setTitle(String title)
   {
      this.title = title;
   }

   public String getPrice()
   {
      return price;
   }

   public void setPrice(String price)
   {
      this.price = price;
   }

   public String getSku()
   {
      return sku;
   }

   public void setSku(String sku)
   {
      this.sku = sku;
   }

   public String getPosition()
   {
      return position;
   }

   public void setPosition(String position)
   {
      this.position = position;
   }

   public String getGrams()
   {
      return grams;
   }

   public void setGrams(String grams)
   {
      this.grams = grams;
   }

   public String getInventoryPolicy()
   {
      return inventoryPolicy;
   }

   public void setInventoryPolicy(String inventoryPolicy)
   {
      this.inventoryPolicy = inventoryPolicy;
   }

   public String getCompareAtPrice()
   {
      return compareAtPrice;
   }

   public void setCompareAtPrice(String compareAtPrice)
   {
      this.compareAtPrice = compareAtPrice;
   }

   public String getFulfillmentService()
   {
      return fulfillmentService;
   }

   public void setFulfillmentService(String fulfillmentService)
   {
      this.fulfillmentService = fulfillmentService;
   }

   public String getInventoryManagement()
   {
      return inventoryManagement;
   }

   public void setInventoryManagement(String inventoryManagement)
   {
      this.inventoryManagement = inventoryManagement;
   }

   public String getOption1()
   {
      return option1;
   }

   public void setOption1(String option1)
   {
      this.option1 = option1;
   }

   public String getOption2()
   {
      return option2;
   }

   public void setOption2(String option2)
   {
      this.option2 = option2;
   }

   public String getOption3()
   {
      return option3;
   }

   public void setOption3(String option3)
   {
      this.option3 = option3;
   }

   public String getCreatedAt()
   {
      return createdAt;
   }

   public void setCreatedAt(String createdAt)
   {
      this.createdAt = createdAt;
   }

   public String getUpadtedAt()
   {
      return upadtedAt;
   }

   public void setUpadtedAt(String upadtedAt)
   {
      this.upadtedAt = upadtedAt;
   }

   public String getTaxable()
   {
      return taxable;
   }

   public void setTaxable(String taxable)
   {
      this.taxable = taxable;
   }

   public String getBarcode()
   {
      return barcode;
   }

   public void setBarcode(String barcode)
   {
      this.barcode = barcode;
   }

   public String getImage_id()
   {
      return image_id;
   }

   public void setImage_id(String image_id)
   {
      this.image_id = image_id;
   }

   public Integer getInventoryQuantity()
   {
      return inventoryQuantity;
   }

   public void setInventoryQuantity(Integer inventoryQuantity)
   {
      this.inventoryQuantity = inventoryQuantity;
   }

   public String getWeight()
   {
      return weight;
   }

   public void setWeight(String weight)
   {
      this.weight = weight;
   }

   public String getWeightUnit()
   {
      return weightUnit;
   }

   public void setWeightUnit(String weightUnit)
   {
      this.weightUnit = weightUnit;
   }

   public String getOldInventoryQuantity()
   {
      return oldInventoryQuantity;
   }

   public void setOldInventoryQuantity(String oldInventoryQuantity)
   {
      this.oldInventoryQuantity = oldInventoryQuantity;
   }

   public String getRequiresShipping()
   {
      return requiresShipping;
   }

   public void setRequiresShipping(String requiresShipping)
   {
      this.requiresShipping = requiresShipping;
   }

   @Override
   public String toString()
   {
      return "Variant{" +
            "id='" + id + '\'' +
            ", productId='" + productId + '\'' +
            ", title='" + title + '\'' +
            ", price='" + price + '\'' +
            ", sku='" + sku + '\'' +
            ", position='" + position + '\'' +
            ", grams='" + grams + '\'' +
            ", inventoryPolicy='" + inventoryPolicy + '\'' +
            ", compareAtPrice='" + compareAtPrice + '\'' +
            ", fulfillmentService='" + fulfillmentService + '\'' +
            ", inventoryManagement='" + inventoryManagement + '\'' +
            ", option1='" + option1 + '\'' +
            ", option2='" + option2 + '\'' +
            ", option3='" + option3 + '\'' +
            ", createdAt='" + createdAt + '\'' +
            ", upadtedAt='" + upadtedAt + '\'' +
            ", taxable='" + taxable + '\'' +
            ", barcode='" + barcode + '\'' +
            ", image_id='" + image_id + '\'' +
            ", inventoryQuantity='" + inventoryQuantity + '\'' +
            ", weight='" + weight + '\'' +
            ", weightUnit='" + weightUnit + '\'' +
            ", oldInventoryQuantity='" + oldInventoryQuantity + '\'' +
            ", requiresShipping='" + requiresShipping + '\'' +
            '}';
   }
}
