package com.islandsoftware.storefronhq.shopify.sync;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.islandsoftware.storefronhq.shopify.sync.model.NgrocResponse;
import com.islandsoftware.storefronhq.shopify.sync.model.NgrocTunnel;
import org.apache.cxf.jaxrs.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.List;

public class NgrocClient
{
   WebClient client;

   private static final Logger LOGGER = LoggerFactory.getLogger(NgrocClient.class);

   public NgrocClient()
   {
      String url = "http://localhost:4040";
      List<?> providers = Arrays.asList(new JacksonJaxbJsonProvider());
      client = WebClient.create(url, providers);
   }

   public String getPublicUrl() throws Exception
   {
      List<NgrocTunnel> tunnels = getTunnels();
      String url = null;
      LOGGER.info("Number of Ngrok tunnels={}", tunnels.size());
      for (NgrocTunnel tunnel : tunnels)
      {
         if (tunnel.getProto().equals("https"))
         {
            url = tunnel.getPublicUrl();
         }
      }
      if (url != null)
      {
         return url;
      }
      throw new Exception("HTTP tunnel not found");
   }

   private List<NgrocTunnel> getTunnels()
   {
      client.back(true);
      client.path("api");
      client.path("tunnels");
      client.accept(MediaType.APPLICATION_JSON_TYPE);
      LOGGER.info("GET {}", client.getCurrentURI());
      NgrocResponse nr = client.get(NgrocResponse.class);
      return nr.getTunnels();
   }

   public String getTcpInfo()
   {
      List<NgrocTunnel> tunnels = getTunnels();
      LOGGER.info("Number of Ngrok tunnels={}", tunnels.size());
      for (NgrocTunnel tunnel : tunnels)
      {
         if (tunnel.getProto().equals("tcp"))
         {
            return tunnel.getPublicUrl();
         }
      }
      return "TCP tunnel not found";
   }
}
