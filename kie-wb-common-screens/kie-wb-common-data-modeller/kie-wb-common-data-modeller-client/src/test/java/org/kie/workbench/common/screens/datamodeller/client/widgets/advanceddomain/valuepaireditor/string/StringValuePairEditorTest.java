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
import org.kie.workbench.common.services.datamodeller.annotations.StringParamsAnnotation;
import org.kie.workbench.common.services.datamodeller.core.AnnotationDefinition;
import org.kie.workbench.common.services.datamodeller.core.AnnotationValuePairDefinition;
import org.kie.workbench.common.services.datamodeller.util.DriverUtils;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class StringValuePairEditorTest {

    @GwtMock
    AbstractStringValuePairEditorView view;

    AnnotationDefinition annotationDefinition;

    @Before
    public void initTest() {
        annotationDefinition = DriverUtils.buildAnnotationDefinition( StringParamsAnnotation.class );
    }

    @Test
    public void testEditorLoad() {

        StringValuePairEditor stringEditor = new StringValuePairEditor( view );

        AnnotationValuePairDefinition valuePairDefinition = annotationDefinition.getValuePair( "stringParam1" );

        stringEditor.init( valuePairDefinition );

        verify( view, times( 1 ) ).setValuePairLabel( valuePairDefinition.getName() );
        verify( view, times( 1 ) ).showValuePairRequiredIndicator( false );
    }

    @Test
    public void testValidValueChange() {

        StringValuePairEditor stringEditor = new StringValuePairEditor( view );

        AnnotationValuePairDefinition valuePairDefinition = annotationDefinition.getValuePair( "stringParam1" );

        stringEditor.init( valuePairDefinition );

        when( view.getValue() ).thenReturn( "ABCDE" );
        stringEditor.onValueChange();

        assertTrue( stringEditor.isValid() );
        assertEquals( "ABCDE", stringEditor.getValue() );
    }

}
