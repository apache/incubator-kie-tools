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

package org.kie.workbench.common.forms.editor.client.editor.modelChanges.displayers.conflicts;

import java.util.List;
import java.util.Optional;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.forms.editor.client.editor.modelChanges.displayers.conflicts.elements.ConflictElement;
import org.kie.workbench.common.forms.editor.client.resources.i18n.FormEditorConstants;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.uberfire.commons.validation.PortablePreconditions;

@Dependent
public class PropertiesConflictsDisplayer implements IsElement,
                                                     PropertiesConflictsDisplayerView.Presenter {

    private PropertiesConflictsDisplayerView view;

    private ManagedInstance<ConflictElement> conflictElements;

    private TranslationService translationService;

    @Inject
    public PropertiesConflictsDisplayer(PropertiesConflictsDisplayerView view,
                                        ManagedInstance<ConflictElement> conflictElements,
                                        TranslationService translationService) {
        this.view = view;
        this.conflictElements = conflictElements;
        this.translationService = translationService;

        view.init(this);
    }

    public void clear() {
        view.clear();
        conflictElements.destroyAll();
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }

    public void showRemovedFields(List<FieldDefinition> fieldDefinitions) {
        PortablePreconditions.checkNotNull("fieldDefinitions",
                                           fieldDefinitions);

        fieldDefinitions.forEach(this::showRemovedFields);
    }

    protected void showRemovedFields(FieldDefinition fieldDefinition) {
        showElement(fieldDefinition,
                    translationService.format(FormEditorConstants.PropertiesConflictsDisplayerViewIpmlRemoved1,
                                              fieldDefinition.getBinding()),
                    translationService.getTranslation(FormEditorConstants.PropertiesConflictsDisplayerViewIpmlRemoved2));
    }

    public void showTypeConflictFields(List<FieldDefinition> fieldDefinitions) {
        PortablePreconditions.checkNotNull("fieldDefinitions",
                                           fieldDefinitions);

        fieldDefinitions.forEach(this::showTypeConflictField);
    }

    protected void showTypeConflictField(FieldDefinition fieldDefinition) {
        showElement(fieldDefinition,
                    translationService.format(FormEditorConstants.PropertiesConflictsDisplayerViewIpmlTypeConflict1,
                                              fieldDefinition.getBinding()),
                    translationService.getTranslation(FormEditorConstants.PropertiesConflictsDisplayerViewIpmlTypeConflict2));
    }

    private void showElement(FieldDefinition fieldDefinition,
                             String message1,
                             String message2) {

        ConflictElement conflictElement = conflictElements.get();

        conflictElement.showConflict(Optional.ofNullable(fieldDefinition.getLabel()).orElse(fieldDefinition.getName()),
                                     message1, message2);

        view.showConflict(conflictElement);
    }
}
