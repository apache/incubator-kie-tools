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
import { ContextExpressionElement } from "./contextExpressionElement";
import { Monaco } from "../__fixtures__/monaco";
import { LiteralExpressionElement } from "./literalExpressionElement";
import { ExpressionHeader } from "./expressionHeader";
import { SelectExpressionMenu } from "./selectExpressionMenu";
import { DecisionTableExpressionElement } from "./decisionTableExpressionElement";
import { FilterExpressionElement } from "./filterExpressionElement";
import { RelationExpressionElement } from "./relationExpressionElement";
import { ListExpressionElement } from "./listExpressionElement";
import { ForExpressionElement } from "./forExpressionElement";
import { EveryExpressionElement } from "./everyExpressionElement";
import { SomeExpressionElement } from "./someExpressionElement";
import { ConditionalExpressionElement } from "./conditionalExpressionElement";
import { FunctionExpressionElement } from "./functionExpressionElement";
import { InvocationExpressionElement } from "./invocationExpressionElement";

export class ExpressionContainer {
  constructor(
    protected locator: Locator,
    protected monaco: Monaco
  ) {}

  get header() {
    return new ExpressionHeader(this.locator);
  }

  asLiteral() {
    return new LiteralExpressionElement(this.locator, this.monaco);
  }

  asRelation() {
    return new RelationExpressionElement(this.locator, this.monaco);
  }

  asContext() {
    return new ContextExpressionElement(this.locator, this.monaco);
  }

  asDecisionTable() {
    return new DecisionTableExpressionElement(this.locator, this.monaco);
  }

  asList() {
    return new ListExpressionElement(this.locator, this.monaco);
  }

  asInvocation() {
    return new InvocationExpressionElement(this.locator, this.monaco);
  }

  asFunction() {
    return new FunctionExpressionElement(this.locator, this.monaco);
  }

  asConditional() {
    return new ConditionalExpressionElement(this.locator, this.monaco);
  }

  asFor() {
    return new ForExpressionElement(this.locator, this.monaco);
  }

  asEvery() {
    return new EveryExpressionElement(this.locator, this.monaco);
  }

  asSome() {
    return new SomeExpressionElement(this.locator, this.monaco);
  }

  asFilter() {
    return new FilterExpressionElement(this.locator, this.monaco);
  }
}

export class ExpressionCell {
  constructor(
    private locator: Locator,
    private monaco: Monaco
  ) {}

  async fill(content: string) {
    return await this.monaco.fill({ monacoParentLocator: this.locator, content: content });
  }

  get content() {
    return this.locator.nth(0);
  }
}

export class ExpressionElementEntry {
  private readonly _expression: ExpressionContainer;

  public constructor(
    private locator: Locator,
    monaco: Monaco
  ) {
    this._expression = new ExpressionContainer(locator, monaco);
  }

  get expression() {
    return this._expression;
  }

  get selectExpressionMenu() {
    return new SelectExpressionMenu(this.locator);
  }
}

export class ExpressionVariable {
  constructor(private locator: Locator) {}

  async fill(content: string) {
    await this.locator.click();
    await this.locator.getByRole("textbox").fill(content);
    await this.locator.getByRole("textbox").press("Enter");
  }

  get content() {
    return this.locator.nth(0);
  }
}
