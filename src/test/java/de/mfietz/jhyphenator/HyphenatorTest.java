package de.mfietz.jhyphenator;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(JUnitParamsRunner.class)
public class HyphenatorTest {

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
        String actual = StringJoiner.join(h.hyphenate(input), "-");
        assertEquals(expected, actual);
    }

    @Test
    @Parameters({
            "crocodile, croc-o-dile",
            "activity, ac-tiv-ity",
            "potato, po-ta-to",
            "hyphenation, hy-phen-a-tion",
            "podcast, pod-cast",
            "message, mes-sage"
    })
    public void testEnUs(String input, String expected) {
        HyphenationPattern us = HyphenationPattern.lookup("en_us");
        Hyphenator h = Hyphenator.getInstance(us);
        String actual = StringJoiner.join(h.hyphenate(input), "-");
        assertEquals(expected, actual);
    }

}
