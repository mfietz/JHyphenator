package de.mfietz.jhyphenator;

import java.io.Serializable;

public class TrieNode implements Serializable {

    private static final long serialVersionUID = 1L;

    public TrieNode() {}

    IntTrieNodeMap codePoint = new IntTrieNodeArrayMap();;
    int[] points;
}

