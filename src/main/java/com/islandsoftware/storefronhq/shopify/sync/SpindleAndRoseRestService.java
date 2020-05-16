package com.islandsoftware.storefronhq.shopify.sync;

import com.islandsoftware.storefronhq.StoreSync;
import com.islandsoftware.storefronhq.etsy.EtsyClient;
import com.islandsoftware.storefronhq.etsy.model.ListingsResponse;
import com.islandsoftware.storefronhq.shopify.sync.model.ShopifyOrder;
import com.islandsoftware.storefronhq.stats.*;
import com.islandsoftware.storefronhq.tools.CreateShopifyProducts;
import com.islandsoftware.storefronhq.tools.NewArrivals;
import com.islandsoftware.storefronhq.tools.SyncShopifyInventoryWithEtsy;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Api(value="spindleandrose")
@Path("/spindleandrose")
public class SpindleAndRoseRestService
{
   private StoreSync storeSync;
   private NgrocClient ngrocClient;
   private StatsCollector statsCollector;
   private static final Logger LOGGER = LoggerFactory.getLogger(SpindleAndRoseRestService.class);

   public SpindleAndRoseRestService(StoreSync storeSync, NgrocClient ngrocClient)
   {
      this.storeSync = storeSync;
      this.ngrocClient = ngrocClient;
      this.statsCollector = storeSync.getStatsCollector();
   }

   /**
    * A shopify product has been updated.
    * @param updatedProduct details on the updated product.
    * @return OK Response
     */
   @POST
   @Path("/product/update")
   @ApiOperation(value="A product update has been received from Shopify")
   @ApiResponses(value = { @ApiResponse(code = 200, message = "Product update received") })
   public Response updateProduct(@ApiParam(value = "Updated product JSON ", required = true) String updatedProduct)
   {
      LOGGER.debug("updateProduct: incoming json=" + updatedProduct);
      return Response.ok().build();
   }

   @POST
   @Path("/order/paid")
   public Response paidOrder(ShopifyOrder order)
   {
      LOGGER.info("paidOrder: order received");
      storeSync.handleShopifyOrder(order);
      return Response.ok().build();
   }

   @POST
   @Path("/admin/sync")
   public Response sync()
   {
      LOGGER.info("sync");
      Thread t = new Thread() {
         public void run() {
            SyncShopifyInventoryWithEtsy.sync(storeSync);
         }
      };
      t.start();
      return Response.ok().build();
   }

   @POST
   @Path("/products")
   public Response createProducts()
   {
      LOGGER.info("createProducts");
      Thread t = new Thread() {
         public void run() {

            try
            {
               storeSync.update();
               storeSync.loadMasterProductMap();
               CreateShopifyProducts.createProducts(storeSync);
            }
            catch (Exception e)
            {
               LOGGER.error("createProducts", e);
            }
         }
      };
      t.start();
      return Response.ok().build();
   }

   @GET
   @Path("product/etsy/{listingId}")
   @Produces(MediaType.APPLICATION_JSON)
   public Response getEstyListing(@PathParam("listingId") long listingId)
   {
      LOGGER.info("getEtsyListing: {}", listingId);
      EtsyClient client = storeSync.getEtsyClientForListingId(listingId);
      ListingsResponse listing = client.getListing(listingId, null);
      return Response.ok().entity(listing).build();
   }

   @GET
   @Path("admin/maps")
   @Produces(MediaType.TEXT_PLAIN)
   public Response getMaps()
   {
      LOGGER.info("getMaps");
      StringBuilder sb = new StringBuilder();
      sb.append("Maps:\n");
      sb.append("ShopifyId2TitleSize=").append(storeSync.getShopifyId2TitleSize());
      sb.append("\n");
      sb.append("ShopifyTitle2IdSize=").append(storeSync.getShopifyTitle2IdSize());
      sb.append("\n");
      sb.append("SpindleEtsyId2TitleSize=").append(storeSync.getEtsyId2TitleSize("spindle"));
      sb.append("\n");
      sb.append("SpindleEtsyTitle2ListingIdSize=").append(storeSync.getEtsyTitle2ListingIdSize("spindle"));
      sb.append("\n");
      sb.append("ImagineEtsyId2TitleSize=").append(storeSync.getEtsyId2TitleSize("imagine"));
      sb.append("\n");
      sb.append("ImagineEtsyTitle2ListingIdSize=").append(storeSync.getEtsyTitle2ListingIdSize("imagine"));
      return Response.ok().entity(sb.toString()).build();
   }

