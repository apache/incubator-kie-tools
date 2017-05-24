/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.drools.workbench.screens.guided.dtable.client.widget.table;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dev.util.collect.HashMap;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.screens.guided.dtable.client.resources.GuidedDecisionTableResources;
import org.drools.workbench.screens.guided.dtable.client.widget.table.accordion.GuidedDecisionTableAccordion;
import org.drools.workbench.screens.guided.dtable.client.widget.table.accordion.GuidedDecisionTableAccordionItem;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.control.AttributeColumnConfigRowView;
import org.gwtbootstrap3.client.ui.Button;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.kie.workbench.common.widgets.client.ruleselector.RuleSelector;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.DefaultGridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLienzoPanel;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.impl.RestrictedMousePanMediator;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub({GridLienzoPanel.class, DefaultGridLayer.class, GridWidget.class, RestrictedMousePanMediator.class})
public class GuidedDecisionTableModellerViewImplTest {

    @Mock
    FlowPanel flowPanel;

    @Mock
    GridLienzoPanel gridPanel;

    @Mock
    DefaultGridLayer gridLayer;

    @Mock
    DefaultGridLayer defaultGridLayer;

    @Mock
    RestrictedMousePanMediator restrictedMousePanMediator;

    @Mock
    VerticalPanel attributeConfigWidget;

    @Mock
    GuidedDecisionTableAccordion accordion;

    @Mock
    GuidedDecisionTableModellerView.Presenter presenter;

    @Mock
    GuidedDecisionTableView.Presenter viewPresenter;

    GuidedDecisionTableModellerViewImpl view;

    @Before
    public void setup() {
        view = spy(new GuidedDecisionTableModellerViewImplFake());

        ApplicationPreferences.setUp(new HashMap<String, String>() {{
            put(ApplicationPreferences.DATE_FORMAT,
                "dd/mm/yy");
        }});
    }

    @Test
    public void testInit() throws Exception {
        doNothing().when(view).setupAccordion(presenter);

        view.init(presenter);

        verify(view).setupAccordion(presenter);
    }

    @Test
    public void testSetupAccordion() throws Exception {
        final FlowPanel accordionContainer = mock(FlowPanel.class);
        final Widget accordionWidget = mock(Widget.class);
        final Widget ruleInheritanceWidget = mock(Widget.class);

        doReturn(accordion).when(view).makeAccordion(presenter);
        doReturn(accordionContainer).when(view).getAccordionContainer();
        doReturn(accordionWidget).when(view).asWidget(accordion);
        doReturn(ruleInheritanceWidget).when(view).ruleInheritanceWidget();

        view.setupAccordion(presenter);

        verify(accordionContainer).add(accordionWidget);
        verify(accordionContainer).add(ruleInheritanceWidget);
    }

    @Test
    public void testMakeAccordion() {
        doReturn(accordion).when(view).getGuidedDecisionTableAccordion();

        final GuidedDecisionTableAccordion actualAccordion = view.makeAccordion(presenter);

        verify(accordion).addItem(GuidedDecisionTableAccordionItem.Type.ATTRIBUTE,
                                  view.getAttributeConfigWidget());
        verify(accordion).addItem(GuidedDecisionTableAccordionItem.Type.METADATA,
                                  view.getMetaDataConfigWidget());
        verify(accordion).addItem(GuidedDecisionTableAccordionItem.Type.CONDITION,
                                  view.getConditionsConfigWidget());
        verify(accordion).addItem(GuidedDecisionTableAccordionItem.Type.ACTION,
                                  view.getActionsConfigWidget());

        assertEquals(accordion,
                     actualAccordion);
    }

    @Test
    public void testRuleInheritanceWidget() throws Exception {
        final Label label = mock(Label.class);
        final Widget widget = mock(Widget.class);

        doReturn(flowPanel).when(view).makeFlowPanel();
        doReturn(label).when(view).ruleInheritanceLabel();
        doReturn(widget).when(view).ruleSelector();

        final Widget ruleInheritanceWidget = view.ruleInheritanceWidget();

        verify(flowPanel).setStyleName(GuidedDecisionTableResources.INSTANCE.css().ruleInheritance());
        verify(flowPanel).add(label);
        verify(flowPanel).add(widget);

        assertEquals(flowPanel,
                     ruleInheritanceWidget);
    }

