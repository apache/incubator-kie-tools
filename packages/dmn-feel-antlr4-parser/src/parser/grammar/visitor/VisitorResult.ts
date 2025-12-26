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

import { DataType } from "../../DataType";
import { BuiltInTypes } from "../../BuiltInTypes";

export class VisitorResult {
  private readonly _dataType?: DataType;
  private readonly _isTerminalNode: boolean;
  private readonly _text: string;

  constructor(args: { text: string; dataType?: DataType }) {
    this._text = args.text;
    this._isTerminalNode = !args.dataType;
    this._dataType = args.dataType;
  }

  get dataType(): DataType {
    return this._dataType ?? BuiltInTypes.Any;
  }

  get isTerminalNode(): boolean {
    return this._isTerminalNode;
  }

  get text(): string {
    return this._text;
  }

  // pode ser um terminal node
  // OU
  // alguma coisa que retorna algo de um certo DataType

  // E aí vai montando, pq no fim, na real, a expressão retorna uma coisa só

  // Vai populando uma lista de SemanticTokens, conforme vai identificando e percorrendo a árvore.
  // Por exemplo, achou date("123123123").day. Eu sei que "date("123123123")" retorna um date.
  // E eu tbm sei que "day" é uma propriedade existente no date.

  // Na real, cada nodo vai retornar somente UM VisitorResult, mas pode ter N SemanticTokens
  // O VisitorResult pode ser:
  // 1. Terminal Node
  // 2. Algum DataType.
}
