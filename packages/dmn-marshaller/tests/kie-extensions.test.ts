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
import { getMarshaller } from "@kie-tools/dmn-marshaller";
import { DMN14__tKnowledgeSource } from "../dist/schemas/dmn-1_4/ts-gen/types";

describe("kie-extensions", () => {
  test("kie:attachment", () => {
    const xml = fs.readFileSync(path.join(__dirname, "../tests-data--manual/other/attachment.dmn"), "utf-8");
    const { parser } = getMarshaller(xml, { upgradeTo: "latest" });
    const json = parser.parse();

    const attachments = (json.definitions.drgElement ?? [])
      .filter((drgElement) => drgElement["__$$element"] === "knowledgeSource" ?? [])
      .flatMap((knowledgeSource: DMN14__tKnowledgeSource) => knowledgeSource.extensionElements?.["kie:attachment"]);

    expect(attachments.length).toStrictEqual(1);
  });

  test("kie:ComponentWidthsExtension", () => {
    const xml = fs.readFileSync(path.join(__dirname, "../tests-data--manual/other/sample12.dmn"), "utf-8");
    const { parser } = getMarshaller(xml, { upgradeTo: "latest" });
    const json = parser.parse();

    const componentWidthsExtension = (json.definitions["dmndi:DMNDI"]?.["dmndi:DMNDiagram"] ?? []).flatMap(
      (d) => d["di:extension"]?.["kie:ComponentsWidthsExtension"] ?? []
    );

    expect(componentWidthsExtension.length).toStrictEqual(1);
    expect(componentWidthsExtension.flatMap((s) => s["kie:ComponentWidths"] ?? []).length).toStrictEqual(24);
  });
});
