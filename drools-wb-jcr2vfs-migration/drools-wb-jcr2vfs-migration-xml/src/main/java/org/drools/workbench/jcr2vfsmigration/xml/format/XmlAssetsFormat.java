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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.drools.workbench.jcr2vfsmigration.xml.model.asset.XmlAsset;
import org.drools.workbench.jcr2vfsmigration.xml.model.asset.XmlAssets;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlAssetsFormat implements XmlFormat<XmlAssets> {

    public static final String ASSETS = "assets";

    private XmlAssetFormat xmlAssetFormat = new XmlAssetFormat();

    @Override
    public void format( StringBuilder sb, XmlAssets xmlAssets ) {
        if ( sb == null || xmlAssets == null ) throw new IllegalArgumentException( "No output or assets specified" );
        sb.append( LT ).append( ASSETS ).append( GT );

        for ( Iterator<XmlAsset> it = xmlAssets.getAssets().iterator(); it.hasNext(); ) {
            xmlAssetFormat.format( sb, it.next() );
        }

        sb.append( LT_SLASH ).append( ASSETS ).append( GT );
    }

    @Override
    public XmlAssets parse( Node assetsNode ) {
        if ( assetsNode == null || !ASSETS.equals( assetsNode.getNodeName() ) ) throw new IllegalArgumentException( "No input assets node specified for parsing" );
        Collection<XmlAsset> assets = new ArrayList<XmlAsset>( 10 );

        NodeList assetNodes = assetsNode.getChildNodes();
        for ( int i = 0; i < assetNodes.getLength(); i++ ) {
            Node assetNode = assetNodes.item( i );
            if ( assetNode != null ) {
                XmlAsset xmlAsset = xmlAssetFormat.parse( assetNode );
                assets.add( xmlAsset );
            }
        }
        return new XmlAssets( assets );
    }
}
