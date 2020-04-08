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

package org.uberfire.ext.editor.commons.client.menu;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.UpdatedLockStatusEvent;
import org.uberfire.ext.editor.commons.client.file.popups.CopyPopUpPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.DeletePopUpPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.RenamePopUpPresenter;
import org.uberfire.ext.editor.commons.client.history.SaveButton;
import org.uberfire.ext.editor.commons.client.menu.HasLockSyncMenuStateHelper.LockSyncMenuStateHelper.Operation;
import org.uberfire.ext.editor.commons.version.CurrentBranch;
import org.uberfire.ext.editor.commons.client.menu.common.DefaultCurrentBranch;
import org.uberfire.ext.editor.commons.client.validation.Validator;
import org.uberfire.ext.editor.commons.service.support.SupportsCopy;
import org.uberfire.ext.editor.commons.service.support.SupportsDelete;
import org.uberfire.ext.editor.commons.service.support.SupportsRename;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.MenuItemCommand;
import org.uberfire.workbench.model.menu.Menus;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class BasicFileMenuBuilderTest {

    @Mock
    private Path mockPath;

    @Mock
    private BasicFileMenuBuilder.PathProvider provider;

    @Mock
    private Validator validator;

    @Mock
    private RestoreVersionCommandProvider restoreVersionCommandProvider;

    @Mock
    private EventSourceMock<NotificationEvent> notification;

    @Mock
    private BusyIndicatorView busyIndicatorView;

    @Mock
    private DeletePopUpPresenter deletePopUpPresenter;

    @Mock
    private CopyPopUpPresenter copyPopUpPresenter;

    @Mock
    private RenamePopUpPresenter renamePopUpPresenter;

    @Mock
    private SupportsDelete deleteService;
    private CallerMock<SupportsDelete> deleteCaller;

    @Mock
    private SupportsRename renameService;

    private CallerMock<SupportsRename> renameCaller;

    @Mock
    private SupportsCopy copyService;
    private CallerMock<SupportsCopy> copyCaller;

    private BasicFileMenuBuilderImpl builder;

    @Before
    public void setup() {
        builder = new BasicFileMenuBuilderImpl(deletePopUpPresenter,
                                               copyPopUpPresenter,
                                               renamePopUpPresenter,
                                               busyIndicatorView,
                                               notification,
                                               restoreVersionCommandProvider);
        deleteCaller = new CallerMock<>(deleteService);
        renameCaller = new CallerMock<>(renameService);
        when(provider.getPath()).thenReturn(mockPath);
    }

    @Test
    public void testDelete() {
        builder.addDelete(mockPath,
                          deleteCaller);

        final Menus menus = builder.build();
        final MenuItem mi = menus.getItems().get(0);
        final MenuItemCommand mic = (MenuItemCommand) mi;

        mic.getCommand().execute();

        verify(deletePopUpPresenter).show(eq(null),
                                          any());
    }

    @Test
    public void testDeleteWithValidator() {
        builder.addDelete(mockPath,
                          deleteCaller,
                          validator);

        final Menus menus = builder.build();
        final MenuItem mi = menus.getItems().get(0);
        final MenuItemCommand mic = (MenuItemCommand) mi;

        mic.getCommand().execute();

        verify(deletePopUpPresenter).show(eq(validator),
                                          any());
    }

    @Test
    public void testDeleteWithProvider() {
        builder.addDelete(provider,
                          deleteCaller);

        final Menus menus = builder.build();
        final MenuItem mi = menus.getItems().get(0);
        final MenuItemCommand mic = (MenuItemCommand) mi;

        verify(provider,
               never()).getPath();

        mic.getCommand().execute();

        verify(provider,
               times(1)).getPath();
        verify(deletePopUpPresenter).show(eq(null),
                                          any());
    }

    @Test
    public void testDeleteWithProviderAndValidator() {
        builder.addDelete(provider,
                          deleteCaller,
                          validator);

        final Menus menus = builder.build();
        final MenuItem mi = menus.getItems().get(0);
        final MenuItemCommand mic = (MenuItemCommand) mi;

        verify(provider,
               never()).getPath();

        mic.getCommand().execute();

        verify(provider,
               times(1)).getPath();
        verify(deletePopUpPresenter).show(eq(validator),
                                          any());
    }

    @Test
    public void testRename() {
        builder.addRename(provider,
                          validator,
                          renameCaller);

        final Menus menus = builder.build();
        final MenuItem mi = menus.getItems().get(0);
        final MenuItemCommand mic = (MenuItemCommand) mi;

        verify(provider,
               never()).getPath();

        mic.getCommand().execute();

        verify(provider,
               times(1)).getPath();
    }

    @Test
    public void testCopy() {
        builder.addCopy(provider,
                        validator,
                        copyCaller);

        final Menus menus = builder.build();
        final MenuItem mi = menus.getItems().get(0);
        final MenuItemCommand mic = (MenuItemCommand) mi;

        verify(provider,
               never()).getPath();

        mic.getCommand().execute();

        verify(provider,
               times(1)).getPath();
    }

    @Test
    public void testAddRestoreVersion() {
        CurrentBranch currentBranch = new DefaultCurrentBranch();
        ArgumentCaptor<CurrentBranch> currentBranchCaptor = ArgumentCaptor.forClass(CurrentBranch.class);
        builder.addRestoreVersion(mock(Path.class),
                                  currentBranch);
        verify(restoreVersionCommandProvider).getCommand(any(Path.class),
                                                         currentBranchCaptor.capture());
        assertEquals(currentBranch,
                     currentBranchCaptor.getValue());
    };

    @Test
    public void menuItemsDisabledWhenLockedByDifferentUser() {
        builder.addSave(new MockSaveButton());
        builder.addRename(mock(Command.class));
        builder.addDelete(mock(Command.class));

        final Menus menus = builder.build();

        final UpdatedLockStatusEvent event = new UpdatedLockStatusEvent(mock(Path.class),
                                                                        true,
                                                                        false);

        builder.onEditorLockInfo(event);

        assertMenuItemEnabled(menus.getItems().get(0),
                              false);
        assertMenuItemEnabled(menus.getItems().get(1),
                              false);
        assertMenuItemEnabled(menus.getItems().get(2),
                              false);
    }

    @Test
    public void menuItemsEnabledWhenNotLocked() {
        builder.addSave(new MockSaveButton());
        builder.addRename(mock(Command.class));
        builder.addDelete(mock(Command.class));

        final Menus menus = builder.build();

        final UpdatedLockStatusEvent event = new UpdatedLockStatusEvent(mock(Path.class),
                                                                        false,
                                                                        false);

        builder.onEditorLockInfo(event);

        assertMenuItemEnabled(menus.getItems().get(0),
                              true);
        assertMenuItemEnabled(menus.getItems().get(1),
                              true);
        assertMenuItemEnabled(menus.getItems().get(2),
                              true);
    }

    @Test
    public void menuItemsEnabledWhenLockedByCurrentUser() {
        builder.addSave(new MockSaveButton());
        builder.addRename(mock(Command.class));
        builder.addDelete(mock(Command.class));

        final Menus menus = builder.build();

        final UpdatedLockStatusEvent event = new UpdatedLockStatusEvent(mock(Path.class),
                                                                        true,
                                                                        true);

        builder.onEditorLockInfo(event);

        assertMenuItemEnabled(menus.getItems().get(0),
                              true);
        assertMenuItemEnabled(menus.getItems().get(1),
                              true);
        assertMenuItemEnabled(menus.getItems().get(2),
                              true);
    }

    @Test
    public void menuItemsDisabledWhenNotLockedWithCustomStateHelper() {
        builder.addSave(new MockSaveButton());
        builder.addRename(mock(Command.class));
        builder.addDelete(mock(Command.class));
        builder.setLockSyncMenuStateHelper((final Path file,
                                            final boolean isLocked,
                                            final boolean isLockedByCurrentUser) -> Operation.DISABLE);

        final Menus menus = builder.build();

        //Not locked, MenuItems should normally be enabled however our custom helper forces disable
        final UpdatedLockStatusEvent event = new UpdatedLockStatusEvent(mock(Path.class),
                                                                        false,
                                                                        false);

        builder.onEditorLockInfo(event);

        assertMenuItemEnabled(menus.getItems().get(0),
                              false);
        assertMenuItemEnabled(menus.getItems().get(1),
                              false);
        assertMenuItemEnabled(menus.getItems().get(2),
                              false);
    }

    @Test
    public void menuItemsStateChangeVetoedWhenLockedWithCustomStateHelper() {
        builder.addSave(new MockSaveButton());
        builder.addRename(mock(Command.class));
        builder.addDelete(mock(Command.class));
        builder.setLockSyncMenuStateHelper((final Path file,
                                            final boolean isLocked,
                                            final boolean isLockedByCurrentUser) -> Operation.VETO);

        final Menus menus = builder.build();
        menus.getItems().get(0).setEnabled(true);
        menus.getItems().get(1).setEnabled(true);
        menus.getItems().get(2).setEnabled(true);

        //Locked, MenuItems should normally be disabled however our custom helper vetos changes
        final UpdatedLockStatusEvent event = new UpdatedLockStatusEvent(mock(Path.class),
                                                                        true,
                                                                        false);

        builder.onEditorLockInfo(event);

        assertMenuItemEnabled(menus.getItems().get(0),
                              true);
        assertMenuItemEnabled(menus.getItems().get(1),
                              true);
        assertMenuItemEnabled(menus.getItems().get(2),
                              true);
    }

    private void assertMenuItemEnabled(final MenuItem menuItem,
                                       final boolean enabled) {
        assertEquals(enabled,
                     menuItem.isEnabled());
    }

    //The real SaveButton keeps state in the GWT Widget.. override to keep in the Presenter
    private class MockSaveButton extends SaveButton {

        private boolean enabled;

        @Override
        public boolean isEnabled() {
            return this.enabled;
        }

        @Override
        public void setEnabled(final boolean enabled) {
            this.enabled = enabled;
        }
    }
}
