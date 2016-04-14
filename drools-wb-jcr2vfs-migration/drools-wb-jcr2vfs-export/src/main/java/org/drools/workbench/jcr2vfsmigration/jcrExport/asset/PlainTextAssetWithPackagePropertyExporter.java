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
package org.drools.workbench.jcr2vfsmigration.jcrExport.asset;

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.server.contenthandler.drools.DRLFileContentHandler;
import org.drools.workbench.jcr2vfsmigration.util.ExportUtils;
import org.drools.workbench.jcr2vfsmigration.xml.model.asset.PlainTextAsset;

import java.util.Arrays;

public class PlainTextAssetWithPackagePropertyExporter
        extends BaseAssetExporter
        implements AssetExporter<PlainTextAsset, ExportContext> {

    @Override
    public PlainTextAsset export( ExportContext exportContext ) {

        String format = exportContext.getJcrAssetItem().getFormat();

        StringBuilder sb = new StringBuilder();
        // both .drl and .dslr files can contain standalone rules
        if ( Arrays.asList( AssetFormats.DRL, AssetFormats.DSL_TEMPLATE_RULE ).contains( format ) &&
                isStandaloneRule( exportContext.getJcrAssetItem().getContent() ) ) {
            sb.append( "rule \"" + exportContext.getJcrAssetItem().getName() + "\"" );
            sb.append( getExtendExpression( exportContext.getJcrModule(), exportContext.getJcrAssetItem(), "") );
            sb.append( "\n" );
            sb.append( "\n" );
            sb.append( exportContext.getJcrAssetItem().getContent() );
            sb.append( "\n" );
            sb.append( "\n" );
            sb.append( "end" );
        } else {
            sb.append( exportContext.getJcrAssetItem().getContent() );
            sb.append( "\n" );
        }
        String content = sb.toString();

        // Support for '#' has been removed from Drools Expert -> replace it with '//'
        if (AssetFormats.DSL.equals(format)
                || AssetFormats.DSL_TEMPLATE_RULE.equals(format)
                || AssetFormats.RULE_TEMPLATE.equals(format)
                || AssetFormats.DRL.equals(format)
                || AssetFormats.FUNCTION.equals(format)) {
            content = ExportUtils.migrateStartOfCommentChar( content );
        }

        return new PlainTextAsset( exportContext.getJcrAssetItem().getName(),
                                   format,
                                   exportContext.getJcrAssetItem().getLastContributor(),
                                   exportContext.getJcrAssetItem().getCheckinComment(),
                                   exportContext.getJcrAssetItem().getLastModified().getTime(),
                                   content );
    }

    private boolean isStandaloneRule(String drl) {
        return DRLFileContentHandler.isStandAloneRule(drl);
    }
}
