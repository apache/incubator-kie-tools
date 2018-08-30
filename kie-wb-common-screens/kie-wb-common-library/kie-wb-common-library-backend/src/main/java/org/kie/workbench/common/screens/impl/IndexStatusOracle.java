/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.impl;

import static java.lang.String.format;

import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.kie.workbench.common.screens.library.api.index.Constants;
import org.slf4j.Logger;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.ext.metadata.MetadataConfig;
import org.uberfire.ext.metadata.engine.MetaIndexEngine;
import org.uberfire.ext.metadata.io.KObjectUtil;
import org.uberfire.ext.metadata.model.KCluster;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;

@Dependent
public class IndexStatusOracle {

    private final Logger logger;
    private final MetaIndexEngine indexEngine;

    // Proxying
    public IndexStatusOracle() {
        this(null, null);
    }

    @Inject
    public IndexStatusOracle(MetadataConfig config, Logger logger) {
        this.logger = logger;
        this.indexEngine = config.getIndexEngine();
    }

    public boolean isIndexed(WorkspaceProject project) {
        Optional<KCluster> clusterOf = kClusterOf(project);

        return clusterOf.map(cluster -> indexEngine.isIndexReady(cluster, Constants.INDEXER_ID))
                        .orElse(false);
    }

    private Optional<KCluster> kClusterOf(WorkspaceProject project) {
        try {
            Path rootPath = Paths.convert(project.getRootPath());
            FileSystem fileSystem = rootPath.getFileSystem();
            KCluster cluster = KObjectUtil.toKCluster(rootPath);

            return Optional.of(cluster);
        } catch (Throwable t) {
            if (logger.isDebugEnabled()) {
                logger.debug(format("Unable to lookup KCluster for project: %s", project), t);
            }

            return Optional.empty();
        }
    }

}
