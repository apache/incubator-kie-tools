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

import com.ait.lienzo.client.core.mediator.Mediators;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Transform;
import com.google.gwt.dev.util.collect.HashMap;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
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
import org.gwtbootstrap3.client.ui.Icon;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.kie.workbench.common.widgets.client.ruleselector.RuleSelector;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.DefaultGridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLienzoPanel;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.TransformMediator;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.impl.RestrictedMousePanMediator;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub({GridLienzoPanel.class, DefaultGridLayer.class, GridWidget.class, RestrictedMousePanMediator.class})
public class GuidedDecisionTableModellerViewImplTest {

    @Mock
    private FlowPanel flowPanel;

    @Mock
    private GridLienzoPanel mockGridPanel;

    @Mock
    private DefaultGridLayer gridLayer;

    @Mock
    private DefaultGridLayer defaultGridLayer;

    @Mock
    private RestrictedMousePanMediator restrictedMousePanMediator;

    @Mock
    private VerticalPanel attributeConfigWidget;

    @Mock
    private GuidedDecisionTableAccordion accordion;

    @Mock
    private GuidedDecisionTableModellerView.Presenter presenter;

    @Mock
    private GuidedDecisionTableView.Presenter viewPresenter;

    @Mock
    private Icon pinnedModeIndicator;

    @Mock
    private RootPanel rootPanel;

    private GuidedDecisionTableModellerViewImpl view;

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

        verify(view).disableButtonMenu();
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

    @Test
    public void testAddKeyDownHandlerAttachesToEditor() {
        //Ensure nobody thinks its a good idea to attach to the RootPanel at some time in the future!
        //See https://issues.jboss.org/browse/GUVNOR-3146
        final KeyDownHandler handler = mock(KeyDownHandler.class);

        view.addKeyDownHandler(handler);

        verify(rootPanel,
               never()).addDomHandler(eq(handler),
                                      eq(KeyDownEvent.getType()));
        verify(mockGridPanel).addKeyDownHandler(eq(handler));
    }

    @Test
    public void testAddMouseDownHandlerAttachesToRootPanel() {
        //Ensure nobody thinks its a good idea to attach to the GridPanel at some time in the future!
        //See https://issues.jboss.org/browse/GUVNOR-3491
        final MouseDownHandler handler = mock(MouseDownHandler.class);

        view.addMouseDownHandler(handler);

        verify(mockGridPanel,
               never()).addMouseDownHandler(eq(handler));
        verify(rootPanel).addDomHandler(eq(handler),
                                        eq(MouseDownEvent.getType()));
    }

    @Test
    public void testEnableButtonMenu() {
        final Button addColumn = mock(Button.class);
        final Button editColumns = mock(Button.class);

        doReturn(addColumn).when(view).getAddColumn();
        doReturn(editColumns).when(view).getEditColumns();

        view.enableButtonMenu();

        verify(addColumn).setEnabled(true);
        verify(editColumns).setEnabled(true);
    }

    @Test
    public void testDisableButtonMenu() {
        final Button addColumn = mock(Button.class);
        final Button editColumns = mock(Button.class);

        doReturn(addColumn).when(view).getAddColumn();
        doReturn(editColumns).when(view).getEditColumns();

        view.disableButtonMenu();

        verify(addColumn).setEnabled(false);
        verify(editColumns).setEnabled(false);
    }

    @Test
    public void testSelect() {
        final GridWidget gridWidget = mock(GridWidget.class);
        final RuleSelector ruleSelector = mock(RuleSelector.class);
        final DefaultGridLayer gridLayer = mock(DefaultGridLayer.class);

        doReturn(ruleSelector).when(view).getRuleSelector();
        doReturn(gridLayer).when(view).getGridLayer();

        view.select(gridWidget);

        verify(view).enableButtonMenu();
        verify(ruleSelector).setEnabled(true);
        verify(gridLayer).select(gridWidget);
    }

    @Test
    public void testRefreshScrollPosition() {
        view.refreshScrollPosition();

        verify(mockGridPanel).refreshScrollPosition();
    }

