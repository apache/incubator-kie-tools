/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.ext.widgets.common.client.menu;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.widgets.common.client.menu.MenuItemFactory.MenuItemViewHolder;
import org.uberfire.mvp.Command;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MenuItemFactoryTest {

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

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        menuItemFactory = new MenuItemFactory(menuItemViewProducer);
        menuItemFactoryViewMocks.clear();

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
            doCallRealMethod().when(v).onClickListItem(any(ClickEvent.class));
            doCallRealMethod().when(v).setClickHandler(any(ClickHandler.class));
            menuItemFactoryViewMocks.add(v);
            return v;
        });
        when(menuItemViewDividerProducer.get()).then(invocation -> {
            final MenuItemDividerView v = mock(MenuItemDividerView.class);
            menuItemFactoryViewMocks.add(v);
            return v;
        });
    }

    @Test
    public void checkMenuItemHeaderConstruction() {
        final MenuItemViewHolder<MenuItemHeaderView> mih = menuItemFactory.makeMenuItemHeader("caption");

        assertNotNull(mih);
        assertNotNull(mih.getMenuItem());
        assertNotNull(mih.getMenuItemView());
    }

    @Test
    public void checkMenuItemWithIconConstruction() {
        final Command c = mock(Command.class);
        final MenuItemViewHolder<MenuItemWithIconView> mih = menuItemFactory.makeMenuItemWithIcon("caption",
                                                                                                  c);

        assertNotNull(mih);
        assertNotNull(mih.getMenuItem());
        assertNotNull(mih.getMenuItemView());
    }

    @Test
    public void checkMenuItemDividerConstruction() {
        final MenuItemViewHolder<MenuItemDividerView> mih = menuItemFactory.makeMenuItemDivider();

        assertNotNull(mih);
        assertNotNull(mih.getMenuItem());
        assertNotNull(mih.getMenuItemView());
    }

    @Test
    public void checkMenuItemWithIconEnabled() {
        final Command c = mock(Command.class);
        final MenuItemViewHolder<MenuItemWithIconView> mih = menuItemFactory.makeMenuItemWithIcon("caption",
                                                                                                  c);

        mih.getMenuItem().setEnabled(true);
        verify(mih.getMenuItemView(),
               times(1)).setEnabled(eq(true));

        mih.getMenuItemView().onClickListItem(mock(ClickEvent.class));

        verify(c,
               times(1)).execute();
    }

    @Test
    public void checkMenuItemWithIconDisabled() {
        final Command c = mock(Command.class);
        final MenuItemViewHolder<MenuItemWithIconView> mih = menuItemFactory.makeMenuItemWithIcon("caption",
                                                                                                  c);

        mih.getMenuItem().setEnabled(false);
        verify(mih.getMenuItemView(),
               times(1)).setEnabled(eq(false));

        mih.getMenuItemView().onClickListItem(mock(ClickEvent.class));

        verify(c,
               never()).execute();
    }
}
