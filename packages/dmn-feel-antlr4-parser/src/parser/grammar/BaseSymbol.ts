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
import { Scope } from "./Scope";

/**
 * Define a base symbol.
 * A "symbol" is anything known by the parser.
 */
export abstract class BaseSymbol implements Symbol {
  private readonly id?: string;
  private readonly type?: Type;
  private readonly scope?: Scope;

  protected constructor(id?: string, type?: Type, scope?: Scope) {
    this.id = id;
    this.type = type;
    this.scope = scope;
  }

  /**
   * The ID of the symbol.
   */
  getId(): string | undefined {
    return this.id;
  }

  /**
   * The scope of the symbol.
   */
  getScope(): Scope | undefined {
    return this.scope;
  }

  /**
   * The type of the symbol.
   */
  getType(): Type | undefined {
    return this.type;
  }
}
