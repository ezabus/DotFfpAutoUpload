package org.zabus.dotffp;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Created by user on 24.11.2015.
 */
public class TableParser extends XmlParser {

    public static void main(String args[]) {
        TableParser parser = new TableParser();
        String path = "C:/Users/user/Documents/Study/Kurs4/boltov/kpo.xml";
//        System.out.println(parser.getContent(parser.getFile(path)));
        Document doc = parser.getDocument(parser.getFile(path));
        Node table = parser.getTable(path);
        System.out.println(doc.getTextContent());
    }

    public Node getTable(String path) {
        Document doc = getDocument(getFile(path));
        return doc.getElementsByTagName("tbl").item(0);
    }
}
