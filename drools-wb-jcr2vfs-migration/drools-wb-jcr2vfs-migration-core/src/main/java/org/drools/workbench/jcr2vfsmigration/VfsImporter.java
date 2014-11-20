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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.drools.guvnor.server.repository.GuvnorBootstrapConfiguration;
import org.drools.workbench.jcr2vfsmigration.config.MigrationConfig;
import org.drools.workbench.jcr2vfsmigration.config.VfrImportConfig;
import org.drools.workbench.jcr2vfsmigration.migrater.AssetMigrater;
import org.drools.workbench.jcr2vfsmigration.migrater.CategoryMigrater;
import org.drools.workbench.jcr2vfsmigration.migrater.ModuleMigrater;
import org.drools.workbench.jcr2vfsmigration.migrater.util.MigrationPathManager;
import org.drools.workbench.jcr2vfsmigration.util.FileManager;
import org.drools.workbench.jcr2vfsmigration.vfsImport.CategoryImporter;
import org.drools.workbench.jcr2vfsmigration.vfsImport.ModuleImporter;
import org.jboss.weld.context.bound.BoundRequestContext;
import org.jboss.weld.context.bound.BoundSessionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class VfsImporter {

    protected static final Logger logger = LoggerFactory.getLogger( VfsImporter.class );

    @Inject
    protected VfrImportConfig vfsImportConfig;

    @Inject
    FileManager fileManager;

    @Inject
    protected ModuleImporter moduleImporter;

    @Inject
    protected CategoryImporter categoryImporter;

    @Inject
    protected MigrationPathManager migrationPathManager;

    @Inject
    protected AssetMigrater assetMigrater;

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
//            setupDirectories();
            startContexts();

            //TO-DO-LIST:
            //1. Migrate globalArea: handle asset imported from globalArea. assetServiceJCR.findAssetPage will return assets imported from globalArea
            //(like a symbol link). Use Asset.getMetaData().getModuleName()=="globalArea" to determine if the asset is actually from globalArea.
            //2. Migrate categories
            //3. Migrate state
            //4. Migrate Guvnor package based permissions: admin/package.admin/package.developer/package.readonly
            //(and dont forget to migrate category based permission, ie, analyst/analyst.readonly)

//            moduleImporter.importAll();
            categoryImporter.importAll();
//            assetMigrater.migrateAll();

            // TODO Refresh the index at the end, similar as in https://github.com/droolsjbpm/kie-commons/blob/master/kieora/kieora-commons-io/src/test/java/org/kie/kieora/io/BatchIndexTest.java
            endContexts();
        } catch ( Throwable t ) {
            //We print out whatever unexpected exceptions we got here
            Jcr2VfsMigrationApp.hasErrors = true;
            t.printStackTrace();
        }

        // TODO
//        if ( Jcr2VfsMigrationApp.hasWarnings ) {
//            System.out.format( "Migration ended with warnings. Written into outputVfsRepository ({%s}).%n",
//                               migrationConfig.getOutputVfsRepository().getAbsolutePath() );
//        } else if ( Jcr2VfsMigrationApp.hasErrors ) {
//            System.out.format( "Migration ended with errors. Written into outputVfsRepository ({%s}).%n",
//                               migrationConfig.getOutputVfsRepository().getAbsolutePath() );
//        } else {
//            System.out.format( "Migration ended. Written into outputVfsRepository ({%s}).%n",
//                               migrationConfig.getOutputVfsRepository().getAbsolutePath() );
//        }
    }

//    protected void setupDirectories() {
//        guvnorBootstrapConfiguration.getProperties().put( "repository.root.directory", determineJcrRepositoryRootDirectory() );
////        System.setProperty( "org.uberfire.nio.git.dir", migrationConfig.getOutputVfsRepository().getAbsolutePath() );
//    }

    /**
     * Workaround the repository.xml and repository directory layout mess.
     * <p/>
     * If repository.root.directory was NOT specified, the layout looks like this:
     * <pre>
     * repository.xml
     * repository
     * repository/repository
     * repository/repository/datastore
     * repository/repository/index
     * repository/repository/...
     * repository/version
     * repository/version/...
     * repository/workspaces
     * repository/workspaces/...
     * </pre>
     * If repository.root.directory was specified however, the layout looks like this:
     * <pre>
     * repository.xml
     * repository
     * repository/datastore
     * repository/index
     * repository/...
     * version
     * version/...
     * workspaces
     * workspaces/...
     * </pre>
     * @return never null
     */
//    protected String determineJcrRepositoryRootDirectory() {
//        File inputJcrRepository = vfrImportConfig.getInputJcrRepository();
//        File repositoryXmlFile = new File( inputJcrRepository, "repository.xml" );
//        if ( !repositoryXmlFile.exists() ) {
//            throw new IllegalStateException(
//                    "The repositoryXmlFile (" + repositoryXmlFile.getAbsolutePath() + ") does not exist.\n"
//                            + "Check your inputJcrRepository (" + inputJcrRepository + ")." );
//        }
//        File repositoryDir = new File( inputJcrRepository, "repository" );
//        if ( !repositoryDir.exists() ) {
//            // They are using a non-default repository.xml (for example with storage in a database)
//            return inputJcrRepository.getAbsolutePath();
//        }
//        File unnestedVersionDir = new File( inputJcrRepository, "version" );
//        File nestedVersionDir = new File( repositoryDir, "version" );
//        if ( unnestedVersionDir.exists() ) {
//            // repository.root.directory was specified
//            return inputJcrRepository.getAbsolutePath();
//        } else if ( nestedVersionDir.exists() ) {
//            // repository.root.directory was not specified => HACK
//            try {
//                FileUtils.copyFile( repositoryXmlFile, new File( repositoryDir, "repository.xml" ) );
//            } catch ( IOException e ) {
//                throw new IllegalStateException( "Cannot copy repositoryXmlFile (" + repositoryXmlFile + ").", e );
//            }
//            return inputJcrRepository.getAbsolutePath() + "/repository";
//        } else {
//            //the "version" dir does not exist if JCR is not using embedded db.
///*            throw new IllegalStateException(
//                    "The unnestedVersionDir (" + unnestedVersionDir.getAbsolutePath()
//                    + ") and the nestedVersionDir (" + nestedVersionDir.getAbsolutePath() + ") does not exist.");*/
//        }
//        return inputJcrRepository.getAbsolutePath();
//
//    }

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
