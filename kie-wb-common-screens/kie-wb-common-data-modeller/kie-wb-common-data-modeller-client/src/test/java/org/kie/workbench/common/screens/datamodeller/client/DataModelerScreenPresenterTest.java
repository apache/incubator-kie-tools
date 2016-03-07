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

package org.kie.workbench.common.screens.datamodeller.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.shared.message.Level;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.guvnor.common.services.shared.security.KieWorkbenchACL;
import org.guvnor.messageconsole.events.PublishBatchMessagesEvent;
import org.guvnor.messageconsole.events.UnpublishMessagesEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.definition.type.Description;
import org.kie.api.definition.type.Label;
import org.kie.workbench.common.screens.datamodeller.client.context.DataModelerWorkbenchContext;
import org.kie.workbench.common.screens.datamodeller.client.context.DataModelerWorkbenchFocusEvent;
import org.kie.workbench.common.screens.datamodeller.client.validation.JavaFileNameValidator;
import org.kie.workbench.common.screens.datamodeller.client.validation.ValidatorService;
import org.kie.workbench.common.screens.datamodeller.client.widgets.DataModelerEditorsTestHelper;
import org.kie.workbench.common.screens.datamodeller.events.DataModelerEvent;
import org.kie.workbench.common.screens.datamodeller.model.DataModelerError;
import org.kie.workbench.common.screens.datamodeller.model.EditorModelContent;
import org.kie.workbench.common.screens.datamodeller.service.DataModelerService;
import org.kie.workbench.common.screens.javaeditor.client.type.JavaResourceType;
import org.kie.workbench.common.screens.javaeditor.client.widget.EditJavaSourceWidget;
import org.kie.workbench.common.services.datamodeller.core.AnnotationDefinition;
import org.kie.workbench.common.services.datamodeller.core.DataModel;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.PropertyType;
import org.kie.workbench.common.services.datamodeller.core.impl.PropertyTypeFactoryImpl;
import org.kie.workbench.common.services.datamodeller.util.DriverUtils;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.widgets.metadata.client.KieEditorWrapperView;
import org.kie.workbench.common.widgets.metadata.client.widget.OverviewWidgetPresenter;
import org.mockito.Mock;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.mvp.LockRequiredEvent;
import org.uberfire.ext.editor.commons.client.history.VersionRecordManager;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.rpc.SessionInfo;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class)
public class DataModelerScreenPresenterTest {

    @GwtMock
    DataModelerScreenPresenter.DataModelerScreenView view;

    @Mock
    SessionInfo sessionInfo;

    @Mock
    EditJavaSourceWidget javaSourceEditor;

    @Mock
    EventSourceMock<DataModelerEvent> dataModelerEvent;

    @Mock
    EventSourceMock<UnpublishMessagesEvent> unpublishMessagesEvent;

    @Mock
    EventSourceMock<PublishBatchMessagesEvent> publishBatchMessagesEvent;

    @Mock
    EventSourceMock<LockRequiredEvent> lockRequired;

    @Mock
    EventSourceMock<DataModelerWorkbenchFocusEvent> dataModelerFocusEvent;

    @Mock
    KieWorkbenchACL kieACL;

    @Mock
    DataModelerService modelerService;

    @Mock
    ValidatorService validatorService;

    @Mock
    JavaFileNameValidator javaFileNameValidator;

    JavaResourceType resourceType;

    @Mock
    DataModelerWorkbenchContext dataModelerWBContext;

    @Mock
    ObservablePath path;

    @GwtMock
    VersionRecordManager versionRecordManager;

    @Mock
    PlaceRequest placeRequest;

    DataModelerScreenPresenter presenter;

    /**
     * Emulates the overview returned from server.
     */
    @Mock
    Overview overview;

    /**
     * Emulates the project returned from server.
     */
    @Mock
    KieProject kieProject;

    /**
     * Emulates the data object returned from server.
     */
    DataObject testObject1;

