/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.editor.menu;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableModellerView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTablePresenter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.DecisionTablePinnedEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.DecisionTableSelectedEvent;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.widgets.common.client.menu.MenuItemDivider;
import org.uberfire.ext.widgets.common.client.menu.MenuItemDividerView;
import org.uberfire.ext.widgets.common.client.menu.MenuItemFactory;
import org.uberfire.ext.widgets.common.client.menu.MenuItemHeader;
import org.uberfire.ext.widgets.common.client.menu.MenuItemHeaderView;
import org.uberfire.ext.widgets.common.client.menu.MenuItemView;
import org.uberfire.ext.widgets.common.client.menu.MenuItemWithIcon;
import org.uberfire.ext.widgets.common.client.menu.MenuItemWithIconView;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ViewMenuBuilderTest {

    private ViewMenuBuilder builder;

    @Mock
    private TranslationService ts;

    @Mock
    private ManagedInstance<MenuItemView> menuItemViewProducer;

    @Mock
    private ManagedInstance<MenuItemView> menuItemViewHeaderProducer;

    @Mock
    private ManagedInstance<MenuItemView> menuItemViewWithIconProducer;

    @Mock
    private ManagedInstance<MenuItemView> menuItemViewDividerProducer;

    private MenuItemFactory menuItemFactory;
    private Set<MenuItemView> menuItemFactoryViewMocks = new HashSet<>();

    @Mock
    private GuidedDecisionTableView.Presenter dtPresenter;
    private GuidedDecisionTablePresenter.Access access = new GuidedDecisionTablePresenter.Access();

    @Mock
    private GuidedDecisionTableView dtPresenterView;

    @Mock
    private GuidedDecisionTableModellerView.Presenter modeller;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        menuItemFactory = new MenuItemFactory(menuItemViewProducer);
        menuItemFactoryViewMocks.clear();

        when(dtPresenter.getAccess()).thenReturn(access);
        when(ts.getTranslation(any(String.class))).thenReturn("i18n");
        when(menuItemViewProducer.select(any(Annotation.class))).thenAnswer((o) -> {
            final Annotation a = (Annotation) o.getArguments()[0];
            if (a.annotationType().equals(MenuItemHeader.class)) {
                return menuItemViewHeaderProducer;
            } else if (a.annotationType().equals(MenuItemWithIcon.class)) {
                return menuItemViewWithIconProducer;
            } else if (a.annotationType().equals(MenuItemDivider.class)) {
                return menuItemViewDividerProducer;
            }
            throw new IllegalArgumentException("Unexpected MenuItemView");
        });
        when(menuItemViewHeaderProducer.get()).then(invocation -> {
            final MenuItemHeaderView v = mock(MenuItemHeaderView.class);
            menuItemFactoryViewMocks.add(v);
            return v;
        });
        when(menuItemViewWithIconProducer.get()).then(invocation -> {
            final MenuItemWithIconView v = mock(MenuItemWithIconView.class);
            menuItemFactoryViewMocks.add(v);
            return v;
        });
        when(menuItemViewDividerProducer.get()).then(invocation -> {
            final MenuItemDividerView v = mock(MenuItemDividerView.class);
            menuItemFactoryViewMocks.add(v);
            return v;
        });

        builder = new ViewMenuBuilder(ts,
                                      menuItemFactory);
        builder.setup();
        builder.setModeller(modeller);
    }

    @Test
    public void testInitialSetup() {
        assertTrue(builder.miZoom125pct.getMenuItem().isEnabled());
        assertTrue(builder.miZoom100pct.getMenuItem().isEnabled());
        assertTrue(builder.miZoom75pct.getMenuItem().isEnabled());
        assertTrue(builder.miZoom50pct.getMenuItem().isEnabled());

        verify(builder.miZoom125pct.getMenuItemView(),
               times(1)).setIconType(eq(null));
        verify(builder.miZoom100pct.getMenuItemView(),
               times(1)).setIconType(eq(IconType.CHECK));
        verify(builder.miZoom75pct.getMenuItemView(),
               times(1)).setIconType(eq(null));
        verify(builder.miZoom50pct.getMenuItemView(),
               times(1)).setIconType(eq(null));

        assertFalse(builder.miToggleMergeState.getMenuItem().isEnabled());
        assertFalse(builder.miViewAuditLog.getMenuItem().isEnabled());
    }

    @Test
    public void testOnDecisionTableSelectedEventWithNonOtherwiseColumnSelected() {
        builder.onDecisionTableSelectedEvent(new DecisionTableSelectedEvent(dtPresenter));

        assertTrue(builder.miToggleMergeState.getMenuItem().isEnabled());
        assertTrue(builder.miViewAuditLog.getMenuItem().isEnabled());
    }

    @Test
    public void testOnZoom125() {
        menuItemFactoryViewMocks.stream().forEach(Mockito::reset);

        builder.onZoom(125);

        verify(builder.miZoom125pct.getMenuItemView(),
               times(1)).setIconType(eq(IconType.CHECK));
        verify(builder.miZoom100pct.getMenuItemView(),
               times(1)).setIconType(eq(null));
        verify(builder.miZoom75pct.getMenuItemView(),
               times(1)).setIconType(eq(null));
        verify(builder.miZoom50pct.getMenuItemView(),
               times(1)).setIconType(eq(null));
        verify(modeller,
               times(1)).setZoom(eq(125));
    }

    @Test
    public void testOnZoom100() {
        menuItemFactoryViewMocks.stream().forEach(Mockito::reset);

        builder.onZoom(100);

        verify(builder.miZoom125pct.getMenuItemView(),
               times(1)).setIconType(eq(null));
        verify(builder.miZoom100pct.getMenuItemView(),
               times(1)).setIconType(eq(IconType.CHECK));
        verify(builder.miZoom75pct.getMenuItemView(),
               times(1)).setIconType(eq(null));
        verify(builder.miZoom50pct.getMenuItemView(),
               times(1)).setIconType(eq(null));
        verify(modeller,
               times(1)).setZoom(eq(100));
    }

    @Test
    public void testOnZoom75() {
        menuItemFactoryViewMocks.stream().forEach(Mockito::reset);

        builder.onZoom(75);

        verify(builder.miZoom125pct.getMenuItemView(),
               times(1)).setIconType(eq(null));
        verify(builder.miZoom100pct.getMenuItemView(),
               times(1)).setIconType(eq(null));
        verify(builder.miZoom75pct.getMenuItemView(),
               times(1)).setIconType(eq(IconType.CHECK));
        verify(builder.miZoom50pct.getMenuItemView(),
               times(1)).setIconType(eq(null));
        verify(modeller,
               times(1)).setZoom(eq(75));
    }

    @Test
    public void testOnZoom50() {
        menuItemFactoryViewMocks.stream().forEach(Mockito::reset);

        builder.onZoom(50);

        verify(builder.miZoom125pct.getMenuItemView(),
               times(1)).setIconType(eq(null));
        verify(builder.miZoom100pct.getMenuItemView(),
               times(1)).setIconType(eq(null));
        verify(builder.miZoom75pct.getMenuItemView(),
               times(1)).setIconType(eq(null));
        verify(builder.miZoom50pct.getMenuItemView(),
               times(1)).setIconType(eq(IconType.CHECK));
        verify(modeller,
               times(1)).setZoom(eq(50));
    }

    @Test
    public void testToggleMergeState() {
        when(dtPresenter.isMerged()).thenReturn(false);

        builder.onDecisionTableSelectedEvent(new DecisionTableSelectedEvent(dtPresenter));

        assertTrue(builder.miToggleMergeState.getMenuItem().isEnabled());
        assertTrue(builder.miViewAuditLog.getMenuItem().isEnabled());
        verify(builder.miToggleMergeState.getMenuItemView(),
               times(1)).setIconType(eq(null));

        builder.onToggleMergeState();

        verify(dtPresenter,
               times(1)).setMerged(eq(true));
        verify(builder.miToggleMergeState.getMenuItemView(),
               times(1)).setIconType(eq(IconType.CHECK));
    }

    @Test
    public void testViewAuditLog() {
        builder.onDecisionTableSelectedEvent(new DecisionTableSelectedEvent(dtPresenter));

        builder.onViewAuditLog();

        verify(dtPresenter,
               times(1)).showAuditLog();
    }

    @Test
    public void testEnableZoom_Pinned() {
        builder.onDecisionTablePinnedEvent(new DecisionTablePinnedEvent(modeller,
                                                                        true));

        assertFalse(builder.miZoom125pct.getMenuItem().isEnabled());
        assertFalse(builder.miZoom100pct.getMenuItem().isEnabled());
        assertFalse(builder.miZoom75pct.getMenuItem().isEnabled());
        assertFalse(builder.miZoom50pct.getMenuItem().isEnabled());
    }

    @Test
    public void testEnableZoom_Pinned_DifferentModeller() {
        builder.onDecisionTablePinnedEvent(new DecisionTablePinnedEvent(mock(GuidedDecisionTableModellerView.Presenter.class),
                                                                        true));

        verify(builder.miZoom125pct.getMenuItemView(),
               never()).setEnabled(any(Boolean.class));
        verify(builder.miZoom100pct.getMenuItemView(),
               never()).setEnabled(any(Boolean.class));
        verify(builder.miZoom75pct.getMenuItemView(),
               never()).setEnabled(any(Boolean.class));
        verify(builder.miZoom50pct.getMenuItemView(),
               never()).setEnabled(any(Boolean.class));
    }

    @Test
    public void testEnableZoom_Unpinned() {
        builder.onDecisionTablePinnedEvent(new DecisionTablePinnedEvent(modeller,
                                                                        false));

        verify(builder.miZoom125pct.getMenuItemView(),
               times(1)).setEnabled(eq(true));
        verify(builder.miZoom100pct.getMenuItemView(),
               times(1)).setEnabled(eq(true));
        verify(builder.miZoom75pct.getMenuItemView(),
               times(1)).setEnabled(eq(true));
        verify(builder.miZoom50pct.getMenuItemView(),
               times(1)).setEnabled(eq(true));
    }

    @Test
    public void testEnableZoom_Unpinned_DifferentModeller() {
        builder.onDecisionTablePinnedEvent(new DecisionTablePinnedEvent(mock(GuidedDecisionTableModellerView.Presenter.class),
                                                                        false));

        verify(builder.miZoom125pct.getMenuItemView(),
               never()).setEnabled(any(Boolean.class));
        verify(builder.miZoom100pct.getMenuItemView(),
               never()).setEnabled(any(Boolean.class));
        verify(builder.miZoom75pct.getMenuItemView(),
               never()).setEnabled(any(Boolean.class));
        verify(builder.miZoom50pct.getMenuItemView(),
               never()).setEnabled(any(Boolean.class));
    }

    @Test
    public void testOnDecisionTableSelectedEventReadOnly() {
        //ViewMenuBuilder.setup() called in @Setup disables view by default
        assertFalse(builder.miToggleMergeState.getMenuItem().isEnabled());
        assertFalse(builder.miViewAuditLog.getMenuItem().isEnabled());

        dtPresenter.getAccess().setReadOnly(true);
        builder.onDecisionTableSelectedEvent(new DecisionTableSelectedEvent(dtPresenter));

        //Verify selecting a read-only Decision Table also disables view
        assertFalse(builder.miToggleMergeState.getMenuItem().isEnabled());
        assertFalse(builder.miViewAuditLog.getMenuItem().isEnabled());
    }
}
