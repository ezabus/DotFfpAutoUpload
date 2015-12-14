package org.zabus.dotffp;

import org.apache.http.NameValuePair;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.wp.usermodel.Paragraph;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.*;
import org.zabus.dotffp.util.ParserUtils;
import org.zabus.dotffp.util.ResponseUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by user on 25.11.2015.
 */
public class DocParser {

    public static final String upperCaseAlphobet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String lowerCaseAlphobet = "abcdefghijklmnopqrstuvwxyz";
    public static final String typoAlphobet = "аbсdеfghijklmnopqrstuvwxyz";
    
//    public static void main(String[] args) {
//        List<Question> questions = getQuestions(args[0]);//.forEach(System.out::println);
//        //List<NameValuePair> items =  question.getFormItems();
//        //items.forEach(System.out::println);
//        Uploader uploader = new Uploader();
//        uploader.login("zabus", "ZaBUS12$)");
//        uploader.initSesskey("506");
//        CloseableHttpClient client = uploader.getHttpClient(uploader.getCookieStore());
//        questions.forEach(question ->
//                uploader.fastSendQuestion(question, "2941", client));
//        //uploader.sendQuestion(question,"2941");
////        List<XWPFTableRow> rows = getTable(getDoc(args[0])).getRows();
////        rows.stream().skip(2).forEach(DocParser::getWrightAnswers);
//    }

    public static void main(String args[]) {
        List<Question> questions = getQuestions(args[0]);
        questions = setTopics(questions);
        Map<String,List<Question>> topicQuestion = questions.stream().collect(Collectors.groupingBy(Question::getTopicName));
        topicQuestion.forEach((topicName,questionList) -> System.out.println(topicName + " " + questionList.size()));
        questions.forEach(System.out::println);
        MoodleClient client = new MoodleClient();
        client.login("zabus", "ZaBUS12$)");
        client.initSesskey("506");
        //client.createTopic("The topic", client.postJump("506"));
        //client.sendQuestion(questions.get(1), "2941");
        //questions.forEach(question -> client.sendQuestion(question, "2941"));
    }

    public static Map<String, List<Question>> getTopicQuestionMap(String pathToFile) {
        List<Question> questions = getQuestions(pathToFile);
        questions = setTopics(questions);
        return questions.stream().collect(Collectors.groupingBy(Question::getTopicName));
    }

    public static List<Question> getQuestions(String path) {
        List<XWPFTableRow> rows = getTable(getDoc(path)).getRows();
        List<Question> questions = new LinkedList<Question>();
        rows.stream().skip(2).forEach(row -> {
//            questions.add(new Question(getNumberOfQuestion(row), getQuestionName(row), getOptionA(row),
//                    getOptionB(row), getOptionC(row), getOptionD(row)));
            Question question = new Question(getNumberOfQuestion(row), getQuestionText(row), getOptionsList(row),getWrightAnswers(row));
            question.setTopicName(getTopicName(row));
            questions.add(question);
        });
        return questions;
    }

    public static List<Question> setTopics(List<Question> questions) {
        String curTopic = questions.get(0).getTopicName();
        for(Question question : questions) {
            if(!question.getTopicName().equals(curTopic) && !question.getTopicName().equals("")) {
                curTopic = question.getTopicName();
            }
            question.setTopicName(curTopic);
        }
        return questions;
    }

    public static String getTopicName(XWPFTableRow row) {
        return ParserUtils.trimTopicName(row.getTableCells().get(0).getText());
    }

    public static XWPFDocument getDoc(String path) {
        XWPFDocument xdoc = null;
        try {
            FileInputStream fis = new FileInputStream(path);
            xdoc = new XWPFDocument(OPCPackage.open(fis));
        } catch (IOException | InvalidFormatException e) {
            e.printStackTrace();
        }
        return xdoc;
    }

    public static XWPFTable getTable(XWPFDocument xdoc) {
        return xdoc.getTables().get(0);
    }

    public static Iterable<XWPFTableRow> getRows(XWPFTable table) {
        return table.getRows();
    }

    public static Integer getNumberOfQuestion(XWPFTableRow row) {
        return Integer.parseInt(row.getTableCells().get(1).getText().trim());
    }

    public static String getQuestionText(XWPFTableRow row) {
        return row.getTableCells().get(2).getText();
    }

    public static List<String> getOptionsList(XWPFTableRow row) {
        List<String> options = new LinkedList<String>();
        options.add(row.getTableCells().get(3).getText());
        options.add(row.getTableCells().get(4).getText());
        options.add(row.getTableCells().get(5).getText());
        options.add(row.getTableCells().get(6).getText());
        return options;
    }

    public static String getOptionA(XWPFTableRow row) {
        return row.getTableCells().get(3).getText();
    }

    public static String getOptionB(XWPFTableRow row) {
        return row.getTableCells().get(4).getText();
    }

    public static String getOptionC(XWPFTableRow row) {
        return row.getTableCells().get(5).getText();
    }

    public static String getOptionD(XWPFTableRow row) {
        return row.getTableCells().get(6).getText();
    }

    public static List<Integer> getWrightAnswers(XWPFTableRow row) {
        String answerString = row.getTableCells().get(7).getText().trim();
        List<String> wrightAnswerLetteers = new ArrayList<>(Arrays.asList(answerString.split(",")));
        //String[] wrightAnswerLetters = Arrays.(answerString.split(","));
//        for(String answers : wrightAnswerLetters) {
//            answers = answers.trim();
//            System.out.print(answers);
//        }
//        System.out.println();
        List<Integer> wrightAnswers = new LinkedList<Integer>();
        wrightAnswerLetteers.stream().forEach(wrightAnswer -> {
            wrightAnswer = wrightAnswer.trim();
            wrightAnswers.add(getNumericPositionOfCharacterInAlphobet(wrightAnswer.charAt(0)));
        });

        return wrightAnswers;
    }

    public static int getNumericPositionOfCharacterInAlphobet(char ch) {
        int pos = lowerCaseAlphobet.indexOf(ch);
        if(pos == -1) {
            pos = upperCaseAlphobet.indexOf(ch);
        }
        if(pos == -1) {
            pos = typoAlphobet.indexOf(ch);
        }
        if(pos == -1) {
            pos = Integer.parseInt(String.valueOf(ch));
        }
        return pos;
    }
}
