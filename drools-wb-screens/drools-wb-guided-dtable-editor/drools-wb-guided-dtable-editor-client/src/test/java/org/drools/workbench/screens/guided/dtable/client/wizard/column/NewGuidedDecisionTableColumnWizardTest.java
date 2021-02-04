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

package org.drools.workbench.screens.guided.dtable.client.wizard.column;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableErraiConstants;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.commons.HasDefaultValuesPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.commons.HasPatternPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.AdditionalInfoPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.OperatorPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.PatternPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.SummaryPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.common.DecisionTablePopoverUtils;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.ActionRetractFactPlugin;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.ActionSetFactPlugin;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.ActionWorkItemPlugin;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.ActionWorkItemSetFieldPlugin;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.BRLActionColumnPlugin;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.BRLConditionColumnPlugin;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.ConditionColumnPlugin;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons.DecisionTableColumnPlugin;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.widgets.core.client.wizards.WizardPage;
import org.uberfire.ext.widgets.core.client.wizards.WizardView;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class NewGuidedDecisionTableColumnWizardTest {

    private List<WizardPage> pages;

    @Mock(extraInterfaces = {HasDefaultValuesPage.class, HasPatternPage.class})
    private ConditionColumnPlugin plugin;

    @Mock
    private ActionWorkItemSetFieldPlugin actionWorkItemSetFieldPlugin;

    @Mock
    private ActionSetFactPlugin actionSetFactPlugin;

    @Mock
    private ActionRetractFactPlugin actionRetractFactPlugin;

    @Mock
    private ActionWorkItemPlugin actionWorkItemPlugin;

    @Mock
    private BRLActionColumnPlugin brlActionColumnPlugin;

    @Mock
    private BRLConditionColumnPlugin brlConditionColumnPlugin;

    @Mock
    private ConditionColumnPlugin conditionColumnPlugin;

    @Mock
    private SummaryPage summaryPage;

    @Mock
    private PatternPage patternPage;

    @Mock
    private AdditionalInfoPage additionalInfoPage;

    @Mock
    private OperatorPage operatorPage;

    @Mock
    private WizardView view;

    @Mock
    private TranslationService translationService;

    @Mock
    private DecisionTablePopoverUtils popoverUtils;

    private NewGuidedDecisionTableColumnWizard wizard;

    @Before
    public void setup() {
        wizard = spy(new NewGuidedDecisionTableColumnWizard(view,
                                                            summaryPage,
                                                            translationService,
                                                            popoverUtils));

        pages = spy(new ArrayList<>());
        wizard.setPages(pages);
    }

    @Test
    public void testLoadPagesWhenTheColumnIsNew() {
        final ArrayList<WizardPage> wizardPages = new ArrayList<>();

        when(plugin.getPages()).thenReturn(wizardPages);
        when(plugin.isNewColumn()).thenReturn(Boolean.TRUE);

        wizard.loadPages(plugin);

        verify(pages).clear();
        verify(pages).addAll(wizardPages);
        verify(pages).add(summaryPage);
    }

    @Test
    public void testLoadPagesWhenTheColumnIsNotNew() {
        final ArrayList<WizardPage> wizardPages = new ArrayList<>();

        when(plugin.getPages()).thenReturn(wizardPages);
        when(plugin.isNewColumn()).thenReturn(Boolean.FALSE);

        wizard.loadPages(plugin);

        verify(pages).clear();
        verify(pages).addAll(wizardPages);
        verify(pages,
               never()).add(summaryPage);
    }

    @Test
    public void testStart() {
        doNothing().when(wizard).parentStart();

        wizard.start(plugin);

        verify(plugin).init(wizard);
        verify(wizard).setupTitle(plugin);
        verify(wizard).loadPages(plugin);
        verify(wizard).initPages(plugin);
    }

    @Test
    public void testSetupTitleWhenColumnIsNew() {
        final String title = "title";

        doReturn(title).when(translationService).format(GuidedDecisionTableErraiConstants.NewGuidedDecisionTableColumnWizard_AddNewColumn);
        doReturn(true).when(plugin).isNewColumn();

        wizard.setupTitle(plugin);

        verify(wizard).setTitle(title);
    }

    @Test
    public void testSetupTitleWhenColumnIsNotNew() {
        final String title = "title";

        doReturn(title).when(translationService).format(GuidedDecisionTableErraiConstants.NewGuidedDecisionTableColumnWizard_EditColumn);
        doReturn(false).when(plugin).isNewColumn();

        wizard.setupTitle(plugin);

        verify(wizard).setTitle(title);
    }

    @Test
    public void testIfIsCompleteDoNotCheckIfColumnIsNewOrEdited() {
        doNothing().when(wizard).parentStart();

        List<WizardPage> wizardPages = Arrays.asList(patternPage,
                                                     operatorPage,
                                                     additionalInfoPage);

        when(plugin.getPages()).thenReturn(wizardPages);

        wizard.start(plugin);

        verify(plugin,
               times(2)).isNewColumn();

        wizard.isComplete(mock(Callback.class));

        verify(plugin,
               times(2)).isNewColumn();

        wizardPages.forEach(page -> verify(page).isComplete(any()));
    }

    @Test
    public void testWizardCompleteActionWorkItemSetFieldPlugin() {
        testCompleteWizard(actionWorkItemSetFieldPlugin);
    }

    @Test
    public void testWizardCompleteActionSetFactPlugin() {
        testCompleteWizard(actionSetFactPlugin);
    }

    @Test
    public void testCompleteWizardActionRetractFactPlugin() {
        testCompleteWizard(actionRetractFactPlugin);
    }

    @Test
    public void testCompleteWizardActionWorkItemPlugin() {
        testCompleteWizard(actionWorkItemPlugin);
    }

    @Test
    public void testCompleteWizardBRLActionColumnPlugin() {
        testCompleteWizard(brlActionColumnPlugin);
    }

    @Test
    public void testCompleteWizardBRLConditionColumnPlugin() {
        testCompleteWizard(brlConditionColumnPlugin);
    }

    @Test
    public void testCompleteWizardConditionColumnPlugin() {
        testCompleteWizard(conditionColumnPlugin);
    }

    @Test
    public void testClosureDestroysPopovers() {
        wizard.close();

        verify(popoverUtils).destroyPopovers();
    }

    private void testCompleteWizard(DecisionTableColumnPlugin plugin) {
        doCallRealMethod().when(plugin).init(wizard);
        wizard.start(plugin);

        wizard.complete();

        verify(plugin).generateColumn();
    }
}
