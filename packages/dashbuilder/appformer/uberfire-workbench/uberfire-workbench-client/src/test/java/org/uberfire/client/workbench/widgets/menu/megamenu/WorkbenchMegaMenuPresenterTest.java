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

package org.uberfire.client.workbench.widgets.menu.megamenu;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.function.Consumer;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.Workbench;
import org.uberfire.client.workbench.events.PerspectiveChange;
import org.uberfire.client.workbench.widgets.menu.megamenu.base.CanBeDisabled;
import org.uberfire.client.workbench.widgets.menu.megamenu.base.HasChildren;
import org.uberfire.client.workbench.widgets.menu.megamenu.brand.MegaMenuBrand;
import org.uberfire.client.workbench.widgets.menu.megamenu.contextmenuitem.ChildContextMenuItemPresenter;
import org.uberfire.client.workbench.widgets.menu.megamenu.contextmenuitem.GroupContextMenuItemPresenter;
import org.uberfire.client.workbench.widgets.menu.megamenu.menuitem.ChildMenuItemPresenter;
import org.uberfire.client.workbench.widgets.menu.megamenu.menuitem.GroupMenuItemPresenter;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.workbench.model.ActivityResourceType;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuPosition;
import org.uberfire.workbench.model.menu.Menus;

import com.google.gwt.user.client.ui.IsWidget;

@RunWith(MockitoJUnitRunner.class)
public class WorkbenchMegaMenuPresenterTest {

    private static final String NAME = "testName";
    private static final String PERSPECTIVE_ID = "perspectiveId";

    @Mock
    private PerspectiveManager perspectiveManager;

    @Mock
    private ActivityManager activityManager;

    @Mock
    private ManagedInstance<MegaMenuBrand> megaMenuBrands;

    @Mock
    private PlaceManager placeManager;

    @Mock
    private SessionInfo sessionInfo;

    @Mock
    private ManagedInstance<ChildMenuItemPresenter> childMenuItemPresenters;

    @Mock
    private ManagedInstance<GroupMenuItemPresenter> groupMenuItemPresenters;

    @Mock
    private ManagedInstance<ChildContextMenuItemPresenter> childContextMenuItemPresenters;

    @Mock
    private ManagedInstance<GroupContextMenuItemPresenter> groupContextMenuItemPresenters;

    @Mock
    private Workbench workbench;

    @Mock
    private WorkbenchMegaMenuPresenter.View view;

    private WorkbenchMegaMenuPresenter presenter;

    @Before
    public void setup() {
        doReturn(true).when(megaMenuBrands).isUnsatisfied();
        presenter = spy(new WorkbenchMegaMenuPresenter(perspectiveManager,
                                                       activityManager,
                                                       view,
                                                       megaMenuBrands,
                                                       placeManager,
                                                       childMenuItemPresenters,
                                                       groupMenuItemPresenters,
                                                       childContextMenuItemPresenters,
                                                       groupContextMenuItemPresenters,
                                                       workbench));
        reset(view);
        presenter.selectableMenuItemByIdentifier = spy(new HashMap<>());
        presenter.hasChildrenMenuItemByIdentifier = spy(new HashMap<>());
        presenter.canBeDisabledMenuItemByIdentifier = spy(new HashMap<>());
        presenter.canHideMenuItemByIdentifier = spy(new HashMap<>());

        doReturn(mock(ChildMenuItemPresenter.class)).when(childMenuItemPresenters).get();
        doReturn(mock(ChildContextMenuItemPresenter.class)).when(childContextMenuItemPresenters).get();
    }

    @Test
    public void testAddCurrentPerspective() {
        final Menus menus = MenuFactory.newSimpleItem(NAME).perspective(PERSPECTIVE_ID).endMenu().build();
        final PlaceRequest placeRequest = new DefaultPlaceRequest(PERSPECTIVE_ID);
        final PerspectiveActivity perspectiveActivity = mock(PerspectiveActivity.class);

        when(perspectiveActivity.getPlace()).thenReturn(placeRequest);
        when(perspectiveManager.getCurrentPerspective()).thenReturn(perspectiveActivity);

        presenter.addMenus(menus);
        verify(presenter).selectMenuItem(PERSPECTIVE_ID);
    }

