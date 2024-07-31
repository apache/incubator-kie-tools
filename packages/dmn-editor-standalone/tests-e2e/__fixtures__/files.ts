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

import path from "path";
import { readFile } from "fs/promises";
import { Page } from "@playwright/test";
import prettier from "prettier";

export enum ExternalFile {
  EMPTY_DRD = "files/empty-drd.dmn",
  EMPTY_DMN = "files/empty.dmn",
  LOAN_PRE_QUALIFICATION_DMN = "files/loan-pre-qualification.dmn",
  CAN_DRIVE_DMN = "files/can-drive.dmn",
  FIND_EMPLOYEES_DMN = "files/find-employees.dmn",
  TYPES_DMN = "files/types.dmn",
  SCORECARD_PMML = "files/scorecard.pmml",
}

export class Files {
  constructor(public page: Page) {}

  public async getFile(filename: ExternalFile) {
    return readFile(path.join(__dirname, filename), "utf8");
  }

  public async getFormattedFile(filename: ExternalFile) {
    return prettier.format(await this.getFile(filename), {
      ...(await prettier.resolveConfig(".")),
      parser: "xml",
    });
  }
}
