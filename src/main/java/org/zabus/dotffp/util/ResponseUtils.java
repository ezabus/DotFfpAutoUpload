package org.zabus.dotffp.util;

import org.apache.http.HttpResponse;

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
}