    /**
     * Emulates the model returned from server.
     */
    DataModel testModel;

    /**
     * Emulates the source returned from server.
     */
    String testSource = "public class Dummy {}";

    /**
     * Emulates the list for parse errors returned from server when a java filed couldn't be parsed.
     */
    List<DataModelerError> testErrors;

    /**
     * Emulates the packages definition returned form server.
     */
    Set<String> testPackages;

    /**
     * Emulates the annotation definitions returned from server.
     */
    Map<String, AnnotationDefinition> testAnnotationDefs;

    /**
     * Emulates the property types definitions returned from server.
     */
    List<PropertyType> testTypeDefs;

    @Before
    public void setUp() throws Exception {

        testObject1 = DataModelerEditorsTestHelper.createTestObject1();
        testModel = DataModelerEditorsTestHelper.createTestModel( testObject1 );
        testErrors = createTestErrors();
        testPackages = createTestPackages();
        testAnnotationDefs = createTestAnnotations();
        testTypeDefs = createTestPropertyTypes();

        presenter = new DataModelerScreenPresenter( view, sessionInfo ) {

            {
                kieView = mock( KieEditorWrapperView.class );
                this.versionRecordManager = DataModelerScreenPresenterTest.this.versionRecordManager;
                overviewWidget = mock( OverviewWidgetPresenter.class );

                javaSourceEditor = DataModelerScreenPresenterTest.this.javaSourceEditor;
                dataModelerEvent = DataModelerScreenPresenterTest.this.dataModelerEvent;
                unpublishMessagesEvent = DataModelerScreenPresenterTest.this.unpublishMessagesEvent;
                publishBatchMessagesEvent = DataModelerScreenPresenterTest.this.publishBatchMessagesEvent;
                lockRequired = DataModelerScreenPresenterTest.this.lockRequired;
                dataModelerFocusEvent = DataModelerScreenPresenterTest.this.dataModelerFocusEvent;
                kieACL = DataModelerScreenPresenterTest.this.kieACL;
                modelerService = new CallerMock<DataModelerService>(
                        DataModelerScreenPresenterTest.this.modelerService );
                validatorService = DataModelerScreenPresenterTest.this.validatorService;
                javaFileNameValidator = DataModelerScreenPresenterTest.this.javaFileNameValidator;
                resourceType = DataModelerScreenPresenterTest.this.resourceType;
                dataModelerWBContext = DataModelerScreenPresenterTest.this.dataModelerWBContext;
                uiStarted = true;
            }

            protected void makeMenuBar() {

            }

            @Override
            protected void selectEditorTab() {
                //emulates the ui action produced by the tabs events.
                onEditTabSelected();
            }

            @Override
            protected void selectOverviewTab() {
                //emulates the ui action produced by the tabs events.
                onOverviewSelected();
            }

            @Override
            public void setSelectedTab( int index ) {
                //emulates the ui action produced by the tabs events.
                switch ( index ) {
                    case 0:
                        onEditTabSelected();
                        break;
                    case 1:
                        onOverviewSelected();
                        break;
                    case 2:
                        onSourceTabSelected();
                        break;
                    default:
                        throw new RuntimeException( "Tab index out of bounds: " + index );
                }
            }
        };
    }

    /**
     * This test emulates the loading of a java file that was parsed without errors.
     * Additionally it supposes that the DataModelerWBContext has not yet the information about property types
     * and annotations definitions, and thus should also check that this information was also loaded from server and
     * properly initialized.
     */
    @Test
    public void loadFileSuccessfulWithTypesInfoTest() {
        loadFileSuccessfulTest( true );
    }

    /**
     * This test emulates the loading of a java file that was parsed without errors.
     * Additionally it supposes that the property types and annotations definitions were already loaded into the
     * DataModelerWBContext, and thus they shouldn't be loaded from server.
     */
    @Test
    public void loadFileSuccessfulWithNoTypesInfoTest() {
        loadFileSuccessfulTest( false );
    }

