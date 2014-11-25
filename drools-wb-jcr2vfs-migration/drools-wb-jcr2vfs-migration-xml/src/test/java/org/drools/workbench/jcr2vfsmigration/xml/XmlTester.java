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

    private String modulesXml = "<modules><module><uuid>e5645c2f-f79b-409a-a765-913fd054c9e2</uuid><type>GLOBAL</type>" +
            "<name>globalArea</name><catRules></catRules></module><module><uuid>d1be22d8-4cef-416d-bd2f-98d4bf59c687</uuid>" +
            "<type>NORMAL</type><name>class.jantest.import.package</name><catRules></catRules></module><module>" +
            "<uuid>fae2b929-c5ef-4ce5-9fa1-514779ca0ae3</uuid><type>NORMAL</type><name>defaultPackage</name><catRules>" +
            "</catRules></module><module><uuid>da98caef-e1c4-4f98-880c-46a740c9131f</uuid><type>NORMAL</type>" +
            "<name>mortgages</name><catRules></catRules></module></modules>";

    private String categoriesXml = "<categories><category name=\"Home Mortgage\"><categories><category name=" +
            "\"Eligibility rules\"></category><category name=\"Pricing rules\"></category><category name=" +
            "\"Test scenarios\"></category><category name=\"Technical\"><categories><category name=\"Sub_tech\">" +
            "<categories><category name=\"Subsub_tech\"></category></categories></category></categories></category>" +
            "</categories></category><category name=\"Commercial Mortgage\"></category></categories>\n";

    private String mapXml = "<catRules><map><entry><key>k1</key><value>v1</value></entry><entry><key>k2</key><value>v2" +
            "</value></entry><entry><key>k3</key><value>v3</value></entry></map></catRules>";

    private String packageHeaderModulesXml = "<modules><module><uuid>id1</uuid><type>GLOBAL</type><name>testModule</name>" +
            "<catRules></catRules>" +
            "<packageHeaderInfo><![CDATA[function matchwo(a,b)\n" +
            "{\n" +
            "if (a < b && a < 0) then\n" +
            "  {\n" +
            "  return 1;\n" +
            "  }\n" +
            "else\n" +
            "  {\n" +
            "  return 0;\n" +
            "  }\n" +
            "}]]></packageHeaderInfo><catRules></catRules></module></modules>";

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
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    public void testMap() {
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
            Map<String, String> map = ExportXmlUtils.parseMap( mapNode );
            System.out.println( map );
            System.out.println( ExportXmlUtils.formatMap( map ) );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    public void testPackageHeader() {
        ModulesXmlFormat modulesXmlFormat = new ModulesXmlFormat();
        InputSource is = new InputSource( new StringReader( packageHeaderModulesXml ) );
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
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    public static void main( String[] args ) {
        XmlTester xt = new XmlTester();
        xt.testModules();
        xt.testCategories();
        xt.testMap();
        xt.testPackageHeader();
    }
}
