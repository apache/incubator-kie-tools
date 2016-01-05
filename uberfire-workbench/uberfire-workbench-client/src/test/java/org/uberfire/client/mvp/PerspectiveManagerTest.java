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

package org.uberfire.client.mvp;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import javax.enterprise.event.Event;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.WorkbenchServicesProxy;
import org.uberfire.client.workbench.events.PerspectiveChange;
import org.uberfire.client.workbench.panels.impl.MultiListWorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.impl.SimpleWorkbenchPanelPresenter;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.impl.PanelDefinitionImpl;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;

@RunWith(MockitoJUnitRunner.class)
public class PerspectiveManagerTest {

    @Mock PlaceManager placeManager;
    @Mock PanelManager panelManager;
    @Mock ActivityManager activityManager;
    @Mock WorkbenchServicesProxy wbServices;
    @Mock Event<PerspectiveChange> perspectiveChangeEvent;

    @InjectMocks PerspectiveManagerImpl perspectiveManager;

    // useful mocks provided by setup method
    private PerspectiveDefinition ozDefinition;
    private PerspectiveActivity oz;
    private PlaceRequest pr;
    private ParameterizedCommand<PerspectiveDefinition> doWhenFinished;
    private Command doWhenFinishedSave;

    @SuppressWarnings("unchecked")
    @Before
    public void setup() {
        ozDefinition = new PerspectiveDefinitionImpl( MultiListWorkbenchPanelPresenter.class.getName() );

        oz = mock( PerspectiveActivity.class );
        pr = mock( PlaceRequest.class );
        when( oz.getDefaultPerspectiveLayout() ).thenReturn( ozDefinition );
        when( oz.getIdentifier() ).thenReturn( "oz_perspective" );
        when( oz.isTransient() ).thenReturn( true );

        doWhenFinished = mock( ParameterizedCommand.class );
        doWhenFinishedSave = mock( Command.class );

        // simulate "finished saving" callback on wbServices.save()
        doAnswer( new Answer<Void>() {
            @Override
            public Void answer( InvocationOnMock invocation ) throws Throwable {
                Command callback = (Command) invocation.getArguments()[2];
                callback.execute();
                return null;
            }
        } ).when( wbServices ).save( any( String.class), any( PerspectiveDefinition.class ), any( Command.class ) );

        // simulate "no saved state found" result on wbServices.loadPerspective()
        doAnswer( new Answer<Void>() {
            @Override
            public Void answer( InvocationOnMock invocation ) throws Throwable {
                ParameterizedCommand<?> callback = (ParameterizedCommand<?>) invocation.getArguments()[1];
                callback.execute( null );
                return null;
            }
        } ).when( wbServices ).loadPerspective( anyString(), any( ParameterizedCommand.class ) );

        // XXX should look at why PanelManager needs to return an alternative panel sometimes.
        // would be better if addWorkbenchPanel returned void.
        when( panelManager.addWorkbenchPanel( any( PanelDefinition.class ),
                                              any( PanelDefinition.class ),
                                              any( Position.class ) ) ).thenAnswer( new Answer<PanelDefinition>() {
                                                  @Override
                                                  public PanelDefinition answer( InvocationOnMock invocation ) {
                                                      return (PanelDefinition) invocation.getArguments()[1];
                                                  }
                                              } );
    }

    @Test
    public void shouldReportNullPerspectiveInitially() throws Exception {
        assertNull( perspectiveManager.getCurrentPerspective() );
    }

    @Test
    public void shouldReportNewPerspectiveAsCurrentAfterSwitching() throws Exception {
        perspectiveManager.switchToPerspective( pr, oz, doWhenFinished );

        assertSame( oz, perspectiveManager.getCurrentPerspective() );
    }

    @Test
    public void shouldSaveNonTransientPerspectives() throws Exception {
        PerspectiveDefinition kansasDefinition = new PerspectiveDefinitionImpl( MultiListWorkbenchPanelPresenter.class.getName() );

        PerspectiveActivity kansas = mock( PerspectiveActivity.class );
        when( kansas.getDefaultPerspectiveLayout() ).thenReturn( kansasDefinition );
        when( kansas.getIdentifier() ).thenReturn( "kansas_perspective" );
        when( kansas.isTransient() ).thenReturn( false );

        perspectiveManager.switchToPerspective( pr, kansas, doWhenFinished );
        perspectiveManager.savePerspectiveState( doWhenFinishedSave );

        verify( wbServices ).save( eq( "kansas_perspective" ), eq( kansasDefinition ), eq( doWhenFinishedSave ) );
    }

