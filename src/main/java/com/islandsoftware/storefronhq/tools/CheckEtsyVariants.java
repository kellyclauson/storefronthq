package com.islandsoftware.storefronhq.tools;

import com.islandsoftware.storefronhq.etsy.EtsyClient;
import com.islandsoftware.storefronhq.etsy.model.ListingsResponse;
import com.islandsoftware.storefronhq.etsy.model.ListingsResult;
import com.islandsoftware.storefronhq.etsy.SpindleAndRoseEtsyClient;
import com.islandsoftware.storefronhq.etsy.model.Variation;
import com.islandsoftware.storefronhq.etsy.model.VariationOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class CheckEtsyVariants
{
   private static final Logger LOGGER = LoggerFactory.getLogger(CheckEtsyVariants.class);

   public static void main(String[] args)
   {
      EtsyClient etsyClient = new SpindleAndRoseEtsyClient();
      Map<String, ListingsResult> listings = etsyClient.listings();
      LOGGER.info("found {} etsy listings", listings.size());
      int count = 0;
      for (ListingsResult listing : listings.values())
      {
         Utils.sleep(500L);
         long listingId = listing.getListingId();
         ListingsResponse listingResponse = etsyClient.getListing(listingId, "Variations");
         ListingsResult[] results = listingResponse.getResults();
         for (ListingsResult result : results)
         {
            Variation[] variations = result.getVariations();
            if (variations != null)
            {
               for (Variation variation : variations)
               {
                  VariationOption[] options = variation.getOptions();
                  if (options != null)
                  {
                     for (VariationOption option : options)
                     {
                        String title = option.getValue();
                        if (!Utils.isValidVariationTitle(title))
                        {
                           LOGGER.error("Invalid variation title [{}] for listing [{}]", title, listing.getTitle());
                           count++;
                        }
                     }
                  }
                  else
                  {
                     LOGGER.info("no options for listing {}", listing.getTitle());
                  }
               }
            }
            else
            {
               LOGGER.info("no variations for listing {}", listing.getTitle());
            }
         }
      }
      LOGGER.info("Complete. Found {} invalid variation titles", count);
   }
}
