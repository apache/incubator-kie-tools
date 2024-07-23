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
import { Monaco } from "../../__fixtures__/monaco";
import { ChildExpression } from "../expressionContainer";
import { NameAndDataTypeCell } from "../nameAndDataTypeCell";

export class ConditionalExpressionElement {
  constructor(
    private locator: Locator,
    private monaco: Monaco
  ) {}

  get if() {
    return new ChildExpression(
      this.locator
        .getByTestId("kie-tools--bee--expression-row-0")
        .getByTestId("kie-tools--bee--expression-column-2")
        .nth(0),
      this.monaco
    );
  }

  get then() {
    return new ChildExpression(
      this.locator
        .getByTestId("kie-tools--bee--expression-row-1")
        .getByTestId("kie-tools--bee--expression-column-2")
        .nth(0),
      this.monaco
    );
  }

  get else() {
    return new ChildExpression(
      this.locator
        .getByTestId("kie-tools--bee--expression-row-2")
        .getByTestId("kie-tools--bee--expression-column-2")
        .nth(0),
      this.monaco
    );
  }

  get expressionHeaderCell() {
    return new NameAndDataTypeCell(this.locator.getByRole("columnheader"));
  }
}
