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

@JsType(namespace = JsPackage.GLOBAL, name = "beeApiWrapper")
public class BoxedExpressionService {

    private static ExpressionEditorViewImpl expressionEditor;

    public static void registerBroadcastForExpression(final ExpressionEditorViewImpl expressionEditor) {
        BoxedExpressionService.expressionEditor = expressionEditor;
    }

    @JsMethod
    public static void resetExpressionDefinition(final ExpressionProps expressionProps) {
        expressionEditor.resetExpressionDefinition(expressionProps);
    }

    @JsMethod
    public static void updateExpression(final ExpressionProps expressionProps) {
        expressionEditor.updateExpression(expressionProps);
    }

    @JsMethod
    public static void openManageDataType() {
        expressionEditor.openManageDataType();
    }

    @JsMethod
    public static void onLogicTypeSelect(final String selectedLogicType) {
        expressionEditor.onLogicTypeSelect(selectedLogicType);
    }

    @JsMethod
    public static void selectObject(final String uuid) {
        expressionEditor.selectDomainObject(uuid);
    }
}
