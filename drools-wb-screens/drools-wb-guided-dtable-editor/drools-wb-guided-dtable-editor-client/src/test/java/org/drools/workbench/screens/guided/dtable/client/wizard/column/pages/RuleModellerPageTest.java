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

import com.google.gwt.junit.GWTMockUtilities;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableErraiConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.BRLActionColumnPlugin;
import org.drools.workbench.screens.guided.rule.client.editor.RuleModeller;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub(RuleModeller.class)
public class RuleModellerPageTest {

    @Mock
    private BRLActionColumnPlugin plugin;

    @Mock
    private RuleModellerPage.View view;

    @Mock
    private SimplePanel content;

    @Mock
    private TranslationService translationService;

    @Mock
    private GuidedDecisionTableView.Presenter presenter;

    @InjectMocks
    private RuleModellerPage<BRLActionColumnPlugin> page = spy(new RuleModellerPage<BRLActionColumnPlugin>(view,
                                                                                                           translationService));

    @BeforeClass
    public static void setupPreferences() {
        // Prevent runtime GWT.create() error at 'content = new SimplePanel()'
        GWTMockUtilities.disarm();
    }

    @Before
    public void setup() {
        when(page.plugin()).thenReturn(plugin);
    }

    @Test
    public void testIsCompleteWhenRuleModellerPageIsCompleted() throws Exception {
        when(plugin.isRuleModellerPageCompleted()).thenReturn(true);

        page.isComplete(Assert::assertTrue);
    }

    @Test
    public void testIsCompleteWhenRuleModellerPageIsNotCompleted() throws Exception {
        when(plugin.isRuleModellerPageCompleted()).thenReturn(false);

        page.isComplete(Assert::assertFalse);
    }

    @Test
    public void testRuleModeller() throws Exception {
        when(plugin.tableFormat()).thenReturn(GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY);
        when(presenter.getDataModelOracle()).thenReturn(mock(AsyncPackageDataModelOracle.class));

        final RuleModeller ruleModeller = page.ruleModeller();

        assertNotNull(ruleModeller);
    }

    @Test
    public void testGetTitle() throws Exception {
        final String errorKey = GuidedDecisionTableErraiConstants.RuleModellerPage_RuleModeller;
        final String errorMessage = "Title";

        when(translationService.format(errorKey)).thenReturn(errorMessage);

        final String title = page.getTitle();

        assertEquals(errorMessage,
                     title);
    }

    @Test
    public void testPrepareView() throws Exception {
        when(plugin.tableFormat()).thenReturn(GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY);

        page.prepareView();

        verify(view).init(page);
        verify(page).markAsViewed();
    }

    @Test
    public void testAsWidget() {
        final Widget contentWidget = page.asWidget();

        assertEquals(contentWidget,
                     content);
    }
}
