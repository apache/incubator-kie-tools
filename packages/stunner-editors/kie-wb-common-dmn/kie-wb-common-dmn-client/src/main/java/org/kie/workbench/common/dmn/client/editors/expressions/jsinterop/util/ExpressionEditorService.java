/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.util;

import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import org.kie.workbench.common.dmn.client.editors.expressions.ExpressionEditorViewImpl;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.ExpressionProps;

/**
 * Entrypoint for requests coming from the React/TS layer. MUST be synchronized with BeeAPI (React/TS)
 * Be careful with the method that explicitly mutates the GTW layer status (marked in method description)
 */
@JsType(namespace = JsPackage.GLOBAL, name = "beeApiWrapper")
public class ExpressionEditorService {

    private static ExpressionEditorViewImpl expressionEditorView;

    public static void registerExpressionEditorView(final ExpressionEditorViewImpl expressionEditor) {
        ExpressionEditorService.expressionEditorView = expressionEditor;
    }

    /**
     * It opens the "Data Type" tab page.
     */
    @JsMethod
    public static void openDataTypePage() {
        expressionEditorView.openDataTypePage();
    }

    @JsMethod
    public static void onLogicTypeSelect(final String selectedLogicType) {
        expressionEditorView.onLogicTypeSelect(selectedLogicType);
    }

    @JsMethod
    public static void selectObject(final String uuid) {
        expressionEditorView.selectDomainObject(uuid);
    }

    /**
     * It updates the expression modified by the React Layer. It MUTATES the GWT layer status.
     * @param expressionProps The updated expression
     */
    @JsMethod
    public static void updateExpression(final ExpressionProps expressionProps) {
        expressionEditorView.updateExpression(expressionProps);
    }
}
