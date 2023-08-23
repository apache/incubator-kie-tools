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

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.Element;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.ConstraintPlaceholderHelper;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.DataTypeConstraintComponent;
import org.uberfire.client.mvp.UberElemental;

@Dependent
public class DataTypeConstraintExpression implements DataTypeConstraintComponent {

    private final View view;

    private final ConstraintPlaceholderHelper placeholderHelper;

    @Inject
    public DataTypeConstraintExpression(final View view,
                                        final ConstraintPlaceholderHelper placeholderHelper) {
        this.view = view;
        this.placeholderHelper = placeholderHelper;
    }

    @PostConstruct
    void setup() {
        view.init(this);
    }

    @Override
    public String getValue() {
        return view.getExpressionValue();
    }

    @Override
    public void setValue(final String value) {
        view.setExpressionValue(value);
    }

    @Override
    public void setConstraintValueType(final String constraintValueType) {
        view.setPlaceholder(placeholderHelper.getPlaceholderSentence(constraintValueType));
    }

    @Override
    public Element getElement() {
        return view.getElement();
    }

    public interface View extends UberElemental<DataTypeConstraintExpression>,
                                  IsElement {

        void setPlaceholder(final String constraintValueType);

        void setExpressionValue(final String value);

        String getExpressionValue();
    }
}
