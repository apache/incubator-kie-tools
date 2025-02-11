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

import { BaseSymbol } from "./BaseSymbol";
import { Type } from "./Type";
import { FeelSyntacticSymbolNature } from "../FeelSyntacticSymbolNature";
import { Identifier } from "../Identifier";

/**
 * Defines a symbol that represents a variable, property or any other known symbol that is not a {@link FunctionSymbol}.
 */
export class IdentifierSymbol extends BaseSymbol {
  private readonly _symbolType: FeelSyntacticSymbolNature | undefined;
  private readonly _symbolSource: Identifier | undefined;
  private readonly _allowDynamicVariables: boolean | undefined;

  constructor(
    id?: string,
    type?: Type,
    variableType?: FeelSyntacticSymbolNature,
    variableSource?: Identifier,
    allowDynamicVariables?: boolean
  ) {
    super(id, type);
    this._symbolType = variableType;
    this._symbolSource = variableSource;
    this._allowDynamicVariables = allowDynamicVariables;
  }

  /**
   * The nature of the symbol. See {@link FeelSyntacticSymbolNature}.
   */
  get symbolType(): FeelSyntacticSymbolNature | undefined {
    return this._symbolType;
  }

  /**
   * The source that originated this symbol.
   */
  get symbolSource(): Identifier | undefined {
    return this._symbolSource;
  }

  /**
   * If it is a symbol where the context allow dynamic variables.
   * Dynamic variables are variables that are validate during runtime and the parser can not determine if they are
   * valid or not.
   * See {@link https://github.com/apache/incubator-kie-tools/pull/2296}
   */
  get allowDynamicVariables(): boolean | undefined {
    return this._allowDynamicVariables;
  }
}
