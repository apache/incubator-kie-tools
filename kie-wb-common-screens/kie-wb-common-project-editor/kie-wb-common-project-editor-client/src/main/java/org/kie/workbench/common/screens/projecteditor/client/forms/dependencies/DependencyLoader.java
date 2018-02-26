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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.Dependency;
import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.services.shared.dependencies.DependencyService;
import org.kie.workbench.common.services.shared.dependencies.EnhancedDependencies;
import org.kie.workbench.common.services.shared.dependencies.NormalEnhancedDependency;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.client.promise.Promises;

@Dependent
public class DependencyLoader {

    private final List<Dependency> updateQueue = new ArrayList<>();

    private final DependencyLoaderView view;
    private final Promises promises;
    private final Caller<DependencyService> dependencyService;
    private EnhancedDependenciesManager manager;

    @Inject
    public DependencyLoader(final DependencyLoaderView view,
                            final Promises promises,
                            final Caller<DependencyService> dependencyService) {
        this.view = view;
        this.promises = promises;
        this.dependencyService = dependencyService;
    }

    public void init(final EnhancedDependenciesManager manager) {
        this.manager = manager;
        this.updateQueue.clear();
    }

    public void load() {
        if (!updateQueue.isEmpty()) {
            loadFromServer();
        } else {
            returnDefault();
        }
    }

    private void returnDefault() {
        final EnhancedDependencies enhancedDependencies = new EnhancedDependencies();
        for (final Dependency dependency : updateQueue) {
            enhancedDependencies.add(new NormalEnhancedDependency(dependency,
                                                                  new HashSet<>()));
        }

        updateQueue.clear();

        manager.onEnhancedDependenciesUpdated(enhancedDependencies);
    }

    private void loadFromServer() {

        view.showBusyIndicator(CommonConstants.INSTANCE.Loading());

        promises.promisify(dependencyService, s -> {
            return s.loadEnhancedDependencies(updateQueue);
        }).then(result -> {
            onLoadSuccess(result);
            view.hideBusyIndicator();
            return promises.resolve();
        }).catch_(i -> {
            returnDefault();
            view.hideBusyIndicator();
            return promises.resolve();
        });
    }

    private void onLoadSuccess(final EnhancedDependencies result) {
        updateQueue.clear();
        manager.onEnhancedDependenciesUpdated(result);
    }

    public void addToQueue(final Dependency dependency) {
        updateQueue.add(dependency);
    }
}
