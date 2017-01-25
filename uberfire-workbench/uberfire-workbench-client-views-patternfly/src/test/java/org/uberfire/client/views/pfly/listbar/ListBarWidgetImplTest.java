/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.client.views.pfly.listbar;

import java.util.List;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ButtonGroup;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.commons.data.Pair;
import org.uberfire.security.Resource;
import org.uberfire.security.ResourceAction;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.security.authz.Permission;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class ListBarWidgetImplTest {

    @Mock
    AuthorizationManager authzManager;

    @Mock
    User identity;

    @Spy
    @InjectMocks
    ListBarWidgetImpl listBar;

    @Before
    public void setUp() throws Exception {
        when(authzManager.authorize(any(Permission.class), any(User.class))).thenReturn(true);
        when(authzManager.authorize(any(Resource.class), any(User.class))).thenReturn(true);
        when(authzManager.authorize(any(Resource.class), any(ResourceAction.class), any(User.class))).thenReturn(true);

        doNothing().when(listBar).setupContextMenu();
    }

    @Test
    public void onSelectPartOnPartHiddenEventIsFired() {

        final PartDefinition selectedPart = mock( PartDefinition.class );
        final PartDefinition currentPart = mock( PartDefinition.class );

        listBar.panelManager = mock( PanelManager.class );
        listBar.partContentView.put( selectedPart, new FlowPanel() );
        listBar.parts.add( selectedPart );
        listBar.currentPart = Pair.newPair( currentPart, new FlowPanel() );
        listBar.partContentView.put( currentPart, new FlowPanel() );
        listBar.titleDropDown = mock( PartListDropdown.class );

        listBar.selectPart( selectedPart );

        verify( listBar.panelManager ).onPartHidden( currentPart );

    }

    @Test
    public void partsIsAddedToListBar() {
        final PartDefinition firstPart = mock( PartDefinition.class );
        final PartDefinition secondPart = mock( PartDefinition.class );

        listBar.parts.add( firstPart );
        listBar.parts.add( secondPart );

        assertEquals(2, listBar.getParts().size());

    }

    @Test
    public void testSingleMenu(){
        final String caption = "test";
        final Menus menus = MenuFactory.newTopLevelMenu(caption).respondsWith(() -> {}).endMenu().build();

        final Widget widget = listBar.makeItem( menus.getItems().get(0), true );

        assertTrue(widget instanceof Button);
        verify((Button)widget).setText(caption);
    }

    @Test
    public void testSubMenus(){
        final String caption = "test";
        final String submenu1 = "submenu1";
        final String submenu2 = "submenu2";
        final Menus menus = MenuFactory.newTopLevelMenu(caption)
                .menus()
                    .menu(submenu1).respondsWith(() -> {}).endMenu()
                    .menu(submenu2).respondsWith(() -> {}).endMenu()
                .endMenus()
                .endMenu()
                .build();

        final Widget widget = listBar.makeItem( menus.getItems().get(0), true );

        assertTrue(widget instanceof ButtonGroup);

        ArgumentCaptor<Widget> buttonCaptor = ArgumentCaptor.forClass(Widget.class);
        verify( ((ButtonGroup) widget), times(2)).add(buttonCaptor.capture());

        final List<Widget> widgetList = buttonCaptor.getAllValues();
        assertEquals(2, widgetList.size());
        verify((Button)widgetList.get(0)).setText(caption);

        ArgumentCaptor<Widget> dropCaptor = ArgumentCaptor.forClass(Widget.class);

        verify((DropDownMenu)widgetList.get(1), times(2)).add(dropCaptor.capture());

        final List<Widget> subMenusWidgetList = dropCaptor.getAllValues();
        assertEquals(2, subMenusWidgetList.size());
        verify((AnchorListItem)subMenusWidgetList.get(0)).setText(submenu1);
        verify((AnchorListItem)subMenusWidgetList.get(1)).setText(submenu2);
    }

}