    @Test
    public void testAddPerspective() {
        final Menus menus = MenuFactory.newSimpleItem(NAME).perspective(PERSPECTIVE_ID).endMenu().build();
        final PlaceRequest placeRequest = new DefaultPlaceRequest("anyId");
        final PerspectiveActivity perspectiveActivity = mock(PerspectiveActivity.class);

        when(perspectiveActivity.getPlace()).thenReturn(placeRequest);
        when(perspectiveManager.getCurrentPerspective()).thenReturn(perspectiveActivity);

        presenter.addMenus(menus);

        verify(presenter,
               never()).selectMenuItem(PERSPECTIVE_ID);
    }

    @Test
    public void testPerspectiveChangeEvent() {
        final Menus menus = MenuFactory.newSimpleItem(NAME).perspective(PERSPECTIVE_ID).endMenu().build();
        final PlaceRequest placeRequest = new DefaultPlaceRequest(PERSPECTIVE_ID);
        final PerspectiveChange perspectiveChange = new PerspectiveChange(placeRequest,
                                                                          null,
                                                                          null,
                                                                          PERSPECTIVE_ID);

        presenter.addMenus(menus);
        presenter.onPerspectiveChange(perspectiveChange);

        verify(presenter).selectMenuItem(PERSPECTIVE_ID);
    }


    @Test
    public void testSetupEnableDisableMenuItemCommand() {
        final String label = "command";
        final Command command = mock(Command.class);
        final Menus menus = MenuFactory.newSimpleItem(label).respondsWith(command).endMenu().build();

        presenter.addMenus(menus);

        verify(presenter).enableMenuItem(anyString(), eq(true));

        menus.getItems().get(0).setEnabled(true);
        verify(presenter, times(2)).enableMenuItem(anyString(), eq(true));

        menus.getItems().get(0).setEnabled(false);
        verify(presenter).enableMenuItem(anyString(), eq(false));
    }

    @Test
    public void testSetupEnableDisableMenuItemPlace() {
        final String label = "placeLabel";
        final PlaceRequest place = mock(PlaceRequest.class);
        final Menus menus = MenuFactory.newSimpleItem(label).place(place).endMenu().build();

        presenter.addMenus(menus);
        verify(presenter).enableMenuItem(anyString(), eq(true));

        menus.getItems().get(0).setEnabled(true);
        verify(presenter, times(2)).enableMenuItem(anyString(), eq(true));

        menus.getItems().get(0).setEnabled(false);
        verify(presenter).enableMenuItem(anyString(), eq(false));
    }

    @Test
    public void testSetupEnableDisableMenuItemPerspective() {
        final String label = "perspectiveLabel";
        final String perspectiveId = "perspectiveId";
        final Menus menus = MenuFactory.newSimpleItem(label).perspective(perspectiveId).endMenu().build();

        presenter.addMenus(menus);
        verify(presenter).enableMenuItem(anyString(), eq(true));

        menus.getItems().get(0).setEnabled(true);
        verify(presenter, times(2)).enableMenuItem(anyString(), eq(true));

        menus.getItems().get(0).setEnabled(false);
        verify(presenter).enableMenuItem(anyString(), eq(false));
    }

