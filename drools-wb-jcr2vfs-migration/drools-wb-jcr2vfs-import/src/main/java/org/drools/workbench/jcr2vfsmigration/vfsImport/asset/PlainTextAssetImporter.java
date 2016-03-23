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
package org.drools.workbench.jcr2vfsmigration.vfsImport.asset;

import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;

import org.drools.workbench.jcr2vfsmigration.util.MigrationPathManager;
import org.drools.workbench.jcr2vfsmigration.xml.model.Module;
import org.drools.workbench.jcr2vfsmigration.xml.model.asset.PlainTextAsset;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.java.nio.file.StandardCopyOption;

public class PlainTextAssetImporter implements AssetImporter<PlainTextAsset> {

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    protected MigrationPathManager migrationPathManager;

    public Path importAsset( Module xmlModule, PlainTextAsset xmlAsset, Path previousVersionPath ) {
        Path path = migrationPathManager.generatePathForAsset( xmlModule, xmlAsset, xmlAsset.getAssetType().toString() );
        final org.uberfire.java.nio.file.Path nioPath = Paths.convert( path );

        //The asset was renamed in this version. We move this asset first.
        if ( previousVersionPath != null && !previousVersionPath.equals( path ) ) {
            ioService.move( Paths.convert( previousVersionPath ), nioPath, StandardCopyOption.REPLACE_EXISTING );
        }

        // Content already transformed in the exporter
        String content = xmlAsset.getContent();

        ioService.write( nioPath,
                         content,
                         (Map<String, ?>) null,    // cast is for disambiguation
                         new CommentedOption( xmlAsset.getLastContributor(),
                             null,
                             xmlAsset.getCheckinComment(),
                             xmlAsset.getLastModified() )
        );
        return path;
    }
}
