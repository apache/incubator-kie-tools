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
package org.kie.workbench.common.dmn.client.editors.expressions;

import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Document;
import org.jboss.errai.common.client.dom.Option;
import org.jboss.errai.common.client.dom.Select;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.api.definition.v1_1.Expression;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType;

@Templated
@Dependent
public class ExpressionEditorViewImpl implements ExpressionEditorView {

    private ExpressionEditorView.Presenter presenter;

    @DataField("exitButton")
    private Div exitButton;

    @DataField("expressionEditor")
    private Div expressionEditor;

    @DataField("expressionEditorDefinition")
    private Select expressionEditorDefinition;

    private Document document;

    private TranslationService ts;

    public ExpressionEditorViewImpl() {
        //CDI proxy
    }

    @Inject
    public ExpressionEditorViewImpl(final Div exitButton,
                                    final Div expressionEditor,
                                    final Select expressionEditorDefinition,
                                    final Document document,
                                    final TranslationService ts) {
        this.exitButton = exitButton;
        this.expressionEditorDefinition = expressionEditorDefinition;
        this.expressionEditor = expressionEditor;
        this.document = document;
        this.ts = ts;
    }

    @Override
    public void init(final ExpressionEditorView.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setExpressionEditorTypes(final List<ExpressionEditorDefinition<Expression>> expressionEditorDefinitions) {
        expressionEditorDefinitions.forEach(t -> expressionEditorDefinition.add(makeExpressionDefinitionWidget(t)));
    }

    @Override
    public void selectExpressionEditorType(final ExpressionType type) {
        expressionEditorDefinition.setSelectedIndex(type.ordinal());
    }

    @Override
    public void setSubEditor(final IsElement editor) {
        DOMUtil.removeAllChildren(expressionEditor);
        expressionEditor.appendChild(editor.getElement());
    }

    @SuppressWarnings("unchecked")
    private Option makeExpressionDefinitionWidget(final ExpressionEditorDefinition<? extends Expression> definition) {
        final Option o = (Option) document.createElement("option");
        o.setValue(definition.getType().name());
        o.setText(definition.getName());
        return o;
    }

    @SuppressWarnings("unused")
    @EventHandler("exitButton")
    void onClickExitButton(final ClickEvent event) {
        presenter.exit();
    }

    @SuppressWarnings("unused")
    @EventHandler("expressionEditorDefinition")
    void onExpressionTypeSelectionChange(final ChangeEvent event) {
        final Option o = (Option) expressionEditorDefinition.getOptions().item(expressionEditorDefinition.getSelectedIndex());
        final String type = o.getValue();
        presenter.onExpressionTypeChanged(ExpressionType.valueOf(type));
    }
}
