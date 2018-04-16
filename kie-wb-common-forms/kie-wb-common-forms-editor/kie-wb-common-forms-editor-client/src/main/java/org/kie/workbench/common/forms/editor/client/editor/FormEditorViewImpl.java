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
package org.kie.workbench.common.forms.editor.client.editor;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.RequiresResize;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.forms.editor.client.resources.i18n.FormEditorConstants;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.metadata.client.KieEditorViewImpl;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.layout.editor.client.api.LayoutEditor;
import org.uberfire.ext.widgets.common.client.common.popups.YesNoCancelPopup;
import org.uberfire.mvp.Command;

@Dependent
@Templated
public class FormEditorViewImpl extends KieEditorViewImpl implements FormEditorPresenter.FormEditorView,
                                                                     RequiresResize {

    @Inject
    @DataField
    private Div container;

    @Inject
    @DataField
    private Div editorContent;

    private TranslationService translationService;

    private FormEditorPresenter presenter;

    @Inject
    public FormEditorViewImpl(TranslationService translationService) {
        this.translationService = translationService;
    }

    @Override
    public void init(FormEditorPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setupLayoutEditor(LayoutEditor layoutEditor) {
        DOMUtil.removeAllChildren(editorContent);
        DOMUtil.appendWidgetToElement(editorContent, layoutEditor.asWidget());
    }

    @Override
    public void showSavePopup(Path path,
                              Command saveCommand,
                              Command cancelCommand) {
        YesNoCancelPopup popup = YesNoCancelPopup.newYesNoCancelPopup(org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants.INSTANCE.Information(),
                                                                      translationService.getTranslation(FormEditorConstants.FormEditorViewImplSaveBeforeRename),
                                                                      saveCommand,
                                                                      CommonConstants.INSTANCE.Save(),
                                                                      null,
                                                                      null,
                                                                      cancelCommand,
                                                                      translationService.getTranslation(FormEditorConstants.FormEditorViewImplDontSave));
        popup.show();
    }

    @Override
    public void onResize() {
        if (getParent() == null) {
            return;
        }

        double height = getParent().getOffsetHeight() * 0.95;
        double width = getParent().getOffsetWidth();

        editorContent.getStyle().setProperty("height", height + "px");

        container.getStyle().setProperty("width", width + "px");
        container.getStyle().setProperty("height", height + "px");
    }
}