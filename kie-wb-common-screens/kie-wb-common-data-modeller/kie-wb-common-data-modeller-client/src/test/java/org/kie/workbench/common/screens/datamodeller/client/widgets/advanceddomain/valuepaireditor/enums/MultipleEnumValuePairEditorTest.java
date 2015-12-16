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

package org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.datamodeller.annotations.EnumParamsAnnotation;
import org.kie.workbench.common.services.datamodeller.annotations.TestEnums;
import org.kie.workbench.common.services.datamodeller.core.AnnotationDefinition;
import org.kie.workbench.common.services.datamodeller.core.AnnotationValuePairDefinition;
import org.kie.workbench.common.services.datamodeller.util.DriverUtils;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class MultipleEnumValuePairEditorTest {

    @GwtMock
    MultipleEnumValuePairEditorView view;

    @GwtMock
    EnumValuePairOptionEditorView optionEditorView;

    Map<String, EnumValuePairOptionEditor> optionEditors;

    AnnotationDefinition annotationDefinition;

    @Before
    public void initTest() {
        optionEditors = new HashMap<String, EnumValuePairOptionEditor>();

        EnumValuePairOptionEditor optionEditor = new EnumValuePairOptionEditor( optionEditorView );
        optionEditor.setOptionLabel( TestEnums.ENUM1.VALUE1.name() );
        optionEditors.put( TestEnums.ENUM1.VALUE1.name(), optionEditor );

        optionEditor = new EnumValuePairOptionEditor( optionEditorView );
        optionEditor.setOptionLabel( TestEnums.ENUM1.VALUE2.name() );
        optionEditors.put( TestEnums.ENUM1.VALUE2.name(), optionEditor );

        optionEditor = new EnumValuePairOptionEditor( optionEditorView );
        optionEditor.setOptionLabel( TestEnums.ENUM1.VALUE3.name() );
        optionEditors.put( TestEnums.ENUM1.VALUE3.name(), optionEditor );

        optionEditor = new EnumValuePairOptionEditor( optionEditorView );
        optionEditor.setOptionLabel( "{}" );
        optionEditors.put( "{}", optionEditor );

        annotationDefinition = DriverUtils.buildAnnotationDefinition( EnumParamsAnnotation.class );
    }

    @Test
    public void testEditorLoad() {

        MultipleEnumValuePairEditor enumEditor = new MultipleEnumValuePairEditorExtended( view );

        AnnotationValuePairDefinition valuePairDefinition = annotationDefinition.getValuePair( "enumArrayParam1" );
        enumEditor.init( valuePairDefinition );

        verify( view, times( 1 ) ).setValuePairLabel( valuePairDefinition.getName() );
        verify( view, times( 1 ) ).showValuePairRequiredIndicator( false );
        verify( view, times( 1 ) ).addOptionEditor( optionEditors.get( TestEnums.ENUM1.VALUE1.name() ) );
        verify( view, times( 1 ) ).addOptionEditor( optionEditors.get( TestEnums.ENUM1.VALUE2.name() ) );
        verify( view, times( 1 ) ).addOptionEditor( optionEditors.get( TestEnums.ENUM1.VALUE3.name() ) );
        verify( view, times( 1 ) ).addOptionEditor( optionEditors.get( "{}" ) );
    }

    @Test
    public void testValueChanges() {

        MultipleEnumValuePairEditor enumEditor = new MultipleEnumValuePairEditorExtended( view );

        AnnotationValuePairDefinition valuePairDefinition = annotationDefinition.getValuePair( "enumArrayParam1" );
        enumEditor.init( valuePairDefinition );

        //emulate the selection of values VALUE1 and VALUE2
        EnumValuePairOptionEditor optionEditor = optionEditors.get( TestEnums.ENUM1.VALUE1.name() );
        when( optionEditorView.getValue() ).thenReturn( true );
        optionEditor.onValueChange();

        optionEditor = optionEditors.get( TestEnums.ENUM1.VALUE2.name() );
        when( optionEditorView.getValue() ).thenReturn( true );
        optionEditor.onValueChange();

        List<String> expectedValues = new ArrayList<String>();
        expectedValues.add( TestEnums.ENUM1.VALUE1.name() );
        expectedValues.add( TestEnums.ENUM2.VALUE2.name() );

        assertEquals( expectedValues, enumEditor.getValue() );
    }

    @Test
    public void testAllValuesSelected() {

        MultipleEnumValuePairEditor enumEditor = new MultipleEnumValuePairEditorExtended( view );

        AnnotationValuePairDefinition valuePairDefinition = annotationDefinition.getValuePair( "enumArrayParam1" );
        enumEditor.init( valuePairDefinition );

        //emulate the selection of values VALUE1 and VALUE2, and then the selection of the "{}" option.
        EnumValuePairOptionEditor optionEditor = optionEditors.get( TestEnums.ENUM1.VALUE1.name() );
        when( optionEditorView.getValue() ).thenReturn( true );
        optionEditor.onValueChange();

        optionEditor = optionEditors.get( TestEnums.ENUM1.VALUE2.name() );
        when( optionEditorView.getValue() ).thenReturn( true );
        optionEditor.onValueChange();

        optionEditor = optionEditors.get( "{}" );
        when( optionEditorView.getValue() ).thenReturn( true );
        optionEditor.onValueChange();

        List<String> expectedValues = new ArrayList<String>();
        assertEquals( expectedValues, enumEditor.getValue() );
    }

    private class MultipleEnumValuePairEditorExtended extends MultipleEnumValuePairEditor {

        public MultipleEnumValuePairEditorExtended( MultipleEnumValuePairEditorView view ) {
            super( view );
        }

        @Override
        protected EnumValuePairOptionEditor createOptionEditor( String option ) {
            return optionEditors.get( option );
        }
    }
}