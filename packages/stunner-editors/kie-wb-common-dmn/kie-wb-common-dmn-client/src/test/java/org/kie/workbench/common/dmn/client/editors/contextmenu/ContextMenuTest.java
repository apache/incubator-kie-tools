/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.editors.contextmenu;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl.ListSelectorHeaderItem;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl.ListSelectorTextItem;
import org.uberfire.mvp.Command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ContextMenuTest {

    private ContextMenu contextMenu;
    private ContextMenuView view;
    public static final String TITLE = "TITLE";
    public static final Command DO_NOTHING = () -> {
    };

    @Before
    public void setUp() {
        view = mock(ContextMenuView.class);

        contextMenu = new ContextMenu(view);
    }

    @Test
    public void testWhenInitContextMenuThenRelatedViewIsInit() {
        contextMenu.init();
        verify(view).init(contextMenu);
    }

    @Test
    public void testWhenShowingContextMenuThenRelatedViewIsShown() {
        contextMenu.show();
        verify(view).show();
    }

    @Test
    public void testWhenShowingContextMenuAndCustomActionsArePassedThenRelatedViewIsShown() {
        contextMenu.show(self -> self.addTextMenuItem(TITLE, true, DO_NOTHING));

        verify(view).show();
        verifyTextMenuItem();
    }

    @Test
    public void testWhenHidingContextMenuThenRelatedViewIsHidden() {
        final ContextMenu contextMenu = new ContextMenu(view);
        contextMenu.hide();
        verify(view).hide();
    }

    @Test
    public void testWhenGettingContextMenuElementThenRelatedViewElementIsGot() {
        contextMenu.getElement();
        verify(view).getElement();
    }

    @Test
    public void testWhenGettingFreshContextMenuItemsThenListIsEmpty() {
        assertThat(contextMenu.getItems()).isNotNull();
        assertThat(contextMenu.getItems()).isEmpty();
    }

    @Test
    public void testWhenSettingHeaderForContextMenuThenItemListContainsHeader() {
        final String iconClass = "icon-class";

        contextMenu.setHeaderMenu(TITLE, iconClass);

        assertThat(contextMenu.getItems()).isNotNull();
        assertThat(contextMenu.getItems()).isNotEmpty();
        assertThat(contextMenu.getItems().size()).isEqualTo(1);
        ListSelectorHeaderItem headerItem = (ListSelectorHeaderItem) contextMenu.getItems().get(0);
        assertThat(headerItem.getText()).isEqualTo(TITLE);
        assertThat(headerItem.getIconClass()).isEqualTo(iconClass);
    }

    @Test
    public void testAddingTextMenuItemForContextMenuThenItemListContainsIt() {
        contextMenu.addTextMenuItem(TITLE, true, DO_NOTHING);

        verifyTextMenuItem();
    }

    private void verifyTextMenuItem() {
        assertThat(contextMenu.getItems()).isNotNull();
        assertThat(contextMenu.getItems()).isNotEmpty();
        assertThat(contextMenu.getItems().size()).isEqualTo(1);
        ListSelectorTextItem textItem = (ListSelectorTextItem) contextMenu.getItems().get(0);
        assertThat(textItem.getText()).isEqualTo(TITLE);
        assertThat(textItem.isEnabled()).isEqualTo(true);
        assertThat(textItem.getCommand()).isEqualTo(DO_NOTHING);
    }
}
