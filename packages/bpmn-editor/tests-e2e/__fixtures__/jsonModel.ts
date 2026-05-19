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

export class JsonModel {
  constructor(
    public page: Page,
    public baseURL?: string
  ) {}

  public async getModel(): Promise<any> {
    const modelElement = this.page.getByTestId("storybook--bpmn-editor-model");
    const modelText = await modelElement.textContent();
    return modelText ? JSON.parse(modelText) : undefined;
  }

  public async getDefinitions(): Promise<any> {
    const model = await this.getModel();
    return model?.definitions;
  }

  public async getProcess(processIndex: number = 0): Promise<any> {
    const definitions = await this.getDefinitions();
    const processes = definitions?.rootElement || definitions?.process;
    return Array.isArray(processes) ? processes[processIndex] : processes;
  }

  public async getFlowElement(args: { processIndex?: number; elementIndex: number }): Promise<any> {
    const process = await this.getProcess(args.processIndex ?? 0);
    return process?.flowElement?.[args.elementIndex];
  }

  public async getDiagram(diagramIndex: number = 0): Promise<any> {
    const definitions = await this.getDefinitions();
    return definitions?.["bpmndi:BPMNDiagram"]?.[diagramIndex];
  }

  public async getPlane(diagramIndex: number = 0): Promise<any> {
    const diagram = await this.getDiagram(diagramIndex);
    return diagram?.["bpmndi:BPMNPlane"];
  }

  public async getShape(args: { diagramIndex?: number; shapeIndex: number }): Promise<any> {
    const plane = await this.getPlane(args.diagramIndex ?? 0);
    return plane?.["di:DiagramElement"]?.[args.shapeIndex];
  }

  public async getBounds(args: { diagramIndex?: number; shapeIndex: number }): Promise<any> {
    const shape = await this.getShape(args);
    return shape?.["dc:Bounds"];
  }
}
