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
import org.drools.workbench.jcr2vfsmigration.migrater.AssetMigrater;
import org.drools.workbench.jcr2vfsmigration.migrater.CategoryMigrater;
import org.drools.workbench.jcr2vfsmigration.migrater.ModuleMigrater;
import org.jboss.weld.context.bound.BoundRequestContext;
import org.jboss.weld.context.bound.BoundSessionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class Jcr2VfsMigrater {

    protected static final Logger logger = LoggerFactory.getLogger(Jcr2VfsMigrater.class);

    @Inject
    protected MigrationConfig migrationConfig;

    @Inject
    protected GuvnorBootstrapConfiguration guvnorBootstrapConfiguration;

    @Inject
    protected ModuleMigrater moduleMigrater;
    @Inject
    protected AssetMigrater assetMigrater;
    @Inject
    protected CategoryMigrater categoryMigrater;

    @Inject
    protected BoundSessionContext sessionContext;
    protected Map<String, Object> sessionDataStore;

    @Inject
    protected BoundRequestContext requestContext;
    protected Map<String, Object> requestDataStore;

    public void parseArgs(String[] args) {
        migrationConfig.parseArgs(args);
    }

    public void migrateAll() {
        logger.info("Migration started: Reading from inputJcrRepository ({}).",
                migrationConfig.getInputJcrRepository().getAbsolutePath());
        setupDirectories();
        startContexts();
//    //TO-DO-LIST:
//    //1. How to migrate the globalArea (moduleServiceJCR.listModules() wont return globalArea)
//    //2. This is also globalArea related: How to handle asset imported from globalArea. assetServiceJCR.findAssetPage will return assets imported from globalArea
//    //(like a symbol link). Use Asset.getMetaData().getModuleName()=="globalArea" to determine if the asset is actually from globalArea.
//    //4. Do we want to migrate package snapshot? probably not...As long as we migrate package history correctly, users can always build a package
//    //with the specified version by themselves.
      //5. Migrate categories
      //6. migratePackagePermissions.   migrateRolesAndPermissionsMetaData

        moduleMigrater.migrateAll();
        assetMigrater.migrateAll();
        categoryMigrater.migrateAll();
        // TODO Refresh the index at the end, similar as in https://github.com/droolsjbpm/kie-commons/blob/master/kieora/kieora-commons-io/src/test/java/org/kie/kieora/io/BatchIndexTest.java
        endContexts();
        logger.info("Migration ended: Written into outputVfsRepository ({}).",
                migrationConfig.getOutputVfsRepository().getAbsolutePath());
    }

    protected void setupDirectories() {
        guvnorBootstrapConfiguration.getProperties().put("repository.root.directory",
                determineJcrRepositoryRootDirectory());
        System.setProperty("org.kie.nio.git.dir", migrationConfig.getOutputVfsRepository().getAbsolutePath());
    }

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
    protected String determineJcrRepositoryRootDirectory() {
        File inputJcrRepository = migrationConfig.getInputJcrRepository();
        File repositoryXmlFile = new File(inputJcrRepository, "repository.xml");
        if (!repositoryXmlFile.exists()) {
            throw new IllegalStateException(
                    "The repositoryXmlFile (" + repositoryXmlFile.getAbsolutePath() + ") does not exist.\n"
                    + "Check your inputJcrRepository (" + inputJcrRepository + ").");
        }
        File repositoryDir = new File(inputJcrRepository, "repository");
        if (!repositoryDir.exists()) {
            // They are using a non-default repository.xml (for example with storage in a database)
            return inputJcrRepository.getAbsolutePath();
        }
        File unnestedVersionDir = new File(inputJcrRepository, "version");
        File nestedVersionDir = new File(repositoryDir, "version");
        if (unnestedVersionDir.exists()) {
            // repository.root.directory was specified
            return inputJcrRepository.getAbsolutePath();
        } else if (nestedVersionDir.exists()) {
            // repository.root.directory was not specified => HACK
            try {
                FileUtils.copyFile(repositoryXmlFile, new File(repositoryDir, "repository.xml"));
            } catch (IOException e) {
                throw new IllegalStateException("Cannot copy repositoryXmlFile (" + repositoryXmlFile + ").", e);
            }
            return inputJcrRepository.getAbsolutePath() + "/repository";
        } else {
            throw new IllegalStateException(
                    "The unnestedVersionDir (" + unnestedVersionDir.getAbsolutePath()
                    + ") and the nestedVersionDir (" + nestedVersionDir.getAbsolutePath() + ") does not exist.");
        }
    }

    protected void startContexts() {
        sessionDataStore = new HashMap<String, Object>();
        sessionContext.associate(sessionDataStore);
        sessionContext.activate();
        requestDataStore = new HashMap<String, Object>();
        requestContext.associate(requestDataStore);
        requestContext.activate();
    }

    protected void endContexts() {
        try {
            requestContext.invalidate();
            requestContext.deactivate();
        } finally {
            requestContext.dissociate(requestDataStore);
        }
        try {
            sessionContext.invalidate();
            sessionContext.deactivate();
        } finally {
            sessionContext.dissociate(sessionDataStore);
        }
    }

}
