/*
 * Copyright 2015 JBoss Inc
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
import com.google.gwt.user.cellview.client.Column;
import org.guvnor.common.services.project.model.Dependency;

public class ScopeColumn
        extends Column<Dependency, String> {

    public ScopeColumn() {
        super( new ScopePopupCell() );

        setFieldUpdater( new FieldUpdater<Dependency, String>() {
            @Override
            public void update( final int index,
                                final Dependency dependency,
                                final String value ) {
                dependency.setScope( value );
            }
        } );
    }

    @Override
    public String getValue( final Dependency dependency ) {
        if ( dependency.getScope() == null ) {
            return "compile";
        } else {
            return dependency.getScope();
        }
    }
}
