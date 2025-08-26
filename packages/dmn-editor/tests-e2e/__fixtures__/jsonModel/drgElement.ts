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
  DMN_LATEST__tBusinessKnowledgeModel,
  DMN_LATEST__tDecision,
  DMN_LATEST__tDecisionService,
  DMN_LATEST__tDefinitions,
  DMN_LATEST__tInputData,
  DMN_LATEST__tKnowledgeSource,
} from "@kie-tools/dmn-marshaller";
import { Page } from "@playwright/test";

type AllDrgElements = NonNullable<DMN_LATEST__tDefinitions["drgElement"]>[0];

export class DrgElement {
  constructor(public page: Page) {}

  public async getBkm(args: { drgElementIndex: number; drdIndex: number }) {
    const drgElement = (await this.getDrgElement({
      drgElementIndex: args.drgElementIndex,
    })) as DMN_LATEST__tBusinessKnowledgeModel & {
      __$$element: string;
    };
    if (drgElement === undefined) {
      throw new Error("Couldn't find DRG element");
    }
    return drgElement;
  }

  public async getDecision(args: { drgElementIndex: number; drdIndex: number }) {
    const drgElement = (await this.getDrgElement({
      drgElementIndex: args.drgElementIndex,
    })) as DMN_LATEST__tDecision & {
      __$$element: string;
    };
    if (drgElement === undefined) {
      throw new Error("Couldn't find DRG element");
    }
    return drgElement;
  }

  public async getDecisionService(args: { drgElementIndex: number; drdIndex: number }) {
    const drgElement = (await this.getDrgElement({
      drgElementIndex: args.drgElementIndex,
    })) as DMN_LATEST__tDecisionService & {
      __$$element: string;
    };
    if (drgElement === undefined) {
      throw new Error("Couldn't find DRG element");
    }
    return drgElement;
  }

  public async getInputData(args: { drgElementIndex: number; drdIndex: number }) {
    const drgElement = (await this.getDrgElement({
      drgElementIndex: args.drgElementIndex,
    })) as DMN_LATEST__tInputData & {
      __$$element: string;
    };
    if (drgElement === undefined) {
      throw new Error("Couldn't find DRG element");
    }
    return drgElement;
  }

  public async getKnowledgeSource(args: { drgElementIndex: number; drdIndex: number }) {
    const drgElement = (await this.getDrgElement({
      drgElementIndex: args.drgElementIndex,
    })) as DMN_LATEST__tKnowledgeSource & {
      __$$element: string;
    };
    if (drgElement === undefined) {
      throw new Error("Couldn't find DRG element");
    }
    return drgElement;
  }

  public async getDrgElement(args: { drgElementIndex: number }): Promise<AllDrgElements | undefined> {
    const textContent = await this.page.getByTestId("storybook--dmn-editor-model").textContent();

    if (textContent === null || textContent === undefined) {
      return;
    }
    return (JSON.parse(textContent) as DmnLatestModel).definitions.drgElement?.[args.drgElementIndex];
  }
}
