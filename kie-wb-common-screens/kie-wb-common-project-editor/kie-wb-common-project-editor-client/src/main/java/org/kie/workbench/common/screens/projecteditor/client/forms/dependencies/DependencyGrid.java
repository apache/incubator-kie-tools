/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.project.model.Dependency;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.projecteditor.client.forms.GAVSelectionHandler;
import org.kie.workbench.common.services.shared.dependencies.DependencyService;
import org.kie.workbench.common.services.shared.whitelist.WhiteList;

@Dependent
public class DependencyGrid
        implements IsWidget {

    private DependencyGridView        view;
    private DependencySelectorPopup   dependencySelectorPopup;
    private Caller<DependencyService> dependencyService;

    private POM pom;
    private WhiteList whiteList;

    public DependencyGrid() {
    }

    @Inject
    public DependencyGrid( final DependencySelectorPopup dependencySelectorPopup,
                           final DependencyGridView view,
                           final Caller<DependencyService> dependencyService ) {
        this.dependencySelectorPopup = dependencySelectorPopup;
        this.dependencyService = dependencyService;

        dependencySelectorPopup.addSelectionHandler( new GAVSelectionHandler() {
            @Override
            public void onSelection( GAV gav ) {
                final Dependency dependency = new Dependency( gav );
                dependency.setScope( "compile" );
                pom.getDependencies().add( dependency );
                show();
            }
        } );

        this.view = view;
        view.setPresenter( this );
    }

    public void setDependencies( final POM pom,
                                 final WhiteList whiteList ) {
        this.pom = pom;
        this.whiteList = whiteList;
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public void onAddDependency() {
        pom.getDependencies().add( new Dependency() );
        show();
    }

    public void onAddDependencyFromRepository() {
        dependencySelectorPopup.show();
    }

    public void onRemoveDependency( final Dependency dependency ) {
        pom.getDependencies().remove( dependency );
        show();
    }

    public void setReadOnly() {
        view.setReadOnly();
    }

    public void show() {

        view.setWhiteList(whiteList);

        dependencyService.call( getLoadDependenciesSuccessfulRemoteCallback(),
                                getLoadDependenciesErrorCallback() ).loadDependencies( pom.getDependencies().getGavs( "compile" ) );
    }

    private RemoteCallback<Collection<Dependency>> getLoadDependenciesSuccessfulRemoteCallback() {
        return new RemoteCallback<Collection<Dependency>>() {
            @Override
            public void callback( final Collection<Dependency> result ) {


                final ArrayList<Dependency> allDependencies = new ArrayList<Dependency>();
                allDependencies.addAll( makeTransitiveDependencies( result ) );
                allDependencies.addAll( pom.getDependencies() );

                dependencyService.call( new RemoteCallback<List<Dependency>>() {
                    @Override
                    public void callback( final List<Dependency> updatedDependencies ) {
                        view.show( updatedDependencies );
                    }
                } ).loadDependenciesWithPackageNames( allDependencies );

            }
        };
    }

    private ErrorCallback<Object> getLoadDependenciesErrorCallback() {
        return new ErrorCallback<Object>() {
            @Override
            public boolean error( final Object o,
                                  final Throwable throwable ) {
                view.show( pom.getDependencies() );
                return false;
            }
        };
    }

    private Collection<Dependency> makeTransitiveDependencies( final Collection<Dependency> dependencies ) {
        final ArrayList<Dependency> result = new ArrayList<Dependency>();

        for ( Dependency dependency : dependencies ) {
            if ( !pom.getDependencies().containsDependency( dependency ) ) {
                dependency.setScope( "transitive" );
                result.add( dependency );
            }
        }

        return result;
    }

    public void onTogglePackagesToWhiteList( final Set<String> packages ) {

        if ( whiteList.containsAll( packages ) ) {
            whiteList.removeAll( packages );
        } else {
            whiteList.addAll( packages );
        }

        view.redraw();
    }
}
