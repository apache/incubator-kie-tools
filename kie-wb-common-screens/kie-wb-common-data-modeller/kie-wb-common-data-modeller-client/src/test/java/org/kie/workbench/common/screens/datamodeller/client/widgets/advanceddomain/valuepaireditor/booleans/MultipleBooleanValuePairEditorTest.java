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

package org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.booleans;

import java.util.ArrayList;
import java.util.List;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.ValuePairEditor;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.multiple.MultipleValuePairEditorView;
import org.kie.workbench.common.services.datamodeller.annotations.BooleanParamsAnnotation;
import org.kie.workbench.common.services.datamodeller.core.AnnotationDefinition;
import org.kie.workbench.common.services.datamodeller.core.AnnotationValuePairDefinition;
import org.kie.workbench.common.services.datamodeller.util.DriverUtils;
import org.uberfire.commons.data.Pair;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class MultipleBooleanValuePairEditorTest {

    @GwtMock
    MultipleValuePairEditorView multipleEditorView;

    @GwtMock
    BooleanValuePairEditorView singleEditorView;

    AnnotationDefinition annotationDefinition;

    List<Pair<String, String>> options;

    List<ValuePairEditor<?>> booleanEditors = new ArrayList<ValuePairEditor<?>>();

    @Before
    public void initTest() {
        annotationDefinition = DriverUtils.buildAnnotationDefinition( BooleanParamsAnnotation.class );
        options = new ArrayList<Pair<String, String>>( );
        options.add( new Pair<String, String>( "true", "true" ) );
        options.add( new Pair<String, String>( "false", "false" ) );
    }

    @Test
    public void testEditorLoad() {

        booleanEditors.clear();
        List<Boolean> originalValues = new ArrayList<Boolean>();
        originalValues.add( Boolean.FALSE );
        originalValues.add( Boolean.FALSE );
        originalValues.add( Boolean.TRUE );
        originalValues.add( Boolean.TRUE );

        MultipleBooleanValuePairEditor multiBooleanEditor = new MultipleBooleanValuePairEditorExtended( multipleEditorView );
        AnnotationValuePairDefinition valuePairDefinition = annotationDefinition.getValuePair( "booleanArrayParam1" );

        multiBooleanEditor.init( valuePairDefinition );

        verify( multipleEditorView, times( 1 ) ).setValuePairLabel( valuePairDefinition.getName() );
        verify( multipleEditorView, times( 1 ) ).showValuePairRequiredIndicator( false );
        when( multipleEditorView.getItemEditors() ).thenReturn( booleanEditors );

        multiBooleanEditor.setValue( originalValues );
        //four individual editors should have been created.
        assertEquals( 4, booleanEditors.size() );

        //and populated with the corresponding value
        verify( singleEditorView, times( 2 ) ).setSelectedValue( "true" );
        verify( singleEditorView, times( 2 ) ).setSelectedValue( "false" );

        assertTrue( multiBooleanEditor.isValid() );
        assertEquals( originalValues, multiBooleanEditor.getValue() );
    }

    @Test
    public void testAddValuesChange() {

        booleanEditors.clear();

        MultipleBooleanValuePairEditor multiBooleanEditor = new MultipleBooleanValuePairEditorExtended( multipleEditorView );
        AnnotationValuePairDefinition valuePairDefinition = annotationDefinition.getValuePair( "booleanArrayParam1" );

        BooleanValuePairEditor addItemEditor = new BooleanValuePairEditor( singleEditorView );
        addItemEditor.init( valuePairDefinition );

        when( multipleEditorView.getAddItemEditor() ).thenReturn( ( ValuePairEditor ) addItemEditor );

        multiBooleanEditor.init( valuePairDefinition );

        List<Boolean> expectedValues = new ArrayList<Boolean>();
        expectedValues.add( Boolean.FALSE );
        expectedValues.add( Boolean.TRUE );
        expectedValues.add( Boolean.FALSE );

        //emulate the user adding values to the array.
        when( singleEditorView.getSelectedValue() ).thenReturn( "false" );
        addItemEditor.onValueChange();
        multiBooleanEditor.onAddItem();

        when( singleEditorView.getSelectedValue() ).thenReturn( "true" );
        addItemEditor.onValueChange();
        multiBooleanEditor.onAddItem();

        when( singleEditorView.getSelectedValue() ).thenReturn( "false" );
        addItemEditor.onValueChange();
        multiBooleanEditor.onAddItem();

        when( multipleEditorView.getItemEditors() ).thenReturn( booleanEditors );

        assertTrue( multiBooleanEditor.isValid() );
        assertEquals( expectedValues, multiBooleanEditor.getValue() );
    }

    private class MultipleBooleanValuePairEditorExtended extends MultipleBooleanValuePairEditor {

        public MultipleBooleanValuePairEditorExtended( MultipleValuePairEditorView view ) {
            super( view );
        }

        @Override
        public ValuePairEditor<?> createValuePairEditor( AnnotationValuePairDefinition valuePairDefinition ) {
            BooleanValuePairEditor booleanEditor = new BooleanValuePairEditor( singleEditorView );
            booleanEditors.add( booleanEditor );
            return booleanEditor;
        }
    }
}