    /**
     * This test emulates the loading of a mal formed java file, and thus with parse errors.
     * Additionally it supposes that the DataModelerWBContext has not yet the information about property types
     * and annotations definitions, and thus should also check that this information was also loaded from server and
     * properly initialized.
     */
    @Test
    public void loadFileUnSuccessfulWithTypesInfoTest() {
        loadFileUnSuccessfulTest( true );
    }

    /**
     * This test emulates the loading of a mal formed java file, and thus with parse errors.
     * Additionally it supposes that the property types and annotations definitions were already loaded into the
     * DataModelerWBContext, and thus they shouldn't be loaded from server.
     */
    @Test
    public void loadFileUnSuccessfulWithNoTypesInfoTest() {
        loadFileUnSuccessfulTest( false );
    }

    /**
     * Tests that a java file without parse errors was successfully loaded.
     *
     * @param loadTypesInfo indicates if the types and annotations definitions loading should be simulated.
     *
     */
    public void loadFileSuccessfulTest( boolean loadTypesInfo ) {

        EditorModelContent content = createContent( loadTypesInfo, false );

        when ( versionRecordManager.getCurrentPath() ).thenReturn( path );
        when( modelerService.loadContent( path, loadTypesInfo ) ).thenReturn( content );
        when( javaSourceEditor.getContent() ).thenReturn( content.getSource() );

        if ( loadTypesInfo ) {
            //types info is not loaded into the DataModelerWBContext.
            when( dataModelerWBContext.isTypesInfoLoaded() ).thenReturn( false );
        } else {
            //types info is already into the DataModelerWBContext.
            when( dataModelerWBContext.isTypesInfoLoaded() ).thenReturn( true );
        }

        //just for convenience, since the DataModelerContext is initialized by taking this definitions from the DMWC.
        when ( dataModelerWBContext.getAnnotationDefinitions() ).thenReturn( testAnnotationDefs );
        when( dataModelerWBContext.getPropertyTypes() ).thenReturn( testTypeDefs );

        presenter.onStartup( path, placeRequest );

        //Verifications during and after model loading.

        verify( view, times( 1 ) ).showLoading();
        verify( view, times( 1 ) ).hideBusyIndicator();

        //presenter should ask the DataModelerWBContext if the types info is already loaded.
        verify( dataModelerWBContext, times( 1 ) ).isTypesInfoLoaded();

        if ( loadTypesInfo ) {
            //the types info should have been set into the DataModelerWBContext as part of the presenter loading.
            verify( dataModelerWBContext, times( 1 ) ).setPropertyTypes( testTypeDefs );
            verify( dataModelerWBContext, times( 1 ) ).setAnnotationDefinitions( testAnnotationDefs );
        } else {
            //the types info shouldn't have been set into the DataModelerWBContext as part of the presenter loading.
            verify( dataModelerWBContext, times( 0 ) ).setPropertyTypes( testTypeDefs );
            verify( dataModelerWBContext, times( 0 ) ).setAnnotationDefinitions( testAnnotationDefs );
        }

        //presenter should clear the system messages related to this editor.
        verify( unpublishMessagesEvent, times( 1 ) ).fire( any( UnpublishMessagesEvent.class ) );

        //presenter should read the expected path.
        verify( modelerService, times( 1 ) ).loadContent( path, loadTypesInfo );

        //verify that the context created by the presenter was properly initialized.
        DataModelerContext context = presenter.context;

        assertEquals( testModel, context.getDataModel() );
        assertEquals( testObject1, context.getDataObject() );
        assertEquals( kieProject, context.getCurrentProject() );
        assertEquals( testPackages, context.getCurrentProjectPackages() );
        assertEquals( testAnnotationDefs, context.getAnnotationDefinitions() );
        assertEquals( content, context.getEditorModelContent() );

        //the file was read successfully, so the status should be PARSED
        assertEquals( DataModelerContext.ParseStatus.PARSED, context.getParseStatus() );
        //the file was read and parsed successfully, so the editor should be now in the editor tab.
        assertEquals( DataModelerContext.EditionMode.GRAPHICAL_MODE, context.getEditionMode() );
        //file was just read, so the status should be NO_CHANGES.
        assertEquals( DataModelerContext.EditionStatus.NO_CHANGES, context.getEditionStatus() );


        //the view should have been initialized with the context.
        verify( view, times( 1 ) ).setContext( context );

        //the source editor should have been initialized with the source returned form server.
        verify( javaSourceEditor, times( 1 ) ).setContent( testSource );

        //current context should have been activated
        verify( dataModelerWBContext, times( 1 ) ).setActiveContext( context );
        //and notifications should have been sent.
        verify( dataModelerFocusEvent, times( 1 ) ).fire( any( DataModelerWorkbenchFocusEvent.class ) );
    }

