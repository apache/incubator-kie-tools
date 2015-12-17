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

package org.kie.workbench.common.services.datamodeller.parser.test;

public class AnnotationsUseVariants {

    @TestAnnotation()
    int field1;

    @TestAnnotation
    int field2;

    @TestAnnotation1
    int field3;

    @TestAnnotation1("value")
    int field4;

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
