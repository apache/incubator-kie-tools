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

import { DmnLatestModel } from "@kie-tools/dmn-marshaller";
import { Page } from "@playwright/test";
import { DrgElements } from "./jsonModel/drgElements";

export enum DataType {
  Undefined = "<Undefined>",
  Any = "Any",
  Boolean = "boolean",
  Context = "context",
  Date = "date",
  DateTime = "date and time",
  DateTimeDuration = "days and time duration",
  Number = "number",
  String = "string",
  Time = "time",
  YearsMonthsDuration = "years and months duration",
}

export class JsonModel {
  public drgElements: DrgElements;

  constructor(public page: Page, public baseURL?: string) {
    this.drgElements = new DrgElements(page, this.getModelDrds);
  }

  public async getModelDrds() {
    const modelContent = await this.page.getByTestId("storybook-backport--dmn-editor-stringfied-model").textContent();
    if (modelContent !== null) {
      try {
        return (JSON.parse(modelContent) as DmnLatestModel)?.definitions?.["dmndi:DMNDI"]?.["dmndi:DMNDiagram"];
      } catch (error) {
        return;
      }
    }
    return;
  }
}
