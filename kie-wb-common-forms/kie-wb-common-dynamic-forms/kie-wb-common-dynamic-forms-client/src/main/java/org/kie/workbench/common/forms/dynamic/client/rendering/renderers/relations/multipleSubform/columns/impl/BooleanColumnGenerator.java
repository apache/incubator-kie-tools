/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.dynamic.client.rendering.renderers.relations.multipleSubform.columns.impl;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Any;

import com.google.gwt.user.cellview.client.Column;
import org.jboss.errai.databinding.client.HasProperties;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.relations.multipleSubform.columns.ColumnGenerator;
import org.uberfire.ext.widgets.table.client.CheckboxCellImpl;

@Dependent
@Any
public class BooleanColumnGenerator implements ColumnGenerator<Boolean> {

    @Override
    public String getType() {
        return Boolean.class.getName();
    }

    @Override
    public Column<HasProperties, Boolean> getColumn( final String property ) {
        final CheckboxCellImpl checkbox = new CheckboxCellImpl( true );

        final Column<HasProperties, Boolean> column = new Column<HasProperties, Boolean>( checkbox ) {
            @Override
            public Boolean getValue( HasProperties object ) {
                Object value = object.get( property );

                if ( value == null ) {
                    return Boolean.FALSE;
                }

                return Boolean.TRUE.equals( value );
            }
        };
        return column;
    }

}
