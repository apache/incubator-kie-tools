/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.widget.DTCellValueWidgetFactory;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.BRLActionColumnPlugin;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.mockito.Mock;
import org.uberfire.client.callbacks.Callback;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class DefaultValuesPageTest {

    private static final String DATE_FORMAT = "dd-MM-yyyy";

    @BeforeClass
    public static void setup() {
        setupPreferences();
    }

    private static void setupPreferences() {
        final Map<String, String> preferences = Collections.singletonMap(ApplicationPreferences.DATE_FORMAT,
                                                                         DATE_FORMAT);
        ApplicationPreferences.setUp(preferences);
    }

    @Mock
    private DefaultValuesPage.View view;

    @Mock
    private BRLActionColumnPlugin plugin;

    @Mock
    private BRLActionColumn brlActionColumn;

    @Mock
    private DTCellValueWidgetFactory factory;

    @Mock
    private IsWidget defaultValueOneWidget;

    @Mock
    private IsWidget defaultValueTwoWidget;

    private DefaultValuesPage page;

    private BRLActionVariableColumn brlActionVariableColumn1 = new BRLActionVariableColumn("var1",
                                                                                           "fieldType");
    private BRLActionVariableColumn brlActionVariableColumn2 = new BRLActionVariableColumn("var2",
                                                                                           "fieldType");

    @Before
    public void setUp() throws Exception {
        page = new DefaultValuesPage(view,
                                     mock(TranslationService.class)) {
            @Override
            protected DTCellValueWidgetFactory factory() {
                return factory;
            }
        };
        page.setPlugin(plugin);
        doReturn(brlActionColumn).when(plugin).editingCol();
        final DTCellValue52 defaultValue = new DTCellValue52();
        brlActionVariableColumn1.setDefaultValue(defaultValue);
        doReturn(defaultValueOneWidget).when(factory).getWidget(brlActionVariableColumn1,
                                                                defaultValue);
        doReturn(defaultValueTwoWidget).when(factory).getWidget(eq(brlActionVariableColumn2),
                                                                any());
    }

    @Test
    public void alwaysComplete() {
        final Callback callback = mock(Callback.class);
        page.isComplete(callback);
        verify(callback).callback(true);
    }

    @Test
    public void viewSetup() {
        assertEquals(view, page.getView());
    }

    @Test
    public void prepareView() {
        page.prepareView();

        verify(view).init(page);
        verify(view).clear();

        verify(view, never()).addVariable(anyString(),
                                          any());
    }

    @Test
    public void prepareViewWithChildren() {
        final GuidedDecisionTableView.Presenter presenter = mock(GuidedDecisionTableView.Presenter.class);
        final BRLActionColumn brlColumn = mock(BRLActionColumn.class);
        final GuidedDecisionTable52 dtable = mock(GuidedDecisionTable52.class);
        final ArrayList<BRLVariableColumn> childColumns = new ArrayList<>();
        childColumns.add(brlActionVariableColumn1);
        childColumns.add(brlActionVariableColumn2);

        doReturn(brlColumn).when(plugin).editingCol();
        doReturn(presenter).when(plugin).getPresenter();
        doReturn(dtable).when(presenter).getModel();
        doReturn(GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY).when(dtable).getTableFormat();
        doReturn(childColumns).when(brlColumn).getChildColumns();

        page.prepareView();

        verify(view).init(page);
        verify(view).clear();

        verify(view).addVariable("var1",
                                 defaultValueOneWidget);
        verify(view).addVariable("var2",
                                 defaultValueTwoWidget);
    }
}