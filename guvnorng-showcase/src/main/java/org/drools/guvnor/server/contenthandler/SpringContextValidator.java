package org.drools.guvnor.server.contenthandler;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * The Meaning of this class is to validate the Syntax of the Spring Context XML FILE.
 */
public class SpringContextValidator {

    private static final String JAXP_SCHEMA_LANGUAGE =
            "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    private static final String W3C_XML_SCHEMA =
            "http://www.w3.org/2001/XMLSchema";
    private InputStream content;

    public void setContent(InputStream content) {
        this.content = content;
    }

    public void setContentAsString(String content) {
        this.content = new ByteArrayInputStream(content.getBytes());
    }

    public String validate() {
        // Create a new factory to create parsers that will
        // be aware of namespaces and will validate or
        // not according to the flag setting.
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(true);
        dbf.setNamespaceAware(true);

        dbf.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);

        try {
            DocumentBuilder builder = dbf.newDocumentBuilder();
            builder.setErrorHandler(new MyErrorHandler());
            builder.parse(this.content);
        } catch (SAXException e) {
            return e.getMessage();
        } catch (ParserConfigurationException e) {
            return e.getMessage();
        } catch (IOException e) {
            return e.getMessage();
        }


        return "";
    }

    class MyErrorHandler implements ErrorHandler {

        public void warning(SAXParseException e) throws SAXException {
            show("Warning", e);
            throw (e);
        }

        public void error(SAXParseException e) throws SAXException {
            show("Error", e);
            throw (e);
        }

        public void fatalError(SAXParseException e) throws SAXException {
            show("Fatal Error", e);
            throw (e);
        }

        private void show(String type, SAXParseException e) {
            System.out.println(type + ": " + e.getMessage());
            System.out.println("Line " + e.getLineNumber() + " Column "
                    + e.getColumnNumber());
            System.out.println("System ID: " + e.getSystemId());
        }
    }
}
