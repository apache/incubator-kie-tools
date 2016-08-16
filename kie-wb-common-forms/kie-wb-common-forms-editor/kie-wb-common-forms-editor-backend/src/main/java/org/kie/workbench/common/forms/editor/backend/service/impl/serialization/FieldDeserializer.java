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
package org.kie.workbench.common.forms.editor.backend.service.impl.serialization;

import java.lang.reflect.Type;
import javax.enterprise.context.Dependent;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.apache.commons.lang3.StringUtils;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Dependent
public class FieldDeserializer implements JsonDeserializer<FieldDefinition> {
    private Logger log = LoggerFactory.getLogger( FieldDeserializer.class );

    @Override
    public FieldDefinition deserialize( JsonElement json, Type typeOfT, JsonDeserializationContext context ) throws JsonParseException {

        JsonObject jsonField =  json.getAsJsonObject();

        JsonElement jsonClassName = jsonField.get( "serializedFieldClassName" );

        if ( jsonClassName != null && !StringUtils.isEmpty( jsonClassName.getAsString() ) ) {
            try {
                return context.deserialize( json, Class.forName( jsonClassName.getAsString() ) );
            } catch ( Exception ex ) {
                log.error( "Error deserializing field", ex );
            }
        }

        return null;
    }
}
