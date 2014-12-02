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
import org.drools.workbench.jcr2vfsmigration.xml.model.asset.BusinessRuleAsset;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import static org.drools.workbench.jcr2vfsmigration.xml.format.XmlAssetFormat.*;

public class BusinessRuleAssetFormat implements XmlFormat<BusinessRuleAsset> {

    private static final String TEXT_CONTENT = "textContent";
    private static final String HAS_DRL = "hasDrl";

    @Override
    public void format( StringBuilder sb, BusinessRuleAsset businessRuleAsset ) {
        if ( sb == null || businessRuleAsset == null ) throw new IllegalArgumentException( "No output or business rule asset specified" );

        sb.append( LT ).append( ASSET )
                .append( " " ).append( ASSET_NAME ).append( "=\"" ).append( businessRuleAsset.getName() ).append( "\"" )
                .append( " " ).append( ASSET_TYPE ).append( "=\"" ).append( businessRuleAsset.getAssetType().toString() ).append( "\"" )
                .append( GT );

        sb.append( LT ).append( TEXT_CONTENT ).append( GT ).append( ExportXmlUtils.formatCdataSection( businessRuleAsset.getContent() ) )
                .append( LT_SLASH ).append( TEXT_CONTENT ).append( GT );

        sb.append( LT ).append( HAS_DRL ).append( GT ).append( businessRuleAsset.hasDSLSentences() ? "true" : "false" )
                .append( LT_SLASH ).append( HAS_DRL ).append( GT );

        sb.append( LT_SLASH ).append( ASSET ).append( GT );
    }

    @Override
    public BusinessRuleAsset parse( Node assetNode ) {
        // Null-ness already checked before
        NamedNodeMap assetAttribs = assetNode.getAttributes();
        String name = assetAttribs.getNamedItem( ASSET_NAME ).getNodeValue();
        String assetType = assetAttribs.getNamedItem( ASSET_TYPE ).getNodeValue();

        Boolean hasDSL = false;
        String textContent = null;
        NodeList assetNodeList = assetNode.getChildNodes();
        for ( int i = 0; i < assetNodeList.getLength(); i++ ) {
            Node node = assetNodeList.item( i );
            String nodeContent = node.getTextContent();
            if ( TEXT_CONTENT.equalsIgnoreCase( node.getNodeName() ) ) {
                textContent = nodeContent;
            } else if ( HAS_DRL.equalsIgnoreCase( node.getNodeName() ) ) {
                hasDSL = Boolean.valueOf( nodeContent );
            }
        }
        return new BusinessRuleAsset( name, assetType, textContent, hasDSL );
    }
}
