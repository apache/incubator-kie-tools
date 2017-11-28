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
package org.dashbuilder.client.navigation.widget.editor;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.dashbuilder.client.navigation.event.NavItemEditCancelledEvent;
import org.dashbuilder.client.navigation.event.NavItemEditStartedEvent;
import org.dashbuilder.client.navigation.plugin.PerspectivePluginManager;
import org.dashbuilder.navigation.NavDivider;
import org.dashbuilder.navigation.NavFactory;
import org.dashbuilder.navigation.NavGroup;
import org.dashbuilder.navigation.NavItem;
import org.dashbuilder.navigation.workbench.NavWorkbenchCtx;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.client.authz.PerspectiveTreeProvider;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(GwtMockitoTestRunner.class)
public class NavItemEditorTest {

    @Mock
    NavItemDefaultEditorView view;

    @Mock
    SyncBeanManager beanManager;

    @Mock
    PerspectiveTreeProvider perspectiveTreeProvider;

    @Mock
    EventSourceMock<NavItemEditStartedEvent> navItemEditStartedEvent;

    @Mock
    EventSourceMock<NavItemEditCancelledEvent> navItemEditCancelledEvent;

    @Mock
    PlaceManager placeManager;

    @Mock
    TargetPerspectiveEditor targetPerspectiveEditor;

    @Mock
    PerspectivePluginManager perspectivePluginManager;

    @Mock
    Command updateCommand;

    NavItemEditor presenter;
    static final String NEW_PERSPECTIVE_NAME = "- New Perspective -";

    @Before
    public void setUp() throws Exception {
        presenter = new NavItemDefaultEditor(view, beanManager,
                placeManager, perspectiveTreeProvider, targetPerspectiveEditor,
                perspectivePluginManager, navItemEditStartedEvent, navItemEditCancelledEvent);

        when(view.i18nNewItemName("Perspective")).thenReturn(NEW_PERSPECTIVE_NAME);
        presenter.getSettings().setLiteralPerspective("Perspective");

        doAnswer(invocationOnMock -> invocationOnMock.getArguments()[0])
                .when(targetPerspectiveEditor).getPerspectiveName(anyString());
    }

    @Test
    public void testChangeGroup() {
        NavGroup navGroup = NavFactory.get().createNavGroup();
        navGroup.setId("id");
        navGroup.setName("name");
        navGroup.setDescription("description");
        navGroup.setModifiable(true);
        presenter.setOnUpdateCommand(updateCommand);
        presenter.edit(navGroup);

        when(view.getItemName()).thenReturn("name");
        presenter.onChangesOk();
        verify(view).finishItemEdition();
        verify(updateCommand).execute();
        assertEquals(presenter.getNavItem().getName(), "name");

        reset(updateCommand);
        reset(view);
        when(view.getItemName()).thenReturn("  \t   ");
        presenter.onChangesOk();
        verify(view, never()).finishItemEdition();
        verify(updateCommand, never()).execute();
        assertEquals(presenter.getNavItem().getName(), "name");

        reset(updateCommand);
        reset(view);
        when(view.getItemName()).thenReturn("newName");
        presenter.onChangesOk();
        verify(view).finishItemEdition();
        verify(updateCommand).execute();
        assertEquals(presenter.getNavItem().getName(), "newName");
    }

