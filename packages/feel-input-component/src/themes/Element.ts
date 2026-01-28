/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

export enum Element {
  FeelKeyword,
  FeelNumeric,
  FeelBoolean,
  FeelString,
  FeelFunction,
  Variable,
  FunctionCall,
  UnknownVariable,
  FunctionParameterVariable,
  DynamicVariable,

  /**
   * NonColorizedElement is an element that have semantic value, but it doesn't have custom colors.
   * For example: date("2025-12-30").day
   * date = FeelFunction
   * ("2025-12-30") = NonColorizedElement
   * . = dot
   * day = FeelKeyword, in this case, a property of the result of the date("2025-12-30")
   */
  NonColorizedElement,
}
