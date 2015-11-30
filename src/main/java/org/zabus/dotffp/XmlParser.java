package org.zabus.dotffp;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.Scanner;

/**
 * Created by user on 24.11.2015.
 */
public class XmlParser {

    public File getFile(String path) {
        return new File(path);
    }

    public String getContent(File file) {
        String content = "";
        try {
            Scanner sc = new Scanner(file);
            StringBuilder stringBuilder = new StringBuilder();
            while (sc.hasNextLine()) {
                stringBuilder.append(sc.nextLine());
            }
            content = new String(stringBuilder);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return content;
    }

    public Document getDocument(File file) {
        Document doc = null;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(new InputSource(new StringReader(getContent(file))));
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        return doc;
    }
}
