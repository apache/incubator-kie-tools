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

package org.drools.workbench.screens.guided.dtable.client.handlers;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52.TableFormat;
import org.drools.workbench.screens.guided.dtable.client.type.GuidedDTableResourceType;
import org.drools.workbench.screens.guided.dtable.client.wizard.NewGuidedDecisionTableWizard;
import org.drools.workbench.screens.guided.dtable.service.GuidedDecisionTableEditorService;
import org.guvnor.common.services.project.model.Package;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracleFactory;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class NewGuidedDecisionTableHandlerTest {

    @Mock
    private PlaceManager placeManager;

    @Mock
    private GuidedDecisionTableEditorService service;
    private Caller<GuidedDecisionTableEditorService> serviceCaller;

    @Mock
    private BusyIndicatorView busyIndicatorView;

    @Mock
    private NewResourcePresenter newResourcePresenter;

    @Mock
    private EventSourceMock<NotificationEvent> mockNotificationEvent;

    @Mock
    private AsyncPackageDataModelOracleFactory oracleFactory;

    @Mock
    private SyncBeanManager beanManager;

    @Mock
    private SyncBeanDef<NewGuidedDecisionTableWizard> wizardBeanDef;

    @Mock
    private NewGuidedDecisionTableWizard wizardBean;

    @GwtMock
    private GuidedDecisionTableOptions options;

    @Captor
    private ArgumentCaptor<Path> pathCaptor;

    @Captor
    private ArgumentCaptor<String> fileNameCaptor;

    private NewGuidedDecisionTableHandler handler;
    private GuidedDTableResourceType resourceType = new GuidedDTableResourceType();

    @Before
    public void setup() {
        serviceCaller = new CallerMock<>( service );
        final NewGuidedDecisionTableHandler wrapped = new NewGuidedDecisionTableHandler( placeManager,
                                                                                         serviceCaller,
                                                                                         resourceType,
                                                                                         options,
                                                                                         busyIndicatorView,
                                                                                         oracleFactory,
                                                                                         beanManager ) {
            {
                this.notificationEvent = mockNotificationEvent;
            }
        };
        handler = spy( wrapped );

        when( beanManager.lookupBean( eq( NewGuidedDecisionTableWizard.class ) ) ).thenReturn( wizardBeanDef );
        when( wizardBeanDef.getInstance() ).thenReturn( wizardBean );

        when( service.create( any( Path.class ),
                              any( String.class ),
                              any( GuidedDecisionTable52.class ),
                              any( String.class ) ) ).<Path>thenAnswer( ( invocation ) -> {
            final Path path = ( (Path) invocation.getArguments()[ 0 ] );
            final String fileName = ( (String) invocation.getArguments()[ 1 ] );
            final Path newPath = PathFactory.newPath( fileName,
                                                      path.toURI() + "/" + fileName );
            return newPath;
        } );
    }

    @Test
    public void testCreate_WithWizard() {
        final String fileName = "fileName";
        final Package pkg = mock( Package.class );
        final Path resourcesPath = PathFactory.newPath( "resources",
                                                        "default://project/src/main/resources" );

        when( pkg.getPackageMainResourcesPath() ).thenReturn( resourcesPath );
        when( options.isUsingWizard() ).thenReturn( true );
        when( options.getTableFormat() ).thenReturn( TableFormat.EXTENDED_ENTRY );

        handler.create( pkg,
                        fileName,
                        newResourcePresenter );

        verify( wizardBean,
                times( 1 ) ).setContent( pathCaptor.capture(),
                                         fileNameCaptor.capture(),
                                         eq( TableFormat.EXTENDED_ENTRY ),
                                         any( AsyncPackageDataModelOracle.class ),
                                         eq( handler ) );
    }

    @Test
    public void testCreate_WithoutWizard() {
        final String fileName = "fileName";
        final Package pkg = mock( Package.class );
        final Path resourcesPath = PathFactory.newPath( "resources",
                                                        "default://project/src/main/resources" );

        when( pkg.getPackageMainResourcesPath() ).thenReturn( resourcesPath );
        when( options.isUsingWizard() ).thenReturn( false );

        handler.create( pkg,
                        fileName,
                        newResourcePresenter );

        verify( busyIndicatorView,
                times( 1 ) ).hideBusyIndicator();
        verify( newResourcePresenter,
                times( 1 ) ).complete();
        verify( mockNotificationEvent,
                times( 1 ) ).fire( any( NotificationEvent.class ) );

        verify( placeManager,
                times( 1 ) ).goTo( pathCaptor.capture() );

        assertEquals( "default://project/src/main/resources/fileName.gdst",
                      pathCaptor.getValue().toURI() );

        verify( service,
                times( 1 ) ).create( eq( resourcesPath ),
                                     eq( fileName + "." + resourceType.getSuffix() ),
                                     any( GuidedDecisionTable52.class ),
                                     any( String.class ) );
    }

}
