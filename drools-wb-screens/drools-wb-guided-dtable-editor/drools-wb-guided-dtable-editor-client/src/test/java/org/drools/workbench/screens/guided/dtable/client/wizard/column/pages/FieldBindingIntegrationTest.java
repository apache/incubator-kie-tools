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

import javax.enterprise.event.Event;

import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTablePresenter;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.NewGuidedDecisionTableColumnWizard;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.common.DecisionTablePopoverUtils;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.ConditionColumnPlugin;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.ext.widgets.core.client.wizards.WizardPage;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;
import org.uberfire.ext.widgets.core.client.wizards.WizardView;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub(RootPanel.class)
public class FieldBindingIntegrationTest {

    @Mock
    private TranslationService translationService;

    @Mock
    private AsyncPackageDataModelOracle oracle;

    @Mock
    private PatternPage<ConditionColumnPlugin> patternPage;

    @Mock
    private CalculationTypePage calculationTypePage;

    @Mock
    private FieldPage<ConditionColumnPlugin> fieldPage;

    @Mock
    private OperatorPage operatorPage;

    @Mock
    private ValueOptionsPageView valueOptionsPageView;

    @Spy
    @InjectMocks
    private ValueOptionsPage<ConditionColumnPlugin> valueOptionsPage = new ValueOptionsPage<>(valueOptionsPageView, translationService);

    @Mock
    private AdditionalInfoPage<ConditionColumnPlugin> additionalInfoPage;

    @Mock
    private SummaryPage summaryPage;

    private Event<WizardPageStatusChangeEvent> changeEvent = new EventSourceMock<>();

    @Mock
    private ValueOptionsPageView view;

    @Mock
    private WizardView wizardView;

    private NewGuidedDecisionTableColumnWizard wizard;

    @Mock
    private GuidedDecisionTablePresenter presenter;

    private GuidedDecisionTable52 model;

    private ConditionColumnPlugin plugin;

    @Before
    public void setup() {
        wizard = new NewGuidedDecisionTableColumnWizard(wizardView, summaryPage,
                                                        translationService, new DecisionTablePopoverUtils());
        wizard.init(presenter);

        model = new GuidedDecisionTable52();
        when(presenter.getModel()).thenReturn(model);
        when(presenter.getDataModelOracle()).thenReturn(oracle);

        plugin = spy(new ConditionColumnPlugin(patternPage, calculationTypePage, fieldPage, operatorPage,
                                               valueOptionsPage, additionalInfoPage, changeEvent, translationService));
        doNothing().when(plugin).fireChangeEvent(any(WizardPage.class));
        wizard.start(plugin);
    }

    @Test
    public void testFieldBindingLiteralCalculationType() throws Exception {
        plugin.setConstraintValue(BaseSingleFieldConstraint.TYPE_LITERAL);

        valueOptionsPage.prepareView();
        assertTrue(valueOptionsPage.canSetupBinding());
    }

    @Test
    public void testFieldBindingFormulaCalculationType() throws Exception {
        plugin.setConstraintValue(BaseSingleFieldConstraint.TYPE_RET_VALUE);

        valueOptionsPage.prepareView();
        assertTrue(valueOptionsPage.canSetupBinding());
    }

    @Test
    public void testFieldBindingPredicateCalculationType() throws Exception {
        plugin.setConstraintValue(BaseSingleFieldConstraint.TYPE_PREDICATE);

        valueOptionsPage.prepareView();
        assertFalse(valueOptionsPage.canSetupBinding());
    }
}
