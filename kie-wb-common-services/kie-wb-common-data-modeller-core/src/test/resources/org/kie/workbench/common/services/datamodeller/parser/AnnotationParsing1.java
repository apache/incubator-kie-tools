/*
 * Copyright 2014 JBoss Inc
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

package org.kie.workbench.common.services.datamodeller.parser;

import org.kie.workbench.common.services.datamodeller.parser.test.TestAnnotation;
import org.kie.workbench.common.services.datamodeller.parser.test.TestAnnotation1;
import org.kie.workbench.common.services.datamodeller.parser.test.TestAnnotation2;
import org.kie.workbench.common.services.datamodeller.parser.test.TestAnnotation3;

/**
 *  Changes on this file can break AnnotationParsing1Test.
 */

@TestAnnotation
@TestAnnotation1("value")
@TestAnnotation2( method1 = "param1", method2 = "param2")
@TestAnnotation3( value = "value", method1 = "param1", method2 = "param2" )
public class AnnotationParsing1 {

    @TestAnnotation()
    public int field1;

    @TestAnnotation
    private int field2;

    public
    @TestAnnotation1
    static int field3;

    @TestAnnotation1("value")
    int field4;

    protected
    @TestAnnotation1( value = "value")
    int field5;

    @TestAnnotation2( method1 = "param1", method2 = "param2")
    int field6;

    @TestAnnotation2(method2 = "param2")
    int field7;

    @TestAnnotation3( value = "value", method1 = "param1", method2 = "param2")
    int field8;

    @TestAnnotation
    @TestAnnotation1("value")
    @TestAnnotation2( method1 = "param1", method2 = "param2")
    @TestAnnotation3( value = "value", method1 = "param1", method2 = "param2" )
    int field9;

}
