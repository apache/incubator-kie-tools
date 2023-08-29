/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.jboss.errai.codegen.test;

/**
 * Expected test results for the {@link LoopBuilderTest}
 * 
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public interface LoopBuilderTestResult {

  public static final String FOREACH_STRING_IN_LIST =
      "   for (String element : list) {" +
          "\n}";

  public static final String FOREACH_STRING_IN_LIST_NOT_NULL =
      "   if (list != null) {\n" +
      "     for (String element : list) {" +
            "\n}" +
          "\n}";
  
  public static final String FOREACH_OBJECT_IN_LIST_NOT_NULL =
      "   if (list != null) {\n" +
      "     for (Object element : list) {" +
            "\n}" +
          "\n}";
  
  public static final String FOREACH_STRING_IN_ARRAY_ONE_STATEMENT =
      "   for (String element : list) {" +
          " \nnew String();" +
          "\n}";

  public static final String FOREACH_OBJECT_IN_LIST_TWO_STATEMENTS =
      "   for (Object element : list) {" +
          " \nnew String();" +
          " \nnew Object();" +
          "\n}";

  public static final String FOREACH_OBJECT_IN_LIST =
      "   for (Object element : list) {" +
          "\n}";

  public static final String FOREACH_NESTED_STRING_IN_LIST =
      "     for (String element : list) {" +
          " \nfor (String anotherElement : anotherList) {" +
          "   \nnew String();" +
          " \n}" +
          "\n}";

  public static final String FOREACH_KEYSET_LOOP =
      "   for (Object key : map.keySet()) {" +
          " System.out.println(key);" +
          "\n}";

  public static final String FOREACH_LITERAL_STRING_ARRAY =
      "   for (String s : new String[] {\"s1\", \"s2\"}) {" +
          " \ns.getBytes();" +
          "\n}";

  public static final String WHILE_EMPTY =
      "   while (b) { }";

  public static final String WHILE_WITH_BODY =
      "   while (b) {\nb = false;\n}";

  public static final String WHILE_RHS_NULL_EMPTY =
      "   while (str != null) { }";

  public static final String WHILE_RHS_EMPTY =
      "   while (str.length() >= 2) { }";

  public static final String WHILE_NESTED_EMPTY =
      "   while ((str != null) && (str.length() > 0)) { }";

  public static final String WHILE_NESTED_LOOPS =
      "   while (str != null) { " +
          " while (str2 != null) {" +
          " }\n" +
          "}";

  public static final String FOR_NO_INITIALIZER_NO_COUNTING_EXP_EMPTY =
      "     for (; i < 100; ) { }";

  public static final String FOR_INITIALIZER_NO_COUNTING_EXP_EMPTY =
      "     for (i = 0; i < 100; ) { }";

  public static final String FOR_INITIALIZER_COUNTING_EXP_EMPTY =
      "     for (i = 0; i < 100; i += 1) { }";

  public static final String FOR_CHAINED_INITIALIZER_NO_COUNTING_EXP_EMPTY =
      "     for (i = 0; i < 100; ) { }";

  public static final String FOR_CHAINED_INITIALIZER_COUNTING_EXP_EMPTY =
      "     for (i = 0; i < 100; i += 1) { }";

  public static final String FOR_DECLARE_INITIALIZER_COUNTING_EXP =
      "     for (int i = 0; i < 100; i += 1) { System.out.println(i); }";

  public static final String DOWHILE_SIMPLE_EXPRESSION_NO_OP =
      "     do { b = false; } while (b);";

  public static final String DOWHILE_SIMPLE_EXPRESSION =
      "     do { n = 1; } while (n >= 1);";

  public static final String DOWHILE_NESTED_EXPRESSION =
      "     do { System.out.println(str); } while ((str != null) && (str.length() > 0));";

  public static final String LOOP_WITH_CONTINUE =
      "     if (i > 100) {" +
          "   for (i = 0; i < 100; i += 1) { " +
          "     if (i == 50) {" +
          "       continue;" +
          "     }" +
          "   }\n" +
          "\n}";

  public static final String LOOP_WITH_CONTINUE_AND_LABEL =
      "     if (i > 100) {" +
          " label:" +
          "   for (i = 0; i < 100; i += 1) { " +
          "     if (i == 50) {" +
          "       continue label;" +
          "     }" +
          "   }\n" +
          "\n}";

  public static final String LOOP_WITH_BREAK =
      "     if (i > 100) {" +
          "   for (i = 0; i < 100; i += 1) { " +
          "     if (i == 50) {" +
          "       break;" +
          "     }" +
          "   }\n" +
          "\n}";

  public static final String LOOP_WITH_BREAK_AND_LABEL =
      "     if (i > 100) {" +
          " label:" +
          "   for (i = 0; i < 100; i += 1) { " +
          "     if (i == 50) {" +
          "       break label;" +
          "     }" +
          "   }\n" +
          "\n}";
}
