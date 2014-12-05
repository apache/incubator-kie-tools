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
package org.drools.workbench.jcr2vfsmigration.jcrExport.asset;

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.Module;
import org.drools.repository.AssetItem;
import org.drools.workbench.jcr2vfsmigration.util.ExportUtils;
import org.drools.workbench.jcr2vfsmigration.xml.model.asset.PlainTextAsset;

public class PlainTextAssetWithPackagePropertyExporter
        extends BaseAssetExporter
        implements AssetExporter<PlainTextAsset> {

    @Override
    public PlainTextAsset export( Module jcrModule, AssetItem jcrAssetItem ) {
        String name = jcrAssetItem.getName();
        String format = jcrAssetItem.getFormat();

        StringBuilder sb = new StringBuilder();
        if ( AssetFormats.DRL.equals( jcrAssetItem.getFormat() ) && jcrAssetItem.getContent().toLowerCase().indexOf("rule ")==-1 ) {
            sb.append( "rule \"" + jcrAssetItem.getName() + "\"" );
            sb.append( getExtendExpression(jcrModule,jcrAssetItem,"") );
            sb.append( "\n" );
            sb.append( "\n" );
            sb.append( jcrAssetItem.getContent() );
            sb.append( "\n" );
            sb.append( "\n" );
            sb.append( "end" );
        }
        else{
            sb.append( jcrAssetItem.getContent() );
            sb.append( "\n" );
        }
        String content = sb.toString();

        // Support for '#' has been removed from Drools Expert -> replace it with '//'
        if (AssetFormats.DSL.equals(jcrAssetItem.getFormat())
                || AssetFormats.DSL_TEMPLATE_RULE.equals(jcrAssetItem.getFormat())
                || AssetFormats.RULE_TEMPLATE.equals(jcrAssetItem.getFormat())
                || AssetFormats.DRL.equals(jcrAssetItem.getFormat())
                || AssetFormats.FUNCTION.equals(jcrAssetItem.getFormat())) {
            content = ExportUtils.migrateStartOfCommentChar( content );
        }

        return new PlainTextAsset( name, format, content );
    }
}
