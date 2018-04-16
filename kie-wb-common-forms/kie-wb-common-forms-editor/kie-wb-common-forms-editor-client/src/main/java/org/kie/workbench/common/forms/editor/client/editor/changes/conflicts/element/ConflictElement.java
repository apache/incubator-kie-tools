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

package org.kie.workbench.common.forms.editor.client.editor.changes.conflicts.element;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.forms.editor.client.resources.i18n.FormEditorConstants;

@Dependent
public class ConflictElement implements IsElement,
                                        ConflictElementView.Presenter {

    boolean isShowMorePressed = false;

    private String firstMessagePart;
    private String fullMessage;

    private ConflictElementView view;
    private TranslationService translationService;

    @Inject
    public ConflictElement(ConflictElementView view,
                           TranslationService translationService) {
        this.view = view;
        this.translationService = translationService;
        this.view.init(this);
    }

    public void showConflict(String target,
                             String firstMessagePart,
                             String secondMessagePart) {
        this.firstMessagePart = firstMessagePart;
        this.fullMessage = firstMessagePart + " " + secondMessagePart;
        view.showConflict(target,
                          firstMessagePart);
    }

    @Override
    public void onShowMoreClick() {
        if (isShowMorePressed) {
            view.setMessage(firstMessagePart);
            view.setShowMoreText(translationService.getTranslation(FormEditorConstants.ShowMoreLabel));
        } else {
            view.setMessage(fullMessage);
            view.setShowMoreText(translationService.getTranslation(FormEditorConstants.ShowLessLabel));
        }
        isShowMorePressed = !isShowMorePressed;
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }
}
