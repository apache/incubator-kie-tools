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

package org.kie.workbench.common.forms.service.impl.fieldProviders;

import javax.enterprise.context.Dependent;

import org.kie.workbench.common.forms.model.FieldTypeInfo;
import org.kie.workbench.common.forms.model.impl.basic.image.PictureFieldDefinition;

@Dependent
public class PictureFieldProvider extends BasicTypeFieldProvider<PictureFieldDefinition> {

    @Override
    public String getProviderCode() {
        return PictureFieldDefinition.CODE;
    }

    @Override
    protected void doRegisterFields() {
        registerPropertyType( String.class );
    }

    @Override
    public int getPriority() {
        return 8;
    }

    @Override
    public PictureFieldDefinition createFieldByType( FieldTypeInfo typeInfo ) {
        return new PictureFieldDefinition();
    }

    @Override
    public PictureFieldDefinition getDefaultField() {
        return new PictureFieldDefinition();
    }

}
