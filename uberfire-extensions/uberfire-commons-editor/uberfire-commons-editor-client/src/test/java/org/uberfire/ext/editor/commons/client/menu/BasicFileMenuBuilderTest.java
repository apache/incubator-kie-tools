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
import org.jboss.errai.common.client.api.Caller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.UpdatedLockStatusEvent;
import org.uberfire.ext.editor.commons.client.file.CopyPopup;
import org.uberfire.ext.editor.commons.client.file.CopyPopupView;
import org.uberfire.ext.editor.commons.client.file.DeletePopup;
import org.uberfire.ext.editor.commons.client.file.RenamePopup;
import org.uberfire.ext.editor.commons.client.history.SaveButton;
import org.uberfire.ext.editor.commons.client.menu.HasLockSyncMenuStateHelper.LockSyncMenuStateHelper.Operation;
import org.uberfire.ext.editor.commons.client.validation.Validator;
import org.uberfire.ext.editor.commons.service.support.SupportsCopy;
import org.uberfire.ext.editor.commons.service.support.SupportsDelete;
import org.uberfire.ext.editor.commons.service.support.SupportsRename;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.Command;
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
    private DeletePopup deletePopup;

    @Mock
    private SupportsDelete deleteService;
    private CallerMock<SupportsDelete> deleteCaller;

    @Mock
    private RenamePopup renamePopup;

    @Mock
    private SupportsRename renameService;
    private CallerMock<SupportsRename> renameCaller;

    @Mock
    private CopyPopup copyPopup;

    @Mock
    private CopyPopupView copyPopupView;

    @Mock
    private SupportsCopy copyService;
    private CallerMock<SupportsCopy> copyCaller;

    private BasicFileMenuBuilderImpl builder;

    @Before
    public void setup() {
        builder = new BasicFileMenuBuilderImpl() {

            @Override
            DeletePopup getDeletePopup( final Path path,
                                        final Caller<? extends SupportsDelete> deleteCaller ) {
                assertEquals( mockPath,
                              path );
                return deletePopup;
            }

            @Override
            RenamePopup getRenamePopup( final Path path,
                                        final Validator validator,
                                        final Caller<? extends SupportsRename> renameCaller ) {
                assertEquals( mockPath,
                              path );
                return renamePopup;
            }

            @Override
            CopyPopup getCopyPopup( final Path path,
                                    final Validator validator,
                                    final Caller<? extends SupportsCopy> copyCaller,
                                    final CopyPopupView copyPopupView ) {
                assertEquals( mockPath,
                              path );
                return copyPopup;
            }
        };
        deleteCaller = new CallerMock<>( deleteService );
        renameCaller = new CallerMock<>( renameService );
        when( provider.getPath() ).thenReturn( mockPath );
    }

    @Test
    public void testDelete() {
        builder.addDelete( provider,
                           deleteCaller );

        final Menus menus = builder.build();
        final MenuItem mi = menus.getItems().get( 0 );
        final MenuItemCommand mic = (MenuItemCommand) mi;

        mic.getCommand().execute();

        verify( provider,
                times( 1 ) ).getPath();
    }

    @Test
    public void testRename() {
        builder.addRename( provider,
                           validator,
                           renameCaller );

        final Menus menus = builder.build();
        final MenuItem mi = menus.getItems().get( 0 );
        final MenuItemCommand mic = (MenuItemCommand) mi;

        mic.getCommand().execute();

        verify( provider,
                times( 1 ) ).getPath();
    }

    @Test
    public void testCopy() {
        builder.addCopy( provider,
                         validator,
                         copyCaller,
                         copyPopupView );

        final Menus menus = builder.build();
        final MenuItem mi = menus.getItems().get( 0 );
        final MenuItemCommand mic = (MenuItemCommand) mi;

        mic.getCommand().execute();

        verify( provider,
                times( 1 ) ).getPath();
    }

    @Test
    public void menuItemsDisabledWhenLockedByDifferentUser() {
        builder.addSave( new MockSaveButton() );
        builder.addRename( mock( Command.class ) );
        builder.addDelete( mock( Command.class ) );

        final Menus menus = builder.build();

        final UpdatedLockStatusEvent event = new UpdatedLockStatusEvent( mock( Path.class ),
                                                                         true,
                                                                         false );

        builder.onEditorLockInfo( event );

        assertMenuItemEnabled( menus.getItems().get( 0 ),
                               false );
        assertMenuItemEnabled( menus.getItems().get( 1 ),
                               false );
        assertMenuItemEnabled( menus.getItems().get( 2 ),
                               false );
    }

    @Test
    public void menuItemsEnabledWhenNotLocked() {
        builder.addSave( new MockSaveButton() );
        builder.addRename( mock( Command.class ) );
        builder.addDelete( mock( Command.class ) );

        final Menus menus = builder.build();

        final UpdatedLockStatusEvent event = new UpdatedLockStatusEvent( mock( Path.class ),
                                                                         false,
                                                                         false );

        builder.onEditorLockInfo( event );

        assertMenuItemEnabled( menus.getItems().get( 0 ),
                               true );
        assertMenuItemEnabled( menus.getItems().get( 1 ),
                               true );
        assertMenuItemEnabled( menus.getItems().get( 2 ),
                               true );
    }

    @Test
    public void menuItemsEnabledWhenLockedByCurrentUser() {
        builder.addSave( new MockSaveButton() );
        builder.addRename( mock( Command.class ) );
        builder.addDelete( mock( Command.class ) );

        final Menus menus = builder.build();

        final UpdatedLockStatusEvent event = new UpdatedLockStatusEvent( mock( Path.class ),
                                                                         true,
                                                                         true );

        builder.onEditorLockInfo( event );

        assertMenuItemEnabled( menus.getItems().get( 0 ),
                               true );
        assertMenuItemEnabled( menus.getItems().get( 1 ),
                               true );
        assertMenuItemEnabled( menus.getItems().get( 2 ),
                               true );
    }

    @Test
    public void menuItemsDisabledWhenNotLockedWithCustomStateHelper() {
        builder.addSave( new MockSaveButton() );
        builder.addRename( mock( Command.class ) );
        builder.addDelete( mock( Command.class ) );
        builder.setLockSyncMenuStateHelper( ( final Path file,
                                              final boolean isLocked,
                                              final boolean isLockedByCurrentUser ) -> Operation.DISABLE );

        final Menus menus = builder.build();

        //Not locked, MenuItems should normally be enabled however our custom helper forces disable
        final UpdatedLockStatusEvent event = new UpdatedLockStatusEvent( mock( Path.class ),
                                                                         false,
                                                                         false );

        builder.onEditorLockInfo( event );

        assertMenuItemEnabled( menus.getItems().get( 0 ),
                               false );
        assertMenuItemEnabled( menus.getItems().get( 1 ),
                               false );
        assertMenuItemEnabled( menus.getItems().get( 2 ),
                               false );
    }

    @Test
    public void menuItemsStateChangeVetoedWhenLockedWithCustomStateHelper() {
        builder.addSave( new MockSaveButton() );
        builder.addRename( mock( Command.class ) );
        builder.addDelete( mock( Command.class ) );
        builder.setLockSyncMenuStateHelper( ( final Path file,
                                              final boolean isLocked,
                                              final boolean isLockedByCurrentUser ) -> Operation.VETO );

        final Menus menus = builder.build();
        menus.getItems().get( 0 ).setEnabled( true );
        menus.getItems().get( 1 ).setEnabled( true );
        menus.getItems().get( 2 ).setEnabled( true );

        //Locked, MenuItems should normally be disabled however our custom helper vetos changes
        final UpdatedLockStatusEvent event = new UpdatedLockStatusEvent( mock( Path.class ),
                                                                         true,
                                                                         false );

        builder.onEditorLockInfo( event );

        assertMenuItemEnabled( menus.getItems().get( 0 ),
                               true );
        assertMenuItemEnabled( menus.getItems().get( 1 ),
                               true );
        assertMenuItemEnabled( menus.getItems().get( 2 ),
                               true );
    }

    private void assertMenuItemEnabled( final MenuItem menuItem,
                                        final boolean enabled ) {
        assertEquals( enabled,
                      menuItem.isEnabled() );
    }

    //The real SaveButton keeps state in the GWT Widget.. override to keep in the Presenter
    private class MockSaveButton extends SaveButton {

        private boolean enabled;

        @Override
        public boolean isEnabled() {
            return this.enabled;
        }

        @Override
        public void setEnabled( final boolean enabled ) {
            this.enabled = enabled;
        }
    }

}
