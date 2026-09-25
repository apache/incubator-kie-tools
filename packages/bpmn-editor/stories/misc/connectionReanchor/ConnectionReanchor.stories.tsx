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

import type { Meta, StoryObj } from "@storybook/react";
import { getMarshaller } from "@kie-tools/bpmn-marshaller";
import { BpmnEditorWrapper, StorybookBpmnEditorProps } from "../../bpmnEditorStoriesWrapper";
import { BpmnEditor, BpmnEditorProps } from "../../../src/BpmnEditor";

// Start -> First Function -> Second Function -> End. First Function's incoming flow is pinned to its
// left border and its outgoing flow to its right border (see the waypoints below).
const startToEndProcess = `<?xml version="1.0" encoding="UTF-8"?>
<definitions
  xmlns="http://www.omg.org/spec/BPMN/20100524/MODEL"
  xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI"
  xmlns:dc="http://www.omg.org/spec/DD/20100524/DC"
  xmlns:di="http://www.omg.org/spec/DD/20100524/DI"
  targetNamespace="https://kie.apache.org/bpmn/connection-reanchor"
  id="connection-reanchor-definitions">
  <process id="connection_reanchor_process" name="Connection Reanchor" isExecutable="true">
    <startEvent id="StartEvent_1" name="Start" />
    <task id="Task_1" name="First Function" />
    <task id="Task_2" name="Second Function" />
    <endEvent id="EndEvent_1" name="End" />
    <sequenceFlow id="Flow_1" sourceRef="StartEvent_1" targetRef="Task_1" />
    <sequenceFlow id="Flow_2" sourceRef="Task_1" targetRef="Task_2" />
    <sequenceFlow id="Flow_3" sourceRef="Task_2" targetRef="EndEvent_1" />
  </process>
  <bpmndi:BPMNDiagram id="Diagram_1">
    <bpmndi:BPMNPlane id="Plane_1" bpmnElement="connection_reanchor_process">
      <bpmndi:BPMNShape id="Shape_StartEvent_1" bpmnElement="StartEvent_1">
        <dc:Bounds x="100" y="200" width="56" height="56" />
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="Shape_Task_1" bpmnElement="Task_1">
        <dc:Bounds x="240" y="188" width="160" height="80" />
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="Shape_Task_2" bpmnElement="Task_2">
        <dc:Bounds x="480" y="188" width="160" height="80" />
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="Shape_EndEvent_1" bpmnElement="EndEvent_1">
        <dc:Bounds x="720" y="200" width="56" height="56" />
      </bpmndi:BPMNShape>
      <bpmndi:BPMNEdge id="Edge_Flow_1" bpmnElement="Flow_1" sourceElement="Shape_StartEvent_1" targetElement="Shape_Task_1">
        <di:waypoint x="128" y="228" />
        <di:waypoint x="240" y="228" />
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="Edge_Flow_2" bpmnElement="Flow_2" sourceElement="Shape_Task_1" targetElement="Shape_Task_2">
        <di:waypoint x="400" y="228" />
        <di:waypoint x="480" y="228" />
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="Edge_Flow_3" bpmnElement="Flow_3" sourceElement="Shape_Task_2" targetElement="Shape_EndEvent_1">
        <di:waypoint x="640" y="228" />
        <di:waypoint x="720" y="228" />
      </bpmndi:BPMNEdge>
    </bpmndi:BPMNPlane>
  </bpmndi:BPMNDiagram>
</definitions>`;

const meta: Meta<BpmnEditorProps> = {
  title: "Misc/Connection Reanchor",
  component: BpmnEditor,
  includeStories: /^[A-Z]/,
};

export default meta;
type Story = StoryObj<StorybookBpmnEditorProps>;

const marshaller = getMarshaller(startToEndProcess, { upgradeTo: "latest" });
const model = marshaller.parser.parse();

export const StartToEnd: Story = {
  render: (args) => BpmnEditorWrapper(),
  args: {
    model: model,
    originalVersion: "2.0",
    externalContextDescription: "The Storybook for the BPMN Editor",
    externalContextName: "Apache KIE :: BPMN Editor :: Storybook",
    issueTrackerHref: "",
    xml: marshaller.builder.build(model),
  },
};
