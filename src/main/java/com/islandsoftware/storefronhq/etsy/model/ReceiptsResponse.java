package com.islandsoftware.storefronhq.etsy.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ReceiptsResponse
{
   private int count;
   private ReceiptResult[] results;
   private ReceiptsParams params;

   public int getCount()
   {
      return count;
   }

   public void setCount(int count)
   {
      this.count = count;
   }

   public ReceiptResult[] getResults()
   {
      return results;
   }

   public void setResults(ReceiptResult[] results)
   {
      this.results = results;
   }

   public ReceiptsParams getParams()
   {
      return params;
   }

   public void setParams(ReceiptsParams params)
   {
      this.params = params;
   }

   String json = "{" +
         "\"count\":4," +
         "\"results\":[" +
         "{" +
         "\"receipt_id\":1158954360," +
         "\"receipt_type\":0," +
         "\"order_id\":467072008," +
         "\"seller_user_id\":88614744," +
         "\"buyer_user_id\":5864139," +
         "\"creation_tsz\":1482870196," +
         "\"last_modified_tsz\":1482870215," +
         "\"name\":\"Laura Means\"," +
         "\"first_line\":\"114 Liberty Pkwy Apt B1\"," +
         "\"second_line\":\"\"," +
         "\"city\":\"Saint Robert\"," +
         "\"state\":\"MO\"," +
         "\"zip\":\"65584-4876\"," +
         "\"country_id\":209," +
         "\"payment_method\":\"cc\"," +
         "\"payment_email\":\"\"," +
         "\"message_from_seller\":\"--Hello~ Thank you for your order! --\"," +
         "\"message_from_buyer\":\"\"," +
         "\"was_paid\":true," +
         "\"total_tax_cost\":\"0.00\"," +
         "\"total_vat_cost\":\"0.00\"," +
         "\"total_price\":\"28.80\"," +
         "\"total_shipping_cost\":\"9.45\"," +
         "\"currency_code\":\"USD\"," +
         "\"message_from_payment\":null," +
         "\"was_shipped\":false," +
         "\"buyer_email\":\"lkmeans@gmail.com\"," +
         "\"seller_email\":\"lexiclauson@gmail.com\"," +
         "\"discount_amt\":\"0.00\"," +
         "\"subtotal\":\"28.80\"," +
         "\"grandtotal\":\"38.25\"," +
         "\"adjusted_grandtotal\":\"38.25\"," +
         "\"shipping_tracking_code\":null," +
         "\"shipping_tracking_url\":null," +
         "\"shipping_carrier\":null," +
         "\"shipping_note\":null," +
         "\"shipping_notification_date\":null," +
         "\"shipments\":[]," +
         "\"has_local_delivery\":false," +
         "\"shipping_details\":" +
            "{\"can_mark_as_shipped\":true," +
            "\"was_shipped\":false," +
            "\"is_future_shipment\":true," +
            "\"not_shipped_state_display\":\"Not Shipped\"," +
            "\"has_upgrade\":true," +
            "\"upgrade_name\":\"USPS Priority Mail\"," +
            "\"shipping_method\":\"USPS Priority Mail\"}," +
         "\"transparent_price_message\":\"\"," +
         "\"show_channel_badge\":false," +
         "\"channel_badge_suffix_string\":\"on Create\"}," +
         "{\"receipt_id\":1158846178,\"receipt_type\":0,\"order_id\":486393387,\"seller_user_id\":88614744,\"buyer_user_id\":5802169,\"creation_tsz\":1482818355,\"last_modified_tsz\":1482860721,\"name\":\"Jill Ivie\",\"first_line\":\"5536 Hero Drive\",\"second_line\":\"\",\"city\":\"Austin\",\"state\":\"TX\",\"zip\":\"78735\",\"country_id\":209,\"payment_method\":\"cc\",\"payment_email\":\"\",\"message_from_seller\":\"------------------------------------------------------------------------------------------------------------------\\r\\nHello~ Thank you for your order! \\r\\n\\r\\nSpindle & Rose is a dream come true for me - cutting and packaging beautiful fabric for fellow creatives! It is my first priority that you are very happy with your purchase. Please don&#39;t hesitate to contact me with any questions or concerns so that we can make your shopping experience as smooth as possible. \\r\\n\\r\\nSPINDLE & ROSE Wishes You The Warmest Regards & Hey...Happy Sewing ~\\r\\n&lt;3 Alexa\\r\\n\\r\\nP.S. We love to see pictures of your projects ; )\\r\\n------------------------------------------------------------------------------------------------------------------\",\"message_from_buyer\":null,\"was_paid\":true,\"total_tax_cost\":\"0.00\",\"total_vat_cost\":\"0.00\",\"total_price\":\"75.92\",\"total_shipping_cost\":\"0.00\",\"currency_code\":\"USD\",\"message_from_payment\":null,\"was_shipped\":true,\"buyer_email\":\"jill.ivie@gmail.com\",\"seller_email\":\"lexiclauson@gmail.com\",\"discount_amt\":\"0.00\",\"subtotal\":\"75.92\",\"grandtotal\":\"75.92\",\"adjusted_grandtotal\":\"75.92\",\"shipping_tracking_code\":\"9305589843900349413719\",\"shipping_tracking_url\":\"https:\\/\\/www.etsy.com\\/your\\/orders\\/1158846178\\/order_tracking\\/85953042307\",\"shipping_carrier\":\"USPS\",\"shipping_note\":\"\",\"shipping_notification_date\":1482860721,\"shipments\":[{\"receipt_shipping_id\":85953042307,\"mailing_date\":1482858000,\"carrier_name\":\"USPS\",\"tracking_code\":\"9305589843900349413719\",\"major_tracking_state\":\"Shipped\",\"current_step\":\"shipped\",\"current_step_date\":1482860721,\"mail_class\":\"USPS Priority Mail\",\"buyer_note\":\"\",\"notification_date\":1482860721,\"is_local_delivery\":false,\"local_delivery_id\":null,\"tracking_url\":\"https:\\/\\/www.etsy.com\\/your\\/orders\\/1158846178\\/order_tracking\\/85953042307?mutv=0kYbpvY17r0_cF-jkENrWG-3BlvqNcRellmkUStM152w0dxAFYSYNNH-RElFAnUm8aECndCAzsdG9H8591eIHUBKLV3sbL60vbdgjK-hV8QkvgrA2GAhQjmcJUlRpD-FbC\"}],\"has_local_delivery\":false,\"shipping_details\":{\"can_mark_as_shipped\":false,\"was_shipped\":true,\"is_future_shipment\":false,\"shipment_date\":1482860721,\"not_shipped_state_display\":\"Not Shipped\",\"shipping_method\":\"Standard Shipping\"},\"transparent_price_message\":\"\",\"show_channel_badge\":false,\"channel_badge_suffix_string\":\"on Create\"},{\"receipt_id\":1158842028,\"receipt_type\":0,\"order_id\":486389191,\"seller_user_id\":88614744,\"buyer_user_id\":6106912,\"creation_tsz\":1482816349,\"last_modified_tsz\":1482859004,\"name\":\"PRISCILLA COHAN\",\"first_line\":\"PO Box 636\",\"second_line\":null,\"city\":\"Lyons\",\"state\":\"CO\",\"zip\":\"80540\",\"country_id\":209,\"payment_method\":\"cc\",\"payment_email\":\"\",\"message_from_seller\":\"------------------------------------------------------------------------------------------------------------------\\r\\nHello~ Thank you for your order! \\r\\n\\r\\nSpindle & Rose is a dream come true for me - cutting and packaging beautiful fabric for fellow creatives! It is my first priority that you are very happy with your purchase. Please don&#39;t hesitate to contact me with any questions or concerns so that we can make your shopping experience as smooth as possible. \\r\\n\\r\\nSPINDLE & ROSE Wishes You The Warmest Regards & Hey...Happy Sewing ~\\r\\n&lt;3 Alexa\\r\\n\\r\\nP.S. We love to see pictures of your projects ; )\\r\\n------------------------------------------------------------------------------------------------------------------\",\"message_from_buyer\":\"Hi Alexa, My sister sent me a gift card. I am happy to be able to buy some of your great fabrics. :-)\",\"was_paid\":true,\"total_tax_cost\":\"2.24\",\"total_vat_cost\":\"0.00\",\"total_price\":\"40.49\",\"total_shipping_cost\":\"6.80\",\"currency_code\":\"USD\",\"message_from_payment\":null,\"was_shipped\":true,\"buyer_email\":\"priscillacohan@gmail.com\",\"seller_email\":\"lexiclauson@gmail.com\",\"discount_amt\":\"0.00\",\"subtotal\":\"40.49\",\"grandtotal\":\"49.53\",\"adjusted_grandtotal\":\"49.53\",\"shipping_tracking_code\":\"9305589843900324425287\",\"shipping_tracking_url\":\"https:\\/\\/www.etsy.com\\/your\\/orders\\/1158842028\\/order_tracking\\/83308984390\",\"shipping_carrier\":\"USPS\",\"shipping_note\":\"\",\"shipping_notification_date\":1482859004,\"shipments\":[{\"receipt_shipping_id\":83308984390,\"mailing_date\":1482858000,\"carrier_name\":\"USPS\",\"tracking_code\":\"9305589843900324425287\",\"major_tracking_state\":\"Shipped\",\"current_step\":\"shipped\",\"current_step_date\":1482859004,\"mail_class\":\"USPS Priority Mail\",\"buyer_note\":\"\",\"notification_date\":1482859004,\"is_local_delivery\":false,\"local_delivery_id\":null,\"tracking_url\":\"https:\\/\\/www.etsy.com\\/your\\/orders\\/1158842028\\/order_tracking\\/83308984390?mutv=0k7OzedRcjRoRK4xA_x9Fv0ztc4nHDkED67vj4fPPCNmFE-0eDo12Kjpqp3tLAfTRFVPj54x5rolNKTN5MzB-oeaadjXg9rsz6FUwHYZpYm0zrdu-9NtSJsUOL_maSp0kn\"}],\"has_local_delivery\":false,\"shipping_details\":{\"can_mark_as_shipped\":false,\"was_shipped\":true,\"is_future_shipment\":false,\"shipment_date\":1482859004,\"not_shipped_state_display\":\"Not Shipped\",\"has_upgrade\":true,\"upgrade_name\":\"USPS Priority Mail\",\"shipping_method\":\"USPS Priority Mail\"},\"transparent_price_message\":\"\",\"show_channel_badge\":false,\"channel_badge_suffix_string\":\"on Create\"},{\"receipt_id\":1158752802,\"receipt_type\":0,\"order_id\":486296115,\"seller_user_id\":88614744,\"buyer_user_id\":28160566,\"creation_tsz\":1482790158,\"last_modified_tsz\":1482856719,\"name\":\"Caron Moore\",\"first_line\":\"940 F Ave\",\"second_line\":null,\"city\":\"Cayce\",\"state\":\"SC\",\"zip\":\"29033\",\"country_id\":209,\"payment_method\":\"cc\",\"payment_email\":\"\",\"message_from_seller\":\"------------------------------------------------------------------------------------------------------------------\\r\\nHello~ Thank you for your order! \\r\\n\\r\\nSpindle & Rose is a dream come true for me - cutting and packaging beautiful fabric for fellow creatives! It is my first priority that you are very happy with your purchase. Please don&#39;t hesitate to contact me with any questions or concerns so that we can make your shopping experience as smooth as possible. \\r\\n\\r\\nSPINDLE & ROSE Wishes You The Warmest Regards & Hey...Happy Sewing ~\\r\\n&lt;3 Alexa\\r\\n\\r\\nP.S. We love to see pictures of your projects ; )\\r\\n------------------------------------------------------------------------------------------------------------------\",\"message_from_buyer\":\"\",\"was_paid\":true,\"total_tax_cost\":\"0.00\",\"total_vat_cost\":\"0.00\",\"total_price\":\"52.78\",\"total_shipping_cost\":\"10.25\",\"currency_code\":\"USD\",\"message_from_payment\":null,\"was_shipped\":true,\"buyer_email\":\"mooreloved77@gmail.com\",\"seller_email\":\"lexiclauson@gmail.com\",\"discount_amt\":\"0.00\",\"subtotal\":\"52.78\",\"grandtotal\":\"63.03\",\"adjusted_grandtotal\":\"52.78\",\"shipping_tracking_code\":\"9305589843900349303539\",\"shipping_tracking_url\":\"https:\\/\\/www.etsy.com\\/your\\/orders\\/1158752802\\/order_tracking\\/85933016397\",\"shipping_carrier\":\"USPS\",\"shipping_note\":\"\",\"shipping_notification_date\":1482856718,\"shipments\":[{\"receipt_shipping_id\":85933016397,\"mailing_date\":1482858000,\"carrier_name\":\"USPS\",\"tracking_code\":\"9305589843900349303539\",\"major_tracking_state\":\"Shipped\",\"current_step\":\"shipped\",\"current_step_date\":1482856718,\"mail_class\":\"USPS Priority Mail\",\"buyer_note\":\"\",\"notification_date\":1482856718,\"is_local_delivery\":false,\"local_delivery_id\":null,\"tracking_url\":\"https:\\/\\/www.etsy.com\\/your\\/orders\\/1158752802\\/order_tracking\\/85933016397?mutv=0kdUfvspJEu5CsHf922l_E-OfUFn5-KUXQRrbdgsEhh0EWh065lvT37zzpYrbRFxHETw1GlXxnx8fVeg09YTkONfmTbyb65d-_dNmtCleqTH-4VG5l8AO7rJHpPe4DJrZT\"}],\"has_local_delivery\":false,\"shipping_details\":{\"can_mark_as_shipped\":false,\"was_shipped\":true,\"is_future_shipment\":false,\"shipment_date\":1482856719,\"not_shipped_state_display\":\"Not Shipped\",\"has_upgrade\":true,\"upgrade_name\":\"USPS Priority Mail\",\"shipping_method\":\"USPS Priority Mail\"},\"transparent_price_message\":\"\",\"show_channel_badge\":false,\"channel_badge_suffix_string\":\"on Create\"}]," +

         "\"params\":{" +
         "\"shop_id\":\"13195966\"," +
         "\"min_created\":\"1482788568\"," +
         "\"max_created\":null," +
         "\"min_last_modified\":null," +
         "\"max_last_modified\":null," +
         "\"limit\":25," +
         "\"offset\":0," +
         "\"page\":null," +
         "\"was_paid\":null," +
         "\"was_shipped\":null}," +
         "\"type\":\"Receipt\"," +
         "\"pagination\":{" +
         "\"effective_limit\":25," +
         "\"effective_offset\":0," +
         "\"next_offset\":null," +
         "\"effective_page\":1," +
         "\"next_page\":null}}";
}
