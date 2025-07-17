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

import { getMarshaller } from "@kie-tools/bpmn-marshaller";
import { DROOLS_NS__PRE_GWT_REMOVAL } from "@kie-tools/bpmn-marshaller/dist/drools-extension";
import { BPMN20__tProcess } from "../dist/schemas/bpmn-2_0/ts-gen/types";
import "@kie-tools/bpmn-marshaller/dist/drools-extension";

const defaultXmlns = `targetNamespace=""
  xmlns:bpmn2="http://www.omg.org/spec/BPMN/20100524/MODEL"
  xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI"
  xmlns:dc="http://www.omg.org/spec/DD/20100524/DC"
  xmlns:di="http://www.omg.org/spec/DD/20100524/DI"`;

// Baseline case, happy path.
describe("namespaced attributes from drools.xsd extension", () => {
  test("normal", () => {
    const normal = `<?xml version="1.0" encoding="UTF-8" ?>
<bpmn2:definitions
  ${defaultXmlns}
  xmlns:drools="http://www.jboss.org/drools"
>
  <bpmn2:process drools:version="1.0.0" />
</bpmn2:definitions>`;

    const marshaller = getMarshaller(normal, { upgradeTo: "latest" });
    const json = marshaller.parser.parse();
    expect(json.definitions.rootElement?.[0].__$$element).toStrictEqual("process");
    expect((json.definitions.rootElement?.[0] as BPMN20__tProcess)["@_drools:version"]).toStrictEqual("1.0.0");
    expect(poorlyFormatted(marshaller.builder.build(json))).toStrictEqual(poorlyFormatted(normal));
  });

  // Maps the `drools` namespace as the default.
  // Since attributes are unnamespaced by default, having a default namespace mapped doesn't mean unqualified attributes will all be
  // assigned to the default namespace.
  test("defaultNs", () => {
    const defaultNs = `<?xml version="1.0" encoding="UTF-8" ?>
<bpmn2:definitions
  ${defaultXmlns}
  xmlns="http://www.jboss.org/drools"
>
  <bpmn2:process version="1.0.0" />
</bpmn2:definitions>`;

    const marshaller = getMarshaller(defaultNs, { upgradeTo: "latest" });
    const json = marshaller.parser.parse();
    expect(json.definitions.rootElement?.[0].__$$element).toStrictEqual("process");
    expect(json.definitions["@_xmlns"]).toStrictEqual(DROOLS_NS__PRE_GWT_REMOVAL);
    expect(json.definitions["@_xmlns:drools"]).toStrictEqual(undefined);
    expect((json.definitions.rootElement?.[0] as BPMN20__tProcess)["@_drools:version"]).toStrictEqual(undefined);
    expect((json.definitions.rootElement?.[0] as any)["@_version"]).toStrictEqual("1.0.0");
    expect(poorlyFormatted(marshaller.builder.build(json))).toStrictEqual(poorlyFormatted(defaultNs));
  });

  // Uses a different declaration name for the `drools` namespace.
  test("renamed", () => {
    const renamed = `<?xml version="1.0" encoding="UTF-8" ?>
<bpmn2:definitions
  ${defaultXmlns}
  xmlns:droolz="http://www.jboss.org/drools"
>
  <bpmn2:process droolz:version="1.0.0" />
</bpmn2:definitions>`;

    const marshaller = getMarshaller(renamed, { upgradeTo: "latest" });
    const json = marshaller.parser.parse();
    expect(json.definitions.rootElement?.[0].__$$element).toStrictEqual("process");
    expect((json.definitions.rootElement?.[0] as BPMN20__tProcess)["@_drools:version"]).toStrictEqual("1.0.0");
    expect((json.definitions.rootElement?.[0] as any)["@_droolz:version"]).toStrictEqual(undefined);
    expect(poorlyFormatted(marshaller.builder.build(json))).toStrictEqual(poorlyFormatted(renamed));
  });

  // Uses a generic attribute called "version", not tied to the `drools` namespace.
  test("wrong", () => {
    const wrong = `<?xml version="1.0" encoding="UTF-8" ?>
<bpmn2:definitions
  ${defaultXmlns}
  xmlns:drools="http://www.jboss.org/drools"
>
  <bpmn2:process version="1.0.0" />
</bpmn2:definitions>`;

    const marshaller = getMarshaller(wrong, { upgradeTo: "latest" });
    const json = marshaller.parser.parse();
    expect(json.definitions.rootElement?.[0].__$$element).toStrictEqual("process");
    expect((json.definitions.rootElement?.[0] as BPMN20__tProcess)["@_drools:version"]).not.toStrictEqual("1.0.0");
    expect((json.definitions.rootElement?.[0] as any)["@_version"]).toStrictEqual("1.0.0");
    expect(poorlyFormatted(marshaller.builder.build(json))).toStrictEqual(poorlyFormatted(wrong));
  });
});

// Just enough for these tests.
function poorlyFormatted(s: string) {
  return s
    .replace(/\n+/g, "") // remove new lines
    .replace(/  /g, " "); // replace double spaces by single space
}
