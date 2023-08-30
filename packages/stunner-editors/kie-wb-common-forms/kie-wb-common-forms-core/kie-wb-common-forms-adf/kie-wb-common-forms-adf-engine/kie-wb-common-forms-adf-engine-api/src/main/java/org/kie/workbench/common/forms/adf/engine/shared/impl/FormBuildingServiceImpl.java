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


package org.kie.workbench.common.forms.adf.engine.shared.impl;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.forms.adf.engine.shared.FormBuildingService;
import org.kie.workbench.common.forms.adf.engine.shared.FormElementFilter;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.FormGenerator;
import org.kie.workbench.common.forms.model.FormDefinition;

@ApplicationScoped
public class FormBuildingServiceImpl implements FormBuildingService {

    protected FormGenerator formGenerator;

    @Inject
    public FormBuildingServiceImpl(FormGenerator formGenerator) {
        this.formGenerator = formGenerator;
    }

    @Override
    public FormDefinition generateFormForModel(Object model, FormElementFilter... filters) {
        if (model == null) {
            throw new IllegalArgumentException("Cannot generate form: Model cannot be null");
        }

        return formGenerator.generateFormForModel(model, filters);
    }

    @Override
    public FormDefinition generateFormForClass(Class clazz, FormElementFilter... filters) {
        if (clazz == null) {
            throw new IllegalArgumentException("Cannot generate form: Class cannot be null");
        }

        return formGenerator.generateFormForClass(clazz, filters);
    }

    @Override
    public FormDefinition generateFormForClassName(String className, FormElementFilter... filters) {
        if (className == null) {
            throw new IllegalArgumentException("Cannot generate form: Class cannot be null");
        }

        return formGenerator.generateFormForClassName(className, filters);
    }
}