    @Test
    public void testSetupEnableDisableContextMenuItem() {
        final String contextLabel = "contextLabel";
        final String perspectiveId = "perspectiveId";
        final Menus contextMenus = MenuFactory.newSimpleItem(contextLabel).endMenu().build();
        final PerspectiveActivity activity = mock(PerspectiveActivity.class);
        final PlaceRequest placeRequest = mock(PlaceRequest.class);

        when(activity.getIdentifier()).thenReturn(perspectiveId);
        doAnswer(invocationOnMock -> {
            invocationOnMock.getArgument(0, Consumer.class).accept(contextMenus);
            return null;
        }).when(activity).getMenus(any());
        when(activity.isType(ActivityResourceType.PERSPECTIVE.name())).thenReturn(true);
        when(activityManager.getActivity(placeRequest)).thenReturn(activity);

        presenter.onPerspectiveChange(new PerspectiveChange(placeRequest,
                                                            null,
                                                            contextMenus,
                                                            perspectiveId));
        verify(presenter).enableContextMenuItem(anyString(), eq(true));

        contextMenus.getItems().get(0).setEnabled(true);
        verify(presenter, times(2)).enableContextMenuItem(anyString(), eq(true));

        contextMenus.getItems().get(0).setEnabled(false);
        verify(presenter).enableContextMenuItem(anyString(), eq(false));
    }

    @Test
    public void testMenuInsertionOrder() {
        final String perspectiveId = "perspectiveId";
        final String label = "perspectiveLabel";
        final Menus firstMenus = MenuFactory.newSimpleItem(label).perspective(perspectiveId).endMenu().build();
        final Menus secondMenus = MenuFactory.newSimpleItem(label).orderAll(1).perspective(perspectiveId).endMenu().build();
        final Menus thirdMenus = MenuFactory.newSimpleItem(label).orderAll(2).perspective(perspectiveId).endMenu().build();

        presenter.addMenus(thirdMenus);
        presenter.addMenus(firstMenus);
        presenter.addMenus(secondMenus);

        assertEquals(3,
                     presenter.getAddedMenus().size());
        assertSame(firstMenus,
                   presenter.getAddedMenus().get(0));
        assertSame(secondMenus,
                   presenter.getAddedMenus().get(1));
        assertSame(thirdMenus,
                   presenter.getAddedMenus().get(2));
    }

    @Test
    public void testView() {
        assertEquals(view,
                     presenter.getView());
    }

    @Test
    public void testClear() {
        presenter.clear();

        verify(view).clear();
    }

    @Test(expected = RuntimeException.class)
    public void setupWithMoreThanOneMegaMenuBrandImplementationTest() {
        doReturn(true).when(megaMenuBrands).isAmbiguous();

        presenter.setup();
    }

    @Test
    public void setupWithNoMegaMenuBrandImplementationTest() {
        doReturn(true).when(megaMenuBrands).isUnsatisfied();

        presenter.setup();

        verify(view).hideBrand();
    }

    @Test
    public void setupWithOneEmptyMegaMenuBrandImplementationTest() {
        doReturn(false).when(megaMenuBrands).isAmbiguous();
        doReturn(false).when(megaMenuBrands).isUnsatisfied();

        final MegaMenuBrandMock megaMenuBrand = new MegaMenuBrandMock("",
                                                                      "",
                                                                      "");
        doReturn(megaMenuBrand).when(megaMenuBrands).get();
        doReturn("defaultMenuText").when(view).getDefaultMenuText();

        presenter.setup();

        verify(view,
               never()).setBrandImage(anyString());
        verify(view,
               never()).setBrandImageTitle(anyString());
        verify(view).hideBrand();
        verify(view).setMenuAccessorText(view.getDefaultMenuText());
    }

    @Test
    public void setupWithOneMegaMenuBrandImplementationTest() {
        doReturn(false).when(megaMenuBrands).isAmbiguous();
        doReturn(false).when(megaMenuBrands).isUnsatisfied();

        final MegaMenuBrandMock megaMenuBrand = new MegaMenuBrandMock("brandImageUrl",
                                                                      "brandImageLabel",
                                                                      "menuAccessorLabel");
        doReturn(megaMenuBrand).when(megaMenuBrands).get();
        doReturn("defaultMenuText").when(view).getDefaultMenuText();

        presenter.setup();

        verify(view).setBrandImage(megaMenuBrand.brandImageUrl());
        verify(view).setBrandImageTitle(megaMenuBrand.brandImageLabel());
        verify(view,
               never()).hideBrand();
        verify(view).setMenuAccessorText(megaMenuBrand.menuAccessorLabel());
    }

