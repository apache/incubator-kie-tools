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
import org.drools.workbench.jcr2vfsmigration.migrater.util.DRLMigrationUtils;
import org.drools.workbench.jcr2vfsmigration.xml.model.asset.PlainTextAsset;

public class PlainTextAssetExporter
        extends BaseAssetExporter
        implements AssetExporter<PlainTextAsset> {

    public PlainTextAsset export( Module jcrModule, AssetItem jcrAssetItem ) {

        String name = jcrAssetItem.getName();
        String format = jcrAssetItem.getFormat();
        String content = jcrAssetItem.getContent();

        // Support for '#' has been removed from Drools Expert -> replace it with '//'
        if ( AssetFormats.DSL.equals(format)
                || AssetFormats.DSL_TEMPLATE_RULE.equals(format)
                || AssetFormats.RULE_TEMPLATE.equals(format)
                || AssetFormats.DRL.equals(format)
                || AssetFormats.FUNCTION.equals(format)) {
            content = DRLMigrationUtils.migrateStartOfCommentChar( content );
        }
        if (AssetFormats.RULE_TEMPLATE.equals(format)){
            content = content.replaceAll("org.drools.guvnor.client.modeldriven.dt.TemplateModel","rule");
        }
        if (AssetFormats.WORKITEM_DEFINITION.equals(format)){
            content = content.replaceAll("org.drools.process.core.","org.drools.core.process.core.");
        }

        return new PlainTextAsset( name, format, content );
    }
}
