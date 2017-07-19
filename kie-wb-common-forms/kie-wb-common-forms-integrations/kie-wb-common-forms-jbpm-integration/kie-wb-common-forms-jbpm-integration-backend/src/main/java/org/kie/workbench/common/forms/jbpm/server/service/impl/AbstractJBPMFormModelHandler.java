/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.jbpm.server.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.kie.workbench.common.forms.editor.backend.service.impl.AbstractFormModelHandler;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.HasPlaceHolder;
import org.kie.workbench.common.forms.jbpm.model.authoring.JBPMFormModel;
import org.kie.workbench.common.forms.jbpm.service.shared.BPMFinderService;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.ModelProperty;
import org.kie.workbench.common.forms.service.shared.FieldManager;
import org.kie.workbench.common.services.backend.project.ProjectClassLoaderHelper;
import org.kie.workbench.common.services.shared.project.KieProjectService;

public abstract class AbstractJBPMFormModelHandler<M extends JBPMFormModel> extends AbstractFormModelHandler<M> {

    protected FieldManager fieldManager;

    protected BPMFinderService bpmFinderService;

    public AbstractJBPMFormModelHandler(KieProjectService projectService,
                                        ProjectClassLoaderHelper classLoaderHelper,
                                        FieldManager fieldManager,
                                        BPMFinderService bpmFinderService) {
        super(projectService,
              classLoaderHelper);
        this.fieldManager = fieldManager;
        this.bpmFinderService = bpmFinderService;
    }

    @Override
    protected void initialize() {
        super.checkInitialized();
    }

    @Override
    protected List<FieldDefinition> doGenerateModelFields() {
        List<FieldDefinition> fields = new ArrayList<>();

        formModel.getProperties().forEach(property -> {
            FieldDefinition field = fieldManager.getDefinitionByDataType(property.getTypeInfo());

            if (field != null) {
                initFieldDefinition(field,
                                    property);
                fields.add(field);
            }
        });

        return fields;
    }

    @Override
    protected FieldDefinition doCreateFieldDefinition(ModelProperty property) {
        Optional<ModelProperty> optional = formModel.getProperties().stream().filter(modelProperty -> modelProperty.getName().equals(property.getName())).findFirst();

        if (optional.isPresent()) {
            FieldDefinition field = fieldManager.getDefinitionByDataType(property.getTypeInfo());
            initFieldDefinition(field,
                                property);
            return field;
        }

        return null;
    }

    protected void initFieldDefinition(FieldDefinition field,
                                       ModelProperty property) {
        String label = StringUtils.capitalize(property.getName());
        field.setId(property.getName());
        field.setName(property.getName());
        field.setLabel(label);
        field.setBinding(property.getName());
        if (field instanceof HasPlaceHolder) {
            ((HasPlaceHolder) field).setPlaceHolder(label);
        }
    }
}
