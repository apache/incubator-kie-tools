/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.kie.workbench.common.forms.editor.client.handler.formModel.defaultFormModel;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.forms.editor.client.handler.formModel.FormModelCreationViewManager;
import org.kie.workbench.common.forms.editor.client.resources.i18n.FormEditorConstants;
import org.kie.workbench.common.forms.model.DefaultFormModel;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.UberElement;

@Dependent
public class DefaultFormModelCreationViewManager implements FormModelCreationViewManager<DefaultFormModel> {

    private TranslationService translationService;

    @Inject
    public DefaultFormModelCreationViewManager(TranslationService translationService) {
        this.translationService = translationService;
    }

    @Override
    public String getLabel() {
        return translationService.getTranslation(FormEditorConstants.DefaultFormModelLabel);
    }

    @Override
    public int getPriority() {
        return 10000;
    }

    @Override
    public void init(Path projectPath) {
        // Nothing to setUp here
    }

    @Override
    public DefaultFormModel getFormModel() {
        return new DefaultFormModel();
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void reset() {

    }

    @Override
    public UberElement getView() {
        return null;
    }
}
