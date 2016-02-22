/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import com.google.gwt.cell.client.FieldUpdater;
import org.kie.workbench.common.screens.projecteditor.client.forms.dependencies.validation.DependencyValidator;
import org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources;
import org.kie.workbench.common.services.shared.dependencies.EnhancedDependency;
import org.kie.workbench.common.services.shared.whitelist.WhiteList;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;

public class WhiteListColumn
        extends com.google.gwt.user.cellview.client.Column<EnhancedDependency, String> {

    private DependencyGrid presenter;
    private WhiteList whiteList;


    public WhiteListColumn() {
        super( new WhiteListCell() );

        setFieldUpdater( new FieldUpdater<EnhancedDependency, String>() {
            @Override
            public void update( final int index,
                                final EnhancedDependency dependency,
                                final String value ) {
                final DependencyValidator dependencyValidator = new DependencyValidator( dependency.getDependency() );

                if ( dependencyValidator.validate() ) {
                    if ( WhiteListCell.TOGGLE.equals( value ) ) {
                        presenter.onTogglePackagesToWhiteList( dependency.getPackages() );
                    }
                } else {
                    showMessage( dependencyValidator.getMessage() );
                }
            }
        } );
    }

    protected void showMessage( final String message ) {
        ErrorPopup.showMessage( message );
    }

    @Override
    public String getValue( final EnhancedDependency enhancedDependency ) {
        final Set<String> packages = enhancedDependency.getPackages();
        if ( packages.isEmpty() ) {
            return ProjectEditorResources.CONSTANTS.PackagesNotIncluded();
        } else if ( whiteList.containsAll( packages ) ) {
            return ProjectEditorResources.CONSTANTS.AllPackagesIncluded();
        } else if ( whiteList.containsAny( packages ) ) {
            return ProjectEditorResources.CONSTANTS.SomePackagesIncluded();
        } else {
            return ProjectEditorResources.CONSTANTS.PackagesNotIncluded();
        }
    }

    public void init( final DependencyGrid presenter,
                      final WhiteList whiteList ) {
        this.presenter = presenter;
        this.whiteList = whiteList;
    }
}
