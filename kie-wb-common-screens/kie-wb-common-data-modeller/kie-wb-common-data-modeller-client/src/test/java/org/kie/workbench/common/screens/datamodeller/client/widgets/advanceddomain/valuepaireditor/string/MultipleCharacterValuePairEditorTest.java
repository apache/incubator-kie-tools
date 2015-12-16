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
import org.kie.workbench.common.services.datamodeller.annotations.CharParamsAnnotation;
import org.kie.workbench.common.services.datamodeller.core.AnnotationDefinition;
import org.kie.workbench.common.services.datamodeller.core.AnnotationValuePairDefinition;
import org.kie.workbench.common.services.datamodeller.util.DriverUtils;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class MultipleCharacterValuePairEditorTest {

    @GwtMock
    MultipleValuePairEditorView multipleEditorView;

    @GwtMock
    AbstractStringValuePairEditorView singleEditorView;

    AnnotationDefinition annotationDefinition;

    List<ValuePairEditor<?>> characterEditors = new ArrayList<ValuePairEditor<?>>();

    @Before
    public void initTest() {
        annotationDefinition = DriverUtils.buildAnnotationDefinition( CharParamsAnnotation.class );
    }

    @Test
    public void testEditorLoad() {

        characterEditors.clear();
        List<String> originalValues = new ArrayList<String>();
        originalValues.add( "A" );
        originalValues.add( "B" );
        originalValues.add( "C" );

        MultipleCharacterValuePairEditor multiCharacterEditor = new MultipleCharacterValuePairEditorExtended( multipleEditorView );
        AnnotationValuePairDefinition valuePairDefinition = annotationDefinition.getValuePair( "charArrayParam1" );

        multiCharacterEditor.init( valuePairDefinition );

        verify( multipleEditorView, times( 1 ) ).setValuePairLabel( valuePairDefinition.getName() );
        verify( multipleEditorView, times( 1 ) ).showValuePairRequiredIndicator( false );

        multiCharacterEditor.setValue( originalValues );
        //three individual editors should have been created.
        assertEquals( 3, characterEditors.size() );

        //and populated with the corresponding value
        verify( singleEditorView, times( 1 ) ).setValue( "A" );
        verify( singleEditorView, times( 1 ) ).setValue( "B" );
        verify( singleEditorView, times( 1 ) ).setValue( "C" );

        assertTrue( multiCharacterEditor.isValid() );
    }

    @Test
    public void testAddValuesChange() {

        characterEditors.clear();

        MultipleCharacterValuePairEditor multiCharacterEditor = new MultipleCharacterValuePairEditorExtended( multipleEditorView );
        AnnotationValuePairDefinition valuePairDefinition = annotationDefinition.getValuePair( "charArrayParam1" );

        CharacterValuePairEditor addItemEditor = new CharacterValuePairEditor( singleEditorView );
        addItemEditor.init( valuePairDefinition );

        when( multipleEditorView.getAddItemEditor() ).thenReturn( ( ValuePairEditor ) addItemEditor );

        multiCharacterEditor.init( valuePairDefinition );

        List<String> expectedValues = new ArrayList<String>();
        expectedValues.add( "A" );
        expectedValues.add( "B" );
        expectedValues.add( "C" );

        //emulate the user adding values to the array.
        when( singleEditorView.getValue() ).thenReturn( "A" );
        addItemEditor.onValueChange();
        multiCharacterEditor.onAddItem();

        when( singleEditorView.getValue() ).thenReturn( "B" );
        addItemEditor.onValueChange();
        multiCharacterEditor.onAddItem();

        when( singleEditorView.getValue() ).thenReturn( "C" );
        addItemEditor.onValueChange();
        multiCharacterEditor.onAddItem();

        when( multipleEditorView.getItemEditors() ).thenReturn( characterEditors );

        assertTrue( multiCharacterEditor.isValid() );
        assertEquals( expectedValues, multiCharacterEditor.getValue() );
    }

    private class MultipleCharacterValuePairEditorExtended extends MultipleCharacterValuePairEditor {

        public MultipleCharacterValuePairEditorExtended( MultipleValuePairEditorView view ) {
            super( view );
        }

        @Override
        public ValuePairEditor<?> createValuePairEditor( AnnotationValuePairDefinition valuePairDefinition ) {
            CharacterValuePairEditor characterEditor = new CharacterValuePairEditor( singleEditorView );
            characterEditors.add( characterEditor );
            return characterEditor;
        }
    }
}
