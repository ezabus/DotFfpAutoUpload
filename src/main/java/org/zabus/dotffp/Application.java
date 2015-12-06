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
//        MoodleClient client = new MoodleClient();
//        client.login("zabus", "ZaBUS12$)");
//        client.initSesskey("506");
        //DocParser.getTopicQuestionMap(args[0]).forEach((topicName, questionList) ->
        //        client.createTopic(topicName, "506"));
        Map<String, List<Question>> topicQuestions = DocParser.getTopicQuestionMap(args[0]);
        topicQuestions.forEach((k,v) -> System.out.println(k + " " + v.size()));
//        AtomicInteger count = new AtomicInteger();
//        topicQuestions.forEach((topicName, questionList) -> client.createTopic(topicName, "506", count.incrementAndGet()));
    }
}
