/*
 * Copyright (C) 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.settings.util.modal.doublevalue;

import java.util.function.BiConsumer;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.uberfire.ext.editor.commons.client.file.popups.elemental2.Elemental2Modal;

@Dependent
public class AddDoubleValueModal extends Elemental2Modal<AddDoubleValueModal.View> {

    private final TranslationService translationService;

    private BiConsumer<String, String> onAdd;

    public interface View extends Elemental2Modal.View<AddDoubleValueModal> {

        void focus();

        String getName();

        String getValue();

        void clearForm();

        void setHeader(final String header);

        void setNameLabel(final String label);

        void setValueLabel(final String label);
    }

    @Inject
    public AddDoubleValueModal(final View view,
                               final TranslationService translationService) {
        super(view);
        this.translationService = translationService;
    }

    public void setup(final String headerKey,
                      final String nameKey,
                      final String valueKey) {

        getView().setHeader(translationService.format(headerKey));
        getView().setNameLabel(translationService.format(nameKey));
        getView().setValueLabel(translationService.format(valueKey));
        super.setup();
    }

    public void show(final BiConsumer<String, String> onAdd) {
        this.onAdd = onAdd;
        getView().clearForm();
        super.show();
        getView().focus();
    }

    public void add() {
        onAdd.accept(getView().getName(), getView().getValue());
        hide();
    }

    public void cancel() {
        hide();
    }
}
