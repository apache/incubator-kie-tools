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


package org.kie.workbench.common.forms.dynamic.client.init.impl;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.validation.client.dynamic.DynamicValidator;
import org.kie.workbench.common.forms.dynamic.client.helper.MapModelBindingHelper;
import org.kie.workbench.common.forms.dynamic.client.init.FormHandlerGenerator;
import org.kie.workbench.common.forms.dynamic.client.processing.engine.handling.DynamicModelValidator;
import org.kie.workbench.common.forms.dynamic.service.shared.DynamicContext;
import org.kie.workbench.common.forms.dynamic.service.shared.impl.MapModelRenderingContext;
import org.kie.workbench.common.forms.dynamic.service.shared.impl.validation.DynamicModelConstraints;
import org.kie.workbench.common.forms.model.JavaFormModel;
import org.kie.workbench.common.forms.processing.engine.handling.FieldStateValidator;
import org.kie.workbench.common.forms.processing.engine.handling.FormHandler;
import org.kie.workbench.common.forms.processing.engine.handling.FormValidator;
import org.kie.workbench.common.forms.processing.engine.handling.impl.FieldChangeHandlerManagerImpl;
import org.kie.workbench.common.forms.processing.engine.handling.impl.FormHandlerImpl;
import org.kie.workbench.common.forms.processing.engine.handling.impl.FormValidatorImpl;

@Dependent
@DynamicContext
public class DynamicFormHandlerGenerator implements FormHandlerGenerator<MapModelRenderingContext> {

    protected DynamicValidator validator;

    private MapModelBindingHelper helper;

    protected FieldStateValidator fieldStateValidator;

    @Inject
    public DynamicFormHandlerGenerator(DynamicValidator validator,
                                       FieldStateValidator fieldStateValidator,
                                       MapModelBindingHelper helper) {
        this.validator = validator;
        this.fieldStateValidator = fieldStateValidator;
        this.helper = helper;
    }

    @Override
    public FormHandler generateFormHandler(MapModelRenderingContext context) {

        DynamicModelValidator dynamicValidator = new DynamicModelValidator(validator);

        if (context.getRootForm().getModel() instanceof JavaFormModel) {
            DynamicModelConstraints constraints = context.getModelConstraints().get(((JavaFormModel) context.getRootForm().getModel()).getType());

            if (constraints != null) {
                dynamicValidator.setModelConstraints(constraints);
            }
        }

        FormValidator formValidator = new FormValidatorImpl(dynamicValidator,
                                                            fieldStateValidator);

        FormHandler handler = new FormHandlerImpl(formValidator,
                                                  new FieldChangeHandlerManagerImpl());

        if (context.getParentContext() == null) {
            helper.initContext(context);
        }

        return handler;
    }
}
