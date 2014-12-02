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
package org.drools.workbench.jcr2vfsmigration.xml.format;

import org.drools.workbench.jcr2vfsmigration.xml.ExportXmlUtils;
import org.drools.workbench.jcr2vfsmigration.xml.model.asset.GuidedDecisionTableAsset;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import static org.drools.workbench.jcr2vfsmigration.xml.format.XmlAssetFormat.*;

public class GuidedDecisionTableAssetFormat implements XmlFormat<GuidedDecisionTableAsset> {

    private static final String CONTENT = "content";
    private static final String EXTENDED_RULE = "extendedRule";

    @Override
    public void format( StringBuilder sb, GuidedDecisionTableAsset guidedDecisionTableAsset ) {
        if ( sb == null || guidedDecisionTableAsset == null ) throw new IllegalArgumentException( "No output or guided decision table asset asset specified" );

        sb.append( LT ).append( ASSET )
                .append( " " ).append( ASSET_NAME ).append( "=\"" ).append( guidedDecisionTableAsset.getName() ).append( "\"" )
                .append( " " ).append( ASSET_TYPE ).append( "=\"" ).append( guidedDecisionTableAsset.getAssetType().toString() ).append( "\"" )
                .append( GT );

        sb.append( LT ).append( CONTENT ).append( GT ).append( ExportXmlUtils.formatCdataSection( guidedDecisionTableAsset.getContent() ) )
                .append( LT_SLASH ).append( CONTENT ).append( GT );

        sb.append( LT ).append( EXTENDED_RULE ).append( GT ).append( ExportXmlUtils.formatCdataSection( guidedDecisionTableAsset.getExtendedRule() ) )
                .append( LT_SLASH ).append( EXTENDED_RULE ).append( GT );

        sb.append( LT_SLASH ).append( ASSET ).append( GT );
    }

    @Override
    public GuidedDecisionTableAsset parse( Node assetNode ) {
        // Null-ness already checked before
        NamedNodeMap assetAttribs = assetNode.getAttributes();
        String name = assetAttribs.getNamedItem( ASSET_NAME ).getNodeValue();
        String assetType = assetAttribs.getNamedItem( ASSET_TYPE ).getNodeValue();

        String content = null;
        String extendedRule = null;
        NodeList assetNodeList = assetNode.getChildNodes();
        for ( int i = 0; i < assetNodeList.getLength(); i++ ) {
            Node node = assetNodeList.item( i );
            String nodeContent = node.getTextContent();
            if ( CONTENT.equalsIgnoreCase( node.getNodeName() ) ) {
                content = nodeContent;
            } else if ( EXTENDED_RULE.equalsIgnoreCase( node.getNodeName() ) ) {
                extendedRule = nodeContent;
            }
        }
        return new GuidedDecisionTableAsset( name, assetType, content, extendedRule );
    }
}
