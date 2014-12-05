/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.jcr2vfsmigration;

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.drools.workbench.jcr2vfsmigration.config.VfrImportConfig;
import org.drools.workbench.jcr2vfsmigration.util.MigrationPathManager;
import org.drools.workbench.jcr2vfsmigration.common.FileManager;
import org.drools.workbench.jcr2vfsmigration.vfsImport.CategoryImporter;
import org.drools.workbench.jcr2vfsmigration.vfsImport.ModuleAssetImporter;
import org.jboss.weld.context.bound.BoundRequestContext;
import org.jboss.weld.context.bound.BoundSessionContext;

@ApplicationScoped
public class VfsImporter {

    @Inject
    protected VfrImportConfig vfsImportConfig;

    @Inject
    FileManager fileManager;

    @Inject
    protected CategoryImporter categoryImporter;

    @Inject
    protected MigrationPathManager migrationPathManager;

    @Inject
    protected ModuleAssetImporter moduleAssetImporter;

    @Inject
    protected BoundSessionContext sessionContext;
    protected Map<String, Object> sessionDataStore;

    @Inject
    protected BoundRequestContext requestContext;
    protected Map<String, Object> requestDataStore;

    public boolean parseArgs( String[] args ) {
        boolean ok = vfsImportConfig.parseArgs( args );
        if ( ok ) fileManager.setExportTempDir( vfsImportConfig.getImportTempDir() );
        return ok;
    }

    public void importAll() {
        System.out.format( "Jcr import started. Reading from import directory ({%s}).%n",
                vfsImportConfig.getImportTempDir().getAbsolutePath() );

        try {
            migrationPathManager.setRepoName( vfsImportConfig.getOutputRepoName(), vfsImportConfig.getOutputVfsRepository().getCanonicalPath() );
            startContexts();

            //TO-DO-LIST:
            //1. Migrate globalArea: handle asset imported from globalArea. assetServiceJCR.findAssetPage will return assets imported from globalArea
            //(like a symbol link). Use Asset.getMetaData().getModuleName()=="globalArea" to determine if the asset is actually from globalArea.
            //2. Migrate categories
            //3. Migrate state
            //4. Migrate Guvnor package based permissions: admin/package.admin/package.developer/package.readonly
            //(and dont forget to migrate category based permission, ie, analyst/analyst.readonly)

            categoryImporter.importAll();
            moduleAssetImporter.importAll();

            // TODO Refresh the index at the end, similar as in https://github.com/droolsjbpm/kie-commons/blob/master/kieora/kieora-commons-io/src/test/java/org/kie/kieora/io/BatchIndexTest.java
            endContexts();
        } catch ( Throwable t ) {
            //We print out whatever unexpected exceptions we got here
            t.printStackTrace();
        }

    }

    protected void startContexts() {
        sessionDataStore = new HashMap<String, Object>();
        sessionContext.associate( sessionDataStore );
        sessionContext.activate();
        requestDataStore = new HashMap<String, Object>();
        requestContext.associate( requestDataStore );
        requestContext.activate();
    }

    protected void endContexts() {
        try {
            requestContext.invalidate();
            requestContext.deactivate();
        } finally {
            requestContext.dissociate( requestDataStore );
        }
        try {
            sessionContext.invalidate();
            sessionContext.deactivate();
        } finally {
            sessionContext.dissociate( sessionDataStore );
        }
    }
}
