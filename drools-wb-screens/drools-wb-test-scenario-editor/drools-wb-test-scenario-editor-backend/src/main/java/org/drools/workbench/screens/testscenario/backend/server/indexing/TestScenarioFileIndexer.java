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
package org.drools.workbench.screens.testscenario.backend.server.indexing;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.drools.workbench.models.testscenarios.backend.util.ScenarioXMLPersistence;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.drools.workbench.screens.testscenario.type.TestScenarioResourceTypeDefinition;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.Package;
import org.kie.soup.project.datamodel.oracle.ModuleDataModelOracle;
import org.kie.workbench.common.services.datamodel.backend.server.service.DataModelService;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.AbstractFileIndexer;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.DefaultIndexBuilder;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.java.nio.file.Path;

@ApplicationScoped
public class TestScenarioFileIndexer extends AbstractFileIndexer {

    @Inject
    protected TestScenarioResourceTypeDefinition type;
    @Inject
    private DataModelService dataModelService;

    @Override
    public boolean supportsPath(final Path path) {
        return type.accept(Paths.convert(path));
    }

    @Override
    public DefaultIndexBuilder fillIndexBuilder(final Path path) throws Exception {
        final String content = ioService.readAllString(path);
        final Scenario model = ScenarioXMLPersistence.getInstance().unmarshal(content);

        final ModuleDataModelOracle dmo = getModuleDataModelOracle(path);
        final Module project = moduleService.resolveModule(Paths.convert(path));
        final Package pkg = moduleService.resolvePackage(Paths.convert(path));

        final DefaultIndexBuilder builder = new DefaultIndexBuilder(Paths.convert(path).getFileName(),
                                                                    project,
                                                                    pkg);
        final TestScenarioIndexVisitor visitor = new TestScenarioIndexVisitor(dmo,
                                                                              builder,
                                                                              model);
        visitor.visit();

        addReferencedResourcesToIndexBuilder(builder,
                                             visitor);

        return builder;
    }

    //Delegate resolution of package name to method to assist testing
    protected String getPackageName(final Path path) {
        return moduleService.resolvePackage(Paths.convert(path)).getPackageName();
    }

    //Delegate resolution of DMO to method to assist testing
    protected ModuleDataModelOracle getModuleDataModelOracle(final Path path) {
        return dataModelService.getModuleDataModel(Paths.convert(path));
    }
}
