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
import org.dashbuilder.navigation.workbench.NavWorkbenchCtx;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.mvp.PlaceManager;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class NavTabListWidgetTest {

    @Mock
    NavTabListWidget.View view;

    @Mock
    NavTabListWidget.View viewAdmin;

    @Mock
    NavTabListWidget.View viewDashboards;

    @Mock
    SyncBeanDef<NavTabListWidget> tablistBean;

    @Mock
    PerspectivePluginManager pluginManager;

    @Mock
    NavigationManager navigationManager;

    @Mock
    SyncBeanManager beanManager;

    @Mock
    PlaceManager placeManager;

    NavTabListWidget tabsAdmin;
    NavTabListWidget tabsDashboards;
    NavTabListWidget presenter;
    NavTree tree;

    public static final String ITEM_ID_HOME = "home";
    public static final String ITEM_ID_GALLERY = "gallery";
    public static final String ITEM_ID_ADMIN = "admin";
    public static final String ITEM_ID_DATASETS = "datasets";
    public static final String ITEM_ID_CONTENTMGMT = "contentmgmt";
    public static final String ITEM_ID_DASHBOARDS = "dashboards";
    public static final String ITEM_ID_DASHBOARD1 = "dashboard1";
    public static final String ITEM_ID_DASHBOARD2 = "dashboard2";

    @Before
    public void setUp() throws Exception {
        tabsAdmin = new NavTabListWidget(viewAdmin, beanManager, pluginManager, placeManager, navigationManager);
        tabsDashboards = new NavTabListWidget(viewDashboards, beanManager, pluginManager, placeManager, navigationManager);
        presenter = new NavTabListWidget(view, beanManager, pluginManager, placeManager, navigationManager);
        presenter.setGotoItemEnabled(true);

        tree = new NavTreeBuilder()
                .item(ITEM_ID_HOME, "Home", null, false, NavWorkbenchCtx.perspective(ITEM_ID_HOME))
                .item(ITEM_ID_GALLERY, "Gallery", null, false, NavWorkbenchCtx.perspective(ITEM_ID_GALLERY))
                .group(ITEM_ID_ADMIN, "Administration", null, false)
                    .item(ITEM_ID_DATASETS, "Datasets", null, false, NavWorkbenchCtx.perspective(ITEM_ID_DATASETS))
                    .item(ITEM_ID_CONTENTMGMT, "Content Manager", null, false, NavWorkbenchCtx.perspective(ITEM_ID_CONTENTMGMT))
                    .endGroup()
                .group(ITEM_ID_DASHBOARDS, "Dashboards", null, false)
                    .item(ITEM_ID_DASHBOARD1, "Dashboard 1", null, false, NavWorkbenchCtx.perspective(ITEM_ID_DASHBOARD1))
                    .item(ITEM_ID_DASHBOARD2, "Dashboard 2", null, false, NavWorkbenchCtx.perspective(ITEM_ID_DASHBOARD2))
                    .endGroup()
                .build();

        when(beanManager.lookupBean(NavTabListWidget.class)).thenReturn(tablistBean);
        when(tablistBean.newInstance()).thenReturn(tabsAdmin, tabsDashboards);
        when(pluginManager.isRuntimePerspective(any(NavItem.class))).thenReturn(true);
    }

    @Test
    public void testShow() {
        presenter.show(tree);
        assertEquals(presenter.getItemSelected(), tree.getItemById(ITEM_ID_HOME));

        verify(view).init(presenter);
        verify(view).addItem(eq(ITEM_ID_HOME), anyString(), anyString(), any());
        verify(view).addItem(eq(ITEM_ID_GALLERY), anyString(), anyString(), any());
        verify(view).addGroupItem(eq(ITEM_ID_ADMIN), anyString(), anyString(), eq(tabsAdmin));
        verify(view).addGroupItem(eq(ITEM_ID_DASHBOARDS), anyString(), anyString(), eq(tabsDashboards));
        verify(view).setSelectedItem(ITEM_ID_HOME);

        verify(viewAdmin).showAsSubmenu(true);
        verify(viewAdmin, never()).showChildrenTabs(any());
        verify(viewAdmin).addItem(eq(ITEM_ID_DATASETS), anyString(), anyString(), any());
        verify(viewAdmin).addItem(eq(ITEM_ID_CONTENTMGMT), anyString(), anyString(), any());

        verify(viewDashboards).showAsSubmenu(true);
        verify(viewDashboards, never()).showChildrenTabs(any());
        verify(viewDashboards).addItem(eq(ITEM_ID_DASHBOARD1), anyString(), anyString(), any());
        verify(viewDashboards).addItem(eq(ITEM_ID_DASHBOARD2), anyString(), anyString(), any());
    }

    @Test
    public void testDefaultNestedItem() {
        presenter.setDefaultNavItemId(ITEM_ID_DASHBOARD2);
        presenter.show(tree);

        assertEquals(presenter.getItemSelected(), tree.getItemById(ITEM_ID_DASHBOARD2));
        assertEquals(tabsDashboards.getItemSelected(), tree.getItemById(ITEM_ID_DASHBOARD2));
        assertNull(tabsAdmin.getItemSelected());

        verify(view, atLeastOnce()).clearSelectedItem();
        verify(view, atLeastOnce()).setSelectedItem(ITEM_ID_DASHBOARDS);
        verify(view).showChildrenTabs(tabsDashboards);
        verify(viewDashboards).setSelectedItem(ITEM_ID_DASHBOARD2);
        verify(viewAdmin, never()).showChildrenTabs(any());
    }


    @Test
    public void testSelectNestedItem() {
        presenter.show(tree);
        reset(view, viewAdmin, viewDashboards);

        presenter.setSelectedItem(ITEM_ID_CONTENTMGMT);
        assertEquals(presenter.getItemSelected(), tree.getItemById(ITEM_ID_CONTENTMGMT));
        assertEquals(tabsAdmin.getItemSelected(), tree.getItemById(ITEM_ID_CONTENTMGMT));
        assertNull(tabsDashboards.getItemSelected());

        verify(view).clearSelectedItem();
        verify(view).showChildrenTabs(any());
        verify(viewAdmin).setSelectedItem(ITEM_ID_CONTENTMGMT);
        verify(viewDashboards, never()).showChildrenTabs(any());
    }

    @Test
    public void testSwitchFromNestedToRoot() {
        presenter.show(tree);
        presenter.setSelectedItem(ITEM_ID_CONTENTMGMT);
        reset(view, viewAdmin, viewDashboards);

        presenter.onItemClicked(tree.getItemById(ITEM_ID_HOME));
        assertEquals(presenter.getItemSelected(), tree.getItemById(ITEM_ID_HOME));
        assertNull(tabsAdmin.getItemSelected());
        assertNull(tabsDashboards.getItemSelected());

        verify(view).clearSelectedItem();
        verify(view).setSelectedItem(ITEM_ID_HOME);
        verify(view, never()).showChildrenTabs(any());
        verify(viewAdmin, never()).showChildrenTabs(any());
        verify(viewDashboards, never()).showChildrenTabs(any());
    }
}