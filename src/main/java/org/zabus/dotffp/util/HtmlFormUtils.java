package org.zabus.dotffp.util;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.zabus.dotffp.HttpClient;
import org.zabus.dotffp.MoodleClient;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Egor on 01.12.2015.
 */
public class HtmlFormUtils {

    public static void main(String args[]) {
        MoodleClient client = new MoodleClient();
        client.login("zabus", "ZaBUS12$)");
        client.initSesskey("508");
        getDefoultFormPairs("2996", client).forEach(System.out::println);
        //System.out.println(getCategory("2996", client));
    }

//    public static List<NameValuePair> getDefoultFormPairs(String cmid, HttpClient httpClient) {
//        String url = "http://dot-ffp.spbgut.ru" +
//                "/question/question.php?" +
//                "returnurl=%2Fmod%2Fquiz%2Fedit.php%3F" +
//                "cmid%3D" + cmid + "%26cat%3D3340%252C6140%26" +
//                "qpage%3D0%26addonpage%3D1&" +
//                "cmid=" + cmid + "&appendqnumstring=addquestion&" +
//                "category=3340&qtype=multichoice&scrollpos=200";
//
//        return getQuestionEditFormPairs(ResponseUtils.getResponseAsString(httpClient.executeGet(url, "")));
//    }

    public static List<NameValuePair> getDefoultFormPairs(String cmid, HttpClient httpClient) {
        String url = "http://dot-ffp.spbgut.ru" +
                "/question/question.php?" +
                "returnurl=%2Fmod%2Fquiz%2Fedit.php%3F" +
                "qpage%3D0%26addonpage%3D1&" +
                "cmid=" + cmid + "&appendqnumstring=addquestion&" +
                "category=" + getCategory(cmid, httpClient) +
                "&qtype=multichoice&scrollpos=200";

        return getQuestionEditFormPairs(ResponseUtils.getResponseAsString(httpClient.executeGet(url, "")));
    }

    public static String getCategory(String cmid, HttpClient httpClient) {
        //http://dot-ffp.spbgut.ru/mod/quiz/edit.php?cmid=2996
        Document doc = Jsoup.parse(ResponseUtils.getResponseAsString(
                httpClient.executeGet("http://dot-ffp.spbgut.ru/mod/quiz/edit.php?cmid=", cmid)));
        return doc.getElementsByClass("singlebutton").first()
                .getElementsByAttributeValue("name", "category").first()
                .attr("value");

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
        nvps.add(getCategoryFormPair(doc));
        return nvps;
    }

    public static NameValuePair getCategoryFormPair(Document document) {
        String value = document.getElementsByClass("fquestioncategory").first()
                .getElementsByTag("option").first()
                .attr("value");
        return new BasicNameValuePair("category", value);
    }

    public static List<NameValuePair> getTopicEditForm(String response) {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        Document doc = Jsoup.parse(response);
        Elements inputs = doc.getElementsByClass("region-content")
                .first()
                .getElementsByTag("input");
        Set<String> formItems = new HashSet<String>();
        inputs.stream()
                .filter(input ->
                        !input.attr("name").contains("rightanswer") && !input.attr("name").contains("generalfeedbac")
                        && !input.attr("name").contains("graceperiod") && !input.attr("name").contains("delay")
                        && !input.attr("name").equals("name") && !input.attr("name").equals("submitbutton")
                        && !input.attr("name").contains("time") && !input.attr("name").contains("specificfeedback")
                        && !input.attr("name").contains("during") && !input.attr("name").equals("boundary_add_fields")
                        && !input.attr("name").equals("showdescription") && !input.attr("name").equals("cancel"))
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

}
