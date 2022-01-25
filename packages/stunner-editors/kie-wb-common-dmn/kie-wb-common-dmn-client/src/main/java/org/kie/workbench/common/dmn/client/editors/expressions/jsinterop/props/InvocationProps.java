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

package org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props;

import jsinterop.annotations.JsType;

import static org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType.INVOCATION;

@JsType
public class InvocationProps extends ExpressionProps{
    public final String invokedFunction;
    public final ContextEntryProps[] bindingEntries;
    public final Double entryInfoWidth;
    public final Double entryExpressionWidth;

    public InvocationProps(final String id, final String name, final String dataType, final String invokedFunction, final ContextEntryProps[] bindingEntries, final Double entryInfoWidth, final Double entryExpressionWidth) {
        super(id, name, dataType, INVOCATION.getText());
        this.invokedFunction = invokedFunction;
        this.bindingEntries = bindingEntries;
        this.entryInfoWidth = entryInfoWidth;
        this.entryExpressionWidth = entryExpressionWidth;
    }
}
