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

package org.kie.workbench.common.screens.server.management.client.wizard;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

import javax.enterprise.event.Event;

import org.jboss.errai.common.client.api.Caller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.api.model.KieServerMode;
import org.kie.server.controller.api.model.spec.Capability;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.workbench.common.screens.server.management.client.events.ServerTemplateListRefresh;
import org.kie.workbench.common.screens.server.management.client.util.ContentChangeHandler;
import org.kie.workbench.common.screens.server.management.client.wizard.config.process.ProcessConfigPagePresenter;
import org.kie.workbench.common.screens.server.management.client.wizard.container.NewContainerFormPresenter;
import org.kie.workbench.common.screens.server.management.client.wizard.template.NewTemplatePresenter;
import org.kie.workbench.common.screens.server.management.service.SpecManagementService;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.ext.widgets.core.client.wizards.AbstractWizard;
import org.uberfire.ext.widgets.core.client.wizards.WizardView;
import org.uberfire.mocks.CallerMock;
import org.uberfire.workbench.events.NotificationEvent;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NewServerTemplateWizardTest {

    @Mock
    NewTemplatePresenter newTemplatePresenter;

    @Mock
    NewTemplatePresenter.View newTemplatePresenterView;

    @Mock
    NewContainerFormPresenter newContainerFormPresenter;

    @Mock
    ProcessConfigPagePresenter processConfigPagePresenter;

    @Mock
    Event<NotificationEvent> notification;

    @Mock
    Event<ServerTemplateListRefresh> serverTemplateListRefreshEvent;

    @Mock
    SpecManagementService specManagementService;

    Caller<SpecManagementService> specManagementServiceCaller;

    @Mock
    WizardView view;

    NewServerTemplateWizard newServerTemplateWizard;

    private final Collection<ContentChangeHandler> contentChangeHandlers = new ArrayList<ContentChangeHandler>();

    private final Answer contentHandlerAnswer = new Answer() {
        @Override
        public Object answer( final InvocationOnMock invocationOnMock ) throws Throwable {
            fireContentHandlers();
            return null;
        }
    };

    private void fireContentHandlers() {
        for ( final ContentChangeHandler handler : contentChangeHandlers ) {
            handler.onContentChange();
        }
    }

    @Before
    public void setup() throws IllegalAccessException, NoSuchFieldException {
        specManagementServiceCaller = new CallerMock<SpecManagementService>( specManagementService );
        when( newTemplatePresenter.getView() ).thenReturn( newTemplatePresenterView );

        final Answer addContentChangeHandler = new Answer() {
            @Override
            public Object answer( InvocationOnMock invocation ) throws Throwable {
                final ContentChangeHandler handler = (ContentChangeHandler) invocation.getArguments()[ 0 ];
                contentChangeHandlers.add( handler );
                return null;
            }
        };
        doAnswer( addContentChangeHandler ).when( newTemplatePresenter ).addContentChangeHandler( any( ContentChangeHandler.class ) );
        doAnswer( addContentChangeHandler ).when( newContainerFormPresenter ).addContentChangeHandler( any( ContentChangeHandler.class ) );

        newServerTemplateWizard = new NewServerTemplateWizard(
                newTemplatePresenter,
                newContainerFormPresenter,
                processConfigPagePresenter,
                specManagementServiceCaller,
                notification,
                serverTemplateListRefreshEvent
        );

        final Field field = AbstractWizard.class.getDeclaredField( "view" );
        field.setAccessible( true );
        field.set( newServerTemplateWizard, view );

        newServerTemplateWizard = spy( newServerTemplateWizard );
    }

    @Test
    public void testTitle() {
        final String title = "title";
        when( newTemplatePresenterView.getNewServerTemplateWizardTitle() ).thenReturn( title );

        assertEquals( title, newServerTemplateWizard.getTitle() );
        verify( newTemplatePresenterView ).getNewServerTemplateWizardTitle();
    }

    @Test
    public void testPages() {
        when( newTemplatePresenter.hasProcessCapability() ).thenReturn( true );
        when( newContainerFormPresenter.isEmpty() ).thenReturn( false );
        when( newContainerFormPresenter.isValid() ).thenReturn( true );

        fireContentHandlers();

        assertEquals( 3, newServerTemplateWizard.getPages().size() );
        verify( view ).selectPage( 0 );

        fireContentHandlers();

        assertEquals( 3, newServerTemplateWizard.getPages().size() );
        verify( view ).selectPage( 0 );

        when( newTemplatePresenter.hasProcessCapability() ).thenReturn( true );
        when( newContainerFormPresenter.isEmpty() ).thenReturn( true );
        when( newContainerFormPresenter.isValid() ).thenReturn( false );

        fireContentHandlers();

        assertEquals( 2, newServerTemplateWizard.getPages().size() );
    }

    @Test
    public void testComplete() {
        ServerTemplate serverTemplate = new ServerTemplate("template-name", "template-name");
        serverTemplate.setMode(KieServerMode.DEVELOPMENT);
        serverTemplate.setCapabilities(singletonList(Capability.PROCESS.toString()));

        when( newTemplatePresenter.isProcessCapabilityChecked() ).thenReturn( true );
        when( newContainerFormPresenter.isEmpty() ).thenReturn( true );
        when( newContainerFormPresenter.isEmpty() ).thenReturn( true );
        when( newTemplatePresenter.getTemplateName() ).thenReturn( "template-name" );
        final String successMessage = "SUCCESS";
        when( newTemplatePresenterView.getNewServerTemplateWizardSaveSuccess() ).thenReturn( successMessage );

        newServerTemplateWizard.complete();

        ArgumentCaptor<ServerTemplate> serverTemplateCapture = ArgumentCaptor.forClass(ServerTemplate.class);
        verify(specManagementService).saveServerTemplate(serverTemplateCapture.capture());
        assertEquals(KieServerMode.DEVELOPMENT, serverTemplateCapture.getValue().getMode());

        verify( notification ).fire( new NotificationEvent( successMessage, NotificationEvent.NotificationType.SUCCESS ) );
        verifyClear();
        verify( serverTemplateListRefreshEvent ).fire( new ServerTemplateListRefresh( "template-name" ) );


        doThrow( new RuntimeException() ).when( specManagementService ).saveServerTemplate( any( ServerTemplate.class ) );
        final String errorMessage = "ERROR";
        when( newTemplatePresenterView.getNewServerTemplateWizardSaveError() ).thenReturn( errorMessage );

        newServerTemplateWizard.complete();

        verify( notification ).fire( new NotificationEvent( errorMessage, NotificationEvent.NotificationType.ERROR ) );
        verify( newServerTemplateWizard ).pageSelected( 0 );
        verify( newServerTemplateWizard ).start();

        verify( newContainerFormPresenter ).initialise();
    }

    @Test
    public void testClear() {
        newServerTemplateWizard.clear();

        verifyClear();
    }

    @Test
    public void testClose() {
        newServerTemplateWizard.close();

        verifyClear();
    }

    private void verifyClear() {
        verify( newTemplatePresenter ).clear();
        verify( newContainerFormPresenter ).clear();
        verify( processConfigPagePresenter ).clear();

        assertEquals( 2, newServerTemplateWizard.getPages().size() );
        assertTrue( newServerTemplateWizard.getPages().contains( newTemplatePresenter ) );
        assertTrue( newServerTemplateWizard.getPages().contains( newContainerFormPresenter ) );
    }
}
