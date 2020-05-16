package com.islandsoftware.storefronhq.tools;

import java.util.Comparator;

public class KeywordVolumeComparator implements Comparator<Keyword>
{
   @Override
   public int compare(Keyword firstKeyword, Keyword secondKeyword)
   {
      // reverse the order to sort from smallest to largest
      return secondKeyword.getVolume() - firstKeyword.getVolume();
   }
}