   @PUT
   @Path("admin/maps")
   public Response updateMaps()
   {
      LOGGER.info("updateMaps");
      storeSync.update();
      return Response.ok().build();
   }

   @DELETE
   @Path("admin/newarrivals")
   public Response removeNewArrivals()
   {
      LOGGER.info("removeNewArrivals");
      try
      {
         NewArrivals.remove();
         return Response.ok().build();
      }
      catch (Exception e)
      {
         LOGGER.error("removeNewArrivals", e);
         return Response.serverError().build();
      }
   }

   @PUT
   @Path("products/reload")
   public Response reloadMasterProductMap()
   {
      LOGGER.info("reloadMasterProductMap");
      try
      {
         storeSync.loadMasterProductMap();
         return Response.ok().build();
      }
      catch (Exception e)
      {
         LOGGER.error("Error loading master product list", e);
         return Response.serverError().build();
      }
   }

   @GET
   @Path("products/missing")
   @Produces(MediaType.TEXT_PLAIN)
   public Response getShopifyProductsNotInEtsy()
   {
      LOGGER.info("getShopifyProductsNotInEtsy");
      StringBuilder sb = new StringBuilder();
      List<Long> ids = storeSync.shopifyProductIdsNotInEtsy();
      sb.append("Products in Shopify that are not in Etsy: ").append(ids.size()).append("\n");
      for (Long id : ids)
      {
         String title = storeSync.getShopifyTitleForShopifyId(id);
         sb.append("Id=").append(id).append(", title=").append(title).append("\n");
      }
      return Response.ok().entity(sb.toString()).build();
   }

   @GET
   @Path("admin/stats/between/{begin}/{end}")
   @Produces(MediaType.APPLICATION_JSON)
   public Response getStatsBetween(
      @PathParam("begin") String begin,
      @PathParam("end") String end,
      @QueryParam("channel") @DefaultValue("all") String channel)
   {
      try
      {
         LOGGER.info("getStatsBetween: begin={} end={}", begin, end);
         StatsResponse statsResponse = new StatsResponse();
         statsResponse.setChannel(channel);
         StatTotals statTotals = statsCollector.statTotals(begin, end, channel);
         statsResponse.setSummary(statTotals);
         return Response.ok().entity(statsResponse).build();
      }
      catch (Exception e)
      {
         LOGGER.error("getStatsForTimePeriod", e);
         return Response.serverError().build();
      }
   }

   @GET
   @Path("admin/stats/{timeperiod}")
   @Produces(MediaType.APPLICATION_JSON)
   public Response getStatsForTimePeriod(
      @PathParam("timeperiod") String timeperiod,
      @QueryParam("expand") @DefaultValue("false") boolean expand,
      @QueryParam("channel") @DefaultValue("all") String channel)
   {
      try
      {
         LOGGER.info("getStatsForTimePeriod: {}", timeperiod);
         StatsResponse statsResponse = new StatsResponse();
         statsResponse.setChannel(channel);
         StatTotals statTotals = statsCollector.statTotals(timeperiod, channel);
         statsResponse.setSummary(statTotals);
         if (expand)
         {
            List<FormattedOrderStats> formattedOrderStats = statsCollector.allStats(timeperiod, channel);
            statsResponse.setOrders(formattedOrderStats);
         }
         return Response.ok().entity(statsResponse).build();
      }
      catch (Exception e)
      {
         LOGGER.error("getStatsForTimePeriod", e);
         return Response.serverError().build();
      }
   }

