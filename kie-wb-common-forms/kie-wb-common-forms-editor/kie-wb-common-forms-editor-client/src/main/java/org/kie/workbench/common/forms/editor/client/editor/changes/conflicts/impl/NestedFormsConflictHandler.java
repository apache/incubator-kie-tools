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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.forms.editor.client.editor.changes.conflicts.element.ConflictElement;
import org.kie.workbench.common.forms.editor.client.resources.i18n.FormEditorConstants;
import org.kie.workbench.common.forms.editor.model.FormModelerContent;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.HasNestedForm;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.IsCRUDDefinition;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.uberfire.mvp.Command;

@Dependent
public class NestedFormsConflictHandler extends AbstractConflictHandler {

    private List<Command> commands = new ArrayList<>();

    @Inject
    public NestedFormsConflictHandler(ManagedInstance<ConflictElement> conflictElementManagedInstance,
                                      TranslationService translationService) {
        super(conflictElementManagedInstance,
              translationService);
    }

    @Override
    public boolean checkConflicts(FormModelerContent content,
                                  Consumer<ConflictElement> conflictElementConsumer) {

        init(content,
             conflictElementConsumer);

        content.getDefinition().getFields().forEach(this::checkConflict);

        return !commands.isEmpty();
    }

    protected void checkConflict(FieldDefinition fieldDefinition) {
        if (fieldDefinition instanceof HasNestedForm) {
            HasNestedForm nestedForm = (HasNestedForm) fieldDefinition;
            if (content.getRenderingContext().getAvailableForms().get(nestedForm.getNestedForm()) == null) {
                consumeConflict(getFieldText(fieldDefinition),
                                translationService.getTranslation(FormEditorConstants.NestedFormsConflictHandlerNestedForm),
                                translationService.getTranslation(FormEditorConstants.NestedFormsConflictHandlerFix));
                commands.add(() -> nestedForm.setNestedForm(null));
            }
        } else if (fieldDefinition instanceof IsCRUDDefinition) {
            IsCRUDDefinition crudDefinition = (IsCRUDDefinition) fieldDefinition;
            if (content.getRenderingContext().getAvailableForms().get(crudDefinition.getCreationForm()) == null) {
                consumeConflict(getFieldText(fieldDefinition),
                                translationService.getTranslation(FormEditorConstants.NestedFormsConflictHandlerCreationForm),
                                translationService.getTranslation(FormEditorConstants.NestedFormsConflictHandlerFix));
                commands.add(() -> crudDefinition.setCreationForm(null));
            }
            if (content.getRenderingContext().getAvailableForms().get(crudDefinition.getEditionForm()) == null) {
                consumeConflict(getFieldText(fieldDefinition),
                                translationService.getTranslation(FormEditorConstants.NestedFormsConflictHandlerEditionForm),
                                translationService.getTranslation(FormEditorConstants.NestedFormsConflictHandlerFix));
                commands.add(() -> crudDefinition.setEditionForm(null));
            }
        }
    }

    @Override
    public void onAccept() {
        commands.forEach(Command::execute);
        commands.clear();
        conflictElementManagedInstance.destroyAll();
    }
}
