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

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.datamodeller.annotations.CharParamsAnnotation;
import org.kie.workbench.common.services.datamodeller.core.AnnotationDefinition;
import org.kie.workbench.common.services.datamodeller.core.AnnotationValuePairDefinition;
import org.kie.workbench.common.services.datamodeller.util.DriverUtils;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class CharacterValuePairEditorTest {

    @GwtMock
    AbstractStringValuePairEditorView view;

    AnnotationDefinition annotationDefinition;

    @Before
    public void initTest() {
        annotationDefinition = DriverUtils.buildAnnotationDefinition( CharParamsAnnotation.class );
    }

    @Test
    public void testEditorLoad() {

        CharacterValuePairEditor characterEditor = new CharacterValuePairEditor( view );

        AnnotationValuePairDefinition valuePairDefinition = annotationDefinition.getValuePair( "charParam1" );

        characterEditor.init( valuePairDefinition );

        verify( view, times( 1 ) ).setValuePairLabel( valuePairDefinition.getName() );
        verify( view, times( 1 ) ).showValuePairRequiredIndicator( false );
    }

    @Test
    public void testValidValueChange() {

        CharacterValuePairEditor characterEditor = new CharacterValuePairEditor( view );

        AnnotationValuePairDefinition valuePairDefinition = annotationDefinition.getValuePair( "charParam1" );

        characterEditor.init( valuePairDefinition );

        when( view.getValue() ).thenReturn( "  A   " );
        characterEditor.onValueChange();

        assertTrue( characterEditor.isValid() );
        //the editor internally trims the entered string
        assertEquals( "A", characterEditor.getValue() );
    }

    @Test
    public void testInvalidValidValueChange() {

        CharacterValuePairEditor characterEditor = new CharacterValuePairEditor( view );

        AnnotationValuePairDefinition valuePairDefinition = annotationDefinition.getValuePair( "charParam1" );

        characterEditor.init( valuePairDefinition );

        when( view.getValue() ).thenReturn( "AFD" );
        characterEditor.onValueChange();

        assertFalse( characterEditor.isValid() );
    }
}
