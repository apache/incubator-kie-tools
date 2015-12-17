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
import org.kie.workbench.common.services.datamodeller.annotations.MarkerAnnotation;
import org.kie.workbench.common.services.datamodeller.annotations.PrimitivesAnnotation;
import org.kie.workbench.common.services.datamodeller.annotations.TestEnums;

@AnnotationValuesAnnotation( primitivesParam = @PrimitivesAnnotation( stringParam = "\"line1\" \n line2 \\ \n line3" ),
        primitivesArrayParam = { @PrimitivesAnnotation( intParam = 2 ), @PrimitivesAnnotation( intParam = 3 ) },
        enumsParam = @EnumsAnnotation( enum1Param = TestEnums.ENUM1.VALUE2 ),
        enumsArrayParam = { @EnumsAnnotation( enum1Param = TestEnums.ENUM1.VALUE2 ), @EnumsAnnotation( enum1Param = TestEnums.ENUM1.VALUE3 ) },
        classAnnotationParam = @ClassAnnotation( classParam = Set.class ),
        classAnnotationArrayParam = { @ClassAnnotation( classParam = Set.class ), @ClassAnnotation( classParam = Set.class ) }
)
@ClassAnnotation( classParam = java.util.Collection.class,
        classArrayParam = { List.class }
)
@EnumsAnnotation( enum1Param = TestEnums.ENUM1.VALUE2, enum1ArrayParam = { TestEnums.ENUM1.VALUE3 },
        enum2Param = TestEnums.ENUM2.VALUE2, enum2ArrayParam = { TestEnums.ENUM2.VALUE3 }
)
@PrimitivesAnnotation( byteParam = ( byte ) 2, byteArrayParam = { 3, 4 },
        shortParam = 2, shortArrayParam = { 3, 4 },
        stringParam = "2", stringArrayParam = { "3", "4" }
)
@MarkerAnnotation
public class AnnotationsUpdateTestResult {

}
