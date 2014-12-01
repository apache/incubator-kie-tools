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

import org.drools.workbench.jcr2vfsmigration.xml.model.asset.AssetType;
import org.drools.workbench.jcr2vfsmigration.xml.model.asset.PlainTextAsset;
import org.drools.workbench.jcr2vfsmigration.xml.model.asset.XmlAsset;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class XmlAssetFormat implements XmlFormat<XmlAsset> {

    public static final String ASSET = "asset";
    public static final String ASSET_NAME = "name";
    public static final String ASSET_TYPE = "type";

    private static PlainTextAssetFormat ptaf = new PlainTextAssetFormat();

    @Override
    public void format( StringBuilder sb, XmlAsset xmlAsset ) {
        if ( sb == null || xmlAsset == null ) throw new IllegalArgumentException( "No output or asset specified" );

        switch ( xmlAsset.getAssetType() ) {
            case ENUMERATION:
            case DSL:
            case DSL_TEMPLATE_RULE:
            case RULE_TEMPLATE:
            case FORM_DEFINITION:
            case SPRING_CONTEXT:
            case SERVICE_CONFIG:
            case WORKITEM_DEFINITION:
            case CHANGE_SET:
            case RULE_FLOW_RF:
            case BPMN_PROCESS:
            case BPMN2_PROCESS:
            case FTL:
            case JSON:
            case FW:
            case DRL :
            case FUNCTION: ptaf.format( sb, ( PlainTextAsset ) xmlAsset ); break;
        }
    }

    @Override
    public XmlAsset parse( Node assetNode ) {
        if ( assetNode == null || !ASSET.equals( assetNode.getNodeName() ) ) throw new IllegalArgumentException( "No input asset node specified for parsing" );

        NamedNodeMap assetAttribs = assetNode.getAttributes();
        if ( assetAttribs == null ) throw new RuntimeException( "Wrong asset xml format; missing type" );
        String assetType = assetAttribs.getNamedItem( ASSET_TYPE ).getNodeValue();

        switch ( AssetType.getByName( assetType ) ) {
            case ENUMERATION:
            case DSL:
            case DSL_TEMPLATE_RULE:
            case RULE_TEMPLATE:
            case FORM_DEFINITION:
            case SPRING_CONTEXT:
            case SERVICE_CONFIG:
            case WORKITEM_DEFINITION:
            case CHANGE_SET:
            case RULE_FLOW_RF:
            case BPMN_PROCESS:
            case BPMN2_PROCESS:
            case FTL:
            case JSON:
            case FW:
            case DRL:
            case FUNCTION: return ptaf.parse( assetNode );
        }
        return null;
    }
}
