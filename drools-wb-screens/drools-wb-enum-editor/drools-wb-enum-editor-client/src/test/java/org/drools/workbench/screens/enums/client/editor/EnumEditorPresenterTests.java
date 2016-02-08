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

package org.drools.workbench.screens.enums.client.editor;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.event.Event;

import com.google.gwt.core.client.GWT;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.enums.client.type.EnumResourceType;
import org.drools.workbench.screens.enums.model.EnumModel;
import org.drools.workbench.screens.enums.model.EnumModelContent;
import org.drools.workbench.screens.enums.service.EnumService;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.jboss.errai.common.client.api.Caller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.kie.workbench.common.widgets.client.source.ViewDRLSourceWidget;
import org.kie.workbench.common.widgets.metadata.client.KieEditorWrapperView;
import org.kie.workbench.common.widgets.metadata.client.widget.OverviewWidgetPresenter;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.ext.editor.commons.client.history.VersionRecordManager;
import org.uberfire.ext.editor.commons.client.validation.DefaultFileNameValidator;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.MenuItem;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class EnumEditorPresenterTests {

    @Mock
    private EnumEditorView view;

    @Mock
    private KieEditorWrapperView mockKieView;

    @Mock
    private OverviewWidgetPresenter mockOverviewWidget;

    @Mock
    private VersionRecordManager mockVersionRecordManager;

    @Mock
    private FileMenuBuilder mockFileMenuBuilder;

    @Mock
    private DefaultFileNameValidator mockFileNameValidator;

    @Mock
    private EnumService enumService;
    private Caller<EnumService> enumServiceCaller;

    @Mock
    private ObservablePath path;

    @Mock
    private PlaceRequest place;

    @Mock
    private Overview overview;

    @GwtMock
    private ViewDRLSourceWidget sourceWidget;

    @Captor
    private ArgumentCaptor<List<EnumRow>> enumsArgumentCaptor;

    @Captor
    private ArgumentCaptor<String> enumStringArgumentCaptor;

    private Event<NotificationEvent> mockNotification = new EventSourceMock<NotificationEvent>() {
        @Override
        public void fire( final NotificationEvent event ) {
            //Do nothing. Default implementation throws a RuntimeException
        }
    };

    private EnumResourceType type;

    private EnumEditorPresenter presenter;

    private EnumModelContent content;
    private EnumModel model;

    @Before
    public void setup() {
        //Mock EnumResourceType
        this.type = GWT.create( EnumResourceType.class );

        //Mock FileMenuBuilder usage since we cannot use FileMenuBuilderImpl either
        when( mockFileMenuBuilder.addSave( any( MenuItem.class ) ) ).thenReturn( mockFileMenuBuilder );
        when( mockFileMenuBuilder.addCopy( any( ObservablePath.class ),
                                           eq( mockFileNameValidator ) ) ).thenReturn( mockFileMenuBuilder );
        when( mockFileMenuBuilder.addRename( any( ObservablePath.class ),
                                             eq( mockFileNameValidator ) ) ).thenReturn( mockFileMenuBuilder );
        when( mockFileMenuBuilder.addDelete( any( ObservablePath.class ) ) ).thenReturn( mockFileMenuBuilder );
        when( mockFileMenuBuilder.addValidate( any( Command.class ) ) ).thenReturn( mockFileMenuBuilder );
        when( mockFileMenuBuilder.addNewTopLevelMenu( any( MenuItem.class ) ) ).thenReturn( mockFileMenuBuilder );

        when( mockVersionRecordManager.getCurrentPath() ).thenReturn( path );
        when( mockVersionRecordManager.getPathToLatest() ).thenReturn( path );

        this.model = new EnumModel( "'Fact.field' : ['a', 'b']" );
        this.content = new EnumModelContent( model,
                                             overview );

        when( enumService.loadContent( path ) ).thenReturn( content );

        when( view.getContent() ).thenReturn( new ArrayList<EnumRow>() {{
            add( new EnumRow( "Fact",
                              "field",
                              "['a', 'b']" ) );
        }} );

        this.enumServiceCaller = new CallerMock<EnumService>( enumService );

        this.presenter = new EnumEditorPresenter( view,
                                                  enumServiceCaller,
                                                  type ) {
            {
                //Yuck, yuck, yuck... the class hierarchy is really a mess
                this.kieView = mockKieView;
                this.overviewWidget = mockOverviewWidget;
                this.menuBuilder = mockFileMenuBuilder;
                this.fileNameValidator = mockFileNameValidator;
                this.versionRecordManager = mockVersionRecordManager;
                this.notification = mockNotification;
            }

        };
    }

    @Test
    public void testOnStartup() {
        presenter.onStartup( path,
                             place );
        verify( enumService,
                times( 1 ) ).loadContent( path );

        verify( view,
                times( 1 ) ).setContent( enumsArgumentCaptor.capture() );
        verify( view,
                times( 1 ) ).hideBusyIndicator();

        final List<EnumRow> enums = enumsArgumentCaptor.getValue();
        assertNotNull( enums );
        assertEquals( 1,
                      enums.size() );
        final EnumRow enumRow = enums.get( 0 );
        assertNotNull( enumRow );
        assertEquals( "Fact",
                      enumRow.getFactName() );
        assertEquals( "field",
                      enumRow.getFieldName() );
        assertEquals( "['a', 'b']",
                      enumRow.getContext() );
    }

    @Test
    public void testOnSave() {
        presenter.onStartup( path,
                             place );
        presenter.save( "message" );

        verify( view,
                times( 1 ) ).getContent();
        verify( enumService,
                times( 1 ) ).save( eq( path ),
                                   enumStringArgumentCaptor.capture(),
                                   any( Metadata.class ),
                                   eq( "message" ) );
        final String enumString = enumStringArgumentCaptor.getValue();
        assertNotNull( enumString );
        assertEquals( enumString,
                      "'Fact.field' : ['a', 'b']\n" );
    }

    @Test
    public void testOnSourceTabSelected() {
        presenter.onStartup( path,
                             place );
        presenter.onSourceTabSelected();

        verify( view,
                times( 1 ) ).getContent();

    }

}
