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

import org.drools.workbench.jcr2vfsmigration.migrater.util.MigrationPathManager;
import org.drools.workbench.jcr2vfsmigration.xml.model.Module;
import org.drools.workbench.jcr2vfsmigration.xml.model.asset.GuidedDecisionTableAsset;
import org.drools.workbench.models.commons.backend.imports.ImportsParser;
import org.drools.workbench.models.datamodel.imports.Imports;
import org.drools.workbench.models.guided.dtable.backend.GuidedDTXMLPersistence;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.guvnor.common.services.project.service.ProjectService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;

public class GuidedDecisionTableImporter implements AssetImporter<GuidedDecisionTableAsset> {

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    protected MigrationPathManager migrationPathManager;

    @Inject
    private ProjectService projectService;

    @Override
    public void importAsset( Module xmlModule, GuidedDecisionTableAsset xmlAsset ) {

        Path path = migrationPathManager.generatePathForAsset( xmlModule, xmlAsset );
        final org.uberfire.java.nio.file.Path nioPath = Paths.convert( path );

        String packageHeader = xmlModule.getPackageHeaderInfo();
        String content = xmlAsset.getContent();
        String extendedRule = xmlAsset.getExtendedRule();

        content = content.replaceAll( "org.drools.guvnor.client.modeldriven.dt52.Pattern52", "Pattern52" );
        GuidedDecisionTable52 model = GuidedDTXMLPersistence.getInstance().unmarshal( content );

        // Add package
        final org.guvnor.common.services.project.model.Package pkg = projectService.resolvePackage( path );
        String pkName = pkg.getPackageName();
        try {
            if( pkName != null && pkg.getPackageName().endsWith( path.getFileName() ) ) {
                pkName = pkg.getPackageName().substring( 0, pkg.getPackageName().indexOf( path.getFileName() )-1 );
            }
        } catch ( Exception e ){
            e.printStackTrace();
        }
        final String requiredPackageName = pkName;
        if ( requiredPackageName != null && !"".equals( requiredPackageName ) ) {
            model.setPackageName( requiredPackageName );
        }
        model.setParentName( extendedRule );

        // Add import
        if ( packageHeader != null ) {
            final Imports imports = ImportsParser.parseImports( packageHeader );
            if ( imports != null ) {
                model.setImports( imports );
            }
        }
        String sourceContent = GuidedDTXMLPersistence.getInstance().marshal( model );

        ioService.write( nioPath,
                         sourceContent,
                         (Map) null,    // cast is for disambiguation
// todo               migrateMetaData(jcrModule, asset),
// todo               new CommentedOption( asset.getLastContributor(),
//                        null,
//                        asset.getCheckinComment(),
//                        asset.getLastModified().getTime() ) );
                         new CommentedOption( "" )
        );
    }
}
