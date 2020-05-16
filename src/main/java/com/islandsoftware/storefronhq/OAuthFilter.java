package com.islandsoftware.storefronhq;

import org.apache.cxf.rs.security.oauth.client.OAuthClientUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import java.io.IOException;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;

public class OAuthFilter implements ClientRequestFilter
{
   private String consumerKey;
   private String consumerSecret;
   private String tokenKey;
   private String tokenSecret;

   private static final Logger LOGGER = LoggerFactory.getLogger(OAuthFilter.class);

   public OAuthFilter(String consumerKey, String consumerSecret, String tokenKey, String tokenSecret)
   {
      this.consumerKey = consumerKey;
      this.consumerSecret = consumerSecret;
      this.tokenKey = tokenKey;
      this.tokenSecret = tokenSecret;
   }

   public void filter(ClientRequestContext clientRequestContext) throws IOException
   {
      final OAuthClientUtils.Consumer consumer = new OAuthClientUtils.Consumer(consumerKey, consumerSecret);
      final OAuthClientUtils.Token token = new OAuthClientUtils.Token(tokenKey, tokenSecret);
      String header = OAuthClientUtils.createAuthorizationHeader(consumer, token, clientRequestContext.getMethod(), clientRequestContext.getUri().toString());
      LOGGER.debug("OauthHeader={}", header);
      clientRequestContext.getHeaders().add(AUTHORIZATION, header);
   }
}
