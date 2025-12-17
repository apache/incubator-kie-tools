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

import { Identifier } from "./Identifier";

/**
 * Describe an Identifier Context in FEEL. Each defined identifier have its own context.
 * For example, when user creates a new DecisionNode-1, a variable called "DecisionNode-1" is defined
 * and a new context is created. In this context, we can have:
 * 1. Inner variables: declared inside the Boxed Expression Editor;
 * 2. Input variables: input nodes to DecisionNode-1;
 * 3. The variable: the variable context may declare a Variable that is valid for this context and for its
 * children, for example, a row in a Context Expression inside a Decision Node.
 */
export interface IdentifierContext {
  /**
   * The unique UUID for the variable context.
   */
  uuid: string;

  /**
   * A Variable Context can be child of another context (i.e. the first entry inside a Context Expression defined in
   * Boxed Expression Editor is child of the parent node).
   */
  parent?: IdentifierContext;

  /**
   * The identifier (variable) declared by this context.
   */
  identifier: Identifier;

  /**
   * Children contexts indexed by its unique uuid.
   */
  children: Map<string, IdentifierContext>;

  /**
   * Input identifiers to this context. They are external known identifiers in this context.
   */
  inputIdentifiers: Array<string>;

  /**
   * Dynamic variables are variables only validated during runtime.
   */
  allowDynamicVariables?: boolean;
}
