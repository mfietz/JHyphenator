package de.mfietz.jhyphenator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Hyphenator.java is an adaptation of Mathew Kurian' TextJustify-Android
 * Library:
 * https://github.com/bluejamesbond/TextJustify-Android/
 * <p/>
 * Code from this project belongs to the following license:
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
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

    public Hyphenator(TrieNode trie, int leftMin, int rightMin) {
        this.trie = trie;
        this.leftMin = leftMin;
        this.rightMin = rightMin;
    }

    private Hyphenator(HyphenationPattern pattern) {
        this(createTrie(pattern.patterns), pattern.leftMin, pattern.rightMin);
    }

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
            String[] patterns = new String[value.length()/key];
            for (int i = 0; i + key <= value.length(); i = i + key) {
               patterns[i/key] = value.substring(i, i+key);
            }
            for (int i = 0; i < patterns.length; i++) {
                String pattern = patterns[i];
                // String[] chars = patterns[i].replaceAll("[0-9]", "").split("");
                t = tree;

                for (int c = 0; c < pattern.length(); c++) {
                    char chr = pattern.charAt(c);
                    if (Character.isDigit(chr)) {
                        continue;
                    }
                    int codePoint = pattern.codePointAt(c);
                    // int codePoint = chars[c].codePointAt(0);
                    if (t.codePoint.get(codePoint) == null) {
                        t.codePoint.put(codePoint, new TrieNode());
                    }
                    t = t.codePoint.get(codePoint);
                }

                /*
                String[] points = patterns[i].split("[^0-9]");
                t.points = new int[points.length];
                for (int p = 0; p < points.length; p++) {
                    try {
                        t.points[p] = Integer.valueOf(points[p]);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        t.points[p] = 0;
                    }
                }
                */

                IntArrayList list = new IntArrayList();
                int digitStart = -1;
                for (int p = 0; p < pattern.length(); p++) {
                    if(Character.isDigit(pattern.charAt(p))) {
                        if(digitStart < 0) {
                            digitStart = p;
                        }
                    } else if(digitStart >= 0) {
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
                        for (int k = 0, nodePointsLength =
                                nodePoints.length; k < nodePointsLength; k++) {
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
        if(start < word.length()-1) {
            result.add(word.substring(start, word.length()-1));
        }
        return result;
    }

    public static void main(String[] args) {
        for(int i=0; i < 5; i++) {
            for (HyphenationPattern p : HyphenationPattern.values()) {
                System.out.println(p.name());
                long start = System.currentTimeMillis();
                TrieNode t = createTrie(p.patterns);
                System.out.println("createTrie() took " + (System.currentTimeMillis() - start) + " ms");
            }
        }
    }

}
