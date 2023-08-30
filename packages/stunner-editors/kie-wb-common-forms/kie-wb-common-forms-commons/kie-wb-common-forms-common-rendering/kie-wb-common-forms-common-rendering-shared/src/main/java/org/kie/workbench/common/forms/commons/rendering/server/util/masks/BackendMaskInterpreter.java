/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.forms.commons.rendering.server.util.masks;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.kie.workbench.common.forms.commons.rendering.shared.util.masks.MaskInterpreter;
import org.kie.workbench.common.forms.commons.rendering.shared.util.masks.ModelInterpreter;

public class BackendMaskInterpreter<T> extends MaskInterpreter<T> {

    public BackendMaskInterpreter(String mask) {
        super(mask);
    }

    @Override
    protected ModelInterpreter<T> getModelInterpreter(T model) {
        Gson gson = new Gson();

        JsonObject jsonObject = gson.toJsonTree(model).getAsJsonObject();

        return propertyName -> {
            String value = "";
            JsonElement propertyValue = jsonObject.get(propertyName);
            if (propertyValue != null) {
                value = propertyValue.getAsString();
            }
            return value;
        };
    }
}
