/*
 * Copyright 2014 JBoss Inc
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import static org.drools.workbench.jcr2vfsmigration.xml.format.XmlFormat.*;

public class ExportXmlUtils {

    private static final String CDATA_SECTION = "#cdata-section";

    public static String formatCdataSection( String content ) {
        StringBuilder sb = new StringBuilder( CDATA_OPEN );
        sb.append( escapeContent( content ) ).append( CDATA_CLOSE );
        return sb.toString();
    }

    public static String formatMap( Map<String, String> map ) {
        StringBuilder sb = new StringBuilder("<map>");
        if ( map != null && map.size() > 0 ) {
            for ( Iterator<Map.Entry<String, String>> mapEntryIt = map.entrySet().iterator(); mapEntryIt.hasNext(); ) {
                sb.append( "<entry>" );
                Map.Entry<String, String> entry = mapEntryIt.next();
                sb.append( "<key>" ).append( entry.getKey() ).append( "</key>" );
                sb.append( "<value>" ).append( entry.getValue() ).append( "</value>" );
                sb.append( "</entry>" );
            }
        }
        sb.append( "</map>" );
        return sb.toString();
    }

    // A CData parent node can contain several CData sections, if the content contained any nested CData section(s).
    public static String parseCdataSection( Node cdataParentNode ) {
        if ( cdataParentNode == null ) return "";
        StringBuilder sb = new StringBuilder();
        NodeList cdataParentNodeChildren = cdataParentNode.getChildNodes();
        if ( cdataParentNodeChildren != null && cdataParentNodeChildren.getLength() > 0 ) {
            for ( int i = 0; i < cdataParentNodeChildren.getLength(); i++ ) {
                Node cdataNode = cdataParentNodeChildren.item( i );
                if ( CDATA_SECTION.equalsIgnoreCase( cdataNode.getNodeName() ) ) sb.append( cdataNode.getTextContent() );
                else System.out.println( "WARNING: only expected CData sections, ignoring: " + cdataNode.getNodeName() );
            }
        }
        return sb.toString();
    }

    public static Map<String, String> parseMap( Node mapNode ) {
        Map<String, String> map = new HashMap<String, String>();
        if ( mapNode != null && "map".equals( mapNode.getNodeName() ) ) {
            NodeList entriesNodes = mapNode.getChildNodes();
            for ( int i = 0; i < entriesNodes.getLength(); i++ ) {
                Node entriesNode = entriesNodes.item( i );
                NodeList entry = entriesNode.getChildNodes();
                String key = null;
                String value = null;
                for ( int j = 0; j < entry.getLength(); j++ ) {
                    Node keyValueNode = entry.item( j );
                    if ( "key".equals( keyValueNode.getNodeName() ) ) {
                        key = keyValueNode.getTextContent();
                    } else if ( "value".equals( keyValueNode.getNodeName() ) ) {
                        value = keyValueNode.getTextContent();
                    }
                }
                map.put( key, value );
            }
        }
        return map;
    }

    // Transforms possible nested CData sections in successive ones, by replacing the "]]>" end sequence with "]]]]><![CDATA[>",
    // which effectively stops and restarts the internal cdata sections.
    private static String escapeContent( String s ) {
        if ( StringUtils.isBlank( s ) ) return "";
        return s.replaceAll( "]]>", "]]]]><![CDATA[>" );
    }
}
