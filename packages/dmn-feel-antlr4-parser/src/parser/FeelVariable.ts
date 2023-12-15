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

import { FeelSyntacticSymbolNature } from "./FeelSyntacticSymbolNature";
import { FeelSymbol } from "./FeelSymbol";
import { Variable } from "./Variable";

export class FeelVariable {
  private readonly _text: string;
  private _startIndex: number;
  private readonly _feelSymbolNature: FeelSyntacticSymbolNature;
  private readonly _length: number;
  private readonly _scopeSymbols: FeelSymbol[];
  private readonly _source: Variable | undefined;
  private readonly _startLine: number;
  private readonly _endLine: number;

  constructor(
    startIndex: number,
    length: number,
    startLine: number,
    endLine: number,
    symbolType: FeelSyntacticSymbolNature,
    text: string,
    scopeSymbols?: FeelSymbol[],
    source?: Variable
  ) {
    this._startIndex = startIndex;
    this._length = length;
    this._feelSymbolNature = symbolType;
    this._text = text;
    this._scopeSymbols = scopeSymbols ?? [];
    this._source = source;
    this._startLine = startLine;
    this._endLine = endLine;
  }

  get source(): Variable | undefined {
    return this._source;
  }

  get text(): string {
    return this._text;
  }

  get startIndex(): number {
    return this._startIndex;
  }

  set startIndex(value: number) {
    this._startIndex = value;
  }

  get startLine(): number {
    return this._startLine;
  }

  get endLine(): number {
    return this._endLine;
  }
  get feelSymbolNature(): FeelSyntacticSymbolNature {
    return this._feelSymbolNature;
  }

  get length(): number {
    return this._length;
  }

  get scopeSymbols(): FeelSymbol[] {
    return this._scopeSymbols;
  }
}
