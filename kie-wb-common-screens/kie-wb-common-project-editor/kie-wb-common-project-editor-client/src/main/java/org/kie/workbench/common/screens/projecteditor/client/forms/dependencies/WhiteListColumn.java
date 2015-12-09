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

import com.google.gwt.cell.client.FieldUpdater;
import org.guvnor.common.services.project.model.Dependency;
import org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources;
import org.kie.workbench.common.services.shared.whitelist.WhiteList;

public class WhiteListColumn
        extends com.google.gwt.user.cellview.client.Column<Dependency, String> {

    private DependencyGrid presenter;
    private WhiteList whiteList;

    public WhiteListColumn() {
        super( new WhiteListCell() );

        setFieldUpdater( new FieldUpdater<Dependency, String>() {
            @Override
            public void update( final int i,
                                final Dependency dependency,
                                final String value ) {
                presenter.onTogglePackagesToWhiteList( dependency.getPackages() );
            }
        } );
    }

    @Override
    public String getValue( final Dependency dependency ) {


        if ( whiteList.containsAll( dependency.getPackages() ) ) {
            return ProjectEditorResources.CONSTANTS.AllPackagesIncluded();
        } else if ( whiteList.containsAny( dependency.getPackages() ) ) {
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
