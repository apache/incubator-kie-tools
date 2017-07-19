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

package org.kie.workbench.common.forms.editor.client.editor.modelChanges;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.forms.editor.client.editor.modelChanges.displayers.conflicts.PropertiesConflictsDisplayer;
import org.kie.workbench.common.forms.editor.client.editor.modelChanges.displayers.newProperties.NewPropertiesDisplayer;
import org.kie.workbench.common.forms.editor.model.FormModelSynchronizationResult;
import org.kie.workbench.common.forms.editor.model.FormModelerContent;
import org.kie.workbench.common.forms.editor.service.shared.model.FormModelSynchronizationUtil;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.FormModel;
import org.kie.workbench.common.forms.service.shared.FieldManager;
import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.mvp.Command;

@Dependent
public class ModelChangesDisplayer implements ModelChangesDisplayerView.Presenter {

    private ModelChangesDisplayerView view;

    private NewPropertiesDisplayer newPropertiesDisplayer;

    private PropertiesConflictsDisplayer propertiesConflictsDisplayer;

    private FieldManager fieldManager;

    private FormModelSynchronizationUtil formModelSynchronizationUtil;

    private FormModelerContent content;

    private Command onClose;

    @Inject
    public ModelChangesDisplayer(ModelChangesDisplayerView view,
                                 PropertiesConflictsDisplayer propertiesConflictsDisplayer,
                                 NewPropertiesDisplayer newPropertiesDisplayer,
                                 FieldManager fieldManager,
                                 FormModelSynchronizationUtil formModelSynchronizationUtil) {
        this.view = view;
        this.newPropertiesDisplayer = newPropertiesDisplayer;
        this.propertiesConflictsDisplayer = propertiesConflictsDisplayer;
        this.fieldManager = fieldManager;
        this.formModelSynchronizationUtil = formModelSynchronizationUtil;
        this.view.init(this);
    }

    public void show(FormModelerContent content,
                     Command onClose) {
        PortablePreconditions.checkNotNull("content",
                                           content);
        PortablePreconditions.checkNotNull("onClose",
                                           onClose);
        if (content.getSynchronizationResult().hasChanges()) {
            this.content = content;
            this.onClose = onClose;

            boolean canShow = false;

            FormModelSynchronizationResult synchronizationResult = content.getSynchronizationResult();
            if (synchronizationResult.hasNewProperties()) {
                List<FieldDefinition> modelFields = new ArrayList<>();
                synchronizationResult.getNewProperties().forEach(property -> content.getAvailableFields().stream().filter(fieldDefinition -> property.getName().equals(fieldDefinition.getBinding())).findFirst().ifPresent(fieldDefinition -> modelFields.add(fieldDefinition)));
                showNewAvailableFields(modelFields);
                canShow = true;
            }

            if (synchronizationResult.hasRemovedProperties() || synchronizationResult.hasConflicts()) {
                FormDefinition formDefinition = content.getDefinition();
                List<FieldDefinition> removedFields = new ArrayList<>();
                if (synchronizationResult.hasRemovedProperties()) {
                    removedFields = synchronizationResult.getRemovedProperties().stream().map(property -> formDefinition.getFieldByBinding(property.getName())).filter(fieldDefinition -> fieldDefinition != null).collect(Collectors.toList());
                }
                List<FieldDefinition> conflictFields = new ArrayList<>();
                if (synchronizationResult.hasConflicts()) {
                    conflictFields = synchronizationResult.getPropertyConflicts().stream().map(conflict -> formDefinition.getFieldByBinding(conflict.getPropertyName())).filter(fieldDefinition -> fieldDefinition != null).collect(Collectors.toList());
                }
                if (!removedFields.isEmpty() || !conflictFields.isEmpty()) {
                    showConflictFields(removedFields,
                                       conflictFields);
                    canShow = true;
                }
            }

            if (canShow) {
                formModelSynchronizationUtil.init(content.getDefinition(),
                                                  content.getSynchronizationResult());

                view.show();
            }
        } else {
            throw new IllegalStateException("No model changes to show");
        }
    }

    protected void showNewAvailableFields(List<FieldDefinition> fieldDefinitions) {
        PortablePreconditions.checkNotNull("fieldDefinitions",
                                           fieldDefinitions);

        newPropertiesDisplayer.showAvailableFields(fieldDefinitions);

        view.getElement().appendChild(newPropertiesDisplayer.getElement());
    }

    protected void showConflictFields(List<FieldDefinition> removedFields,
                                      List<FieldDefinition> typeConflictFields) {
        PortablePreconditions.checkNotNull("removedFields",
                                           removedFields);
        PortablePreconditions.checkNotNull("typeConflictFields",
                                           typeConflictFields);

        propertiesConflictsDisplayer.showRemovedFields(removedFields);

        propertiesConflictsDisplayer.showTypeConflictFields(typeConflictFields);

        view.getElement().appendChild(propertiesConflictsDisplayer.getElement());
    }

    @Override
    public FormModel getFormModel() {
        return content.getDefinition().getModel();
    }

    @Override
    public void close() {
        newPropertiesDisplayer.clear();
        propertiesConflictsDisplayer.clear();

        formModelSynchronizationUtil.fixRemovedFields();
        formModelSynchronizationUtil.resolveConflicts();

        onClose.execute();
    }
}
