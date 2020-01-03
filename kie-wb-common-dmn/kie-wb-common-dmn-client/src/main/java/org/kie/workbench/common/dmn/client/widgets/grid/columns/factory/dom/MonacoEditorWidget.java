/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.dom;

import java.util.Optional;

import com.google.gwt.user.client.DOM;
import org.gwtbootstrap3.client.ui.base.TextBoxBase;
import org.uberfire.client.views.pfly.monaco.jsinterop.MonacoStandaloneCodeEditor;

public class MonacoEditorWidget extends TextBoxBase {

    private MonacoStandaloneCodeEditor codeEditor;

    public MonacoEditorWidget() {
        super(DOM.createDiv());
    }

    public void setCodeEditor(final MonacoStandaloneCodeEditor codeEditor) {
        this.codeEditor = codeEditor;
    }

    public void setValue(final String value) {
        getCodeEditor().ifPresent(c -> c.setValue(value));
    }

    @Override
    public String getValue() {
        return getCodeEditor()
                .map(editor -> editor.getValue())
                .orElse("");
    }

    @Override
    public void setFocus(final boolean focused) {
        getCodeEditor().ifPresent(c -> {
            if (focused) {
                c.focus();
            }
            // IStandaloneCodeEditor(codeEditor) supports focus, but does not support blur.
            // https://microsoft.github.io/monaco-editor/api/interfaces/monaco.editor.istandalonecodeeditor.html
        });
    }

    public Optional<MonacoStandaloneCodeEditor> getCodeEditor() {
        return Optional.ofNullable(codeEditor);
    }

    @Override
    public void setTabIndex(final int index) {
        // IStandaloneCodeEditor(codeEditor) does not support tab index.
        // https://microsoft.github.io/monaco-editor/api/interfaces/monaco.editor.istandalonecodeeditor.html
    }
}
