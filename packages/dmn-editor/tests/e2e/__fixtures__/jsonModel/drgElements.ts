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
  DMN15__tDefinitions,
  DMN15__tInputData,
  DMN15__tKnowledgeSource,
  DMNDI15__DMNDiagram,
  DMNDI15__DMNShape,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { Page } from "@playwright/test";
import { STORYBOOK__DMN_EDITOR_MODEL } from "../jsonModel";

type AllDrgElements = NonNullable<DMN15__tDefinitions["drgElement"]>[0];

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

export class DrgElements {
  constructor(public page: Page) {}

  public async getBkm(args: { name: string; drdName: string }) {
    const drgElement = (await this.getDrgElement({ name: args.name })) as DMN15__tBusinessKnowledgeModel & {
      __$$element: string;
    };
    if (drgElement === undefined) {
      throw new Error("Couldn't find DRG element");
    }

    const drgElementBounds = await this.getDrgElementBoundsOnDrd({
      drgElementId: drgElement["@_id"] ?? "",
      drdName: args.drdName,
    });

    const {
      __$$element,
      "@_id": id,
      "@_label": label,
      "@_name": name,
      authorityRequirement,
      description,
      encapsulatedLogic,
      extensionElements,
      knowledgeRequirement,
      variable,
      ...undesiredProperties
    } = drgElement;

    return {
      // TODO: Check for relationships and encapsulated logic
      // authorityRequirement: authorityRequirement?.map((e) => ({
      //   requiredDecision: e.requiredDecision?.["@_href"],
      //   requiredInput: e.requiredInput?.["@_href"],
      //   requiredAuthority: e.requiredAuthority?.["@_href"],
      // })),
      // knowledgeRequirement: knowledgeRequirement?.map((e) => ({
      //   requiredKnowledge: e.requiredKnowledge?.["@_href"],
      // })),
      // drgElement.encapsulatedLogic

      bounds: {
        ...drgElementBounds,
      },
      description: drgElement.description?.__$$text,
      extensionElements: drgElement.extensionElements?.["kie:attachment"]?.map((e) => ({
        name: e["@_name"],
        url: e["@_url"],
      })),
      name: drgElement.variable?.["@_name"],
      variable: {
        name: variable?.["@_name"],
        typeRef: variable?.["@_typeRef"],
        description: variable?.description?.__$$text,
      },

      undesiredProperties: this.getUndesiredPropertiesKeys(undesiredProperties),
    };
  }

  public async getDecision(args: { name: string; drdName: string }) {
    const drgElement = (await this.getDrgElement({ name: args.name })) as DMN15__tDecision & {
      __$$element: string;
    };
    if (drgElement === undefined) {
      throw new Error("Couldn't find DRG element");
    }

    const drgElementBounds = await this.getDrgElementBoundsOnDrd({
      drgElementId: drgElement["@_id"] ?? "",
      drdName: args.drdName,
    });

    const {
      __$$element,
      "@_id": id,
      "@_label": label,
      "@_name": name,
      allowedAnswers,
      authorityRequirement,
      decisionMaker,
      decisionOwner,
      description,
      expression,
      extensionElements,
      impactedPerformanceIndicator,
      informationRequirement,
      knowledgeRequirement,
      question,
      supportedObjective,
      usingProcess,
      usingTask,
      variable,
      ...undesiredProperties
    } = drgElement;

    return {
      // TODO: Check for relationships and expression
      // expression
      // authorityRequirement: authorityRequirement?.map((e) => ({
      //   requiredDecision: e.requiredDecision?.["@_href"],
      //   requiredInput: e.requiredInput?.["@_href"],
      //   requiredAuthority: e.requiredAuthority?.["@_href"],
      // })),
      // informationRequirement: informationRequirement?.map((e) => ({
      //   requiredDecision: e.requiredDecision?.["@_href"],
      //   requiredInput: e.requiredInput?.["@_href"],
      // })),
      // knowledgeRequirement: knowledgeRequirement?.map((e) => ({
      //   requiredKnowledge: e.requiredKnowledge?.["@_href"],
      // })),

      allowedAnswers: allowedAnswers?.__$$text,
      bounds: {
        ...drgElementBounds,
      },
      decisionMaker: decisionMaker?.map((e) => e["@_href"]),
      decisionOwner: decisionOwner?.map((e) => e["@_href"]),
      description: description?.__$$text,
      extensionElements: extensionElements?.["kie:attachment"]?.map((e) => ({
        name: e["@_name"],
        url: e["@_url"],
      })),
      impactedPerformanceIndicator: impactedPerformanceIndicator?.map((e) => e["@_href"]),
      label: label,
      name: name,
      question: question?.__$$text,
      supportedObjective: supportedObjective?.map((e) => e["@_href"]),
      usingProcess: usingProcess?.map((e) => e["@_href"]),
      usingTask: usingTask?.map((e) => e["@_href"]),
      variable: {
        name: variable?.["@_name"],
        typeRef: variable?.["@_typeRef"],
        description: variable?.description?.__$$text,
      },

      undesiredProperties: this.getUndesiredPropertiesKeys(undesiredProperties),
    };
  }

