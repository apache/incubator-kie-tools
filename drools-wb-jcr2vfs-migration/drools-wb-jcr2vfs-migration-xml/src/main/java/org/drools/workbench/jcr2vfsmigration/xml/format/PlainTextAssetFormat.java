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
import org.drools.workbench.jcr2vfsmigration.xml.model.asset.PlainTextAsset;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import static org.drools.workbench.jcr2vfsmigration.xml.format.XmlAssetFormat.*;

// todo extend from base class to avoid code repeat in format and parse methods
// todo evaluate if having different asset types is really necessary, instead of having 1 generic XmlAsset (which would get
// rid of some casting probably (see XmlAssetFormat)
public class PlainTextAssetFormat implements XmlFormat<PlainTextAsset> {

    private static final String TEXT_CONTENT = "textContent";

    @Override
    public void format( StringBuilder sb, PlainTextAsset plainTextAsset ) {
        if ( sb == null || plainTextAsset == null ) throw new IllegalArgumentException( "No output or plain text asset specified" );

        sb.append( LT ).append( ASSET )
                .append( " " ).append( ASSET_NAME ).append( "=\"" ).append( plainTextAsset.getName() ).append( "\"" )
                .append( " " ).append( ASSET_TYPE ).append( "=\"" ).append( plainTextAsset.getAssetType().toString() ).append( "\"" )
                .append( GT );

        sb.append( LT ).append( TEXT_CONTENT ).append( GT ).append( ExportXmlUtils.formatCdataSection( plainTextAsset.getContent() ) )
                .append( LT_SLASH ).append( TEXT_CONTENT ).append( GT );

        sb.append( LT_SLASH ).append( ASSET ).append( GT );

    }

    @Override
    public PlainTextAsset parse( Node assetNode ) {
        // NUll-ness already checked before
        NamedNodeMap assetAttribs = assetNode.getAttributes();
        String name = assetAttribs.getNamedItem( ASSET_NAME ).getNodeValue();
        String assetType = assetAttribs.getNamedItem( ASSET_TYPE ).getNodeValue();

        String textContent = null;
        NodeList assetNodeList = assetNode.getChildNodes();
        for ( int i = 0; i < assetNodeList.getLength(); i++ ) {
            Node node = assetNodeList.item( i );
            String nodeContent = node.getTextContent();
            if ( TEXT_CONTENT.equalsIgnoreCase( node.getNodeName() ) ) textContent = nodeContent;
        }
        return new PlainTextAsset( name, assetType, textContent );
    }
}