    @Test
    public void addMenuItemWithParentTest() {
        final ChildMenuItemPresenter childMenuItemPresenter = mock(ChildMenuItemPresenter.class);
        doReturn(childMenuItemPresenter).when(childMenuItemPresenters).get();
        final HasChildren parent = mock(HasChildren.class);
        presenter.hasChildrenMenuItemByIdentifier.put("parentId",
                                                      parent);

        presenter.addMenuItem("id",
                              "label",
                              "parentId",
                              mock(Command.class),
                              MenuPosition.LEFT);

        verify(childMenuItemPresenter).setup(eq("label"),
                                             any());
        verify(presenter.selectableMenuItemByIdentifier).put("id",
                                                             childMenuItemPresenter);
        verify(presenter.canBeDisabledMenuItemByIdentifier).put("id",
                                                                childMenuItemPresenter);
        verify(view).addMenuItemOnParent(childMenuItemPresenter,
                                         parent);
    }

    @Test
    public void addMenuItemOnTheRightWithoutParentTest() {
        final ChildMenuItemPresenter childMenuItemPresenter = mock(ChildMenuItemPresenter.class);
        doReturn(childMenuItemPresenter).when(childMenuItemPresenters).get();

        presenter.addMenuItem("id",
                              "label",
                              null,
                              mock(Command.class),
                              MenuPosition.RIGHT);

        verify(childMenuItemPresenter).setup(eq("label"),
                                             any());
        verify(presenter.selectableMenuItemByIdentifier).put("id",
                                                             childMenuItemPresenter);
        verify(presenter.canBeDisabledMenuItemByIdentifier).put("id",
                                                                childMenuItemPresenter);
        verify(view).addMenuItemOnRight(childMenuItemPresenter);
    }

    @Test
    public void addMenuItemOnTheLeftWithoutParentTest() {
        final ChildMenuItemPresenter childMenuItemPresenter = mock(ChildMenuItemPresenter.class);
        doReturn(childMenuItemPresenter).when(childMenuItemPresenters).get();

        presenter.addMenuItem("id",
                              "label",
                              null,
                              mock(Command.class),
                              MenuPosition.LEFT);

        verify(childMenuItemPresenter).setup(eq("label"),
                                             any());
        verify(presenter.selectableMenuItemByIdentifier).put("id",
                                                             childMenuItemPresenter);
        verify(presenter.canBeDisabledMenuItemByIdentifier).put("id",
                                                                childMenuItemPresenter);
        verify(view).addMenuItemOnLeft(childMenuItemPresenter);
    }

    @Test
    public void addCustomIsElementMenuItemTest() {
        final IsElement menu = mock(IsElement.class);
        presenter.addCustomMenuItem(menu,
                                    MenuPosition.RIGHT);

        verify(view).addCustomMenuItem(menu);
    }

    @Test
    public void addCustomIsWidgetMenuItemTest() {
        final IsWidget menu = mock(IsWidget.class);
        presenter.addCustomMenuItem(menu,
                                    MenuPosition.RIGHT);

        verify(view).addCustomMenuItem(menu);
    }

    @Test
    public void addGroupMenuItemTest() {
        final GroupMenuItemPresenter groupMenuItemPresenter = mock(GroupMenuItemPresenter.class);
        doReturn(groupMenuItemPresenter).when(groupMenuItemPresenters).get();

        presenter.addGroupMenuItem("id",
                                   "label",
                                   MenuPosition.LEFT);

        verify(groupMenuItemPresenter).setup(eq("label"));
        verify(presenter.hasChildrenMenuItemByIdentifier).put("id",
                                                              groupMenuItemPresenter);
        verify(view).addGroupMenuItem(groupMenuItemPresenter);
    }

