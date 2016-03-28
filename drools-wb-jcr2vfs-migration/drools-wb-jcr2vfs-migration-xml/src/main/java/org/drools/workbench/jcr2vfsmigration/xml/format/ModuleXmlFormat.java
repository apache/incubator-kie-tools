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
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.drools.workbench.jcr2vfsmigration.xml.model.Module;
import org.drools.workbench.jcr2vfsmigration.xml.model.ModuleType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import static org.drools.workbench.jcr2vfsmigration.xml.ExportXmlUtils.*;

public class ModuleXmlFormat implements XmlFormat<Module> {

    private static final Logger logger = LoggerFactory.getLogger(ModuleXmlFormat.class);

    protected static final String MODULE = "module";
    protected static final String MODULE_UUID = "uuid";
    protected static final String MODULE_TYPE = "type";
    protected static final String MODULE_NAME = "name";
    protected static final String MODULE_LAST_CONTRIBUTOR = "lastContrib";
    protected static final String MODULE_CHECKIN_COMMENT = "comment";
    protected static final String MODULE_LAST_MODIFIED = "lastModif";
    protected static final String MODULE_NORM_PACKAGENAME = "normalizedPackageName";
    protected static final String MODULE_PACKAGEHEADER = "packageHeaderInfo";
    protected static final String MODULE_GLOBALS = "globals";
    protected static final String MODULE_CATRULES = "catRules";
    protected static final String MODULE_ASSET_FILE = "assetExportFileName";

    @Override
    public void format( StringBuilder sb, Module module ) {
        if ( sb == null || module == null ) throw new IllegalArgumentException( "No output or Module specified" );

        sb.append( LT ).append( MODULE ).append( GT );

        sb.append( LT ).append( MODULE_UUID ).append( GT ).append( module.getUuid() ).append( LT_SLASH )
                .append( MODULE_UUID ).append( GT );
        sb.append( LT ).append( MODULE_TYPE ).append( GT ).append( module.getType() ).append( LT_SLASH )
                .append( MODULE_TYPE ).append( GT );
        sb.append( LT ).append( MODULE_NAME ).append( GT ).append( escapeXml( module.getName() ) ).append( LT_SLASH )
                .append( MODULE_NAME ).append( GT );

        String lastContributor = StringUtils.isNotBlank( module.getLastContributor() ) ? module.getLastContributor() : "--";
        sb.append( LT ).append( MODULE_LAST_CONTRIBUTOR ).append( GT ).append( lastContributor ).append( LT_SLASH )
                .append( MODULE_LAST_CONTRIBUTOR ).append( GT );

        // Check-in comment as CData section
        sb.append( formatCheckinComment( module.getCheckinComment() ) );

        Date lastModified = module.getLastModified() != null ? module.getLastModified() : new Date();
        sb.append( LT ).append( MODULE_LAST_MODIFIED ).append( GT ).append( lastModified.getTime() ).append( LT_SLASH )
                .append( MODULE_LAST_MODIFIED ).append( GT );

        sb.append( LT ).append( MODULE_NORM_PACKAGENAME ).append( GT ).append( module.getNormalizedPackageName() )
                .append( LT_SLASH ).append( MODULE_NORM_PACKAGENAME ).append( GT );
        sb.append( LT ).append( MODULE_ASSET_FILE ).append( GT ).append( module.getAssetExportFileName() )
                .append( LT_SLASH ).append( MODULE_ASSET_FILE ).append( GT );

        // Package header info
        sb.append( formatPackageHeaderInfo( module.getPackageHeaderInfo() ) );

        // Globals String
        sb.append( formatGlobals( module.getGlobalsString() ) );

        // Category rules
        sb.append( formatCatRules( module ) );

        sb.append( LT_SLASH ).append( MODULE ).append( GT );
//        logger.info( "    Module [{}] exported.", module.getName() );
    }

