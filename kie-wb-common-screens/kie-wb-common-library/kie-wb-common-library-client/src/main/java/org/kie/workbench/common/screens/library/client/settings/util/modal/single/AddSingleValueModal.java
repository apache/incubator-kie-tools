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

package org.kie.workbench.common.screens.library.client.settings.util.modal.single;

import java.util.function.Consumer;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.uberfire.ext.editor.commons.client.file.popups.elemental2.Elemental2Modal;

@Dependent
public class AddSingleValueModal extends Elemental2Modal<AddSingleValueModal.View> {

    private final TranslationService translationService;

    private Consumer<String> onAdd;

    public interface View extends Elemental2Modal.View<AddSingleValueModal> {

        void focus();

        String getValue();

        void clearForm();

        void setHeader(final String header);

        void setLabel(final String label);
    }

    @Inject
    public AddSingleValueModal(final View view,
                               final TranslationService translationService) {
        super(view);
        this.translationService = translationService;
    }

    public void setup(final String headerKey,
                      final String labelKey) {

        getView().setHeader(translationService.format(headerKey));
        getView().setLabel(translationService.format(labelKey));
        super.setup();
    }

    public void show(final Consumer<String> onAdd) {
        this.onAdd = onAdd;
        getView().clearForm();
        super.show();
        getView().focus();
    }

    public void add() {
        onAdd.accept(getView().getValue());
        hide();
    }

    public void cancel() {
        hide();
    }
}
