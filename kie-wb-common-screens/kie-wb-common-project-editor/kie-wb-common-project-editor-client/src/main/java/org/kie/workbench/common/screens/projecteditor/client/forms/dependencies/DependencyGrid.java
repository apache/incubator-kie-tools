/*
 * Copyright 2013 JBoss Inc
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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.project.model.Dependency;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.kie.workbench.common.screens.projecteditor.client.forms.GAVSelectionHandler;

@Dependent
public class DependencyGrid
        implements IsWidget {

    private DependencyGridView view;
    private DependencySelectorPopup dependencySelectorPopup;

    private POM pom;

    public DependencyGrid() {
    }

    @Inject
    public DependencyGrid( final DependencySelectorPopup dependencySelectorPopup,
                           final DependencyGridView view ) {
        this.dependencySelectorPopup = dependencySelectorPopup;

        dependencySelectorPopup.addSelectionHandler( new GAVSelectionHandler() {
            @Override
            public void onSelection( GAV gav ) {
                pom.getDependencies().add( new Dependency( gav ) );
                show();
            }
        } );

        this.view = view;
        view.setPresenter( this );
    }

    public void setDependencies( final POM pom ) {
        this.pom = pom;
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public void onAddDependencyButton() {
        pom.getDependencies().add( new Dependency() );
        show();
    }

    public void onAddDependencyFromRepositoryButton() {
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
        view.show( pom.getDependencies() );

    }

}
