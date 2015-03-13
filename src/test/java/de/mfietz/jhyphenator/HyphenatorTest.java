package de.mfietz.jhyphenator;

import static org.junit.Assert.assertEquals;

import java.util.List;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class) public class HyphenatorTest {

    public static String join(List<String> list, String delimiter) {
        if (list == null || list.size() == 0) {
            return "";
        }
        StringBuilder result = new StringBuilder(list.get(0));
        for (int i = 1; i < list.size(); i++) {
            result.append(delimiter).append(list.get(i));
        }
        return result.toString();
    }

    @Test
    @Parameters({
      "Kochschule, Koch-schu-le", 
      "Seewetterdienst, See-wet-ter-dienst",
      "Hochverrat, Hoch-ver-rat", 
      "Musterbeispiel, Mus-ter-bei-spiel",
      "Bundespräsident, Bun-des-prä-si-dent", 
      "Schmetterling, Schmet-ter-ling",
      "Christian, Chris-ti-an"
    }) 
    public void testDe(String input, String expected) {
        HyphenationPattern de = HyphenationPattern.lookup("de");
        Hyphenator h = Hyphenator.getInstance(de);
        String actual = join(h.hyphenate(input), "-");
        assertEquals(expected, actual);
    }

    @Test
    @Parameters({
      "crocodile, croc-o-dile", 
      "activity, ac-tiv-ity",
      "potato, po-ta-to",
      "hyphenation, hy-phen-a-tion",
      "podcast, pod-cast", "message, mes-sage"
    })
    public void testEnUs(String input, String expected) {
        HyphenationPattern us = HyphenationPattern.lookup("en_us");
        Hyphenator h = Hyphenator.getInstance(us);
        String actual = join(h.hyphenate(input), "-");
        assertEquals(expected, actual);
    }
    
    @Test
    @Parameters({
      "segítség, se-gít-ség" 
    })
    public void testHu(String input, String expected) {
      HyphenationPattern us = HyphenationPattern.lookup("hu");
      Hyphenator h = Hyphenator.getInstance(us);
      String actual = join(h.hyphenate(input), "-");
      assertEquals(expected, actual);
  }


}
