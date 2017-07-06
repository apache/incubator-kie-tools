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

package org.drools.workbench.screens.guided.dtable.client.wizard.column;

import java.util.ArrayList;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableErraiConstants;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.SummaryPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons.DecisionTableColumnPlugin;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.widgets.core.client.wizards.WizardPage;
import org.uberfire.ext.widgets.core.client.wizards.WizardView;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class NewGuidedDecisionTableColumnWizardTest {

    @Mock
    private List<WizardPage> pages;

    @Mock
    private DecisionTableColumnPlugin plugin;

    @Mock
    private SummaryPage summaryPage;

    @Mock
    private WizardView view;

    @Mock
    private TranslationService translationService;

    private NewGuidedDecisionTableColumnWizard wizard;

    @Before
    public void setup() {
        wizard = spy(new NewGuidedDecisionTableColumnWizard(view,
                                                            summaryPage,
                                                            translationService));

        when(wizard.getPages()).thenReturn(pages);
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

        doNothing().when(wizard).parentStart();
        doReturn(title).when(translationService).format(GuidedDecisionTableErraiConstants.NewGuidedDecisionTableColumnWizard_AddNewColumn);
        doReturn(true).when(plugin).isNewColumn();

        wizard.setupTitle(plugin);

        verify(wizard).setTitle(title);
    }

    @Test
    public void testSetupTitleWhenColumnIsNotNew() {
        final String title = "title";

        doNothing().when(wizard).parentStart();
        doReturn(title).when(translationService).format(GuidedDecisionTableErraiConstants.NewGuidedDecisionTableColumnWizard_EditColumn);
        doReturn(false).when(plugin).isNewColumn();

        wizard.setupTitle(plugin);

        verify(wizard).setTitle(title);
    }
}
