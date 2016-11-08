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

package org.kie.workbench.common.forms.serialization.impl;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;;
import org.kie.workbench.common.forms.model.FormModel;
import org.kie.workbench.common.forms.serialization.FormDefinitionSerializer;

@Dependent
public class FormDefinitionSerializerImpl implements FormDefinitionSerializer {;

    private FieldSerializer fieldSerializer;

    private FormModelSerializer formModelSerializer;

    @Inject
    public FormDefinitionSerializerImpl( FieldSerializer fieldSerializer, FormModelSerializer formModelSerializer ) {
        this.fieldSerializer = fieldSerializer;
        this.formModelSerializer = formModelSerializer;
    }

    @Override
    public String serialize( FormDefinition form ) {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter( FormModel.class, formModelSerializer );
        builder.registerTypeAdapter( FieldDefinition.class, fieldSerializer );

        Gson gson = builder.create();

        return gson.toJson( form );
    }

    @Override
    public FormDefinition deserialize( String serializedForm ) {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter( FormModel.class, formModelSerializer );
        builder.registerTypeAdapter( FieldDefinition.class, fieldSerializer );

        Gson gson = builder.create();

        return gson.fromJson( serializedForm, FormDefinition.class );
    }
}
