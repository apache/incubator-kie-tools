/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.datamodeller.driver;

import org.kie.workbench.common.services.datamodeller.annotations.ClassAnnotation;
import java.util.List;
import java.util.Map;
import java.io.Serializable;
import org.kie.workbench.common.services.datamodeller.annotations.PrimitivesAnnotation;
import org.kie.workbench.common.services.datamodeller.annotations.EnumsAnnotation;
import org.kie.workbench.common.services.datamodeller.annotations.ENUM3;
import org.kie.workbench.common.services.datamodeller.annotations.TestEnums.ENUM1;
import org.kie.workbench.common.services.datamodeller.annotations.TestEnums.ENUM2;
import org.kie.workbench.common.services.datamodeller.annotations.AnnotationValuesAnnotation;

@ClassAnnotation(classParam = List.class, classArrayParam = { Map.class,
        List.class, Serializable.class })
@PrimitivesAnnotation(stringParam = "TheValue", stringArrayParam = { "value1",
        "value2", "value3" })
@EnumsAnnotation(enum3Param = ENUM3.VALUE1, enum1Param = ENUM1.VALUE2, enum2ArrayParam = {
        ENUM2.VALUE1, ENUM2.VALUE2, ENUM2.VALUE3 })
@AnnotationValuesAnnotation(primitivesParam = @org.kie.workbench.common.services.datamodeller.annotations.PrimitivesAnnotation(stringParam = "The Value"), primitivesArrayParam = {
        @org.kie.workbench.common.services.datamodeller.annotations.PrimitivesAnnotation(stringParam = "The Value1"),
        @org.kie.workbench.common.services.datamodeller.annotations.PrimitivesAnnotation(stringParam = "The Value2") })
public class TestAnnotations
{
}
