package com.example.demo.controller;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.OutputStream;
import java.io.StringWriter;

public class HelloWorld {

    public static void main(String[] args) {
        try {

            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document xmlDocument = builder.parse("C:\\Users\\a156400\\Desktop\\DentalH710588196callupgrade.xml");
            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();
            XPathExpression expr;

            //Step 1 : Get AppEntryRequest Object
            expr = xpath.compile("/compositeAsync/ApplicationEntryRequest");
            Node node = (Node) expr.evaluate(xmlDocument, XPathConstants.NODE);
            System.out.println(node);

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            // root elements
            Document docW = docBuilder.newDocument();
            Node firstDocImportedNode = docW.importNode(node, true);
            docW.appendChild(firstDocImportedNode );

            System.out.println(writeXml(docW));
            // Convert using JAXB UnMarshallers to ApplicationEntryRequest object.

            // get a policy # from ApplicationEntryRequest

            //todo: You can replace * with Policy #
            expr = xpath.compile("compositeAsync/*/callUpgradeEligible[text()]");
            String eligibleText = (String) expr.evaluate(xmlDocument, XPathConstants.STRING);
            System.out.println("Is Eligible" + eligibleText);

            //todo: You can replace * with Policy #
            expr = xpath.compile("compositeAsync/*/*[self::Applicant or self::Spouse]//UnderwritingQuestionnairesInfo/insproUnderwritingQuestionnaire//insproUnderwritingQuestionnaireItem/answer/text()");
            NodeList nodeList2 = (NodeList) expr.evaluate(xmlDocument, XPathConstants.NODESET);
            for (int i = 0 ; i < nodeList2.getLength(); i++) {
                System.out.println(nodeList2.item(i).getTextContent());
            }
            System.out.println(nodeList2);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private static String writeXml(Document doc)
            throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        transformer.transform(source, result);
        return writer.toString();
    }



}
