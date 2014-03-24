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

import java.util.ArrayList;
import java.util.List;

/**
 *  Changes on this file can break FieldsParsing1Test.
 */
public class FieldParsing1 extends java.lang.Object implements java.io.Serializable {


    public String field1;

    public static String field2 ;

    public static final Integer FIELD3 = new Integer("3")  ;

    transient boolean field4;

    protected   List<String>   field5;

        protected   static List<List<String>> field6 = new ArrayList<List<String>>();


    public    String[]      field7    ;

    public    static    java.lang.String   field8[]  =  new String[] {"value1",  "value2" } ;

        private    static   String  field9 [][][];

    protected List<String>[] field10 = new  List[] {  new ArrayList<String>(), new ArrayList<String>() };

protected int field11    =   11   ;

        protected char field12 = 12,    field13  =  13 ;

    Boolean field14 =   false, field15=true, field16 = !true ;


}
