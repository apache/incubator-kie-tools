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

import { Specification } from "@serverlessworkflow/sdk-typescript";
import { Page } from "@playwright/test";

type AllSwfElements = NonNullable<Specification.States>[0];

export class SwfElement {
  constructor(public page: Page) {}

  public async getCallbackstate(args: { swfElementIndex: number }) {
    const swfElement = (await this.getSwfElement({
      swfElementIndex: args.swfElementIndex,
    })) as Specification.ICallbackstate;
    if (swfElement === undefined) {
      throw new Error("Couldn't find SWF element");
    }
    return swfElement;
  }

  public async getDatabasedswitchstate(args: { swfElementIndex: number }) {
    const swfElement = (await this.getSwfElement({
      swfElementIndex: args.swfElementIndex,
    })) as Specification.IDatabasedswitchstate;
    if (swfElement === undefined) {
      throw new Error("Couldn't find SWF element");
    }
    return swfElement;
  }

  public async getEventbasedswitchstate(args: { swfElementIndex: number }) {
    const swfElement = (await this.getSwfElement({
      swfElementIndex: args.swfElementIndex,
    })) as Specification.IEventbasedswitchstate;
    if (swfElement === undefined) {
      throw new Error("Couldn't find SWF element");
    }
    return swfElement;
  }

  public async getEventstate(args: { swfElementIndex: number }) {
    const swfElement = (await this.getSwfElement({
      swfElementIndex: args.swfElementIndex,
    })) as Specification.IEventstate;
    if (swfElement === undefined) {
      throw new Error("Couldn't find SWF element");
    }
    return swfElement;
  }

  public async getForeachstate(args: { swfElementIndex: number }) {
    const swfElement = (await this.getSwfElement({
      swfElementIndex: args.swfElementIndex,
    })) as Specification.IForeachstate;
    if (swfElement === undefined) {
      throw new Error("Couldn't find SWF element");
    }
    return swfElement;
  }

  public async getInjectstate(args: { swfElementIndex: number }) {
    const swfElement = (await this.getSwfElement({
      swfElementIndex: args.swfElementIndex,
    })) as Specification.IInjectstate;
    if (swfElement === undefined) {
      throw new Error("Couldn't find SWF element");
    }
    return swfElement;
  }

  public async getOperationstate(args: { swfElementIndex: number }) {
    const swfElement = (await this.getSwfElement({
      swfElementIndex: args.swfElementIndex,
    })) as Specification.IOperationstate;
    if (swfElement === undefined) {
      throw new Error("Couldn't find SWF element");
    }
    return swfElement;
  }

  public async getParallelstate(args: { swfElementIndex: number }) {
    const swfElement = (await this.getSwfElement({
      swfElementIndex: args.swfElementIndex,
    })) as Specification.IParallelstate;
    if (swfElement === undefined) {
      throw new Error("Couldn't find SWF element");
    }
    return swfElement;
  }

  public async getSleepstate(args: { swfElementIndex: number }) {
    const swfElement = (await this.getSwfElement({
      swfElementIndex: args.swfElementIndex,
    })) as Specification.ISleepstate;
    if (swfElement === undefined) {
      throw new Error("Couldn't find SWF element");
    }
    return swfElement;
  }

  public async getSwfElement(args: { swfElementIndex: number }): Promise<AllSwfElements | undefined> {
    const textContent = await this.page.getByTestId("storybook--swf-editor-model").textContent();

    if (textContent === null || textContent === undefined) {
      return;
    }
    return (JSON.parse(textContent) as Specification.IWorkflow).states[args.swfElementIndex];
  }
}
