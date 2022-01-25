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

package org.drools.workbench.screens.scenariosimulation.type;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import org.guvnor.common.services.project.categories.Decision;
import org.uberfire.backend.vfs.Path;
import org.uberfire.workbench.annotations.VisibleAsset;
import org.uberfire.workbench.category.Category;
import org.uberfire.workbench.type.ResourceTypeDefinition;

import static org.drools.scenariosimulation.api.utils.ScenarioSimulationSharedUtils.FILE_EXTENSION;

@Default
@VisibleAsset
@ApplicationScoped
public class ScenarioSimulationResourceTypeDefinition implements ResourceTypeDefinition {

    private Category category;

    public ScenarioSimulationResourceTypeDefinition() {
    }

    @Inject
    public ScenarioSimulationResourceTypeDefinition(final Decision category) {
        this.category = category;
    }

    @Override
    public Category getCategory() {
        return this.category;
    }

    @Override
    public String getShortName() {
        return FILE_EXTENSION;
    }

    @Override
    public String getDescription() {
        return "Test Scenarios";
    }

    @Override
    public String getPrefix() {
        return "";
    }

    @Override
    public String getSuffix() {
        return FILE_EXTENSION;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public String getSimpleWildcardPattern() {
        return "*." + getSuffix();
    }

    @Override
    public boolean accept(final Path path) {
        return path.getFileName().endsWith("." + getSuffix());
    }
}
