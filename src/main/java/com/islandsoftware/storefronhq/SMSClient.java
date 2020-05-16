package com.islandsoftware.storefronhq;

import com.islandsoftware.storefronhq.tools.Utils;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SMSClient
{
   private static final String ACCOUNT_SID = "AC13c286084514100cd4cb3718faa1ed9e";
   private static final String AUTH_TOKEN = "09ba06b186e1b3c57eecd72173b41e9b";
   private static final String TWILIO_NUMBER = "+13032178449";
   private static final String KELLY = "+13033243876";
   private static final String ALEXA = "+13039492771";
   private static final Logger LOGGER = LoggerFactory.getLogger(SMSClient.class);

   private static List<String> numbers = new ArrayList<String>();
   private static Set<String> messages = new HashSet<>();

   static
   {
      Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
      numbers.add(KELLY);
      numbers.add(ALEXA);
   }

   public static void main(String[] args)
   {
      try
      {
         for (String number : numbers)
         {
            Message message = Message.creator(new PhoneNumber(number), new PhoneNumber(TWILIO_NUMBER), "Hi from SpindleAndRose app").create();
            System.out.println(message.getStatus());
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   public static void alertAlexa(String text)
   {
      try
      {
            LOGGER.info("alertAlexa: sending to {} alert text=[{}]", ALEXA, text);
            Message.creator(new PhoneNumber(ALEXA), new PhoneNumber(TWILIO_NUMBER), "SpindleAndRoseApp: " + text).create();
      }
      catch (Exception e)
      {
         LOGGER.error("error", e);
      }
   }

   public static void alertAll(String text)
   {
      try
      {
         for (String number : numbers)
         {
            LOGGER.info("alertAll: sending to {} alert text=[{}]", number, text);
            Message.creator(new PhoneNumber(number), new PhoneNumber(TWILIO_NUMBER), "SpindleAndRoseApp: " + text).create();
         }
      }
      catch (Exception e)
      {
         LOGGER.error("error", e);
      }
   }

   public synchronized static void alertAdmin(String text)
   {
      try
      {
         LOGGER.info("alertAdmin: adding to message set text=[{}]", text);
         if (!messages.add(text))
         {
            LOGGER.warn("alertAdmin: message set already contains {}", text);
         }
      }
      catch (Exception e)
      {
         LOGGER.error("error", e);
      }
   }

   public synchronized static void sendMessages()
   {
      if (messages.size() > 0)
      {
         LOGGER.info("sendMessages: there are {} messages ready to send", messages.size());
         for (String message : messages)
         {
            LOGGER.info("sendMessages: sending to {} alert text=[{}]", KELLY, message);
            Message.creator(new PhoneNumber(KELLY), new PhoneNumber(TWILIO_NUMBER), "SpindleAndRoseApp: " + message).create();
            Utils.sleep(1000L);
         }
         messages.clear();
      }
   }
}
