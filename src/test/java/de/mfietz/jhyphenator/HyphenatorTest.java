package de.mfietz.jhyphenator;


import org.junit.Test;


import static org.junit.Assert.assertEquals;

public class HyphenatorTest {

    @Test
    public void testDe() {
        HyphenationPattern de = HyphenationPattern.lookup("de");
        Hyphenator h = Hyphenator.getInstance(de);

        String expected = "Koch|schu|le";
        String actual = StringJoiner.join(h.hyphenate("Kochschule"), "|");
        assertEquals(expected, actual);

        expected = "See|wet|ter|dienst";
        actual = StringJoiner.join(h.hyphenate("Seewetterdienst"), "|");
        assertEquals(expected, actual);

        expected = "Hoch|ver|rat";
        actual = StringJoiner.join(h.hyphenate("Hochverrat"), "|");
        assertEquals(expected, actual);
    }

    @Test
    public void testDeName() {
        HyphenationPattern de = HyphenationPattern.lookup("de");
        Hyphenator h = Hyphenator.getInstance(de);
        String expected = "Chris|ti|an";
        String actual = StringJoiner.join(h.hyphenate("Christian"), "|");
        assertEquals(expected, actual);
    }

    @Test
    public void testEnUs() {
        HyphenationPattern us = HyphenationPattern.lookup("en_us");
        Hyphenator h = Hyphenator.getInstance(us);

        String expected = "croc|o|dile";
        String actual = StringJoiner.join(h.hyphenate("crocodile"), "|");
        assertEquals(expected, actual);

        expected = "ac|tiv|ity";
        actual = StringJoiner.join(h.hyphenate("activity"), "|");
        assertEquals(expected, actual);
    }

}
