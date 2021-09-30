/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.displayer.client.widgets.sourcecode;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import org.dashbuilder.displayer.client.resources.i18n.SourceCodeEditorConstants;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Node;
import org.jboss.errai.common.client.dom.NodeList;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.common.client.dom.UnorderedList;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.ext.widgets.common.client.ace.AceEditor;
import org.uberfire.ext.widgets.common.client.ace.AceEditorMode;
import org.uberfire.ext.widgets.common.client.ace.AceEditorTheme;

@Dependent
@Templated
public class SourceCodeEditorView extends Composite
        implements SourceCodeEditor.View {

    SourceCodeEditor presenter;
    boolean eventsEnabled = true;

    @Inject
    @DataField
    Div mainDiv;

    @Inject
    @DataField
    AceEditor aceEditor;

    @Inject
    @DataField
    Span errorLabel;

    @Inject
    @DataField
    Button addVariableButton;

    @Inject
    @DataField
    UnorderedList variablesMenu;

    @Override
    public void init(SourceCodeEditor presenter) {
        this.presenter = presenter;
        aceEditor.startEditor();
        aceEditor.setTheme(AceEditorTheme.CHROME);
        aceEditor.setReadOnly(false);
        aceEditor.addOnChangeHandler(this::onEditorChange);
        aceEditor.setAutocompleteEnabled(true);
        addVariableButton.setTitle(SourceCodeEditorConstants.INSTANCE.add_variable());
    }

    protected void onEditorChange(JavaScriptObject obj) {
        if (eventsEnabled && !presenter.onSourceCodeChanged(aceEditor.getText())) {
            aceEditor.setFocus();
        }
    }

    @Override
    public void clearAll() {
        clearError();
        removeAllChildren(variablesMenu);
        aceEditor.setFocus();
    }

    @Override
    public void edit(SourceCodeType type, String code) {
        switch (type) {
            case HTML:
                aceEditor.setMode(AceEditorMode.HTML);
                break;
            case JAVASCRIPT:
                aceEditor.setMode(AceEditorMode.JAVASCRIPT);
                break;
        }

        eventsEnabled = false;
        aceEditor.setText(code != null ? code : "");
        aceEditor.setFocus();
        eventsEnabled = true;
    }

    @Override
    public void focus() {
        aceEditor.setFocus();
    }

    @Override
    public void error(String error) {
        mainDiv.setClassName("form-group has-error");
        errorLabel.setInnerHTML(error);
    }

    @Override
    public void clearError() {
        mainDiv.setClassName("form-group");
        errorLabel.setInnerHTML("");
    }

    @Override
    public void declareVariable(String var, String description) {

        SpanElement span = Document.get().createSpanElement();
        span.setInnerText(var);

        AnchorElement anchor = Document.get().createAnchorElement();
        anchor.setTitle(description);
        anchor.appendChild(span);

        LIElement li = Document.get().createLIElement();
        li.getStyle().setCursor(Style.Cursor.POINTER);
        li.appendChild(anchor);

        variablesMenu.appendChild((Node) li);

        Event.sinkEvents(anchor, Event.ONCLICK);
        Event.setEventListener(anchor, event -> {
            if(Event.ONCLICK == event.getTypeInt()) {
                presenter.onVariableSelected(var);
            }
        });
    }

    @Override
    public void injectVariable(String var) {
        aceEditor.insertAtCursor(var);
    }

    private void removeAllChildren(org.jboss.errai.common.client.dom.Element element) {
        NodeList nodeList = element.getChildNodes();
        int lenght = nodeList.getLength();
        for (int i=0; i<lenght; i++) {
            element.removeChild(nodeList.item(0));
        }
    }
}