    @Test
    public void shouldNotSaveTransientPerspectives() throws Exception {
        PerspectiveDefinition kansasDefinition = new PerspectiveDefinitionImpl( MultiListWorkbenchPanelPresenter.class.getName() );

        PerspectiveActivity kansas = mock( PerspectiveActivity.class );
        when( kansas.getDefaultPerspectiveLayout() ).thenReturn( kansasDefinition );
        when( kansas.getIdentifier() ).thenReturn( "kansas_perspective" );
        when( kansas.isTransient() ).thenReturn( true );

        perspectiveManager.switchToPerspective( pr, kansas, doWhenFinished );
        perspectiveManager.savePerspectiveState( doWhenFinishedSave );

        verify( wbServices, never() ).save( any( String.class ), eq( kansasDefinition ), any( Command.class ) );
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldLoadNewNonTransientPerspectiveState() throws Exception {
        when( oz.isTransient() ).thenReturn( false );

        perspectiveManager.switchToPerspective( pr, oz, doWhenFinished );

        verify( wbServices ).loadPerspective( eq( "oz_perspective" ), any( ParameterizedCommand.class ) );
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldNotLoadNewTransientPerspectiveState() throws Exception {
        when( oz.isTransient() ).thenReturn( true );

        perspectiveManager.switchToPerspective( pr, oz, doWhenFinished );

        verify( wbServices, never() ).loadPerspective( eq( "oz_perspective" ), any( ParameterizedCommand.class ) );
    }

    @Test
    public void shouldExecuteCallbackWhenDoneLaunchingPerspective() throws Exception {
        perspectiveManager.switchToPerspective( pr, oz, doWhenFinished );

        verify( doWhenFinished ).execute( ozDefinition );
    }

    @Test
    public void shouldFireEventWhenLaunchingNewPerspective() throws Exception {
        perspectiveManager.switchToPerspective( pr, oz, doWhenFinished );

        verify( perspectiveChangeEvent ).fire( refEq( new PerspectiveChange( pr, ozDefinition, null, "oz_perspective" )) );
    }

    @Test
    public void shouldAddAllPanelsToPanelManager() throws Exception {
        PanelDefinition westPanel = new PanelDefinitionImpl( MultiListWorkbenchPanelPresenter.class.getName() );
        PanelDefinition eastPanel = new PanelDefinitionImpl( MultiListWorkbenchPanelPresenter.class.getName() );
        PanelDefinition northPanel = new PanelDefinitionImpl( MultiListWorkbenchPanelPresenter.class.getName() );
        PanelDefinition southPanel = new PanelDefinitionImpl( MultiListWorkbenchPanelPresenter.class.getName() );
        PanelDefinition southWestPanel = new PanelDefinitionImpl( MultiListWorkbenchPanelPresenter.class.getName() );

        ozDefinition.getRoot().appendChild( CompassPosition.WEST, westPanel );
        ozDefinition.getRoot().appendChild( CompassPosition.EAST, eastPanel );
        ozDefinition.getRoot().appendChild( CompassPosition.NORTH, northPanel );
        ozDefinition.getRoot().appendChild( CompassPosition.SOUTH, southPanel );
        southPanel.appendChild( CompassPosition.WEST, southWestPanel );

        // we assume this will be set correctly (verified elsewhere)
        when( panelManager.getRoot() ).thenReturn( ozDefinition.getRoot() );

        perspectiveManager.switchToPerspective( pr, oz, doWhenFinished );

        verify( panelManager ).addWorkbenchPanel( ozDefinition.getRoot(), westPanel, CompassPosition.WEST );
        verify( panelManager ).addWorkbenchPanel( ozDefinition.getRoot(), eastPanel, CompassPosition.EAST );
        verify( panelManager ).addWorkbenchPanel( ozDefinition.getRoot(), northPanel, CompassPosition.NORTH );
        verify( panelManager ).addWorkbenchPanel( ozDefinition.getRoot(), southPanel, CompassPosition.SOUTH );
        verify( panelManager ).addWorkbenchPanel( southPanel, southWestPanel, CompassPosition.WEST );
    }

    @Test
    public void shouldDestroyAllOldPanelsWhenSwitchingRoot() throws Exception {
        PerspectiveDefinition fooPerspectiveDef = new PerspectiveDefinitionImpl( MultiListWorkbenchPanelPresenter.class.getName() );
        PanelDefinition rootPanel = fooPerspectiveDef.getRoot();
        PanelDefinition fooPanel = new PanelDefinitionImpl( SimpleWorkbenchPanelPresenter.class.getName() );
        PanelDefinition fooChildPanel = new PanelDefinitionImpl( SimpleWorkbenchPanelPresenter.class.getName() );
        PanelDefinition barPanel = new PanelDefinitionImpl( SimpleWorkbenchPanelPresenter.class.getName() );
        PanelDefinition bazPanel = new PanelDefinitionImpl( SimpleWorkbenchPanelPresenter.class.getName() );

        rootPanel.appendChild( fooPanel );
        rootPanel.appendChild( barPanel );
        rootPanel.appendChild( bazPanel );

        fooPanel.appendChild( fooChildPanel );

        PerspectiveActivity fooPerspective = mock( PerspectiveActivity.class );
        when( fooPerspective.getDefaultPerspectiveLayout() ).thenReturn( fooPerspectiveDef );
        when( fooPerspective.isTransient() ).thenReturn( true );

        perspectiveManager.switchToPerspective( pr, fooPerspective, doWhenFinished );
        perspectiveManager.switchToPerspective( pr, oz, doWhenFinished );

        verify( panelManager ).removeWorkbenchPanel( fooPanel );
        verify( panelManager ).removeWorkbenchPanel( fooChildPanel );
        verify( panelManager ).removeWorkbenchPanel( barPanel );
        verify( panelManager ).removeWorkbenchPanel( bazPanel );
        verify( panelManager, never() ).removeWorkbenchPanel( rootPanel );
    }

    @Test @Ignore // TODO this is PlaceManager's job now
    public void shouldLaunchPartsFoundInPanels() throws Exception {
        PartDefinitionImpl rootPart1 = new PartDefinitionImpl( new DefaultPlaceRequest( "rootPart1" ) );
        PartDefinitionImpl southPart1 = new PartDefinitionImpl( new DefaultPlaceRequest( "southPart1" ) );
        PartDefinitionImpl southPart2 = new PartDefinitionImpl( new DefaultPlaceRequest( "southPart2" ) );
        PartDefinitionImpl southWestPart1 = new PartDefinitionImpl( new DefaultPlaceRequest( "southWestPart1" ) );
        PartDefinitionImpl southWestPart2 = new PartDefinitionImpl( new DefaultPlaceRequest( "southWestPart2" ) );

        ozDefinition.getRoot().addPart( rootPart1 );

        PanelDefinition southPanel = new PanelDefinitionImpl( MultiListWorkbenchPanelPresenter.class.getName() );
        southPanel.addPart( southPart1 );
        southPanel.addPart( southPart2 );
        ozDefinition.getRoot().appendChild( CompassPosition.SOUTH, southPanel );

        PanelDefinition southWestPanel = new PanelDefinitionImpl( MultiListWorkbenchPanelPresenter.class.getName() );
        southWestPanel.addPart( southWestPart1 );
        southWestPanel.addPart( southWestPart2 );
        southPanel.appendChild( CompassPosition.WEST, southWestPanel );

        // we assume this will be set correctly (verified elsewhere)
        when( panelManager.getRoot() ).thenReturn( ozDefinition.getRoot() );

        perspectiveManager.switchToPerspective( pr, oz, doWhenFinished );

        InOrder inOrder = inOrder( placeManager );
        inOrder.verify( placeManager ).goTo( rootPart1, ozDefinition.getRoot() );
        inOrder.verify( placeManager ).goTo( southPart1, southPanel );
        inOrder.verify( placeManager ).goTo( southPart2, southPanel );
        inOrder.verify( placeManager ).goTo( southWestPart1, southWestPanel );
        inOrder.verify( placeManager ).goTo( southWestPart2, southWestPanel );
    }

}
