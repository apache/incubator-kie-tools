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

import * as React from "react";
import type { Meta, StoryObj } from "@storybook/react";
import { getMarshaller } from "@kie-tools/bpmn-marshaller";
import { generateUuid } from "@kie-tools/boxed-expression-component/dist/api";
import { BpmnEditorWrapper, StorybookBpmnEditorProps } from "../../bpmnEditorStoriesWrapper";
import { BpmnEditor, BpmnEditorProps } from "../../../src/BpmnEditor";
import { GRPC_API_CALL_TASK, REST_API_CALL_TASK } from "./other/OtherCustomTasks";

export const generateEmptyBpmn20 = () => `<?xml version="1.0" encoding="UTF-8"?>
<definitions
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:bpmn2="http://www.omg.org/spec/BPMN/20100524/MODEL"
  xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI"
  xmlns:bpsim="http://www.bpsim.org/schemas/1.0"
  xmlns:dc="http://www.omg.org/spec/DD/20100524/DC"
  xmlns:drools="http://www.jboss.org/drools"
  id="${generateUuid()}"
  xsi:schemaLocation="http://www.omg.org/spec/BPMN/20100524/MODEL BPMN20.xsd http://www.jboss.org/drools drools.xsd http://www.bpsim.org/schemas/1.0 bpsim.xsd http://www.omg.org/spec/DD/20100524/DC DC.xsd http://www.omg.org/spec/DD/20100524/DI DI.xsd"
  exporter="jBPM Process Modeler"
  exporterVersion="2.0"
  expressionLanguage="" 
  targetNamespace="https://kie.apache.org/bpmn/${generateUuid()}"
>
  <process id="Untitled" drools:packageName="com.example" drools:version="1.0" drools:adHoc="false" name="Untitled" isExecutable="true" processType="Public">
    <task id="_F2B7EABF-DF20-4911-85D7-A9183E34AAA1" drools:taskName="rest-api-call-task" name="Rest API call Task">
      <extensionElements>
        <drools:metaData name="elementname">
          <drools:metaValue><![CDATA[Rest API call Task]]></drools:metaValue>
        </drools:metaData>
      </extensionElements>
    </task>
    <task id="_F2B7EABF-DF20-4911-85D7-A9183E34AAA2" drools:taskName="grpc-api-call-task" name="gRPC API call Task">
      <extensionElements>
        <drools:metaData name="elementname">
          <drools:metaValue><![CDATA[gRPC API call Task]]></drools:metaValue>
        </drools:metaData>
      </extensionElements>
    </task>
  </process>
  <bpmndi:BPMNDiagram>
    <bpmndi:BPMNPlane bpmnElement="Untitled">
      <bpmndi:BPMNShape id="shape__F2B7EABF-DF20-4911-85D7-A9183E34AAA1" bpmnElement="_F2B7EABF-DF20-4911-85D7-A9183E34AAA1">
        <dc:Bounds height="102" width="154" x="416" y="100"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="shape__F2B7EABF-DF20-4911-85D7-A9183E34AAA2" bpmnElement="_F2B7EABF-DF20-4911-85D7-A9183E34AAA2">
        <dc:Bounds height="102" width="154" x="416" y="220"/>
      </bpmndi:BPMNShape>
    </bpmndi:BPMNPlane>
  </bpmndi:BPMNDiagram>
</definitions>`;

const meta: Meta<BpmnEditorProps> = {
  title: "Features/CustomTasks",
  component: BpmnEditor,
  includeStories: /^[A-Z]/,
};

export default meta;
type Story = StoryObj<StorybookBpmnEditorProps>;

const marshaller = getMarshaller(generateEmptyBpmn20(), { upgradeTo: "latest" });
const model = marshaller.parser.parse();

export const CustomTasks: Story = {
  render: (args) => BpmnEditorWrapper(),
  args: {
    model: model,
    originalVersion: "2.0",
    externalContextDescription: "The Storybook for the BPMN Editor",
    externalContextName: "Apache KIE :: BPMN Editor :: Storybook",
    issueTrackerHref: "",
    xml: marshaller.builder.build(model),
    //
    customTasks: [REST_API_CALL_TASK, GRPC_API_CALL_TASK],
  },
};
