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

package org.kie.workbench.common.screens.examples.client.wizard.pages.repository;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.enterprise.event.Event;

import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.examples.client.wizard.model.ExamplesWizardModel;
import org.kie.workbench.common.screens.examples.model.ExampleRepository;
import org.kie.workbench.common.screens.examples.service.ExamplesService;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RepositoryPageTest {

    private static final String EXAMPLE_REPOSITORY = "https://github.com/guvnorngtestuser1/guvnorng-playground.git";

    @Mock
    private RepositoryPageView view;

    @Mock
    private TranslationService translator;

    private ExamplesService examplesService = mock( ExamplesService.class );
    private Caller<ExamplesService> examplesServiceCaller = new CallerMock<ExamplesService>( examplesService );

    @Spy
    private Event<WizardPageStatusChangeEvent> pageStatusChangedEvent = new EventSourceMock<WizardPageStatusChangeEvent>() {
        @Override
        public void fire( final WizardPageStatusChangeEvent event ) {
            //Do nothing. Default implementation throws an exception.
        }
    };

    @Captor
    private ArgumentCaptor<List<ExampleRepository>> repositoriesArgumentCaptor;

    private RepositoryPage page;

    private ExamplesWizardModel model;

    @Before
    public void setup() {
        page = new RepositoryPage( view,
                                   translator,
                                   examplesServiceCaller,
                                   pageStatusChangedEvent ) {
            @Override
            boolean isUrlValid( final String url ) {
                try {
                    //The Presenter uses GWT's URL utilities not available in regular Mockito tests
                    new URL( url );

                } catch ( MalformedURLException mfe ) {
                    return false;
                }
                return true;
            }
        };

        model = new ExamplesWizardModel();
        page.setModel( model );
    }

    @Test
    public void testInit() {
        page.init();
        verify( view,
                times( 1 ) ).init( eq( page ) );
        verify( view,
                times( 1 ) ).setPlaceHolder( any( String.class ) );
    }

    @Test
    public void testInitialise() {
        page.initialise();
        verify( view,
                times( 1 ) ).initialise();
    }

    @Test
    public void testAsWidget() {
        page.asWidget();
        verify( view,
                times( 1 ) ).asWidget();
    }

    @Test
    public void testSetRepositories_Null() {
        page.setRepositories( null );
        verify( view,
                times( 1 ) ).setRepositories( eq( Collections.<ExampleRepository>emptyList() ) );
    }

    @Test
    public void testSetRepositories_Empty() {
        page.setRepositories( Collections.<ExampleRepository>emptySet() );
        verify( view,
                times( 1 ) ).setRepositories( eq( Collections.<ExampleRepository>emptyList() ) );
    }

    @Test
    public void testSetRepositories() {
        final Set<ExampleRepository> repositories = new HashSet<ExampleRepository>() {{
            add( new ExampleRepository( "b" ) );
            add( new ExampleRepository( "a" ) );
        }};
        page.setRepositories( repositories );
        verify( view,
                times( 1 ) ).setRepositories( repositoriesArgumentCaptor.capture() );

        final List<ExampleRepository> sortedRepositories = repositoriesArgumentCaptor.getValue();
        assertNotNull( sortedRepositories );
        assertEquals( 2,
                      sortedRepositories.size() );
        assertEquals( "a",
                      sortedRepositories.get( 0 ).getUrl() );
        assertEquals( "b",
                      sortedRepositories.get( 1 ).getUrl() );
    }

    @Test
    public void testSetSelectedRepository() {
        page.setSelectedRepository( new ExampleRepository( "" ) );
        verify( pageStatusChangedEvent,
                times( 1 ) ).fire( any( WizardPageStatusChangeEvent.class ) );
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testIsComplete_NullRepository() {
        final Callback<Boolean> callback = mock( Callback.class );
        page.isComplete( callback );

        verify( callback,
                times( 1 ) ).callback( eq( false ) );
        verify( view,
                times( 1 ) ).setUrlGroupType( eq( ValidationState.ERROR ) );
        verify( view,
                times( 1 ) ).showUrlHelpMessage( any( String.class ) );
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testIsComplete_EmptyRepositoryUrl() {
        final ExampleRepository repository = new ExampleRepository( "" );
        model.setSelectedRepository( repository );
        final Callback<Boolean> callback = mock( Callback.class );
        page.isComplete( callback );

        verify( callback,
                times( 1 ) ).callback( eq( false ) );
        verify( view,
                times( 1 ) ).setUrlGroupType( eq( ValidationState.ERROR ) );
        verify( view,
                times( 1 ) ).showUrlHelpMessage( any( String.class ) );

        assertFalse( repository.isUrlValid() );
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testIsComplete_InvalidRepositoryUrl() {
        final ExampleRepository repository = new ExampleRepository( "cheese" );
        model.setSelectedRepository( repository );
        final Callback<Boolean> callback = mock( Callback.class );
        page.isComplete( callback );

        verify( callback,
                times( 1 ) ).callback( eq( false ) );
        verify( view,
                times( 1 ) ).setUrlGroupType( eq( ValidationState.ERROR ) );
        verify( view,
                times( 1 ) ).showUrlHelpMessage( any( String.class ) );

        assertFalse( repository.isUrlValid() );
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testIsComplete_ValidRepositoryUrl() {
        final ExampleRepository repository = new ExampleRepository( EXAMPLE_REPOSITORY );
        model.setSelectedRepository( repository );
        final Callback<Boolean> callback = mock( Callback.class );
        page.isComplete( callback );

        verify( callback,
                times( 1 ) ).callback( eq( true ) );
        verify( view,
                times( 1 ) ).setUrlGroupType( eq( ValidationState.NONE ) );
        verify( view,
                times( 1 ) ).hideUrlHelpMessage();

        assertTrue( repository.isUrlValid() );
    }

}
