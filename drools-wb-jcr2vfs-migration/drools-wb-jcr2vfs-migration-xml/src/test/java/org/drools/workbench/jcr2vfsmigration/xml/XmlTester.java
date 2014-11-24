package org.drools.workbench.jcr2vfsmigration.xml;

import java.io.StringReader;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.drools.workbench.jcr2vfsmigration.xml.format.CategoriesXmlFormat;
import org.drools.workbench.jcr2vfsmigration.xml.format.ModulesXmlFormat;
import org.drools.workbench.jcr2vfsmigration.xml.model.Categories;
import org.drools.workbench.jcr2vfsmigration.xml.model.Modules;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class XmlTester {

    private String modulesXml = "<modules><module><type>normal</type><name>mod1</name><uuid>uuid1</uuid></module>" +
            "<module><type>normal</type><name>mod2</name><uuid>uuid2</uuid></module>" +
            "<module><type>normal</type><name>mod3</name><uuid>uuid3</uuid></module>" +
            "<module><type>global</type><name>globalmodule</name><uuid>global</uuid></module></modules>";

    private String categoriesXml = "<categories><category name=\"Home Mortgage\"><categories><category name=" +
            "\"Eligibility rules\"></category><category name=\"Pricing rules\"></category><category name=" +
            "\"Test scenarios\"></category><category name=\"Technical\"><categories><category name=\"Sub_tech\">" +
            "<categories><category name=\"Subsub_tech\"></category></categories></category></categories></category>" +
            "</categories></category><category name=\"Commercial Mortgage\"></category></categories>\n";

    private String mapXml = "<catRules><map><entry><key>k1</key><value>v1</value></entry><entry><key>k2</key><value>v2" +
            "</value></entry><entry><key>k3</key><value>v3</value></entry></map></catRules>";

    public void testModules() {
        ModulesXmlFormat modulesXmlFormat = new ModulesXmlFormat();
        InputSource is = new InputSource( new StringReader( modulesXml ) );
        Document xml = null;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            xml = db.parse( is );
            NodeList children = xml.getChildNodes();
            if ( children.getLength() != 1 ) throw new IllegalArgumentException( "Wrong xml format" );
            Node node = children.item( 0 );
            if (node != null && "modules".equals( node.getNodeName())) {
                Modules modules = modulesXmlFormat.parse( node );

                StringBuilder sb = new StringBuilder();
                modulesXmlFormat.format( sb, modules );
                System.out.println( sb.toString() );
            }
            return;
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    public void testCategories() {
        CategoriesXmlFormat categoriesXmlFormat = new CategoriesXmlFormat();
        InputSource is = new InputSource( new StringReader( categoriesXml ) );
        Document xml = null;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            xml = db.parse( is );
            NodeList children = xml.getChildNodes();
            if ( children.getLength() != 1 ) throw new IllegalArgumentException( "Wrong xml format" );
            Node node = children.item( 0 );
            if (node != null && "categories".equals( node.getNodeName())) {
                Categories categories = categoriesXmlFormat.parse( node );

                StringBuilder sb = new StringBuilder();
                categoriesXmlFormat.format( sb, categories );
                System.out.println( sb.toString() );
            }
            return;
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    public void testMap() {
        ExportXmlUtils xmlUtils = new ExportXmlUtils();
        InputSource is = new InputSource( new StringReader( mapXml ) );
        Document xml = null;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            xml = db.parse( is );
            NodeList children = xml.getChildNodes();
            if ( children.getLength() != 1 ) throw new IllegalArgumentException( "Wrong xml format" );
            Node catRulesNode = children.item( 0 );
            NodeList catRulesChildren = catRulesNode.getChildNodes();
            if ( catRulesChildren.getLength() != 1 ) throw new IllegalArgumentException( "Wrong xml format" );
            Node mapNode = catRulesChildren.item( 0 );
            Map<String, String> map = xmlUtils.parseMap( mapNode );
            System.out.println( map );
            System.out.println( xmlUtils.formatMap( map ) );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    public static void main( String[] args ) {
        XmlTester xt = new XmlTester();
        xt.testModules();
        xt.testCategories();
        xt.testMap();
    }
}
