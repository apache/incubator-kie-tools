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
package org.drools.workbench.screens.dtablexls.client.editor;

import java.util.List;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.guided.dtable.shared.conversion.ConversionResult;
import org.drools.workbench.screens.dtablexls.client.resources.DecisionTableXLSResources;
import org.drools.workbench.screens.dtablexls.client.resources.i18n.DecisionTableXLSEditorConstants;
import org.drools.workbench.screens.dtablexls.client.resources.images.DecisionTableXLSImageResources;
import org.drools.workbench.screens.dtablexls.client.type.DecisionTableXLSResourceType;
import org.drools.workbench.screens.dtablexls.client.type.DecisionTableXLSXResourceType;
import org.drools.workbench.screens.dtablexls.service.DecisionTableXLSContent;
import org.drools.workbench.screens.dtablexls.service.DecisionTableXLSService;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.metadata.client.KieEditorWrapperView;
import org.kie.workbench.common.widgets.metadata.client.widget.OverviewWidgetPresenter;
import org.mockito.Mock;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.client.history.VersionRecordManager;
import org.uberfire.mvp.PlaceRequest;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class DecisionTableXLSEditorPresenterTest {

    @GwtMock
    DecisionTableXLSImageResources decisionTableXLSImageResources;

    @GwtMock
    DecisionTableXLSResources decisionTableXLSResources;

    @GwtMock
    DecisionTableXLSEditorConstants decisionTableXLSEditorConstants;

    @Mock
    DecisionTableXLSResourceType decisionTableXLSResourceType;

    @Mock
    DecisionTableXLSXResourceType decisionTableXLSXResourceType;

    @Mock
    ObservablePath XLSPath;

    @Mock
    ObservablePath XLSXPath;

    @Mock
    DecisionTableXLSEditorView view;

    @Mock
    KieEditorWrapperView kieView;

    DecisionTableXLSEditorPresenter presenter;

    @Before
    public void setUp() throws Exception {

        when( decisionTableXLSResourceType.getSuffix() ).thenReturn( "XLS" );
        when( decisionTableXLSResourceType.accept( XLSPath ) ).thenReturn( true );
        when( decisionTableXLSResourceType.accept( XLSXPath ) ).thenReturn( false );

        when( decisionTableXLSXResourceType.getSuffix() ).thenReturn( "XLSX" );
        when( decisionTableXLSXResourceType.accept( XLSPath ) ).thenReturn( false );
        when( decisionTableXLSXResourceType.accept( XLSXPath ) ).thenReturn( true );

        presenter = new DecisionTableXLSEditorPresenter( view,
                                                         decisionTableXLSResourceType,
                                                         decisionTableXLSXResourceType,
                                                         new ServiceMock() ) {
            {
                kieView = mock( KieEditorWrapperView.class );
                versionRecordManager = mock( VersionRecordManager.class );
                overviewWidget = mock( OverviewWidgetPresenter.class );
            }

            protected void makeMenuBar() {

            }

            protected void addSourcePage() {

            }
        };
    }

    @Test
    public void testXLSSetup() throws Exception {
        presenter.onStartup( XLSPath,
                             mock( PlaceRequest.class ) );

        verify( view ).init( presenter );
        verify( view ).setupUploadWidget( decisionTableXLSResourceType );
        verify( view ).setPath( any( Path.class ) );
        verify( view ).setReadOnly( false );
    }

    @Test
    public void testXLSXSetup() throws Exception {
        presenter.onStartup( XLSXPath,
                             mock( PlaceRequest.class ) );

        verify( view ).init( presenter );
        verify( view ).setupUploadWidget( decisionTableXLSXResourceType );
        verify( view ).setPath( any( Path.class ) );
        verify( view ).setReadOnly( false );
    }

    private class ServiceMock
            implements Caller<DecisionTableXLSService> {

        private DecisionTableXLSService decisionTableXLSService = new DecisionTableXLSServiceMock();
        RemoteCallback remoteCallback;

        @Override
        public DecisionTableXLSService call() {
            return decisionTableXLSService;
        }

        @Override
        public DecisionTableXLSService call( RemoteCallback<?> remoteCallback ) {
            return call( remoteCallback, null );
        }

        @Override
        public DecisionTableXLSService call( RemoteCallback<?> remoteCallback, ErrorCallback<?> errorCallback ) {
            this.remoteCallback = remoteCallback;
            return decisionTableXLSService;
        }

        private class DecisionTableXLSServiceMock
                implements DecisionTableXLSService {

            @Override public ConversionResult convert( Path path ) {
                return null;
            }

            @Override
            public DecisionTableXLSContent loadContent( Path path ) {
                DecisionTableXLSContent content = new DecisionTableXLSContent();
                content.setOverview( new Overview() );
                remoteCallback.callback( content );
                return null;
            }

            @Override public String getSource( Path path ) {
                return null;
            }

            @Override public Path copy( Path path, String newName, String comment ) {
                return null;
            }

            @Override public void delete( Path path, String comment ) {

            }

            @Override public Path rename( Path path, String newName, String comment ) {
                return null;
            }

            @Override public List<ValidationMessage> validate( Path path, Path content ) {
                return null;
            }
        }
    }
}