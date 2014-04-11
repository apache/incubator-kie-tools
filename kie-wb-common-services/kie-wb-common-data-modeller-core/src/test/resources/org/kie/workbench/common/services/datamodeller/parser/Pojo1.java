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

import java.util.List;

import org.kie.api.definition.type.Description;

/**
 * Changes on this class can break Pojo1FiledParsingTest the test.
 */
class Pojo1 {

    int field1;

    java.lang.Integer field2;

    Integer field3;

    List<Integer> field4;

    java.util.List<Integer> field5;

    List<java.lang.Integer> field6;

    java.util.List<java.lang.Integer> field7;


 /*comment1*/
    private /*comment2*/ java.lang.String name  ; public  static  int a  = 3 ,   b =   4         ;

    java.util.List<List<String>> list; }
