/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.services.datamodeller.driver.package3;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kie.workbench.common.services.datamodeller.annotations.AnnotationValuesAnnotation;
import org.kie.workbench.common.services.datamodeller.annotations.ClassAnnotation;
import org.kie.workbench.common.services.datamodeller.annotations.ENUM3;
import org.kie.workbench.common.services.datamodeller.annotations.EnumsAnnotation;
import org.kie.workbench.common.services.datamodeller.annotations.PrimitivesAnnotation;
import org.kie.workbench.common.services.datamodeller.annotations.TestEnums;

/**
 * This class is the input for the annotations update test. If this class is modified, then AnnotationsUpdateTestResult
 * should probably modified too.
 */

@AnnotationValuesAnnotation( primitivesParam = @PrimitivesAnnotation( stringParam = "1" ),
        primitivesArrayParam = { @PrimitivesAnnotation( intParam = 1 ), @PrimitivesAnnotation( intParam = 2 ) },
        enumsParam = @EnumsAnnotation( enum1Param = TestEnums.ENUM1.VALUE1 ),
        enumsArrayParam = { @EnumsAnnotation( enum1Param = TestEnums.ENUM1.VALUE1 ), @EnumsAnnotation( enum1Param = TestEnums.ENUM1.VALUE2 ) },
        classAnnotationParam = @ClassAnnotation( classParam = Map.class ),
        classAnnotationArrayParam = { @ClassAnnotation( classParam = Map.class ), @ClassAnnotation( classParam = Set.class ) }
)
@ClassAnnotation( classParam = java.util.List.class,
        classArrayParam = { List.class, Collection.class, Map.class, Set.class }
)
@EnumsAnnotation( enum1Param = TestEnums.ENUM1.VALUE1, enum1ArrayParam = { TestEnums.ENUM1.VALUE1, TestEnums.ENUM1.VALUE2 },
        enum2Param = TestEnums.ENUM2.VALUE1, enum2ArrayParam = { TestEnums.ENUM2.VALUE1, TestEnums.ENUM2.VALUE2 },
        enum3Param = ENUM3.VALUE1, enum3ArrayParam = { ENUM3.VALUE1, ENUM3.VALUE2 }
)
@PrimitivesAnnotation( byteParam = ( byte ) 1, byteArrayParam = { 1, 2 },
        shortParam = 1, shortArrayParam = { 1, 2 },
        intParam = 1, intArrayParam = { 1, 2 },
        longParam = 1, longArrayParam = { 1, 2 },
        floatParam = 1, floatArrayParam = { 1, 2 },
        doubleParam = 1, doubleArrayParam = { 1, 2 },
        booleanParam = true, booleanArrayParam = { true, true },
        charParam = '1', charArrayParam = { '1', '2' },
        stringParam = "1", stringArrayParam = { "1", "2" }
)
public class AnnotationsUpdateTest {

}
