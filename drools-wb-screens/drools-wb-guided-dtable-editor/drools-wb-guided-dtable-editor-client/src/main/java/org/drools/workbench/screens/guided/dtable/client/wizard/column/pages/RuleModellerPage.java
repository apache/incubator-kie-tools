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

import com.google.gwt.event.shared.EventBus;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableErraiConstants;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.commons.HasRuleModellerPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.common.BaseDecisionTableColumnPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.BRLActionColumnPlugin;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons.DecisionTableColumnPlugin;
import org.drools.workbench.screens.guided.rule.client.editor.RuleModeller;
import org.drools.workbench.screens.guided.rule.client.editor.RuleModellerConfiguration;
import org.drools.workbench.screens.guided.rule.client.editor.RuleModellerWidgetFactory;
import org.drools.workbench.screens.guided.template.client.editor.TemplateModellerWidgetFactory;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.mvp.UberElement;

@Dependent
public class RuleModellerPage<T extends HasRuleModellerPage & DecisionTableColumnPlugin> extends BaseDecisionTableColumnPage<T> {

    private View view;

    private RuleModeller ruleModeller;

    @Inject
    public RuleModellerPage(final View view,
                            final TranslationService translationService) {
        super(translationService);

        this.view = view;
    }

    @Override
    public String getTitle() {
        return translate(GuidedDecisionTableErraiConstants.RuleModellerPage_RuleModeller);
    }

    @Override
    public void isComplete(final Callback<Boolean> callback) {
        callback.callback(plugin().isRuleModellerPageCompleted());
    }

    @Override
    protected UberElement<?> getView() {
        return view;
    }

    @Override
    public void prepareView() {
        view.init(this);

        markAsViewed();
        setupRuleModeller();
    }

    private void setupRuleModeller() {
        view.setupRuleModellerWidget(ruleModeller());
        view.setRuleModelerDescription(plugin().getRuleModellerDescription());
    }

    RuleModeller ruleModeller() {
        if (ruleModeller == null) {
            ruleModeller = newRuleModeller();
        }
        return ruleModeller;
    }

    private RuleModeller newRuleModeller() {
        final RuleModeller ruleModeller = new RuleModeller(ruleModel(),
                                                           oracle(),
                                                           widgetFactory(),
                                                           configuration(),
                                                           eventBus(),
                                                           isReadOnly());

        presenter.getPackageParentRuleNames(ruleModeller::setRuleNamesForPackage);

        return ruleModeller;
    }

    private boolean isReadOnly() {
        return presenter.isReadOnly();
    }

    private EventBus eventBus() {
        return presenter.getEventBus();
    }

    private AsyncPackageDataModelOracle oracle() {
        return presenter.getDataModelOracle();
    }

    private RuleModel ruleModel() {
        return plugin().getRuleModel();
    }

    RuleModellerConfiguration configuration() {
        return plugin().getRuleModellerConfiguration();
    }

    private RuleModellerWidgetFactory widgetFactory() {
        final GuidedDecisionTable52.TableFormat tableFormat = plugin().tableFormat();

        switch (tableFormat) {
            case EXTENDED_ENTRY:
                return new TemplateModellerWidgetFactory();
            case LIMITED_ENTRY:
                return new RuleModellerWidgetFactory();
            default:
                throw new UnsupportedOperationException("Unsupported table format: " + tableFormat);
        }
    }

    void markAsViewed() {
        plugin().setRuleModellerPageAsCompleted();
    }

    public interface View extends UberElement<RuleModellerPage> {

        void setupRuleModellerWidget(RuleModeller ruleModeller);

        void setRuleModelerDescription(String description);
    }
}
