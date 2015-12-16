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

package org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.numeric;

import com.google.gwtmockito.GwtMock;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.util.NumberType;
import org.kie.workbench.common.services.datamodeller.annotations.ByteParamsAnnotation;
import org.kie.workbench.common.services.datamodeller.annotations.DoubleParamsAnnotation;
import org.kie.workbench.common.services.datamodeller.annotations.FloatParamsAnnotation;
import org.kie.workbench.common.services.datamodeller.annotations.IntParamsAnnotation;
import org.kie.workbench.common.services.datamodeller.annotations.LongParamsAnnotation;
import org.kie.workbench.common.services.datamodeller.annotations.ShortParamsAnnotation;
import org.kie.workbench.common.services.datamodeller.core.AnnotationDefinition;
import org.kie.workbench.common.services.datamodeller.core.AnnotationValuePairDefinition;
import org.kie.workbench.common.services.datamodeller.util.DriverUtils;

public class NumericValuePairEditorBaseTest {

    @GwtMock
    protected NumericValuePairEditorView singleEditorView;

    protected NumericValuePairEditor createEditor( NumberType numberType, String paramName ) {
        NumericValuePairEditor numericEditor = new NumericValuePairEditor( singleEditorView );
        AnnotationDefinition annotationDefinition = createAnnotationDefinition( numberType );
        AnnotationValuePairDefinition valuePairDefinition = annotationDefinition.getValuePair( paramName );
        numericEditor.init( valuePairDefinition );
        return  numericEditor;
    }

    protected AnnotationDefinition createAnnotationDefinition( NumberType numberType ) {
        AnnotationDefinition annotationDefinition = null;

        switch ( numberType ) {
            case BYTE:
                annotationDefinition = createAnnotationDefinition( ByteParamsAnnotation.class );
                break;
            case SHORT:
                annotationDefinition = createAnnotationDefinition( ShortParamsAnnotation.class );
                break;
            case INT:
                annotationDefinition = createAnnotationDefinition( IntParamsAnnotation.class );
                break;
            case LONG:
                annotationDefinition = createAnnotationDefinition( LongParamsAnnotation.class );
                break;
            case FLOAT:
                annotationDefinition = createAnnotationDefinition( FloatParamsAnnotation.class );
                break;
            case DOUBLE:
                annotationDefinition = createAnnotationDefinition( DoubleParamsAnnotation.class );
                break;
        }
        return annotationDefinition;
    }

    protected AnnotationDefinition createAnnotationDefinition( Class<?> clazz ) {
        return DriverUtils.buildAnnotationDefinition( clazz );
    }

}
