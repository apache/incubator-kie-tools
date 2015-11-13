/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.editor.commons.client.history;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.event.Event;
import javax.enterprise.util.TypeLiteral;

import com.google.gwt.view.client.AsyncDataProvider;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.client.history.event.VersionSelectedEvent;
import org.uberfire.ext.editor.commons.version.VersionService;
import org.uberfire.java.nio.base.version.VersionRecord;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class VersionHistoryPresenterTest {

    private VersionHistoryPresenterView view;
    private VersionHistoryPresenterView.Presenter presenter;
    private VersionService service;
    private VersionHistoryPresenter screen;
    private List<VersionRecord> records = new ArrayList<VersionRecord>();
    private VersionHistoryPresenterTest.VersionSelectedEventMock event;
    private Path path111;
    private Path path222;
    private Path path333;

    @Before
    public void setUp() throws Exception {
        view = mock( VersionHistoryPresenterView.class );

        path111 = mock( Path.class );
        when( path111.toURI() ).thenReturn( "hehe//test.file" );
        path222 = mock( Path.class );
        when( path222.toURI() ).thenReturn( "hehe//test.file" );
        path333 = mock( Path.class );
        when( path333.toURI() ).thenReturn( "hehe//test.file" );
        event = spy( new VersionSelectedEventMock() );

        screen = new VersionHistoryPresenter(
                view,
                new VersionServiceMock(),
                event );

        presenter = screen;
    }

    @Test
    public void testSetPresenter() throws Exception {
        verify( view ).setPresenter( presenter );
    }

    @Test
    public void testLoadHistory() throws Exception {

        VersionRecord versionRecord1 = getVersionRecord( "111" );
        records.add( versionRecord1 );
        VersionRecord versionRecord2 = getVersionRecord( "222" );
        records.add( versionRecord2 );
        VersionRecord versionRecord3 = getVersionRecord( "333" );
        records.add( versionRecord3 );

        screen.init( path333 );

        screen.refresh( "333" );

        verify( view ).setup( eq( "333" ), any( AsyncDataProvider.class ) );
    }

    @Test
    public void testSelectVersion() throws Exception {
        VersionRecord versionRecord1 = getVersionRecord( "111" );
        records.add( versionRecord1 );
        VersionRecord versionRecord2 = getVersionRecord( "222" );
        records.add( versionRecord2 );

        screen.init( path222 );

        screen.onSelect( versionRecord1 );

        ArgumentCaptor<VersionSelectedEvent> argumentCaptor = ArgumentCaptor.forClass( VersionSelectedEvent.class );
        verify( event ).fire( argumentCaptor.capture() );

        assertEquals( versionRecord1, argumentCaptor.getValue().getVersionRecord() );

    }

    @Test
    public void testVersionChanges() throws Exception {
        VersionRecord versionRecord1 = getVersionRecord( "111" );
        records.add( versionRecord1 );
        VersionRecord versionRecord2 = getVersionRecord( "222" );
        records.add( versionRecord2 );

        screen.init( path222 );

        screen.refresh( "222" );

        verify( view ).setup( eq( "222" ), any( AsyncDataProvider.class ) );

        Path pathToFile = mock( Path.class );
        when( pathToFile.toURI() ).thenReturn( "hehe//test.file" );
        screen.onVersionChange( new VersionSelectedEvent( pathToFile, versionRecord1 ) );

        verify( view ).setup( eq( "111" ), any( AsyncDataProvider.class ) );
    }

    @Test
    public void testVersionChangeForSomeOtherFile() throws Exception {
        VersionRecord versionRecord1 = getVersionRecord( "111" );
        records.add( versionRecord1 );
        VersionRecord versionRecord2 = getVersionRecord( "222" );
        records.add( versionRecord2 );

        screen.init( path222 );

        screen.refresh( "222" );

        verify( view ).setup( eq( "222" ), any( AsyncDataProvider.class ) );

        Path pathToFile = mock( Path.class );
        when( pathToFile.toURI() ).thenReturn( "hehe//another.file" );
        screen.onVersionChange( new VersionSelectedEvent( pathToFile, getVersionRecord( "111" ) ) );

        verify( view ).setup( eq( "222" ), any( AsyncDataProvider.class ) );

    }

    private VersionRecord getVersionRecord( String version ) {
        VersionRecord versionRecord = mock( VersionRecord.class );
        when( versionRecord.id() ).thenReturn( version );
        when( versionRecord.uri() ).thenReturn( "hehe//test.file" );
        return versionRecord;
    }

    private class VersionServiceMock
            implements Caller<VersionService> {

        RemoteCallback callback;

        private VersionServiceMock() {

            service = new VersionService() {
                public List<VersionRecord> getVersions( final Path path ) {
                    callback.callback( records );
                    return null;
                }

                public Path getPathToPreviousVersion( String uri ) {
                    return null;
                }

                public Path restore( final Path path,
                                     final String comment ) {
                    return null;
                }

            };
        }

        @Override
        public VersionService call() {
            return service;
        }

        @Override
        public VersionService call( RemoteCallback<?> remoteCallback ) {
            callback = remoteCallback;
            return service;
        }

        @Override
        public VersionService call( RemoteCallback<?> remoteCallback,
                                    ErrorCallback<?> errorCallback ) {
            callback = remoteCallback;
            return service;
        }
    }

    private class VersionSelectedEventMock
            implements Event<VersionSelectedEvent> {

        @Override
        public void fire( VersionSelectedEvent event ) {

        }

        @Override
        public Event<VersionSelectedEvent> select( Annotation... annotations ) {
            return null;
        }

        @Override
        public <U extends VersionSelectedEvent> Event<U> select( Class<U> uClass,
                                                                 Annotation... annotations ) {
            return null;
        }

        @Override
        public <U extends VersionSelectedEvent> Event<U> select( TypeLiteral<U> subtype,
                                                                 Annotation... qualifiers ) {
            return null;
        }
    }
}
