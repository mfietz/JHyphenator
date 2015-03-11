package de.mfietz.jhyphenator;

import java.util.List;

public class StringJoiner {

    public static String join(List<String> list, String delimiter) {
        if(list == null || list.size() == 0) {
            return "";
        }
        StringBuilder result = new StringBuilder(list.get(0));
        for(int i=1; i < list.size(); i++) {
            result.append(delimiter).append(list.get(i));
        }
        return result.toString();
    }

}
