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

import java.util.EnumMap;

import org.drools.workbench.jcr2vfsmigration.xml.model.asset.AssetType;
import org.drools.workbench.jcr2vfsmigration.xml.model.asset.XmlAsset;
import org.w3c.dom.Node;

public class XmlAssetFormat implements XmlFormat<XmlAsset> {

    private static EnumMap<AssetType, XmlFormat<?>> assetFormats = new EnumMap<AssetType, XmlFormat<?>>( AssetType.class );

    static {
        PlainTextAssetFormat ptaf = new PlainTextAssetFormat();
        assetFormats.put( AssetType.ENUMERATION, ptaf );
        assetFormats.put( AssetType.DSL, ptaf );
        assetFormats.put( AssetType.DSL_TEMPLATE_RULE, ptaf );
        assetFormats.put( AssetType.RULE_TEMPLATE, ptaf );
        assetFormats.put( AssetType.FORM_DEFINITION, ptaf );
        assetFormats.put( AssetType.SPRING_CONTEXT, ptaf );
        assetFormats.put( AssetType.SERVICE_CONFIG, ptaf );
        assetFormats.put( AssetType.WORKITEM_DEFINITION, ptaf );
        assetFormats.put( AssetType.CHANGE_SET, ptaf );
        assetFormats.put( AssetType.RULE_FLOW_RF, ptaf );
        assetFormats.put( AssetType.BPMN_PROCESS, ptaf );
        assetFormats.put( AssetType.BPMN2_PROCESS, ptaf );
        assetFormats.put( AssetType.FTL, ptaf );
        assetFormats.put( AssetType.JSON, ptaf );
        assetFormats.put( AssetType.FW, ptaf );
    }

    @Override
    public void format( StringBuilder sb, XmlAsset xmlAsset ) {
        if ( sb == null || xmlAsset == null ) throw new IllegalArgumentException( "No output or asset specified" );

        XmlFormat xmlFormat = assetFormats.get( xmlAsset.getAssetType() );
        if ( xmlFormat == null ) throw new RuntimeException( "Xml asset format is undefined" );
        xmlFormat.format( sb, xmlAsset );
        return;
    }

    @Override
    public XmlAsset parse( Node node ) {
        return null;
    }
}
