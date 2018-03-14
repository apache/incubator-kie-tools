/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.client.views.pfly.multiscreen;

import java.util.Optional;

import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLDocument;
import elemental2.dom.HTMLElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.views.pfly.widgets.Button;
import org.uberfire.client.views.pfly.widgets.KebabMenu;
import org.uberfire.client.views.pfly.widgets.KebabMenuItem;
import org.uberfire.security.Resource;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.workbench.model.menu.MenuCustom;
import org.uberfire.workbench.model.menu.MenuGroup;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.MenuItemCommand;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MultiScreenMenuBuilderTest {

    @Mock
    AuthorizationManager authManager;

    @Mock
    User identity;

    @Mock
    HTMLDocument document;

    @Mock
    ManagedInstance<KebabMenu> kebabMenus;

    @Mock
    ManagedInstance<KebabMenuItem> kebabMenuItems;

    @Mock
    ManagedInstance<Button> buttons;

    @InjectMocks
    MultiScreenMenuBuilder menuBuilder;

    @Before
    public void setup() {
        when(authManager.authorize(any(Resource.class),
                                   eq(identity))).thenReturn(true);
        final Button button = mock(Button.class);
        when(button.getElement()).thenReturn(mock(HTMLButtonElement.class));
        when(buttons.get()).thenReturn(button);
        when(kebabMenus.get()).thenReturn(mock(KebabMenu.class));
    }

    @Test
    public void testDeniedPermission() {
        final MenuItem menuItem = mock(MenuItem.class);

        when(authManager.authorize(any(Resource.class),
                                   eq(identity))).thenReturn(false);

        final Optional<HTMLElement> optional = menuBuilder.apply(menuItem);

        assertFalse(optional.isPresent());
        verify(authManager).authorize(menuItem,
                                      identity);
        verifyZeroInteractions(kebabMenus,
                               document);
    }

    @Test
    public void testAllowedPermission() {
        final MenuItem menuItem = mock(MenuItem.class);

        when(authManager.authorize(any(Resource.class),
                                   eq(identity))).thenReturn(false);
        when(authManager.authorize(menuItem,
                                   identity)).thenReturn(true);

        final Optional<HTMLElement> optional = menuBuilder.apply(menuItem);

        assertFalse(optional.isPresent());
        verify(authManager).authorize(menuItem,
                                      identity);
        verifyZeroInteractions(kebabMenus,
                               document);
    }

    @Test
    public void testMenuItemCommand() {
        final MenuItemCommand menuItem = mock(MenuItemCommand.class);

        final Optional<HTMLElement> optional = menuBuilder.apply(menuItem);

        assertTrue(optional.isPresent());
        verify(buttons).get();
        verifyZeroInteractions(kebabMenus);
    }

    @Test
    public void testMenuGroup() {
        final KebabMenu kebabMenu = mock(KebabMenu.class);
        when(kebabMenu.getElement()).thenReturn(mock(HTMLDivElement.class));
        when(kebabMenus.get()).thenReturn(kebabMenu);

        final MenuGroup menuItem = mock(MenuGroup.class);

        final Optional<HTMLElement> optional = menuBuilder.apply(menuItem);

        assertTrue(optional.isPresent());
        verify(kebabMenus).get();
        verifyZeroInteractions(document);
    }

    @Test
    public void testMenuCustom() {
        final MenuCustom menuItem = mock(MenuCustom.class);
        when(menuItem.build()).thenReturn(mock(HTMLElement.class));

        final Optional<HTMLElement> optional = menuBuilder.apply(menuItem);

        assertTrue(optional.isPresent());
        verify(menuItem).build();
        verifyZeroInteractions(kebabMenus,
                               document);
    }
}
