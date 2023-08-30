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


package org.kie.workbench.common.forms.common.rendering.client.util.masks;

import org.jboss.errai.databinding.client.HasProperties;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.kie.workbench.common.forms.commons.rendering.shared.util.masks.MaskInterpreter;
import org.kie.workbench.common.forms.commons.rendering.shared.util.masks.ModelInterpreter;

public class ClientMaskInterpreter<T> extends MaskInterpreter<T> {

    public ClientMaskInterpreter(String mask) {
        super(mask);
    }

    @Override
    protected ModelInterpreter<T> getModelInterpreter(T model) {

        HasProperties hasProperties;

        if (model instanceof HasProperties) {
            hasProperties = (HasProperties) model;
        } else {
            hasProperties = (HasProperties) DataBinder.forModel(model).getModel();
        }

        return propertyName -> {
            Object result = hasProperties.get(propertyName);

            if (result == null) {
                return "";
            }

            return result.toString();
        };
    }
}
