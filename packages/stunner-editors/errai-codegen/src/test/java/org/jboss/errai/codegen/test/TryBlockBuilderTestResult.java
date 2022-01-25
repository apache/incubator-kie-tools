/*
 * Copyright (C) 2011 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.codegen.test;

/**
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public interface TryBlockBuilderTestResult {

  public static final String EMPTY_TRY_FINALLY_BLOCK =
      "     try {\n" +
          " } " +
          " finally {\n" +
          " }\n";

  public static final String EMPTY_TRY_CATCH_BLOCK =
      "     try {\n" +
          " } " +
          " catch (Throwable t) {\n" +
          " }\n";

  public static final String EMPTY_TRY_CATCH_FINALLY_BLOCK =
      "     try {\n" +
          " } " +
          " catch (Throwable t) {\n" +
          " }\n" +
          " finally {\n" +
          " }\n";

  public static final String EMPTY_TRY_MULTIPLE_CATCH_FINALLY_BLOCK =
      "     try {\n" +
          " } " +
          " catch (Exception e) {\n" +
          " }\n" +
          " catch (Throwable t) {\n" +
          " }\n" +
          " finally {\n" +
          " }\n";
  
  public static final String TRY_CATCH_FINALLY_BLOCK =
    "     try {\n" +
        "   throw new Exception();" +
        " } " +
        " catch (Exception e) {\n" +
        "   throw new RuntimeException(e);" +
        " }\n" +
        " finally {\n" +
        "   return 0;" + 
        " }\n";
}
