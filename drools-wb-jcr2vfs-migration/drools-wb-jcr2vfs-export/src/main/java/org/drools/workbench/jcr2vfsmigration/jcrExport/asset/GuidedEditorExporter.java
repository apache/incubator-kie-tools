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

import javax.inject.Inject;

import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.server.builder.BRMSPackageBuilder;
import org.drools.guvnor.server.contenthandler.drools.BRLContentHandler;
import org.drools.guvnor.server.repository.Preferred;
import org.drools.ide.common.client.modeldriven.brl.RuleModel;
import org.drools.ide.common.server.util.BRXMLPersistence;
import org.drools.repository.AssetItem;
import org.drools.repository.RulesRepository;
import org.drools.workbench.jcr2vfsmigration.xml.model.asset.BusinessRuleAsset;

public class GuidedEditorExporter
        extends BaseAssetExporter
        implements AssetExporter<BusinessRuleAsset> {

    @Inject
    @Preferred
    private RulesRepository rulesRepository;

    @Override
    public BusinessRuleAsset export( Module jcrModule, AssetItem jcrAssetItem ) {

        String name = jcrAssetItem.getName();
        String format = jcrAssetItem.getFormat();
        String content = jcrAssetItem.getContent();

        RuleModel ruleModel = BRXMLPersistence.getInstance().unmarshal( content );
        boolean hasDSL = ruleModel.hasDSLSentences();

        StringBuilder sb = new StringBuilder();
        BRMSPackageBuilder builder = new BRMSPackageBuilder( rulesRepository.loadModuleByUUID( jcrModule.getUuid() ) );
        BRLContentHandler handler = new BRLContentHandler();
        handler.assembleDRL( builder, jcrAssetItem, sb );

        //Support for # has been removed from Drools Expert
        content = sb.toString().replaceAll( "#", "//" );
        content = getExtendExpression( jcrModule, jcrAssetItem, content);

        return new BusinessRuleAsset( name, format, content, hasDSL );
    }
}
