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
import { DrgElement } from "./jsonModel/drgElement";
import { Drd } from "./jsonModel/drd";

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

export const STORYBOOK__DMN_EDITOR_MODEL = "div[data-testid='storybook--dmn-editor-model']";

export class JsonModel {
  public drgElements: DrgElement;
  public drd: Drd;

  constructor(public page: Page, public baseURL?: string) {
    this.drgElements = new DrgElement(page);
    this.drd = new Drd(page, this.drgElements);
  }
}
