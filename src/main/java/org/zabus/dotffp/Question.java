package org.zabus.dotffp;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by user on 25.11.2015.
 */
public class Question {

    private int numberOfQuestion;
    private String questionText;
    private List<String> options;
    private List<Integer> wrightNumbers;

    public Question(int numberOfQuestion, String questionText, String optionA, String optionB, String optionC, String optionD) {
        this.numberOfQuestion = numberOfQuestion;
        this.questionText = questionText;
        options = new LinkedList<>();
        options.add(optionA);
        options.add(optionB);
        options.add(optionC);
        options.add(optionD);
    }

    public Question(int numberOfQuestion, String questionText, List<String> options, List<Integer> wrightNumbers) {
        this.numberOfQuestion = numberOfQuestion;
        this.questionText = questionText;
        this.options = options;
        this.wrightNumbers = wrightNumbers;
    }

    public List<NameValuePair> getFormItems() {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("name", questionText));
        nvps.add(new BasicNameValuePair("category", "3340,6140"));
        nvps.add(new BasicNameValuePair("questiontext[text]", "<p>" + questionText + "<p>"));
        nvps.add(new BasicNameValuePair("numhints", "0"));
        nvps.add(new BasicNameValuePair("noanswers", String.valueOf(options.size())));
        nvps.add(new BasicNameValuePair("correctfeedback[text]","<p>Ваш ответ верный.</p>"));
        nvps.add(new BasicNameValuePair("incorrectfeedback[text]","<p>Ваш ответ неверный.</p>"));
        nvps.add(new BasicNameValuePair("partiallycorrectfeedback[text]","<p>Ваш ответ частично верный.</p>"));
        nvps.add(new BasicNameValuePair("answernumbering","none"));
        nvps.addAll(options.stream().map(option -> new BasicNameValuePair("answer[" + 1 + "][text]", "<p>" + option + "<p>")).collect(Collectors.toList()));
        nvps.addAll(getFractions());
        return nvps;
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
            fractions.add("0");
        }
        return fractions;
    }

    public List<String> setFractionCost(List<String> fractions) {
        double fraction = 1 / wrightNumbers.size();
        for(Integer i : wrightNumbers) {
            fractions.set(i, String.valueOf(fraction));
        }
        return fractions;
    }

    public int getNumberOfQuestion() {
        return numberOfQuestion;
    }

    public void setNumberOfQuestion(int numberOfQuestion) {
        this.numberOfQuestion = numberOfQuestion;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
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

    @Override
    public String toString() {
        return "Question{" +
                "numberOfQuestion=" + numberOfQuestion +
                ", questionText='" + questionText + '\'' +
                ", options=" + options +
                ", wrightNumbers=" + wrightNumbers +
                '}';
    }
}
