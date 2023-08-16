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
public interface SwitchBlockBuilderTestResult {

  public static final String SWITCH_BLOCK_EMPTY =
      "     switch (n) {\n" +
          " } ";

  public static final String SWITCH_BLOCK_INT =
      "     switch (n) {\n" +
          "   case 0: System.out.println(\"0\"); System.out.println(\"break\"); break;" +
          "   case 1: break;" +
          "   default: break;" +
          " } ";

  public static final String SWITCH_BLOCK_INTEGER_NO_DEFAULT =
      "     switch (n) {\n" +
          "   case 0: System.out.println(\"0\"); System.out.println(\"break\"); break;" +
          "   case 1: break;" +
          " } ";

  public static final String SWITCH_BLOCK_ENUM =
      "     switch (t) {\n" +
          "   case A: System.out.println(\"A\"); System.out.println(\"break\"); break;" +
          "   case B: break;" +
          "   default: break;" +
          " } ";

  public static final String SWITCH_BLOCK_INT_FALLTHROUGH =
      "     switch (n) {\n" +
          "   case 0:" +
          "   case 1: System.out.println(\"1\"); System.out.println(\"break\"); break;" +
          " } ";

  public static final String SWITCH_BLOCK_CHAINED_INVOCATION =
      "     switch (str.length()) {\n" +
          "   case 0: System.out.println(\"0\"); System.out.println(\"break\"); break;" +
          "   case 1: break;" +
          "   default: break;" +
          " } ";
  
  public static final String SWITCH_BLOCK_CHAR_CHAINED =
    "     switch (c) {\n" +
        "   case 'a': System.out.println(\"a\"); break;" +
        "   case 'b': break;" +
        "   default: break;" +
        " } ";
}
