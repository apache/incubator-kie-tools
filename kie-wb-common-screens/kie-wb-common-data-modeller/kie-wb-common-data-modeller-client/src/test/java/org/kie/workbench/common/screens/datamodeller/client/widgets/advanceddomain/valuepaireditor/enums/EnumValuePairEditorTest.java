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
import java.util.List;

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
import org.uberfire.commons.data.Pair;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class EnumValuePairEditorTest {

    @GwtMock
    EnumValuePairEditorView view;

    List<Pair<String, String>> enumOptions;

    AnnotationDefinition annotationDefinition;

    @Before
    public void initTest() {
        enumOptions = new ArrayList<Pair<String, String>>();
        enumOptions.add( new Pair<String, String>( TestEnums.ENUM1.VALUE1.name(), TestEnums.ENUM1.VALUE1.name() ) );
        enumOptions.add( new Pair<String, String>( TestEnums.ENUM1.VALUE2.name(), TestEnums.ENUM1.VALUE2.name() ) );
        enumOptions.add( new Pair<String, String>( TestEnums.ENUM1.VALUE3.name(), TestEnums.ENUM1.VALUE3.name() ) );

        annotationDefinition = DriverUtils.buildAnnotationDefinition( EnumParamsAnnotation.class );
    }

    @Test
    public void testEditorLoad() {

        EnumValuePairEditor enumEditor = new EnumValuePairEditor( view );

        AnnotationValuePairDefinition valuePairDefinition = annotationDefinition.getValuePair( "enumParam1" );

        enumEditor.init( valuePairDefinition );

        verify( view, times( 1 ) ).initOptions( enumOptions );
        verify( view, times( 1 ) ).setValuePairLabel( valuePairDefinition.getName() );
        verify( view, times( 1 ) ).showValuePairRequiredIndicator( false );
    }

    @Test
    public void testValueChange() {

        EnumValuePairEditor enumEditor = new EnumValuePairEditor( view );

        AnnotationValuePairDefinition valuePairDefinition = annotationDefinition.getValuePair( "enumParam1" );

        enumEditor.init( valuePairDefinition );

        when( view.getSelectedValue() ).thenReturn( TestEnums.ENUM1.VALUE3.name() );
        enumEditor.onValueChange();

        assertTrue( enumEditor.isValid() );
        assertEquals( TestEnums.ENUM1.VALUE3.name(), enumEditor.getValue() );
    }
}
