package org.drools.workbench.jcr2vfsmigration.xml;

public class XMLStringBuilderTest {

    public void testXmlStringBuilder() {
        String xml =
            new XMLStringBuilder()
                .startTag( "modules" ).newLine().indent()
                .startTag( "module").newLine().indent()
                .startTag( "name").printTagContent( "module1" ).endTag( "name" ).newLine()
                .startTag( "uuid").printTagContent( "uuid1" ).endTag( "uuid" ).newLine().unIndent()
                .endTag( "module" ).newLine()
                .startTag( "module" ).newLine().indent()
                .startTag( "name").printTagContent( "module1" ).endTag( "name" ).newLine()
                .startTag( "uuid" ).printTagContent( "uuid1" ).endTag( "uuid" ).newLine()
                .endTag( "module" ).newLine().unIndent()
                .endTag( "modules" )
                .toString();
        System.out.println( "XML: \n" + xml );
    }

    public static void main( String[] args ) {
        XMLStringBuilderTest test = new XMLStringBuilderTest();
        test.testXmlStringBuilder();
    }
}
