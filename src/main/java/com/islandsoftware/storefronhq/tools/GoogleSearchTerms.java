package com.islandsoftware.storefronhq.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class GoogleSearchTerms
{
   private static final Logger LOGGER = LoggerFactory.getLogger(GoogleSearchTerms.class);

   public static void main(String[] args)
   {
      try
      {
         Set<String> knownSearchTerms = loadKnownSearchTerms("C:\\tmp\\knownSearchTerms.csv");
         Set<String> searchTerms = loadSearchTerms("C:\\Users\\krcla\\Downloads\\Search terms report (1).csv");
         Set<String> unknownSearchTerms = new TreeSet<>();
         for (String searchTerm : searchTerms)
         {
            if (!knownSearchTerms.contains(searchTerm))
            {
               unknownSearchTerms.add(searchTerm);
            }
         }
         Utils.toFile(unknownSearchTerms, "C:\\tmp\\searchTermsToResolve.csv");

         // terms have been resolved, add to known list
         knownSearchTerms.addAll(unknownSearchTerms);
         Utils.toFile(knownSearchTerms, "C:\\tmp\\knownSearchTerms.csv");
      }
      catch (Exception e)
      {
         LOGGER.error("main", e);
      }
   }

   private static Set<String> loadKnownSearchTerms(String filename) throws Exception
   {
      Set<String> terms = new TreeSet<>();
      File file = new File(filename);
      if (file.exists())
      {
         List<String> lines = Files.readAllLines(Paths.get(filename));
         for (String line : lines)
         {
            terms.add(line.trim());
         }
      }
      return terms;
   }


   private static Set<String> loadSearchTerms(String filename) throws Exception
   {
      Set<String> terms = new TreeSet<>();
      List<String> lines = Files.readAllLines(Paths.get(filename));
      int count = 0;
      for (String line : lines)
      {
         if (++count < 4)
         {
            continue;
         }
         if (count > lines.size() - 2)
         {
            continue;
         }
         String[] split = line.split(",");
         terms.add(split[0]);
      }
      return terms;
   }
}
