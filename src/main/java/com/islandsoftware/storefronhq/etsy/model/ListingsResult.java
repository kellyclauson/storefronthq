package com.islandsoftware.storefronhq.etsy.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringEscapeUtils;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ListingsResult
{
   private String title;
   private String state;
   private String[] sku;
   private String[] tags;
   private Integer quantity;
   @JsonProperty ("listing_id")
   private Long listingId;
   private String description;
   private String price;
   @JsonProperty ("item_weight")
   private String itemWeight;
   @JsonProperty ("Images")
   private EtsyImage[] images;
   @JsonProperty ("Variations")
   private Variation[] variations;
   @JsonProperty ("shipping_template_id")
   private Long shippingTemplateId;
   @JsonProperty ("category_id")
   private Long categoryId;
   private String[] materials;
   @JsonProperty ("taxonomy_id")
   private Long taxonomyId;
   @JsonProperty ("user_id")
   private Long userId;

   public String getState()
   {
      return state;
   }

   public void setState(String state)
   {
      this.state = state;
   }

   public String getTitle()
   {
      return title;
   }

   public void setTitle(String title)
   {
      this.title = StringEscapeUtils.unescapeXml(title);
   }

   public String[] getSku()
   {
      return sku;
   }

   public void setSku(String[] sku)
   {
      this.sku = sku;
   }

   public String[] getTags()
   {
      return tags;
   }

   public void setTags(String[] tags)
   {
      this.tags = tags;
   }

   public Integer getQuantity()
   {
      return quantity;
   }

   public void setQuantity(Integer quantity)
   {
      this.quantity = quantity;
   }

   public Long getListingId()
   {
      return listingId;
   }

   public void setListingId(Long listingId)
   {
      this.listingId = listingId;
   }

   public String getDescription()
   {
      return description;
   }

   public void setDescription(String description)
   {
      this.description = description;
   }

   public String getPrice()
   {
      return price;
   }

   public void setPrice(String price)
   {
      this.price = price;
   }

   public String getItemWeight()
   {
      return itemWeight;
   }

   public void setItemWeight(String itemWeight)
   {
      this.itemWeight = itemWeight;
   }

   public EtsyImage[] getImages()
   {
      return images;
   }

   public void setImages(EtsyImage[] images)
   {
      this.images = images;
   }

   public Variation[] getVariations()
   {
      return variations;
   }

   public void setVariations(Variation[] variations)
   {
      this.variations = variations;
   }

   public Long getShippingTemplateId()
   {
      return shippingTemplateId;
   }

   public void setShippingTemplateId(Long shippingTemplateId)
   {
      this.shippingTemplateId = shippingTemplateId;
   }

   public Long getCategoryId()
   {
      return categoryId;
   }

   public void setCategoryId(Long categoryId)
   {
      this.categoryId = categoryId;
   }

   public String[] getMaterials()
   {
      return materials;
   }

   public void setMaterials(String[] materials)
   {
      this.materials = materials;
   }

   public Long getTaxonomyId()
   {
      return taxonomyId;
   }

   public void setTaxonomyId(Long taxonomyId)
   {
      this.taxonomyId = taxonomyId;
   }

   public Long getUserId()
   {
      return userId;
   }

   public void setUserId(Long userId)
   {
      this.userId = userId;
   }
}