   @GET
   @Path("admin/stats/days/{numdays}")
   @Produces(MediaType.APPLICATION_JSON)
   public Response getStatsForDays(
      @PathParam("numdays") int numdays,
      @QueryParam("expand") @DefaultValue("false") boolean expand,
      @QueryParam("channel") @DefaultValue("all") String channel)
   {
      try
      {
         LOGGER.info("getStatsForDays: {}", numdays);
         StatsResponse statsResponse = new StatsResponse();
         statsResponse.setChannel(channel);
         StatTotals statTotals = statsCollector.statTotals(numdays, channel);
         statsResponse.setSummary(statTotals);
         if (expand)
         {
            List<FormattedOrderStats> formattedOrderStats = statsCollector.allStats(numdays, channel);
            statsResponse.setOrders(formattedOrderStats);
         }
         return Response.ok().entity(statsResponse).build();
      }
      catch (Exception e)
      {
         LOGGER.error("getStatsForDays", e);
         return Response.serverError().build();
      }
   }

   /**
    * Sort by dayOfWeek, orders, revenue, profit, profit ratio...
    */
   @GET
   @Path("admin/stats/byday")
   @Produces(MediaType.APPLICATION_JSON)
   public Response getStatsByDay(
      @QueryParam("channel") @DefaultValue("all") String channel,
      @QueryParam("sortBy") @DefaultValue("orders") String sortBy,
      @QueryParam("orderBy") @DefaultValue("desc") String orderBy)
   {
      try
      {
         LOGGER.info("getStatsByDay: channel={} sortBy={} orderBy={}", channel, sortBy, orderBy);
         StatsForDaysOfWeekResponse statsResponse = new StatsForDaysOfWeekResponse();
         statsResponse.setChannel(channel);
         statsResponse.setSortBy(sortBy);
         statsResponse.setOrderBy(orderBy);
         List<StatTotalsBySegment> dayOfWeekStats = statsCollector.dayOfWeekStats(channel);
         statsCollector.sort(dayOfWeekStats, sortBy, orderBy);
         statsResponse.setStats(dayOfWeekStats);
         return Response.ok().entity(statsResponse).build();
      }
      catch (Exception e)
      {
         LOGGER.error("getStatsByDay", e);
         return Response.serverError().build();
      }
   }

   @GET
   @Path("admin/stats/bymonth")
   @Produces(MediaType.APPLICATION_JSON)
   public Response getStatsByMonth(
      @QueryParam("channel") @DefaultValue("all") String channel,
      @QueryParam("sortBy") @DefaultValue("orders") String sortBy,
      @QueryParam("orderBy") @DefaultValue("desc") String orderBy)
   {
      try
      {
         LOGGER.info("getStatsByMonth: channel={} sortBy={} orderBy={}", channel, sortBy, orderBy);
         StatsForDaysOfWeekResponse statsResponse = new StatsForDaysOfWeekResponse();
         statsResponse.setChannel(channel);
         statsResponse.setSortBy(sortBy);
         statsResponse.setOrderBy(orderBy);
         List<StatTotalsBySegment> dayOfWeekStats = statsCollector.monthOfYearStats(channel);
         statsCollector.sort(dayOfWeekStats, sortBy, orderBy);
         statsResponse.setStats(dayOfWeekStats);
         return Response.ok().entity(statsResponse).build();
      }
      catch (Exception e)
      {
         LOGGER.error("getStatsByMonth", e);
         return Response.serverError().build();
      }
   }

   @GET
   @Path("admin/stats/last")
   @Produces(MediaType.APPLICATION_JSON)
   public Response getLastOrder()
   {
      try
      {
         LOGGER.info("getLastOrder");
         FormattedOrderStats stats = statsCollector.lastOrder();
         LOGGER.info("getLastOrder: {}", stats);
         return Response.ok().entity(stats).build();
      }
      catch (Exception e)
      {
         LOGGER.error("getLastOrder", e);
         return Response.serverError().build();
      }
   }

   @GET
   @Path("admin/http")
   @Produces(MediaType.TEXT_PLAIN)
   public Response getHttpInfo()
   {
      try
      {
         return Response.ok().entity(ngrocClient.getPublicUrl()).build();
      }
      catch (Exception e)
      {
         return Response.serverError().build();
      }
   }

   @GET
   @Path("admin/tcp")
   @Produces(MediaType.TEXT_PLAIN)
   public Response getTcpInfo()
   {
      return Response.ok().entity(ngrocClient.getTcpInfo()).build();
   }
}
