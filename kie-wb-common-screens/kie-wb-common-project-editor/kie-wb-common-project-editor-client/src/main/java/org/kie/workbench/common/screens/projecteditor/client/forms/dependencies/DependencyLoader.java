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
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.services.shared.dependencies.DependencyService;
import org.kie.workbench.common.services.shared.dependencies.EnhancedDependencies;
import org.kie.workbench.common.services.shared.dependencies.NormalEnhancedDependency;

@Dependent
public class DependencyLoader {

    private final List<Dependency> updateQueue = new ArrayList<>();

    private final Caller<DependencyService>   dependencyService;
    private       EnhancedDependenciesManager manager;

    @Inject
    public DependencyLoader( final Caller<DependencyService> dependencyService ) {
        this.dependencyService = dependencyService;
    }

    public void init( final EnhancedDependenciesManager manager ) {
        this.manager = manager;
        this.updateQueue.clear();
    }

    public void load() {
        if ( !updateQueue.isEmpty() ) {
            loadFromServer();
        } else {
            returnDefault();
        }
    }

    private void returnDefault() {
        EnhancedDependencies enhancedDependencies = new EnhancedDependencies();
        for ( Dependency dependency : updateQueue ) {
            enhancedDependencies.add( new NormalEnhancedDependency( dependency,
                                                                    new HashSet<String>() ) );
        }

        updateQueue.clear();

        manager.onEnhancedDependenciesUpdated( enhancedDependencies );
    }

    private void loadFromServer() {

        dependencyService.call( new RemoteCallback<EnhancedDependencies>() {
                                    @Override
                                    public void callback( final EnhancedDependencies result ) {
                                        onLoadSuccess( result );
                                    }
                                },
                                new ErrorCallback<Object>() {
                                    @Override
                                    public boolean error( final Object o,
                                                          final Throwable throwable ) {


                                        returnDefault();
                                        return false;
                                    }
                                } ).loadEnhancedDependencies( updateQueue );
    }

    private void onLoadSuccess( final EnhancedDependencies result ) {
        updateQueue.clear();
        manager.onEnhancedDependenciesUpdated( result );
    }

    public void addToQueue( final Dependency dependency ) {
        updateQueue.add( dependency );
    }
}
