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

package org.drools.workbench.screens.guided.dtable.client.widget.table;

import java.util.HashMap;

import com.ait.lienzo.client.core.mediator.Mediators;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Transform;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.control.ColumnLabelWidget;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.DefaultGridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLienzoPanel;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.GridPinnedModeManager;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.TransformMediator;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.impl.RestrictedMousePanMediator;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyDouble;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub({ColumnLabelWidget.class, GridLienzoPanel.class, DefaultGridLayer.class, GridWidget.class, RestrictedMousePanMediator.class})
public class GuidedDecisionTableModellerViewImplTest {

    @Mock
    private GridLienzoPanel mockGridPanel;

    @Mock
    private DefaultGridLayer defaultGridLayer;

    @Mock
    private RestrictedMousePanMediator restrictedMousePanMediator;

    @Mock
    private GuidedDecisionTableModellerView.Presenter presenter;

    @Mock
    private GuidedDecisionTableView dtableView;

    @Mock
    private HTMLPanel pinnedModeIndicator;

    @Captor
    private ArgumentCaptor<Command> commandArgumentCaptor;

    @Mock
    private RootPanel rootPanel;

    @Mock
    private TranslationService translationService;

    private GuidedDecisionTableModellerViewImpl view;

    @Before
    public void setup() {
        view = spy(new GuidedDecisionTableModellerViewImplFake(translationService));

        ApplicationPreferences.setUp(new HashMap<String, String>() {{
            put(ApplicationPreferences.DATE_FORMAT,
                "dd/mm/yy");
        }});
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
    public void testSelect() {
        final GridWidget gridWidget = mock(GridWidget.class);
        final DefaultGridLayer gridLayer = mock(DefaultGridLayer.class);

        doReturn(gridLayer).when(view).getGridLayer();

        view.select(gridWidget);

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

        final Element element = mock(Element.class);
        final Style style = mock(Style.class);

        doReturn(element).when(pinnedModeIndicator).getElement();
        doReturn(style).when(element).getStyle();

        view.setPinnedModeIndicatorVisibility(true, 0);

        verify(style).setTop(0.5, Style.Unit.EM);
    }

    @Test
    public void testPinnedModeHidden() throws Exception {

        final Element element = mock(Element.class);
        final Style style = mock(Style.class);

        doReturn(element).when(pinnedModeIndicator).getElement();
        doReturn(style).when(element).getStyle();

        view.setPinnedModeIndicatorVisibility(false, 0);

        verify(style).setTop(-2.0, Style.Unit.EM);
    }

    @Test
    public void testRemoveDecisionTableWhenPinned() {
        final Command callback = mock(Command.class);
        final GridPinnedModeManager.PinnedContext context = mock(GridPinnedModeManager.PinnedContext.class);

        when(defaultGridLayer.isGridPinned()).thenReturn(true);
        when(defaultGridLayer.getPinnedContext()).thenReturn(context);
        when(context.getGridWidget()).thenReturn(dtableView);

        view.removeDecisionTable(dtableView,
                                 callback);

        verify(defaultGridLayer,
               times(1)).exitPinnedMode(commandArgumentCaptor.capture());

        final Command command = commandArgumentCaptor.getValue();
        assertNotNull(command);
        command.execute();

        verify(defaultGridLayer,
               times(1)).remove(dtableView);
        verify(callback,
               times(1)).execute();
        verify(defaultGridLayer,
               times(1)).batch();
    }

    @Test
    public void testRemoveDecisionTableWhenNotPinned() {
        final Command callback = mock(Command.class);

        view.removeDecisionTable(dtableView,
                                 callback);

        verify(callback,
               times(1)).execute();
    }

    class GuidedDecisionTableModellerViewImplFake extends GuidedDecisionTableModellerViewImpl {

        public GuidedDecisionTableModellerViewImplFake(final TranslationService translationService) {
            super(translationService);

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

        @Override
        Presenter getPresenter() {
            return presenter;
        }
    }
}
