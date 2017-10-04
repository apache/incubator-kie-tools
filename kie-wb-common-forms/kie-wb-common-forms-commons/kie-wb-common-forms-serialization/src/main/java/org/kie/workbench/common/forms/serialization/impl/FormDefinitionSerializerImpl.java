/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.FormModel;
import org.kie.workbench.common.forms.model.MetaDataEntry;
import org.kie.workbench.common.forms.model.ModelMetaData;
import org.kie.workbench.common.forms.model.ModelProperty;
import org.kie.workbench.common.forms.model.TypeInfo;
import org.kie.workbench.common.forms.model.impl.ModelPropertyImpl;
import org.kie.workbench.common.forms.model.impl.TypeInfoImpl;
import org.kie.workbench.common.forms.model.impl.meta.ModelMetaDataImpl;
import org.kie.workbench.common.forms.serialization.FormDefinitionSerializer;
import org.kie.workbench.common.forms.service.shared.meta.processing.MetaDataEntryManager;

@Dependent
public class FormDefinitionSerializerImpl implements FormDefinitionSerializer {

    private FieldSerializer fieldSerializer;

    private FormModelSerializer formModelSerializer;

    private MetaDataEntryManager metaDataEntryManager;

    @Inject
    public FormDefinitionSerializerImpl(FieldSerializer fieldSerializer,
                                        FormModelSerializer formModelSerializer,
                                        MetaDataEntryManager metaDataEntryManager) {
        this.fieldSerializer = fieldSerializer;
        this.formModelSerializer = formModelSerializer;
        this.metaDataEntryManager = metaDataEntryManager;
    }

    @Override
    public String serialize(FormDefinition form) {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(FormModel.class,
                                    formModelSerializer);
        builder.registerTypeAdapter(FieldDefinition.class,
                                    fieldSerializer);

        Gson gson = builder.create();

        return gson.toJson(form);
    }

    @Override
    public FormDefinition deserialize(String serializedForm) {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(FormModel.class,
                                    formModelSerializer);
        builder.registerTypeAdapter(FieldDefinition.class,
                                    fieldSerializer);

        builder.registerTypeAdapter(ModelProperty.class,
                                    (JsonDeserializer<ModelProperty>) (json, typeOfT, context) -> context.deserialize(json,
                                                                                                                      ModelPropertyImpl.class));
        builder.registerTypeAdapter(TypeInfo.class,
                                    (JsonDeserializer<TypeInfo>) (json, typeOfT, context) -> context.deserialize(json,
                                                                                                                 TypeInfoImpl.class));

        builder.registerTypeAdapter(ModelMetaData.class,
                                    (JsonDeserializer<ModelMetaData>) (json, typeOfT, context) -> context.deserialize(json,
                                                                                                                 ModelMetaDataImpl.class));

        builder.registerTypeAdapter(MetaDataEntry.class,
                                    (JsonDeserializer<MetaDataEntry>) (json, typeOfT, context) -> {
                                        JsonObject jsonField = json.getAsJsonObject();
                                        JsonElement jsonName = jsonField.get("name");
                                        return context.deserialize(json,
                                                                   metaDataEntryManager.getMetaDataEntryClass(jsonName.getAsString()));
                                    });

        Gson gson = builder.create();

        return gson.fromJson(serializedForm,
                             FormDefinition.class);
    }
}
