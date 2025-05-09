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
import {
  DMN15__tDefinitions,
  DMN15__tItemDefinition,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { Page } from "@playwright/test";

type AllDrgDataTypes = NonNullable<DMN15__tDefinitions["itemDefinition"]>[0];

export class drgDataType {
  constructor(public page: Page) {}

  public async getDataType(args: { drgDataTypeIndex: number; drdIndex: number }) {
    const drgElement = (await this.getDrgDataType({
      drgDataTypeIndex: args.drgDataTypeIndex,
    })) as DMN15__tItemDefinition & {
      __$$element: string;
    };
    if (drgElement === undefined) {
      throw new Error("Couldn't find DRG data type");
    }
    return drgElement;
  }

  public async getDrgDataType(args: { drgDataTypeIndex: number }): Promise<AllDrgDataTypes | undefined> {
    const textContent = await this.page.getByTestId("storybook--dmn-editor-model").textContent();

    if (textContent === null || textContent === undefined) {
      return;
    }
    return (JSON.parse(textContent) as DmnLatestModel).definitions.itemDefinition?.[args.drgDataTypeIndex];
  }
}
