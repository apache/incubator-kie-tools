/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.screens.enums.backend.server.indexing;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.drools.workbench.screens.enums.type.EnumResourceTypeDefinition;
import org.kie.soup.project.datamodel.commons.util.MVELEvaluator;
import org.kie.soup.project.datamodel.oracle.ModuleDataModelOracle;
import org.kie.workbench.common.services.datamodel.backend.server.builder.util.DataEnumLoader;
import org.kie.workbench.common.services.datamodel.backend.server.service.DataModelService;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.AbstractFileIndexer;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.DefaultIndexBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.java.nio.file.Path;

@ApplicationScoped
public class EnumFileIndexer extends AbstractFileIndexer {

    private static final Logger logger = LoggerFactory.getLogger(EnumFileIndexer.class);
    @Inject
    protected EnumResourceTypeDefinition type;
    @Inject
    private DataModelService dataModelService;

    private MVELEvaluator mvelEvaluator;

    @Inject
    public EnumFileIndexer(MVELEvaluator mvelEvaluator) {
        this.mvelEvaluator = mvelEvaluator;
    }

    @Override
    public boolean supportsPath(final Path path) {
        return type.accept(Paths.convert(path));
    }

    @Override
    public DefaultIndexBuilder fillIndexBuilder(final Path path) throws Exception {
        final String enumDefinition = ioService.readAllString(path);
        final DataEnumLoader enumLoader = new DataEnumLoader(enumDefinition,
                                                             mvelEvaluator);
        if (enumLoader.hasErrors()) {
            logger.info("Unable to index '" + path.toUri().toString() + "'. Related errors follow:");
            for (String e : enumLoader.getErrors()) {
                logger.info(e);
            }
        }

        final ModuleDataModelOracle dmo = getModuleDataModelOracle(path);

        final DefaultIndexBuilder builder = getIndexBuilder(path);
        if (builder == null) {
            return null;
        }

        final EnumIndexVisitor visitor = new EnumIndexVisitor(dmo,
                                                              path,
                                                              enumLoader);
        visitor.visit();
        addReferencedResourcesToIndexBuilder(builder,
                                             visitor);

        return builder;
    }

    //Delegate resolution of DMO to method to assist testing
    protected ModuleDataModelOracle getModuleDataModelOracle(final Path path) {
        return dataModelService.getModuleDataModel(Paths.convert(path));
    }
}
