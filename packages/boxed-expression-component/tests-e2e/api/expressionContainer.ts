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
import { ContextExpressionElement } from "./expressions/contextExpressionElement";
import { Monaco } from "../__fixtures__/monaco";
import { LiteralExpressionElement } from "./expressions/literalExpressionElement";
import { ExpressionHeader } from "./expressionHeader";
import { SelectExpressionMenu } from "./expressions/selectExpressionMenu";
import { DecisionTableExpressionElement } from "./expressions/decisionTableExpressionElement";
import { FilterExpressionElement } from "./expressions/filterExpressionElement";
import { RelationExpressionElement } from "./expressions/relationExpressionElement";
import { ListExpressionElement } from "./expressions/listExpressionElement";
import { ForExpressionElement } from "./expressions/forExpressionElement";
import { EveryExpressionElement } from "./expressions/everyExpressionElement";
import { SomeExpressionElement } from "./expressions/someExpressionElement";
import { ConditionalExpressionElement } from "./expressions/conditionalExpressionElement";
import { FunctionExpressionElement } from "./expressions/functionExpressionElement";
import { InvocationExpressionElement } from "./expressions/invocationExpressionElement";

export class ExpressionContainer {
  constructor(
    protected locator: Locator,
    protected monaco: Monaco
  ) {}

  get header() {
    return new ExpressionHeader(this.locator);
  }

  public asLiteral() {
    return new LiteralExpressionElement(this.locator, this.monaco);
  }

  public asRelation() {
    return new RelationExpressionElement(this.locator, this.monaco);
  }

  public asContext() {
    return new ContextExpressionElement(this.locator, this.monaco);
  }

  public asDecisionTable() {
    return new DecisionTableExpressionElement(this.locator, this.monaco);
  }

  public asList() {
    return new ListExpressionElement(this.locator, this.monaco);
  }

  public asInvocation() {
    return new InvocationExpressionElement(this.locator, this.monaco);
  }

  public asFunction() {
    return new FunctionExpressionElement(this.locator, this.monaco);
  }

  public asConditional() {
    return new ConditionalExpressionElement(this.locator, this.monaco);
  }

  public asFor() {
    return new ForExpressionElement(this.locator, this.monaco);
  }

  public asEvery() {
    return new EveryExpressionElement(this.locator, this.monaco);
  }

  public asSome() {
    return new SomeExpressionElement(this.locator, this.monaco);
  }

  public asFilter() {
    return new FilterExpressionElement(this.locator, this.monaco);
  }

  get contextMenu() {
    return new ContextMenu(this.locator.nth(0));
  }

  public async isEmpty() {
    return (await this.locator.nth(0).getByTestId("kie-tools--bee--logic-type-selected-header").count()) === 0;
  }
}

export class ExpressionCell {
  constructor(
    private locator: Locator,
    private monaco: Monaco
  ) {}

  public async fill(content: string) {
    return await this.monaco.fill({ monacoParentLocator: this.locator, content: content });
  }

  public get content() {
    return this.locator.nth(0);
  }

  public get contextMenu() {
    return new ContextMenu(this.locator);
  }
}

export class ContextMenu {
  constructor(private locator: Locator) {}

  public async open() {
    await this.locator.nth(0).click({ button: "right" });
  }

  public heading(sectionName: string) {
    return this.locator.page().getByRole("heading", { name: sectionName });
  }

  public option(option: string) {
    return this.locator.page().getByTestId("kie-tools--bee--context-menu-container").getByRole("menuitem", {
      name: option,
      exact: true,
    });
  }

  public button(option: string) {
    return this.locator.page().getByTestId("kie-tools--bee--context-menu-container").getByRole("button", {
      name: option,
      exact: true,
    });
  }

  public radio(option: string) {
    return this.locator.page().getByTestId("kie-tools--bee--context-menu-container").getByRole("radio", {
      name: option,
      exact: true,
    });
  }
}

export class ChildExpression {
  private readonly _expression: ExpressionContainer;

  public constructor(
    private locator: Locator,
    monaco: Monaco
  ) {
    this._expression = new ExpressionContainer(
      this.locator.getByTestId("kie-tools--bee--expression-container").nth(0),
      monaco
    );
  }

  get expression() {
    return this._expression;
  }

  get selectExpressionMenu() {
    return new SelectExpressionMenu(this.locator.getByTestId("kie-tools--bee--expression-container").nth(0));
  }

  get contextMenu() {
    return new ContextMenu(this.locator.getByRole("cell").nth(0));
  }
}

export class IteratorVariable {
  constructor(private locator: Locator) {}

  public async fill(content: string) {
    await this.locator.click();
    await this.locator.getByRole("textbox").fill(content);
    await this.locator.getByRole("textbox").press("Enter");
  }

  get content() {
    return this.locator.nth(0);
  }
}
