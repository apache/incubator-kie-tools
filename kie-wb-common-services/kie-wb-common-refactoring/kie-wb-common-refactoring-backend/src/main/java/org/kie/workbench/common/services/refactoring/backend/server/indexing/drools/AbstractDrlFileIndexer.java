/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.refactoring.backend.server.indexing.drools;

import java.util.List;

import org.drools.compiler.compiler.DrlParser;
import org.drools.compiler.compiler.DroolsError;
import org.drools.compiler.lang.descr.PackageDescr;
import org.kie.api.io.ResourceType;
import org.kie.soup.project.datamodel.oracle.ModuleDataModelOracle;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.AbstractFileIndexer;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.DefaultIndexBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.ErrorMessageUtilities;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.IndexBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.PackageDescrIndexVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.ext.metadata.engine.Indexer;
import org.uberfire.java.nio.file.Path;

/**
 * All Drools-related {@link Indexer} implemenations should implement this class in order to avoid duplicate code.
 */
public abstract class AbstractDrlFileIndexer extends AbstractFileIndexer {

    private static final Logger logger = LoggerFactory.getLogger(AbstractDrlFileIndexer.class);

    /**
     * All Drools-related {@link Indexer} implementations end up extracting the DRL from the related Rule representation
     * (see {@link ResourceType}).
     * </p>
     * The following method then parses the DRL and returns all relevant reference information.
     * @param path The {@link Path} of the asset/resource, necessary for extracting reference information.
     * @param drl A {@link String} representation of the DRL.
     * @return The {@link DefaultIndexBuilder}
     * @throws Exception
     */
    public IndexBuilder fillDrlIndexBuilder(final Path path,
                                                   final String drl) throws Exception {

        final DrlParser drlParser = new DrlParser();
        final PackageDescr packageDescr = drlParser.parse(true,
                                                          drl);

        if (drlParser.hasErrors()) {
            final List<DroolsError> errors = drlParser.getErrors();
            logger.warn(ErrorMessageUtilities.makeErrorMessage(path,
                                                               errors.toArray(new DroolsError[errors.size()])));
            return null;
        }
        if (packageDescr == null) {
            logger.warn(ErrorMessageUtilities.makeErrorMessage(path));
            return null;
        }

        final ModuleDataModelOracle dmo = getModuleDataModelOracle(path);

        // responsible for basic index info: project name, branch, etc
        final DefaultIndexBuilder builder = getIndexBuilder(path);
        if (builder == null) {
            return null;
        }
        builder.setPackageName(packageDescr.getName());

        // Retrieves info from the parsed syntac tree (PackageDescr)
        final PackageDescrIndexVisitor visitor = new PackageDescrIndexVisitor(dmo,
                                                                              builder,
                                                                              packageDescr);
        visitor.visit();
        addReferencedResourcesToIndexBuilder(builder,
                                             visitor);

        return builder;
    }

    /**
     * Delegate resolution of package name to method to assist testing
     * @param path The {@link Path} of the file being indexed
     * @return The package name, as a {@link String}
     */
    protected String getPackageName(final Path path) {
        return moduleService.resolvePackage(Paths.convert(path)).getPackageName();
    }

    /**
     * Delegate resolution of DMO to method to assist testing
     * @param path The {@link Path} of the file being indexed
     * @return The all-seeing, all-knowing {@link ModuleDataModelOracle}
     */
    protected abstract ModuleDataModelOracle getModuleDataModelOracle(final Path path);
}
