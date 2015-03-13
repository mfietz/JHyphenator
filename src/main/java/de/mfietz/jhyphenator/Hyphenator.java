package de.mfietz.jhyphenator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Hyphenator.java is an highly optimized adaptation of parts from Mathew
 * Kurian's TextJustify-Android Library:
 * https://github.com/bluejamesbond/TextJustify-Android/
 */

public class Hyphenator implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final HashMap<HyphenationPattern, Hyphenator> cached;

    static {
        cached = new HashMap<HyphenationPattern, Hyphenator>();
    }

    private TrieNode trie;
    private int leftMin;
    private int rightMin;

    private Hyphenator(HyphenationPattern pattern) {
        this.trie = createTrie(pattern.patterns);
        this.leftMin = pattern.leftMin;
        this.rightMin = pattern.rightMin;
    }

    /**
     * Returns a hyphenator instance for a given hypenation pattern
     *
     * @param hyphenationPattern hyphenation language pattern
     * @return newly created or cached hyphenator instance
     */
    public static Hyphenator getInstance(HyphenationPattern hyphenationPattern) {
        synchronized (cached) {
            if (!cached.containsKey(hyphenationPattern)) {
                cached.put(hyphenationPattern, new Hyphenator(hyphenationPattern));
                return cached.get(hyphenationPattern);
            }

            return cached.get(hyphenationPattern);
        }
    }

    private static TrieNode createTrie(Map<Integer, String> patternObject) {
        TrieNode t, tree = new TrieNode();

        for (Map.Entry<Integer, String> entry : patternObject.entrySet()) {
            int key = entry.getKey();
            String value = entry.getValue();
            String[] patterns = new String[value.length() / key];
            for (int i = 0; i + key <= value.length(); i = i + key) {
                patterns[i / key] = value.substring(i, i + key);
            }
            for (int i = 0; i < patterns.length; i++) {
                String pattern = patterns[i];
                t = tree;

                for (int c = 0; c < pattern.length(); c++) {
                    char chr = pattern.charAt(c);
                    if (Character.isDigit(chr)) {
                        continue;
                    }
                    int codePoint = pattern.codePointAt(c);
                    if (t.codePoint.get(codePoint) == null) {
                        t.codePoint.put(codePoint, new TrieNode());
                    }
                    t = t.codePoint.get(codePoint);
                }

                IntArrayList list = new IntArrayList();
                int digitStart = -1;
                for (int p = 0; p < pattern.length(); p++) {
                    if (Character.isDigit(pattern.charAt(p))) {
                        if (digitStart < 0) {
                            digitStart = p;
                        }
                    } else if (digitStart >= 0) {
                        String number = pattern.substring(digitStart, p);
                        list.add(Integer.valueOf(number));
                        digitStart = -1;
                    } else {
                        list.add(0);
                    }
                }
                t.points = list.toArray();
            }
        }
        return tree;
    }

    /**
     * Returns a list of syllables that indicates at which points the word can
     * be broken with a hyphen
     *
     * @param word Word to hyphenate
     * @return list of syllables
     */
    public List<String> hyphenate(String word) {
        word = "_" + word + "_";

        String lowercase = word.toLowerCase();

        int wordLength = lowercase.length();
        int[] points = new int[wordLength];
        int[] characterPoints = new int[wordLength];
        for (int i = 0; i < wordLength; i++) {
            points[i] = 0;
            characterPoints[i] = lowercase.codePointAt(i);
        }

        TrieNode node, trie = this.trie;
        int[] nodePoints;
        for (int i = 0; i < wordLength; i++) {
            node = trie;
            for (int j = i; j < wordLength; j++) {
                node = node.codePoint.get(characterPoints[j]);
                if (node != null) {
                    nodePoints = node.points;
                    if (nodePoints != null) {
                        for (int k = 0, nodePointsLength = nodePoints.length;
                             k < nodePointsLength; k++) {
                            points[i + k] = Math.max(points[i + k], nodePoints[k]);
                        }
                    }
                } else {
                    break;
                }
            }
        }

        List<String> result = new ArrayList<String>();
        int start = 1;
        for (int i = 1; i < wordLength - 1; i++) {
            if (i > this.leftMin && i < (wordLength - this.rightMin) && points[i] % 2 > 0) {
                result.add(word.substring(start, i));
                start = i;
            }
        }
        if (start < word.length() - 1) {
            result.add(word.substring(start, word.length() - 1));
        }
        return result;
    }

}
