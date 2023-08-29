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
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public interface IfBlockBuilderTestResult {

  public static final String EMPTY_IF_BLOCK_NO_RHS =
      "if (str.endsWith(\"abc\")) { }\n";

  public static final String EMPTY_IF_BLOCK_NO_RHS_AND_NEGATION =
      "if (!(str.endsWith(\"abc\"))) { }\n";

  public static final String EMPTY_IF_BLOCK_LITERAL_RHS =
      "if (n == 1) { }\n";

  public static final String EMPTY_IF_BLOCK_NULL_RHS =
      "if (str != null) { }\n";

  public static final String EMPTY_IF_BLOCK_INSTANCE_OF_RHS =
      "if (str instanceof String) { }\n";

  public static final String IF_ELSE_BLOCK_NO_RHS =
      "   if (str.endsWith(\"abc\")) { " +
          " Integer n = 0;\n" +
          "} else {" +
          " \nInteger n = 1;\n" +
          "}\n";

  public static final String IF_ELSE_BLOCK_RHS =
      "   if (n > m) { " +
          " Integer n = 0;\n" +
          "} else {" +
          " \nInteger n = 1;\n" +
          "}\n";

  public static final String IF_ELSEIF_BLOCK_NO_RHS_NESTED =
      "   if (s.endsWith(\"abc\")) {\n" +
          " n = 0;\n" +
          "} else {\n" +
          " if (s.startsWith(\"def\")) { " +
          "   n = 1;\n" +
          " }\n" +
          "}\n";

  public static final String IF_ELSEIF_BLOCK_NO_RHS =
      "   if (s.endsWith(\"abc\")) {\n" +
          " n = 0;\n" +
          "} else if (s.startsWith(\"def\")) { " +
          " n = 1;\n" +
          "}\n";

  public static final String IF_ELSEIF_ELSE_BLOCK_NO_RHS_NESTED =
      "   if (s.endsWith(\"abc\")) {\n" +
          " n = 0;\n" +
          "} else {\n" +
          " if (s.startsWith(\"def\")) { " +
          "   n = 1;\n" +
          " } else { " +
          "   n = 2;\n" +
          " }\n" +
          "}\n";

  public static final String IF_ELSEIF_ELSE_BLOCK_NO_RHS =
      "   if (s.endsWith(\"abc\")) {\n" +
          " n = 0;\n" +
          "} else if (s.startsWith(\"def\")) { " +
          " n = 1;\n" +
          "} else { " +
          " n = 2;\n" +
          "}\n";

  public static final String IF_ELSEIF_ELSE_BLOCK_RHS_NESTED =
      "   if (n > m) {\n" +
          " n = 0;\n" +
          "} else {\n" +
          " if (m > n) { " +
          "   n = 1;\n" +
          " } else { " +
          "   n = 2;\n" +
          " }\n" +
          "}\n";

  public static final String IF_ELSEIF_ELSE_BLOCK_RHS =
      "   if (n > m) {\n" +
          " n = 0;\n" +
          "} else if (m > n) { " +
          " n = 1;\n" +
          "} else if (m == n) { " +
          " n = 2;\n" +
          "} else { " +
          " n = 3;\n" +
          "}\n";

  public static final String IF_ELSEIF_BLOCK_UNCHAINED_NESTED_EXPRESSIONS =
      "   if ((\"foo\" == \"bar\") || ((\"cat\" == \"dog\") && (\"girl\" != \"boy\"))) { " +
          " } " +
          "else if (a && b) {" +
          " System.out.println(a); " +
          "} ";

  public static final String IF_BLOCK_UNCHAINED_WITH_EXPRESSION_USING_NEGATION =
      "   if (a && (!(b))) {" +
            " System.out.println(a); " +
            "} ";

  public static final String IF_BLOCK_UNCHAINED_WITH_EXPRESSION_USING_ARITHMETICS =
      "   if (((a + b) / c) > 1) {" +
          " System.out.println(a); " +
          "} ";
}
