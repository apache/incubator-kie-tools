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
import org.drools.workbench.jcr2vfsmigration.xml.model.asset.AssetType;
import org.drools.workbench.jcr2vfsmigration.xml.model.asset.XmlAsset;
import org.drools.workbench.jcr2vfsmigration.xml.model.asset.XmlAssets;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import static org.drools.workbench.jcr2vfsmigration.xml.ExportXmlUtils.*;

public abstract class AbstractXmlAssetFormat implements XmlFormat<XmlAsset> {

    protected static final String ASSET = "asset";
    protected static final String ASSET_NAME = "name";
    protected static final String ASSET_TYPE = "type";
    protected static final String ASSET_LAST_CONTRIBUTOR = "lastContrib";
    protected static final String ASSET_LAST_MODIFIED = "lastModif";
    protected static final String ASSET_CHECKIN_COMMENT = "comment";
    protected static final String ASSET_HISTORY = "history";

    private XmlAssetsFormat xmlAssetsFormat;

    @Override
    public void format( StringBuilder sb, XmlAsset xmlAsset ) {
        if ( sb == null || xmlAsset == null ) throw new IllegalArgumentException( "No output or asset specified" );

        if ( AssetType.IGNORED.equals( xmlAsset.getAssetType() ) ) return;

        initialize();

        formatAssetStart( sb, xmlAsset );
        sb.append( formatAssetAsString( xmlAsset ) );
        formatAssetEnd( sb, xmlAsset );
    }

    @Override
    public XmlAsset parse( Node assetNode ) {
        if ( assetNode == null || !ASSET.equals( assetNode.getNodeName() ) ) throw new IllegalArgumentException( "No input asset node specified for parsing" );

        initialize();

        XmlGenericAttributes genericAttributes = parseGenericNodeContent( assetNode );
        XmlAsset xmlAsset = parseStringToXmlAsset( genericAttributes.getAssetName(),
                                                   genericAttributes.getAssetFormat(),
                                                   genericAttributes.getAssetLastContributor(),
                                                   genericAttributes.getAssetCheckinComment(),
                                                   genericAttributes.getAssetLastModified(),
                                                   assetNode );

        xmlAsset.setAssetHistory( parseAssetHistory( assetNode ) );

        return xmlAsset;
    }

    protected abstract String formatAssetAsString( XmlAsset xmlAsset );

    protected abstract XmlAsset parseStringToXmlAsset( String name,
                                                       String format,
                                                       String lastContributor,
                                                       String checkinComment,
                                                       Date lastModified,
                                                       Node assetNode );

    protected XmlAssets parseAssetHistory( Node assetNode ) {
        NodeList assetNodeChildren = assetNode.getChildNodes();
        for ( int i = 0; i < assetNodeChildren.getLength(); i++ ) {
            Node node = assetNodeChildren.item( i );
            if ( ASSET_HISTORY.equals( node.getNodeName() ) ) return xmlAssetsFormat.parse( node.getFirstChild() );
        }
        return new XmlAssets();
    }

    private void formatAssetStart( StringBuilder sb, XmlAsset xmlAsset) {
        String lastContributor = StringUtils.isNotBlank( xmlAsset.getLastContributor() ) ? xmlAsset.getLastContributor() : "--";
        Date lastModified = xmlAsset.getLastModified() != null ? xmlAsset.getLastModified() : new Date();
        sb.append( LT ).append( ASSET )
                .append( " " ).append( ASSET_NAME ).append( "=\"" ).append( escapeXml( xmlAsset.getName() ) ).append( "\"" )
                .append( " " ).append( ASSET_TYPE ).append( "=\"" ).append( xmlAsset.getAssetType().toString() ).append( "\"" )
                .append( " " ).append( ASSET_LAST_CONTRIBUTOR ).append( "=\"" ).append( lastContributor ).append( "\"" )
                .append( " " ).append( ASSET_LAST_MODIFIED ).append( "=\"" ).append( lastModified.getTime() ).append( "\"" )
                .append( GT );

        // format comment as a CData section, in case it contains any funny characters
        sb.append( LT ).append( ASSET_CHECKIN_COMMENT ).append( GT )
                .append( formatCdataSection( xmlAsset.getCheckinComment() ) )
                .append( LT_SLASH ).append( ASSET_CHECKIN_COMMENT ).append( GT );
    }

    private void formatAssetEnd( StringBuilder sb, XmlAsset xmlAsset) {
        // Format asset history, (it won't if we're in the process of formatting a history asset )
        if ( xmlAsset.getAssetHistory() != null ) {
            sb.append( LT ).append( ASSET_HISTORY ).append( GT );
            xmlAssetsFormat.format( sb, xmlAsset.getAssetHistory() );
            sb.append( LT_SLASH ).append( ASSET_HISTORY ).append( GT );
        }

        sb.append( LT_SLASH ).append( ASSET ).append( GT );
    }

    private XmlGenericAttributes parseGenericNodeContent( Node assetNode ) {
        // Null-ness already checked before
        NamedNodeMap assetAttribs = assetNode.getAttributes();

        Node commentNode = assetNode.getFirstChild();
        String checkinComment = parseCdataSection( commentNode ); // Need the CData parent-node

        return new XmlGenericAttributes( unEscapeXml( assetAttribs.getNamedItem( ASSET_NAME ).getNodeValue() ),
                                         assetAttribs.getNamedItem( ASSET_TYPE ).getNodeValue(),
                                         assetAttribs.getNamedItem( ASSET_LAST_CONTRIBUTOR ).getNodeValue(),
                                         checkinComment,
                                         new Date( Long.parseLong( assetAttribs.getNamedItem( ASSET_LAST_MODIFIED ).getNodeValue(), 10 ) )
        );
    }

    private void initialize() {
        if ( xmlAssetsFormat == null ) xmlAssetsFormat = new XmlAssetsFormat();
    }

    private class XmlGenericAttributes {
        private String assetName;
        private String assetFormat;
        private String assetLastContributor;
        private String assetCheckinComment;
        private Date assetLastModified;

        private XmlGenericAttributes( String assetName, String assetFormat, String assetLastContributor, String assetCheckinComment, Date assetLastModified ) {
            this.assetName = assetName;
            this.assetFormat = assetFormat;
            this.assetLastContributor = assetLastContributor;
            this.assetCheckinComment = assetCheckinComment;
            this.assetLastModified = assetLastModified;
        }

        private String getAssetName() {
            return assetName;
        }

        private String getAssetFormat() {
            return assetFormat;
        }

        private String getAssetLastContributor() {
            return assetLastContributor;
        }

        private String getAssetCheckinComment() {
            return assetCheckinComment;
        }

        private Date getAssetLastModified() {
            return assetLastModified;
        }
    }
}
