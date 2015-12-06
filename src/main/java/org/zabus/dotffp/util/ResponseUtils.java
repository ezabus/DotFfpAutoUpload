package org.zabus.dotffp.util;

import org.apache.http.HttpResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Egor on 01.12.2015.
 */
public class ResponseUtils {

    public static String getResponseAsString(HttpResponse response) {
        String content = "";
        try {
            System.out.println(response.getStatusLine());
            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            content = new String(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    public static int getIdOfTopic(HttpResponse response, int section) {
        String page = getResponseAsString(response);
        Document doc = Jsoup.parse(page);
        String moduleID = doc.getElementsByClass("topics")
                                .first()
                                .getElementsByClass("section-" + section)
                                .first()
                                .getElementsByClass("modtype_quiz")
                                .first()
                                .id();
        return Integer.parseInt(moduleID.substring(moduleID.lastIndexOf('-')));
    }
}
