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

import { FeelSyntacticSymbolNature } from "../../FeelSyntacticSymbolNature";
import { DataType } from "../../DataType";

/**
 * Define a Semantic Token identified by {@link FeelVisitorImpl}.
 */
export class SemanticToken {
  private readonly _startIndex: number;
  private readonly _endIndex: number;
  private readonly _startLine: number;
  private readonly _endLine: number;
  private readonly _symbolNature: FeelSyntacticSymbolNature;
  private readonly _dataTypeReturn: DataType;
  private readonly _text: string;

  constructor(args: {
    startIndex: number;
    endIndex: number;
    startLine: number;
    endLine: number;
    symbolNature: FeelSyntacticSymbolNature;
    dataTypeReturn: DataType;
    text: string;
  }) {
    this._startIndex = args.startIndex;
    this._endIndex = args.endIndex;
    this._symbolNature = args.symbolNature;
    this._dataTypeReturn = args.dataTypeReturn;
    this._startLine = args.startLine;
    this._endLine = args.endLine;
    this._text = args.text;
  }

  get startLine(): number {
    return this._startLine;
  }

  get endLine(): number {
    return this._endLine;
  }

  get text(): string {
    return this._text;
  }

  get startIndex(): number {
    return this._startIndex;
  }

  get endIndex(): number {
    return this._endIndex;
  }

  get symbolNature(): FeelSyntacticSymbolNature {
    return this._symbolNature;
  }

  get dataTypeReturn(): DataType {
    return this._dataTypeReturn;
  }
}
