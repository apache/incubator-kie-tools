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

package org.kie.workbench.common.screens.examples.client.wizard.pages.organizationalunit;

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
import org.kie.workbench.common.screens.examples.model.ExampleOrganizationalUnit;
import org.kie.workbench.common.screens.examples.model.ExampleTargetRepository;
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
public class OUPageTest {

    private static final String EXAMPLE_REPOSITORY = "https://github.com/guvnorngtestuser1/guvnorng-playground.git";

    @Mock
    private OUPageView view;

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
    private ArgumentCaptor<List<ExampleOrganizationalUnit>> organizationalUnitArgumentCaptor;

    private OUPage page;

    private ExamplesWizardModel model;

    @Before
    public void setup() {
        page = new OUPage( view,
                           translator,
                           examplesServiceCaller,
                           pageStatusChangedEvent );

        model = new ExamplesWizardModel();
        page.setModel( model );
    }

    @Test
    public void testInit() {
        page.init();
        verify( view,
                times( 1 ) ).init( eq( page ) );
        verify( view,
                times( 1 ) ).setTargetRepositoryPlaceHolder( any( String.class ) );
        verify( view,
                times( 1 ) ).setOrganizationalUnitsPlaceHolder( any( String.class ) );
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
    public void testSetOrganizationalUnits_Null() {
        page.setOrganizationalUnits( null );
        verify( view,
                times( 1 ) ).setOrganizationalUnits( eq( Collections.<ExampleOrganizationalUnit>emptyList() ) );
    }

    @Test
    public void testSetOrganizationalUnits_Empty() {
        page.setOrganizationalUnits( Collections.<ExampleOrganizationalUnit>emptySet() );
        verify( view,
                times( 1 ) ).setOrganizationalUnits( eq( Collections.<ExampleOrganizationalUnit>emptyList() ) );
    }

    @Test
    public void testSetOrganizationalUnits() {
        final Set<ExampleOrganizationalUnit> organizationalUnits = new HashSet<ExampleOrganizationalUnit>() {{
            add( new ExampleOrganizationalUnit( "ou1" ) );
            add( new ExampleOrganizationalUnit( "ou2" ) );
        }};
        page.setOrganizationalUnits( organizationalUnits );
        verify( view,
                times( 1 ) ).setOrganizationalUnits( organizationalUnitArgumentCaptor.capture() );

        final List<ExampleOrganizationalUnit> sortedOrganizationalUnits = organizationalUnitArgumentCaptor.getValue();
        assertNotNull( sortedOrganizationalUnits );
        assertEquals( 2,
                      sortedOrganizationalUnits.size() );
        assertEquals( "ou1",
                      sortedOrganizationalUnits.get( 0 ).getName() );
        assertEquals( "ou2",
                      sortedOrganizationalUnits.get( 1 ).getName() );
    }

    @Test
    public void testSetTargetRepository() {
        page.setTargetRepository( new ExampleTargetRepository( "target" ) );
        verify( pageStatusChangedEvent,
                times( 1 ) ).fire( any( WizardPageStatusChangeEvent.class ) );
    }

    @Test
    public void testSetTargetOrganizationalUnit() {
        page.setTargetOrganizationalUnit( new ExampleOrganizationalUnit( "ou1" ) );
        verify( pageStatusChangedEvent,
                times( 1 ) ).fire( any( WizardPageStatusChangeEvent.class ) );
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testIsComplete_NullOU_NullRepository() {
        final Callback<Boolean> callback = mock( Callback.class );
        page.isComplete( callback );

        verify( callback,
                times( 1 ) ).callback( eq( false ) );
        verify( view,
                times( 1 ) ).setTargetRepositoryGroupType( eq( ValidationState.ERROR ) );
        verify( view,
                times( 1 ) ).showTargetRepositoryHelpMessage( any( String.class ) );
        verify( view,
                times( 1 ) ).setTargetOrganizationalUnitGroupType( eq( ValidationState.ERROR ) );
        verify( view,
                times( 1 ) ).showTargetOrganizationalUnitHelpMessage( any( String.class ) );
        verify( examplesService,
                never() ).validateRepositoryName( any( String.class ) );
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testIsComplete_EmptyOU_NullRepository() {
        model.setTargetOrganizationalUnit( new ExampleOrganizationalUnit( "" ) );
        final Callback<Boolean> callback = mock( Callback.class );
        page.isComplete( callback );

        verify( callback,
                times( 1 ) ).callback( eq( false ) );
        verify( view,
                times( 1 ) ).setTargetRepositoryGroupType( eq( ValidationState.ERROR ) );
        verify( view,
                times( 1 ) ).showTargetRepositoryHelpMessage( any( String.class ) );
        verify( view,
                times( 1 ) ).setTargetOrganizationalUnitGroupType( eq( ValidationState.ERROR ) );
        verify( view,
                times( 1 ) ).showTargetOrganizationalUnitHelpMessage( any( String.class ) );
        verify( examplesService,
                never() ).validateRepositoryName( any( String.class ) );
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testIsComplete_ValidOU_NullRepository() {
        model.setTargetOrganizationalUnit( new ExampleOrganizationalUnit( "demo" ) );
        final Callback<Boolean> callback = mock( Callback.class );
        page.isComplete( callback );

        verify( callback,
                times( 1 ) ).callback( eq( false ) );
        verify( view,
                times( 1 ) ).setTargetRepositoryGroupType( eq( ValidationState.ERROR ) );
        verify( view,
                times( 1 ) ).showTargetRepositoryHelpMessage( any( String.class ) );
        verify( view,
                times( 1 ) ).setTargetOrganizationalUnitGroupType( eq( ValidationState.NONE ) );
        verify( view,
                times( 1 ) ).hideTargetOrganizationalUnitHelpMessage();
        verify( examplesService,
                never() ).validateRepositoryName( any( String.class ) );
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testIsComplete_ValidOU_EmptyRepository() {
        model.setTargetRepository( new ExampleTargetRepository( "" ) );
        model.setTargetOrganizationalUnit( new ExampleOrganizationalUnit( "demo" ) );
        final Callback<Boolean> callback = mock( Callback.class );
        page.isComplete( callback );

        verify( callback,
                times( 1 ) ).callback( eq( false ) );
        verify( view,
                times( 1 ) ).setTargetRepositoryGroupType( eq( ValidationState.ERROR ) );
        verify( view,
                times( 1 ) ).showTargetRepositoryHelpMessage( any( String.class ) );
        verify( view,
                times( 1 ) ).setTargetOrganizationalUnitGroupType( eq( ValidationState.NONE ) );
        verify( view,
                times( 1 ) ).hideTargetOrganizationalUnitHelpMessage();
        verify( examplesService,
                never() ).validateRepositoryName( any( String.class ) );
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testIsComplete_ValidOU_InvalidRepository() {
        model.setTargetRepository( new ExampleTargetRepository( "%$£" ) );
        model.setTargetOrganizationalUnit( new ExampleOrganizationalUnit( "demo" ) );

        when( examplesService.validateRepositoryName( any( String.class ) ) ).thenReturn( false );

        final Callback<Boolean> callback = mock( Callback.class );
        page.isComplete( callback );

        verify( callback,
                times( 1 ) ).callback( eq( false ) );
        verify( view,
                times( 1 ) ).setTargetRepositoryGroupType( eq( ValidationState.ERROR ) );
        verify( view,
                times( 1 ) ).showTargetRepositoryHelpMessage( any( String.class ) );
        verify( view,
                times( 1 ) ).setTargetOrganizationalUnitGroupType( eq( ValidationState.NONE ) );
        verify( view,
                times( 1 ) ).hideTargetOrganizationalUnitHelpMessage();
        verify( examplesService,
                times( 1 ) ).validateRepositoryName( any( String.class ) );
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testIsComplete_ValidOU_ValidRepository() {
        model.setTargetRepository( new ExampleTargetRepository( "target" ) );
        model.setTargetOrganizationalUnit( new ExampleOrganizationalUnit( "demo" ) );

        when( examplesService.validateRepositoryName( any( String.class ) ) ).thenReturn( true );

        final Callback<Boolean> callback = mock( Callback.class );
        page.isComplete( callback );

        verify( callback,
                times( 1 ) ).callback( eq( true ) );
        verify( view,
                times( 1 ) ).setTargetRepositoryGroupType( eq( ValidationState.NONE ) );
        verify( view,
                times( 1 ) ).hideTargetRepositoryHelpMessage();
        verify( view,
                times( 1 ) ).setTargetOrganizationalUnitGroupType( eq( ValidationState.NONE ) );
        verify( view,
                times( 1 ) ).hideTargetOrganizationalUnitHelpMessage();
        verify( examplesService,
                times( 1 ) ).validateRepositoryName( any( String.class ) );
    }

}
