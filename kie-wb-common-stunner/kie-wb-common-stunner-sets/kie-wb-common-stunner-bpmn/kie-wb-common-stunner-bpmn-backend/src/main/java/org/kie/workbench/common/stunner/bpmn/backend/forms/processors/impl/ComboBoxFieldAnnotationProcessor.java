/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.backend.forms.processors.impl;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.drools.workbench.models.datamodel.oracle.Annotation;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.statik.impl.FieldSetting;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.statik.impl.processors.AbstractFieldAnnotationProcessor;
import org.kie.workbench.common.forms.dynamic.service.context.generation.TransformerContext;
import org.kie.workbench.common.stunner.bpmn.forms.meta.definition.ComboBox;
import org.kie.workbench.common.stunner.bpmn.forms.model.ComboBoxFieldDefinition;
import org.kie.workbench.common.stunner.bpmn.forms.service.fieldProviders.ComboBoxFieldProvider;

@Dependent
public class ComboBoxFieldAnnotationProcessor extends AbstractFieldAnnotationProcessor<ComboBoxFieldDefinition, ComboBoxFieldProvider> {

    @Inject
    public ComboBoxFieldAnnotationProcessor(final ComboBoxFieldProvider fieldProvider) {
        super(fieldProvider);
    }

    @Override
    protected void initField(final ComboBoxFieldDefinition field,
                             final Annotation annotation,
                             final FieldSetting fieldSetting,
                             final TransformerContext context) {
        field.setDefaultValue((String) annotation.getParameters().get("defaultValue"));
    }

    @Override
    protected Class<ComboBox> getSupportedAnnotation() {
        return ComboBox.class;
    }
}
