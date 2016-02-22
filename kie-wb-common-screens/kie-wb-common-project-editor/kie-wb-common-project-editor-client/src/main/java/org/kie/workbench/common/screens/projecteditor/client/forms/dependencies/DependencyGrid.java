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

import java.util.Set;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.project.model.Dependency;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.kie.workbench.common.screens.projecteditor.client.forms.GAVSelectionHandler;
import org.kie.workbench.common.services.shared.dependencies.EnhancedDependencies;
import org.kie.workbench.common.services.shared.dependencies.EnhancedDependency;
import org.kie.workbench.common.services.shared.whitelist.WhiteList;
import org.uberfire.client.callbacks.Callback;

@Dependent
public class DependencyGrid
        implements IsWidget {

    private final DependencyGridView          view;
    private final DependencySelectorPopup     dependencySelectorPopup;
    private final NewDependencyPopup          newDependencyPopup;
    private final EnhancedDependenciesManager enhancedDependenciesManager;

    private WhiteList whiteList;

    @Inject
    public DependencyGrid( final DependencySelectorPopup dependencySelectorPopup,
                           final NewDependencyPopup newDependencyPopup,
                           final EnhancedDependenciesManager enhancedDependenciesManager,
                           final DependencyGridView view ) {
        this.dependencySelectorPopup = dependencySelectorPopup;
        this.newDependencyPopup = newDependencyPopup;
        this.enhancedDependenciesManager = enhancedDependenciesManager;

        dependencySelectorPopup.addSelectionHandler( new GAVSelectionHandler() {
            @Override
            public void onSelection( GAV gav ) {
                onAddDependencyFromRepository( gav );
            }
        } );

        this.view = view;
        view.setPresenter( this );
    }

    private void onAddDependencyFromRepository( final GAV gav ) {
        final Dependency dependency = new Dependency( gav );
        dependency.setScope( "compile" );

        enhancedDependenciesManager.addNew( dependency );
    }

    public void setDependencies( final POM pom,
                                 final WhiteList whiteList ) {
        this.whiteList = whiteList;

        enhancedDependenciesManager.init( pom,
                                          new Callback<EnhancedDependencies>() {

                                              @Override
                                              public void callback( final EnhancedDependencies enhancedDependencies ) {
                                                  view.hideBusyIndicator();
                                                  view.show( enhancedDependencies );
                                              }
                                          } );

        view.setWhiteList( whiteList );
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public void onAddDependency() {
        this.newDependencyPopup.show( new Callback<Dependency>() {
            @Override
            public void callback( final Dependency result ) {
                enhancedDependenciesManager.addNew( result );
            }
        } );

    }

    public void onAddDependencyFromRepository() {
        dependencySelectorPopup.show();
    }

    public void onRemoveDependency( final EnhancedDependency dependency ) {
        enhancedDependenciesManager.delete( dependency );
    }

    public void setReadOnly() {
        view.setReadOnly();
    }

    public void show() {
        enhancedDependenciesManager.update();
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
