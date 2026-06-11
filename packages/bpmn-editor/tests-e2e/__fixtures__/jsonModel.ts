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
import { BpmnLatestModel } from "@kie-tools/bpmn-marshaller";
import { Normalized } from "@kie-tools/bpmn-editor/dist/normalization/normalize";
export class JsonModel {
  constructor(
    public page: Page,
    public baseURL?: string
  ) {}

  public async getModel(): Promise<Normalized<BpmnLatestModel>> {
    const modelElement = this.page.getByTestId("storybook--bpmn-editor-model");
    const modelText = await modelElement.textContent();
    try {
      if (modelText === null) {
        throw new Error("BPMN Editor - jsonModel - couldn't get modelText");
      }
      return JSON.parse(modelText);
    } catch (error: any) {
      // Just throw the error
      throw new Error(error);
    }
  }

  public async getDefinitions() {
    return (await this.getModel()).definitions;
  }

  public async getProcess() {
    return (await this.getDefinitions()).rootElement?.find((e) => e.__$$element === "process");
  }

  public async getFlowElement(args: { elementIndex: number }) {
    return (await this.getProcess())?.flowElement?.[args.elementIndex];
  }

  public async getDiagram(diagramIndex: number = 0) {
    return (await this.getDefinitions())["bpmndi:BPMNDiagram"]?.[diagramIndex];
  }

  public async getPlane(diagramIndex: number = 0) {
    return (await this.getDiagram(diagramIndex))?.["bpmndi:BPMNPlane"];
  }

  public async getShape(args: { diagramIndex?: number; shapeIndex: number }) {
    return (await this.getPlane(args.diagramIndex ?? 0))?.["di:DiagramElement"]?.[args.shapeIndex];
  }

  public async getBounds(args: { diagramIndex?: number; shapeIndex: number }) {
    const shape = await this.getShape(args);
    if (shape?.__$$element === "bpmndi:BPMNShape") {
      return shape?.["dc:Bounds"];
    }
    return undefined;
  }
}
