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

import { SymbolType } from "./SymbolType";

export class FeelVariable {
  private readonly _text: string;
  private readonly _startIndex: number;
  private readonly _variableType: SymbolType;
  private readonly _length: number;

  constructor(startIndex: number, length: number, variableType: SymbolType, text: string) {
    this._startIndex = startIndex;
    this._length = length;
    this._variableType = variableType;
    this._text = text;
  }

  get text(): string {
    return this._text;
  }

  get startIndex(): number {
    return this._startIndex;
  }

  get variableType(): SymbolType {
    return this._variableType;
  }

  get length(): number {
    return this._length;
  }
}
