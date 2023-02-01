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
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.ContextProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.DecisionTableProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.ExpressionProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.FunctionProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.InvocationProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.ListProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.LiteralProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.RelationProps;

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
    public static void broadcastLiteralExpressionDefinition(final LiteralProps literalProps) {
        expressionEditor.broadcastLiteralExpressionDefinition(literalProps);
    }

    @JsMethod
    public static void broadcastContextExpressionDefinition(final ContextProps contextProps) {
        expressionEditor.broadcastContextExpressionDefinition(contextProps);
    }

    @JsMethod
    public static void broadcastRelationExpressionDefinition(final RelationProps relationProps) {
        expressionEditor.broadcastRelationExpressionDefinition(relationProps);
    }

    @JsMethod
    public static void broadcastListExpressionDefinition(final ListProps listProps) {
        expressionEditor.broadcastListExpressionDefinition(listProps);
    }

    @JsMethod
    public static void broadcastInvocationExpressionDefinition(final InvocationProps invocationProps) {
        expressionEditor.broadcastInvocationExpressionDefinition(invocationProps);
    }

    @JsMethod
    public static void broadcastFunctionExpressionDefinition(final FunctionProps functionProps) {
        expressionEditor.broadcastFunctionExpressionDefinition(functionProps);
    }

    @JsMethod
    public static void broadcastDecisionTableExpressionDefinition(final DecisionTableProps decisionTableProps) {
        expressionEditor.broadcastDecisionTableExpressionDefinition(decisionTableProps);
    }

    /**
     * It creates an UNDO action in the GWT layer with the current expression status.
     * This must be called from the REACT layer just before *ANY* user action we want to be undoable.
     * GWT layer needs to store the expression status before the user change, for this reason this must be called
     * BEFORE ANY change is actually persisted in the GWT layer.
     */
    @JsMethod
    public static void createUndoCommand() {
        expressionEditor.createUndoCommand();
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
