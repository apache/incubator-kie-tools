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

import { Scope } from "./Scope";
import { Symbol } from "./Symbol";
import { Type } from "./Type";
import { Expression } from "../Expression";
import { FeelSyntacticSymbolNature } from "../FeelSyntacticSymbolNature";

/**
 * Defines a symbol that represents a function or a method.
 */
export class FunctionSymbol implements Symbol {
  private readonly id;

  constructor(id: string) {
    this.id = id;
  }

  getId(): string | undefined {
    return this.id;
  }

  getType(): Type | undefined {
    return {
      source: {
        value: this.id,
        feelSyntacticSymbolNature: FeelSyntacticSymbolNature.Unknown,
        expressionsThatUseTheIdentifier: new Map<string, Expression>(),
      },
      name: this.id,
    };
  }

  /**
   * Symbols and Functions does not define scopes, so it returns undefined.
   */
  getScope(): Scope | undefined {
    return undefined;
  }
}
