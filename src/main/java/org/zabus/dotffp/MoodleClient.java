package org.zabus.dotffp;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.zabus.dotffp.util.HtmlFormUtils;

import javax.lang.model.element.Name;
import java.util.ArrayList;
import java.util.List;

import static org.zabus.dotffp.util.ResponseUtils.*;
/**
 * Created by Egor on 01.12.2015.
 */
public class MoodleClient  extends HttpClient{

    //"http://dot-ffp.spbgut.ru/login/index.php"
    private String sesskey;
    public static final String loginURL = "http://dot-ffp.spbgut.ru/login/index.php";

    public static void main(String args[]) {
        MoodleClient moodleClient = new MoodleClient();
        moodleClient.login("zabus", "ZaBUS12$)");
        moodleClient.initSesskey("508");
        List<Question> questions = DocParser.getQuestions("C:/Users/user/Documents/Study/Kurs4/boltov/aisd.docx");
        List<NameValuePair> nvps = HtmlFormUtils.getDefoultFormPairs("2996",moodleClient);
        nvps.addAll(questions.get(0).getFormItems());
        nvps.forEach(System.out::println);

    }

    public BasicCookieStore login(String login, String password) {
        CloseableHttpClient httpClient = getHttpClient();
        executePost(loginURL, getLoginParams(login, password), httpClient);
        return cookieStore;
    }

    public static List<NameValuePair> getLoginParams(String login, String password) {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("username", login));
        nvps.add(new BasicNameValuePair("password", password));
        return nvps;
    }

    public HttpResponse sendQuestion(Question question, String cmid) {
        String questionURL = "http://dot-ffp.spbgut.ru/question/question.php";
        List<NameValuePair> nvps = HtmlFormUtils.getDefoultFormPairs(cmid, this);
        nvps.addAll(question.getFormItems());
        return executePost(questionURL, nvps, getHttpClient(cookieStore));
    }

    public void sendQuestions(Iterable<Question> questions, String cmid) {
        CloseableHttpClient httpClient = getHttpClient(cookieStore);
        questions.forEach(question -> {
            List<NameValuePair> nvps = HtmlFormUtils.getDefoultFormPairs(cmid, this);
            nvps.addAll(question.getFormItems());
            executePost("http://dot-ffp.spbgut.ru/question/question.php", nvps, httpClient);
        });
    }

    public void printTopicCreateForm() {
        List<NameValuePair> items =
                HtmlFormUtils.getQuestionEditFormPairs(getResponseAsString(executeGet("http://dot-ffp.spbgut.ru/course/modedit.php?add=quiz&type=&course=506&section=3&return=0&sr=0", "")));
        items.forEach(System.out::println);
    }

    public int createTopic(String topicName, String courseID, int section) {
        return getIdOfTopic(createTopic(topicName, postJump(courseID, section)), section);
    }

    public HttpResponse createTopic(String name, HttpResponse jumpResponse) {
        List<NameValuePair> nvps =
        //        HtmlFormUtils.getTopicEditForm(getResponseAsString(executeGet("http://dot-ffp.spbgut.ru/course/modedit.php?add=quiz&type=&course=506&section=3&return=0&sr=0", "")));
                HtmlFormUtils.getTopicEditForm(getResponseAsString(jumpResponse));
        nvps.add(new BasicNameValuePair("name", name));
        nvps.add(new BasicNameValuePair("navmethod", "free"));
        nvps.add(new BasicNameValuePair("overduehandling", "autosubmit"));
        nvps.add(new BasicNameValuePair("preferredbehaviour", "deferredfeedback"));
        nvps.add(new BasicNameValuePair("questiondecimalpoints", "-1"));
        nvps.add(new BasicNameValuePair("questionsperpage", "0"));
        nvps.add(new BasicNameValuePair("gradecat", "482"));
        nvps.add(new BasicNameValuePair("grademethod", "1"));
        nvps.add(new BasicNameValuePair("groupingid", "0"));
        nvps.add(new BasicNameValuePair("attemptonlast", "0"));
        nvps.add(new BasicNameValuePair("browsersecurity", "-"));
        nvps.add(new BasicNameValuePair("visible", "1"));
        nvps.add(new BasicNameValuePair("showblocks", "0"));
        nvps.add(new BasicNameValuePair("showuserpicture", "0"));
        nvps.add(new BasicNameValuePair("shuffleanswers", "1"));
        nvps.add(new BasicNameValuePair("shufflequestions", "1"));
        nvps.add(new BasicNameValuePair("decimalpoints", "2"));
        nvps.add(new BasicNameValuePair("feedbacktext[0][text]", ""));
        nvps.add(new BasicNameValuePair("feedbacktext[1][text]", ""));
        nvps.add(new BasicNameValuePair("feedbacktext[2][text]", ""));
        nvps.add(new BasicNameValuePair("feedbacktext[3][text]", ""));
        nvps.add(new BasicNameValuePair("feedbacktext[4][text]", ""));
        nvps.add(new BasicNameValuePair("introeditor[text]", ""));
        nvps.add(new BasicNameValuePair("attempts", "0"));
        nvps.forEach(System.out::println);
        return executePost("http://dot-ffp.spbgut.ru/course/modedit.php", nvps, getHttpClient(cookieStore));
    }

    public HttpResponse postJump(String courseID, int section) {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("course", courseID));
        nvps.add(new BasicNameValuePair("jump", "http://dot-ffp.spbgut.ru/course/mod.php?id=" +
                 courseID +  "&sesskey=" + sesskey + "&sr=0&add=quiz&section=" + section));
        nvps.add(new BasicNameValuePair("sesskey", sesskey));
        return executePost("http://dot-ffp.spbgut.ru/course/jumpto.php", nvps, getHttpClient(cookieStore));
    }

    public void initSesskey(String courseID) {
        setSesskey(getSessKey(getResponseAsString(
                executeGet("http://dot-ffp.spbgut.ru/course/view.php", "?id=" + courseID))));
    }

    public String getSessKey(String response) {
        Document doc = Jsoup.parse(response);
        String sesskey = doc.getElementsByClass("singlebutton")
                .first()
                .getElementsByTag("input")
                .get(2)
                .attr("value");
        System.out.println(sesskey);
        return sesskey;
    }

    public String getSesskey() {
        return sesskey;
    }

    public void setSesskey(String sesskey) {
        this.sesskey = sesskey;
    }
}
