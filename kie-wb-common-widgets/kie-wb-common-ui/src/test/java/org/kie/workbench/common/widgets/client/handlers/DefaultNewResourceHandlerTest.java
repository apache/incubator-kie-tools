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

import javax.enterprise.event.Event;

import com.google.gwt.core.client.Callback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.jboss.errai.common.client.api.Caller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.kie.workbench.common.services.shared.validation.ValidationService;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.editor.commons.client.validation.ValidatorWithReasonCallback;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.type.ResourceTypeDefinition;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class DefaultNewResourceHandlerTest {

    private DefaultNewResourceHandler handler;

    private PackageListBox packagesListBox;
    private ProjectContext context;
    private KieProjectService projectService;
    private Caller<KieProjectService> projectServiceCaller;
    private ValidationService validationService;
    private Caller<ValidationService> validationServiceCaller;
    private PlaceManager placeManager;
    private Event<NotificationEvent> notificationEvent;
    private BusyIndicatorView busyIndicatorView;

    @Before
    public void setup() {
        packagesListBox = mock( PackageListBox.class );
        context = mock( ProjectContext.class );
        projectService = mock( KieProjectService.class );
        projectServiceCaller = new CallerMock<KieProjectService>( projectService );
        validationService = mock( ValidationService.class );
        validationServiceCaller = new CallerMock<ValidationService>( validationService );
        placeManager = mock( PlaceManager.class );
        notificationEvent = new EventSourceMock<NotificationEvent>();
        busyIndicatorView = mock( BusyIndicatorView.class );

        handler = new DefaultNewResourceHandler( packagesListBox,
                                                 context,
                                                 projectServiceCaller,
                                                 validationServiceCaller,
                                                 placeManager,
                                                 notificationEvent,
                                                 busyIndicatorView ) {
            @Override
            public String getDescription() {
                return "mock";
            }

            @Override
            public IsWidget getIcon() {
                return null;
            }

            @Override
            public ResourceTypeDefinition getResourceType() {
                final ResourceTypeDefinition resourceType = mock( ResourceTypeDefinition.class );
                when( resourceType.getPrefix() ).thenReturn( "" );
                when( resourceType.getSuffix() ).thenReturn( "suffix" );
                return resourceType;
            }

            @Override
            public void create( final org.guvnor.common.services.project.model.Package pkg,
                                final String baseFileName,
                                final NewResourcePresenter presenter ) {

            }
        };
    }

    @Test
    public void testValidateValidFileName() {
        final org.guvnor.common.services.project.model.Package pkg = mock( Package.class );
        final ValidatorWithReasonCallback callback = mock( ValidatorWithReasonCallback.class );
        when( packagesListBox.getSelectedPackage() ).thenReturn( pkg );
        when( validationService.isFileNameValid( "filename.suffix" ) ).thenReturn( true );

        handler.validate( "filename",
                          callback );

        verify( callback,
                times( 1 ) ).onSuccess();
        verify( callback,
                never() ).onFailure();
        verify( callback,
                never() ).onFailure( any( String.class ) );
    }

    @Test
    public void testValidateInvalidFileName() {
        final org.guvnor.common.services.project.model.Package pkg = mock( Package.class );
        final ValidatorWithReasonCallback callback = mock( ValidatorWithReasonCallback.class );
        when( packagesListBox.getSelectedPackage() ).thenReturn( pkg );
        when( validationService.isFileNameValid( "filename.suffix" ) ).thenReturn( false );

        handler.validate( "filename",
                          callback );

        verify( callback,
                times( 1 ) ).onFailure( any( String.class ) );
        verify( callback,
                never() ).onFailure();
        verify( callback,
                never() ).onSuccess();
    }

    @Test
    public void testAcceptContextWithNoContext() {
        final Callback<Boolean, Void> callback = mock( Callback.class );

        handler.acceptContext( callback );

        verify( callback,
                times( 1 ) ).onSuccess( false );
    }

    @Test
    public void testAcceptContextWithContextWithNoProject() {
        final Callback<Boolean, Void> callback = mock( Callback.class );
        when( context.getActiveProject() ).thenReturn( null );

        handler.acceptContext( callback );
        verify( callback,
                times( 1 ) ).onSuccess( false );
    }

    @Test
    public void testAcceptContextWithContextWithProject() {
        final Callback<Boolean, Void> callback = mock( Callback.class );
        when( context.getActiveProject() ).thenReturn( mock( Project.class ) );

        handler.acceptContext( callback );
        verify( callback,
                times( 1 ) ).onSuccess( true );
    }

    @Test
    public void testGetCommand() {
        final NewResourcePresenter presenter = mock( NewResourcePresenter.class );
        final Command command = handler.getCommand( presenter );
        assertNotNull( command );

        command.execute();
        verify( presenter,
                times( 1 ) ).show( handler );
    }

}
