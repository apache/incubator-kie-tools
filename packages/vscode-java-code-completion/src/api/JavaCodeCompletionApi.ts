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

import { JavaCodeCompletionAccessor } from "./JavaCodeCompletionAccessor";
import { JavaCodeCompletionClass } from "./JavaCodeCompletionClass";

export interface JavaCodeCompletionApi {
  /**
   * Return a list of accessors for a given class. If the class does not exist or if it can't find anything
   * with
   * @param fqcn Class name in FQCN format
   * @param query A string query that matches accessor names.
   * @returns A List of accessors or empty list if nothing found
   */
  getAccessors(fqcn: string, query: string): Promise<JavaCodeCompletionAccessor[]>;
  /**
   * Return a list of classes for given query. If the query is empty or there is not matching result,
   * an empty list is returned
   * @param query A class name or a FQCN string
   * @returns A list of Classes or empty list if nothing found
   */
  getClasses(query: string): Promise<JavaCodeCompletionClass[]>;
  /**
   * Returns if the language server is available or not.
   */
  isLanguageServerAvailable(): Promise<boolean>;
}