    /**
     * Tests that a java file with parse errors was successfully loaded.
     *
     * @param loadTypesInfo indicates if the types and annotations definitions loading should be simulated.
     *
     */
    public void loadFileUnSuccessfulTest( boolean loadTypesInfo ) {

        EditorModelContent content = createContent( loadTypesInfo, true );
        //when there are parse errors the returned data object is null.
        content.setDataObject( null );

        when( versionRecordManager.getCurrentPath() ).thenReturn( path );
        when( modelerService.loadContent( path, loadTypesInfo ) ).thenReturn( content );
        when( javaSourceEditor.getContent() ).thenReturn( content.getSource() );

        if ( loadTypesInfo ) {
            //types info is not loaded into the DataModelerWBContext.
            when( dataModelerWBContext.isTypesInfoLoaded() ).thenReturn( false );
        } else {
            //types info is already into the DataModelerWBContext.
            when( dataModelerWBContext.isTypesInfoLoaded() ).thenReturn( true );
        }

        //just for convenience, since the DataModelerContext is initialized by taking this definitions from the DMWC.
        when( dataModelerWBContext.getAnnotationDefinitions() ).thenReturn( testAnnotationDefs );
        when( dataModelerWBContext.getPropertyTypes() ).thenReturn( testTypeDefs );

        presenter.onStartup( path, placeRequest );

        //Verifications during and after model loading.

        verify( view, times( 1 ) ).showLoading();
        verify( view, times( 1 ) ).hideBusyIndicator();

        //presenter should ask the DataModelerWBContext if the types info is already loaded.
        verify( dataModelerWBContext, times( 1 ) ).isTypesInfoLoaded();

        if ( loadTypesInfo ) {
            //the types info should have been set into the DataModelerWBContext as part of the presenter loading.
            verify( dataModelerWBContext, times( 1 ) ).setPropertyTypes( testTypeDefs );
            verify( dataModelerWBContext, times( 1 ) ).setAnnotationDefinitions( testAnnotationDefs );
        } else {
            //the types info shouldn't have been set into the DataModelerWBContext as part of the presenter loading.
            verify( dataModelerWBContext, times( 0 ) ).setPropertyTypes( testTypeDefs );
            verify( dataModelerWBContext, times( 0 ) ).setAnnotationDefinitions( testAnnotationDefs );
        }

        //presenter should clear the system messages related to this editor.
        verify( unpublishMessagesEvent, times( 1 ) ).fire( any( UnpublishMessagesEvent.class ) );

        //presenter should read the expected path.
        verify( modelerService, times( 1 ) ).loadContent( path, loadTypesInfo );

        //parse errors should have been published.
        verify( publishBatchMessagesEvent, times( 1 ) ).fire( any( PublishBatchMessagesEvent.class ) );
        //parse errors dialog should have been raised.
        verify( view, times( 1 ) ).showParseErrorsDialog( anyString(), anyString(), any( Command.class ) );

        //at this point the parse errors popup is raised and waiting for the user to press the ok button.
        //emulate the user click on the button.
        presenter.getOnLoadParseErrorCommand().execute();


        //verify that the context created by the presenter was properly initialized.
        DataModelerContext context = presenter.context;

        assertEquals( testModel, context.getDataModel() );
        assertEquals( null, context.getDataObject() );
        assertEquals( kieProject, context.getCurrentProject() );
        assertEquals( testPackages, context.getCurrentProjectPackages() );
        assertEquals( testAnnotationDefs, context.getAnnotationDefinitions() );
        assertEquals( content, context.getEditorModelContent() );

        //parse errors wherer produced on server so the status should be PARSE_ERRORS
        assertEquals( DataModelerContext.ParseStatus.PARSE_ERRORS, context.getParseStatus() );
        //the file wasn't parsed the editor should go to the source tab.
        assertEquals( DataModelerContext.EditionMode.SOURCE_MODE, context.getEditionMode() );
        //file was just read, so the status should be NO_CHANGES.
        assertEquals( DataModelerContext.EditionStatus.NO_CHANGES, context.getEditionStatus() );


        //context wasn't set on the view since there aren't a data object to show.
        verify( view, times( 0 ) ).setContext( context );

        //the source editor should have been initialized with the source returned form server.
        verify( javaSourceEditor, times( 2 ) ).setContent( testSource );

        //current context should have been activated
        verify( dataModelerWBContext, times( 1 ) ).setActiveContext( context );
        //and notifications should have been sent.
        verify( dataModelerFocusEvent, times( 1 ) ).fire( any( DataModelerWorkbenchFocusEvent.class ) );
    }


