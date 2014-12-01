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

import org.drools.workbench.jcr2vfsmigration.xml.model.asset.AttachmentAsset;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import static org.drools.workbench.jcr2vfsmigration.xml.format.XmlAssetFormat.*;

public class AttachmentAssetFormat implements XmlFormat<AttachmentAsset> {

    private static final String ATTACHMENT_FILENAME = "attachmentFileName";

    @Override
    public void format( StringBuilder sb, AttachmentAsset attachmentAsset ) {
        if ( sb == null || attachmentAsset == null ) throw new IllegalArgumentException( "No output or attachment asset specified" );

        sb.append( LT ).append( ASSET )
                .append( " " ).append( ASSET_NAME ).append( "=\"" ).append( attachmentAsset.getName() ).append( "\"" )
                .append( " " ).append( ASSET_TYPE ).append( "=\"" ).append( attachmentAsset.getAssetType().toString() ).append( "\"" )
                .append( GT );

        sb.append( LT ).append( ATTACHMENT_FILENAME ).append( GT ).append( attachmentAsset.getAttachmentFileName() )
                .append( LT_SLASH ).append( ATTACHMENT_FILENAME ).append( GT );

        sb.append( LT_SLASH ).append( ASSET ).append( GT );
    }

    @Override
    public AttachmentAsset parse( Node assetNode ) {
        // Null-ness already checked before
        NamedNodeMap assetAttribs = assetNode.getAttributes();
        String name = assetAttribs.getNamedItem( ASSET_NAME ).getNodeValue();
        String assetType = assetAttribs.getNamedItem( ASSET_TYPE ).getNodeValue();

        String attachmentFile = null;
        NodeList assetNodeList = assetNode.getChildNodes();
        for ( int i = 0; i < assetNodeList.getLength(); i++ ) {
            Node node = assetNodeList.item( i );
            String nodeContent = node.getTextContent();
            if ( ATTACHMENT_FILENAME.equalsIgnoreCase( node.getNodeName() ) ) attachmentFile = nodeContent;
        }
        return new AttachmentAsset( name, assetType, attachmentFile );
    }
}
