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

import { Page } from "@playwright/test";
import { BoxedContext, BoxedEvery, BoxedFilter, BoxedFor, BoxedSome, Normalized } from "../api/types";

interface BoxedExpressionComponent {
  expression: string;
  widthsById: Record<string, number[]>;
}

export enum WidthConstants {
  CONTEXT_ENTRY_VARIABLE_MIN_WIDTH = 120,
  DECISION_TABLE_ANNOTATION_MIN_WIDTH = 100,
  DECISION_TABLE_INPUT_MIN_WIDTH = 100,
  DECISION_TABLE_OUTPUT_MIN_WIDTH = 100,
  INVOCATION_PARAMETER_MIN_WIDTH = 120,
  RELATION_EXPRESSION_COLUMN_MIN_WIDTH = 100,
}

export class JsonModel {
  constructor(public page: Page) {}

  public async getDecisionTableId(): Promise<string | undefined> {
    const jsonObject = await this.getBoxedExpressionContent();
    if (jsonObject === undefined) {
      throw new Error("Couldn't find Boxed Expression Content");
    }
    const expression = jsonObject.expression || {};
    return expression["@_id"];
  }

  public async getWidthsById(): Promise<number[]> {
    const jsonObject = await this.getBoxedExpressionContent();
    const decisionTableId = await this.getDecisionTableId();
    if (jsonObject === undefined || decisionTableId === undefined) {
      throw new Error("Couldn't find Boxed Expression Content");
    }
    const widthsById = jsonObject.widthsById || {};
    return widthsById[decisionTableId] || [];
  }

  public async getContextExpression(): Promise<Normalized<BoxedContext>> {
    const content = await this.getBoxedExpressionContent();
    return (content?.expression || {}) as Normalized<BoxedContext>;
  }

  public async getFilterExpression(): Promise<Normalized<BoxedFilter>> {
    const content = await this.getBoxedExpressionContent();
    return (content?.expression || {}) as Normalized<BoxedFilter>;
  }

  public async getEveryExpression(): Promise<Normalized<BoxedEvery>> {
    const content = await this.getBoxedExpressionContent();
    return (content?.expression || {}) as Normalized<BoxedEvery>;
  }

  public async getForExpression(): Promise<Normalized<BoxedFor>> {
    const content = await this.getBoxedExpressionContent();
    return (content?.expression || {}) as Normalized<BoxedFor>;
  }

  public async getSomeExpression(): Promise<Normalized<BoxedSome>> {
    const content = await this.getBoxedExpressionContent();
    return (content?.expression || {}) as Normalized<BoxedSome>;
  }

  private async getBoxedExpressionContent(): Promise<BoxedExpressionComponent | undefined> {
    const textContent = await this.page.getByTestId("storybook--boxed-expression-component").textContent();

    if (textContent === null || textContent === undefined) {
      return;
    }
    return JSON.parse(textContent);
  }
}