    @Test
    public void testRuleSelector() throws Exception {
        final RuleSelector ruleSelector = mock(RuleSelector.class);

        doReturn(ruleSelector).when(view).getRuleSelector();

        final Widget actualRuleSelector = view.ruleSelector();

        verify(ruleSelector).setEnabled(false);
        verify(ruleSelector).addValueChangeHandler(any());

        assertEquals(ruleSelector,
                     actualRuleSelector);
    }

    @Test
    public void testRuleInheritanceLabel() throws Exception {
        final Label label = view.ruleInheritanceLabel();

        assertNotNull(label);
    }

    @Test
    public void testSetupSubMenu() throws Exception {
        final Button addColumn = mock(Button.class);
        final Button editColumns = mock(Button.class);

        doReturn(addColumn).when(view).getAddColumn();
        doReturn(editColumns).when(view).getEditColumns();

        view.setupSubMenu();

        verify(addColumn).addClickHandler(any());
        verify(editColumns).addClickHandler(any());
    }

    @Test
    public void testAddColumn() throws Exception {
        doReturn(presenter).when(view).getPresenter();

        view.addColumn();

        verify(presenter).openNewGuidedDecisionTableColumnWizard();
    }

    @Test
    public void testEditColumns() throws Exception {
        final FlowPanel accordionContainer = mock(FlowPanel.class);
        final Button editColumns = mock(Button.class);

        doReturn(presenter).when(view).getPresenter();
        doReturn(accordionContainer).when(view).getAccordionContainer();
        doReturn(editColumns).when(view).getEditColumns();
        doNothing().when(view).toggleClassName(any(),
                                               any());

        view.editColumns();

        verify(view).toggleClassName(accordionContainer,
                                     GuidedDecisionTableResources.INSTANCE.css().openedAccordion());
        verify(view).toggleClassName(editColumns,
                                     "active");
    }

    @Test
    public void testRefreshAttributeWidgetEmpty() throws Exception {
        final GuidedDecisionTableAccordionItem item = mock(GuidedDecisionTableAccordionItem.class);
        final Label blankSlate = mock(Label.class);
        final List<AttributeCol52> columns = new ArrayList<>();

        doReturn(accordion).when(view).getAccordion();
        doReturn(attributeConfigWidget).when(view).getAttributeConfigWidget();
        doReturn(blankSlate).when(view).blankSlate();

        doReturn(item).when(accordion).getItem(GuidedDecisionTableAccordionItem.Type.ATTRIBUTE);

        view.refreshAttributeWidget(columns);

        verify(attributeConfigWidget).clear();
        verify(attributeConfigWidget).add(blankSlate);
        verify(item).setOpen(false);
    }

    @Test
    public void testRefreshAttributeWidget() throws Exception {
        final GuidedDecisionTableAccordionItem item = mock(GuidedDecisionTableAccordionItem.class);
        final Label blankSlate = mock(Label.class);
        final List<AttributeCol52> columns = new ArrayList<AttributeCol52>() {{
            add(attributeColumn());
        }};

        doReturn(presenter).when(view).getPresenter();
        doReturn(accordion).when(view).getAccordion();
        doReturn(attributeConfigWidget).when(view).getAttributeConfigWidget();
        doReturn(blankSlate).when(view).blankSlate();
        doReturn(item).when(accordion).getItem(GuidedDecisionTableAccordionItem.Type.ATTRIBUTE);

        view.refreshAttributeWidget(columns);

        verify(attributeConfigWidget).clear();
        verify(attributeConfigWidget,
               never()).add(blankSlate);
        verify(item,
               never()).setOpen(anyBoolean());
        verify(attributeConfigWidget).add(any(AttributeColumnConfigRowView.class));
    }

    private AttributeCol52 attributeColumn() {
        final AttributeCol52 attributeCol52 = mock(AttributeCol52.class);
        final DTCellValue52 defaultValue = mock(DTCellValue52.class);

        doReturn("salience").when(attributeCol52).getAttribute();
        doReturn(defaultValue).when(attributeCol52).getDefaultValue();

        return attributeCol52;
    }

    class GuidedDecisionTableModellerViewImplFake extends GuidedDecisionTableModellerViewImpl {

        public GuidedDecisionTableModellerViewImplFake() {
            /* do nothing */
        }

        DefaultGridLayer defaultGridLayer() {
            return defaultGridLayer;
        }

        RestrictedMousePanMediator restrictedMousePanMediator() {
            return restrictedMousePanMediator;
        }
    }
}
