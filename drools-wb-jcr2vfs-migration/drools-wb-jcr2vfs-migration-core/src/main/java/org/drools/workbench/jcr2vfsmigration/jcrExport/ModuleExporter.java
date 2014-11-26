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
package org.drools.workbench.jcr2vfsmigration.jcrExport;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.rpc.SerializationException;
import org.apache.commons.lang.StringUtils;
import org.drools.guvnor.client.rpc.AssetPageRequest;
import org.drools.guvnor.client.rpc.AssetPageRow;
import org.drools.guvnor.client.rpc.PageResponse;
import org.drools.guvnor.server.RepositoryAssetService;
import org.drools.guvnor.server.RepositoryModuleService;
import org.drools.guvnor.server.repository.Preferred;
import org.drools.repository.AssetItem;
import org.drools.repository.RulesRepository;
import org.drools.workbench.jcr2vfsmigration.migrater.util.MigrationPathManager;
import org.drools.workbench.jcr2vfsmigration.util.FileManager;
import org.drools.workbench.jcr2vfsmigration.xml.format.ModulesXmlFormat;
import org.drools.workbench.jcr2vfsmigration.xml.model.Module;
import org.drools.workbench.jcr2vfsmigration.xml.model.ModuleType;
import org.drools.workbench.jcr2vfsmigration.xml.model.Modules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class ModuleExporter {

    protected static final Logger logger = LoggerFactory.getLogger( ModuleExporter.class );

    private static int assetFileName = 1;

    @Inject
    protected RepositoryModuleService jcrRepositoryModuleService;

    @Inject
    protected RepositoryAssetService jcrRepositoryAssetService;

    @Inject
    @Preferred
    private RulesRepository rulesRepository;

    @Inject
    protected MigrationPathManager migrationPathManager;

    @Inject
    FileManager fileManager;

    ModulesXmlFormat modulesXmlFormat = new ModulesXmlFormat();

    public void exportAll() {

        System.out.println( "  Module export started" );
        org.drools.guvnor.client.rpc.Module jcrGlobalModule = jcrRepositoryModuleService.loadGlobalModule();
        org.drools.guvnor.client.rpc.Module[] jcrModules = jcrRepositoryModuleService.listModules();

        if ( jcrGlobalModule == null && jcrModules.length == 0 ) {
            System.out.println( "  No modules to be exported" );
            return;
        }

        Collection<Module> normalModules = new ArrayList<Module>( 5 );
        for ( org.drools.guvnor.client.rpc.Module jcrModule : jcrModules ) {
            normalModules.add( export( ModuleType.NORMAL, jcrModule ) );
        }

        Module globalModule = export( ModuleType.GLOBAL, jcrGlobalModule );

        Modules modules = new Modules( globalModule, normalModules );

        StringBuilder xml = new StringBuilder();
        modulesXmlFormat.format( xml, modules );

        PrintWriter pw = fileManager.createModuleExportFileWriter();
        pw.print( xml.toString() );
        pw.close();

        System.out.println( "  Module export ended" );
    }

    private Module export( ModuleType moduleType, org.drools.guvnor.client.rpc.Module jcrModule ) {
        System.out.format( "Module [%s] exported. %n", jcrModule.getName() );

        //setting CategoryRules to jcr module (needed in asset migration)
        for( org.drools.repository.ModuleIterator packageItems = rulesRepository.listModules(); packageItems.hasNext(); ) {
            org.drools.repository.ModuleItem packageItem = packageItems.next();
            if( packageItem.getUUID().equals( jcrModule.getUuid() ) ){
                jcrModule.setCatRules( packageItem.getCategoryRules() );
                break;
            }
        }

        String normalizedPackageName = migrationPathManager.normalizePackageName( jcrModule.getName() );

        // Export package header info
        String packageHeaderInfo = null;
        jcrModule.setName( normalizedPackageName );
        try {
            List<String> formats = new ArrayList<String>();
            formats.add("package");
            AssetPageRequest request = new AssetPageRequest(jcrModule.getUuid(),
                    formats,
                    null,
                    0,
                    10);
            PageResponse<AssetPageRow> response = jcrRepositoryAssetService.findAssetPage(request);
            if (response.getTotalRowSize() > 0) {
                AssetPageRow row = response.getPageRowList().get(0);
                AssetItem assetItemJCR = rulesRepository.loadAssetByUUID(row.getUuid());

                packageHeaderInfo = assetItemJCR.getContent();
            }
        } catch ( SerializationException e ) {
            throw new IllegalStateException( e );
        }

        String assetExportFileName = setupAssetExportFile( jcrModule.getUuid() );

        return new Module( moduleType,
                           jcrModule.getUuid(),
                           jcrModule.getName(),
                           normalizedPackageName,
                           packageHeaderInfo,
                           jcrModule.getCatRules(),
                           assetExportFileName );
    }

    // Attempt creation of the asset export file firstly with the module's uuid. If this were null or the file could not
    // be successfully created, then try again with a shorter (i.e. simple number) name.
    private String setupAssetExportFile( String moduleUuid ) {
        StringBuilder fileNameBuilder = new StringBuilder();
        boolean success = false;
        if ( StringUtils.isNotBlank( moduleUuid ) ) {
            fileNameBuilder.insert( 0, moduleUuid );
            success = fileManager.createAssetExportFile( fileNameBuilder.toString() );
        }
        if ( !success ) {
            fileNameBuilder.replace( 0, fileNameBuilder.lastIndexOf( "." ), Integer.toString( assetFileName++ ) );
            success = fileManager.createAssetExportFile( fileNameBuilder.toString() );
            if ( ! success ) {
                System.out.println( "Module asset file could not be created" );
                return null;
            }
        }
        return fileNameBuilder.toString();
    }
}
