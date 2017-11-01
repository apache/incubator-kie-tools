/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.guided.dtable.client.handlers;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.guided.dtable.client.type.GuidedDTableGraphResourceType;
import org.drools.workbench.screens.guided.dtable.model.GuidedDecisionTableEditorGraphModel;
import org.drools.workbench.screens.guided.dtable.service.GuidedDecisionTableGraphEditorService;
import org.guvnor.common.services.project.model.Package;
import org.jboss.errai.common.client.api.Caller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.kie.workbench.common.widgets.client.handlers.NewResourceSuccessEvent;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class NewGuidedDecisionTableGraphHandlerTest {

    @Mock
    private PlaceManager placeManager;

    @Mock
    private GuidedDecisionTableGraphEditorService service;
    private Caller<GuidedDecisionTableGraphEditorService> serviceCaller;

    @Mock
    private BusyIndicatorView busyIndicatorView;

    @Mock
    private NewResourcePresenter newResourcePresenter;

    @Mock
    private EventSourceMock<NotificationEvent> mockNotificationEvent;

    @Mock
    private EventSourceMock<NewResourceSuccessEvent> newResourceSuccessEventMock;

    @Captor
    private ArgumentCaptor<Path> pathCaptor;

    @Captor
    private ArgumentCaptor<String> fileNameCaptor;

    private NewGuidedDecisionTableGraphHandler handler;
    private GuidedDTableGraphResourceType resourceType = new GuidedDTableGraphResourceType();

    @Before
    public void setup() {
        serviceCaller = new CallerMock<>( service );
        final NewGuidedDecisionTableGraphHandler wrapped = new NewGuidedDecisionTableGraphHandler( placeManager,
                                                                                                   serviceCaller,
                                                                                                   resourceType,
                                                                                                   busyIndicatorView ) {
            {
                this.notificationEvent = mockNotificationEvent;
                this.newResourceSuccessEvent = newResourceSuccessEventMock;
            }
        };
        handler = spy( wrapped );

        when( service.create( any( Path.class ),
                              any( String.class ),
                              any( GuidedDecisionTableEditorGraphModel.class ),
                              any( String.class ) ) ).<Path>thenAnswer( ( invocation ) -> {
            final Path path = ( (Path) invocation.getArguments()[ 0 ] );
            final String fileName = ( (String) invocation.getArguments()[ 1 ] );
            final Path newPath = PathFactory.newPath( fileName,
                                                      path.toURI() + "/" + fileName );
            return newPath;
        } );
    }

    @Test
    public void testCreate() {
        final String fileName = "fileName";
        final Package pkg = mock( Package.class );
        final Path resourcesPath = PathFactory.newPath( "resources",
                                                        "default://project/src/main/resources" );

        when( pkg.getPackageMainResourcesPath() ).thenReturn( resourcesPath );

        handler.create( pkg,
                        fileName,
                        newResourcePresenter );

        verify( busyIndicatorView,
                times( 1 ) ).hideBusyIndicator();
        verify( newResourcePresenter,
                times( 1 ) ).complete();
        verify( mockNotificationEvent,
                times( 1 ) ).fire( any( NotificationEvent.class ) );
        verify( newResourceSuccessEventMock,
                times( 1 ) ).fire( any( NewResourceSuccessEvent.class ) );
        verify( placeManager,
                times( 1 ) ).goTo( pathCaptor.capture() );

        assertEquals( "default://project/src/main/resources/fileName." + resourceType.getSuffix(),
                      pathCaptor.getValue().toURI() );

        verify( service,
                times( 1 ) ).create( eq( resourcesPath ),
                                     eq( fileName + "." + resourceType.getSuffix() ),
                                     any( GuidedDecisionTableEditorGraphModel.class ),
                                     any( String.class ) );
    }

}
