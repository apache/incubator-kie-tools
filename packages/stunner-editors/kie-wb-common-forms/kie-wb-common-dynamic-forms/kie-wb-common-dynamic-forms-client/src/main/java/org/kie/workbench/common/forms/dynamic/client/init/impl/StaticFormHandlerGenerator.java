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
import javax.validation.Validator;

import org.kie.workbench.common.forms.dynamic.client.init.FormHandlerGenerator;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.forms.dynamic.service.shared.StaticContext;
import org.kie.workbench.common.forms.processing.engine.handling.FieldStateValidator;
import org.kie.workbench.common.forms.processing.engine.handling.FormHandler;
import org.kie.workbench.common.forms.processing.engine.handling.FormValidator;
import org.kie.workbench.common.forms.processing.engine.handling.impl.DefaultModelValidator;
import org.kie.workbench.common.forms.processing.engine.handling.impl.FieldChangeHandlerManagerImpl;
import org.kie.workbench.common.forms.processing.engine.handling.impl.FormHandlerImpl;
import org.kie.workbench.common.forms.processing.engine.handling.impl.FormValidatorImpl;

@Dependent
@StaticContext
public class StaticFormHandlerGenerator implements FormHandlerGenerator<FormRenderingContext> {

    protected Validator validator;

    protected FieldStateValidator fieldStateValidator;

    @Inject
    public StaticFormHandlerGenerator(Validator validator,
                                      FieldStateValidator fieldStateValidator) {
        this.validator = validator;
        this.fieldStateValidator = fieldStateValidator;
    }

    @Override
    public FormHandler generateFormHandler(FormRenderingContext context) {
        FormValidator formValidator = new FormValidatorImpl(new DefaultModelValidator(validator),
                                                            fieldStateValidator);

        FormHandler handler = new FormHandlerImpl(formValidator,
                                                  new FieldChangeHandlerManagerImpl());

        return handler;
    }
}
