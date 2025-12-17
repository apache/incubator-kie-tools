/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

import { Identifier } from "./Identifier";
import { FeelIdentifiedSymbol } from "./FeelIdentifiedSymbol";
import { ExpressionSource } from "./IdentifiersRepository";

export class Expression {
  private readonly _uuid: string;
  private _fullExpression: string;
  private _identifiersOfTheExpression: Array<FeelIdentifiedSymbol>;
  private source: ExpressionSource;

  constructor(uuid: string, source: ExpressionSource) {
    this._uuid = uuid;
    this._identifiersOfTheExpression = new Array<FeelIdentifiedSymbol>();
    this._fullExpression = source.text?.__$$text ?? "";
    this.source = source;
  }

  public applyChangesToExpressionSource() {
    if (this.source.text) {
      this.source.text = { __$$text: this._fullExpression };
    }
  }

  public renameIdentifier(identifier: Identifier, newName: String) {
    // It is safe to assume that identifiers are already ordered by the parser.
    let offset = 0;
    for (const feelIdentifiedSymbol of this._identifiersOfTheExpression) {
      feelIdentifiedSymbol.startIndex += offset;
      if (feelIdentifiedSymbol.source != undefined && feelIdentifiedSymbol.source === identifier) {
        this.replaceAt(feelIdentifiedSymbol.startIndex, identifier.value.length, newName);
        offset += newName.length - identifier.value.length;
      }
    }
  }

  private replaceAt(position: number, oldLength: number, newVariable: String) {
    const part1 = this.fullExpression.substring(0, position);
    const newPart = newVariable;
    const part2 = this.fullExpression.substring(position + oldLength);

    this.fullExpression = part1 + newPart + part2;
  }

  get identifiersOfTheExpression(): Array<FeelIdentifiedSymbol> {
    return this._identifiersOfTheExpression;
  }

  set identifiersOfTheExpression(value: Array<FeelIdentifiedSymbol>) {
    this._identifiersOfTheExpression = value;
  }

  get fullExpression(): string {
    return this._fullExpression;
  }

  set fullExpression(value: string) {
    this._fullExpression = value;
  }

  get uuid(): string {
    return this._uuid;
  }
}
