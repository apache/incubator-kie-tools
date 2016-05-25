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

package org.kie.workbench.common.screens.examples.client.wizard;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.enterprise.event.Event;

import org.guvnor.common.services.project.context.ProjectContextChangeEvent;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.examples.client.wizard.model.ExamplesWizardModel;
import org.kie.workbench.common.screens.examples.client.wizard.pages.organizationalunit.OUPage;
import org.kie.workbench.common.screens.examples.client.wizard.pages.project.ProjectPage;
import org.kie.workbench.common.screens.examples.client.wizard.pages.repository.RepositoryPage;
import org.kie.workbench.common.screens.examples.model.ExampleOrganizationalUnit;
import org.kie.workbench.common.screens.examples.model.ExampleRepository;
import org.kie.workbench.common.screens.examples.model.ExampleTargetRepository;
import org.kie.workbench.common.screens.examples.model.ExamplesMetaData;
import org.kie.workbench.common.screens.examples.service.ExamplesService;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.ext.widgets.core.client.wizards.WizardView;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ExamplesWizardTest {

    private static final String EXAMPLE_REPOSITORY1 = "https://github.com/guvnorngtestuser1/guvnorng-playground.git";
    private static final String EXAMPLE_REPOSITORY2 = "https://github.com/guvnorngtestuser1/jbpm-console-ng-playground-kjar.git";
    private static final String EXAMPLE_ORGANIZATIONAL_UNIT1 = "ou1";
    private static final String EXAMPLE_ORGANIZATIONAL_UNIT2 = "ou2";

    @Mock
    private RepositoryPage repositoryPage;

    @Mock
    private ProjectPage projectPage;

    @Mock
    private OUPage organizationalUnitPage;

    @Mock
    private BusyIndicatorView busyIndicatorView;

    private ExamplesService examplesService = mock( ExamplesService.class );
    private Caller<ExamplesService> examplesServiceCaller = new CallerMock<ExamplesService>( examplesService );

    @Spy
    private Event<ProjectContextChangeEvent> event = new EventSourceMock<ProjectContextChangeEvent>() {
        @Override
        public void fire( final ProjectContextChangeEvent event ) {
            //Do nothing. Default implementation throws an exception.
        }
    };

    @Mock
    private TranslationService translator;

    @Captor
    private ArgumentCaptor<Set<ExampleRepository>> repositoriesArgumentCaptor;

    @Captor
    private ArgumentCaptor<Set<ExampleOrganizationalUnit>> organizationalUnitsArgumentCaptor;

    @Mock
    private Callback<Boolean> callback;

    private final WizardView mockView = mock( WizardView.class );

    private final Set<ExampleRepository> repositories = new HashSet<ExampleRepository>() {{
        add( new ExampleRepository( EXAMPLE_REPOSITORY1 ) );
        add( new ExampleRepository( EXAMPLE_REPOSITORY2 ) );
    }};
    private final Set<ExampleOrganizationalUnit> organizationalUnits = new HashSet<ExampleOrganizationalUnit>() {{
        add( new ExampleOrganizationalUnit( EXAMPLE_ORGANIZATIONAL_UNIT1 ) );
        add( new ExampleOrganizationalUnit( EXAMPLE_ORGANIZATIONAL_UNIT2 ) );
    }};
    private ExamplesMetaData metaData = new ExamplesMetaData( repositories,
                                                              organizationalUnits );

    private ExamplesWizard wizard;

    @Before
    public void setup() {
        wizard = new ExamplesWizard( repositoryPage,
                                     projectPage,
                                     organizationalUnitPage,
                                     busyIndicatorView,
                                     examplesServiceCaller,
                                     event,
                                     translator ) {
            {
                this.view = mockView;
            }
        };
        when( examplesService.getMetaData() ).thenReturn( metaData );
    }

    @Test
    public void testStart() {
        final ArgumentCaptor<ExamplesWizardModel> modelArgumentCaptor = ArgumentCaptor.forClass( ExamplesWizardModel.class );

        wizard.start();
        verify( repositoryPage,
                times( 1 ) ).initialise();
        verify( projectPage,
                times( 1 ) ).initialise();
        verify( organizationalUnitPage,
                times( 1 ) ).initialise();
        verify( repositoryPage,
                times( 1 ) ).setModel( modelArgumentCaptor.capture() );
        verify( projectPage,
                times( 1 ) ).setModel( modelArgumentCaptor.getValue() );
        verify( organizationalUnitPage,
                times( 1 ) ).setModel( modelArgumentCaptor.getValue() );
        verify( repositoryPage,
                times( 1 ) ).setRepositories( repositoriesArgumentCaptor.capture() );
        verify( organizationalUnitPage,
                times( 1 ) ).setOrganizationalUnits( organizationalUnitsArgumentCaptor.capture() );

        assertEquals( repositories,
                      repositoriesArgumentCaptor.getValue() );
        assertEquals( organizationalUnits,
                      organizationalUnitsArgumentCaptor.getValue() );
    }

    @Test
    public void testClose() {
        wizard.close();

        verify( repositoryPage,
                times( 1 ) ).destroy();
        verify( projectPage,
                times( 1 ) ).destroy();
        verify( organizationalUnitPage,
                times( 1 ) ).destroy();
    }

    @Test
    public void testGetPageWidget() {
        wizard.getPageWidget( 0 );
        verify( repositoryPage,
                times( 1 ) ).prepareView();
        verify( repositoryPage,
                times( 1 ) ).asWidget();

        wizard.getPageWidget( 1 );
        verify( projectPage,
                times( 1 ) ).prepareView();
        verify( projectPage,
                times( 1 ) ).asWidget();

        wizard.getPageWidget( 2 );
        verify( organizationalUnitPage,
                times( 1 ) ).prepareView();
        verify( organizationalUnitPage,
                times( 1 ) ).asWidget();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testIsComplete_RepositoryPageIncomplete() {
        doAnswer( new Answer<Boolean>() {
            @Override
            public Boolean answer( final InvocationOnMock invocation ) throws Throwable {
                final Callback<Boolean> callback = (Callback<Boolean>) invocation.getArguments()[ 0 ];
                callback.callback( false );
                return null;
            }
        } ).when( repositoryPage ).isComplete( any( Callback.class ) );

        wizard.isComplete( callback );

        verify( callback,
                times( 1 ) ).callback( eq( true ) );
        verify( callback,
                times( 1 ) ).callback( eq( false ) );
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testIsComplete_ProjectPageIncomplete() {
        doAnswer( new Answer<Boolean>() {
            @Override
            public Boolean answer( final InvocationOnMock invocation ) throws Throwable {
                final Callback<Boolean> callback = (Callback<Boolean>) invocation.getArguments()[ 0 ];
                callback.callback( false );
                return null;
            }
        } ).when( projectPage ).isComplete( any( Callback.class ) );

        wizard.isComplete( callback );

        verify( callback,
                times( 1 ) ).callback( eq( true ) );
        verify( callback,
                times( 1 ) ).callback( eq( false ) );
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testIsComplete_OrganizationalUnitPageIncomplete() {
        doAnswer( new Answer<Boolean>() {
            @Override
            public Boolean answer( final InvocationOnMock invocation ) throws Throwable {
                final Callback<Boolean> callback = (Callback<Boolean>) invocation.getArguments()[ 0 ];
                callback.callback( false );
                return null;
            }
        } ).when( organizationalUnitPage ).isComplete( any( Callback.class ) );

        wizard.isComplete( callback );

        verify( callback,
                times( 1 ) ).callback( eq( true ) );
        verify( callback,
                times( 1 ) ).callback( eq( false ) );
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testIsComplete_AllPagesComplete() {
        doAnswer( new Answer<Boolean>() {
            @Override
            public Boolean answer( final InvocationOnMock invocation ) throws Throwable {
                final Callback<Boolean> callback = (Callback<Boolean>) invocation.getArguments()[ 0 ];
                callback.callback( true );
                return null;
            }
        } ).when( repositoryPage ).isComplete( any( Callback.class ) );
        doAnswer( new Answer<Boolean>() {
            @Override
            public Boolean answer( final InvocationOnMock invocation ) throws Throwable {
                final Callback<Boolean> callback = (Callback<Boolean>) invocation.getArguments()[ 0 ];
                callback.callback( true );
                return null;
            }
        } ).when( projectPage ).isComplete( any( Callback.class ) );
        doAnswer( new Answer<Boolean>() {
            @Override
            public Boolean answer( final InvocationOnMock invocation ) throws Throwable {
                final Callback<Boolean> callback = (Callback<Boolean>) invocation.getArguments()[ 0 ];
                callback.callback( true );
                return null;
            }
        } ).when( organizationalUnitPage ).isComplete( any( Callback.class ) );

        wizard.isComplete( callback );

        verify( callback,
                times( 1 ) ).callback( eq( true ) );
        verify( callback,
                never() ).callback( eq( false ) );
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOnComplete() {
        wizard.start();
        wizard.complete();

        verify( busyIndicatorView,
                times( 1 ) ).showBusyIndicator( any( String.class ) );
        verify( busyIndicatorView,
                times( 1 ) ).hideBusyIndicator();
        verify( examplesService,
                times( 1 ) ).setupExamples( any( ExampleOrganizationalUnit.class ),
                                            any( ExampleTargetRepository.class ),
                                            any( List.class ) );
        verify( event,
                times( 1 ) ).fire( any( ProjectContextChangeEvent.class ) );
    }

}
