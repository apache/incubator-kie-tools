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
import { DMNDI15__DMNDiagram, DMNDI15__DMNShape } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { Page } from "@playwright/test";
import { DrgElement } from "./drgElement";

export class Drd {
  constructor(public page: Page, public drgElement: DrgElement) {}

  public async getDrgElementBoundsOnDrd(args: { drgElementIndex: number; drdIndex: number }) {
    const drd = await this.getDrd({ drdIndex: args.drdIndex });
    return (drd?.["dmndi:DMNDiagramElement"]?.[args.drgElementIndex] as DMNDI15__DMNShape)?.["dc:Bounds"];
  }

  private async getDrd(args: { drdIndex: number }): Promise<DMNDI15__DMNDiagram | undefined> {
    const textContent = await this.page.getByTestId("storybook--dmn-editor-model").textContent();

    if (textContent === null || textContent === undefined) {
      return;
    }
    return (JSON.parse(textContent) as DmnLatestModel).definitions?.["dmndi:DMNDI"]?.["dmndi:DMNDiagram"]?.[
      args.drdIndex
    ];
  }
}