    @Test
    public void testChangePerspective() {
        NavItem navItem = NavFactory.get().createNavItem();
        NavWorkbenchCtx navCtxA = NavWorkbenchCtx.perspective("A");
        NavWorkbenchCtx navCtxB = NavWorkbenchCtx.perspective("B");
        navItem.setContext(navCtxA.toString());

        navItem.setId("id");
        navItem.setName("name");
        navItem.setDescription("description");
        navItem.setModifiable(true);
        presenter.setOnUpdateCommand(updateCommand);
        presenter.edit(navItem);

        // Empty perspective
        when(view.getItemName()).thenReturn("name");
        presenter.onChangesOk();
        verify(view, never()).finishItemEdition();

        // Perspective changes
        reset(updateCommand);
        reset(view);
        when(view.getItemName()).thenReturn("name");
        when(targetPerspectiveEditor.getPerspectiveId()).thenReturn("B");
        presenter.onChangesOk();
        verify(view).finishItemEdition();
        verify(updateCommand).execute();
        assertEquals(presenter.getNavItem().getName(), "name");
        assertEquals(presenter.getNavItem().getContext(), navCtxB.toString());

        // Cancel changes
        navItem.setContext(navCtxA.toString());
        presenter.edit(navItem);
        reset(updateCommand);
        reset(targetPerspectiveEditor);
        reset(view);
        when(view.getItemName()).thenReturn("newName");
        when(targetPerspectiveEditor.getPerspectiveId()).thenReturn("B");
        presenter.cancelEdition();
        verify(view).finishItemEdition();
        verify(view).setItemName("name");
        verify(targetPerspectiveEditor).setPerspectiveId("A");
        assertEquals(presenter.getNavItem().getName(), "name");
        assertEquals(presenter.getNavItem().getContext(), navCtxA.toString());
    }

    @Test
    public void testEditGroup() {
        NavGroup navGroup = NavFactory.get().createNavGroup();
        navGroup.setId("id");
        navGroup.setName("name");
        navGroup.setDescription("description");
        navGroup.setModifiable(false);
        presenter.edit(navGroup);

        verify(view, atLeastOnce()).clearChildren();
        verify(view, atLeastOnce()).clearCommands();
        verify(view).setItemName("name");
        verify(view).setItemDescription("description");
        verify(view).setItemEditable(false);
        verify(view).setItemType(NavItemEditor.ItemType.GROUP);
        verify(view, atLeastOnce()).setCommandsEnabled(true);
        verify(view, atLeastOnce()).addCommand(any(), any());
        verify(view, never()).setContextWidget(any());
    }

    @Test
    public void testEditDivider() {
        NavDivider divider = NavFactory.get().createDivider();
        divider.setId("id");
        divider.setName("name");
        divider.setDescription("description");
        divider.setModifiable(true);
        presenter.edit(divider);

        verify(view, atLeastOnce()).clearChildren();
        verify(view, atLeastOnce()).clearCommands();
        verify(view).setItemName("name");
        verify(view).setItemDescription("description");
        verify(view).setItemEditable(false);
        verify(view).setItemType(NavItemEditor.ItemType.DIVIDER);
        verify(view, atLeastOnce()).setCommandsEnabled(true);
        verify(view, atLeastOnce()).addCommand(any(), any());
        verify(view, never()).setContextWidget(any());

        reset(view);
        presenter.startEdition();
        verify(view, never()).startItemEdition();
    }

    @Test
    public void testEditPerspective() {
        NavItem navItem = NavFactory.get().createNavItem();
        navItem.setModifiable(false);
        navItem.setContext(NavWorkbenchCtx.perspective("p1").toString());
        presenter.edit(navItem);

        verify(view, atLeastOnce()).clearChildren();
        verify(view, atLeastOnce()).clearCommands();
        verify(view).setItemEditable(false);
        verify(view).setItemType(NavItemEditor.ItemType.PERSPECTIVE);
        verify(view, atLeastOnce()).setCommandsEnabled(true);
        verify(view, atLeastOnce()).addCommand(any(), any());
        verify(view).setContextWidget(targetPerspectiveEditor);
    }

