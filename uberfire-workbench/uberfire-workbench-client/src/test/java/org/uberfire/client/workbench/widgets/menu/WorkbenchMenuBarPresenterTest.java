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

package org.uberfire.client.workbench.widgets.menu;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.workbench.events.PerspectiveChange;
import org.uberfire.client.workbench.events.PlaceMaximizedEvent;
import org.uberfire.client.workbench.events.PlaceMinimizedEvent;
import org.uberfire.client.workbench.widgets.menu.events.PerspectiveVisibiltiyChangeEvent;
import org.uberfire.experimental.service.auth.ExperimentalActivitiesAuthorizationManager;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.security.Resource;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.workbench.model.ActivityResourceType;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.MenuPosition;
import org.uberfire.workbench.model.menu.Menus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WorkbenchMenuBarPresenterTest {

    private static final String NAME = "testName";
    private static final String PERSPECTIVE_ID = "perspectiveId";
    private static final String SECOND_PERSPECTIVE_ID = "secondPerspectiveId";
    private static final String THIRD_PERSPECTIVE_ID = "thirdPerspectiveId";

    @Mock
    protected AuthorizationManager authzManager;
    @Mock
    protected User identity;
    @Mock
    private PerspectiveManager perspectiveManager;
    @Mock
    private ActivityManager activityManager;

    @Mock
    private WorkbenchMenuBarPresenter.View view;

    @Mock
    private ExperimentalActivitiesAuthorizationManager experimentalActivitiesAuthorizationManager;

    @InjectMocks
    private WorkbenchMenuBarPresenter presenter;

    @Test
    public void testAddCurrentPerspective() {
        final Menus menus = MenuFactory.newSimpleItem(NAME).perspective(PERSPECTIVE_ID).endMenu().build();
        final PlaceRequest placeRequest = new DefaultPlaceRequest(PERSPECTIVE_ID);
        final PerspectiveActivity perspectiveActivity = mock(PerspectiveActivity.class);

        when(perspectiveActivity.getPlace()).thenReturn(placeRequest);
        when(perspectiveManager.getCurrentPerspective()).thenReturn(perspectiveActivity);
        when(authzManager.authorize(any(Resource.class),
                                    eq(identity))).thenReturn(true);

        presenter.addMenus(menus);
        verify(view).selectMenuItem(PERSPECTIVE_ID);
    }

    @Test
    public void testAddPerspective() {
        final Menus menus = MenuFactory.newSimpleItem(NAME).perspective(PERSPECTIVE_ID).endMenu().build();
        final PlaceRequest placeRequest = new DefaultPlaceRequest("anyId");
        final PerspectiveActivity perspectiveActivity = mock(PerspectiveActivity.class);

        when(perspectiveActivity.getPlace()).thenReturn(placeRequest);
        when(perspectiveManager.getCurrentPerspective()).thenReturn(perspectiveActivity);
        when(authzManager.authorize(any(Resource.class),
                                    eq(identity))).thenReturn(true);

        presenter.addMenus(menus);

        verify(view,
               never()).selectMenuItem(PERSPECTIVE_ID);
    }

    @Test
    public void testPerspectiveChangeEvent() {
        final Menus menus = MenuFactory.newSimpleItem(NAME).perspective(PERSPECTIVE_ID).endMenu().build();
        final PlaceRequest placeRequest = new DefaultPlaceRequest(PERSPECTIVE_ID);
        final PerspectiveActivity perspectiveActivity = mock(PerspectiveActivity.class);
        final PerspectiveChange perspectiveChange = new PerspectiveChange(placeRequest,
                                                                          null,
                                                                          null,
                                                                          PERSPECTIVE_ID);

        when(perspectiveActivity.getPlace()).thenReturn(placeRequest);
        when(perspectiveActivity.isType(ActivityResourceType.PERSPECTIVE.name())).thenReturn(true);
        when(authzManager.authorize(any(Resource.class),
                                    eq(identity))).thenReturn(true);

        presenter.addMenus(menus);
        presenter.onPerspectiveChange(perspectiveChange);

        verify(view).selectMenuItem(PERSPECTIVE_ID);
    }

    @Test
    public void testAddMenuWithPermission() {
        final Menus menus = MenuFactory.newSimpleItem(NAME).perspective(PERSPECTIVE_ID).endMenu().build();
        when(authzManager.authorize(menus.getItems().get(0),
                                    identity)).thenReturn(true);

        presenter.addMenus(menus);

        verify(authzManager).authorize(menus.getItems().get(0),
                                       identity);
        verify(view).addMenuItem(eq(PERSPECTIVE_ID),
                                 eq(NAME),
                                 isNull(String.class),
                                 any(Command.class),
                                 any(MenuPosition.class));
    }

    @Test
    public void testAddMenuWithoutPermission() {
        final Menus menus = MenuFactory.newSimpleItem(NAME).perspective(PERSPECTIVE_ID).endMenu().build();
        when(authzManager.authorize(menus.getItems().get(0),
                                    identity)).thenReturn(false);

        presenter.addMenus(menus);

        verify(authzManager).authorize(menus.getItems().get(0),
                                       identity);
        verify(view,
               never()).addMenuItem(eq(PERSPECTIVE_ID),
                                    eq(NAME),
                                    isNull(String.class),
                                    any(Command.class),
                                    any(MenuPosition.class));
    }

    @Test
    public void testAddContextMenuWithPermission() {
        final MenuPosition position = MenuPosition.LEFT;
        final Menus contextMenus = MenuFactory.newSimpleItem(NAME).endMenu().build();
        final PerspectiveActivity activity = mock(PerspectiveActivity.class);
        final PlaceRequest placeRequest = mock(PlaceRequest.class);

        when(activity.getIdentifier()).thenReturn(PERSPECTIVE_ID);
        doAnswer(invocationOnMock -> {
            invocationOnMock.getArgumentAt(0, Consumer.class).accept(contextMenus);
            return null;
        }).when(activity).getMenus(any());
        when(activity.isType(ActivityResourceType.PERSPECTIVE.name())).thenReturn(true);
        when(authzManager.authorize(contextMenus.getItems().get(0),
                                    identity)).thenReturn(true);
        when(activityManager.getActivity(placeRequest)).thenReturn(activity);

        presenter.onPerspectiveChange(new PerspectiveChange(placeRequest,
                                                            null,
                                                            contextMenus,
                                                            PERSPECTIVE_ID));

        verify(authzManager).authorize(contextMenus.getItems().get(0),
                                       identity);
        verify(view).clearContextMenu();
        verify(view).addContextMenuItem(eq(PERSPECTIVE_ID),
                                        anyString(),
                                        eq(NAME),
                                        isNull(String.class),
                                        any(Command.class),
                                        eq(position));
    }

    @Test
    public void testAddContextMenuWithoutPermission() {
        final Menus contextMenus = MenuFactory.newSimpleItem(NAME).endMenu().build();
        final PerspectiveActivity activity = mock(PerspectiveActivity.class);
        final PlaceRequest placeRequest = mock(PlaceRequest.class);

        when(activity.getIdentifier()).thenReturn(PERSPECTIVE_ID);
        doAnswer(invocationOnMock -> {
            invocationOnMock.getArgumentAt(0, Consumer.class).accept(contextMenus);
            return null;
        }).when(activity).getMenus(any());
        when(activity.isType(ActivityResourceType.PERSPECTIVE.name())).thenReturn(true);
        when(authzManager.authorize(contextMenus.getItems().get(0),
                                    identity)).thenReturn(false);
        when(activityManager.getActivity(placeRequest)).thenReturn(activity);

        presenter.onPerspectiveChange(new PerspectiveChange(placeRequest,
                                                            null,
                                                            contextMenus,
                                                            PERSPECTIVE_ID));

        verify(authzManager).authorize(contextMenus.getItems().get(0),
                                       identity);
        verify(view).clearContextMenu();
        verify(view,
               never()).addContextMenuItem(anyString(),
                                           anyString(),
                                           anyString(),
                                           anyString(),
                                           any(Command.class),
                                           any(MenuPosition.class));
    }

    @Test
    public void testSetupEnableDisableMenuItemCommand() {
        final Command command = mock(Command.class);
        final Menus menus = MenuFactory.newSimpleItem(NAME).respondsWith(command).endMenu().build();

        when(authzManager.authorize(menus.getItems().get(0),
                                    identity)).thenReturn(true);

        presenter.addMenus(menus);
        verify(view).enableMenuItem(anyString(), eq(true));

        menus.getItems().get(0).setEnabled(true);
        verify(view, times(2)).enableMenuItem(anyString(), eq(true));

        menus.getItems().get(0).setEnabled(false);
        verify(view).enableMenuItem(anyString(), eq(false));
    }

    @Test
    public void testSetupEnableDisableMenuItemPlace() {
        final PlaceRequest place = mock(PlaceRequest.class);
        final Menus menus = MenuFactory.newSimpleItem(NAME).place(place).endMenu().build();

        when(authzManager.authorize(menus.getItems().get(0),
                                    identity)).thenReturn(true);

        presenter.addMenus(menus);
        verify(view).enableMenuItem(anyString(), eq(true));

        menus.getItems().get(0).setEnabled(true);
        verify(view, times(2)).enableMenuItem(anyString(), eq(true));

        menus.getItems().get(0).setEnabled(false);
        verify(view).enableMenuItem(anyString(), eq(false));
    }

    @Test
    public void testSetupEnableDisableMenuItemPerspective() {
        final Menus menus = MenuFactory.newSimpleItem(NAME).perspective(PERSPECTIVE_ID).endMenu().build();

        when(authzManager.authorize(menus.getItems().get(0),
                                    identity)).thenReturn(true);

        presenter.addMenus(menus);
        verify(view).enableMenuItem(anyString(), eq(true));

        menus.getItems().get(0).setEnabled(true);
        verify(view, times(2)).enableMenuItem(anyString(), eq(true));

        menus.getItems().get(0).setEnabled(false);
        verify(view).enableMenuItem(anyString(), eq(false));
    }

    @Test
    public void testSetupEnableDisableContextMenuItem() {
        final Menus contextMenus = MenuFactory.newSimpleItem(NAME).endMenu().build();
        final PerspectiveActivity activity = mock(PerspectiveActivity.class);
        final PlaceRequest placeRequest = mock(PlaceRequest.class);

        when(activity.getIdentifier()).thenReturn(PERSPECTIVE_ID);
        doAnswer(invocationOnMock -> {
            invocationOnMock.getArgumentAt(0, Consumer.class).accept(contextMenus);
            return null;
        }).when(activity).getMenus(any());
        when(activity.isType(ActivityResourceType.PERSPECTIVE.name())).thenReturn(true);
        when(authzManager.authorize(contextMenus.getItems().get(0),
                                    identity)).thenReturn(true);
        when(activityManager.getActivity(placeRequest)).thenReturn(activity);

        presenter.onPerspectiveChange(new PerspectiveChange(placeRequest,
                                                            null,
                                                            contextMenus,
                                                            PERSPECTIVE_ID));
        verify(view).enableContextMenuItem(anyString(), eq(true));

        contextMenus.getItems().get(0).setEnabled(true);
        verify(view, times(2)).enableContextMenuItem(anyString(), eq(true));

        contextMenus.getItems().get(0).setEnabled(false);
        verify(view).enableContextMenuItem(anyString(), eq(false));
    }

    @Test
    public void testMenuInsertionOrder() {
        final Menus firstMenus = MenuFactory.newSimpleItem(NAME).perspective(PERSPECTIVE_ID).endMenu().build();
        final Menus secondMenus = MenuFactory.newSimpleItem(NAME).orderAll(1).perspective(SECOND_PERSPECTIVE_ID).endMenu().build();
        final Menus thirdMenus = MenuFactory.newSimpleItem(NAME).orderAll(2).perspective(THIRD_PERSPECTIVE_ID).endMenu().build();

        when(authzManager.authorize(firstMenus.getItems().get(0),
                                    identity)).thenReturn(true);
        when(authzManager.authorize(secondMenus.getItems().get(0),
                                    identity)).thenReturn(true);
        when(authzManager.authorize(thirdMenus.getItems().get(0),
                                    identity)).thenReturn(true);

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
    public void testAddMenusWithExperimentalPerspective() {

        when(authzManager.authorize(any(MenuItem.class), any())).thenReturn(true);

        List<MenuItem> items = new ArrayList<>();

        items.add(MenuFactory.newSimpleItem(NAME).perspective(PERSPECTIVE_ID).endMenu().build().getItems().get(0));
        items.add(MenuFactory.newSimpleItem(NAME).perspective(SECOND_PERSPECTIVE_ID).endMenu().build().getItems().get(0));
        items.add(MenuFactory.newSimpleItem(NAME).perspective(THIRD_PERSPECTIVE_ID).endMenu().build().getItems().get(0));

        final Menus menus = MenuFactory.newSimpleItem(NAME).withItems(items).endMenu().build();

        when(experimentalActivitiesAuthorizationManager.authorizeActivityId(anyString()))
                .thenAnswer((Answer<Boolean>) invocationOnMock -> !SECOND_PERSPECTIVE_ID.equals(invocationOnMock.getArguments()[0]));

        presenter.addMenus(menus);

        verify(experimentalActivitiesAuthorizationManager, times(3)).authorizeActivityId(anyString());
        verify(view, times(2)).setMenuItemVisible(anyString(), eq(true));
        verify(view).setMenuItemVisible(anyString(), eq(false));
    }

    @Test
    public void testShowHideExperimentalPerspectiveMenus() {
        testAddMenusWithExperimentalPerspective();

        presenter.onPerspectiveVisibilityChange(new PerspectiveVisibiltiyChangeEvent(PERSPECTIVE_ID, false));
        verify(view).setMenuItemVisible(PERSPECTIVE_ID, false);

        presenter.onPerspectiveVisibilityChange(new PerspectiveVisibiltiyChangeEvent(SECOND_PERSPECTIVE_ID, true));
        verify(view).setMenuItemVisible(SECOND_PERSPECTIVE_ID, true);

        presenter.onPerspectiveVisibilityChange(new PerspectiveVisibiltiyChangeEvent(THIRD_PERSPECTIVE_ID, false));
        verify(view).setMenuItemVisible(THIRD_PERSPECTIVE_ID, false);
    }

    @Test
    public void testView() {
        assertEquals(view,
                     presenter.getView());
    }

    @Test
    public void testCollapse() {
        presenter.collapse();

        assertFalse(presenter.isUseExpandedMode());
        verify(view).collapse();
    }

    @Test
    public void testExpand() {
        presenter.expand();

        assertTrue(presenter.isUseExpandedMode());
        verify(view).expand();
    }

    @Test
    public void testAddCollapseHandler() {
        final Command command = mock(Command.class);

        presenter.addCollapseHandler(command);

        verify(view).addCollapseHandler(command);
    }

    @Test
    public void testExpandHandler() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ((Command) invocation.getArguments()[0]).execute();
                return null;
            }
        }).when(view).addExpandHandler(any(Command.class));

        presenter.setup();

        assertTrue(presenter.isExpanded());
    }

    @Test
    public void testCollapseHandler() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ((Command) invocation.getArguments()[0]).execute();
                return null;
            }
        }).when(view).addCollapseHandler(any(Command.class));

        presenter.setup();

        assertFalse(presenter.isExpanded());
    }

    @Test
    public void testAddExpandHandler() {
        final Command command = mock(Command.class);

        presenter.addExpandHandler(command);

        verify(view).addExpandHandler(command);
    }

    @Test
    public void testClear() {
        presenter.clear();

        verify(view).clear();
    }

    @Test
    public void testOnPlaceMaximized() {
        presenter.onPlaceMaximized(mock(PlaceMaximizedEvent.class));

        verify(view).collapse();
    }

    @Test
    public void testOnPlaceMinimized() {
        presenter.onPlaceMinimized(mock(PlaceMinimizedEvent.class));

        verify(view).expand();
    }

    @Test
    public void testOnPlaceMinimizedExpandMode() {
        presenter.collapse();
        presenter.onPlaceMinimized(mock(PlaceMinimizedEvent.class));

        verify(view,
               never()).expand();
    }
}