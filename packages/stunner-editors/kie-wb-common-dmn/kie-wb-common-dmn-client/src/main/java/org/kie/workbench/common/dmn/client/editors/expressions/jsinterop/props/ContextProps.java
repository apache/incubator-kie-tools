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

package org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props;

import jsinterop.annotations.JsType;

import static org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType.CONTEXT;

@JsType
public class ContextProps extends ExpressionProps {
    public final ContextEntryProps[] contextEntries;
    public final ExpressionProps result;
    public final Double entryInfoWidth;
    public final Double entryExpressionWidth;

    public ContextProps(final String id, final String name, final String dataType, final ContextEntryProps[] contextEntries, final ExpressionProps result, final Double entryInfoWidth, final Double entryExpressionWidth) {
        super(id, name, dataType, CONTEXT.getText());
        this.contextEntries = contextEntries;
        this.result = result;
        this.entryInfoWidth = entryInfoWidth;
        this.entryExpressionWidth = entryExpressionWidth;
    }
}
