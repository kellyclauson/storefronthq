package com.islandsoftware.storefronhq.tools;

import com.islandsoftware.storefronhq.etsy.EtsyClient;
import com.islandsoftware.storefronhq.etsy.SpindleAndRoseEtsyClient;
import com.islandsoftware.storefronhq.etsy.model.ListingsResult;
import com.islandsoftware.storefronhq.etsy.model.VariationsResponse;
import com.islandsoftware.storefronhq.etsy.model.VariationOption;
import com.islandsoftware.storefronhq.etsy.model.VariationsResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;

public class SaveEtsyVariations
{
   private static final Logger LOGGER = LoggerFactory.getLogger(SaveEtsyVariations.class);

   public static void main(String[] args)
   {
      StringBuilder sb = new StringBuilder();
      sb.append("listingId,weight,value,formattedValue,price\n");

      EtsyClient etsyClient = new SpindleAndRoseEtsyClient();
      Map<String, ListingsResult> listings = etsyClient.listings();
      for (ListingsResult listingsResult : listings.values())
      {
         Long id = listingsResult.getListingId();
         VariationsResponse variations = etsyClient.getVariations(id);
         String text = handleResponse(id, variations, listingsResult.getItemWeight());
         sb.append(text);

      }
      write("c:/src/spindleandrose/etsyListingVariations.csv", sb.toString());
   }

   private static String handleResponse(long listingId, VariationsResponse variations, String weight)
   {
      StringBuilder sb = new StringBuilder();
      List<VariationsResult> results = variations.getResults();
      for (VariationsResult result : results)
      {
         List<VariationOption> options = result.getOptions();
         for (VariationOption option : options)
         {
            sb.append(listingId);
            sb.append(",");
            sb.append(weight);
            sb.append(",");
            sb.append(option.getValue());
            sb.append(",");
            sb.append(option.getFormattedValue());
            sb.append(",");
            sb.append(option.getPrice());
            sb.append("\n");
         }
      }
      return sb.toString();
   }

   private static void write(String filename, String text)
   {
      try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename)))
      {
         bw.write(text);
      }
      catch (Exception e)
      {
         LOGGER.error("write", e);
      }
   }
}
