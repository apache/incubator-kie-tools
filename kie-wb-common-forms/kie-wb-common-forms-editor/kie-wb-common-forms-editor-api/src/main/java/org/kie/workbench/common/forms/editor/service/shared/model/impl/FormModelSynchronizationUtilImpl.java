/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.editor.service.shared.model.impl;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.forms.commons.shared.layout.FormLayoutTemplateGenerator;
import org.kie.workbench.common.forms.editor.model.FormModelSynchronizationResult;
import org.kie.workbench.common.forms.editor.model.TypeConflict;
import org.kie.workbench.common.forms.editor.service.shared.model.FormModelSynchronizationUtil;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.ModelProperty;
import org.kie.workbench.common.forms.service.shared.FieldManager;

@Dependent
public class FormModelSynchronizationUtilImpl implements FormModelSynchronizationUtil {

    private static Logger logger = Logger.getLogger(FormModelSynchronizationUtilImpl.class.getName());

    private FieldManager fieldManager;

    private FormLayoutTemplateGenerator formLayoutTemplateGenerator;

    private FormDefinition form;

    private FormModelSynchronizationResult synchronizationResult;

    @Inject
    public FormModelSynchronizationUtilImpl(FieldManager fieldManager,
                                            FormLayoutTemplateGenerator formLayoutTemplateGenerator) {
        this.fieldManager = fieldManager;
        this.formLayoutTemplateGenerator = formLayoutTemplateGenerator;
    }

    @Override
    public void init(FormDefinition form,
                     FormModelSynchronizationResult synchronizationResult) {
        PortablePreconditions.checkNotNull("form",
                                           form);
        PortablePreconditions.checkNotNull("synchronizationResult",
                                           synchronizationResult);

        this.form = form;
        this.synchronizationResult = synchronizationResult;
    }

    @Override
    public void fixRemovedFields() {
        if (synchronizationResult.hasRemovedProperties()) {
            synchronizationResult.getRemovedProperties().forEach(this::unBindField);
        }
    }

    protected void unBindField(ModelProperty removedProperty) {
        /*
        If some properties have been removed from the formModel we are going to unbind (not removing) the fields that
        are binded to them to avoid problems.
        */
        Optional<FieldDefinition> optional = Optional.ofNullable(form.getFieldByBinding(removedProperty.getName()));
        if (optional.isPresent()) {
            optional.get().setBinding(null);
            logger.warning("Variable '" + removedProperty.getName() + "' has been removed from the Process/Task. Unbinding form field to avoid conflicts during runtime.");
        }
    }

    @Override
    public void resolveConflicts() {
        if (synchronizationResult.hasConflicts()) {
            synchronizationResult.getPropertyConflicts().forEach(this::resolveConflict);
        }
    }

    protected void resolveConflict(TypeConflict typeConflict) {
        /*
        If there are type conflicts (a property that has changed the type) we are going to solve it by trying to
        update the binded field to the right FieldDefinition.
        */
        Optional<FieldDefinition> originalformFieldOptional = Optional.ofNullable(form.getFieldByBinding(typeConflict.getPropertyName()));
        if (originalformFieldOptional.isPresent()) {
            FieldDefinition originalFormField = originalformFieldOptional.get();
            logger.warning("Conflict found on variable '" + typeConflict.getPropertyName() + "', previous type was '" + typeConflict.getBefore().getClassName() + "' and new one is '" + typeConflict.getNow().getClassName() + "'. Trying to fix.");

            // Determining if the current FieldType is suitable for the new property type.
            Optional<FieldDefinition> newFieldOptional = Optional.ofNullable(fieldManager.getFieldFromProvider(originalFormField.getFieldType().getTypeName(),
                                                                                                               typeConflict.getNow()));

            FieldDefinition newField;

            if (newFieldOptional.isPresent()) {
                // There's a suitable FieldDefinition with the same FieldType than the old field for the new property type.
                newField = newFieldOptional.get();
            } else {
                // It seems that the new property type isn't compatible with the actual FieldDefinition. Getting a compatible FieldDefintion..
                newField = fieldManager.getDefinitionByDataType(typeConflict.getNow());
            }

            newField.setId(originalFormField.getId());
            newField.setName(originalFormField.getName());
            newField.copyFrom(originalFormField);
            newField.setStandaloneClassName(typeConflict.getNow().getClassName());
            form.getFields().remove(originalFormField);
            form.getFields().add(newField);
        }
    }

    @Override
    public void addNewFields() {
        addNewFields(fieldManager::getDefinitionByModelProperty);
    }

    @Override
    public void addNewFields(Function<ModelProperty, FieldDefinition> fieldProviderFunction) {
        if (synchronizationResult.hasNewProperties()) {

            /*
            If there are new properties we are going to add them to the form & update the form template.
            */
            synchronizationResult.getNewProperties().forEach(newProperty -> addNewField(newProperty,
                                                                                        fieldProviderFunction));

            List<FieldDefinition> newFields = synchronizationResult.getNewProperties().stream().map(form::getFieldByBoundProperty).collect(Collectors.toList());
            formLayoutTemplateGenerator.updateLayoutTemplate(form,
                                                             newFields);
        }
    }

    protected void addNewField(ModelProperty newProperty,
                               Function<ModelProperty, FieldDefinition> fieldProviderFunction) {
        if (!Optional.ofNullable(form.getFieldByBinding(newProperty.getName())).isPresent()) {
            logger.info("Adding new form field for variable '" + newProperty.getName() + "'.");
            FieldDefinition newField = fieldProviderFunction.apply(newProperty);
            form.getFields().add(newField);
        }
    }
}
