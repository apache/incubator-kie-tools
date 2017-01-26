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

package org.kie.workbench.common.screens.datamodeller.client.widgets.droolsdomain;

import javax.xml.bind.annotation.XmlRootElement;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.definition.type.ClassReactive;
import org.kie.api.definition.type.Duration;
import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.PropertyReactive;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Timestamp;
import org.kie.api.definition.type.TypeSafe;
import org.kie.api.remote.Remotable;
import org.kie.workbench.common.screens.datamodeller.client.util.AnnotationValueHandler;
import org.kie.workbench.common.screens.datamodeller.client.util.UIUtil;
import org.kie.workbench.common.screens.datamodeller.client.widgets.DataModelerEditorsTestHelper;
import org.kie.workbench.common.screens.datamodeller.client.widgets.DomainEditorBaseTest;
import org.kie.workbench.common.screens.datamodeller.events.DataModelerEvent;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.impl.DataObjectImpl;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.kie.workbench.common.screens.datamodeller.client.widgets.DataModelerEditorsTestHelper.*;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class DroolsDataObjectEditorTest
        extends DomainEditorBaseTest {

    @Mock
    private DroolsDataObjectEditorView view;

    private DroolsDataObjectEditor objectEditor;

    @Before
    public void initTest( ) {
        super.initTest( );
        objectEditor = createObjectEditor( );
    }

    @Test
    public void loadDataObjectTest( ) {

        //The domain editors typically reacts upon DataModelerContext changes.
        //when the context changes the editor will typically be reloaded.
        objectEditor.onContextChange( context );

        DataObject dataObject = context.getDataObject( );

        //the view should be populated with the values from the dataObject.
        verify( view, times( 1 ) ).setTypeSafe( "true" );
        verify( view, times( 1 ) ).setPropertyReactive( true );
        verify( view, times( 1 ) ).setRole( "EVENT" );
        verify( view, times( 1 ) ).setTimeStampField( "field2" );
        verify( view, times( 1 ) ).setDurationField( "field3" );
        verify( view, times( 1 ) ).setExpires( "1h" );
        verify( view, times( 1 ) ).setRemotable( true );

        assertFalse( objectEditor.isReadonly( ) );

    }

    @Test
    public void valuesChangeTest( ) {

        //load the editor
        objectEditor.onContextChange( context );

        //emulate the user input
        when( view.getTypeSafe( ) ).thenReturn( "false" );
        when( view.getPropertyReactive( ) ).thenReturn( false );
        when( view.getRole( ) ).thenReturn( null );
        when( view.getTimeStampField( ) ).thenReturn( NEW_FIELD_NAME );
        when( view.getDurationField( ) ).thenReturn( NEW_FIELD_NAME );
        when( view.getExpires( ) ).thenReturn( "100d" ); //new timer expression
        when( view.getRemotable( ) ).thenReturn( false );

        when( validationService.isTimerIntervalValid( "100d" ) ).thenReturn( true );

        //notify the presenter about the changes in the UI
        objectEditor.onTypeSafeChange( );
        objectEditor.onPropertyReactiveChange( );
        objectEditor.onRoleChange( );
        objectEditor.onTimeStampFieldChange( );
        objectEditor.onDurationFieldChange( );
        objectEditor.onExpiresChange( );
        objectEditor.onRemotableChange( );

        //After the changes has been processed by the presenter the dataObject should have been populated with the new values.
        DataObject dataObject = context.getDataObject( );

        assertEquals( "false", AnnotationValueHandler.getStringValue( dataObject, TypeSafe.class.getName( ), "value" ) );
        assertNull( dataObject.getAnnotation( PropertyReactive.class.getName( ) ) );
        assertNull( dataObject.getAnnotation( Role.class.getName( ) ) );
        assertEquals( NEW_FIELD_NAME, AnnotationValueHandler.getStringValue( dataObject, Timestamp.class.getName( ), "value" ) );
        assertEquals( NEW_FIELD_NAME, AnnotationValueHandler.getStringValue( dataObject, Duration.class.getName( ), "value" ) );
        assertEquals( "100d", AnnotationValueHandler.getStringValue( dataObject, Expires.class.getName( ), "value" ) );
        assertNull( dataObject.getAnnotation( XmlRootElement.class.getName( ) ) );
        assertNull( dataObject.getAnnotation( Remotable.class.getName( ) ) );

        verify( dataModelerEvent, times( 7 ) ).fire( any( DataModelerEvent.class ) );

    }

    @Test
    public void typeSafeChangeTest( ) {
        preloadEmptyObject( );
        // tests the TypeSafe being set to true.
        when( view.getTypeSafe( ) ).thenReturn( "true" );
        objectEditor.onTypeSafeChange( );
        assertEquals( "true",
                AnnotationValueHandler.getStringValue( context.getDataObject( ), TypeSafe.class.getName( ), "value" ) );

        // tests the TypeSave being set to false.
        when( view.getTypeSafe( ) ).thenReturn( "false" );
        objectEditor.onTypeSafeChange( );
        assertEquals( "false",
                AnnotationValueHandler.getStringValue( context.getDataObject( ), TypeSafe.class.getName( ), "value" ) );

        // tests the TypeSafe not set.
        when( view.getTypeSafe( ) ).thenReturn( UIUtil.NOT_SELECTED );
        objectEditor.onTypeSafeChange( );
        assertNull( context.getDataObject( ).getAnnotation( TypeSafe.class.getName( ) ) );
    }

    @Test
    public void propertyReactiveChangeTest( ) {
        preloadEmptyObject( );
        // emulate that the ClassReactive was previously set
        context.getDataObject( ).addAnnotation( DataModelerEditorsTestHelper.createAnnotation( ClassReactive.class ) );

        // tests the PropertyReactive being set to true.
        when( view.getPropertyReactive( ) ).thenReturn( true );
        objectEditor.onPropertyReactiveChange( );
        // the PropertyReactive should have been set
        assertNotNull( context.getDataObject( ).getAnnotation( PropertyReactive.class.getName( ) ) );
        // the ClassReactive should have been removed.
        assertNull( context.getDataObject( ).getAnnotation( ClassReactive.class.getName( ) ) );

        // emulate that the PropertyReactive was previously set.
        context.getDataObject( ).addAnnotation( DataModelerEditorsTestHelper.createAnnotation( PropertyReactive.class ) );
        // tests the property reactive being set to false.
        when( view.getPropertyReactive( ) ).thenReturn( false );
        objectEditor.onPropertyReactiveChange( );
        // the PropertyReactive should have been removed.
        assertNull( context.getDataObject( ).getAnnotation( PropertyReactive.class.getName( ) ) );
    }

    @Test
    public void classReactiveChangeTest( ) {
        preloadEmptyObject( );
        // emulate that the PropertyReactive was previously set
        context.getDataObject( ).addAnnotation( DataModelerEditorsTestHelper.createAnnotation( PropertyReactive.class ) );

        // tests the ClassReactive reactive being set to true.
        when( view.getClassReactive( ) ).thenReturn( true );
        objectEditor.onClassReactiveChange( );
        // the ClassReactive should have been set
        assertNotNull( context.getDataObject( ).getAnnotation( ClassReactive.class.getName( ) ) );
        // the PropertyReactive should have been removed.
        assertNull( context.getDataObject( ).getAnnotation( PropertyReactive.class.getName( ) ) );

        // emulate that the ClassReactive was previously set.
        context.getDataObject( ).addAnnotation( DataModelerEditorsTestHelper.createAnnotation( ClassReactive.class ) );
        // tests the ClassReactive being set to false.
        when( view.getClassReactive( ) ).thenReturn( false );
        objectEditor.onClassReactiveChange( );
        // the ClassReactive should have been removed.
        assertNull( context.getDataObject( ).getAnnotation( ClassReactive.class.getName( ) ) );
    }

    @Test
    public void roleChangeTest( ) {
        preloadEmptyObject( );
        // tests the Role being set.
        when( view.getRole( ) ).thenReturn( "EVENT" );
        objectEditor.onRoleChange( );
        // the Role should have been set.
        assertNotNull( context.getDataObject( ).getAnnotation( Role.class.getName( ) ) );
        assertEquals( "EVENT",
                AnnotationValueHandler.getStringValue( context.getDataObject( ), Role.class.getName( ), "value" ) );

        // test the Role being unset.
        when( view.getRole( ) ).thenReturn( UIUtil.NOT_SELECTED );
        objectEditor.onRoleChange( );
        // the Role should have been removed.
        assertNull( context.getDataObject( ).getAnnotation( Role.class.getName( ) ) );
    }

    @Test
    public void timeStampChangeTest( ) {
        preloadEmptyObject( );
        // tests the TimeStamp being set.
        when( view.getTimeStampField( ) ).thenReturn( "someFieldName" );
        objectEditor.onTimeStampFieldChange( );
        // the TimeStamp should have been set.
        assertEquals( "someFieldName",
                AnnotationValueHandler.getStringValue( context.getDataObject( ), Timestamp.class.getName( ), "value" ) );

        // tests the TimeStamp being unset.
        when( view.getTimeStampField( ) ).thenReturn( UIUtil.NOT_SELECTED );
        objectEditor.onTimeStampFieldChange( );
        assertNull( context.getDataObject( ).getAnnotation( Timestamp.class.getName( ) ) );
    }

    @Test
    public void durationChangeTest( ) {
        preloadEmptyObject( );
        // tests the Duration being set.
        when( view.getDurationField( ) ).thenReturn( "someFieldName" );
        objectEditor.onDurationFieldChange( );
        ;
        // the Duration should have been set.
        assertEquals( "someFieldName",
                AnnotationValueHandler.getStringValue( context.getDataObject( ), Duration.class.getName( ), "value" ) );

        // tests the Duration being unset.
        when( view.getDurationField( ) ).thenReturn( UIUtil.NOT_SELECTED );
        objectEditor.onDurationFieldChange( );
        assertNull( context.getDataObject( ).getAnnotation( Duration.class.getName( ) ) );
    }

    @Test
    public void expiresChangeTest( ) {
        preloadEmptyObject( );
        // tests the Expires being set with a valid value.
        when( view.getExpires( ) ).thenReturn( "1h" );
        when( validationService.isTimerIntervalValid( "1h" ) ).thenReturn( true );
        objectEditor.onExpiresChange( );
        // the Expires should have been set
        assertEquals( "1h",
                AnnotationValueHandler.getStringValue( context.getDataObject( ), Expires.class.getName( ), "value" ) );

        // test the Expires being unset.
        when( view.getExpires( ) ).thenReturn( "" );
        when( validationService.isTimerIntervalValid( "" ) ).thenReturn( true );
        objectEditor.onExpiresChange( );
        assertNull( context.getDataObject( ).getAnnotation( Expires.class.getName( ) ) );
    }

    @Test
    public void testRemotableChangeTest( ) {
        preloadEmptyObject( );
        // tests the data object being marked as "remotable".
        // emulate that the legacy annotation @Remotable was set for whatever reason. e.g. a project generated in an
        // older WB version.
        context.getDataObject( ).addAnnotation( DataModelerEditorsTestHelper.createAnnotation( Remotable.class ) );
        when( view.getRemotable( ) ).thenReturn( true );
        objectEditor.onRemotableChange( );
        // the new defined annotation XmlRootElement for marking the data object as "remotable should have been set
        assertNotNull( context.getDataObject( ).getAnnotation( XmlRootElement.class.getName( ) ) );
        // the legacy annotation should have been removed.
        assertNull( context.getDataObject( ).getAnnotation( Remotable.class.getName( ) ) );

        // emulate that the legacy annotation @Remotable was set for whatever reason. e.g. a project generated in an
        // older WB version.
        context.getDataObject( ).addAnnotation( DataModelerEditorsTestHelper.createAnnotation( Remotable.class ) );
        // tests the data object being maked as non "remotable".
        when( view.getRemotable( ) ).thenReturn( false );
        objectEditor.onRemotableChange( );
        //the XmlRootElement annotation should have been removed.
        assertNull( context.getDataObject( ).getAnnotation( XmlRootElement.class.getName( ) ) );
        // the legacy annotation should have been removed.
        assertNull( context.getDataObject( ).getAnnotation( Remotable.class.getName( ) ) );
    }

    private DroolsDataObjectEditor createObjectEditor( ) {
        DroolsDataObjectEditor objectEditor = new DroolsDataObjectEditor( view,
                handlerRegistry,
                dataModelerEvent,
                commandBuilder,
                validatorService );

        return objectEditor;
    }

    private void preloadEmptyObject( ) {
        //load the editor
        context.setDataObject( new DataObjectImpl( ) );
        objectEditor.onContextChange( context );
    }
}