    @Override
    public Module parse( Node moduleNode ) {
        if ( moduleNode == null || !MODULE.equals( moduleNode.getNodeName() ) ) throw new IllegalArgumentException( "No input module node specified for parsing" );

        String name = null;
        String lastContributor = null;
        String checkinComment = null;
        Date lastModified = null;
        String normalizedPackageName = null;
        String uuid = null;
        String packageHeaderInfo = null;
        String globals = null;
        ModuleType type = null;
        Map<String, String> catRules = null;
        String assetExportFileName = null;

        NodeList moduleProps = moduleNode.getChildNodes();
        for ( int i = 0; i < moduleProps.getLength(); i++ ) {
            Node propertyNode = moduleProps.item( i );
            String nodeContent = propertyNode.getTextContent();
            if ( MODULE_NAME.equalsIgnoreCase( propertyNode.getNodeName() ) ) {
                name = unEscapeXml( nodeContent );
            } else if ( MODULE_LAST_CONTRIBUTOR.equalsIgnoreCase( propertyNode.getNodeName() ) ) {
                 lastContributor = nodeContent;
            } else if ( MODULE_CHECKIN_COMMENT.equalsIgnoreCase( propertyNode.getNodeName() ) ) {
                checkinComment = parseCheckinComment( propertyNode );
            } else if ( MODULE_LAST_MODIFIED.equalsIgnoreCase( propertyNode.getNodeName() ) ) {
                lastModified = new Date( Long.parseLong( nodeContent, 10 ) );
            } else if ( MODULE_NORM_PACKAGENAME.equalsIgnoreCase( propertyNode.getNodeName() ) ) {
                normalizedPackageName = nodeContent;
            } else if ( MODULE_UUID.equalsIgnoreCase( propertyNode.getNodeName() ) ) {
                uuid = nodeContent;
            } else if ( MODULE_TYPE.equalsIgnoreCase( propertyNode.getNodeName() ) ) {
                type = ModuleType.getByName( nodeContent );
            } else if ( MODULE_PACKAGEHEADER.equalsIgnoreCase( propertyNode.getNodeName() ) ) {
                packageHeaderInfo = parsePackageHeaderInfo( propertyNode );
            } else if ( MODULE_GLOBALS.equalsIgnoreCase( propertyNode.getNodeName() ) ) {
                globals = parseGlobals( propertyNode );
            } else if ( MODULE_CATRULES.equalsIgnoreCase( propertyNode.getNodeName() ) ) {
                catRules = parseCatRules( propertyNode );
            } else if ( MODULE_ASSET_FILE.equalsIgnoreCase( propertyNode.getNodeName() ) ) {
                assetExportFileName = nodeContent;
            }
        }
        return new Module( type,
                           uuid,
                           name,
                           lastContributor,
                           checkinComment,
                           lastModified,
                           normalizedPackageName,
                           packageHeaderInfo,
                           globals,
                           catRules,
                           assetExportFileName );
    }

    private String formatCheckinComment( String checkinComment ) {
        StringBuilder sbCheckinComment = new StringBuilder( LT );
        sbCheckinComment.append( MODULE_CHECKIN_COMMENT ).append( GT );
        if ( StringUtils.isNotBlank( checkinComment ) ) {
            sbCheckinComment.append( formatCdataSection( checkinComment ) );
        }
        sbCheckinComment.append( LT_SLASH ).append( MODULE_CHECKIN_COMMENT ).append( GT );
        return sbCheckinComment.toString();
    }

    private String parseCheckinComment( Node checkinCommentNode ) {
        if ( !MODULE_CHECKIN_COMMENT.equalsIgnoreCase( checkinCommentNode.getNodeName() ) )
            throw new IllegalArgumentException( "Wrong xml format: " + MODULE_CHECKIN_COMMENT );
        return parseCdataSection( checkinCommentNode ); // Need the CData parent-node
    }

    private String formatCatRules( Module module ) {
        StringBuilder sbCatRules = new StringBuilder( LT ).append( MODULE_CATRULES ).append( GT );
        Map<String, String> mapCatRules = module.getCatRules();
        if ( mapCatRules.size() > 0 ) {
            sbCatRules.append( formatMap( mapCatRules ) );
        }
        sbCatRules.append( LT_SLASH ).append( MODULE_CATRULES ).append( GT );
        return sbCatRules.toString();
    }

    private Map<String, String> parseCatRules( Node catRulesNode ) {
        Map<String, String> catRules;
        NodeList catRulesNodeChildren = catRulesNode.getChildNodes();
        if ( catRulesNodeChildren.getLength() > 1 ) throw new IllegalArgumentException( "Wrong xml format: " + MODULE_CATRULES );
        catRules = parseMap( catRulesNodeChildren.item( 0 ) );
        return catRules;
    }

    private String formatPackageHeaderInfo( String packageHeaderInfo ) {
        StringBuilder sbPackageHeader = new StringBuilder( LT );
        sbPackageHeader.append( MODULE_PACKAGEHEADER ).append( GT );
        if ( StringUtils.isNotBlank( packageHeaderInfo ) ) {
            sbPackageHeader.append( formatCdataSection( packageHeaderInfo ) );
        }
        sbPackageHeader.append( LT_SLASH ).append( MODULE_PACKAGEHEADER ).append( GT );
        return sbPackageHeader.toString();
    }

    private String parsePackageHeaderInfo( Node headerInfoNode ) {
        if ( !MODULE_PACKAGEHEADER.equalsIgnoreCase( headerInfoNode.getNodeName() ) )
            throw new IllegalArgumentException( "Wrong xml format: " + MODULE_PACKAGEHEADER );
        return parseCdataSection( headerInfoNode ); // Need the CData parent-node
    }

    private String formatGlobals( String globals ) {
        StringBuilder sbGlobals = new StringBuilder( LT );
        sbGlobals.append( MODULE_GLOBALS ).append( GT );
        if ( StringUtils.isNotBlank( globals ) ) {
            sbGlobals.append( formatCdataSection( globals ) );
        }
        sbGlobals.append( LT_SLASH ).append( MODULE_GLOBALS ).append( GT );
        return sbGlobals.toString();
    }

    private String parseGlobals( Node globalsNode ) {
        if ( !MODULE_GLOBALS.equalsIgnoreCase( globalsNode.getNodeName() ) )
            throw new IllegalArgumentException( "Wrong xml format: " + MODULE_GLOBALS );
        return parseCdataSection( globalsNode ); // Need the CData parent-node
    }
}