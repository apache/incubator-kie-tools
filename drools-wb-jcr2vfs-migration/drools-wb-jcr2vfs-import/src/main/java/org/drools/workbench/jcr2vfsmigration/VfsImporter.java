/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

import org.drools.workbench.jcr2vfsmigration.common.FileManager;
import org.drools.workbench.jcr2vfsmigration.config.VfsImportConfig;
import org.drools.workbench.jcr2vfsmigration.util.MigrationPathManager;
import org.drools.workbench.jcr2vfsmigration.vfsImport.ModuleAssetImporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;

@ApplicationScoped
public class VfsImporter {
    private static final Logger logger = LoggerFactory.getLogger(VfsImporter.class);

    @Inject
    protected VfsImportConfig vfsImportConfig;

    @Inject
    private FileManager fileManager;

    @Inject
    protected MigrationPathManager migrationPathManager;

    @Inject
    protected ModuleAssetImporter moduleAssetImporter;

    public boolean parseArgs( String[] args ) {
        boolean ok = vfsImportConfig.parseArgs( args );
        if ( ok ) fileManager.setExportTempDir( vfsImportConfig.getImportTempDir() );
        return ok;
    }

    public void importAll() {
        logger.info("VFS import started. Reading from import directory {}.", vfsImportConfig.getImportTempDir().getAbsolutePath() );


        String vfsRepoCanonicalPath = getVfsRepoCanonicalPath(vfsImportConfig);

        migrationPathManager.setRepoName(vfsImportConfig.getOutputRepoName(), vfsRepoCanonicalPath);

        // TO-DO-LIST:
        //   - Migrate categories
        //   - Migrate state
        //   - Migrate Guvnor package based permissions: admin/package.admin/package.developer/package.readonly
        //     (and don't forget to migrate category based permission, ie, analyst/analyst.readonly)

        moduleAssetImporter.importAll();
    }

    private String getVfsRepoCanonicalPath(VfsImportConfig config) {
        try {
            return config.getOutputVfsRepository().getCanonicalPath();
        } catch (IOException e) {
            throw new RuntimeException("Can't determine canonical path for output VFS repository!" , e);
        }
    }
}
