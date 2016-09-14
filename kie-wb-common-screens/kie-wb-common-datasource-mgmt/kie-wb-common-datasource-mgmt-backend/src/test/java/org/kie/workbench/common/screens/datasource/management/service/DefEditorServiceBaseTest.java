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

package org.kie.workbench.common.screens.datasource.management.service;

import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.screens.datasource.management.backend.core.DataSourceRuntimeManager;
import org.kie.workbench.common.screens.datasource.management.backend.service.AbstractDefEditorService;
import org.kie.workbench.common.screens.datasource.management.backend.service.DataSourceServicesHelper;
import org.kie.workbench.common.screens.datasource.management.model.DataSourceDeploymentInfo;
import org.kie.workbench.common.screens.datasource.management.model.Def;
import org.kie.workbench.common.screens.datasource.management.model.DefEditorContent;
import org.kie.workbench.common.screens.datasource.management.model.DriverDeploymentInfo;
import org.kie.workbench.common.screens.datasource.management.util.MavenArtifactResolver;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.mockito.Mock;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.service.RenameService;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public abstract class DefEditorServiceBaseTest {

    protected static final String SESSION_ID = "sessionId";

    protected static final String IDENTITY = "userId";

    protected static final String GLOBAL_URI = "default://master@datasources/";

    protected static final String PROJECT_URI = "default://master@TestRepo/project/src/resources/META-INF";

    protected static final String FILE_URI = "default://master@TestRepo/project/src/resources/META-INF/someFile.txt";

    protected static final String COMMENT = "Some comment";

    @Mock
    protected DataSourceRuntimeManager runtimeManager;

    @Mock
    protected DataSourceServicesHelper serviceHelper;

    @Mock
    protected IOService ioService;

    @Mock
    protected KieProjectService projectService;

    @Mock
    protected CommentedOptionFactory optionsFactory;

    @Mock
    protected RenameService renameService;

    @Mock
    protected MavenArtifactResolver artifactResolver;

    @Mock
    protected Path path;

    @Mock
    protected KieProject project;

    protected AbstractDefEditorService editorService;

    @Mock
    protected DataSourceDeploymentInfo dataSourceDeploymentInfo;

    @Mock
    protected DriverDeploymentInfo driverDeploymentInfo;

    @Before
    public void setup() {
        when ( optionsFactory.getSafeSessionId() ).thenReturn( SESSION_ID );
        when( optionsFactory.getSafeIdentityName() ).thenReturn( IDENTITY );
    }

    protected abstract DefEditorContent getExpectedContent();

    protected abstract String getExpectedDefString();

    protected abstract String getExpectedFileName();

    protected abstract Def getExpectedDef();

    protected abstract Def getOriginalDef();

    protected abstract String getOriginalDefString();

    @Test
    public void testLoadContent() {

        when( path.toURI() ).thenReturn( FILE_URI );
        org.uberfire.java.nio.file.Path nioPath = Paths.convert( path );

        String source = getExpectedDefString();
        when( ioService.readAllString( nioPath ) ).thenReturn( source );
        when( projectService.resolveProject( path ) ).thenReturn( project );

        DefEditorContent result = editorService.loadContent( path );

        //The returned content should be the expected.
        assertEquals( getExpectedContent(), result );
    }

    @Test
    public void testCreateInProject() {
        testCreate( false );
    }

    @Test
    public void testCreateGlobal() {
        testCreate( true );
    }

    private void testCreate( boolean global ) {

        if ( global ) {
            when( path.toURI() ).thenReturn( GLOBAL_URI );
        } else {
            when( path.toURI() ).thenReturn( PROJECT_URI );
            when( serviceHelper.getProjectDataSourcesContext( project ) ).thenReturn( path );
        }

        //expected target path
        org.uberfire.java.nio.file.Path targetNioPath = Paths.convert( path ).resolve( getExpectedFileName() );
        //expected source
        String source = getExpectedDefString();

        when ( serviceHelper.getGlobalDataSourcesContext() ).thenReturn( path );
        when ( ioService.exists( targetNioPath ) ).thenReturn( false );

        if ( global ) {
            editorService.createGlobal( getExpectedDef() );
        } else {
            editorService.create( getExpectedDef(), project );
        }

        //we wants that:
        // 1) the expected file was saved.
        verify( ioService, times( 1 ) ).write( eq( targetNioPath ), eq( source ), any( CommentedOption.class) );
        //2) the definition was deployed, and 3) the notification was fired.
        verifyCreateConditions( global );
    }

    protected abstract void verifyCreateConditions( boolean global );

    @Test
    public void testSave() {
        testSave( false );
    }

    @Test
    public void testSaveWithNameModified() {
        testSave( true );
    }

    protected void testSave( boolean nameModified ) {

        Def originalDef = getExpectedDef();
        String originalSource = getOriginalDefString();

        if ( !nameModified ) {
            originalDef.setName( "dataSourceName" );
        }

        //expected target path
        when( path.toURI() ).thenReturn( FILE_URI );
        org.uberfire.java.nio.file.Path targetNioPath = Paths.convert( path );

        when ( ioService.readAllString( targetNioPath ) ).thenReturn( originalSource );
        when ( projectService.resolveProject( path ) ).thenReturn( project );

        editorService.save( path, getExpectedContent(), COMMENT );

        //we wants that:

        //The expected file was saved.
        //1) the expected file was saved
        verify( optionsFactory, times( 1 ) ).makeCommentedOption( COMMENT );
        verify( ioService, times( 1 ) ).write( eq( targetNioPath ), eq( getExpectedDefString() ), any( CommentedOption.class ) );

        //2) the file was renamed if the name changed.
        if ( nameModified ) {
            verify( renameService, times( 1 ) ).rename( path, getExpectedDef().getName(), COMMENT );
        }

        //3) the definition was deployed and 4) the notification was fired.
        verifySaveConditions( );
    }

    protected abstract void verifySaveConditions();

    @Test
    public void testDelete() throws Exception {
        //current file
        String content = getExpectedDefString();
        when( path.toURI() ).thenReturn( FILE_URI );
        org.uberfire.java.nio.file.Path nioPath = Paths.convert( path );
        when( ioService.readAllString( nioPath ) ).thenReturn( content );
        when( ioService.exists( nioPath ) ).thenReturn( true );

        when( projectService.resolveProject( path ) ).thenReturn( project );

        when( runtimeManager.getDataSourceDeploymentInfo(
                getExpectedDef().getUuid() ) ).thenReturn( dataSourceDeploymentInfo );
        when( runtimeManager.getDriverDeploymentInfo(
                getExpectedDef().getUuid() ) ).thenReturn( driverDeploymentInfo );

        editorService.delete( path, COMMENT );

        //we wants that:
        //1) the file was deleted
        //2) the definition was un-deployed, and 3) the delete notification was fired.
        verifyDeleteConditions();
    }

    protected abstract void verifyDeleteConditions();

}