    private EditorModelContent createContent( boolean includeTypesInfo, boolean addParseErrors ) {
        EditorModelContent content = new EditorModelContent();

        content.setDataObject( testObject1 );
        content.setDataModel( testModel );
        content.setSource( testSource );
        content.setOriginalClassName( testObject1.getClassName() );
        content.setOriginalPackageName( testObject1.getPackageName() );
        content.setPath( path );
        content.setCurrentProject( kieProject );
        content.setCurrentProjectPackages( testPackages );
        content.setOverview( overview );

        if ( includeTypesInfo ) {
            content.setAnnotationDefinitions( testAnnotationDefs );
            content.setPropertyTypes( testTypeDefs );
        }

        if ( addParseErrors ) {
            content.setErrors( testErrors );
        }
        return content;
    }

    private Set<String> createTestPackages() {
        HashSet<String> packages = new HashSet<String>(  );

        packages.add( "package1" );
        packages.add( "package2" );
        return packages;
    }

    private Map<String, AnnotationDefinition> createTestAnnotations() {
        Map<String, AnnotationDefinition> annotationsDef = new HashMap<String, AnnotationDefinition>(  );
        AnnotationDefinition annotationDefinition = DriverUtils.buildAnnotationDefinition( Label.class );

        annotationsDef.put( annotationDefinition.getClassName(), annotationDefinition );
        annotationDefinition =  DriverUtils.buildAnnotationDefinition( Description.class );
        annotationsDef.put( annotationDefinition.getClassName(), annotationDefinition );
        return annotationsDef;
    }

    private List<PropertyType> createTestPropertyTypes() {
        return PropertyTypeFactoryImpl.getInstance().getBasePropertyTypes();
    }

    private List<DataModelerError> createTestErrors() {
        List<DataModelerError> errors = new ArrayList<DataModelerError>(  );
        errors.add( new DataModelerError( 1, "error1", Level.ERROR, path, 1, 0 ) );
        errors.add( new DataModelerError( 2, "error2", Level.ERROR, path, 2, 0 ) );
        errors.add( new DataModelerError( 3, "error3", Level.ERROR, path, 3, 0 ) );
        return errors;
    }

}