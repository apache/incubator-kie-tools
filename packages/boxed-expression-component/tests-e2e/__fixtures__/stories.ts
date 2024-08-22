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

type BoxedExpressionTypes = "base" | "nested";

export class Stories {
  constructor(
    public page: Page,
    public baseURL?: string
  ) {
    this.page = page;
    this.baseURL = baseURL;
  }

  public getIframeURL(iframeId: string) {
    return `iframe.html?id=${iframeId}&viewMode=story`;
  }

  public async openBoxedContext(
    type: BoxedExpressionTypes | "readonly" | "installment-calculation" | "customer" = "base"
  ) {
    await this.page.goto(`${this.baseURL}/${this.getIframeURL(`boxed-expressions-context--${type}`)}` ?? "");
  }

  public async openDecisionTable(type: BoxedExpressionTypes | "readonly" | "discount" | "undefined-widths" = "base") {
    await this.page.goto(`${this.baseURL}/${this.getIframeURL(`boxed-expressions-decision-table--${type}`)}` ?? "");
  }

  public async openBoxedFunction(type: BoxedExpressionTypes | "readonly" | "installment-calculation" = "base") {
    await this.page.goto(`${this.baseURL}/${this.getIframeURL(`boxed-expressions-function--${type}`)}` ?? "");
  }

  public async openBoxedInvocation(type: BoxedExpressionTypes | "readonly" | "monthly-installment" = "base") {
    await this.page.goto(`${this.baseURL}/${this.getIframeURL(`boxed-expressions-invocation--${type}`)}` ?? "");
  }

  public async openBoxedList(type: BoxedExpressionTypes | "readonly" | "age-groups" = "base") {
    await this.page.goto(`${this.baseURL}/${this.getIframeURL(`boxed-expressions-list--${type}`)}` ?? "");
  }

  public async openBoxedLiteral(type: BoxedExpressionTypes | "readonly" | "can-drive" = "base") {
    await this.page.goto(`${this.baseURL}/${this.getIframeURL(`boxed-expressions-literal--${type}`)}` ?? "");
  }

  public async openRelation(type: BoxedExpressionTypes | "readonly" | "bigger" | "people" = "base") {
    await this.page.goto(`${this.baseURL}/${this.getIframeURL(`boxed-expressions-relation--${type}`)}` ?? "");
  }

  public async openBoxedFilter(type: BoxedExpressionTypes | "readonly" | "rebooked-flights" = "base") {
    await this.page.goto(`${this.baseURL}/${this.getIframeURL(`boxed-expressions-filter--${type}`)}` ?? "");
  }

  public async openBoxedEvery(type: BoxedExpressionTypes | "readonly" = "base") {
    await this.page.goto(`${this.baseURL}/${this.getIframeURL(`boxed-expressions-every--${type}`)}` ?? "");
  }

  public async openBoxedSome(type: BoxedExpressionTypes | "readonly" = "base") {
    await this.page.goto(`${this.baseURL}/${this.getIframeURL(`boxed-expressions-some--${type}`)}` ?? "");
  }

  public async openBoxedFor(type: BoxedExpressionTypes | "readonly" = "base") {
    await this.page.goto(`${this.baseURL}/${this.getIframeURL(`boxed-expressions-for--${type}`)}` ?? "");
  }

  public async openBoxedConditional(type: BoxedExpressionTypes | "readonly" = "base") {
    await this.page.goto(`${this.baseURL}/${this.getIframeURL(`boxed-expressions-conditional--${type}`)}` ?? "");
  }
}
