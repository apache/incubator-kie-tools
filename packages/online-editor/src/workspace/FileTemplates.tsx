/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

export const emptyTemplates = {
  default: "",

  dmn: `<dmn:definitions xmlns:dmn="http://www.omg.org/spec/DMN/20180521/MODEL/" xmlns="https://kiegroup.org/dmn/_C1C79B97-8F91-42EC-89C5-2466294AF4A5" xmlns:feel="http://www.omg.org/spec/DMN/20180521/FEEL/" xmlns:kie="http://www.drools.org/kie/dmn/1.2" xmlns:dmndi="http://www.omg.org/spec/DMN/20180521/DMNDI/" xmlns:di="http://www.omg.org/spec/DMN/20180521/DI/" xmlns:dc="http://www.omg.org/spec/DMN/20180521/DC/" id="_99D6F3C5-F49A-4B48-ACC4-B04551E42313" name="new-file" typeLanguage="http://www.omg.org/spec/DMN/20180521/FEEL/" namespace="https://kiegroup.org/dmn/_C1C79B97-8F91-42EC-89C5-2466294AF4A5">
  <dmn:extensionElements/>
  <dmndi:DMNDI>
    <dmndi:DMNDiagram id="_91A0CC37-0D6C-4323-80D8-57547A4B2A11" name="DRG">
      <di:extension>
        <kie:ComponentsWidthsExtension/>
      </di:extension>
    </dmndi:DMNDiagram>
  </dmndi:DMNDI>
</dmn:definitions>`,

  bpmn: `<bpmn2:definitions xmlns:bpmn2="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" xmlns:bpsim="http://www.bpsim.org/schemas/1.0" xmlns:drools="http://www.jboss.org/drools" xmlns:xsi="xsi" id="_RmQ5UARWEDqJgNv7aIREng" xsi:schemaLocation="http://www.omg.org/spec/BPMN/20100524/MODEL BPMN20.xsd http://www.jboss.org/drools drools.xsd http://www.bpsim.org/schemas/1.0 bpsim.xsd http://www.omg.org/spec/DD/20100524/DC DC.xsd http://www.omg.org/spec/DD/20100524/DI DI.xsd " exporter="jBPM Process Modeler" exporterVersion="2.0" targetNamespace="http://www.omg.org/bpmn20">
<bpmn2:process id="new_file" drools:packageName="com.example" drools:version="1.0" drools:adHoc="false" name="new-file" isExecutable="true" processType="Public"/>
<bpmndi:BPMNDiagram>
  <bpmndi:BPMNPlane bpmnElement="new_file"/>
</bpmndi:BPMNDiagram>
<bpmn2:relationship type="BPSimData">
  <bpmn2:extensionElements>
    <bpsim:BPSimData>
      <bpsim:Scenario id="default" name="Simulationscenario">
        <bpsim:ScenarioParameters/>
      </bpsim:Scenario>
    </bpsim:BPSimData>
  </bpmn2:extensionElements>
  <bpmn2:source>_RmQ5UARWEDqJgNv7aIREng</bpmn2:source>
  <bpmn2:target>_RmQ5UARWEDqJgNv7aIREng</bpmn2:target>
</bpmn2:relationship>
</bpmn2:definitions>`,

  bpmn2: `<bpmn2:definitions xmlns:bpmn2="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" xmlns:bpsim="http://www.bpsim.org/schemas/1.0" xmlns:drools="http://www.jboss.org/drools" xmlns:xsi="xsi" id="_RmQ5UARWEDqJgNv7aIREng" xsi:schemaLocation="http://www.omg.org/spec/BPMN/20100524/MODEL BPMN20.xsd http://www.jboss.org/drools drools.xsd http://www.bpsim.org/schemas/1.0 bpsim.xsd http://www.omg.org/spec/DD/20100524/DC DC.xsd http://www.omg.org/spec/DD/20100524/DI DI.xsd " exporter="jBPM Process Modeler" exporterVersion="2.0" targetNamespace="http://www.omg.org/bpmn20">
  <bpmn2:process id="new_file" drools:packageName="com.example" drools:version="1.0" drools:adHoc="false" name="new-file" isExecutable="true" processType="Public"/>
  <bpmndi:BPMNDiagram>
    <bpmndi:BPMNPlane bpmnElement="new_file"/>
  </bpmndi:BPMNDiagram>
  <bpmn2:relationship type="BPSimData">
    <bpmn2:extensionElements>
      <bpsim:BPSimData>
        <bpsim:Scenario id="default" name="Simulationscenario">
          <bpsim:ScenarioParameters/>
        </bpsim:Scenario>
      </bpsim:BPSimData>
    </bpmn2:extensionElements>
    <bpmn2:source>_RmQ5UARWEDqJgNv7aIREng</bpmn2:source>
    <bpmn2:target>_RmQ5UARWEDqJgNv7aIREng</bpmn2:target>
  </bpmn2:relationship>
  </bpmn2:definitions>`,

  pmml: `<PMML xmlns="http://www.dmg.org/PMML-4_4" version="4.4">
  <Header/>
  <DataDictionary/>
  <Scorecard modelName="Untitled model" functionName="regression">
    <MiningSchema/>
    <Output/>
    <Characteristics/>
  </Scorecard>
</PMML>`,
};
