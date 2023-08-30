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


package org.kie.workbench.common.forms.processing.engine.handling.impl.test;

import org.jboss.errai.databinding.client.BindableProxy;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.kie.workbench.common.forms.processing.engine.handling.FieldChangeHandlerManager;
import org.kie.workbench.common.forms.processing.engine.handling.FormField;
import org.kie.workbench.common.forms.processing.engine.handling.FormValidator;
import org.kie.workbench.common.forms.processing.engine.handling.impl.FormHandlerImpl;
import org.kie.workbench.common.forms.processing.engine.handling.impl.model.User;
import org.kie.workbench.common.forms.processing.engine.handling.impl.model.UserProxy;

public class TestFormHandler extends FormHandlerImpl {

    protected DataBinder dataBinder;

    public TestFormHandler(FormValidator validator,
                           FieldChangeHandlerManager fieldChangeManager,
                           DataBinder dataBinder) {
        super(validator,
              fieldChangeManager);
        this.dataBinder = dataBinder;
    }

    @Override
    protected DataBinder getBinderForModel(Object model) {
        return dataBinder;
    }

    @Override
    public void notifyFieldChange(String fieldName, Object newValue) {
        super.notifyFieldChange(fieldName, newValue);
    }

    @Override
    public void processFieldChange(FormField formField, Object newValue) {
        super.processFieldChange(formField, newValue);
    }

    @Override
    public void setEnabledOnChangeValidations(boolean enabledOnChangeValidations) {
        super.setEnabledOnChangeValidations(enabledOnChangeValidations);
    }

    @Override
    protected Object readPropertyValue(BindableProxy proxy,
                                       String fieldBinding) {
        if (fieldBinding.indexOf(".") != -1) {
            // Nested property

            int separatorPosition = fieldBinding.indexOf(".");
            String nestedModelName = fieldBinding.substring(0,
                                                            separatorPosition);
            String property = fieldBinding.substring(separatorPosition + 1);
            Object nestedModel = proxy.get(nestedModelName);
            if (nestedModel == null) {
                return null;
            }

            return readPropertyValue(new UserProxy((User) nestedModel),
                                     property);
        }
        return proxy.get(fieldBinding);
    }
}
