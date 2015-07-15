package com.metatrope.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import com.google.common.io.ByteStreams;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XPathUtil {
    private static final XPath xpath = XPathFactory.newInstance().newXPath();
    private static final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

    private final Document doc;

    public XPathUtil(String xml) {
        this.doc = parseDomDocument(xml);
    }

    public XPathUtil(InputStream is) {
        try {
            this.doc = parseDomDocument(new String(ByteStreams.toByteArray(is)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private Document parseDomDocument(String xml) {
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(new InputSource(new StringReader(xml)));
        } catch (IOException | SAXException | ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public String getString(String path) {
        return getString(doc, path);
    }
    
    public String getString(Node node, String path) {
        try {
            XPathExpression xpe = xpath.compile(path);
            return (String) xpe.evaluate(node, XPathConstants.STRING);
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }
    }
    
    public NodeList getNodes(String path) {
        try {
            XPathExpression xpe = xpath.compile(path);
            return (NodeList) xpe.evaluate(doc, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }
    }
    
}
