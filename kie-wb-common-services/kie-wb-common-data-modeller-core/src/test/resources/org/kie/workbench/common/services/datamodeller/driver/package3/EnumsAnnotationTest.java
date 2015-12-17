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
package org.kie.workbench.common.services.datamodeller.driver.package3;

import java.io.Serializable;

import org.kie.workbench.common.services.datamodeller.annotations.ENUM3;
import org.kie.workbench.common.services.datamodeller.annotations.EnumsAnnotation;
import org.kie.workbench.common.services.datamodeller.annotations.TestEnums;

/**
 * This class is intended for different tests. In case you change it be sure the tests still works.
 */

@EnumsAnnotation( enum1Param = TestEnums.ENUM1.VALUE1, enum1ArrayParam = { TestEnums.ENUM1.VALUE1, TestEnums.ENUM1.VALUE2 },
        enum2Param = TestEnums.ENUM2.VALUE1, enum2ArrayParam = { TestEnums.ENUM2.VALUE1, TestEnums.ENUM2.VALUE2 },
        enum3Param = ENUM3.VALUE1, enum3ArrayParam = { ENUM3.VALUE1, ENUM3.VALUE2 }
)
public class EnumsAnnotationTest implements Serializable {

    @EnumsAnnotation( enum1Param = TestEnums.ENUM1.VALUE1, enum1ArrayParam = { TestEnums.ENUM1.VALUE1, TestEnums.ENUM1.VALUE2 },
            enum2Param = TestEnums.ENUM2.VALUE1, enum2ArrayParam = { TestEnums.ENUM2.VALUE1, TestEnums.ENUM2.VALUE2 },
            enum3Param = ENUM3.VALUE1, enum3ArrayParam = { ENUM3.VALUE1, ENUM3.VALUE2 }
    )
    private String field1;

}
