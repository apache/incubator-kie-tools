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

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ButtonGroup;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.gwtbootstrap3.client.ui.PanelBody;
import org.gwtbootstrap3.client.ui.PanelHeader;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.client.views.pfly.maximize.MaximizeToggleButton;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.commons.data.Pair;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.security.Resource;
import org.uberfire.security.ResourceAction;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.security.authz.Permission;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ListBarWidgetImplTest {

    @Mock
    AuthorizationManager authzManager;

    @Mock
    User identity;

    @Mock
    PanelManager panelManager;

    @Spy
    @InjectMocks
    ListBarWidgetImpl listBar;

    @Before
    public void setUp() throws Exception {
        when(authzManager.authorize(any(Permission.class),
                                    any(User.class))).thenReturn(true);
        when(authzManager.authorize(any(Resource.class),
                                    any(User.class))).thenReturn(true);
        when(authzManager.authorize(any(Resource.class),
                                    any(ResourceAction.class),
                                    any(User.class))).thenReturn(true);

        doNothing().when(listBar).setupContextMenu();

        listBar.contextMenu = mock(ButtonGroup.class);
        listBar.titleDropDown = mock(PartListDropdown.class);
        listBar.content = mock(PanelBody.class);
        listBar.header = mock(PanelHeader.class);
        listBar.maximizeButton = mock(MaximizeToggleButton.class);

        final Element element = mock(Element.class);
        final Style style = mock(Style.class);
        doReturn(style).when(element).getStyle();
        doReturn(element).when(listBar.content).getElement();
    }

    @Test
    public void onSelectPartOnPartHiddenEventIsFiredTest() {
        final PartDefinition selectedPart = mock(PartDefinition.class);
        final PartDefinition currentPart = mock(PartDefinition.class);

        listBar.partContentView.put(selectedPart,
                                    new FlowPanel());
        listBar.parts.add(selectedPart);
        listBar.currentPart = Pair.newPair(currentPart,
                                           new FlowPanel());
        listBar.partContentView.put(currentPart,
                                    new FlowPanel());

        listBar.selectPart(selectedPart);

        verify(panelManager).onPartHidden(currentPart);
        verify(listBar).resizePanelBody();
    }

    @Test
    public void partsIsAddedToListBarTest() {
        final PartDefinition firstPart = mock(PartDefinition.class);
        final PartDefinition secondPart = mock(PartDefinition.class);

        listBar.parts.add(firstPart);
        listBar.parts.add(secondPart);

        assertEquals(2,
                     listBar.getParts().size());
    }

    @Test
    public void changeTitleForSelectablePart() {
        final PartDefinition part = getPartDefinition(true,
                                                      false);
        final IsWidget widget = mock(IsWidget.class);

        listBar.changeTitle(part,
                            "title",
                            widget);

        verify(listBar.titleDropDown).changeTitle(part,
                                                  "title",
                                                  widget);
    }

    @Test
    public void changeTitleForUnselectablePart() {
        final PartDefinition part = getPartDefinition(false,
                                                      false);
        final IsWidget widget = mock(IsWidget.class);

        listBar.changeTitle(part,
                            "title",
                            widget);

        verify(listBar.titleDropDown,
               never()).changeTitle(part,
                                    "title",
                                    widget);
    }

    @Test
    public void addNewSelectablePartTest() {
        final PartDefinition part = getPartDefinition(true,
                                                      false);
        final WorkbenchPartPresenter presenter = getWorkbenchPartPresenter(part);
        final WorkbenchPartPresenter.View view = getWorkbenchPartView(presenter);

        listBar.parts.add(getPartDefinition(false,
                                            true));

        listBar.addPart(view);

        verify(listBar,
               never()).selectPart(part);
        verify(listBar.titleDropDown).addPart(view);
        verify(listBar).setupCSSLocators(any(),
                                         any());

        assertSame(part,
                   listBar.parts.getFirst());
    }

    @Test
    public void addNewUnselectablePartTest() {
        final PartDefinition part = getPartDefinition(false,
                                                      false);
        final WorkbenchPartPresenter presenter = getWorkbenchPartPresenter(part);
        final WorkbenchPartPresenter.View view = getWorkbenchPartView(presenter);

        listBar.addPart(view);

        verify(listBar,
               never()).selectPart(part);
        verify(listBar.titleDropDown,
               never()).addPart(view);
        verify(listBar).resizePanelBody();
    }

    @Test
    public void addExistentPartTest() {
        final PartDefinition part = getPartDefinition(true,
                                                      true);
        final WorkbenchPartPresenter presenter = getWorkbenchPartPresenter(part);
        final WorkbenchPartPresenter.View view = getWorkbenchPartView(presenter);

        listBar.addPart(view);

        verify(listBar).selectPart(part);
        verify(listBar.titleDropDown,
               never()).addPart(view);
        verify(listBar).resizePanelBody();
    }

    @Test
    public void selectNewPartTest() {
        final PartDefinition part = getPartDefinition(true,
                                                      false);

        final boolean selected = listBar.selectPart(part);

        assertFalse(selected);
        verify(listBar.titleDropDown,
               never()).selectPart(part);
        verify(listBar,
               never()).setupContextMenu();
        verify(listBar.header,
               never()).setVisible(anyBoolean());
        verify(listBar,
               never()).resizePanelBody();
    }

    @Test
    public void selectExistentUnselectablePartTest() {
        final PartDefinition part = getPartDefinition(false,
                                                      true);

        final boolean selected = listBar.selectPart(part);

        assertTrue(selected);
        verify(listBar.titleDropDown,
               never()).selectPart(part);
        verify(listBar,
               never()).setupContextMenu();
        verify(listBar.header).setVisible(false);
        verify(listBar).resizePanelBody();
    }

    @Test
    public void selectExistentSelectablePartTest() {
        final PartDefinition currentPart = getPartDefinition(false,
                                                             false);
        listBar.currentPart = Pair.newPair(currentPart,
                                           new FlowPanel());

        final PartDefinition selectedPart = getPartDefinition(true,
                                                              true);

        final boolean selected = listBar.selectPart(selectedPart);

        assertTrue(selected);
        verify(listBar.titleDropDown).selectPart(selectedPart);
        verify(listBar).setupContextMenu();
        verify(listBar.header).setVisible(true);
        verify(listBar).resizePanelBody();

        assertEquals(1, listBar.parts.size());
        assertSame(currentPart, listBar.parts.getFirst());
        assertSame(selectedPart, listBar.currentPart.getK1());
    }

    @Test
    public void removeUnselectablePartTest() {
        final PartDefinition part = getPartDefinition(false,
                                                      true);

        listBar.remove(part);

        verify(listBar.titleDropDown,
               never()).removePart(part);
        verify(listBar).resizePanelBody();
    }

    @Test
    public void notifyPartHiddenOnRemoveTest() {
        final PartDefinition part = getPartDefinition(true,
                                                      true);
        listBar.selectPart(part);
        listBar.remove(part);

        verify(listBar.titleDropDown).removePart(part);
        verify(panelManager).onPartHidden(part);
    }

    @Test
    public void removeSelectablePartTest() {
        final PartDefinition part = getPartDefinition(true,
                                                      true);

        listBar.remove(part);

        verify(listBar.titleDropDown).removePart(part);
        verify(listBar).resizePanelBody();
    }

    private PartDefinition getPartDefinition(final boolean selectable,
                                             final boolean existent) {
        final PartDefinition part = mock(PartDefinition.class);
        doReturn(selectable).when(part).isSelectable();
        doReturn("").when(part).asString();

        if (existent) {
            listBar.partContentView.put(part,
                                        new FlowPanel());
            listBar.parts.add(part);
            listBar.partContentView.put(part,
                                        new FlowPanel());
        }

        return part;
    }

    private WorkbenchPartPresenter getWorkbenchPartPresenter(final PartDefinition part) {
        final WorkbenchPartPresenter presenter = mock(WorkbenchPartPresenter.class);
        doReturn(part).when(presenter).getDefinition();
        return presenter;
    }

    private WorkbenchPartPresenter.View getWorkbenchPartView(final WorkbenchPartPresenter presenter) {
        final WorkbenchPartPresenter.View view = mock(WorkbenchPartPresenter.View.class);
        doReturn(presenter).when(view).getPresenter();
        return view;
    }

    @Test
    public void testSingleMenu() {
        final String caption = "test";
        final Menus menus = MenuFactory.newTopLevelMenu(caption).respondsWith(() -> {
        }).endMenu().build();

        final Widget widget = listBar.makeItem(menus.getItems().get(0),
                                               true);

        assertTrue(widget instanceof Button);
        verify((Button) widget).setText(caption);
    }

    @Test
    public void testSubMenus() {
        final String caption = "test";
        final String submenu1 = "submenu1";
        final String submenu2 = "submenu2";
        final Menus menus = MenuFactory.newTopLevelMenu(caption)
                .menus()
                .menu(submenu1).respondsWith(() -> {
                }).endMenu()
                .menu(submenu2).respondsWith(() -> {
                }).endMenu()
                .endMenus()
                .endMenu()
                .build();

        final Widget widget = listBar.makeItem(menus.getItems().get(0),
                                               true);

        assertTrue(widget instanceof ButtonGroup);

        ArgumentCaptor<Widget> buttonCaptor = ArgumentCaptor.forClass(Widget.class);
        verify(((ButtonGroup) widget),
               times(2)).add(buttonCaptor.capture());

        final List<Widget> widgetList = buttonCaptor.getAllValues();
        assertEquals(2,
                     widgetList.size());
        verify((Button) widgetList.get(0)).setText(caption);

        ArgumentCaptor<Widget> dropCaptor = ArgumentCaptor.forClass(Widget.class);

        verify((DropDownMenu) widgetList.get(1),
               times(2)).add(dropCaptor.capture());

        final List<Widget> subMenusWidgetList = dropCaptor.getAllValues();
        assertEquals(2,
                     subMenusWidgetList.size());
        verify((AnchorListItem) subMenusWidgetList.get(0)).setText(submenu1);
        verify((AnchorListItem) subMenusWidgetList.get(1)).setText(submenu2);
    }

    @Test
    public void getNextSelectablePartTest() {
        PartDefinition part1 = new PartDefinitionImpl(mock(PlaceRequest.class));
        part1.setSelectable(false);
        PartDefinition part2 = new PartDefinitionImpl(mock(PlaceRequest.class));
        part2.setSelectable(true);
        PartDefinition part3 = new PartDefinitionImpl(mock(PlaceRequest.class));
        part3.setSelectable(false);
        PartDefinition part4 = new PartDefinitionImpl(mock(PlaceRequest.class));
        part4.setSelectable(true);
        PartDefinition part5 = new PartDefinitionImpl(mock(PlaceRequest.class));
        part5.setSelectable(false);

        LinkedHashSet<PartDefinition> parts = new LinkedHashSet<>();
        parts.add(part1);
        parts.add(part2);
        parts.add(part3);
        parts.add(part4);
        parts.add(part5);
        doReturn(parts).when(listBar).getUnselectedParts();

        assertSame(part2, listBar.getNextPart(part1));
        assertSame(part4, listBar.getNextPart(part2));
        assertSame(part2, listBar.getNextPart(part3));
        assertSame(part2, listBar.getNextPart(part4));
        assertSame(part2, listBar.getNextPart(part5));
    }

    @Test
    public void getFirstUnselectablePartTest() {
        PartDefinition part1 = new PartDefinitionImpl(mock(PlaceRequest.class));
        part1.setSelectable(false);
        PartDefinition part2 = new PartDefinitionImpl(mock(PlaceRequest.class));
        part2.setSelectable(true);
        PartDefinition part3 = new PartDefinitionImpl(mock(PlaceRequest.class));
        part3.setSelectable(false);

        LinkedHashSet<PartDefinition> parts = new LinkedHashSet<>();
        parts.add(part1);
        parts.add(part2);
        parts.add(part3);
        doReturn(parts).when(listBar).getUnselectedParts();

        assertSame(part2, listBar.getNextPart(part1));
        assertSame(part1, listBar.getNextPart(part2));
        assertSame(part2, listBar.getNextPart(part3));
    }

    @Test
    public void resizePanelBodyForUnselectablePart() {
        final PartDefinitionImpl partDefinition = new PartDefinitionImpl(mock(PlaceRequest.class));
        partDefinition.setSelectable(false);
        listBar.currentPart = new Pair<>(partDefinition,
                                         mock(FlowPanel.class));

        listBar.resizePanelBody();

        verify(listBar.content.getElement().getStyle()).setProperty("height",
                                                                    "100%");
    }

    @Test
    public void resizePanelBodyForSelectablePart() {
        final PartDefinitionImpl partDefinition = new PartDefinitionImpl(mock(PlaceRequest.class));
        partDefinition.setSelectable(true);
        listBar.currentPart = new Pair<>(partDefinition,
                                         mock(FlowPanel.class));
        doReturn(10).when(listBar.header).getOffsetHeight();

        listBar.resizePanelBody();

        verify(listBar.content.getElement().getStyle()).setProperty("height",
                                                                    "calc(100% - 10px)");
    }

    @Test
    public void getPartsTest() {
        listBar.currentPart = Pair.newPair(getPartDefinition(false,
                                                             false),
                                           null);
        listBar.parts = new LinkedList<>();
        listBar.parts.add(getPartDefinition(false,
                                            false));

        final List<PartDefinition> parts = (List<PartDefinition>) listBar.getParts();

        assertSame(listBar.currentPart.getK1(),
                   parts.get(0));
        assertSame(listBar.parts.get(0),
                   parts.get(1));
    }

    @Test
    public void disableExpandPart() {
        listBar.disableExpandPart();
        verify(listBar.maximizeButton).setVisible(false);
    }
}