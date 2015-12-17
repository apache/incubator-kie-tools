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

import org.kie.workbench.common.services.datamodeller.annotations.PrimitivesAnnotation;

/**
 * This class is intended for different tests. In case you change it be sure the tests still works.
 */

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
public class PrimitivesAnnotationTest implements Serializable {

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
    private String field1;

}
