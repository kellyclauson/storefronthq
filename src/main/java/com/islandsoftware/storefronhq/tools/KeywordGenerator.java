package com.islandsoftware.storefronhq.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class KeywordGenerator
{
   public static void main(String[] args)
   {
      List<String> themes = new ArrayList<>();
      themes.add("alexander henry");
      themes.add("cactus");
      themes.add("folk");
      themes.add("frida");
      themes.add("freida");
      themes.add("mexican");
      themes.add("skull");
      themes.add("southwest");
      //String keywords = build(themes, "fabric", null);
      //keywords = keywords + build(themes, "fabric", "sewing");
      //keywords = keywords + build(themes, "fabric", "quilting");
      //keywords = keywords + build(themes, "fabric", "by the yard");
      //System.out.println(keywords);


      Map<String, List<String>> map = new TreeMap<>();
      for (String theme : themes)
      {
         List list = map.get(theme);
         if (list == null)
         {
            list = new ArrayList();
            map.put(theme, list);
         }
         list.addAll(build(theme, "fabric", null));
         list.addAll(build(theme, "fabric", "sewing"));
         list.addAll(build(theme, "fabric", "quilting"));
         list.addAll(build(theme, "fabric", "by the yard"));
         list.addAll(build(theme, "material", null));
         list.addAll(build(theme, "material", "sewing"));
         list.addAll(build(theme, "material", "quilting"));
         list.addAll(build(theme, "material", "by the yard"));
      }

      for (String theme : map.keySet())
      {
         System.out.println("Theme: " + theme);
         for (String keyword : map.get(theme))
         {
            System.out.println(keyword);
         }
         System.out.println("\n\n");
      }
   }

   private static List<String> build(String theme, String type, String modifier)
   {
      List<String> list = new ArrayList();
      StringBuilder sb = new StringBuilder();
      String[] split = theme.split(" ");
      int count = 0;
      for (String s : split)
      {
         sb.append("+").append(s);
         if (++count < split.length)
         {
            sb.append(" ");
         }
         else if (modifier != null)
         {
            if (modifier.equals("sewing") || modifier.equals("quilting"))
            {
               sb.append(" +").append(modifier).append(" +").append(type);
            }
            else
            {
               sb.append(" +").append(type).append(" ");
               String[] split1 = modifier.split(" ");
               int count1 = 0;
               for (String s1 : split1)
               {
                  sb.append("+").append(s1);
                  if (++count1 < split1.length)
                  {
                     sb.append(" ");
                  }
               }
            }
         }
         else
         {
            sb.append(" +").append(type);
         }
      }
      list.add(sb.toString());
      sb = new StringBuilder();


      split = theme.split(" ");
      count = 0;
      sb.append("[");
      for (String s : split)
      {
         sb.append(s);
         if (++count < split.length)
         {
            sb.append(" ");
         }
         else if (modifier != null)
         {
            if (modifier.equals("sewing") || modifier.equals("quilting"))
            {
               sb.append(" ").append(modifier).append(" ").append(type);
            }
            else
            {
               sb.append(" ").append(type).append(" ").append(modifier);
            }
         }
         else
         {
            sb.append(" ").append(type);
         }
      }
      sb.append("]");
      list.add(sb.toString());
      sb = new StringBuilder();

      split = theme.split(" ");
      count = 0;
      sb.append("\"");
      for (String s : split)
      {
         sb.append(s);
         if (++count < split.length)
         {
            sb.append(" ");
         }
         else if (modifier != null)
         {
            if (modifier.equals("sewing") || modifier.equals("quilting"))
            {
               sb.append(" ").append(modifier).append(" ").append(type);
            }
            else
            {
               sb.append(" ").append(type).append(" ").append(modifier);
            }
         }
         else
         {
            sb.append(" ").append(type);
         }

      }
      sb.append("\"");
      list.add(sb.toString());

      return list;
   }

   private static String build(List<String> themes, String type, String modifier)
   {
      StringBuilder sb = new StringBuilder();
      for (String theme : themes)
      {
         String[] split = theme.split(" ");
         int count = 0;
         for (String s : split)
         {
            sb.append("+").append(s);
            if (++count < split.length)
            {
               sb.append(" ");
            }
            else if (modifier != null)
            {
               if (modifier.equals("sewing") || modifier.equals("quilting"))
               {
                  sb.append(" +").append(modifier).append(" +").append(type);
               }
               else
               {
                  sb.append(" +").append(type).append(" ");
                  String[] split1 = modifier.split(" ");
                  int count1 = 0;
                  for (String s1 : split1)
                  {
                     sb.append("+").append(s1);
                     if (++count1 < split1.length)
                     {
                        sb.append(" ");
                     }
                  }
               }
            }
            else
            {
               sb.append(" +").append(type);
            }
         }
         sb.append("\n");
      }
      for (String theme : themes)
      {
         String[] split = theme.split(" ");
         int count = 0;
         sb.append("[");
         for (String s : split)
         {
            sb.append(s);
            if (++count < split.length)
            {
               sb.append(" ");
            }
            else if (modifier != null)
            {
               if (modifier.equals("sewing") || modifier.equals("quilting"))
               {
                  sb.append(" ").append(modifier).append(" ").append(type);
               }
               else
               {
                  sb.append(" ").append(type).append(" ").append(modifier);
               }
            }
            else
            {
               sb.append(" ").append(type);
            }
         }
         sb.append("]");
         sb.append("\n");
      }
      for (String theme : themes)
      {
         String[] split = theme.split(" ");
         int count = 0;
         sb.append("\"");
         for (String s : split)
         {
            sb.append(s);
            if (++count < split.length)
            {
               sb.append(" ");
            }
            else if (modifier != null)
            {
               if (modifier.equals("sewing") || modifier.equals("quilting"))
               {
                  sb.append(" ").append(modifier).append(" ").append(type);
               }
               else
               {
                  sb.append(" ").append(type).append(" ").append(modifier);
               }
            }
            else
            {
               sb.append(" ").append(type);
            }

         }
         sb.append("\"");
         sb.append("\n");
      }
      return sb.toString();
   }

}
