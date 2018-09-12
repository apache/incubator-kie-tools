/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.dtablexls.backend.server.indexing;

import javax.enterprise.context.ApplicationScoped;

import org.drools.workbench.screens.dtablexls.type.DecisionTableXLSXResourceTypeDefinition;
import org.kie.soup.project.datamodel.commons.oracle.ModuleDataModelOracleImpl;
import org.kie.soup.project.datamodel.oracle.ModuleDataModelOracle;
import org.kie.workbench.common.services.refactoring.backend.server.TestIndexer;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;

/**
 * Test indexer
 */
@ApplicationScoped
public class TestDecisionTableXLSXFileIndexer extends DecisionTableXLSXFileIndexer implements TestIndexer<DecisionTableXLSXResourceTypeDefinition> {

    @Override
    public void setIOService(final IOService ioService) {
        this.ioService = ioService;
    }

    @Override
    public void setModuleService(final KieModuleService moduleService) {
        this.moduleService = moduleService;
    }

    @Override
    public void setResourceTypeDefinition(final DecisionTableXLSXResourceTypeDefinition type) {
        this.type = type;
    }

    @Override
    protected ModuleDataModelOracle getModuleDataModelOracle(final Path path) {
        return new ModuleDataModelOracleImpl();
    }
}
