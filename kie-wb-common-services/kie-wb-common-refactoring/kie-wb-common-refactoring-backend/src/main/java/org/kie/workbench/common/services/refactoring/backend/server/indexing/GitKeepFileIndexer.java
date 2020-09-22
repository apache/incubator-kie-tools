/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.refactoring.backend.server.indexing;

import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;

import org.guvnor.common.services.project.model.Package;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.java.nio.file.Path;

@ApplicationScoped
public class GitKeepFileIndexer
        extends AbstractFileIndexer {

    private static final Logger logger = LoggerFactory.getLogger(GitKeepFileIndexer.class);
    private static final String KEEP_FILE = ".gitkeep";

    @Override
    protected IndexBuilder fillIndexBuilder(final Path path) throws Exception {
        // create indexbuilder
        final KieModule module = getModule(path);

        if (module == null) {
            logger.error("Unable to index {0}: module could not be resolved.", path.toUri().toString());
            return null;
        }

        final Package pkg = getPackage(path);
        if (pkg == null) {
            logger.error("Unable to index {0}: package could not be resolved.", path.toUri().toString());
            return null;
        }

        return new DefaultIndexBuilder(Paths.convert(path).getFileName(),
                                       module,
                                       pkg);
    }

    @Override
    public boolean supportsPath(final Path path) {
        return Objects.equals(path.getFileName().toString(), KEEP_FILE);
    }

    /*
     * Present in order to be overridden in tests
     */
    protected KieModule getModule(final Path path) {
        return moduleService.resolveModule(Paths.convert(path));
    }

    /*
     * Present in order to be overridden in tests
     */
    protected Package getPackage(final Path path) {
        return moduleService.resolvePackage(Paths.convert(path));
    }
}
