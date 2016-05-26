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

package org.kie.workbench.common.widgets.client.handlers;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.Callback;
import org.guvnor.common.services.project.context.ProjectContext;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.MenuItemCommand;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class NewResourcesMenuTest {

    private NewResourcesMenu menu;

    @Mock
    private SyncBeanManager iocBeanManager;

    @Mock
    private NewResourcePresenter newResourcePresenter;

    @Mock
    private SyncBeanDef<NewResourceHandler> handlerBeanDef;

    @Mock
    private NewResourceHandler handler;

    @Mock
    private Command command;

    @Mock
    private ProjectContext projectContext;

    @Before
    public void setup() {
        when( iocBeanManager.lookupBeans( NewResourceHandler.class ) ).thenReturn( new ArrayList<SyncBeanDef<NewResourceHandler>>() {{
            add( handlerBeanDef );
        }} );
        when( handlerBeanDef.getInstance() ).thenReturn( handler );
        when( handler.canCreate() ).thenReturn( true );
        when( handler.getDescription() ).thenReturn( "handler" );
        when( handler.getCommand( newResourcePresenter ) ).thenReturn( command );

        menu = new NewResourcesMenu( iocBeanManager,
                                     newResourcePresenter,
                                     projectContext );

        menu.setup();
    }

    @Test
    public void testListenerIsBound() throws Exception {
        verify( projectContext ).addChangeHandler( menu );
    }

    @Test
    public void testGetMenuItems() {
        final List<MenuItem> menus = menu.getMenuItems();
        assertNotNull( menus );
        assertEquals( 1,
                      menus.size() );
    }

    @Test
    public void testGetMenuItemsCanNotCreate() {
        when( handler.canCreate() ).thenReturn( false );

        final List<MenuItem> menus = menu.getMenuItems();
        assertNotNull( menus );
        assertEquals( 1,
                      menus.size() );
    }

    @Test
    public void testMenuItemCommand() {
        final List<MenuItem> menus = menu.getMenuItems();
        assertNotNull( menus );
        assertEquals( 1,
                      menus.size() );

        final MenuItem mi = menus.get( 0 );
        assertTrue( mi.isEnabled() );

        assertTrue( mi instanceof MenuItemCommand );
        final MenuItemCommand miu = (MenuItemCommand) mi;

        assertNotNull( miu.getCommand() );
        miu.getCommand().execute();

        verify( command,
                times( 1 ) ).execute();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOnProjectContextChangedEnabled() {

        doAnswer( new Answer() {
            public Object answer( final InvocationOnMock invocation ) {
                final Object[] args = invocation.getArguments();
                final Callback callback = (Callback) args[ 0 ];
                callback.onSuccess( true );
                return null;
            }
        } ).when( handler ).acceptContext( any( Callback.class ) );

        menu.onChange();

        final List<MenuItem> menus = menu.getMenuItems();
        final MenuItem mi = menus.get( 0 );
        assertTrue( mi.isEnabled() );
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOnProjectContextChangedDisabled() {

        doAnswer( new Answer() {
            public Object answer( final InvocationOnMock invocation ) {
                final Object[] args = invocation.getArguments();
                final Callback callback = (Callback) args[ 0 ];
                callback.onSuccess( false );
                return null;
            }
        } ).when( handler ).acceptContext( any( Callback.class ) );

        menu.onChange();

        final List<MenuItem> menus = menu.getMenuItems();
        final MenuItem mi = menus.get( 0 );
        assertFalse( mi.isEnabled() );
    }

}
