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


package org.kie.workbench.common.forms.dynamic.service.shared.impl;

import java.util.HashMap;
import java.util.Map;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.forms.dynamic.service.shared.AbstractFormRenderingContext;
import org.kie.workbench.common.forms.dynamic.service.shared.impl.validation.DynamicModelConstraints;

/**
 * FormRenderinigContext for dynamic data
 */
@Portable
public class MapModelRenderingContext extends AbstractFormRenderingContext<Map<String, Object>> {

    public static final String FORM_ENGINE_OBJECT_IDX = "__FormEngine-ObjectIndex";
    public static final String FORM_ENGINE_EDITED_OBJECT = "__FormEngine-EditedObject";

    public Map<String, DynamicModelConstraints> modelValidations = new HashMap<>();

    public MapModelRenderingContext(@MapsTo("namespace") String namespace) {
        super(namespace);
    }

    @Override
    protected AbstractFormRenderingContext<Map<String, Object>> getNewInstance(String namespace) {
        MapModelRenderingContext copy = new MapModelRenderingContext(namespace);
        copy.setModelValidations(modelValidations);
        return copy;
    }

    public Map<String, DynamicModelConstraints> getModelConstraints() {
        return modelValidations;
    }

    public void setModelValidations(Map<String, DynamicModelConstraints> modelValidations) {
        this.modelValidations = modelValidations;
    }
}
