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

import org.drools.guvnor.client.rpc.Module;
import org.drools.repository.AssetItem;
import org.drools.workbench.jcr2vfsmigration.xml.model.asset.GuidedDecisionTableAsset;

public class GuidedDecisionTableExporter
        extends BaseAssetExporter
        implements AssetExporter<GuidedDecisionTableAsset> {

    @Override
    public GuidedDecisionTableAsset export( Module jcrModule, AssetItem jcrAssetItem ) {

        String name = jcrAssetItem.getName();
        String format = jcrAssetItem.getFormat();
        String content = jcrAssetItem.getContent();
        String extendedRule = getExtendedRuleFromCategoryRules(jcrModule,jcrAssetItem,"");

        return new GuidedDecisionTableAsset( name, format, content, extendedRule );
    }
}
