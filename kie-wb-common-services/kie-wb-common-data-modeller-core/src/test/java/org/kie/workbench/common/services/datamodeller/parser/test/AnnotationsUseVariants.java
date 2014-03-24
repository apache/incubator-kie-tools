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

package org.kie.workbench.common.services.datamodeller.parser.test;

public class AnnotationsUseVariants {

    @TestAnnotation()
    int field1;

    @TestAnnotation
    int field2;

    @TestAnnotation1
    int field3;

    //@TestAnnotation1(method1 = "param1")
    @TestAnnotation1("param1")    // value() method
            int field4;

    @TestAnnotation2(method2 = "e")
    int getField5;

}
