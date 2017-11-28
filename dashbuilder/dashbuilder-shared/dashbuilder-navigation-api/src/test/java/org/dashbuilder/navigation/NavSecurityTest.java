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
package org.dashbuilder.navigation;

import java.util.ArrayList;
import java.util.List;

import org.dashbuilder.navigation.impl.NavTreeBuilder;
import org.dashbuilder.navigation.workbench.NavSecurityController;
import org.dashbuilder.navigation.workbench.NavWorkbenchCtx;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.security.authz.AuthorizationManager;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class NavSecurityTest {

    public static final String ITEM_HOME_ID = "home";
    public static final String ITEM_ADMIN_ID = "admin";
    public static final String ITEM_SECURITY_ID = "security";
    public static final String ITEM_DATASETS_ID = "datasets";

    @Mock
    AuthorizationManager authorizationManager;

    @Mock
    User user;

    NavTree tree;
    NavSecurityController controller;

    @Before
    public void setUp() {
        tree = new NavTreeBuilder()
                .item(ITEM_HOME_ID, "Home", null, false, NavWorkbenchCtx.permission(ITEM_HOME_ID))
                .divider()
                .group(ITEM_ADMIN_ID, "Administration", null, false)
                .item(ITEM_SECURITY_ID, "Security", null, false, NavWorkbenchCtx.permission(ITEM_SECURITY_ID))
                .item(ITEM_DATASETS_ID, "Data sets", null, false, NavWorkbenchCtx.permission(ITEM_DATASETS_ID))
                .build();

        controller = new NavSecurityController(authorizationManager, user);
    }

    @Test
    public void testTreeStructure() {
        when(authorizationManager.authorize(ITEM_HOME_ID, user)).thenReturn(true);
        when(authorizationManager.authorize(ITEM_SECURITY_ID, user)).thenReturn(false);
        when(authorizationManager.authorize(ITEM_DATASETS_ID, user)).thenReturn(true);

        NavTree securedTree = controller.secure(tree, true);

        List<NavItem> rootNavItems = securedTree.getRootItems();
        assertEquals(rootNavItems.size(), 3);

        NavItem admin = securedTree.getItemById(ITEM_ADMIN_ID);
        assertTrue(admin instanceof NavGroup);
        assertEquals(((NavGroup) admin).getChildren().size(), 1);
    }

    @Test
    public void testHideEmptyGroups() {
        when(authorizationManager.authorize(ITEM_HOME_ID, user)).thenReturn(true);
        when(authorizationManager.authorize(ITEM_SECURITY_ID, user)).thenReturn(false);
        when(authorizationManager.authorize(ITEM_DATASETS_ID, user)).thenReturn(false);

        NavTree securedTree = controller.secure(tree, true);
        NavItem admin = securedTree.getItemById(ITEM_ADMIN_ID);
        assertEquals(securedTree.getRootItems().size(), 2);
        assertNull(admin);

        List<NavItem> navItems = new ArrayList<>(tree.getRootItems());
        controller.secure(navItems, true);
        assertEquals(navItems.size(), 2);
    }
}