    @Test
    public void testCommandsAvailability() {
        // Disable move actions
        presenter.setMoveUpEnabled(false);
        presenter.setMoveDownEnabled(false);

        // Non-modifiable group (only creation actions available)
        NavGroup navGroup = NavFactory.get().createNavGroup();
        navGroup.setModifiable(false);
        presenter.edit(navGroup);
        verify(view, atLeastOnce()).setCommandsEnabled(true);

        // Modifiable group (creation actions)
        reset(view);
        navGroup.setModifiable(true);
        presenter.edit(navGroup);
        verify(view, atLeastOnce()).setCommandsEnabled(true);

        // Non-modifiable perspective (move actions disabled => no actions)
        reset(view);
        NavItem navItem = NavFactory.get().createNavItem();
        navItem.setContext(NavWorkbenchCtx.perspective("p1").toString());
        navItem.setModifiable(false);
        presenter.edit(navItem);
        verify(view, never()).setCommandsEnabled(true);

        // Modifiable perspective (only delete action)
        reset(view);
        navItem.setModifiable(true);
        presenter.edit(navItem);
        verify(view).setItemEditable(true);
        verify(view).setItemDeletable(true);
        verify(view, never()).setCommandsEnabled(true);

        // Modifiable divider (only delete action)
        reset(view);
        NavDivider navDivider = NavFactory.get().createDivider();
        navDivider.setModifiable(true);
        presenter.edit(navDivider);
        verify(view).setItemEditable(false);
        verify(view).setItemDeletable(true);
        verify(view, never()).setCommandsEnabled(true);

        // Non-modifiable divider (no actions)
        reset(view);
        navDivider.setModifiable(false);
        presenter.edit(navDivider);
        verify(view, never()).setCommandsEnabled(true);

        // Move actions available
        reset(view);
        presenter.setMoveUpEnabled(true);
        presenter.setMoveDownEnabled(true);
        presenter.edit(navItem);
        verify(view, atLeastOnce()).setCommandsEnabled(true);
        reset(view);
        presenter.edit(navDivider);
        verify(view, atLeastOnce()).setCommandsEnabled(true);
        reset(view);
        presenter.edit(navGroup);
        verify(view, atLeastOnce()).setCommandsEnabled(true);
    }

    @Test
    public void testItemNameFromPerspective() {
        NavItem navItem = NavFactory.get().createNavItem();
        navItem.setId("id");
        navItem.setName("name");
        navItem.setModifiable(true);
        navItem.setContext(NavWorkbenchCtx.perspective("A").toString());

        // Existing item => The name does not changes on perspective change
        presenter.edit(navItem);
        presenter.startEdition();
        verify(view).setItemName("name");
        when(targetPerspectiveEditor.getPerspectiveId()).thenReturn("B");
        presenter.onTargetPerspectiveUpdated();
        verify(view, never()).setItemName("B");

        // Newly created item => The name always matches the selected perspective
        reset(view);
        navItem.setName(NEW_PERSPECTIVE_NAME);
        presenter.edit(navItem);
        presenter.startEdition();
        verify(view).setItemName("A");
        when(targetPerspectiveEditor.getPerspectiveId()).thenReturn("B");
        presenter.onTargetPerspectiveUpdated();
        verify(view).setItemName("B");

        // If user changes the name then the auto-matching is disabled
        reset(view);
        presenter.onItemNameChanged();
        presenter.onTargetPerspectiveUpdated();
        verify(view, never()).setItemName("B");
    }

    @Test
    public void testCancelRestoresLastBackup() {
        NavItem navItem = NavFactory.get().createNavItem();
        navItem.setId("id");
        navItem.setName("name");
        navItem.setModifiable(true);
        navItem.setContext(NavWorkbenchCtx.perspective("A").toString());

        // Modify the item
        when(targetPerspectiveEditor.getPerspectiveId()).thenReturn("A");
        presenter.edit(navItem);
        presenter.startEdition();
        when(view.getItemName()).thenReturn("newName");
        presenter.onChangesOk();
        assertEquals(presenter.getNavItem().getName(), "newName");

        // Edit again and cancel edition. The name must match the latest name set.
        presenter.startEdition();
        presenter.cancelEdition();
        assertEquals(presenter.getNavItem().getName(), "newName");

        // Editing a new item resets the latest backup
        presenter.edit(navItem);
        presenter.startEdition();
        presenter.cancelEdition();
        assertEquals(presenter.getNavItem().getName(), "name");
    }

    @Test
    public void testBackupIsNotUpdatedOnError() {
        NavItem navItem = NavFactory.get().createNavItem();
        navItem.setId("id");
        navItem.setName("name");
        navItem.setModifiable(true);
        navItem.setContext(NavWorkbenchCtx.perspective("A").toString());

        // Edit with errors (missing perspective selected)
        presenter.edit(navItem);
        presenter.startEdition();
        when(view.getItemName()).thenReturn("newName");
        presenter.onChangesOk();
        assertEquals(presenter.getNavItem().getName(), "newName");

        // Cancel edition restores the initial backup
        presenter.cancelEdition();
        assertEquals(presenter.getNavItem().getName(), "name");
    }
}