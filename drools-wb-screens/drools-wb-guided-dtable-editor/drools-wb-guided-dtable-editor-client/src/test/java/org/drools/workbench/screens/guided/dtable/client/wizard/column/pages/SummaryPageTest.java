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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gwt.junit.GWTMockUtilities;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableErraiConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.NewGuidedDecisionTableColumnWizard;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.ConditionColumnPlugin;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons.BaseDecisionTableColumnPlugin;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons.DecisionTableColumnPlugin;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class SummaryPageTest {

    @Mock
    private ConditionColumnPlugin plugin;

    @Mock
    private SummaryPage.View view;

    @Mock
    private SimplePanel content;

    @Mock
    private TranslationService translationService;

    @Mock
    private NewGuidedDecisionTableColumnWizard wizard;

    @Mock
    private GuidedDecisionTableView.Presenter presenter;

    @Mock
    private ManagedInstance<DecisionTableColumnPlugin> pluginManagedInstance;

    @InjectMocks
    private SummaryPage page = spy(new SummaryPage(pluginManagedInstance,
                                                   view,
                                                   translationService));

    @BeforeClass
    public static void setupPreferences() {
        final Map<String, String> preferences = new HashMap<String, String>() {{
            put(ApplicationPreferences.DATE_FORMAT,
                "dd/mm/yyyy");
        }};
        ApplicationPreferences.setUp(preferences);

        // Prevent runtime GWT.create() error at 'content = new SimplePanel()'
        GWTMockUtilities.disarm();
    }

    @Before
    public void setup() {
        when(page.plugin()).thenReturn(plugin);
    }

    @Test
    public void testIsComplete() {
        page.isComplete(Assert::assertTrue);
    }

    @Test
    public void testOpenPageWhenSelectedItemIsBlank() {
        final String selectedItem = "";

        page.openPage(selectedItem);

        verify(wizard,
               never()).start(any(BaseDecisionTableColumnPlugin.class));
    }

    @Test
    public void testOpenPageWhenTableIsReadOnly() {
        when(presenter.isReadOnly()).thenReturn(true);

        final String selectedItem = "page";

        page.openPage(selectedItem);

        verify(wizard,
               never()).start(any(BaseDecisionTableColumnPlugin.class));
    }

    @Test
    public void testOpenPageWhenTheWizardCanBeOpened() {
        final DecisionTableColumnPlugin plugin = pluginMock("plugin");
        final String selectedItem = "page";

        doReturn(plugin).when(page).findPluginByIdentifier(selectedItem);
        doReturn(false).when(presenter).isReadOnly();

        page.openPage(selectedItem);

        verify(wizard).start(plugin);
    }

    @Test
    public void testPluginsSortedByTitle() {
        final List<String> expectedTitles = Arrays.asList("Plugin A",
                                                          "Plugin B",
                                                          "Plugin C");

        final List<String> actualTitles = mapTitles(page.sortByTitle(new ArrayList<DecisionTableColumnPlugin>() {{
            add(pluginMock("Plugin A"));
            add(pluginMock("Plugin C"));
            add(pluginMock("Plugin B"));
        }}));

        assertEquals(expectedTitles,
                     actualTitles);
    }

    private DecisionTableColumnPlugin pluginMock(final String title) {
        final DecisionTableColumnPlugin mock = mock(DecisionTableColumnPlugin.class);

        when(mock.getTitle()).thenReturn(title);

        return mock;
    }

    private List<String> mapTitles(final List<DecisionTableColumnPlugin> plugins) {
        return plugins
                .stream()
                .map(DecisionTableColumnPlugin::getTitle)
                .collect(Collectors.toList());
    }

    @Test
    public void testGetTitle() throws Exception {
        final String errorKey = GuidedDecisionTableErraiConstants.SummaryPage_NewColumn;
        final String errorMessage = "Title";

        when(translationService.format(errorKey)).thenReturn(errorMessage);

        final String title = page.getTitle();

        assertEquals(errorMessage,
                     title);
    }

    @Test
    public void testPrepareView() throws Exception {
        page.prepareView();

        verify(view).init(page);
    }

    @Test
    public void testAsWidget() {
        final Widget contentWidget = page.asWidget();

        assertEquals(contentWidget,
                     content);
    }
}
