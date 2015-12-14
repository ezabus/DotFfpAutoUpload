package org.zabus.dotffp;

import javax.print.Doc;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * Created by user on 03.12.2015.
 */
public class Application {

    public static void main(String args[]) {
        MoodleClient client = new MoodleClient();
        client.login("zabus", "ZaBUS12$)");
        String courseID = getCourseID(args[1]);
        client.initSesskey(courseID);
        upload(client,getCourseID(args[1]),args[0]);
        //printQuestions(DocParser.getTopicQuestionMap(args[0]));
        //DocParser.getTopicQuestionMap(args[0]).forEach((topicName, questionList) ->
        //        client.createTopic(topicName, "506"));
//        List<Question> questions = DocParser.getQuestions(args[0]);
//        int topicID = client.createTopic(questions.get(0).getTopicName(),"506",1);
//        System.out.println("topicID = " + topicID);
//        client.sendQuestions(questions, String.valueOf(topicID));

    }


    public static void printQuestions(Map<String, List<Question>> topicQuestion) {
        topicQuestion.forEach((k,v) -> {
            System.out.println(">>" + k + " " + v.size());
            v.forEach(System.out::println);
        });
    }

    public static void upload(MoodleClient client, String courseID, String fileName) {
        Map<String, List<Question>> topicQuestions = DocParser.getTopicQuestionMap(fileName);
        //topicQuestions.forEach((k,v) -> System.out.println(k + " " + v.size()));
        AtomicInteger count = new AtomicInteger();
        topicQuestions.forEach((topicName, questionList) -> {
            int topicID = client.createTopic(topicName, courseID, count.incrementAndGet());
            questionList.forEach(question -> client.sendQuestion(question, String.valueOf(topicID)));
        });
    }

    public static String getCourseID(String url) {
        return url.substring(url.lastIndexOf('=') + 1);
    }
}
