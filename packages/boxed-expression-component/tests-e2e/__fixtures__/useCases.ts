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

type FindEmployees = "employees" | "find-employees-by-knowledge" | "find-by-employees";

interface LoanOriginationsPaths {
  "application-risk-score": "";
  "required-monthly-installment": "";
  "bureau-strategy-decision-service":
    | "bureau-call-type"
    | "eligibility"
    | "pre-bureau-affordability"
    | "pre-bureau-risk-category"
    | "strategy";
  functions: "affordability-calculation" | "installment-calculation";
  "routing-decision-service": "post-bureau-affordability" | "post-bureau-risk-category" | "routing";
}

export class UseCases {
  constructor(public page: Page, public baseURL?: string) {
    this.page = page;
    this.baseURL = baseURL;
  }

  public getIframeURL(iframeId: string) {
    return `iframe.html?id=${iframeId}&viewMode=story`;
  }

  public async openCanDrive() {
    await this.page.goto(`${this.baseURL}/${this.getIframeURL(`use-cases-can-drive--expression`)}` ?? "");
  }

  public async openFindEmployees(type: FindEmployees) {
    await this.page.goto(`${this.baseURL}/${this.getIframeURL(`use-cases-find-employees--${type}`)}` ?? "");
  }

  public async openLoanOriginations<Path extends keyof LoanOriginationsPaths>(
    path: Path,
    subpath?: LoanOriginationsPaths[Path]
  ) {
    if (subpath) {
      await this.page.goto(
        `${this.baseURL}/${this.getIframeURL(`use-cases-loan-originations-${path}-${subpath}--expression`)}` ?? ""
      );
    } else {
      await this.page.goto(
        `${this.baseURL}/${this.getIframeURL(`use-cases-loan-originations-${path}--expression`)}` ?? ""
      );
    }
  }
}
