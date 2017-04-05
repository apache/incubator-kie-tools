/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.guided.dtable.client.wizard.column.pages;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ChangeEvent;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.jboss.errai.common.client.dom.Input;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class CalculationTypePageView implements IsElement,
                                                CalculationTypePage.View {

    @DataField("isConstraintValueTypeLiteral")
    Input isConstraintValueTypeLiteral;

    @DataField("isConstraintValueTypeRetValue")
    Input isConstraintValueTypeRetValue;

    @DataField("isConstraintValueTypePredicate")
    Input isConstraintValueTypePredicate;

    private CalculationTypePage page;

    @Inject
    public CalculationTypePageView(final Input isConstraintValueTypeLiteral,
                                   final Input isConstraintValueTypeRetValue,
                                   final Input isConstraintValueTypePredicate) {
        this.isConstraintValueTypeLiteral = isConstraintValueTypeLiteral;
        this.isConstraintValueTypeRetValue = isConstraintValueTypeRetValue;
        this.isConstraintValueTypePredicate = isConstraintValueTypePredicate;
    }

    @EventHandler({"isConstraintValueTypeLiteral", "isConstraintValueTypeRetValue", "isConstraintValueTypePredicate"})
    public void onConstraintValueSelected(ChangeEvent event) {
        page.setConstraintValue(constraintValue());
    }

    @Override
    public void init(final CalculationTypePage page) {
        this.page = page;
    }

    @Override
    public void selectConstraintValue(final int constraintValue) {
        isConstraintValueTypeLiteral.setChecked(false);
        isConstraintValueTypeRetValue.setChecked(false);
        isConstraintValueTypePredicate.setChecked(false);

        switch (constraintValue) {
            case BaseSingleFieldConstraint.TYPE_LITERAL:
                isConstraintValueTypeLiteral.setChecked(true);
                break;
            case BaseSingleFieldConstraint.TYPE_RET_VALUE:
                isConstraintValueTypeRetValue.setChecked(true);
                break;
            case BaseSingleFieldConstraint.TYPE_PREDICATE:
                isConstraintValueTypePredicate.setChecked(true);
                break;
        }
    }

    private int constraintValue() {
        if (isConstraintValueTypeLiteral.getChecked()) {
            return BaseSingleFieldConstraint.TYPE_LITERAL;
        } else if (isConstraintValueTypeRetValue.getChecked()) {
            return BaseSingleFieldConstraint.TYPE_RET_VALUE;
        } else if (isConstraintValueTypePredicate.getChecked()) {
            return BaseSingleFieldConstraint.TYPE_PREDICATE;
        }
        return BaseSingleFieldConstraint.TYPE_UNDEFINED;
    }
}
