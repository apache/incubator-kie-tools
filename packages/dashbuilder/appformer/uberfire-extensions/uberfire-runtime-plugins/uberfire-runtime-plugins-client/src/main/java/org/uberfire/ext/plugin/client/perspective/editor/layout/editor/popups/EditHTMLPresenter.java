/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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
package org.uberfire.ext.plugin.client.perspective.editor.layout.editor.popups;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.Modal;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorPresenter;
import org.uberfire.ext.layout.editor.client.api.ModalConfigurationContext;
import org.uberfire.ext.plugin.client.perspective.editor.layout.editor.HTMLLayoutDragComponent;

@Dependent
public class EditHTMLPresenter {

    private View view;
    private ModalConfigurationContext modalConfigurationContext;
    private HtmlEditorPresenter htmlEditor;

    @Inject
    public EditHTMLPresenter(final View view,
                             final HtmlEditorPresenter htmlEditor) {
        this.view = view;
        this.htmlEditor = htmlEditor;
    }

    public void init(final ModalConfigurationContext modalConfigurationContext) {
        this.modalConfigurationContext = modalConfigurationContext;
        setupHTMLEditor();
        view.init(this);
    }

    public void show() {
        view.show();
    }

    void setupHTMLEditor() {
        String html = modalConfigurationContext.getComponentProperty(HTMLLayoutDragComponent.HTML_CODE_PARAMETER);

        if (html == null || html.isEmpty()) {
            html = view.getHtmlEditorPlaceHolder();
        }

        htmlEditor.setContent(html);
        htmlEditor.load();
    }

    void closeClick() {
        modalConfigurationContext.configurationCancelled();
    }

    void cancelClick() {
        view.hide();
        destroyHtmlEditor();
        modalConfigurationContext.configurationCancelled();
    }

    void okClick() {
        view.hide();
        modalConfigurationContext.setComponentProperty(HTMLLayoutDragComponent.HTML_CODE_PARAMETER,
                                                       htmlEditor.getContent());
        destroyHtmlEditor();
        modalConfigurationContext.configurationFinished();
    }

    public void destroyHtmlEditor() {
        htmlEditor.destroy();
    }

    public HtmlEditorPresenter.View getHtmlEditorView() {
        return htmlEditor.getView();
    }

    public ModalConfigurationContext getModalConfigurationContext() {
        return this.modalConfigurationContext;
    }

    public View getView() {
        return view;
    }

    public interface View extends UberElement<EditHTMLPresenter> {

        void show();

        void hide();

        String getHtmlEditorPlaceHolder();

        Modal getModal();
    }
}
