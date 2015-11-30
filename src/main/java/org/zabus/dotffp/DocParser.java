package org.zabus.dotffp;

import org.apache.http.NameValuePair;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.wp.usermodel.Paragraph;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by user on 25.11.2015.
 */
public class DocParser {

    public static final String upperCaseAlphobet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String lowerCaseAlphobet = "abcdefghijklmnopqrstuvwxyz";
    public static final String typoAlphobet = "аbсdеfghijklmnopqrstuvwxyz";
//    public static void main(String[] args) {
//        try {
//            FileInputStream fis = new FileInputStream("C:/Users/user/Documents/Study/Kurs4/boltov/kpo.docx");
//            XWPFDocument xdoc = new XWPFDocument(OPCPackage.open(fis));
//            XWPFHeaderFooterPolicy policy = new XWPFHeaderFooterPolicy(xdoc);
//            XWPFTable table = xdoc.getTables().get(0);
//            Iterable<XWPFTableRow> rows = table.getRows();
//            int i = 0;
//            for(XWPFTableRow row : rows) {
//                Iterable<XWPFTableCell> cells = row.getTableCells();
//                int j = 0;
//                for(XWPFTableCell cell : cells) {
//                    System.out.println(i + "." + j + " " + cell.getText());
//                    j++;
//                }
//                i++;
//            }
//        } catch(Exception ex) {
//            ex.printStackTrace();
//        }
//    }

    public static void main(String[] args) {
        List<Question> questions = getQuestions(args[0]);//.forEach(System.out::println);
        Question question = questions.get(3);
        List<NameValuePair> items =  question.getFormItems();
        items.forEach(System.out::println);
        Uploader uploader = new Uploader();
        uploader.login("zabus", "ZaBUS12$)");
        uploader.initSesskey("506");
        uploader.sendQuestion(question,"2939");
//        List<XWPFTableRow> rows = getTable(getDoc(args[0])).getRows();
//        rows.stream().skip(2).forEach(DocParser::getWrightAnswers);
    }

    public static List<Question> getQuestions(String path) {
        List<XWPFTableRow> rows = getTable(getDoc(path)).getRows();
        List<Question> questions = new LinkedList<Question>();
        rows.stream().skip(2).forEach(row -> {
//            questions.add(new Question(getNumberOfQuestion(row), getQuestionText(row), getOptionA(row),
//                    getOptionB(row), getOptionC(row), getOptionD(row)));
            questions.add(new Question(getNumberOfQuestion(row), getQuestionText(row), getOptionsList(row),getWrightAnswers(row)));
        });
        return questions;
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
        return Integer.parseInt(row.getTableCells().get(1).getText());
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
