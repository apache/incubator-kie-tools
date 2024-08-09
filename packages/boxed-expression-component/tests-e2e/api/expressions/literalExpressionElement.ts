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

import { Locator, Page } from "@playwright/test";
import { Monaco } from "../../__fixtures__/monaco";
import { NameAndDataTypeCell } from "../nameAndDataTypeCell";
import { ContextMenu, ExpressionCell } from "../expressionContainer";

export class LiteralExpressionElement {
  constructor(
    public locator: Locator | Page,
    public monaco: Monaco
  ) {}

  public async fill(expression: string) {
    await this.monaco.fill({ monacoParentLocator: this.locator, nth: 0, content: expression });
  }

  get content() {
    return this.locator.getByRole("cell").nth(0);
  }

  get cell() {
    return new ExpressionCell(this.locator.getByRole("cell").nth(0), this.monaco);
  }

  get equalsSignContextMenu() {
    return new ContextMenu(this.locator.getByTestId("kie-tools--equals-sign"));
  }

  get expressionHeaderCell() {
    return new NameAndDataTypeCell(this.locator.getByRole("columnheader"));
  }
}
