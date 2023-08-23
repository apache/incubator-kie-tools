/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.util;

import jsinterop.annotations.JsIgnore;
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

    @JsIgnore
    public static void registerExpressionEditorView(final ExpressionEditorViewImpl expressionEditor) {
        ExpressionEditorService.expressionEditorView = expressionEditor;
    }

    /**
     * Given a logic type, it returns its default Expression definition based on the current Expression.
     * It supports both root and nested expressions.
     * @param logicType The selected logicType (see. ExpressionType.java)
     * @param dataType The expression dataType
     * @return The default Expression definition as ExpressionProps
     */
    @JsMethod
    public static ExpressionProps getDefaultExpressionDefinition(final String logicType, final String dataType) {
        return expressionEditorView.getDefaultExpressionDefinition(logicType, dataType);
    }

    /**
     * It opens the "Data Type" tab page.
     */
    @JsMethod
    public static void openDataTypePage() {
        expressionEditorView.openDataTypePage();
    }

    @JsMethod
    public static void selectObject(final String uuid) {
        expressionEditorView.selectDomainObject(uuid);
    }

    /**
     * It updates the expression modified by the React Layer. It **MUTATES** the GWT layer status.
     * @param expressionProps The updated expression
     */
    @JsMethod
    public static void updateExpression(final ExpressionProps expressionProps) {
        expressionEditorView.updateExpression(expressionProps);
    }
}
