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

import java.util.AbstractList;
import java.util.List;

/**
 * Changes on this class can break MethodParsing1Test.
 */
public class MethodParsing1 {

    public MethodParsing1() { this(null); }

    public MethodParsing1(String field1) { this.field1 = field1; }

    public MethodParsing1(int a, int b) { this(a, b, (byte)1); }

    public MethodParsing1(int a, int b, byte c) { super(); }

    public class MethodParsing1Inner1 {

        //buy now we are skipping inner clases
        private int a;

        public int getA() {
            return a;
        }

        public void setA(int a) {
            this.a = a;
        }
    };

    private String field1;

    public String getField1() { return field1; }

    public void setField1(String field1) { this.field1 = field1; }

    private int method1() { return -1; }

    private void method2() {}

    public static java.lang.String method3() { return null; }

    public static final Integer method4() { return null; }

    public void method5(java.lang.Integer param1, int param2) {}

    java.util.List<java.lang.String> method6() { return null;    }

    protected   java.util.AbstractList<String>    method7  ( final int   param1 ,  java.lang.Integer   param2  ,   java.util.List<java.lang.Integer>      param3      ) {    return  null  ;    }

    int method8  ( final int   param1 ,  java.lang.Integer   param2   ) [  ]   [    ] { return null; }

    int method9 ( final Object ...  param1) { return -1;}

    private java.util.AbstractList<Object> method10  (  final java.lang.String param1,  int param2 , List<java.util.List<String>>...param3) { return null; }

    //bound types are not yet recognized
    //public static <T extends Foo & Bar> T getFooBar()


    public class MethodParsing1Inner2 {

        //buy now we are skipping inner clases
        private int a;

        public int getA() {
            return a;
        }

        public void setA(int a) {

            class MethodParsing1BlockClass {

                //buy now we are skipping inner clases
                private int a;

                public int getA() {
                    return a;
                }

                public void setA(int a) {
                    this.a = a;
                }
            };

            this.a = a;
        }
    };

}
