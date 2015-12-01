package org.zabus.dotffp;

import org.apache.http.*;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import sun.net.www.http.HttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by user on 25.11.2015.
 */
@Deprecated
public class Uploader {

    public BasicCookieStore cookieStore;
    public static String sesskey;

    public static void main(String args[]) {
        Uploader uploader = new Uploader();
        uploader.login("zabus", "ZaBUS12$)");
        //confirm();
        printCookies(uploader.getCookieStore());
        uploader.initSesskey("506");
        //uploader.startUploading("506");
        HttpResponse response = uploader.sendQuestion("2939", "506");
        //uploader.getPage(response.getHeaders("Location")[0].toString(),"");
    }

    public void login(String login, String password) {
        CloseableHttpClient httpClient = getHttpClient();
        try {
            HttpPost httpPost = new HttpPost("http://dot-ffp.spbgut.ru/login/index.php");
            List<NameValuePair> nvps = getLoginParams(login, password);
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));
            httpClient.execute(httpPost);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HttpResponse getPage(String url, String urlParams) {
        HttpResponse response = null;
        try {
            CloseableHttpClient httpClient = getHttpClient(cookieStore);
            HttpGet httpGet = new HttpGet(url + urlParams);
            response = httpClient.execute(httpGet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public static List<NameValuePair> getLoginParams(String login, String password) {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("username", login));
        nvps.add(new BasicNameValuePair("password", password));
        return nvps;
    }

    public static List<NameValuePair> getEditFormParams(String id, String sesskey, String status) {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("id", id));
        nvps.add(new BasicNameValuePair("sesskey", sesskey));
        nvps.add(new BasicNameValuePair("edit", status));
        return nvps;
    }

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

    public static void printCookies(BasicCookieStore store) {
        List<Cookie> cookies = store.getCookies();
        if (cookies.isEmpty()) {
            System.out.println("None");
        } else {
            for (Cookie cooky : cookies) {
                System.out.println("- " + cooky.toString());
            }
        }
    }

    public void initSesskey(String courseID) {
        setSesskey(getSessKey(getResponseAsString(
                getPage("http://dot-ffp.spbgut.ru/course/view.php", "?id=" + courseID))));
    }

    public static void setSesskey(String sesskey) {
        Uploader.sesskey = sesskey;
    }

    public HttpResponse sendQuestion(String cmid, String courseID) {
        String url = "http://dot-ffp.spbgut.ru" +
                "/question/question.php?" +
                "returnurl=%2Fmod%2Fquiz%2Fedit.php%3F" +
                "cmid%3D" + cmid + "%26cat%3D3340%252C6140%26" +
                "qpage%3D0%26addonpage%3D1&" +
                "cmid=" + cmid + "&appendqnumstring=addquestion&" +
                "category=3340&qtype=multichoice&scrollpos=200";
        CloseableHttpClient httpClient = getHttpClient(cookieStore);
        HttpResponse response = null;
        try {
            HttpPost httpPost = new HttpPost("http://dot-ffp.spbgut.ru/question/question.php");
            List<NameValuePair> nvps = getQuestionEditFormPairs(getResponseAsString(getPage(url, "")));
            nvps.addAll(getQuestionAnswerFormPairs());
            nvps.forEach(System.out::println);
            httpPost = setHeadersForQuestionPost(httpPost);
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
            response = httpClient.execute(httpPost);
            System.out.println(getResponseAsString(response));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public HttpResponse sendQuestion(Question question, String cmid) {
        CloseableHttpClient httpClient = getHttpClient(cookieStore);
        HttpResponse response = null;
        try {
            HttpPost httpPost = new HttpPost("http://dot-ffp.spbgut.ru/question/question.php");
            List<NameValuePair> nvps = getDefoultFormPairs(cmid);
            nvps.addAll(question.getFormItems());
            nvps.forEach(System.out::println);
            httpPost = setHeadersForQuestionPost(httpPost);
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
            response = httpClient.execute(httpPost);
            System.out.println(getResponseAsString(response));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public HttpResponse fastSendQuestion(Question question, String cmid, CloseableHttpClient httpClient) {
        HttpResponse response = null;
        try {
            HttpPost httpPost = new HttpPost("http://dot-ffp.spbgut.ru/question/question.php");
            List<NameValuePair> nvps = getDefoultFormPairs(cmid);
            nvps.addAll(question.getFormItems());
            nvps.forEach(System.out::println);
            httpPost = setHeadersForQuestionPost(httpPost);
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
            response = httpClient.execute(httpPost);
            System.out.println(getResponseAsString(response));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public List<NameValuePair> getDefoultFormPairs(String cmid) {
        String url = "http://dot-ffp.spbgut.ru" +
                "/question/question.php?" +
                "returnurl=%2Fmod%2Fquiz%2Fedit.php%3F" +
                "cmid%3D" + cmid + "%26cat%3D3340%252C6140%26" +
                "qpage%3D0%26addonpage%3D1&" +
                "cmid=" + cmid + "&appendqnumstring=addquestion&" +
                "category=3340&qtype=multichoice&scrollpos=200";
        return getQuestionEditFormPairs(getResponseAsString(getPage(url, "")));
    }

    public HttpPost setHeadersForQuestionPost(HttpPost httpRequest) {
        httpRequest.setHeader("Referer", "http://dot-ffp.spbgut.ru/question/question.php?returnurl=%2Fmod%2Fquiz%2Fedit.php%3Fcmid%3D2939%26addonpage%3D1&cmid=2939&appendqnumstring=addquestion&category=3340&qtype=multichoice&scrollpos=200");
        return httpRequest;
    }

    public List<NameValuePair> getQuestionAnswerFormPairs() {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("name", "Testing question"));
        nvps.add(new BasicNameValuePair("category", "3340,6140"));
        nvps.add(new BasicNameValuePair("questiontext[text]", "<p>automaicly added question<p>"));
        nvps.add(new BasicNameValuePair("numhints", "0"));
        nvps.add(new BasicNameValuePair("noanswers", "4"));
        nvps.add(new BasicNameValuePair("answer[0][text]","<p>option 1<p>"));
        nvps.add(new BasicNameValuePair("fraction[0]","1.0"));
        nvps.add(new BasicNameValuePair("answer[1][text]","<p>option 2<p>"));
        nvps.add(new BasicNameValuePair("fraction[1]","0.0"));
        nvps.add(new BasicNameValuePair("answer[2][text]","<p>option 3<p>"));
        nvps.add(new BasicNameValuePair("fraction[2]","0.0"));
        nvps.add(new BasicNameValuePair("answer[3][text]","<p>option 4<p>"));
        nvps.add(new BasicNameValuePair("fraction[3]","0.0"));
//        nvps.add(new BasicNameValuePair("answer[4][text]","<p>option 5<p>"));
//        nvps.add(new BasicNameValuePair("fraction[4]","0.0"));
//        nvps.add(new BasicNameValuePair("feedback[0][text]","<p>feedback 1<p>"));
//        nvps.add(new BasicNameValuePair("feedback[1][text]","<p>feedback 2<p>"));
//        nvps.add(new BasicNameValuePair("feedback[2][text]","<p>feedback 3<p>"));
//        nvps.add(new BasicNameValuePair("feedback[3][text]","<p>feedback 4<p>"));
//        nvps.add(new BasicNameValuePair("feedback[4][text]","<p>feedback 4<p>"));
//        nvps.add(new BasicNameValuePair("hint[0][text]","<p>hint 1<p>"));
//        nvps.add(new BasicNameValuePair("hint[1][text]","<p>hint 2<p>"));
        nvps.add(new BasicNameValuePair("generalfeedback[text]","<p>configuring automatic question postiong<p>"));
        nvps.add(new BasicNameValuePair("correctfeedback[text]","<p>Ваш ответ верный.</p>"));
        nvps.add(new BasicNameValuePair("incorrectfeedback[text]","<p>Ваш ответ неверный.</p>"));
        nvps.add(new BasicNameValuePair("partiallycorrectfeedback[text]","<p>Ваш ответ частично верный.</p>"));
        nvps.add(new BasicNameValuePair("answernumbering","none"));
        return nvps;
    }

    public List<NameValuePair> getQuestionEditFormPairs(String response) {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        Document doc = Jsoup.parse(response);
        Elements inputs = doc.getElementsByClass("region-content")
                        .first()
                        .getElementsByTag("input");
        //inputs.forEach(input -> System.out.println(input.attr("name") + " : " + input.attr("value")));
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

    public static String getSessKey(String response) {
        Document doc = Jsoup.parse(response);
        String sesskey = doc.getElementsByClass("singlebutton")
                        .first()
                        .getElementsByTag("input")
                        .get(2)
                        .attr("value");
        System.out.println(sesskey);
        return sesskey;
    }

    public CloseableHttpClient getHttpClient(BasicCookieStore cookieStore) {
        CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultCookieStore(cookieStore)
                .build();
        return httpclient;
    }

    public CloseableHttpClient getHttpClient() {
        cookieStore = new BasicCookieStore();
        CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultCookieStore(cookieStore)
                .build();
        return httpclient;
    }

    public BasicCookieStore getCookieStore() {
        return cookieStore;
    }
}
