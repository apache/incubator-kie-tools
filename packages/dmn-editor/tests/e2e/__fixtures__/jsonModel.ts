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
  DMN15__tBusinessKnowledgeModel,
  DMN15__tDecision,
  DMN15__tDecisionService,
  DMN15__tInputData,
  DMNDI15__DMNShape,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { Page } from "@playwright/test";
import { NodeType } from "./nodes";

export class JsonModel {
  constructor(public page: Page, public baseURL?: string) {}

  public async getModelContent(): Promise<DmnLatestModel | undefined> {
    const modelContent = await this.page.getByTestId("storybook-backport--dmn-editor-stringfied-model").textContent();
    if (modelContent !== null) {
      try {
        return JSON.parse(modelContent) as DmnLatestModel;
      } catch (error) {
        return;
      }
    }
    return;
  }

  public async getModelDrgElements() {
    return (await this.getModelContent())?.definitions?.drgElement;
  }

  public async getModelDrds() {
    return (await this.getModelContent())?.definitions?.["dmndi:DMNDI"]?.["dmndi:DMNDiagram"];
  }

  public async getDrgElementName(args: { name: string }) {
    return (await this.findDrgElement({ name: args.name }))?.["@_name"];
  }

  public async getDrgElementTypeRef(args: { name: string; type: NodeType }) {
    return (await this.getDrgElementVariable({ name: args.name, type: args.type }))?.["@_typeRef"];
  }

  public async getDrgElementVariable(args: { name: string; type: NodeType }) {
    const drgElement = await this.findDrgElement({ name: args.name });
    switch (args.type) {
      case NodeType.INPUT_DATA:
        return (drgElement as DMN15__tInputData).variable;
      case NodeType.DECISION:
        return (drgElement as DMN15__tDecision).variable;
      case NodeType.BKM:
        return (drgElement as DMN15__tBusinessKnowledgeModel).variable;
      case NodeType.DECISION_SERVICE:
        return (drgElement as DMN15__tDecisionService).variable;
      default:
        throw new Error("Invalid DRG element");
    }
  }

  public async getDrgElementBoundsOnDrd(args: { drgElementName: string; drgNodeType: NodeType; drdName: string }) {
    const bounds = (
      await this.findDrgShapeOnDrd({
        drgShapeName: args.drgElementName,
        drdName: args.drdName,
      })
    )["dc:Bounds"];
    return {
      x: bounds?.["@_x"] ?? 0,
      y: bounds?.["@_y"] ?? 0,
      height: bounds?.["@_height"],
      width: bounds?.["@_width"],
    };
  }

  public async getDrgElementPositionOnDrd(args: { drgElementName: string; drgNodeType: NodeType; drdName: string }) {
    const bounds = await this.getDrgElementBoundsOnDrd({
      drgElementName: args.drgElementName,
      drgNodeType: args.drgNodeType,
      drdName: args.drdName,
    });
    return { x: bounds.x, y: bounds.y };
  }

  private async waitForModelToBeUpdated(args: { name: string }) {
    return this.page.waitForFunction((drgElementName) => {
      const model = document.querySelector(
        "div[data-testid='storybook-backport--dmn-editor-stringfied-model']"
      )?.textContent;
      if (model) {
        return (JSON.parse(model) as DmnLatestModel).definitions.drgElement?.find(
          (e) => e["@_name"] === drgElementName
        );
      }
      return;
    }, args.name);
  }

  private async findDrgElement(args: { name: string }) {
    return (await this.waitForModelToBeUpdated({ name: args.name })).jsonValue();
  }

  private async findDrgShapeOnDrd(args: { drgShapeName: string; drdName: string }) {
    const drgElement = await this.findDrgElement({ name: args.drgShapeName });
    const drd = (await this.getModelDrds())?.find((e) => e["@_name"] === args.drdName);
    return drd?.["dmndi:DMNDiagramElement"]?.find(
      (e) => e["@_dmnElementRef"] === drgElement?.["@_id"]
    ) as DMNDI15__DMNShape;
  }
}
