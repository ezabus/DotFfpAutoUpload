package org.zabus.dotffp;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by user on 25.11.2015.
 */
public class Question {

    private int numberOfQuestion;
    private String questionName;
    private String topicName;
    private List<String> options;
    private List<Integer> wrightNumbers;

    public Question() {

    }

    public Question(int numberOfQuestion, String questionText, String optionA, String optionB, String optionC, String optionD) {
        this.numberOfQuestion = numberOfQuestion;
        this.questionName = questionText;
        options = new LinkedList<>();
        options.add(optionA);
        options.add(optionB);
        options.add(optionC);
        options.add(optionD);
    }

    public Question(int numberOfQuestion, String questionText, List<String> options, List<Integer> wrightNumbers) {
        this.numberOfQuestion = numberOfQuestion;
        this.questionName = questionText;
        this.options = options;
        this.wrightNumbers = wrightNumbers;
    }

    public List<NameValuePair> getFormItems() {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("name", questionName));
        nvps.add(new BasicNameValuePair("questiontext[text]", "<p>" + questionName + "<p>"));
        nvps.add(new BasicNameValuePair("numhints", "0"));
        nvps.add(new BasicNameValuePair("noanswers", String.valueOf(options.size())));
        nvps.add(new BasicNameValuePair("correctfeedback[text]","<p>Ваш ответ верный.</p>"));
        nvps.add(new BasicNameValuePair("incorrectfeedback[text]","<p>Ваш ответ неверный.</p>"));
        nvps.add(new BasicNameValuePair("partiallycorrectfeedback[text]","<p>Ваш ответ частично верный.</p>"));
        nvps.add(new BasicNameValuePair("answernumbering","none"));
        nvps.add(new BasicNameValuePair("single","0"));
        for(int i = 0; i < options.size(); i++) {
            nvps.add(new BasicNameValuePair("answer[" + i + "][text]", "<p>" + options.get(i) + "<p>"));
        }
        nvps.addAll(getFractions());
        return nvps;
    }

    public String getQuestionName() {
        return (wrightNumbers.size() > 1) ? "Выберите один или несколько ответов:" : "Выберите один ответ:";
    }

    public List<NameValuePair> getFractions() {
        List<String> fractions = setFractionCost(initFractions());
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        int k = 0;
        for(String fraction : fractions) {
            nvps.add(new BasicNameValuePair("fraction[" + k + "]", fraction));
            k++;
        }
        return nvps;
    }

    public List<String> initFractions() {
        List<String> fractions = new ArrayList<String>();
        for(int i = 0; i < options.size(); i++) {
            fractions.add("0.0");
        }
        return fractions;
    }

    public List<String> setFractionCost(List<String> fractions) {
        String fraction = getFraction();
        for(Integer i : wrightNumbers) {
            fractions.set(i, String.valueOf(fraction));
        }
        return fractions;
    }

    public String getFraction() {
        double fraction =  1.0 / (double) wrightNumbers.size();
        int temp = (int) (fraction * 10000000);
        fraction = (double) temp / (double) 10000000;
        //return String.format("%.7f", fraction).replaceFirst(",",".");
        return String.valueOf(fraction);
    }

    public void addWrightQuestion(int numberOfQuestion) {
        wrightNumbers.add(numberOfQuestion);
    }

    public int getNumberOfQuestion() {
        return numberOfQuestion;
    }

    public void setNumberOfQuestion(int numberOfQuestion) {
        this.numberOfQuestion = numberOfQuestion;
    }

    public String getQuestionText() {
        return questionName;
    }

    public void setQuestionName(String questionName) {
        this.questionName = questionName;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public List<Integer> getWrightNumbers() {
        return wrightNumbers;
    }

    public void setWrightNumbers(List<Integer> wrightNumbers) {
        this.wrightNumbers = wrightNumbers;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    @Override
    public String toString() {
        return "Question{" +
                "numberOfQuestion=" + numberOfQuestion +
                ", topicName='" + topicName + '\'' +
                ", questionName='" + questionName + '\'' +
                ", options=" + options +
                ", wrightNumbers=" + wrightNumbers +
                '}';
    }
}
