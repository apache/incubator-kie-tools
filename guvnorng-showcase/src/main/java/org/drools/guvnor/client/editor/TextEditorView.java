/*
 * Copyright 2012 JBoss Inc
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

package org.drools.guvnor.client.editor;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Panel;
import org.drools.guvnor.client.common.ResizableTextArea;

public class TextEditorView extends Composite implements TextEditorPresenter.View {

    @Inject UiBinder<Panel, TextEditorView> uiBinder;
    @UiField public ResizableTextArea fileContent;
    private boolean isDirty = false;

    @PostConstruct
    public void init() {
        initWidget(uiBinder.createAndBindUi(this));

        fileContent.addChangeHandler(new ChangeHandler() {
            public void onChange(ChangeEvent event) {
                isDirty = true;
            }
        });

    }

    public void setContent(final String content) {
        fileContent.setText(content);
    }

    public String getContent() {
        return fileContent.getText();
    }

    @Override public void setFocus() {
        fileContent.setFocus(true);
    }

    @Override public boolean isDirty() {
        return isDirty;
    }

    @Override public void setDirty(boolean dirty) {
        isDirty = dirty;
    }

}