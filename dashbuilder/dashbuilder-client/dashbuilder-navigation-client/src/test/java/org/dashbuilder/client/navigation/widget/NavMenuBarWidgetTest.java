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
import org.dashbuilder.navigation.NavGroup;
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

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class NavMenuBarWidgetTest {

    @Mock
    NavMenuBarWidget.View view;

    @Mock
    NavDropDownWidget.View viewAdmin;

    @Mock
    NavDropDownWidget.View viewDashboards;

    @Mock
    SyncBeanDef<NavDropDownWidget> dropDownBean;

    @Mock
    PerspectivePluginManager pluginManager;

    @Mock
    NavigationManager navigationManager;

    @Mock
    SyncBeanManager beanManager;

    @Mock
    PlaceManager placeManager;

    NavDropDownWidget dropDownAdmin;
    NavDropDownWidget dropDownDashboards;
    NavMenuBarWidget presenter;
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
        dropDownAdmin = new NavDropDownWidget(viewAdmin, beanManager, navigationManager);
        dropDownDashboards = new NavDropDownWidget(viewDashboards, beanManager, navigationManager);
        presenter = new NavMenuBarWidget(view, beanManager, pluginManager, placeManager, navigationManager);

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

        when(beanManager.lookupBean(NavDropDownWidget.class)).thenReturn(dropDownBean);
        when(dropDownBean.newInstance()).thenReturn(dropDownAdmin, dropDownDashboards);
    }

    @Test
    public void testShowMenuBar() {
        presenter.show(tree);

        verify(view).init(presenter);

        verify(view, never()).setSelectedItem(anyString());
        verify(view).addItem(eq(ITEM_ID_HOME), anyString(), anyString(), any());
        verify(view).addItem(eq(ITEM_ID_GALLERY), anyString(), anyString(), any());
        verify(view).addGroupItem(eq(ITEM_ID_ADMIN), anyString(), anyString(), eq(dropDownAdmin));
        verify(view).addGroupItem(eq(ITEM_ID_DASHBOARDS), anyString(), anyString(), eq(dropDownDashboards));

        verify(viewAdmin).setDropDownName("Administration");
        verify(viewAdmin, never()).setActive(true);
        verify(viewAdmin).addItem(eq(ITEM_ID_DATASETS), anyString(), anyString(), any());
        verify(viewAdmin).addItem(eq(ITEM_ID_CONTENTMGMT), anyString(), anyString(), any());

        verify(viewDashboards).setDropDownName("Dashboards");
        verify(viewDashboards, never()).setActive(true);
        verify(viewDashboards).addItem(eq(ITEM_ID_DASHBOARD1), anyString(), anyString(), any());
        verify(viewDashboards).addItem(eq(ITEM_ID_DASHBOARD2), anyString(), anyString(), any());
    }

    @Test
    public void testSelectRootItem() {
        presenter.show(tree);
        reset(view, viewAdmin, viewDashboards);

        presenter.setSelectedItem(ITEM_ID_HOME);

        verify(view).setSelectedItem(ITEM_ID_HOME);
        verify(viewAdmin, never()).setActive(true);
        verify(viewDashboards, never()).setActive(true);
    }

    @Test
    public void testSelectNestedItem() {
        presenter.show(tree);
        reset(view, viewAdmin, viewDashboards);

        presenter.setSelectedItem(ITEM_ID_CONTENTMGMT);
        assertEquals(presenter.getItemSelected(), tree.getItemById(ITEM_ID_CONTENTMGMT));
        assertEquals(dropDownAdmin.getItemSelected(), tree.getItemById(ITEM_ID_CONTENTMGMT));
        assertNull(dropDownDashboards.getItemSelected());

        verify(view).clearSelectedItem();
        verify(viewAdmin).setActive(true);
        verify(viewAdmin).setSelectedItem(ITEM_ID_CONTENTMGMT);
        verify(viewDashboards, never()).setActive(true);
    }

    @Test
    public void testSwitchFromNestedToRoot() {
        presenter.show(tree);
        presenter.setSelectedItem(ITEM_ID_CONTENTMGMT);
        reset(view, viewAdmin, viewDashboards);

        presenter.onItemClicked(tree.getItemById(ITEM_ID_HOME));
        assertEquals(presenter.getItemSelected(), tree.getItemById(ITEM_ID_HOME));
        assertNull(dropDownAdmin.getItemSelected());
        assertNull(dropDownDashboards.getItemSelected());

        verify(view).clearSelectedItem();
        verify(view).setSelectedItem(ITEM_ID_HOME);
        verify(viewAdmin).setActive(false);
        verify(viewDashboards, never()).setActive(true);
    }

    @Test
    public void testNullNavGroup() {
        presenter.show((NavGroup) null);
        verify(view).errorNavGroupNotFound();
    }

    @Test
    public void testNullNavTree() {
        presenter.show((NavTree) null);
        verify(view).errorNavItemsEmpty();
    }
}