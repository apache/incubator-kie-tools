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

import { Symbol } from "./Symbol";
import { Type } from "./Type";

export interface Scope {
  /**
   * Returns the scope name
   */
  getName(): string;

  /**
   * Returns the enclosing (parent) scope
   */
  getParentScope(): Scope | undefined;

  /**
   * Adds an enclosed (child) scope
   */
  addChildScope: (scope: Scope) => void;

  /**
   * Gets a map of all children scopes.
   * The key of the map is the string name of the
   * children scope and the value is the scope itself.
   */
  getChildScopes(): Map<string, Scope>;

  /**
   * Defines a new symbol in this scope
   */
  define(symbol: Symbol): boolean;

  /**
   * Searches and returns a symbol with the given
   * id if it exists. The search is recursive
   * up, so if a symbol is not found in the current
   * scope, the algorithm searches the parent
   * scopes all the way to the root built-in
   * scope.
   */
  resolve(id: string): Symbol | undefined;

  /**
   * Searches and returns a symbol with the given
   * qualified name if it exists. The search is recursive
   * up, so if a symbol is not found in the current scope,
   * the algorithm searches the parent scopes all the
   * way to the root built-in scope.
   */
  resolve(qualifiedName: string[]): Symbol | undefined;

  /**
   * This method is used during context-aware parsing
   * to find multi-token symbols iteratively. It is used
   * in conjunction with the #followUp method below.
   */
  start: (token: string) => void;

  /**
   * This method is used during context-aware parsing
   * to find multi-token symbols iteratively. It is used
   * in conjunction with the #start method above.
   */
  followUp(token: string, isPredict: boolean): boolean;

  getSymbols(): Map<string, Symbol>;

  getType(): Type | undefined;
}
