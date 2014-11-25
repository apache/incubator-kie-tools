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

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import static org.drools.workbench.jcr2vfsmigration.xml.format.XmlFormat.*;

public class ExportXmlUtils {

    private static final String CDATA_SECTION = "#cdata-section";

    public static String formatCdataSection( String content ) {
        if ( content.contains( CDATA_CLOSE ) ) throw new RuntimeException( "Illegal close of CDATA section inside " + content );
        StringBuilder sb = new StringBuilder( CDATA_OPEN );
        sb.append( content ).append( CDATA_CLOSE );
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

    public static String parseCdataSection( Node cdataNode ) {
        if ( cdataNode != null && CDATA_SECTION.equalsIgnoreCase( cdataNode.getNodeName() ) ) {
            return cdataNode.getTextContent();
        }
        return "";
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
}
