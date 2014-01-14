package com.example.ProRssReader;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import java.util.ArrayList;

/**
 * Created by Hedgehog on 13.01.14.
 */
public class SaxParser extends DefaultHandler {
    private static final String ITEM = "item";
    private static final String TITLE = "title";
    private static final String DESCRIPTION = "description";
    private static final String LINK = "link";
    private static final String PUBDATE = "pubdate";

    private static final String ATOM_ITEM = "entry";
    private static final String ATOM_DESCRIPTION = "summary";
    private static final String ATOM_LINK = "id";
    private static final String ATOM_PUBDATE = "published";

    private ArrayList<RssOneItem> feedList = new ArrayList<RssOneItem>();

    public ArrayList<RssOneItem> getFeedList() {
        return feedList;
    }


    boolean isTitle = false;
    boolean isDescription = false;
    boolean isLink = false;
    boolean isPubDate = false;
    boolean inItem = false;

    String title = "";
    String description = "";
    String link = "";
    String pubdate = "";

    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, name, attributes);
        if (name.equalsIgnoreCase(ITEM) || name.equalsIgnoreCase(ATOM_ITEM) || inItem) {
            inItem = true;
            if (name.equalsIgnoreCase(TITLE)) {
                title = "";
                isTitle = true;
            }
            if (name.equalsIgnoreCase(DESCRIPTION) || name.equalsIgnoreCase(ATOM_DESCRIPTION)) {
                description = "";
                isDescription = true;
            }
            if (name.equalsIgnoreCase(LINK) || name.equalsIgnoreCase(ATOM_LINK)) {
                link = "";
                isLink = true;
            }
            if (name.equalsIgnoreCase(PUBDATE) || name.equalsIgnoreCase(ATOM_PUBDATE)) {
                pubdate = "";
                isPubDate = true;
            }
        }
    }

    @Override
    public void endElement(String uri, String localName,
                           String name) throws SAXException {
        if (name.equalsIgnoreCase(TITLE)) {
            isTitle = false;
        }
        if (name.equalsIgnoreCase(DESCRIPTION) || name.equalsIgnoreCase(ATOM_DESCRIPTION)) {
            isDescription = false;
        }
        if (name.equalsIgnoreCase(LINK) || name.equalsIgnoreCase(ATOM_LINK)) {
            isLink = false;
        }
        if (name.equalsIgnoreCase(PUBDATE) || name.equalsIgnoreCase(ATOM_PUBDATE)) {
            isPubDate = false;
        }
        if (name.equalsIgnoreCase(ITEM) || name.equalsIgnoreCase(ATOM_ITEM)) {
            RssOneItem temporaryVariable = new RssOneItem(title, description, link, pubdate);
            inItem = false;
            feedList.add(temporaryVariable);
        }
    }

    @Override
    public void characters(char chars[], int start, int length) throws SAXException {
        if (isTitle) {
            title += new String(chars, start, length);
        }
        if (isDescription) {
            description += new String(chars, start, length);
        }
        if (isLink) {
            link += new String(chars, start, length);
        }
        if (isPubDate) {
            pubdate += new String(chars, start, length);
        }
    }
}
