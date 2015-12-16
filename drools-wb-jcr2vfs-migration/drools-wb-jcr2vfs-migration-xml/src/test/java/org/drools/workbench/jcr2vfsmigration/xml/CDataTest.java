/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.jcr2vfsmigration.xml;

import java.io.StringReader;
import java.text.MessageFormat;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import static org.junit.Assert.*;

public class CDataTest {

    private String simpleText = "This is a simple text.";
    private String oneNestedText = "This is a simple text with one nested <![CDATA[ cdata-section 1 ]]>, but that's all.";
    private String multiNestedText = "This is a simple text with a first nested <![CDATA[ cdata-section 1 ]]>, and also " +
            "a second nested <![CDATA[ cdata-section 2 ]]>, in successive order.";
    private String levelNestedText = "This is a simple text with nested <![CDATA[ cdata-section 1, and also a second level " +
            "nested <![CDATA[ cdata-section 2 ]]> section ]]>, multilevel.";
    private String multiLevelCombinedNestedText = "This is a simple text with a first nested <![CDATA[ cdata-section 1, " +
            "and also a second level nested <![CDATA[ cdata-section 2 ]]> section ]]>, and also a third nested <![CDATA[ " +
            "cdata-section 3 ]]>, in successive order.";

    private String simpleTextExpected = "<![CDATA[This is a simple text.]]>";
    private String oneNestedTextExpected = "<![CDATA[This is a simple text with one nested <![CDATA[ cdata-section 1 ]]]]>" +
            "<![CDATA[>, but that's all.]]>";
    private String multiNestedTextExpected = "<![CDATA[This is a simple text with a first nested <![CDATA[ cdata-section 1" +
            " ]]]]><![CDATA[>, and also a second nested <![CDATA[ cdata-section 2 ]]]]><![CDATA[>, in successive order.]]>";
    private String levelNestedTextExpected = "<![CDATA[This is a simple text with nested <![CDATA[ cdata-section 1, and " +
            "also a second level nested <![CDATA[ cdata-section 2 ]]]]><![CDATA[> section ]]]]><![CDATA[>, multilevel.]]>";
    private String multiLevelCombinedNestedTextExpected = "<![CDATA[This is a simple text with a first nested <![CDATA[ " +
            "cdata-section 1, and also a second level nested <![CDATA[ cdata-section 2 ]]]]><![CDATA[> section ]]]]><![CDATA[>, " +
            "and also a third nested <![CDATA[ cdata-section 3 ]]]]><![CDATA[>, in successive order.]]>";

    private String templateXml = "<cdataParent>{0}</cdataParent>";

    @Test
    public void testFormatCdataSection() {
        assertEquals( simpleTextExpected, ExportXmlUtils.formatCdataSection( simpleText ) );
        assertEquals( oneNestedTextExpected, ExportXmlUtils.formatCdataSection( oneNestedText ) );
        assertEquals( multiNestedTextExpected, ExportXmlUtils.formatCdataSection( multiNestedText ) );
        assertEquals( levelNestedTextExpected, ExportXmlUtils.formatCdataSection( levelNestedText ) );
        assertEquals( multiLevelCombinedNestedTextExpected, ExportXmlUtils.formatCdataSection( multiLevelCombinedNestedText ) );
    }

    @Test
    public void testParseCdataSections() {
        testXmlString( getXml( templateXml, simpleTextExpected ), simpleText );
        testXmlString( getXml( templateXml, oneNestedTextExpected ), oneNestedText );
        testXmlString( getXml( templateXml, multiNestedTextExpected ), multiNestedText );
        testXmlString( getXml( templateXml, levelNestedTextExpected ), levelNestedText );
        testXmlString( getXml( templateXml, multiLevelCombinedNestedTextExpected ), multiLevelCombinedNestedText );
    }

    private void testXmlString( String strXml, String expectedResult ) {
        InputSource is = new InputSource( new StringReader( strXml ) );
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document xml = db.parse( is );
            Node parentNode = xml.getFirstChild();
            if ( parentNode != null && "cdataParent".equals( parentNode.getNodeName())) {
                assertEquals( expectedResult, ExportXmlUtils.parseCdataSection( parentNode ) );
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    private String getXml( String pattern, String escapedContent ) {
        return MessageFormat.format( pattern, escapedContent );
    }
}
