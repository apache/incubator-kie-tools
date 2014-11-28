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

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.drools.workbench.jcr2vfsmigration.xml.ExportXmlUtils;
import org.drools.workbench.jcr2vfsmigration.xml.model.Module;
import org.drools.workbench.jcr2vfsmigration.xml.model.ModuleType;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ModuleXmlFormat implements XmlFormat<Module> {

    public static final String MODULE = "module";
    public static final String MODULE_UUID = "uuid";
    public static final String MODULE_TYPE = "type";
    public static final String MODULE_NAME = "name";
    public static final String MODULE_NORM_PACKAGENAME = "normalizedPackageName";
    public static final String MODULE_PACKAGEHEADER = "packageHeaderInfo";
    public static final String MODULE_CATRULES = "catRules";
    public static final String MODULE_ASSET_FILE = "assetExportFileName";

    @Override
    public void format( StringBuilder sb, Module module ) {
        if ( sb == null || module == null ) throw new IllegalArgumentException( "No output or Module specified" );

        sb.append( LT ).append( MODULE ).append( GT );

        sb.append( LT ).append( MODULE_UUID ).append( GT ).append( module.getUuid() ).append( LT_SLASH )
                .append( MODULE_UUID ).append( GT );
        sb.append( LT ).append( MODULE_TYPE ).append( GT ).append( module.getType() ).append( LT_SLASH )
                .append( MODULE_TYPE ).append( GT );
        sb.append( LT ).append( MODULE_NAME ).append( GT ).append( module.getName() ).append( LT_SLASH )
                .append( MODULE_NAME ).append( GT );
        sb.append( LT ).append( MODULE_NORM_PACKAGENAME ).append( GT ).append( module.getNormalizedPackageName() )
                .append( LT_SLASH ).append( MODULE_NORM_PACKAGENAME ).append( GT );
        sb.append( LT ).append( MODULE_ASSET_FILE ).append( GT ).append( module.getAssetExportFileName() )
                .append( LT_SLASH ).append( MODULE_ASSET_FILE ).append( GT );

        // Package header info
        sb.append( formatPackageHeaderInfo( module.getPackageHeaderInfo() ) );

        // Category rules
        sb.append( formatCatRules( module ) );

        sb.append( LT_SLASH ).append( MODULE ).append( GT );
        System.out.format( "Module [%s] exported. %n", module.getName() );
    }

    @Override
    public Module parse( Node moduleNode ) {
        if ( moduleNode == null || !MODULE.equals( moduleNode.getNodeName() ) ) throw new IllegalArgumentException( "No input module node specified for parsing" );

        String name = null;
        String normalizedPackageName = null;
        String uuid = null;
        String packageHeaderInfo = null;
        ModuleType type = null;
        Map<String, String> catRules = null;
        String assetExportFileName = null;

        NodeList moduleProps = moduleNode.getChildNodes();
        for ( int i = 0; i < moduleProps.getLength(); i++ ) {
            Node propertyNode = moduleProps.item( i );
            String nodeContent = propertyNode.getTextContent();
            if ( MODULE_NAME.equalsIgnoreCase( propertyNode.getNodeName() ) ) {
                name = nodeContent;
            } else if ( MODULE_NORM_PACKAGENAME.equalsIgnoreCase( propertyNode.getNodeName() ) ) {
                normalizedPackageName = nodeContent;
            } else if ( MODULE_UUID.equalsIgnoreCase( propertyNode.getNodeName() ) ) {
                uuid = nodeContent;
            } else if ( MODULE_TYPE.equalsIgnoreCase( propertyNode.getNodeName() ) ) {
                type = ModuleType.getByName( nodeContent );
            } else if ( MODULE_PACKAGEHEADER.equalsIgnoreCase( propertyNode.getNodeName() ) ) {
                packageHeaderInfo = parsePackageHeaderInfo( propertyNode );
            } else if ( MODULE_CATRULES.equalsIgnoreCase( propertyNode.getNodeName() ) ) {
                catRules = parseCatRules( propertyNode );
            } else if ( MODULE_ASSET_FILE.equalsIgnoreCase( propertyNode.getNodeName() ) ) {
                assetExportFileName = nodeContent;
            }
        }
        return new Module( type,
                           uuid,
                           name,
                           normalizedPackageName,
                           packageHeaderInfo,
                           catRules,
                           assetExportFileName );
    }

    private String formatCatRules( Module module ) {
        StringBuilder sbCatRules = new StringBuilder( LT ).append( MODULE_CATRULES ).append( GT );
        Map<String, String> mapCatRules = module.getCatRules();
        if ( mapCatRules.size() > 0 ) {
            sbCatRules.append( ExportXmlUtils.formatMap( mapCatRules ) );
        }
        sbCatRules.append( LT_SLASH ).append( MODULE_CATRULES ).append( GT );
        return sbCatRules.toString();
    }

    private Map<String, String> parseCatRules( Node catRulesNode ) {
        Map<String, String> catRules;
        NodeList catRulesNodeChildren = catRulesNode.getChildNodes();
        if ( catRulesNodeChildren.getLength() > 1 ) throw new IllegalArgumentException( "Wrong xml format: " + MODULE_CATRULES );
        catRules = ExportXmlUtils.parseMap( catRulesNodeChildren.item( 0 ) );
        return catRules;
    }

    private String formatPackageHeaderInfo( String packageHeaderInfo ) {
        StringBuilder sbPackageHeader = new StringBuilder( LT );
        sbPackageHeader.append( MODULE_PACKAGEHEADER ).append( GT );
        if ( StringUtils.isNotBlank( packageHeaderInfo ) ) {
            sbPackageHeader.append( ExportXmlUtils.formatCdataSection( packageHeaderInfo ) );
        }
        sbPackageHeader.append( LT_SLASH ).append( MODULE_PACKAGEHEADER ).append( GT );
        return sbPackageHeader.toString();
    }

    private String parsePackageHeaderInfo( Node headerInfoNode ) {
        NodeList headerNodeChildren = headerInfoNode.getChildNodes();
        if ( headerNodeChildren.getLength() > 1 ) throw new IllegalArgumentException( "Wrong xml format: " + MODULE_PACKAGEHEADER );
        return ExportXmlUtils.parseCdataSection( headerNodeChildren.item( 0 ) );
    }
}