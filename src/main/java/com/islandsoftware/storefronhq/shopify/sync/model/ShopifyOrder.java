package com.islandsoftware.storefronhq.shopify.sync.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ShopifyOrder
{
   public static void main(String[] args)
   {
      try
      {
         ShopifyOrder order = new ObjectMapper().readValue(exampleOrderJson, ShopifyOrder.class);
         System.out.println("paidOrder: productId=" + order.getLineItems()[0].getProductId());
         System.out.println("paidOrder: title=" + order.getLineItems()[0].getTitle());
         System.out.println("paidOrder: variantId=" + order.getLineItems()[0].getVariantId());
         System.out.println("paidOrder: variantTitle=" + order.getLineItems()[0].getVariantTitle());
         System.out.println("paidOrder: quantity=" + order.getLineItems()[0].getQuantity());
         System.out.println("paidOrder: weight=" + order.getWeight());
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   private long id;
   private String totalPrice;
   private String subTotal;
   private String tax;
   private int weight;
   private ShippingAddress shippingAddress;

   @JsonProperty ("line_items")
   private LineItems[] lineItems;

   public LineItems[] getLineItems()
   {
      return lineItems;
   }

   @JsonProperty ("total_price")
   public String getTotalPrice()
   {
      return totalPrice;
   }

   public void setTotalPrice(String totalPrice)
   {
      this.totalPrice = totalPrice;
   }

   @JsonProperty ("subtotal_price")
   public String getSubTotal()
   {
      return subTotal;
   }

   public void setSubTotal(String subTotal)
   {
      this.subTotal = subTotal;
   }

   @JsonProperty ("total_tax")
   public String getTax()
   {
      return tax;
   }

   public void setTax(String tax)
   {
      this.tax = tax;
   }

   @JsonProperty ("total_weight")
   public int getWeight()
   {
      return weight;
   }

   public void setWeight(int weight)
   {
      this.weight = weight;
   }
   public void setLineItems(LineItems[] lineItems)
   {
      this.lineItems = lineItems;
   }

   @JsonProperty ("id")
   public long getId()
   {
      return id;
   }

   public void setId(long id)
   {
      this.id = id;
   }

   @JsonProperty ("shipping_address")
   public ShippingAddress getShippingAddress()
   {
      return shippingAddress;
   }

   public void setShippingAddress(ShippingAddress shippingAddress)
   {
      this.shippingAddress = shippingAddress;
   }

   private static String exampleOrderJson =
         "{\"id\":4319647555," +
         "\"email\":\"krclauson@gmail.com\"," +
         "\"closed_at\":null," +
         "\"created_at\":\"2016-12-25T13:13:51-07:00\"," +
               "\"updated_at\":\"2016-12-25T13:13:51-07:00\"," +
               "\"number\":1," +
               "\"note\":\"\"," +
               "\"token\":\"567863cdc425887e3acc30da38cda910\"," +
               "\"gateway\":\"bogus\"," +
               "\"test\":true," +
               "\"total_price\":\"18.63\"," +
               "\"subtotal_price\":\"7.95\"," +
               "\"total_weight\":99," +
               "\"total_tax\":\"0.68\"," +
               "\"taxes_included\":false," +
               "\"currency\":\"USD\"," +
               "\"financial_status\":\"paid\"," +
               "\"confirmed\":true," +
               "\"total_discounts\":\"0.00\"," +
               "\"total_line_items_price\":\"7.95\"," +
               "\"cart_token\":\"e20fc6b11fd7507088314f932d8c607a\"," +
               "\"buyer_accepts_marketing\":false," +
               "\"name\":\"#1001\"," +
               "\"referring_site\":\"\"," +
               "\"landing_site\":\"\\/?key=a0c823a39300105d6fac12d34016640c095e63a413acfbfb129df128a7bac1ed\"," +
               "\"cancelled_at\":null," +
               "\"cancel_reason\":null," +
               "\"total_price_usd\":\"18.63\"," +
               "\"checkout_token\":\"e9491c99cdfdca97ee42ec688c452298\"," +
               "\"reference\":null," +
               "\"user_id\":null," +
               "\"location_id\":null," +
               "\"source_identifier\":null," +
               "\"source_url\":null," +
               "\"processed_at\":\"2016-12-25T13:13:51-07:00\"," +
               "\"device_id\":null," +
               "\"browser_ip\":\"73.203.83.194\"," +
               "\"landing_site_ref\":null," +
               "\"order_number\":1001," +
               "\"discount_codes\":[]," +
               "\"note_attributes\":[]," +
               "\"payment_gateway_names\":[\"bogus\"]," +
               "\"processing_method\":\"direct\"," +
               "\"checkout_id\":9776984579," +
               "\"source_name\":\"web\"," +
               "\"fulfillment_status\":null," +
               "\"tax_lines\":[" +
                  "{\"title\":\"CO State Tax\",\"price\":\"0.23\",\"rate\":0.029}," +
                  "{\"title\":\"Adams County Tax\",\"price\":\"0.06\",\"rate\":0.0075}," +
                  "{\"title\":\"Brighton Municipal Tax\",\"price\":\"0.39\",\"rate\":0.0485}]," +
               "\"tags\":\"\"," +
               "\"contact_email\":\"krclauson@gmail.com\"," +
               "\"order_status_url\":\"https:\\/\\/checkout.shopify.com\\/16489493\\/checkouts\\/e9491c99cdfdca97ee42ec688c452298\\/thank_you_token?key=04b2cb7264b2c5f67f74ba602fa6977f\"," +
               "\"line_items\":[" +
                  "{\"id\":8049506115," +
                  "\"variant_id\":32011416515," +
                  "\"title\":\"Bird Fabric | Charley Harper Print | Animal Prints | Organic Cotton Fabric | Birch Organic Fabric| Lily Pads | Herons | Heron Art\"," +
                  "\"quantity\":1," +
                  "\"price\":\"7.95\"," +
                  "\"grams\":99," +
                  "\"sku\":null," +
                  "\"variant_title\":\"1\\/2 Yard (18\\u0026quot; x 44\\u0026quot;)\"," +
                  "\"vendor\":\"Birch Organic Fabrics\"," +
                  "\"fulfillment_service\":\"manual\"," +
                  "\"product_id\":9134659523," +
                  "\"requires_shipping\":true," +
                  "\"taxable\":true," +
                  "\"gift_card\":false," +
                  "\"name\":\"Bird Fabric | Charley Harper Print | Animal Prints | Organic Cotton Fabric | Birch Organic Fabric| Lily Pads | Herons | Heron Art - 1\\/2 Yard (18\\u0026quot; x 44\\u0026quot;)\"," +
                  "\"variant_inventory_management\":\"shopify\"," +
                  "\"properties\":[]," +
                  "\"product_exists\":true," +
                  "\"fulfillable_quantity\":1," +
                  "\"total_discount\":\"0.00\"," +
                  "\"fulfillment_status\":null," +
                  "\"tax_lines\":[" +
                     "{\"title\":\"CO State Tax\",\"price\":\"0.23\",\"rate\":0.029}," +
                     "{\"title\":\"Adams County Tax\",\"price\":\"0.06\",\"rate\":0.0075}," +
                     "{\"title\":\"Brighton Municipal Tax\",\"price\":\"0.39\",\"rate\":0.0485}]," +
                  "\"origin_location\":{\"id\":2450018115,\"country_code\":\"US\",\"province_code\":\"CO\",\"name\":\"SpindleAndRose\",\"address1\":\"13995 Cook St\",\"address2\":\"\",\"city\":\"Thornton\",\"zip\":\"80602\"}," +
                  "\"destination_location\":{\"id\":2497320771,\"country_code\":\"US\",\"province_code\":\"CO\",\"name\":\"Kelly Clauson\",\"address1\":\"13995 Cook St\",\"address2\":\"\",\"city\":\"Thornton\",\"zip\":\"80602\"}}]," +
                  "\"shipping_lines\":[{\"id\":3475560259,\"title\":\"Standard Shipping\",\"price\":\"10.00\",\"code\":\"Standard Shipping\",\"source\":\"shopify\",\"phone\":null,\"requested_fulfillment_service_id\":null,\"delivery_category\":null,\"carrier_identifier\":null,\"tax_lines\":[]}],\"billing_address\":{\"first_name\":\"Kelly\",\"address1\":\"13995 Cook St\",\"phone\":null,\"city\":\"Thornton\",\"zip\":\"80602\",\"province\":\"Colorado\",\"country\":\"United States\",\"last_name\":\"Clauson\",\"address2\":\"\",\"company\":null,\"latitude\":39.949667,\"longitude\":-104.948675,\"name\":\"Kelly Clauson\",\"country_code\":\"US\",\"province_code\":\"CO\"},\"shipping_address\":{\"first_name\":\"Kelly\",\"address1\":\"13995 Cook St\",\"phone\":null,\"city\":\"Thornton\",\"zip\":\"80602\",\"province\":\"Colorado\",\"country\":\"United States\",\"last_name\":\"Clauson\",\"address2\":\"\",\"company\":null,\"latitude\":39.949667,\"longitude\":-104.948675,\"name\":\"Kelly Clauson\",\"country_code\":\"US\",\"province_code\":\"CO\"},\"fulfillments\":[],\"client_details\":{\"browser_ip\":\"73.203.83.194\",\"accept_language\":\"en-US,en;q=0.8\",\"user_agent\":\"Mozilla\\/5.0 (Windows NT 6.1; WOW64) AppleWebKit\\/537.36 (KHTML, like Gecko) Chrome\\/53.0.2785.101 Safari\\/537.36\",\"session_hash\":\"414639388801d228b2f77f4374b8e66e\",\"browser_width\":1663,\"browser_height\":920},\"refunds\":[],\"payment_details\":{\"credit_card_bin\":\"1\",\"avs_result_code\":null,\"cvv_result_code\":null,\"credit_card_number\":\"•••• •••• •••• 1\",\"credit_card_company\":\"Bogus\"},\"customer\":{\"id\":5094235843,\"email\":\"krclauson@gmail.com\",\"accepts_marketing\":false,\"created_at\":\"2016-12-25T13:12:50-07:00\",\"updated_at\":\"2016-12-25T13:13:51-07:00\",\"first_name\":\"Kelly\",\"last_name\":\"Clauson\",\"orders_count\":0,\"state\":\"disabled\",\"total_spent\":\"0.00\",\"last_order_id\":null,\"note\":null,\"verified_email\":true,\"multipass_identifier\":null,\"tax_exempt\":false,\"tags\":\"\",\"last_order_name\":null,\"default_address\":{\"id\":5173742915,\"first_name\":\"Kelly\",\"last_name\":\"Clauson\",\"company\":null,\"address1\":\"13995 Cook St\",\"address2\":\"\",\"city\":\"Thornton\",\"province\":\"Colorado\",\"country\":\"United States\",\"zip\":\"80602\",\"phone\":null,\"name\":\"Kelly Clauson\",\"province_code\":\"CO\",\"country_code\":\"US\",\"country_name\":\"United States\",\"default\":true}}}";
}
