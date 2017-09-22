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

package org.kie.workbench.common.forms.editor.client.editor.changes.conflicts.impl;

import java.util.function.Consumer;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.forms.editor.client.editor.changes.conflicts.element.ConflictElement;
import org.kie.workbench.common.forms.editor.client.resources.i18n.FormEditorConstants;
import org.kie.workbench.common.forms.editor.model.FormModelSynchronizationResult;
import org.kie.workbench.common.forms.editor.model.FormModelerContent;
import org.kie.workbench.common.forms.editor.service.shared.model.FormModelSynchronizationUtil;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;

@Dependent
public class FormModelConflictHandler extends AbstractConflictHandler {

    private FormModelSynchronizationUtil formModelSynchronizationUtil;

    private boolean removed;
    private boolean conflicts;

    @Inject
    public FormModelConflictHandler(FormModelSynchronizationUtil formModelSynchronizationUtil,
                                    ManagedInstance<ConflictElement> conflictElementManagedInstance,
                                    TranslationService translationService) {
        super(conflictElementManagedInstance,
              translationService);
        this.formModelSynchronizationUtil = formModelSynchronizationUtil;
    }

    @Override
    public boolean checkConflicts(FormModelerContent content,
                                  Consumer<ConflictElement> conflictElementConsumer) {
        init(content,
             conflictElementConsumer);
        this.removed = false;
        this.conflicts = false;

        FormDefinition formDefinition = content.getDefinition();

        FormModelSynchronizationResult synchronizationResult = content.getSynchronizationResult();

        if (synchronizationResult == null) {
            return false;
        }

        checkRemovedProperties(formDefinition,
                               synchronizationResult);
        checkConflictProperties(formDefinition,
                                synchronizationResult);

        return removed || conflicts;
    }

    private void checkRemovedProperties(FormDefinition formDefinition,
                                        FormModelSynchronizationResult synchronizationResult) {
        if (synchronizationResult.hasRemovedProperties()) {
            synchronizationResult.getRemovedProperties().stream().map(property -> formDefinition.getFieldByBinding(property.getName())).forEach(this::consumeRemovedFieldConflict);
        }
    }

    protected void consumeRemovedFieldConflict(FieldDefinition fieldDefinition) {
        if (fieldDefinition != null) {
            removed = true;
            consumeConflict(getFieldText(fieldDefinition),
                            translationService.format(FormEditorConstants.ModelPropertyRemoved1,
                                                      fieldDefinition.getBinding()),
                            translationService.getTranslation(FormEditorConstants.ModelPropertyRemoved2));
        }
    }

    private void checkConflictProperties(FormDefinition formDefinition,
                                         FormModelSynchronizationResult synchronizationResult) {
        if (synchronizationResult.hasConflicts()) {
            synchronizationResult.getPropertyConflicts().stream().map(conflict -> formDefinition.getFieldByBinding(conflict.getPropertyName())).forEach(this::consumeTypeConflict);
        }
    }

    protected void consumeTypeConflict(FieldDefinition fieldDefinition) {
        if (fieldDefinition != null) {
            conflicts = true;
            consumeConflict(getFieldText(fieldDefinition),
                            translationService.format(FormEditorConstants.ModelPropertyTypeConflict1,
                                                      fieldDefinition.getBinding()),
                            translationService.getTranslation(FormEditorConstants.ModelPropertyTypeConflict2));
        }
    }

    @Override
    public void onAccept() {
        formModelSynchronizationUtil.init(content.getDefinition(),
                                          content.getSynchronizationResult());
        if (removed) {
            formModelSynchronizationUtil.fixRemovedFields();
        }
        if (conflicts) {
            formModelSynchronizationUtil.resolveConflicts();
        }
        conflictElementManagedInstance.destroyAll();
    }
}
