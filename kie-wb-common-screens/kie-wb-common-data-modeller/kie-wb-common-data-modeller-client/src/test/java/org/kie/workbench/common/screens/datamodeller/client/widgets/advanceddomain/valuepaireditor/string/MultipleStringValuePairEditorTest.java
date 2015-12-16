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

package org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.string;

import java.util.ArrayList;
import java.util.List;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.ValuePairEditor;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.multiple.MultipleValuePairEditorView;
import org.kie.workbench.common.services.datamodeller.annotations.StringParamsAnnotation;
import org.kie.workbench.common.services.datamodeller.core.AnnotationDefinition;
import org.kie.workbench.common.services.datamodeller.core.AnnotationValuePairDefinition;
import org.kie.workbench.common.services.datamodeller.util.DriverUtils;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class MultipleStringValuePairEditorTest {

    @GwtMock
    MultipleValuePairEditorView multipleEditorView;

    @GwtMock
    AbstractStringValuePairEditorView singleEditorView;

    AnnotationDefinition annotationDefinition;

    List<ValuePairEditor<?>> stringEditors = new ArrayList<ValuePairEditor<?>>();

    @Before
    public void initTest() {
        annotationDefinition = DriverUtils.buildAnnotationDefinition( StringParamsAnnotation.class );
    }

    @Test
    public void testEditorLoad() {

        stringEditors.clear();
        List<String> originalValues = new ArrayList<String>();
        originalValues.add( "AAA" );
        originalValues.add( "BBB" );
        originalValues.add( "CCC" );

        MultipleStringValuePairEditor multiStringEditor = new MultipleStringValuePairEditorExtended( multipleEditorView );
        AnnotationValuePairDefinition valuePairDefinition = annotationDefinition.getValuePair( "stringParam1" );

        multiStringEditor.init( valuePairDefinition );

        verify( multipleEditorView, times( 1 ) ).setValuePairLabel( valuePairDefinition.getName() );
        verify( multipleEditorView, times( 1 ) ).showValuePairRequiredIndicator( false );

        multiStringEditor.setValue( originalValues );
        //three individual editors should have been created.
        assertEquals( 3, stringEditors.size() );

        //and populated with the corresponding value
        verify( singleEditorView, times( 1 ) ).setValue( "AAA" );
        verify( singleEditorView, times( 1 ) ).setValue( "BBB" );
        verify( singleEditorView, times( 1 ) ).setValue( "CCC" );

        assertTrue( multiStringEditor.isValid() );
    }

    @Test
    public void testAddValuesChange() {

        stringEditors.clear();

        MultipleStringValuePairEditor multiStringEditor = new MultipleStringValuePairEditorExtended( multipleEditorView );
        AnnotationValuePairDefinition valuePairDefinition = annotationDefinition.getValuePair( "stringParam1" );

        StringValuePairEditor addItemEditor = new StringValuePairEditor( singleEditorView );
        addItemEditor.init( valuePairDefinition );

        when( multipleEditorView.getAddItemEditor() ).thenReturn( ( ValuePairEditor ) addItemEditor );

        multiStringEditor.init( valuePairDefinition );

        List<String> expectedValues = new ArrayList<String>();
        expectedValues.add( "AAA" );
        expectedValues.add( "BBB" );
        expectedValues.add( "CCC" );

        //emulate the user adding values to the array.
        when( singleEditorView.getValue() ).thenReturn( "AAA" );
        addItemEditor.onValueChange();
        multiStringEditor.onAddItem();

        when( singleEditorView.getValue() ).thenReturn( "BBB" );
        addItemEditor.onValueChange();
        multiStringEditor.onAddItem();

        when( singleEditorView.getValue() ).thenReturn( "CCC" );
        addItemEditor.onValueChange();
        multiStringEditor.onAddItem();

        when( multipleEditorView.getItemEditors() ).thenReturn( stringEditors );

        assertTrue( multiStringEditor.isValid() );
        assertEquals( expectedValues, multiStringEditor.getValue() );
    }

    private class MultipleStringValuePairEditorExtended extends MultipleStringValuePairEditor {

        public MultipleStringValuePairEditorExtended( MultipleValuePairEditorView view ) {
            super( view );
        }

        @Override
        public ValuePairEditor<?> createValuePairEditor( AnnotationValuePairDefinition valuePairDefinition ) {
            StringValuePairEditor stringEditor = new StringValuePairEditor( singleEditorView );
            stringEditors.add( stringEditor );
            return stringEditor;
        }
    }
}