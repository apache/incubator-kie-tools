/*
 * Copyright 2014 JBoss Inc
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

package org.kie.workbench.common.screens.projecteditor.client.wizard;

import javax.enterprise.event.Event;

import org.guvnor.common.services.project.client.ArtifactIdChangeHandler;
import org.guvnor.common.services.project.client.GAVEditor;
import org.guvnor.common.services.project.client.GAVEditorView;
import org.guvnor.common.services.project.client.POMEditorPanel;
import org.guvnor.common.services.project.client.POMEditorPanelView;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.screens.projecteditor.service.ProjectScreenService;
import org.kie.workbench.common.services.shared.validation.ValidationService;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mocks.CallerMock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class GAVWizardPageTest {

    private POMEditorPanelView pomEditorView;
    private POMEditorPanel pomEditor;

    private GAVEditor gavEditor;
    private GAVEditorView gavEditorView;

    private PlaceManager placeManager;
    private GAVWizardPage page;
    private GAVWizardPageView view;

    private ProjectScreenService projectScreenService;
    private ValidationService validationService;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        projectScreenService = mock( ProjectScreenService.class );
        validationService = mock( ValidationService.class );

        gavEditorView = mock( GAVEditorView.class );
        gavEditor = new GAVEditor( gavEditorView );

        placeManager = mock( PlaceManager.class );
        pomEditorView = mock( POMEditorPanelView.class );
        pomEditor = spy( new POMEditorPanel( pomEditorView,
                                             placeManager ) );

        //POMEditorView implementation updates a nested GAVEditor presenter. Mock the implementation to avoid use of real widgets
        doAnswer( new Answer<Void>() {
            @Override
            public Void answer( final InvocationOnMock invocation ) throws Throwable {
                final String artifactId = (String) invocation.getArguments()[ 0 ];
                gavEditor.setArtifactID( artifactId );
                return null;
            }
        } ).when( pomEditorView ).setArtifactID( any( String.class ) );

        //POMEditorView implementation updates a nested GAVEditor presenter. Mock the implementation to avoid use of real widgets
        doAnswer( new Answer<Void>() {
            @Override
            public Void answer( final InvocationOnMock invocation ) throws Throwable {
                final GAV gav = (GAV) invocation.getArguments()[ 0 ];
                gavEditor.setGAV( gav );
                return null;
            }
        } ).when( pomEditorView ).setGAV( any( GAV.class ) );

        //POMEditorView implementation updates a nested GAVEditor presenter. Mock the implementation to avoid use of real widgets
        doAnswer( new Answer<Void>() {
            @Override
            public Void answer( final InvocationOnMock invocation ) throws Throwable {
                final ArtifactIdChangeHandler handler = (ArtifactIdChangeHandler) invocation.getArguments()[ 0 ];
                gavEditor.addArtifactIdChangeHandler( handler );
                return null;
            }
        } ).when( pomEditorView ).addArtifactIdChangeHandler( any( ArtifactIdChangeHandler.class ) );

        view = mock( GAVWizardPageView.class );
        page = spy( new GAVWizardPage( pomEditor,
                                       view,
                                       mock( Event.class ),
                                       new CallerMock<ProjectScreenService>( projectScreenService ),
                                       new CallerMock<ValidationService>( validationService ) ) );
        page.initialise();
    }

    @Test
    public void testInvalidPOMWithParent() throws Exception {
        when( projectScreenService.validateGroupId( any( String.class ) ) ).thenReturn( false );
        when( projectScreenService.validateArtifactId( any( String.class ) ) ).thenReturn( false );
        when( projectScreenService.validateVersion( any( String.class ) ) ).thenReturn( false );
        when( validationService.isProjectNameValid( any( String.class ) ) ).thenReturn( false );
        page.setPom( new POM(),
                     true );

        verify( page,
                times( 1 ) ).validateName( any( String.class ) );
        verify( page,
                never() ).validateGroupId( any( String.class ) );
        verify( page,
                times( 1 ) ).validateArtifactId( any( String.class ) );
        verify( page,
                never() ).validateVersion( any( String.class ) );

        verify( pomEditor,
                times( 1 ) ).setValidName( eq( false ) );
        verify( pomEditor,
                never() ).setValidGroupID( eq( false ) );
        verify( pomEditor,
                times( 1 ) ).setValidArtifactID( eq( false ) );
        verify( pomEditor,
                never() ).setValidVersion( eq( false ) );
    }

    @Test
    public void testInvalidPOMWithoutParent() throws Exception {
        when( projectScreenService.validateGroupId( any( String.class ) ) ).thenReturn( false );
        when( projectScreenService.validateArtifactId( any( String.class ) ) ).thenReturn( false );
        when( projectScreenService.validateVersion( any( String.class ) ) ).thenReturn( false );
        when( validationService.isProjectNameValid( any( String.class ) ) ).thenReturn( false );
        page.setPom( new POM(),
                     false );

        verify( page,
                times( 1 ) ).validateName( any( String.class ) );
        verify( page,
                times( 1 ) ).validateGroupId( any( String.class ) );
        verify( page,
                times( 1 ) ).validateArtifactId( any( String.class ) );
        verify( page,
                times( 1 ) ).validateVersion( any( String.class ) );

        verify( pomEditor,
                times( 1 ) ).setValidName( eq( false ) );
        verify( pomEditor,
                times( 1 ) ).setValidGroupID( eq( false ) );
        verify( pomEditor,
                times( 1 ) ).setValidArtifactID( eq( false ) );
        verify( pomEditor,
                times( 1 ) ).setValidVersion( eq( false ) );
    }

    @Test
    public void testValidPOMWithParent() throws Exception {
        when( projectScreenService.validateGroupId( any( String.class ) ) ).thenReturn( true );
        when( projectScreenService.validateArtifactId( any( String.class ) ) ).thenReturn( true );
        when( projectScreenService.validateVersion( any( String.class ) ) ).thenReturn( true );
        when( validationService.isProjectNameValid( any( String.class ) ) ).thenReturn( true );
        page.setPom( new POM(),
                     true );

        verify( page,
                times( 1 ) ).validateName( any( String.class ) );
        verify( page,
                never() ).validateGroupId( any( String.class ) );
        verify( page,
                times( 1 ) ).validateArtifactId( any( String.class ) );
        verify( page,
                never() ).validateVersion( any( String.class ) );

        verify( pomEditor,
                times( 1 ) ).setValidName( eq( true ) );
        verify( pomEditor,
                never() ).setValidGroupID( eq( true ) );
        verify( pomEditor,
                times( 1 ) ).setValidArtifactID( eq( true ) );
        verify( pomEditor,
                never() ).setValidVersion( eq( true ) );
    }

    @Test
    public void testValidPOMWithoutParent() throws Exception {
        when( projectScreenService.validateGroupId( any( String.class ) ) ).thenReturn( true );
        when( projectScreenService.validateArtifactId( any( String.class ) ) ).thenReturn( true );
        when( projectScreenService.validateVersion( any( String.class ) ) ).thenReturn( true );
        when( validationService.isProjectNameValid( any( String.class ) ) ).thenReturn( true );
        page.setPom( new POM(),
                     false );

        verify( page,
                times( 1 ) ).validateName( any( String.class ) );
        verify( page,
                times( 1 ) ).validateGroupId( any( String.class ) );
        verify( page,
                times( 1 ) ).validateArtifactId( any( String.class ) );
        verify( page,
                times( 1 ) ).validateVersion( any( String.class ) );

        verify( pomEditor,
                times( 1 ) ).setValidName( eq( true ) );
        verify( pomEditor,
                times( 1 ) ).setValidGroupID( eq( true ) );
        verify( pomEditor,
                times( 1 ) ).setValidArtifactID( eq( true ) );
        verify( pomEditor,
                times( 1 ) ).setValidVersion( eq( true ) );
    }

    @Test
    public void testPomsWithParentDataDisableFieldsParentNotSet() throws Exception {
        page.setPom( new POM(), false );

        verify( pomEditor, never() ).disableGroupID( anyString() );
        verify( pomEditor, never() ).disableVersion( anyString() );
    }

    @Test
    public void testPomsWithParentDataDisableFieldsParentSet() throws Exception {
        when( view.InheritedFromAParentPOM() ).thenReturn( "InheritedFromAParentPOM" );
        POM pom = new POM();
        pom.getGav().setGroupId( "supergroup" );
        page.setPom( pom, true );

        verify( pomEditor ).disableGroupID( "InheritedFromAParentPOM" );
        verify( pomEditor ).disableVersion( "InheritedFromAParentPOM" );
    }

    @Test
    public void testSetNameValidArtifactID() {
        when( projectScreenService.validateGroupId( any( String.class ) ) ).thenReturn( true );
        when( projectScreenService.validateArtifactId( any( String.class ) ) ).thenReturn( true );
        when( projectScreenService.validateVersion( any( String.class ) ) ).thenReturn( true );
        when( validationService.isProjectNameValid( any( String.class ) ) ).thenReturn( true );

        //POMEditorView implementation updates a nested GAVEditor presenter. Mock the implementation to avoid use of real widgets
        doAnswer( new Answer<Void>() {
            @Override
            public Void answer( final InvocationOnMock invocation ) throws Throwable {
                final String artifactId = (String) invocation.getArguments()[ 0 ];
                gavEditor.setArtifactID( artifactId );
                return null;
            }
        } ).when( pomEditorView ).setArtifactID( any( String.class ) );

        //POMEditorView implementation updates a nested GAVEditor presenter. Mock the implementation to avoid use of real widgets
        doAnswer( new Answer<Void>() {
            @Override
            public Void answer( final InvocationOnMock invocation ) throws Throwable {
                final GAV gav = (GAV) invocation.getArguments()[ 0 ];
                gavEditor.setGAV( gav );
                return null;
            }
        } ).when( pomEditorView ).setGAV( any( GAV.class ) );

        final POM pom = new POM();
        page.setPom( pom,
                     false );
        verify( validationService,
                times( 1 ) ).isProjectNameValid( null );
        verify( projectScreenService,
                times( 1 ) ).validateArtifactId( null );

        pomEditor.onNameChange( "project-name" );

        verify( pomEditorView,
                times( 1 ) ).setArtifactID( eq( "project-name" ) );
        verify( validationService,
                times( 1 ) ).isProjectNameValid( eq( "project-name" ) );
        verify( projectScreenService,
                times( 1 ) ).validateArtifactId( eq( "project-name" ) );

        assertEquals( "project-name",
                      pom.getName() );
        assertEquals( "project-name",
                      pom.getGav().getArtifactId() );
    }

    @Test
    public void testSetNameInvalidArtifactID() {
        when( projectScreenService.validateGroupId( any( String.class ) ) ).thenReturn( true );
        when( projectScreenService.validateArtifactId( any( String.class ) ) ).thenReturn( true );
        when( projectScreenService.validateVersion( any( String.class ) ) ).thenReturn( true );
        when( validationService.isProjectNameValid( any( String.class ) ) ).thenReturn( true );

        final POM pom = new POM();
        page.setPom( pom,
                     false );
        verify( validationService,
                times( 1 ) ).isProjectNameValid( null );
        verify( projectScreenService,
                times( 1 ) ).validateArtifactId( null );

        pomEditor.onNameChange( "project-name!" );

        verify( pomEditorView,
                times( 1 ) ).setArtifactID( eq( "project-name" ) );
        verify( validationService,
                times( 1 ) ).isProjectNameValid( eq( "project-name!" ) );
        verify( projectScreenService,
                times( 1 ) ).validateArtifactId( eq( "project-name" ) );

        assertEquals( "project-name!",
                      pom.getName() );
        assertEquals( "project-name",
                      pom.getGav().getArtifactId() );
    }

    @Test
    public void testSetNameValidArtifactIDUserChanged() {
        when( projectScreenService.validateGroupId( any( String.class ) ) ).thenReturn( true );
        when( projectScreenService.validateArtifactId( any( String.class ) ) ).thenReturn( true );
        when( projectScreenService.validateVersion( any( String.class ) ) ).thenReturn( true );
        when( validationService.isProjectNameValid( any( String.class ) ) ).thenReturn( true );

        final POM pom = new POM();
        page.setPom( pom,
                     false );
        verify( validationService,
                times( 1 ) ).isProjectNameValid( null );
        verify( projectScreenService,
                times( 1 ) ).validateArtifactId( null );

        gavEditor.onArtifactIdChange( "artifactId" );
        pomEditor.onNameChange( "project-name" );

        verify( pomEditorView,
                never() ).setArtifactID( eq( "project-name" ) );
        verify( validationService,
                times( 1 ) ).isProjectNameValid( eq( "project-name" ) );
        verify( projectScreenService,
                times( 1 ) ).validateArtifactId( eq( "artifactId" ) );

        assertEquals( "project-name",
                      pom.getName() );
        assertEquals( "artifactId",
                      pom.getGav().getArtifactId() );
    }

    @Test
    public void testSetNameValidArtifactIDUserChangedThenRevert() {
        when( projectScreenService.validateGroupId( any( String.class ) ) ).thenReturn( true );
        when( projectScreenService.validateArtifactId( any( String.class ) ) ).thenReturn( true );
        when( projectScreenService.validateVersion( any( String.class ) ) ).thenReturn( true );
        when( validationService.isProjectNameValid( any( String.class ) ) ).thenReturn( true );

        final POM pom = new POM();
        page.setPom( pom,
                     false );
        verify( validationService,
                times( 1 ) ).isProjectNameValid( null );
        verify( projectScreenService,
                times( 1 ) ).validateArtifactId( null );

        //Simulate the User change the Artifact ID manually
        gavEditor.onArtifactIdChange( "artifactId" );
        pomEditor.onNameChange( "project-name" );

        verify( pomEditorView,
                never() ).setArtifactID( eq( "project-name" ) );
        verify( validationService,
                times( 1 ) ).isProjectNameValid( eq( "project-name" ) );
        verify( projectScreenService,
                times( 1 ) ).validateArtifactId( eq( "artifactId" ) );

        assertEquals( "project-name",
                      pom.getName() );
        assertEquals( "artifactId",
                      pom.getGav().getArtifactId() );

        //Revert change to Artifact ID
        gavEditor.onArtifactIdChange( "" );
        pomEditor.onNameChange( "project-name" );

        verify( pomEditorView,
                times( 1 ) ).setArtifactID( eq( "project-name" ) );
        verify( validationService,
                times( 2 ) ).isProjectNameValid( eq( "project-name" ) );
        verify( projectScreenService,
                times( 1 ) ).validateArtifactId( eq( "artifactId" ) );

        assertEquals( "project-name",
                      pom.getName() );
        assertEquals( "project-name",
                      pom.getGav().getArtifactId() );

    }

}
