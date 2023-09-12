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


package org.kie.workbench.common.forms.processing.engine.handling.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.Dependent;

import org.jboss.errai.common.client.api.Assert;
import org.kie.workbench.common.forms.processing.engine.handling.FieldChangeHandler;
import org.kie.workbench.common.forms.processing.engine.handling.FieldChangeHandlerManager;
import org.kie.workbench.common.forms.processing.engine.handling.FormField;
import org.kie.workbench.common.forms.processing.engine.handling.FormValidator;

@Dependent
public class FieldChangeHandlerManagerImpl implements FieldChangeHandlerManager {

    private FormValidator validator;

    private Map<String, FieldChangeProcessor> fieldExecutors = new HashMap<>();
    private List<FieldChangeHandler> defaultChangeHandlers = new ArrayList<>();

    @Override
    public void setValidator(FormValidator validator) {
        this.validator = validator;
    }

    @Override
    public void registerField(FormField formField) {
        fieldExecutors.put(formField.getFieldName(),
                           new FieldChangeProcessor(formField));
    }

    @Override
    public void addFieldChangeHandler(FieldChangeHandler changeHandler) {
        defaultChangeHandlers.add(changeHandler);
    }

    @Override
    public void addFieldChangeHandler(String fieldName,
                                      FieldChangeHandler changeHandler) {
        Assert.notNull("FieldName cannot be null",
                       fieldName);
        Assert.notNull("FieldChangeHandler cannot be null",
                       changeHandler);

        FieldChangeProcessor executor = fieldExecutors.get(fieldName);

        if (executor != null) {
            executor.addFieldChangeHandler(changeHandler);
        }
    }

    @Override
    public void processFieldChange(String fieldName,
                                   Object newValue,
                                   Object model) {
        validateAndNotify(fieldName,
                          newValue,
                          model,
                          true);
    }

    @Override
    public void notifyFieldChange(String fieldName,
                                  Object newValue) {
        validateAndNotify(fieldName,
                          newValue,
                          null,
                          false);
    }

    protected void validateAndNotify(String fieldName,
                                     Object newValue,
                                     Object model,
                                     boolean validate) {
        assert fieldName != null;

        String realFieldName = fieldName;

        if (realFieldName.indexOf(".") != -1) {
            realFieldName = realFieldName.substring(0,
                                                    realFieldName.indexOf("."));
        }

        FieldChangeProcessor executor = fieldExecutors.get(realFieldName);

        if (executor != null) {
            if (validate && executor.isRequiresValidation()) {
                if (validator != null && !validator.validate(executor.getField(),
                                                             model)) {
                    return;
                }
            }
            doProcess(executor.getChangeHandlers(),
                      fieldName,
                      newValue);
            doProcess(defaultChangeHandlers,
                      fieldName,
                      newValue);
        }
    }

    protected void doProcess(Collection<FieldChangeHandler> handlers,
                             String fieldName,
                             Object newValue) {
        for (FieldChangeHandler handler : handlers) {
            handler.onFieldChange(fieldName,
                                  newValue);
        }
    }

    @Override
    public void clear() {
        fieldExecutors.clear();
        defaultChangeHandlers.clear();
    }
}
