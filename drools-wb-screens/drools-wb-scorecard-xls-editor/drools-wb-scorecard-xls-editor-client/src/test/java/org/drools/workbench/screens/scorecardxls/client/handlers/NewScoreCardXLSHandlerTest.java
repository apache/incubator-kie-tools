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

package org.drools.workbench.screens.scorecardxls.client.handlers;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.enterprise.event.Event;

import com.google.gwt.user.client.Command;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.scorecardxls.client.type.ScoreCardXLSResourceType;
import org.guvnor.common.services.project.model.Package;
import org.jboss.errai.bus.client.api.ClientMessageBus;
import org.jboss.errai.bus.client.framework.ClientMessageBusImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.kie.workbench.common.widgets.client.handlers.NewResourceSuccessEvent;
import org.kie.workbench.common.widgets.client.widget.AttachmentFileWidget;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class NewScoreCardXLSHandlerTest {

    private NewScoreCardXLSHandler handler;

    @Mock
    private EventSourceMock<NewResourceSuccessEvent> newResourceSuccessEventMock;

    @Mock
    private PlaceManager placeManager;

    @Mock
    private BusyIndicatorView busyIndicatorView;

    @Mock
    private NewResourcePresenter newResourcePresenter;

    @Mock
    private ClientMessageBus clientMessageBus;

    @GwtMock
    private AttachmentFileWidget uploadWidget;

    @Captor
    private ArgumentCaptor<Command> successCmdCaptor;

    @Captor
    private ArgumentCaptor<Command> failureCmdCaptor;

    @Captor
    private ArgumentCaptor<Path> newPathCaptor;

    private ScoreCardXLSResourceType resourceType = new ScoreCardXLSResourceType();

    @Mock
    private EventSourceMock<NotificationEvent> mockNotificationEvent;

    @Before
    public void setup() {
        handler = new NewScoreCardXLSHandler( placeManager,
                                              resourceType,
                                              busyIndicatorView,
                                              clientMessageBus ) {
            {
                this.notificationEvent = mockNotificationEvent;
                this.newResourceSuccessEvent = newResourceSuccessEventMock;
            }

            @Override
            protected String encode( final String fileName ) {
                return NewScoreCardXLSHandlerTest.this.encode( fileName );
            }

            @Override
            protected String getClientId() {
                return "123";
            }
        };
        handler.setUploadWidget( uploadWidget );
    }

    @Test
    public void testSuccess() {
        final String fileName = "fileName";
        final Package pkg = mock( Package.class );
        final Path resourcesPath = PathFactory.newPath( "resources",
                                                        "default://project/src/main/resources" );

        when( pkg.getPackageMainResourcesPath() ).thenReturn( resourcesPath );
        handler.create( pkg,
                        fileName,
                        newResourcePresenter );

        verify( uploadWidget,
                times( 1 ) ).submit( eq( resourcesPath ),
                                     eq( fileName + "." + resourceType.getSuffix() ),
                                     any( String.class ),
                                     successCmdCaptor.capture(),
                                     failureCmdCaptor.capture() );

        successCmdCaptor.getValue().execute();

        verify( busyIndicatorView,
                times( 1 ) ).hideBusyIndicator();
        verify( newResourcePresenter,
                times( 1 ) ).complete();
        verify( mockNotificationEvent,
                times( 1 ) ).fire( any( NotificationEvent.class ) );
        verify( newResourceSuccessEventMock,
                times( 1 ) ).fire( any( NewResourceSuccessEvent.class ) );
        verify( placeManager,
                times( 1 ) ).goTo( newPathCaptor.capture() );

        assertEquals( "default://project/src/main/resources/fileName.sxls",
                      newPathCaptor.getValue().toURI() );
    }

    @Test
    public void testSuccessMultiByteProjectName() {
        final String fileName = "fileName";
        final Package pkg = mock( Package.class );
        final Path resourcesPath = PathFactory.newPath( "resources",
                                                        "default://" + encode( "ああ" ) + "/src/main/resources" );

        when( pkg.getPackageMainResourcesPath() ).thenReturn( resourcesPath );

        handler.create( pkg,
                        fileName,
                        newResourcePresenter );

        verify( uploadWidget,
                times( 1 ) ).submit( eq( resourcesPath ),
                                     eq( fileName + "." + resourceType.getSuffix() ),
                                     any( String.class ),
                                     successCmdCaptor.capture(),
                                     failureCmdCaptor.capture() );

        successCmdCaptor.getValue().execute();

        verify( busyIndicatorView,
                times( 1 ) ).hideBusyIndicator();
        verify( newResourcePresenter,
                times( 1 ) ).complete();
        verify( mockNotificationEvent,
                times( 1 ) ).fire( any( NotificationEvent.class ) );
        verify( newResourceSuccessEventMock,
                times( 1 ) ).fire( any( NewResourceSuccessEvent.class ) );
        verify( placeManager,
                times( 1 ) ).goTo( newPathCaptor.capture() );

        assertEquals( "default://%E3%81%82%E3%81%82/src/main/resources/fileName.sxls",
                      newPathCaptor.getValue().toURI() );
    }

    @Test
    public void testSuccessMultiByteFileName() {
        final String fileName = "あああ";
        final Package pkg = mock( Package.class );
        final Path resourcesPath = PathFactory.newPath( "resources",
                                                        "default://project/src/main/resources" );

        when( pkg.getPackageMainResourcesPath() ).thenReturn( resourcesPath );

        handler.create( pkg,
                        fileName,
                        newResourcePresenter );

        verify( uploadWidget,
                times( 1 ) ).submit( eq( resourcesPath ),
                                     eq( fileName + "." + resourceType.getSuffix() ),
                                     any( String.class ),
                                     successCmdCaptor.capture(),
                                     failureCmdCaptor.capture() );

        successCmdCaptor.getValue().execute();

        verify( busyIndicatorView,
                times( 1 ) ).hideBusyIndicator();
        verify( newResourcePresenter,
                times( 1 ) ).complete();
        verify( mockNotificationEvent,
                times( 1 ) ).fire( any( NotificationEvent.class ) );
        verify( newResourceSuccessEventMock,
                times( 1 ) ).fire( any( NewResourceSuccessEvent.class ) );
        verify( placeManager,
                times( 1 ) ).goTo( newPathCaptor.capture() );

        assertEquals( "default://project/src/main/resources/%E3%81%82%E3%81%82%E3%81%82.sxls",
                      newPathCaptor.getValue().toURI() );
    }

    @Test
    public void testSuccessMultiByteProjectNameAndFileName() {
        final String fileName = "あああ";
        final Package pkg = mock( Package.class );
        final Path resourcesPath = PathFactory.newPath( "resources",
                                                        "default://" + encode( "ああ" ) + "/src/main/resources" );

        when( pkg.getPackageMainResourcesPath() ).thenReturn( resourcesPath );

        handler.create( pkg,
                        fileName,
                        newResourcePresenter );

        verify( uploadWidget,
                times( 1 ) ).submit( eq( resourcesPath ),
                                     eq( fileName + "." + resourceType.getSuffix() ),
                                     any( String.class ),
                                     successCmdCaptor.capture(),
                                     failureCmdCaptor.capture() );

        successCmdCaptor.getValue().execute();

        verify( busyIndicatorView,
                times( 1 ) ).hideBusyIndicator();
        verify( newResourcePresenter,
                times( 1 ) ).complete();
        verify( mockNotificationEvent,
                times( 1 ) ).fire( any( NotificationEvent.class ) );
        verify( newResourceSuccessEventMock,
                times( 1 ) ).fire( any( NewResourceSuccessEvent.class ) );
        verify( placeManager,
                times( 1 ) ).goTo( newPathCaptor.capture() );

        assertEquals( "default://%E3%81%82%E3%81%82/src/main/resources/%E3%81%82%E3%81%82%E3%81%82.sxls",
                      newPathCaptor.getValue().toURI() );
    }

    @Test
    public void testGetServletUrl() {
        assertEquals( "scorecardxls/file?clientId=123", handler.getServletUrl() );
    }

    private String encode( final String s ) {
        try {
            return URLEncoder.encode( s, "UTF-8" );
        } catch ( UnsupportedEncodingException uee ) {
            fail( uee.getMessage() );
        }
        return "";
    }

}
