package org.zabus.dotffp.util;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.zabus.dotffp.HttpClient;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Egor on 01.12.2015.
 */
public class HtmlFormUtils {

    public static List<NameValuePair> getDefoultFormPairs(String cmid, HttpClient httpClient) {
        String url = "http://dot-ffp.spbgut.ru" +
                "/question/question.php?" +
                "returnurl=%2Fmod%2Fquiz%2Fedit.php%3F" +
                "cmid%3D" + cmid + "%26cat%3D3340%252C6140%26" +
                "qpage%3D0%26addonpage%3D1&" +
                "cmid=" + cmid + "&appendqnumstring=addquestion&" +
                "category=3340&qtype=multichoice&scrollpos=200";
        return getQuestionEditFormPairs(ResponseUtils.getResponseAsString(httpClient.executeGet(url, "")));
    }

    public static List<NameValuePair> getQuestionEditFormPairs(String response) {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        Document doc = Jsoup.parse(response);
        Elements inputs = doc.getElementsByClass("region-content")
                .first()
                .getElementsByTag("input");
        Set<String> formItems = new HashSet<String>();
        inputs.stream()
                .filter(input -> !input.attr("name").equals("name") && !input.attr("name").equals("addanswers")
                        && !input.attr("name").equals("addhint") && !input.attr("name").equals("cancel")
                        && !input.attr("name").equals("noanswers") && !input.attr("name").equals("numhints"))
                .forEach(input -> {
                            String name = input.attr("name");
                            if (!formItems.contains(name)) {
                                nvps.add(new BasicNameValuePair(name, input.attr("value")));
                                formItems.add(name);
                            }
                        }
                );
        return nvps;
    }

    public static List<NameValuePair> getTopicEditForm(String response) {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        Document doc = Jsoup.parse(response);
        Elements inputs = doc.getElementsByClass("region-content")
                .first()
                .getElementsByTag("input");
        Set<String> formItems = new HashSet<String>();
        inputs.stream().forEach(input -> {
                    String name = input.attr("name");
                    if (!formItems.contains(name)) {
                        nvps.add(new BasicNameValuePair(name, input.attr("value")));
                        formItems.add(name);
                    }
                }
        );
        return nvps;
    }

}
