package com.islandsoftware.storefronhq.tools;

import com.islandsoftware.storefronhq.etsy.EtsyClient;
import com.islandsoftware.storefronhq.etsy.SpindleAndRoseEtsyClient;
import com.islandsoftware.storefronhq.etsy.model.ListingInfo;
import com.islandsoftware.storefronhq.etsy.model.ListingsResponse;
import com.islandsoftware.storefronhq.etsy.model.ListingsResult;
import com.islandsoftware.storefronhq.etsy.model.Variation;
import com.islandsoftware.storefronhq.etsy.model.VariationOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class RestoreEtsyVariations
{
   private static final Logger LOGGER = LoggerFactory.getLogger(RestoreEtsyVariations.class);

   public static void main(String[] args)
   {
      EtsyClient etsyClient = new SpindleAndRoseEtsyClient();
      Map<Long, ListingInfo> map = read("c:/src/spindleandrose/etsyListingVariations.csv");
      for (Entry<Long, ListingInfo> entry : map.entrySet())
      {
         ListingsResponse listing = etsyClient.getListing(entry.getKey(), "Variations");
         if (listing != null)
         {
            ListingsResult[] results = listing.getResults();
            if (results != null)
            {
               for (ListingsResult result : results)
               {
                  Variation[] variations = result.getVariations();
                  if (variations != null)
                  {
                     for (Variation variation : variations)
                     {
                        VariationOption[] voptions = variation.getOptions();
                        if (voptions != null)
                        {
                           if (voptions.length == 1 && voptions[0].getValue().startsWith("1/2"))
                           {
                              // okay, this is a listing that we converted to 1/2 Yard only, we need to restore it
                              String options = toString(entry.getValue().getOptions());
                              LOGGER.info(options);
                              etsyClient.updateVariation(entry.getKey(), options);
                              Map<String, String> params = new HashMap<>();
                              params.put("item_weight", entry.getValue().getWeight());
                              params.put("quantity", String.valueOf(result.getQuantity() / 2));
                              params.put("description", restoreDescription(result.getDescription()));
                              etsyClient.updateListing(entry.getKey(), params);
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static String restoreDescription(String description)
   {
      String s = description.replaceFirst("##.*\n", "We offer free shipping on orders over \\$50 (US only) and more product options at www.spindleandrose.com!");
      s = s.replaceFirst("##.*\n", "");
      return s;
   }

   private static String toString(List<VariationOption> options)
   {
            /*
            POST /v2/listings/:listing_id/variations
            variations=[{"property_id":506, "value":"1/2 Yard", "is_available":true, "price": 100}]
             */
      StringBuilder sb = new StringBuilder();
      sb.append("[");
      int i = 0;
      for (VariationOption option : options)
      {
         sb.append("{");
         sb.append("\"property_id\":506,");
         sb.append(" \"value\":");
         sb.append("\"");
         sb.append(option.getValue().replaceAll("&quot;", ""));
         sb.append("\",");
         sb.append(" \"formatted_value\":");
         sb.append("\"");
         sb.append(option.getFormattedValue().replaceAll("&quot;", ""));
         sb.append("\",");
         sb.append(" \"is_available\":true,");
         sb.append(" \"price\":");
         sb.append(option.getPrice());
         sb.append("}");
         if (++i < options.size())
         {
            sb.append(",");
         }
      }
      sb.append("]");
      return sb.toString();
   }

   private static Map<Long, ListingInfo> read(String filename)
   {
      Map<Long, ListingInfo> options = new HashMap<>();
      try (BufferedReader br = new BufferedReader(new FileReader(filename)))
      {
         String line = br.readLine();
         int i = 0;
         while(line != null)
         {
            if (i++ == 0)
            {
               // skip header row
            }
            else
            {
               VariationOption option = new VariationOption();
               String[] split = line.split(",");
               long id = Long.parseLong(split[0]);
               String weight = split[1];
               option.setValue(split[2]);
               option.setFormattedValue(split[3]);
               option.setPrice(Double.parseDouble(split[4]));
               ListingInfo info = options.get(id);
               if (info == null)
               {
                  info = new ListingInfo();
                  info.setWeight(weight);
                  info.setOptions(new ArrayList<>());
                  options.put(id, info);
               }
               info.getOptions().add(option);
            }
            line = br.readLine();
         }
      }
      catch (Exception e)
      {
         LOGGER.error("read", e);
      }
      return options;
   }


}
