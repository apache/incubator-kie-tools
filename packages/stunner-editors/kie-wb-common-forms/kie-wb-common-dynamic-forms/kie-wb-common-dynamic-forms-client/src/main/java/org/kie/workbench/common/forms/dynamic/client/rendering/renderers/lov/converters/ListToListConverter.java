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


package org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.converters;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.databinding.client.api.Converter;

public class ListToListConverter implements Converter<List, List> {

    @Override
    public Class<List> getModelType() {
        return List.class;
    }

    @Override
    public Class<List> getComponentType() {
        return List.class;
    }

    @Override
    public List toModelValue(List componentValue) {
        return componentValue;
    }

    @Override
    public List toWidgetValue(List modelValue) {
        if(modelValue == null) {
            return new ArrayList();
        }
        return modelValue;
    }
}
