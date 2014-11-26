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
import org.w3c.dom.Node;

public class PlainTextAssetFormat implements XmlFormat<PlainTextAsset> {

    // TODO move these into (abstract) base class if needed and possible
    private static final String ASSET = "asset";
    private static final String ASSET_TYPE = "assetType";

    private static final String TEXT_CONTENT = "textContent";

    @Override
    public void format( StringBuilder sb, PlainTextAsset plainTextAsset ) {
        if ( sb == null || plainTextAsset == null ) throw new IllegalArgumentException( "No output or plain text asset specified" );

        sb.append( LT ).append( ASSET ).append( GT );

        sb.append( LT ).append( ASSET_TYPE ).append( GT ).append( plainTextAsset.getAssetType().toString() )
                .append( LT_SLASH ).append( ASSET_TYPE ).append( GT );
        sb.append( LT ).append( TEXT_CONTENT ).append( GT ).append( ExportXmlUtils.formatCdataSection( plainTextAsset.getContent() ) )
                .append( LT_SLASH ).append( TEXT_CONTENT ).append( GT );

        sb.append( LT_SLASH ).append( ASSET ).append( GT );

    }

    @Override
    public PlainTextAsset parse( Node node ) {
        return null;
    }
}
