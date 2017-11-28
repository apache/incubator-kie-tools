/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.client.navigation.widget;

import org.dashbuilder.client.navigation.NavigationManager;
import org.dashbuilder.client.navigation.plugin.PerspectivePluginManager;
import org.dashbuilder.navigation.NavItem;
import org.dashbuilder.navigation.NavTree;
import org.dashbuilder.navigation.impl.NavTreeBuilder;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.mvp.PlaceManager;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class NavTilesWidgetTest {

    @Mock
    NavTilesWidget.View view;

    @Mock
    PerspectivePluginManager pluginManager;

    @Mock
    NavigationManager navigationManager;

    @Mock
    SyncBeanManager beanManager;

    @Mock
    PlaceManager placeManager;

    @Mock
    SyncBeanDef<NavItemTileWidget> tileWidgetBeanDef;

    @Mock
    NavItemTileWidget tileWidget;

    NavTilesWidget presenter;
    NavTree tree;

    @Before
    public void setUp() throws Exception {
        when(beanManager.lookupBean(NavItemTileWidget.class)).thenReturn(tileWidgetBeanDef);
        when(tileWidgetBeanDef.getInstance()).thenReturn(tileWidget);
        presenter = new NavTilesWidget(view, navigationManager, pluginManager, placeManager, beanManager);

        tree = new NavTreeBuilder()
                .group("Home", "Home", null, false)
                .group("A", "A", null, false)
                .item("A1", "A1", null, false)
                .item("A2", "A2", null, false)
                .group("A3", "A3", null, false)
                .item("A31", "A3", null, false)
                .build();
    }

    @Test
    public void testOpenItem() {
        NavItem navItem = tree.getItemById("A");
        presenter.openItem(navItem);
        assertEquals(presenter.getNavItemStack().size(), 2);

        verify(view, times(3)).addTileWidget(tileWidget);
        verify(view).clearBreadcrumb();
        verify(view).addBreadcrumbItem(eq("Home"), any());
        verify(view).addBreadcrumbItem(eq("A"));

        reset(view);
        navItem = tree.getItemById("A3");
        presenter.openItem(navItem);
        assertEquals(presenter.getNavItemStack().size(), 3);

        verify(view, times(1)).addTileWidget(tileWidget);
        verify(view).clearBreadcrumb();
        verify(view).addBreadcrumbItem(eq("Home"), any());
        verify(view).addBreadcrumbItem(eq("A"), any());
        verify(view).addBreadcrumbItem(eq("A3"));
    }

    @Test
    public void testGotoHome() {
        NavItem navItem = tree.getItemById("A");
        presenter.openItem(navItem);
        reset(view);

        NavItem homeItem = tree.getItemById("Home");
        presenter.gotoBreadcrumbItem(homeItem);
        assertEquals(presenter.getNavItemStack().size(), 0);

        verify(view, times(1)).addTileWidget(tileWidget);
        verify(view).clearBreadcrumb();
        verify(view, never()).addBreadcrumbItem(any(), any());
    }
}