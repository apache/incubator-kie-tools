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

package org.kie.workbench.common.forms.service.impl;

import java.util.List;

import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormModel;
import org.kie.workbench.common.forms.service.FormModelHandler;
import org.uberfire.backend.vfs.Path;

public abstract class AbstractFormModelHandler<F extends FormModel> implements FormModelHandler<F> {

    protected F formModel;
    protected Path path;

    @Override
    public void init( F formModel, Path path ) {
        this.formModel = formModel;
        this.path = path;
        initialize();
    }

    @Override
    public List<FieldDefinition> getAllFormModelFields() {
        checkInitialized();
        return doGenerateModelFields();
    }

    @Override
    public FieldDefinition createFieldDefinition( String fieldName ) {
        checkInitialized();
        return doCreateFieldDefinition( fieldName );
    }

    protected abstract void initialize();

    protected abstract List<FieldDefinition> doGenerateModelFields();

    protected abstract FieldDefinition doCreateFieldDefinition( String fieldName );

    public void checkInitialized() {
        if ( path == null || formModel == null ) {
            throw new IllegalArgumentException( "Handler isn't initialized" );
        }
    }
}
