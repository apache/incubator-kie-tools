/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { Locator } from "@playwright/test";
import { Monaco } from "../__fixtures__/monaco";
import { ExpressionCell } from "./expressionContainer";

export class RelationExpressionElement {
  constructor(
    private locator: Locator,
    private monaco: Monaco
  ) {}

  cellAt(param: { column: number; row: number }) {
    return new ExpressionCell(
      this.locator.getByTestId(`expression-row-${param.row - 1}`).getByTestId(`expression-column-${param.column}`),
      this.monaco
    );
  }

  public async fill(args: { startAtCell: number; relationData: any[][] }) {
    let cellNumber = args.startAtCell;
    for (const row of args.relationData) {
      for (const cellData of row) {
        await this.monaco.fill({ monacoParentLocator: this.locator, content: cellData, nth: cellNumber });
        cellNumber++;
      }
    }
  }
}
