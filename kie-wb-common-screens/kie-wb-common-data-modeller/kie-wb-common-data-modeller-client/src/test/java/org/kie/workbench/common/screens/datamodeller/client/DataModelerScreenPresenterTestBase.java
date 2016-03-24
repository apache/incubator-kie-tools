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
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.rpc.SessionInfo;

import static org.mockito.Mockito.mock;

@RunWith( GwtMockitoTestRunner.class )
public abstract class DataModelerScreenPresenterTestBase {

    @GwtMock
    protected DataModelerScreenPresenter.DataModelerScreenView view;

    @Mock
    protected SessionInfo sessionInfo;

    @Mock
    protected EditJavaSourceWidget javaSourceEditor;

    @Mock
    protected EventSourceMock<DataModelerEvent> dataModelerEvent;

    @Mock
    protected EventSourceMock<UnpublishMessagesEvent> unpublishMessagesEvent;

    @Mock
    protected EventSourceMock<PublishBatchMessagesEvent> publishBatchMessagesEvent;

    @Mock
    protected EventSourceMock<LockRequiredEvent> lockRequired;

    @Mock
    protected EventSourceMock<DataModelerWorkbenchFocusEvent> dataModelerFocusEvent;

    @Mock
    protected KieWorkbenchACL kieACL;

    @Mock
    protected DataModelerService modelerService;

    @Mock
    protected ValidatorService validatorService;

    @Mock
    protected JavaFileNameValidator javaFileNameValidator;

    protected JavaResourceType resourceType;

    @Mock
    protected DataModelerWorkbenchContext dataModelerWBContext;

    @Mock
    protected ObservablePath path;

    @GwtMock
    protected VersionRecordManager versionRecordManager;

    @Mock
    protected PlaceRequest placeRequest;

    protected DataModelerScreenPresenter presenter;

    /**
     * Emulates the overview returned from server.
     */
    @Mock
    protected Overview overview;

    /**
     * Emulates the project returned from server.
     */
    @Mock
    protected KieProject kieProject;

    /**
     * Emulates the data object returned from server.
     */
    protected DataObject testObject1;

    /**
     * Emulates the model returned from server.
     */
    protected DataModel testModel;

    /**
     * Emulates the source returned from server.
     */
    protected String testSource = "public class Dummy {}";

    /**
     * Emulates the list for parse errors returned from server when a java filed couldn't be parsed.
     */
    protected List<DataModelerError> testErrors;

    /**
     * Emulates the packages definition returned form server.
     */
    protected Set<String> testPackages;

    /**
     * Emulates the annotation definitions returned from server.
     */
    protected Map<String, AnnotationDefinition> testAnnotationDefs;

    /**
     * Emulates the property types definitions returned from server.
     */
    protected List<PropertyType> testTypeDefs;

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
                this.versionRecordManager = DataModelerScreenPresenterTestBase.this.versionRecordManager;
                overviewWidget = mock( OverviewWidgetPresenter.class );

                javaSourceEditor = DataModelerScreenPresenterTestBase.this.javaSourceEditor;
                dataModelerEvent = DataModelerScreenPresenterTestBase.this.dataModelerEvent;
                unpublishMessagesEvent = DataModelerScreenPresenterTestBase.this.unpublishMessagesEvent;
                publishBatchMessagesEvent = DataModelerScreenPresenterTestBase.this.publishBatchMessagesEvent;
                lockRequired = DataModelerScreenPresenterTestBase.this.lockRequired;
                dataModelerFocusEvent = DataModelerScreenPresenterTestBase.this.dataModelerFocusEvent;
                kieACL = DataModelerScreenPresenterTestBase.this.kieACL;
                modelerService = new CallerMock<DataModelerService>(
                        DataModelerScreenPresenterTestBase.this.modelerService );
                validatorService = DataModelerScreenPresenterTestBase.this.validatorService;
                javaFileNameValidator = DataModelerScreenPresenterTestBase.this.javaFileNameValidator;
                resourceType = DataModelerScreenPresenterTestBase.this.resourceType;
                dataModelerWBContext = DataModelerScreenPresenterTestBase.this.dataModelerWBContext;
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

    protected EditorModelContent createContent( boolean includeTypesInfo, boolean addParseErrors ) {
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

    protected Set<String> createTestPackages() {
        HashSet<String> packages = new HashSet<String>(  );

        packages.add( "package1" );
        packages.add( "package2" );
        return packages;
    }

    protected Map<String, AnnotationDefinition> createTestAnnotations() {
        Map<String, AnnotationDefinition> annotationsDef = new HashMap<String, AnnotationDefinition>(  );
        AnnotationDefinition annotationDefinition = DriverUtils.buildAnnotationDefinition( Label.class );

        annotationsDef.put( annotationDefinition.getClassName(), annotationDefinition );
        annotationDefinition =  DriverUtils.buildAnnotationDefinition( Description.class );
        annotationsDef.put( annotationDefinition.getClassName(), annotationDefinition );
        return annotationsDef;
    }

    protected List<PropertyType> createTestPropertyTypes() {
        return PropertyTypeFactoryImpl.getInstance().getBasePropertyTypes();
    }

    protected List<DataModelerError> createTestErrors() {
        List<DataModelerError> errors = new ArrayList<DataModelerError>(  );
        errors.add( new DataModelerError( 1, "error1", Level.ERROR, path, 1, 0 ) );
        errors.add( new DataModelerError( 2, "error2", Level.ERROR, path, 2, 0 ) );
        errors.add( new DataModelerError( 3, "error3", Level.ERROR, path, 3, 0 ) );
        return errors;
    }
}
