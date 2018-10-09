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

package org.kie.workbench.common.screens.projecteditor.client.forms.dependencies;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.Dependencies;
import org.guvnor.common.services.project.model.Dependency;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.kie.workbench.common.screens.projecteditor.client.forms.dependencies.validation.DependencyValidator;
import org.kie.workbench.common.services.shared.dependencies.EnhancedDependencies;
import org.kie.workbench.common.services.shared.dependencies.EnhancedDependency;
import org.kie.workbench.common.services.shared.dependencies.NormalEnhancedDependency;
import org.uberfire.client.callbacks.Callback;

/**
 * By default the dependencies in POM do not include the information about the packages each Dependency contains.
 * Neither do they include the transitive dependencies.
 * We can use this class to enhance the list and fill in the packages.
 */
@Dependent
public class EnhancedDependenciesManager {

    private final EnhancedDependencies enhancedDependencies = new EnhancedDependencies();
    private final DependencyLoader loader;

    private Callback<EnhancedDependencies> callback;
    private Dependencies originalSetOfDependencies;

    @Inject
    public EnhancedDependenciesManager(final DependencyLoader loader) {
        this.loader = loader;
    }

    public void init(final POM pom,
                     final Callback<EnhancedDependencies> callback) {

        loader.init(this);

        this.originalSetOfDependencies = pom.getDependencies();
        this.enhancedDependencies.clear();

        addToQueue(originalSetOfDependencies.getCompileScopedGavs());

        this.callback = callback;
    }

    private void addToQueue(final Collection<GAV> originalSetOfDependencies) {
        for (final GAV gav : originalSetOfDependencies) {
            loader.addToQueue(getDependency(gav));
        }
    }

    private Dependency getDependency(final GAV gav) {
        for (final Dependency originalSetOfDependency : originalSetOfDependencies) {
            if (originalSetOfDependency.isGAVEqual(gav)) {
                return originalSetOfDependency;
            }
        }

        return null;
    }

    public void update() {
        loader.load();
    }

    void onEnhancedDependenciesUpdated(final EnhancedDependencies loadedEnhancedDependencies) {
        for (final EnhancedDependency enhancedDependency : loadedEnhancedDependencies) {

            updateOriginal(enhancedDependency);

            updateEnhanced(enhancedDependency);
        }

        callback.callback(this.enhancedDependencies);
    }

    private void updateEnhanced(final EnhancedDependency enhancedDependency) {
        if (this.enhancedDependencies.contains(enhancedDependency)) {
            this.enhancedDependencies.update(enhancedDependency);
        } else {
            this.enhancedDependencies.add(enhancedDependency);
        }
    }

    private void updateOriginal(final EnhancedDependency enhancedDependency) {
        if (enhancedDependency instanceof NormalEnhancedDependency) {
            updateWithOriginalDependency((NormalEnhancedDependency) enhancedDependency);
        }
    }

    private void updateWithOriginalDependency(final NormalEnhancedDependency enhancedDependency) {
        final Dependency originalDependency = originalSetOfDependencies.get(enhancedDependency.getDependency());
        enhancedDependency.setDependency(originalDependency);
    }

    public void delete(final EnhancedDependency enhancedDependency) {
        enhancedDependencies.remove(enhancedDependency);
        originalSetOfDependencies.remove(enhancedDependency.getDependency());
        update();
    }

    public void addNew(final Dependency dependency) {
        originalSetOfDependencies.add(dependency);
        loader.addToQueue(dependency);
        update();
    }

    public void validateDependency() {
        enhancedDependencies.asList().forEach(e -> {
            DependencyValidator validator = new DependencyValidator(e.getDependency());
            if (!validator.validate()) {
                delete(e);
            }
        });
    }
}
