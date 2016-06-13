/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.widgets.metadata.client.menu;

import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.metadata.client.KieDocument;
import org.kie.workbench.common.widgets.metadata.client.menu.RegisteredDocumentsMenuView.DocumentMenuItem;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.workbench.model.menu.MenuItem;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RegisteredDocumentsMenuBuilderTest {

    private RegisteredDocumentsMenuBuilder builder;

    @Mock
    private RegisteredDocumentsMenuView view;

    @Mock
    private SyncBeanDef<DocumentMenuItem> documentMenuItemBeanDef;

    @Mock
    private DocumentMenuItem documentMenuItem;

    @Mock
    private SyncBeanManager beanManager;

    @Captor
    private ArgumentCaptor<Command> removeDocumentCommandCaptor;

    @Captor
    private ArgumentCaptor<Command> activateDocumentCommandCaptor;

    @Before
    public void setup() {
        final RegisteredDocumentsMenuBuilder wrapped = new RegisteredDocumentsMenuBuilder( view,
                                                                                           beanManager );
        wrapped.setup();

        this.builder = spy( wrapped );

        when( beanManager.lookupBean( eq( DocumentMenuItem.class ) ) ).thenReturn( documentMenuItemBeanDef );
        when( documentMenuItemBeanDef.newInstance() ).thenReturn( documentMenuItem );
    }

    @Test
    public void testEnable() {
        final MenuItem mi = builder.build();
        mi.setEnabled( true );

        verify( view,
                times( 1 ) ).setEnabled( eq( true ) );
    }

    @Test
    public void testDisable() {
        final MenuItem mi = builder.build();
        mi.setEnabled( false );

        verify( view,
                times( 1 ) ).setEnabled( eq( false ) );
    }

    @Test
    public void testOnOpenDocument_WithCommand() {
        final Command command = mock( Command.class );
        builder.setOpenDocumentCommand( command );
        builder.onOpenDocument();

        verify( command,
                times( 1 ) ).execute();
    }

    @Test
    public void testOnOpenDocument_WithoutCommand() {
        try {
            builder.onOpenDocument();
        } catch ( NullPointerException npe ) {
            fail( "Null Commands should be handled." );
        }
    }

    @Test
    public void testOnSaveDocuments_WithCommand() {
        final Command command = mock( Command.class );
        builder.setSaveDocumentsCommand( command );
        builder.onSaveDocuments();

        verify( command,
                times( 1 ) ).execute();
    }

    @Test
    public void testOnSaveDocuments_WithoutCommand() {
        try {
            builder.onSaveDocuments();
        } catch ( NullPointerException npe ) {
            fail( "Null Commands should be handled." );
        }
    }

    @Test
    public void testRegisterDocument() {
        final KieDocument document = makeKieDocument();
        builder.registerDocument( document );

        verify( documentMenuItem,
                times( 1 ) ).setName( "filename" );
        verify( documentMenuItem,
                times( 1 ) ).setRemoveDocumentCommand( removeDocumentCommandCaptor.capture() );
        verify( documentMenuItem,
                times( 1 ) ).setActivateDocumentCommand( activateDocumentCommandCaptor.capture() );
        verify( view,
                times( 1 ) ).addDocument( eq( documentMenuItem ) );

        final Command removeDocumentCommand = removeDocumentCommandCaptor.getValue();
        assertNotNull( removeDocumentCommand );
        removeDocumentCommand.execute();

        verify( builder,
                times( 1 ) ).onRemoveDocument( document );

        final Command activateDocumentCommand = activateDocumentCommandCaptor.getValue();
        assertNotNull( activateDocumentCommand );
        activateDocumentCommand.execute();

        verify( builder,
                times( 1 ) ).onActivateDocument( document );
    }

    @Test
    public void testDereregisterDocument() {
        final KieDocument document = makeKieDocument();
        builder.registerDocument( document );

        builder.deregisterDocument( document );

        verify( view,
                times( 1 ) ).deleteDocument( documentMenuItem );
        verify( beanManager,
                times( 1 ) ).destroyBean( documentMenuItem );
    }

    private KieDocument makeKieDocument() {
        final KieDocument document = mock( KieDocument.class );
        final ObservablePath currentPath = mock( ObservablePath.class );
        when( document.getCurrentPath() ).thenReturn( currentPath );
        when( currentPath.getFileName() ).thenReturn( "filename" );
        return document;
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOnActivateDocument_WithCommand() {
        final KieDocument document = makeKieDocument();
        final ParameterizedCommand command = mock( ParameterizedCommand.class );
        builder.setActivateDocumentCommand( command );
        builder.onActivateDocument( document );

        verify( command,
                times( 1 ) ).execute( eq( document ) );
    }

    @Test
    public void testOnActivateDocument_WithoutCommand() {
        final KieDocument document = makeKieDocument();
        try {
            builder.onActivateDocument( document );
        } catch ( NullPointerException npe ) {
            fail( "Null Commands should be handled." );
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOnRemoveDocument_WithCommand() {
        final KieDocument document = makeKieDocument();
        final ParameterizedCommand command = mock( ParameterizedCommand.class );
        builder.setRemoveDocumentCommand( command );
        builder.onRemoveDocument( document );

        verify( command,
                times( 1 ) ).execute( eq( document ) );
    }

    @Test
    public void testOnRemoveDocument_WithoutCommand() {
        final KieDocument document = makeKieDocument();
        try {
            builder.onRemoveDocument( document );
        } catch ( NullPointerException npe ) {
            fail( "Null Commands should be handled." );
        }
    }

    @Test
    public void testActivateDocument_active() {
        final KieDocument document = makeKieDocument();
        builder.registerDocument( document );
        builder.activateDocument( document );

        verify( documentMenuItem,
                times( 1 ) ).setActive( eq( true ) );
    }

    @Test
    public void testActivateDocument_inactive() {
        final KieDocument document = makeKieDocument();
        builder.registerDocument( document );
        builder.activateDocument( mock( KieDocument.class ) );

        verify( documentMenuItem,
                times( 1 ) ).setActive( eq( false ) );
    }

    @Test
    public void testDispose() {
        final KieDocument document = makeKieDocument();
        builder.registerDocument( document );

        builder.dispose();

        verify( view,
                times( 1 ) ).clear();
        verify( beanManager,
                times( 1 ) ).destroyBean( documentMenuItem );
    }

}
