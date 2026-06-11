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
import "@kie-tools/bpmn-marshaller/dist/drools-extension";
import { BPMN20__tProcess } from "@kie-tools/bpmn-marshaller/dist/schemas/bpmn-2_0/ts-gen/types";

export type FlowElements = BPMN20__tProcess["flowElement"];
export type ArtifactElements = BPMN20__tProcess["artifact"];
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

  // Events
  public async getEvents(flowElements?: FlowElements) {
    const elements = flowElements ?? (await this.getProcess())?.flowElement;
    return elements?.filter((e) => e.__$$element === "event") ?? [];
  }

  public async getStartEvents(flowElements?: FlowElements) {
    const elements = flowElements ?? (await this.getProcess())?.flowElement;
    return elements?.filter((e) => e.__$$element === "startEvent") ?? [];
  }

  public async getEndEvents(flowElements?: FlowElements) {
    const elements = flowElements ?? (await this.getProcess())?.flowElement;
    return elements?.filter((e) => e.__$$element === "endEvent") ?? [];
  }

  public async getIntermediateThrowEvents(flowElements?: FlowElements) {
    const elements = flowElements ?? (await this.getProcess())?.flowElement;
    return elements?.filter((e) => e.__$$element === "intermediateThrowEvent") ?? [];
  }

  public async getIntermediateCatchEvents(flowElements?: FlowElements) {
    const elements = flowElements ?? (await this.getProcess())?.flowElement;
    return elements?.filter((e) => e.__$$element === "intermediateCatchEvent") ?? [];
  }

  public async getBoundaryEvents(flowElements?: FlowElements) {
    const elements = flowElements ?? (await this.getProcess())?.flowElement;
    return elements?.filter((e) => e.__$$element === "boundaryEvent") ?? [];
  }

  public async getImplicitThrowEvents(flowElements?: FlowElements) {
    const elements = flowElements ?? (await this.getProcess())?.flowElement;
    return elements?.filter((e) => e.__$$element === "implicitThrowEvent") ?? [];
  }

  // Gateways
  public async getExclusiveGateways(flowElements?: FlowElements) {
    const elements = flowElements ?? (await this.getProcess())?.flowElement;
    return elements?.filter((e) => e.__$$element === "exclusiveGateway") ?? [];
  }

  public async getInclusiveGateways(flowElements?: FlowElements) {
    const elements = flowElements ?? (await this.getProcess())?.flowElement;
    return elements?.filter((e) => e.__$$element === "inclusiveGateway") ?? [];
  }

  public async getParallelGateways(flowElements?: FlowElements) {
    const elements = flowElements ?? (await this.getProcess())?.flowElement;
    return elements?.filter((e) => e.__$$element === "parallelGateway") ?? [];
  }

  public async getEventBasedGateways(flowElements?: FlowElements) {
    const elements = flowElements ?? (await this.getProcess())?.flowElement;
    return elements?.filter((e) => e.__$$element === "eventBasedGateway") ?? [];
  }

  public async getComplexGateways(flowElements?: FlowElements) {
    const elements = flowElements ?? (await this.getProcess())?.flowElement;
    return elements?.filter((e) => e.__$$element === "complexGateway") ?? [];
  }

  // Tasks
  public async getTasks(flowElements?: FlowElements) {
    const elements = flowElements ?? (await this.getProcess())?.flowElement;
    return elements?.filter((e) => e.__$$element === "task") ?? [];
  }

  public async getScriptTasks(flowElements?: FlowElements) {
    const elements = flowElements ?? (await this.getProcess())?.flowElement;
    return elements?.filter((e) => e.__$$element === "scriptTask") ?? [];
  }

  public async getUserTasks(flowElements?: FlowElements) {
    const elements = flowElements ?? (await this.getProcess())?.flowElement;
    return elements?.filter((e) => e.__$$element === "userTask") ?? [];
  }

  public async getManualTasks(flowElements?: FlowElements) {
    const elements = flowElements ?? (await this.getProcess())?.flowElement;
    return elements?.filter((e) => e.__$$element === "manualTask") ?? [];
  }

  public async getServiceTasks(flowElements?: FlowElements) {
    const elements = flowElements ?? (await this.getProcess())?.flowElement;
    return elements?.filter((e) => e.__$$element === "serviceTask") ?? [];
  }

  public async getSendTasks(flowElements?: FlowElements) {
    const elements = flowElements ?? (await this.getProcess())?.flowElement;
    return elements?.filter((e) => e.__$$element === "sendTask") ?? [];
  }

  public async getReceiveTasks(flowElements?: FlowElements) {
    const elements = flowElements ?? (await this.getProcess())?.flowElement;
    return elements?.filter((e) => e.__$$element === "receiveTask") ?? [];
  }

  public async getBusinessRuleTasks(flowElements?: FlowElements) {
    const elements = flowElements ?? (await this.getProcess())?.flowElement;
    return elements?.filter((e) => e.__$$element === "businessRuleTask") ?? [];
  }

  // Activities
  public async getCallActivities(flowElements?: FlowElements) {
    const elements = flowElements ?? (await this.getProcess())?.flowElement;
    return elements?.filter((e) => e.__$$element === "callActivity") ?? [];
  }

  public async getSubProcesses(flowElements?: FlowElements) {
    const elements = flowElements ?? (await this.getProcess())?.flowElement;
    return elements?.filter((e) => e.__$$element === "subProcess") ?? [];
  }

  public async getAdHocSubProcesses(flowElements?: FlowElements) {
    const elements = flowElements ?? (await this.getProcess())?.flowElement;
    return elements?.filter((e) => e.__$$element === "adHocSubProcess") ?? [];
  }

  public async getTransactions(flowElements?: FlowElements) {
    const elements = flowElements ?? (await this.getProcess())?.flowElement;
    return elements?.filter((e) => e.__$$element === "transaction") ?? [];
  }

  // Data
  public async getDataObjects(flowElements?: FlowElements) {
    const elements = flowElements ?? (await this.getProcess())?.flowElement;
    return elements?.filter((e) => e.__$$element === "dataObject") ?? [];
  }

  public async getDataObjectReferences(flowElements?: FlowElements) {
    const elements = flowElements ?? (await this.getProcess())?.flowElement;
    return elements?.filter((e) => e.__$$element === "dataObjectReference") ?? [];
  }

  public async getDataStoreReferences(flowElements?: FlowElements) {
    const elements = flowElements ?? (await this.getProcess())?.flowElement;
    return elements?.filter((e) => e.__$$element === "dataStoreReference") ?? [];
  }

  // Flows
  public async getSequenceFlows(flowElements?: FlowElements) {
    const elements = flowElements ?? (await this.getProcess())?.flowElement;
    return elements?.filter((e) => e.__$$element === "sequenceFlow") ?? [];
  }

  // Choreography
  public async getCallChoreographies(flowElements?: FlowElements) {
    const elements = flowElements ?? (await this.getProcess())?.flowElement;
    return elements?.filter((e) => e.__$$element === "callChoreography") ?? [];
  }

  public async getChoreographyTasks(flowElements?: FlowElements) {
    const elements = flowElements ?? (await this.getProcess())?.flowElement;
    return elements?.filter((e) => e.__$$element === "choreographyTask") ?? [];
  }

  public async getSubChoreographies(flowElements?: FlowElements) {
    const elements = flowElements ?? (await this.getProcess())?.flowElement;
    return elements?.filter((e) => e.__$$element === "subChoreography") ?? [];
  }

  public async getGroups(artifactElements?: ArtifactElements) {
    const elements = artifactElements ?? (await this.getProcess())?.artifact;
    return elements?.filter((e) => e.__$$element === "group") ?? [];
  }

  public async getTextAnnotations(artifactElements?: ArtifactElements) {
    const elements = artifactElements ?? (await this.getProcess())?.artifact;
    return elements?.filter((e) => e.__$$element === "textAnnotation") ?? [];
  }

  public async getLaneSet() {
    return (await this.getProcess())?.laneSet;
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
