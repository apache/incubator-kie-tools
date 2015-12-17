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
import org.guvnor.common.services.project.context.ProjectContextChangeEvent;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.MenuItemCommand;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class NewResourcesMenuTest {

    private NewResourcesMenu menu;

    private SyncBeanManager iocBeanManager;
    private NewResourcePresenter newResourcePresenter;
    private IOCBeanDef<NewResourceHandler> handlerBeanDef;
    private NewResourceHandler handler;
    private Command command;

    @Before
    public void setup() {
        iocBeanManager = mock( SyncBeanManager.class );
        newResourcePresenter = mock( NewResourcePresenter.class );
        command = mock( Command.class );
        handlerBeanDef = mock( IOCBeanDef.class );
        handler = mock( NewResourceHandler.class );

        when( iocBeanManager.lookupBeans( NewResourceHandler.class ) ).thenReturn( new ArrayList<IOCBeanDef<NewResourceHandler>>() {{
            add( handlerBeanDef );
        }} );
        when( handlerBeanDef.getInstance() ).thenReturn( handler );
        when( handler.getDescription() ).thenReturn( "handler" );
        when( handler.getCommand( newResourcePresenter ) ).thenReturn( command );

        menu = new NewResourcesMenu( iocBeanManager,
                                     newResourcePresenter );

        menu.setup();
    }

    @Test
    public void testGetMenuItems() {
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
        final ProjectContextChangeEvent event = mock( ProjectContextChangeEvent.class );

        doAnswer( new Answer() {
            public Object answer( final InvocationOnMock invocation ) {
                final Object[] args = invocation.getArguments();
                final Callback callback = (Callback) args[ 1 ];
                callback.onSuccess( true );
                return null;
            }
        } ).when( handler ).acceptContext( any( ProjectContext.class ),
                                           any( Callback.class ) );

        menu.onProjectContextChanged( event );

        final List<MenuItem> menus = menu.getMenuItems();
        final MenuItem mi = menus.get( 0 );
        assertTrue( mi.isEnabled() );
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOnProjectContextChangedDisabled() {
        final ProjectContextChangeEvent event = mock( ProjectContextChangeEvent.class );

        doAnswer( new Answer() {
            public Object answer( final InvocationOnMock invocation ) {
                final Object[] args = invocation.getArguments();
                final Callback callback = (Callback) args[ 1 ];
                callback.onSuccess( false );
                return null;
            }
        } ).when( handler ).acceptContext( any( ProjectContext.class ),
                                           any( Callback.class ) );

        menu.onProjectContextChanged( event );

        final List<MenuItem> menus = menu.getMenuItems();
        final MenuItem mi = menus.get( 0 );
        assertFalse( mi.isEnabled() );
    }

}
