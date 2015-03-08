package de.mfietz.jhyphenator;

import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import java.io.FileInputStream;

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

public class Hyphenator  {

    private static final HashMap<HyphenationPattern, Hyphenator> cached;

    static {
        cached = new HashMap<HyphenationPattern, Hyphenator>();
    }

    private TrieNode trie;
    private int leftMin;
    private int rightMin;

    private Hyphenator(HyphenationPattern pattern) {
        this.trie = this.createTrie(pattern.patterns);
        this.leftMin = pattern.leftMin;
        this.rightMin = pattern.rightMin;
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

                String[] chars = patterns[i].replaceAll("[0-9]", "").split("");

                String[] points = patterns[i].split("[^0-9]");

                t = tree;

                for (int c = 0; c < chars.length; c++) {
                    if (chars[c].length() == 0) {
                        continue;
                    }
                    int codePoint = chars[c].codePointAt(0);
                    if (t.codePoint.get(codePoint) == null) {
                        t.codePoint.put(codePoint, new TrieNode());
                    }
                    t = t.codePoint.get(codePoint);
                }

                t.points = new int[points.length];
                for (int p = 0; p < points.length; p++) {
                    try {
                        t.points[p] = Integer.valueOf(points[p]);
                    } catch (NumberFormatException e) {
                        t.points[p] = 0;
                    }
                }
            }
        }
        return tree;
    }

    public String hyphenate(String word) {
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

        StringBuilder result = new StringBuilder();
        for (int i = 1; i < wordLength - 1; i++) {
            if (i > this.leftMin && i < (wordLength - this.rightMin) && points[i] % 2 > 0) {
                result.append("|");
            }
            result.append(word,i, i + 1);
        }
        return result.toString();
    }

    public static void write(String name, TrieNode t) {
        try {
            FSTObjectOutput out = new FSTObjectOutput(new FileOutputStream(name + ".bin"));
            out.writeObject( t );
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static TrieNode read(String name) {
        TrieNode t = null;
        try {
            FSTObjectInput in = new FSTObjectInput(new FileInputStream(name +".bin"));
            t = (TrieNode)in.readObject();
            in.close();
        } catch(IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return t;
    }

    public static void main(String[] args) {
        for(int i=0; i < 5; i++) {
            for (HyphenationPattern p : HyphenationPattern.values()) {
                System.out.println(p.name());
                long start = System.currentTimeMillis();
                TrieNode t = createTrie(p.patterns);
                System.out.println("createTrie() took " + (System.currentTimeMillis() - start) + " ms");
                start = System.currentTimeMillis();
                write(p.name(), t);
                System.out.println("write() took " + (System.currentTimeMillis() - start) + " ms");
                start = System.currentTimeMillis();
                t = read(p.name());
                System.out.println("read() took " + (System.currentTimeMillis() - start) + " ms");
            }
        }
    }

}