  public async getDecisionService(args: { name: string; drdName: string }) {
    const drgElement = (await this.getDrgElement({ name: args.name })) as DMN15__tDecisionService & {
      __$$element: string;
    };
    if (drgElement === undefined) {
      throw new Error("Couldn't find DRG element");
    }

    const drgElementBounds = await this.getDrgElementBoundsOnDrd({
      drgElementId: drgElement["@_id"] ?? "",
      drdName: args.drdName,
    });

    const {
      __$$element,
      "@_id": id,
      "@_label": label,
      "@_name": name,
      description,
      encapsulatedDecision,
      extensionElements,
      inputData,
      inputDecision,
      outputDecision,
      variable,
      ...undesiredProperties
    } = drgElement;

    return {
      bounds: {
        ...drgElementBounds,
      },
      description: description?.__$$text,
      encapsulatedDecision: encapsulatedDecision?.map((e) => e["@_href"]),
      inputData: inputData?.map((e) => e["@_href"]),
      inputDecision: inputDecision?.map((e) => e["@_href"]),
      label: label,
      name: name,
      outputDecision: outputDecision?.map((e) => e["@_href"]),
      variable: {
        name: variable?.["@_name"],
        typeRef: variable?.["@_typeRef"],
        description: variable?.description?.__$$text,
      },

      undesiredProperties: this.getUndesiredPropertiesKeys(undesiredProperties),
    };
  }

  public async getInputData(args: { name: string; drdName: string }) {
    const drgElement = (await this.getDrgElement({ name: args.name })) as DMN15__tInputData & {
      __$$element: string;
    };
    if (drgElement === undefined) {
      throw new Error("Couldn't find DRG element");
    }

    const drgElementBounds = await this.getDrgElementBoundsOnDrd({
      drgElementId: drgElement["@_id"] ?? "",
      drdName: args.drdName,
    });

    const {
      __$$element,
      "@_id": id,
      "@_label": label,
      "@_name": name,
      description,
      extensionElements,
      variable,
      ...undesiredProperties
    } = drgElement;

    return {
      bounds: {
        ...drgElementBounds,
      },
      description: description?.__$$text,
      extensionElements: extensionElements?.["kie:attachment"]?.map((e) => ({
        name: e["@_name"],
        url: e["@_url"],
      })),
      name: name,
      label: label,
      variable: {
        name: variable?.["@_name"],
        typeRef: variable?.["@_typeRef"],
      },

      undesiredProperties: this.getUndesiredPropertiesKeys(undesiredProperties),
    };
  }

  public async getKnowledgeSource(args: { name: string; drdName: string }) {
    const drgElement = (await this.getDrgElement({ name: args.name })) as DMN15__tKnowledgeSource & {
      __$$element: string;
    };
    if (drgElement === undefined) {
      throw new Error("Couldn't find DRG element");
    }

    const drgElementBounds = await this.getDrgElementBoundsOnDrd({
      drgElementId: drgElement["@_id"] ?? "",
      drdName: args.drdName,
    });

    const {
      __$$element,
      "@_id": id,
      "@_locationURI": locationUri,
      "@_name": name,
      authorityRequirement,
      description,
      extensionElements,
      owner,
      type,
      ...undesiredProperties
    } = drgElement;

    return {
      // TODO: Check for relationship
      // authorityRequirement: authorityRequirement?.map((e) => ({
      //   requiredDecision: e.requiredDecision?.["@_href"],
      //   requiredInput: e.requiredInput?.["@_href"],
      //   requiredAuthority: e.requiredAuthority?.["@_href"],
      // })),

      bounds: {
        ...drgElementBounds,
      },
      description: description?.__$$text,
      extensionElements: extensionElements?.["kie:attachment"]?.map((e) => ({
        name: e["@_name"],
        url: e["@_url"],
      })),
      locationUri: locationUri,
      name: name,
      owner: owner?.["@_href"],
      type: type?.__$$text,

      undesiredProperties: this.getUndesiredPropertiesKeys(undesiredProperties),
    };
  }

  private getUndesiredPropertiesKeys(undesiredProperties: object) {
    return undesiredProperties && Object.keys(undesiredProperties).length > 0
      ? Object.keys(undesiredProperties)
      : undefined;
  }

  private async getDrgElementBoundsOnDrd(args: { drgElementId: string; drdName: string }) {
    const drd = await this.getDrd({ name: args.drdName });
    const bounds = (
      drd?.["dmndi:DMNDiagramElement"]?.find((e) => e["@_dmnElementRef"] === args.drgElementId) as DMNDI15__DMNShape
    )?.["dc:Bounds"];

    return {
      x: bounds?.["@_x"] ?? 0,
      y: bounds?.["@_y"] ?? 0,
      height: bounds?.["@_height"] ?? 0,
      width: bounds?.["@_width"] ?? 0,
    };
  }

  // FIXME: Luiz - Two nodes with the same name will not trigger this function!
  private async waitForModelToHaveDrgElement(args: { name: string }) {
    return this.page.waitForFunction((drgElementName) => {
      const model = document.querySelector("div[data-testid='storybook--dmn-editor-model']")?.textContent;
      if (model !== null && model !== undefined) {
        return (JSON.parse(model) as DmnLatestModel).definitions.drgElement?.find(
          (e) => e["@_name"] === drgElementName
        );
      }
      return;
    }, args.name);
  }

  private async getDrgElement(args: { name: string }): Promise<AllDrgElements | undefined> {
    return (await this.waitForModelToHaveDrgElement({ name: args.name })).jsonValue();
  }

  private async waitForModelToHaveDrd(args: { name: string }) {
    return this.page.waitForFunction((drgElementName) => {
      const model = document.querySelector("div[data-testid='storybook--dmn-editor-model']")?.textContent;
      if (model !== null && model !== undefined) {
        return (JSON.parse(model) as DmnLatestModel)?.definitions?.["dmndi:DMNDI"]?.["dmndi:DMNDiagram"]?.find(
          (e) => e["@_name"] === drgElementName
        );
      }
      return;
    }, args.name);
  }

  private async getDrd(args: { name: string }): Promise<DMNDI15__DMNDiagram | undefined> {
    return (await this.waitForModelToHaveDrd({ name: args.name })).jsonValue();
  }
}
