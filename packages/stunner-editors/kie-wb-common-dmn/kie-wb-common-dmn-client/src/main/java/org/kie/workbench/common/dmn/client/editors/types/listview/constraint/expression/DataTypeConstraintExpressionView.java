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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.expression;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.HTMLTextAreaElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
@Dependent
public class DataTypeConstraintExpressionView implements DataTypeConstraintExpression.View {

    @DataField("expression")
    private final HTMLTextAreaElement expression;

    private DataTypeConstraintExpression presenter;

    @Inject
    public DataTypeConstraintExpressionView(final HTMLTextAreaElement expression) {
        this.expression = expression;
    }

    @Override
    public void init(final DataTypeConstraintExpression presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setPlaceholder(final String placeholder) {
        expression.setAttribute("placeholder", placeholder);
    }

    @Override
    public String getExpressionValue() {
        return expression.value;
    }

    @Override
    public void setExpressionValue(final String value) {
        expression.value = value;
    }
}
