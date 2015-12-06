package org.zabus.dotffp.util;

/**
 * Created by user on 04.12.2015.
 */
public class ParserUtils {

    public static void main(String argsp[]) {
        System.out.println(trimTopicName("1213"));
    }

    public static String trimTopicName(String topicName) {
        int i = 0;
        while((i < topicName.length()) && (Character.isDigit(topicName.charAt(i)))) {
            i++;
        }
        return topicName.substring(i);
    }
}
