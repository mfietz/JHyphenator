package de.mfietz.jhyphenator;

import java.io.Serializable;

public class IntTrieNodeArrayMap implements IntTrieNodeMap, Serializable {

    private static final long serialVersionUID = 1L;

    static final int DEFAULT_INITIAL_CAPACITY = 16;

    private int[] keys = new int[0];
    private TrieNode[] values = new TrieNode[0];

    private int size = 0;

    public IntTrieNodeArrayMap() {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    public IntTrieNodeArrayMap(int capacity) {
        keys = new int[capacity];
        values = new TrieNode[capacity];
    }

    private int findIndex(final int key) {
        for(int i = 0; i < size; i++) {
            if (keys[i] == key) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public TrieNode put(final int key, final TrieNode node) {
        final int oldIndex = findIndex(key);
        if (oldIndex >= 0) {
            final TrieNode oldValue = values[oldIndex];
            values[oldIndex] = node;
            return oldValue;
        }
        if (size == keys.length) {
            final int[] newKeys = new int[size == 0 ? DEFAULT_INITIAL_CAPACITY : size * 2 ];
            System.arraycopy(keys, 0, newKeys, 0, size);
            final TrieNode[] newValues = new TrieNode[size == 0 ? 2 : size * 2];
            System.arraycopy(values, 0, newValues, 0, size);
            keys = newKeys;
            values = newValues;
        }
        keys[size] = key;
        values[size] = node;
        size++;
        return null;
    }

    @Override
    public TrieNode get(final int key) {
        for( int i = 0; i < size; i++) {
            if (keys[i] == key) return values[i];
        }
        return null;
    }
}