    @Test
    public void addContextMenuItemTest() {
        final ChildContextMenuItemPresenter childContextMenuItemPresenter = mock(ChildContextMenuItemPresenter.class);
        doReturn(childContextMenuItemPresenter).when(childContextMenuItemPresenters).get();
        final HasChildren parent = mock(HasChildren.class);
        presenter.hasChildrenMenuItemByIdentifier.put("parentId",
                                                      parent);

        presenter.addContextMenuItem("menuItemId",
                                     "id",
                                     "label",
                                     "parentId",
                                     mock(Command.class),
                                     MenuPosition.LEFT);

        verify(childContextMenuItemPresenter).setup(eq("label"),
                                                    any());
        verify(presenter.selectableMenuItemByIdentifier).put("id",
                                                             childContextMenuItemPresenter);
        verify(presenter.canBeDisabledMenuItemByIdentifier).put("id",
                                                                childContextMenuItemPresenter);
        verify(view).addContextMenuItemOnParent(childContextMenuItemPresenter,
                                                parent);
        verify(view).setContextMenuActive(true);
    }

    @Test
    public void addContextGroupMenuItemTest() {
        final GroupContextMenuItemPresenter groupContextMenuItemPresenter = mock(GroupContextMenuItemPresenter.class);
        doReturn(groupContextMenuItemPresenter).when(groupContextMenuItemPresenters).get();

        presenter.addContextGroupMenuItem("menuItemId",
                                          "id",
                                          "label",
                                          MenuPosition.LEFT);

        verify(groupContextMenuItemPresenter).setup("label");
        verify(presenter.hasChildrenMenuItemByIdentifier).put("id",
                                                              groupContextMenuItemPresenter);
        verify(presenter.canBeDisabledMenuItemByIdentifier).put("id",
                                                                groupContextMenuItemPresenter);
        verify(view).addContextMenuItem(groupContextMenuItemPresenter);
        verify(view).setContextMenuActive(true);
    }

    @Test
    public void clearContextMenuTest() {
        view.clearContextMenu();

        verify(view).clearContextMenu();
    }

    @Test
    public void enableMenuItemTest() {
        final CanBeDisabled menuItem = mock(CanBeDisabled.class);
        presenter.canBeDisabledMenuItemByIdentifier.put("id",
                                                        menuItem);

        presenter.enableMenuItem("id",
                                 true);

        verify(menuItem).enable();
    }

    @Test
    public void disableMenuItemTest() {
        final CanBeDisabled menuItem = mock(CanBeDisabled.class);
        presenter.canBeDisabledMenuItemByIdentifier.put("id",
                                                        menuItem);

        presenter.enableMenuItem("id",
                                 false);

        verify(menuItem).disable();
    }

    @Test
    public void enableContextMenuItemTest() {
        final CanBeDisabled menuItem = mock(CanBeDisabled.class);
        presenter.canBeDisabledMenuItemByIdentifier.put("id",
                                                        menuItem);

        presenter.enableContextMenuItem("id",
                                        true);

        verify(presenter).enableMenuItem("id",
                                         true);
    }

    @Test
    public void setupHomeLinkWithNoDefaultPerspective() {
        doReturn(null).when(workbench).getHomePerspectiveActivity();

        presenter.setupHomeLink();

        ArgumentCaptor<Command> commandCaptor = ArgumentCaptor.forClass(Command.class);
        verify(view).setHomeLinkAction(commandCaptor.capture());
        verify(view).setBrandImageAction(commandCaptor.capture());
        commandCaptor.getValue().execute();

        verify(placeManager,
               never()).goTo(anyString());
    }

    class MegaMenuBrandMock implements MegaMenuBrand {

        private String brandImageUrl;

        private String brandImageLabel;

        private String menuAccessorLabel;

        public MegaMenuBrandMock(String brandImageUrl,
                                 String brandImageLabel,
                                 String menuAccessorLabel) {
            this.brandImageUrl = brandImageUrl;
            this.brandImageLabel = brandImageLabel;
            this.menuAccessorLabel = menuAccessorLabel;
        }

        @Override
        public String brandImageUrl() {
            return brandImageUrl;
        }

        @Override
        public String brandImageLabel() {
            return brandImageLabel;
        }

        @Override
        public String menuAccessorLabel() {
            return menuAccessorLabel;
        }
    }
}
