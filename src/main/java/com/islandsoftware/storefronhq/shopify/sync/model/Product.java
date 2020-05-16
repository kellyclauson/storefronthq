package com.islandsoftware.storefronhq.shopify.sync.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Product implements Serializable
{
   @JsonProperty("id")
   private Long id;

   @JsonProperty("title")
   private String title;

   @JsonProperty("body_html")
   private String bodyHtml;

   @JsonProperty("vendor")
   private String vendor;

   @JsonProperty("handle")
   private String handle;

   @JsonProperty("tags")
   private String tags;

   @JsonProperty("product_type")
   private String productType;

   @JsonProperty("variants")
   private List<Variant> variants;

   @JsonProperty("images")
   private ShopifyImage[] images;

   @JsonProperty("options")
   private ShopifyOption[] options;

   @JsonProperty("metafields_global_title_tag")
   private String metaFieldsGlobalTitleTag;

   @JsonProperty("metafields_global_description_tag")
   private String metaFieldsGlobalDescriptionTag;

   @JsonIgnore
   private Double price;
   @JsonIgnore
   private Double weight;
   @JsonIgnore
   private String sku;
   @JsonIgnore
   private Integer inventory;

   public Long getId()
   {
      return id;
   }

   public void setId(Long id)
   {
      this.id = id;
   }

   public String getTitle()
   {
      return title;
   }

   public void setTitle(String title)
   {
      this.title = title;
   }

   public String getTags()
   {
      return tags;
   }

   public void setTags(String tags)
   {
      this.tags = tags;
   }

   public String getProductType()
   {
      return productType;
   }

   public void setProductType(String productType)
   {
      this.productType = productType;
   }

   public String getVendor()
   {
      return vendor;
   }

   public void setVendor(String vendor)
   {
      this.vendor = vendor;
   }

   public String getHandle()
   {
      return handle;
   }

   public void setHandle(String handle)
   {
      this.handle = handle;
   }

   public List<Variant> getVariants()
   {
      return variants;
   }

   public void setVariants(List<Variant> variants)
   {
      this.variants = variants;
   }

   public Double getPrice()
   {
      return price;
   }

   public void setPrice(Double price)
   {
      this.price = price;
   }

   public Double getWeight()
   {
      return weight;
   }

   public void setWeight(Double weight)
   {
      this.weight = weight;
   }

   public String getSku()
   {
      return sku;
   }

   public void setSku(String sku)
   {
      this.sku = sku;
   }

   public Integer getInventory()
   {
      return inventory;
   }

   public void setInventory(Integer inventory)
   {
      this.inventory = inventory;
   }

   public ShopifyImage[] getImages()
   {
      return images;
   }

   public void setImages(ShopifyImage[] images)
   {
      this.images = images;
   }

   public ShopifyOption[] getOptions()
   {
      return options;
   }

   public void setOptions(ShopifyOption[] options)
   {
      this.options = options;
   }

   public String getBodyHtml()
   {
      return bodyHtml;
   }

   public void setBodyHtml(String bodyHtml)
   {
      this.bodyHtml = bodyHtml;
   }

   public String getMetaFieldsGlobalTitleTag()
   {
      return metaFieldsGlobalTitleTag;
   }

   public void setMetaFieldsGlobalTitleTag(String metaFieldsGlobalTitleTag)
   {
      this.metaFieldsGlobalTitleTag = metaFieldsGlobalTitleTag;
   }

   public String getMetaFieldsGlobalDescriptionTag()
   {
      return metaFieldsGlobalDescriptionTag;
   }

   public void setMetaFieldsGlobalDescriptionTag(String metaFieldsGlobalDescriptionTag)
   {
      this.metaFieldsGlobalDescriptionTag = metaFieldsGlobalDescriptionTag;
   }

   @Override
   public String toString()
   {
      return "Product{" +
            "id='" + id + '\'' +
            ", title='" + title + '\'' +
            ", productType='" + productType + '\'' +
            ", vendor='" + vendor + '\'' +
            ", handle='" + handle + '\'' +
            ", tags='" + tags + '\'' +
            ", variants=" + variants +
            '}';
   }

   String s = "{\n" +
         "  \"product\": {\n" +
         "    \"id\": 9134667907,\n" +
         "    \"title\": \"Acorn Fabric | Woodland Print | Organic Cotton\",\n" +
         "    \"body_html\": \"<h1><span style=\\\"color: #ea9999;\\\">Organic Woodland Print</span></h1>\\n<p>Medium weight, 100% GOTS-certified organic cotton fabric. 44-45\\\" in width. From Monaluna's Westwood Collection.</p>\\n<p>A fun and unique forest fabric of graphic acorns on caramel brown ground. This fabric is of wonderful smooth quality, perfect for quilting, apparel, pillows, and more!</p>\\n<p>Acorns are 1 1/4\\\" tall.</p>\",\n" +
         "    \"vendor\": \"Monaluna Organic Fabric\",\n" +
         "    \"product_type\": \"Fabric\",\n" +
         "    \"created_at\": \"2016-12-09T09:18:19-07:00\",\n" +
         "    \"handle\": \"acorn-fabric-woodland-print-yellow-brown-fall-fabric-thanksgiving-print-kitchen-decor-organic-fabric-monaluna\",\n" +
         "    \"updated_at\": \"2016-12-14T17:05:07-07:00\",\n" +
         "    \"published_at\": \"2016-12-09T09:18:00-07:00\",\n" +
         "    \"template_suffix\": null,\n" +
         "    \"published_scope\": \"global\",\n" +
         "    \"tags\": \"autumn, organic fabric, thanksgiving, woodland\",\n" +
         "    \"variants\": [\n" +
         "      {\n" +
         "        \"id\": 32011460483,\n" +
         "        \"product_id\": 9134667907,\n" +
         "        \"title\": \"Fat Quarter(18&quot;x22&quot;)\",\n" +
         "        \"price\": \"4.24\",\n" +
         "        \"sku\": \"ML-WW-07-BLT-FQ\",\n" +
         "        \"position\": 1,\n" +
         "        \"grams\": 51,\n" +
         "        \"inventory_policy\": \"deny\",\n" +
         "        \"compare_at_price\": null,\n" +
         "        \"fulfillment_service\": \"manual\",\n" +
         "        \"inventory_management\": \"shopify\",\n" +
         "        \"option1\": \"Fat Quarter(18&quot;x22&quot;)\",\n" +
         "        \"option2\": null,\n" +
         "        \"option3\": null,\n" +
         "        \"created_at\": \"2016-12-09T09:18:19-07:00\",\n" +
         "        \"updated_at\": \"2016-12-14T14:56:56-07:00\",\n" +
         "        \"taxable\": true,\n" +
         "        \"barcode\": \"\",\n" +
         "        \"image_id\": null,\n" +
         "        \"inventory_quantity\": 15,\n" +
         "        \"weight\": 1.8,\n" +
         "        \"weight_unit\": \"oz\",\n" +
         "        \"old_inventory_quantity\": 15,\n" +
         "        \"requires_shipping\": true\n" +
         "      },\n" +
         "      {\n" +
         "        \"id\": 32011460547,\n" +
         "        \"product_id\": 9134667907,\n" +
         "        \"title\": \"1/4 Yard (9&quot;x44&quot;)\",\n" +
         "        \"price\": \"3.86\",\n" +
         "        \"sku\": \"ML-WW-07-BLT-25\",\n" +
         "        \"position\": 2,\n" +
         "        \"grams\": 51,\n" +
         "        \"inventory_policy\": \"deny\",\n" +
         "        \"compare_at_price\": null,\n" +
         "        \"fulfillment_service\": \"manual\",\n" +
         "        \"inventory_management\": \"shopify\",\n" +
         "        \"option1\": \"1/4 Yard (9&quot;x44&quot;)\",\n" +
         "        \"option2\": null,\n" +
         "        \"option3\": null,\n" +
         "        \"created_at\": \"2016-12-09T09:18:19-07:00\",\n" +
         "        \"updated_at\": \"2016-12-14T14:56:56-07:00\",\n" +
         "        \"taxable\": true,\n" +
         "        \"barcode\": \"\",\n" +
         "        \"image_id\": null,\n" +
         "        \"inventory_quantity\": 15,\n" +
         "        \"weight\": 1.8,\n" +
         "        \"weight_unit\": \"oz\",\n" +
         "        \"old_inventory_quantity\": 15,\n" +
         "        \"requires_shipping\": true\n" +
         "      },\n" +
         "      {\n" +
         "        \"id\": 32011460611,\n" +
         "        \"product_id\": 9134667907,\n" +
         "        \"title\": \"1/2 Yard (18&quot;x44&quot;)\",\n" +
         "        \"price\": \"7.71\",\n" +
         "        \"sku\": \"ML-WW-07-BLT-50\",\n" +
         "        \"position\": 3,\n" +
         "        \"grams\": 99,\n" +
         "        \"inventory_policy\": \"deny\",\n" +
         "        \"compare_at_price\": null,\n" +
         "        \"fulfillment_service\": \"manual\",\n" +
         "        \"inventory_management\": \"shopify\",\n" +
         "        \"option1\": \"1/2 Yard (18&quot;x44&quot;)\",\n" +
         "        \"option2\": null,\n" +
         "        \"option3\": null,\n" +
         "        \"created_at\": \"2016-12-09T09:18:19-07:00\",\n" +
         "        \"updated_at\": \"2016-12-14T14:56:56-07:00\",\n" +
         "        \"taxable\": true,\n" +
         "        \"barcode\": \"\",\n" +
         "        \"image_id\": null,\n" +
         "        \"inventory_quantity\": 15,\n" +
         "        \"weight\": 3.5,\n" +
         "        \"weight_unit\": \"oz\",\n" +
         "        \"old_inventory_quantity\": 15,\n" +
         "        \"requires_shipping\": true\n" +
         "      },\n" +
         "      {\n" +
         "        \"id\": 32011460675,\n" +
         "        \"product_id\": 9134667907,\n" +
         "        \"title\": \"3/4 Yard (27&quot;x44&quot;)\",\n" +
         "        \"price\": \"11.57\",\n" +
         "        \"sku\": \"ML-WW-07-BLT-75\",\n" +
         "        \"position\": 4,\n" +
         "        \"grams\": 150,\n" +
         "        \"inventory_policy\": \"deny\",\n" +
         "        \"compare_at_price\": null,\n" +
         "        \"fulfillment_service\": \"manual\",\n" +
         "        \"inventory_management\": \"shopify\",\n" +
         "        \"option1\": \"3/4 Yard (27&quot;x44&quot;)\",\n" +
         "        \"option2\": null,\n" +
         "        \"option3\": null,\n" +
         "        \"created_at\": \"2016-12-09T09:18:19-07:00\",\n" +
         "        \"updated_at\": \"2016-12-14T14:56:56-07:00\",\n" +
         "        \"taxable\": true,\n" +
         "        \"barcode\": \"\",\n" +
         "        \"image_id\": null,\n" +
         "        \"inventory_quantity\": 15,\n" +
         "        \"weight\": 5.3,\n" +
         "        \"weight_unit\": \"oz\",\n" +
         "        \"old_inventory_quantity\": 15,\n" +
         "        \"requires_shipping\": true\n" +
         "      },\n" +
         "      {\n" +
         "        \"id\": 32011460739,\n" +
         "        \"product_id\": 9134667907,\n" +
         "        \"title\": \"1 Yard (36&quot;x44&quot;)\",\n" +
         "        \"price\": \"15.42\",\n" +
         "        \"sku\": \"ML-WW-07-BLT\",\n" +
         "        \"position\": 5,\n" +
         "        \"grams\": 198,\n" +
         "        \"inventory_policy\": \"deny\",\n" +
         "        \"compare_at_price\": null,\n" +
         "        \"fulfillment_service\": \"manual\",\n" +
         "        \"inventory_management\": \"shopify\",\n" +
         "        \"option1\": \"1 Yard (36&quot;x44&quot;)\",\n" +
         "        \"option2\": null,\n" +
         "        \"option3\": null,\n" +
         "        \"created_at\": \"2016-12-09T09:18:19-07:00\",\n" +
         "        \"updated_at\": \"2016-12-12T09:28:45-07:00\",\n" +
         "        \"taxable\": true,\n" +
         "        \"barcode\": null,\n" +
         "        \"image_id\": null,\n" +
         "        \"inventory_quantity\": 15,\n" +
         "        \"weight\": 7,\n" +
         "        \"weight_unit\": \"oz\",\n" +
         "        \"old_inventory_quantity\": 15,\n" +
         "        \"requires_shipping\": true\n" +
         "      }\n" +
         "    ],\n" +
         "    \"options\": [\n" +
         "      {\n" +
         "        \"id\": 10981964867,\n" +
         "        \"product_id\": 9134667907,\n" +
         "        \"name\": \"Length\",\n" +
         "        \"position\": 1,\n" +
         "        \"values\": [\n" +
         "          \"Fat Quarter(18&quot;x22&quot;)\",\n" +
         "          \"1/4 Yard (9&quot;x44&quot;)\",\n" +
         "          \"1/2 Yard (18&quot;x44&quot;)\",\n" +
         "          \"3/4 Yard (27&quot;x44&quot;)\",\n" +
         "          \"1 Yard (36&quot;x44&quot;)\"\n" +
         "        ]\n" +
         "      }\n" +
         "    ],\n" +
         "    \"images\": [\n" +
         "      {\n" +
         "        \"id\": 19023414851,\n" +
         "        \"product_id\": 9134667907,\n" +
         "        \"position\": 1,\n" +
         "        \"created_at\": \"2016-12-09T09:18:19-07:00\",\n" +
         "        \"updated_at\": \"2016-12-14T14:56:46-07:00\",\n" +
         "        \"src\": \"https://cdn.shopify.com/s/files/1/1648/9493/products/il_fullxfull.1055106276_atlw.jpg?v=1481752606\",\n" +
         "        \"variant_ids\": []\n" +
         "      }\n" +
         "    ],\n" +
         "    \"image\": {\n" +
         "      \"id\": 19023414851,\n" +
         "      \"product_id\": 9134667907,\n" +
         "      \"position\": 1,\n" +
         "      \"created_at\": \"2016-12-09T09:18:19-07:00\",\n" +
         "      \"updated_at\": \"2016-12-14T14:56:46-07:00\",\n" +
         "      \"src\": \"https://cdn.shopify.com/s/files/1/1648/9493/products/il_fullxfull.1055106276_atlw.jpg?v=1481752606\",\n" +
         "      \"variant_ids\": []\n" +
         "    }\n" +
         "  }\n" +
         "}";

}
