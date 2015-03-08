package de.mfietz.jhyphenator;

public class IntArrayList {

    public static final int DEFAULT_INITIAL_CAPACITY = 16;

    private int[] values;
    private int size;

    public IntArrayList() {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    public IntArrayList(int capacity) {
        values = new int[capacity];
    }

    public void add(int i) {
        if(size == values.length) {
            int[] newValues = new int[size == 0 ? DEFAULT_INITIAL_CAPACITY : size * 2];
            System.arraycopy(values, 0, newValues, 0, size);
            values = newValues;
        }
        values[size] = i;
        size++;
    }

    public int[] toArray() {
        int[] result = new int[size];
        System.arraycopy(values, 0, result, 0, size);
        return result;
    }

}
