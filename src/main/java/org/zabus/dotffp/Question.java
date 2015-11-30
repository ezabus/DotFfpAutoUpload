package org.zabus.dotffp;

import java.util.LinkedList;
import java.util.List;

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
