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

import * as fs from "fs";
import * as path from "path";
import { getMarshaller } from "@kie-tools/bpmn-marshaller";
import { BPMN20__tDefinitions } from "@kie-tools/bpmn-marshaller/dist/schemas/bpmn-2_0/ts-gen/types";

describe("readme", () => {
  test("usage", () => {
    const originalXml = `<?xml version="1.0" encoding="UTF-8" ?>
<definitions xmlns="http://www.omg.org/spec/BPMN/20100524/MODEL" id="_bpmn_1" targetNamespace="http://kie.apache.org/bpmn/_bpmn_1">
  <itemDefinition id="_foo_item_definition" structureRef="org.acme.Foo" />
</definitions>
`;

    const marshaller = getMarshaller(originalXml);
    const json = marshaller.parser.parse();
    const bpmn: BPMN20__tDefinitions = json.definitions;

    bpmn.rootElement ??= [];
    bpmn.rootElement.push({
      __$$element: "itemDefinition",
      "@_id": "_bar_item_definition",
      "@_structureRef": "org.acme.Bar",
    });

    const xml = marshaller.builder.build(json);

    expect(xml).toStrictEqual(`<?xml version="1.0" encoding="UTF-8" ?>
<definitions xmlns="http://www.omg.org/spec/BPMN/20100524/MODEL" id="_bpmn_1" targetNamespace="http://kie.apache.org/bpmn/_bpmn_1" xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" xmlns:dc="http://www.omg.org/spec/DD/20100524/DC" xmlns:di="http://www.omg.org/spec/DD/20100524/DI">
  <itemDefinition id="_foo_item_definition" structureRef="org.acme.Foo" />
  <itemDefinition id="_bar_item_definition" structureRef="org.acme.Bar" />
</definitions>
`);
  });
});
