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
package org.drools.workbench.jcr2vfsmigration.vfsImport.asset;

import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;

import org.drools.workbench.jcr2vfsmigration.migrater.util.DRLMigrationUtils;
import org.drools.workbench.jcr2vfsmigration.migrater.util.MigrationPathManager;
import org.drools.workbench.jcr2vfsmigration.xml.model.Module;
import org.drools.workbench.jcr2vfsmigration.xml.model.asset.AssetType;
import org.drools.workbench.jcr2vfsmigration.xml.model.asset.PlainTextAsset;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;

public class PlainTextAssetImporter implements AssetImporter<PlainTextAsset> {

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    protected MigrationPathManager migrationPathManager;

    public void importAsset( Module xmlModule, PlainTextAsset asset ) {
        Path path = migrationPathManager.generatePathForAsset( xmlModule, asset );
        final org.uberfire.java.nio.file.Path nioPath = Paths.convert( path );

        String content = asset.getContent();

        // Support for '#' has been removed from Drools Expert -> replace it with '//'
        if ( AssetType.DSL.equals(asset.getAssetType())
                || AssetType.DSL_TEMPLATE_RULE.equals(asset.getAssetType())
                || AssetType.RULE_TEMPLATE.equals(asset.getAssetType())
                || AssetType.DRL.equals(asset.getAssetType())
                || AssetType.FUNCTION.equals(asset.getAssetType())) {
            content = DRLMigrationUtils.migrateStartOfCommentChar( content );
        }
        if (AssetType.RULE_TEMPLATE.equals(asset.getAssetType())){
            content = content.replaceAll("org.drools.guvnor.client.modeldriven.dt.TemplateModel","rule");
        }
        if (AssetType.WORKITEM_DEFINITION.equals(asset.getAssetType())){
            content = content.replaceAll("org.drools.process.core.","org.drools.core.process.core.");
        }

        ioService.write( nioPath,
                         content,
                         (Map) null,    // cast is for disambiguation
// todo                migrateMetaData(jcrModule, asset),
//                new CommentedOption( asset.getLastContributor(),
//                        null,
//                        asset.getCheckinComment(),
//                        asset.getLastModified().getTime() ) );
                         new CommentedOption( "" )
                       );


    }
}
