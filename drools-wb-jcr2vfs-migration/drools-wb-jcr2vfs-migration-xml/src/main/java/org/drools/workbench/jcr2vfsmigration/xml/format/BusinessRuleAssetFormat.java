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

import org.drools.workbench.jcr2vfsmigration.xml.ExportXmlUtils;
import org.drools.workbench.jcr2vfsmigration.xml.model.asset.BusinessRuleAsset;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class BusinessRuleAssetFormat extends XmlAssetFormat {

    private static final String CONTENT = "content";
    private static final String HAS_DRL = "hasDrl";

    protected String doFormat( BusinessRuleAsset businessRuleAsset ) {
        StringBuilder sb = new StringBuilder()
                .append( LT ).append( CONTENT ).append( GT ).append( ExportXmlUtils.formatCdataSection( businessRuleAsset.getContent() ) )
                .append( LT_SLASH ).append( CONTENT ).append( GT );

        sb.append( LT ).append( HAS_DRL ).append( GT ).append( businessRuleAsset.hasDSLSentences() ? "true" : "false" )
                .append( LT_SLASH ).append( HAS_DRL ).append( GT );

        return sb.toString();
    }

    protected BusinessRuleAsset doParse( String name,
                                         String format,
                                         String lastContributor,
                                         String checkinComment,
                                         Date lastModified,
                                         Node assetNode ) {

        Boolean hasDSL = false;
        String content = null;
        NodeList assetNodeList = assetNode.getChildNodes();
        for ( int i = 0; i < assetNodeList.getLength(); i++ ) {
            Node node = assetNodeList.item( i );
            String nodeContent = node.getTextContent();
            if ( CONTENT.equalsIgnoreCase( node.getNodeName() ) ) {
                content = nodeContent;
            } else if ( HAS_DRL.equalsIgnoreCase( node.getNodeName() ) ) {
                hasDSL = Boolean.valueOf( nodeContent );
            }
        }
        return new BusinessRuleAsset( name, format, lastContributor, checkinComment, lastModified, content, hasDSL );
    }
}
