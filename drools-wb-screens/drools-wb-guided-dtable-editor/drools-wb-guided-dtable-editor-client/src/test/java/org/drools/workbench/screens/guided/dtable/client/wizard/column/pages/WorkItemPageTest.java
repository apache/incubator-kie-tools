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

import java.util.function.BiConsumer;

import com.google.gwt.junit.GWTMockUtilities;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.datamodel.workitems.PortableWorkDefinition;
import org.drools.workbench.models.guided.dtable.shared.model.ActionWorkItemCol52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableErraiConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.ActionWorkItemPlugin;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class WorkItemPageTest {

    @Mock
    private WorkItemPage.View view;

    @Mock
    private ActionWorkItemPlugin plugin;

    @Mock
    private GuidedDecisionTableView.Presenter presenter;

    @Mock
    private ActionWorkItemCol52 editingCol;

    @Mock
    private SimplePanel content;

    @Mock
    private TranslationService translationService;

    @Captor
    private ArgumentCaptor<BiConsumer<String, String>> consumer;

    @InjectMocks
    private WorkItemPage<ActionWorkItemPlugin> page = spy(new WorkItemPage<ActionWorkItemPlugin>(view,
                                                                                                 translationService));

    @BeforeClass
    public static void staticSetup() {
        // Prevent runtime GWT.create() error at 'content = new SimplePanel()'
        GWTMockUtilities.disarm();
    }

    @Test
    public void testIsCompleteWhenWorkItemIsSet() {
        when(plugin.isWorkItemSet()).thenReturn(true);

        page.isComplete(Assert::assertTrue);
    }

    @Test
    public void testIsCompleteWhenWorkItemIsNotSet() {
        when(plugin.isWorkItemSet()).thenReturn(false);

        page.isComplete(Assert::assertFalse);
    }

    @Test
    public void testEnableParameters() {
        page.enableParameters();

        assertTrue(page.isParametersEnabled());
    }

    @Test
    public void testForEachWorkItem() {
        final BiConsumer<String, String> biConsumer = (displayName, name) -> {
        };

        page.forEachWorkItem(biConsumer);

        verify(plugin).forEachWorkItem(biConsumer);
    }

    @Test
    public void testHasWorkItemsWhenItIsFalse() {
        when(view.workItemsCount()).thenReturn(1);

        final boolean hasWorkItems = page.hasWorkItems();

        assertFalse(hasWorkItems);
    }

    @Test
    public void testHasWorkItemsWhenItIsTrue() {
        when(view.workItemsCount()).thenReturn(2);

        final boolean hasWorkItems = page.hasWorkItems();

        assertTrue(hasWorkItems);
    }

    @Test
    public void testSelectWorkItem() {
        final String workItem = "workItem";

        when(view.getSelectedWorkItem()).thenReturn(workItem);

        page.selectWorkItem(view.getSelectedWorkItem());

        verify(plugin).setWorkItem(workItem);
        verify(page).showParameters();
    }

    @Test
    public void testShowParametersWhenParametersAreNotEnabled() {
        final PortableWorkDefinition workDefinition = mock(PortableWorkDefinition.class);

        when(plugin.getWorkItemDefinition()).thenReturn(workDefinition);

        page.showParameters();

        verify(view).hideParameters();
    }

    @Test
    public void testShowParametersWhenItDoesNotHaveWorkItemDefinition() {
        when(plugin.getWorkItemDefinition()).thenReturn(null);

        page.enableParameters();
        page.showParameters();

        verify(view).hideParameters();
    }

    @Test
    public void testShowParametersWhenItHasWorkItemDefinitionParametersAreEnabled() {
        final PortableWorkDefinition workDefinition = mock(PortableWorkDefinition.class);

        when(plugin.getWorkItemDefinition()).thenReturn(workDefinition);

        page.enableParameters();
        page.showParameters();

        verify(view).showParameters(any());
    }

    @Test
    public void testCurrentWorkItem() {
        final String expectedWorkItem = "workItem";

        when(plugin.getWorkItem()).thenReturn(expectedWorkItem);

        final String actualWorkItem = page.currentWorkItem();

        verify(plugin).getWorkItem();
        assertEquals(expectedWorkItem,
                     actualWorkItem);
    }

    @Test
    public void testGetTitle() throws Exception {
        final String errorKey = GuidedDecisionTableErraiConstants.WorkItemPage_WorkItem;
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
        verify(page).markAsViewed();
    }

    @Test
    public void testAsWidget() {
        final Widget contentWidget = page.asWidget();

        assertEquals(contentWidget,
                     content);
    }

    @Test
    public void testSetupWorkItemsListWhenPageHasWorkItems() {
        final String displayName = "displayName";
        final String name = "name";

        doReturn(true).when(page).hasWorkItems();
        doNothing().when(page).forEachWorkItem(this.consumer.capture());
        doReturn(displayName).when(page).currentWorkItem();

        page.setupWorkItemsList();

        final BiConsumer<String, String> consumer = this.consumer.getValue();

        consumer.accept(displayName,
                        name);

        verify(view).addItem(displayName,
                             name);
        verify(view).selectWorkItem(displayName);
        verify(page).showParameters();
    }

    @Test
    public void testSetupWorkItemsListWhenPageDoesNotHaveWorkItems() {
        doReturn(false).when(page).hasWorkItems();
        doNothing().when(page).forEachWorkItem((s1, s2) -> {
        });

        page.setupWorkItemsList();

        verify(view).setupEmptyWorkItemList();
        verify(page,
               never()).showParameters();
        verify(view,
               never()).addItem(any(),
                                any());
    }

    private PortableWorkDefinition newPortableWorkDefinition(final String displayName,
                                                             final String name) {
        final PortableWorkDefinition mock = mock(PortableWorkDefinition.class);

        doReturn(displayName).when(mock).getDisplayName();
        doReturn(name).when(mock).getName();

        return mock;
    }
}
