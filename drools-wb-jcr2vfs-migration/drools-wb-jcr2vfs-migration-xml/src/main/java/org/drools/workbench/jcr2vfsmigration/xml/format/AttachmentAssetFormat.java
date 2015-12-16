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
package org.drools.workbench.jcr2vfsmigration.xml.format;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.drools.workbench.jcr2vfsmigration.xml.model.asset.AttachmentAsset;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class AttachmentAssetFormat extends XmlAssetFormat {

    private static final String ORIGINAL_FORMAT = "originalFormat";
    private static final String ATTACHMENT_FILENAME = "attachmentFileName";

    protected String doFormat( AttachmentAsset attachmentAsset ) {
        StringBuilder sb = new StringBuilder();
        if ( StringUtils.isNotBlank( attachmentAsset.getOriginalFormat() ) ) {
            sb.append( LT ).append( ORIGINAL_FORMAT ).append( GT )
              .append( attachmentAsset.getOriginalFormat() )
              .append( LT_SLASH ).append( ORIGINAL_FORMAT ).append( GT );
        }
        sb.append( LT ).append( ATTACHMENT_FILENAME ).append( GT )
          .append( attachmentAsset.getAttachmentFileName() )
          .append( LT_SLASH ).append( ATTACHMENT_FILENAME ).append( GT );

        return sb.toString();
    }

    protected AttachmentAsset doParse( String name,
                                       String format,
                                       String lastContributor,
                                       String checkinComment,
                                       Date lastModified,
                                       Node assetNode ) {

        String originalFormat = null;
        String attachmentFile = null;
        NodeList assetNodeList = assetNode.getChildNodes();
        for ( int i = 0; i < assetNodeList.getLength(); i++ ) {
            Node node = assetNodeList.item( i );
            String nodeContent = node.getTextContent();
            if ( ORIGINAL_FORMAT.equalsIgnoreCase( node.getNodeName() ) ) {
                originalFormat = nodeContent;
            } else if ( ATTACHMENT_FILENAME.equalsIgnoreCase( node.getNodeName() ) ) {
                attachmentFile = nodeContent;
            }
        }
        return new AttachmentAsset( name, format, lastContributor, checkinComment, lastModified, originalFormat, attachmentFile );
    }
}
