package de.mfietz.jhyphenator;

public interface IntTrieNodeMap {

    public TrieNode put(final int key, final TrieNode node);

    public TrieNode get(final int key);

}
