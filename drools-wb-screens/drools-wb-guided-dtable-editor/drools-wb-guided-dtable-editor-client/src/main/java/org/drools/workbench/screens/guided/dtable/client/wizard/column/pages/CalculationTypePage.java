/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.wizard.column.pages;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableErraiConstants;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.common.BaseDecisionTableColumnPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.ConditionColumnPlugin;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.mvp.UberElement;

@Dependent
public class CalculationTypePage extends BaseDecisionTableColumnPage<ConditionColumnPlugin> {

    private View view;

    @Inject
    public CalculationTypePage(final View view,
                               final TranslationService translationService) {
        super(translationService);

        this.view = view;
    }

    @Override
    protected UberElement<?> getView() {
        return view;
    }

    @Override
    public String getTitle() {
        return translate(GuidedDecisionTableErraiConstants.CalculationTypePage_CalculationType);
    }

    @Override
    public void isComplete(final Callback<Boolean> callback) {
        callback.callback(getConstraintValue() != BaseSingleFieldConstraint.TYPE_UNDEFINED);
    }

    @Override
    public void prepareView() {
        view.init(this);
        view.selectConstraintValue(getConstraintValue());
    }

    int getConstraintValue() {
        return plugin().constraintValue();
    }

    void setConstraintValue(final int constraintValue) {
        plugin().setConstraintValue(constraintValue);
    }

    public interface View extends UberElement<CalculationTypePage> {

        void selectConstraintValue(final int constraintValue);
    }
}
