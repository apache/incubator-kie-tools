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

package org.kie.workbench.common.dmn.client.js;

import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsType;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.DataTypeProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.ExpressionProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.PMMLParam;

@JsType(isNative = true)
public class DMNLoader {

    /**
     * Method used to render the boxed-expression-component (TS/React world) in the DMN Editor (Java/GWT world)
     * @param selector DOM selector, where the component will be placed
     * @param expressionHolderId identifier of the decision node, where the expression will be hold
     * @param expressionProps expression to render
     * @param isClearSupportedOnRootExpression tells if the root expression should have the clear button or not (e.g. functions wrapped in BKM nodes should not)
     * @param pmmlDocuments PMML parameters
     * @param hideDmn14BoxedExpressions hide the DMN 1.4 expressions, due to compatibility reasons
     */
    @JsMethod(namespace = "__KIE__DMN_LOADER__")
    public static native void renderBoxedExpressionEditor(final String selector, final String expressionHolderId, final ExpressionProps expressionProps, final DataTypeProps[] dataTypes, final Boolean isClearSupportedOnRootExpression, final PMMLParam[] pmmlDocuments, final String xml, final Boolean hideDmn14BoxedExpressions);

    /**
     * Method used to unmount the boxed-expression-component (TS/React world) from the DMN Editor (Java/GWT world)
     * @param selector DOM selector, where the component will be removed
     */
    @JsMethod(namespace = "__KIE__DMN_LOADER__")
    public static native void unmountBoxedExpressionEditor(final String selector);

    /**
     * Method used to render the import-java-classes component (TS/React world) in the DMN Editor (Java/GWT world)
     * @param selector DOM selector, where the component will be placed
     */
    @JsMethod(namespace = "__KIE__DMN_LOADER__")
    public static native void renderImportJavaClasses(final String selector);
}
