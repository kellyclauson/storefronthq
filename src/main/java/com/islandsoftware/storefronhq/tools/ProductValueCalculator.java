package com.islandsoftware.storefronhq.tools;

import com.islandsoftware.storefronhq.etsy.EtsyClient;
import com.islandsoftware.storefronhq.etsy.SpindleAndRoseEtsyClient;
import com.islandsoftware.storefronhq.etsy.model.ListingsResponse;
import com.islandsoftware.storefronhq.etsy.model.ListingsResult;
import com.islandsoftware.storefronhq.etsy.model.Variation;
import com.islandsoftware.storefronhq.etsy.model.VariationOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ProductValueCalculator
{
   private static final double COST_OF_GOODS_SOLD = 0.5089058524173028;
   private static final Logger LOGGER = LoggerFactory.getLogger(ProductValueCalculator.class);

   public static void main(String[] args)
   {
      calculate();
   }

   public static void calculate()
   {
      Double totalValue = 0.0;
      EtsyClient etsyClient = new SpindleAndRoseEtsyClient();
      Map<String, ListingsResult> listings = etsyClient.listings("price");
      int count = 0;
      StringBuilder sb = new StringBuilder();
      sb.append("title,quantity,cost,totalCost,price,retailValue");
      for (ListingsResult listing : listings.values())
      {
         count++;
         boolean foundPrice = false;
         Integer quantity = listing.getQuantity();
         long listingId = listing.getListingId();
         LOGGER.info("product {} of {}, title={}", count, listings.size(), listing.getTitle());
         ListingsResponse listingResponse = etsyClient.getListing(listingId, "Variations");
         ListingsResult[] results = listingResponse.getResults();
         Double price = 0.0;
         for (ListingsResult result : results)
         {
            Variation[] variations = result.getVariations();
            if (variations != null)
            {
               for (Variation variation : variations)
               {
                  VariationOption[] options = variation.getOptions();
                  price = findHighestPrice(options);
                  //LOGGER.info("cost={}", cost);
                  totalValue += (price * quantity);
                  foundPrice = true;
               }
            }
         }
         if (!foundPrice)
         {
            String p = listing.getPrice();
            //LOGGER.info("price={}", p);
            price = Double.parseDouble(p);
            totalValue += (price * quantity);
         }
         sb.append(listing.getTitle()).append(",")
                 .append(quantity).append(",")
                 .append("$").append(Utils.format(price/1.965)).append(",")
                 .append("$").append(Utils.format((quantity*price)/1.965)).append(",")
                 .append("$").append(Utils.format(price)).append(",")
                 .append("$").append(Utils.format(quantity*price))
                 .append("\n");
      }
      LOGGER.info(sb.toString());
      LOGGER.info("total retail value: {}", totalValue);
      double wholesaleValue = totalValue / 1.965;
      double midValue = ((totalValue - wholesaleValue) / 2.0) + wholesaleValue;
      LOGGER.info("total wholesale value: {}", wholesaleValue);
      wholesaleValue = totalValue * COST_OF_GOODS_SOLD;
      LOGGER.info("total wholesale value using cost of goods sold: {}", wholesaleValue);

      LOGGER.info("mid value: {}", midValue);
   }

   private static Double findHighestPrice(VariationOption[] options)
   {
      double highest = 0.0;
      for (VariationOption option : options)
      {
         if (option.getPrice() > highest)
         {
            highest = option.getPrice();
         }
      }
      return highest;
   }
}
