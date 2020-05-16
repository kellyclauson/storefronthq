package com.islandsoftware.storefronhq.shopify.sync;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.net.SocketTimeoutException;

public class TimeoutExceptionHandler implements ExceptionMapper<Throwable>
{
   private static final Logger LOGGER = LoggerFactory.getLogger(TimeoutExceptionHandler.class);

   @Override
   public Response toResponse(Throwable e)
   {
      LOGGER.error("TimeoutExceptionHandler.toResponse: {}", e.getMessage());
      if (e instanceof SocketTimeoutException)
      {
         return Response.status(Response.Status.REQUEST_TIMEOUT).header("exception", e.getMessage()).build();
      }
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("exception", e.getMessage()).build();
   }
}