    @Test
    public void testSetup() throws Exception {
        final AbsolutePanel mainPanel = mock(AbsolutePanel.class);
        final Transform transform = mock(Transform.class);
        final Viewport viewport = mock(Viewport.class);
        final Mediators mediators = mock(Mediators.class);
        final Element element = mock(Element.class);

        doReturn(transform).when(transform).scale(anyDouble());

        doReturn(transform).when(view).newTransform();

        doReturn(mediators).when(viewport).getMediators();

        doReturn(element).when(mockGridPanel).getElement();
        doReturn(mainPanel).when(mockGridPanel).getScrollPanel();
        doReturn(viewport).when(mockGridPanel).getViewport();

        view.setup();

        verify(view).setupSubMenu();
        verify(view).setupGridPanel();
        verify(mediators).push(restrictedMousePanMediator);
    }

    @Test
    public void testRadarIsUpdatedAfterScrolling() throws Exception {
        final ArgumentCaptor<ScrollHandler> scrollHandler = ArgumentCaptor.forClass(ScrollHandler.class);
        final ScrollEvent scrollEvent = mock(ScrollEvent.class);
        final AbsolutePanel mainPanel = mock(AbsolutePanel.class);
        final Transform transform = mock(Transform.class);
        final Viewport viewport = mock(Viewport.class);
        final Mediators mediators = mock(Mediators.class);
        final Element element = mock(Element.class);

        doReturn(transform).when(transform).scale(anyDouble());

        doReturn(presenter).when(view).getPresenter();
        doReturn(transform).when(view).newTransform();

        doReturn(mediators).when(viewport).getMediators();

        doReturn(element).when(mockGridPanel).getElement();
        doReturn(mainPanel).when(mockGridPanel).getScrollPanel();
        doReturn(viewport).when(mockGridPanel).getViewport();

        view.setupGridPanel();

        verify(mainPanel).addDomHandler(scrollHandler.capture(),
                                        eq(ScrollEvent.getType()));

        scrollHandler.getValue().onScroll(scrollEvent);

        verify(presenter).updateRadar();
    }

    @Test
    public void testScrollbarsUpdatedAfterZoom() throws Exception {
        final double x = 10.0;
        final double y = 20.0;
        final int zoom = 70;
        final Transform transform = mock(Transform.class);
        final Viewport viewport = mock(Viewport.class);
        final TransformMediator mediator = mock(TransformMediator.class);

        doReturn(transform).when(mediator).adjust(eq(transform),
                                                  any());

        doReturn(mediator).when(restrictedMousePanMediator).getTransformMediator();

        doReturn(x).when(transform).getTranslateX();
        doReturn(y).when(transform).getTranslateY();

        doReturn(transform).when(view).newTransform();

        doReturn(transform).when(viewport).getTransform();

        doReturn(viewport).when(mockGridPanel).getViewport();

        view.setZoom(zoom);

        verify(transform).translate(x,
                                    y);
        verify(transform).scale(zoom / 100.0);
        verify(viewport,
               times(2)).setTransform(transform);
        verify(viewport).batch();
        verify(mockGridPanel).refreshScrollPosition();
    }

    @Test
    public void testPinnedModeVisible() throws Exception {
        view.setPinnedModeIndicatorVisibility(true);

        verify(pinnedModeIndicator).setVisible(true);
    }

    @Test
    public void testPinnedModeHidden() throws Exception {
        view.setPinnedModeIndicatorVisibility(false);

        verify(pinnedModeIndicator).setVisible(false);
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
            this.gridPanel = mockGridPanel;
            this.pinnedModeIndicator = GuidedDecisionTableModellerViewImplTest.this.pinnedModeIndicator;
        }

        DefaultGridLayer defaultGridLayer() {
            return defaultGridLayer;
        }

        RestrictedMousePanMediator restrictedMousePanMediator() {
            return restrictedMousePanMediator;
        }

        @Override
        RootPanel rootPanel() {
            return rootPanel;
        }
    }
}
