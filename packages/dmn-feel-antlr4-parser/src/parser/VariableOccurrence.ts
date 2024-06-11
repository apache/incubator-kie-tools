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

import { Variable } from "./Variable";
import { FeelVariable } from "./FeelVariable";

export class Expression {
  private readonly _uuid: string;
  private _fullExpression: string;
  private _variables: Array<FeelVariable>;

  constructor(uuid: string, fullExpression?: string) {
    this._uuid = uuid;
    this._variables = new Array<FeelVariable>();
    this._fullExpression = fullExpression ?? "";
  }

  public renameVariable(renamedVariable: Variable, newName: String) {
    // We assume that variables are already ordered by the parser

    let offset = 0;
    for (const variable of this._variables) {
      variable.startIndex += offset;
      if (variable.source != undefined && variable.source === renamedVariable) {
        this.replaceAt(variable.startIndex, renamedVariable.value.length, newName);
        offset += renamedVariable.value.length - newName.length;
      }
    }
  }

  private replaceAt(position: number, oldLength: number, newVariable: String) {
    const part1 = this.fullExpression.substring(0, position);
    const newPart = newVariable;
    const part2 = this.fullExpression.substring(position + oldLength);

    this.fullExpression = part1 + newPart + part2;
  }

  get variables(): Array<FeelVariable> {
    return this._variables;
  }

  set variables(value: Array<FeelVariable>) {
    this._variables = value;
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
