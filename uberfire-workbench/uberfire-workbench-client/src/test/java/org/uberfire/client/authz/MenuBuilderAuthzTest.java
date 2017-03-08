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

package org.uberfire.client.authz;

import java.util.Arrays;
import java.util.Collections;

import org.jboss.errai.security.shared.api.RoleImpl;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.menu.AuthFilterMenuVisitor;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.security.authz.PermissionManager;
import org.uberfire.security.impl.authz.DefaultAuthorizationManager;
import org.uberfire.security.impl.authz.DefaultPermissionManager;
import org.uberfire.workbench.model.menu.MenuCustom;
import org.uberfire.workbench.model.menu.MenuGroup;
import org.uberfire.workbench.model.menu.MenuItemCommand;
import org.uberfire.workbench.model.menu.MenuItemPerspective;
import org.uberfire.workbench.model.menu.MenuItemPlain;
import org.uberfire.workbench.model.menu.MenuVisitor;
import org.uberfire.workbench.model.menu.Menus;

import static org.mockito.Mockito.*;
import static org.uberfire.workbench.model.menu.MenuFactory.newSimpleItem;
import static org.uberfire.workbench.model.menu.MenuFactory.newTopLevelMenu;

@RunWith(MockitoJUnitRunner.class)
public class MenuBuilderAuthzTest {

    @Mock
    User user;
    @Spy
    MenuVisitor menuVisitor = new CustomVisitor();
    Menus menus;
    AuthFilterMenuVisitor authzVisitor;
    AuthorizationManager authorizationManager;
    PermissionManager permissionManager;

    @Before
    public void setUp() {
        when(user.getRoles()).thenReturn(Collections.singleton(new RoleImpl("admin")));

        menus = newTopLevelMenu("Group1")
                .withPermission("group1")
                .withItems(Arrays.asList(
                        newSimpleItem("Perspective 1").perspective("perspective1").endMenu().build().getItems().get(0),
                        newSimpleItem("Perspective 2").perspective("perspective2").endMenu().build().getItems().get(0)
                ))
                .endMenu().build();

        permissionManager = new DefaultPermissionManager();
        authorizationManager = new DefaultAuthorizationManager(permissionManager);
        authzVisitor = spy(new AuthFilterMenuVisitor(authorizationManager,
                                                     user,
                                                     menuVisitor));
    }

    @Test
    public void testVisit() {
        menus.accept(authzVisitor);

        verify(menuVisitor).visitEnter(any(MenuGroup.class));
        verify(menuVisitor,
               times(2)).visit(any(MenuItemPerspective.class));
    }

    @Test
    public void testVisit2() {
        permissionManager.setAuthorizationPolicy(
                permissionManager.newAuthorizationPolicy()
                        .role("admin")
                        .permission("perspective.read.perspective1",
                                    false)
                        .build());

        menus.accept(authzVisitor);

        verify(menuVisitor).visitEnter(any(MenuGroup.class));
        verify(menuVisitor,
               times(1)).visit(any(MenuItemPerspective.class));
    }

    @Test
    public void testVisit3() {
        permissionManager.setAuthorizationPolicy(
                permissionManager.newAuthorizationPolicy()
                        .role("admin")
                        .permission("perspective.read.perspective1",
                                    false)
                        .permission("perspective.read.perspective2",
                                    false)
                        .build());

        menus.accept(authzVisitor);

        verify(menuVisitor,
               never()).visitEnter(any(MenuGroup.class));
        verify(menuVisitor,
               never()).visit(any(MenuItemPerspective.class));
    }

    @Test
    public void testVisit4() {
        Menus menus = newTopLevelMenu("Group")
                .withItems(Arrays.asList(
                        newSimpleItem("Perspective 1").withPermission("perspective.read.perspective1").endMenu().build().getItems().get(0),
                        newSimpleItem("Perspective 2").withPermission("perspective.read.perspective2").endMenu().build().getItems().get(0)
                ))
                .endMenu().build();

        permissionManager.setAuthorizationPolicy(
                permissionManager.newAuthorizationPolicy()
                        .role("admin")
                        .permission("perspective.read.perspective1",
                                    false)
                        .permission("perspective.read.perspective2",
                                    false)
                        .build());

        menus.accept(authzVisitor);

        verify(menuVisitor,
               never()).visitEnter(any(MenuGroup.class));
        verify(menuVisitor,
               never()).visit(any(MenuItemPerspective.class));
    }

    @Test
    public void testVisit5() {
        permissionManager.setAuthorizationPolicy(
                permissionManager.newAuthorizationPolicy()
                        .role("admin")
                        .permission("group1",
                                    false)
                        .build());

        menus.accept(authzVisitor);

        verify(menuVisitor,
               never()).visitEnter(any(MenuGroup.class));
        verify(menuVisitor,
               never()).visit(any(MenuItemPerspective.class));
    }

    private class CustomVisitor implements MenuVisitor {

        @Override
        public boolean visitEnter(Menus menus) {
            return true;
        }

        @Override
        public void visitLeave(Menus menus) {

        }

        @Override
        public boolean visitEnter(MenuGroup menuGroup) {
            return true;
        }

        @Override
        public void visitLeave(MenuGroup menuGroup) {

        }

        @Override
        public void visit(MenuItemPlain menuItemPlain) {

        }

        @Override
        public void visit(MenuItemCommand menuItemCommand) {

        }

        @Override
        public void visit(MenuItemPerspective menuItemPerspective) {

        }

        @Override
        public void visit(MenuCustom<?> menuCustom) {

        }
    }
}