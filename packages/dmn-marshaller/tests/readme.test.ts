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
import { DMN_LATEST__tDefinitions, getMarshaller } from "@kie-tools/dmn-marshaller";

describe("readme", () => {
  test("usage", () => {
    const originalXml = `<?xml version="1.0" encoding="UTF-8" ?>
<definitions xmlns="https://www.omg.org/spec/DMN/20240513/MODEL/" id="_dmn_1" targetNamespace="http://kie.apache.org/dmn/_dmn_1">
  <itemDefinition id="_foo_item_definition" name="Foo" />
</definitions>
`;

    const marshaller = getMarshaller(originalXml, { upgradeTo: "latest" });
    const json = marshaller.parser.parse();
    const dmn: DMN_LATEST__tDefinitions = json.definitions;

    dmn.itemDefinition ??= [];
    dmn.itemDefinition.push({
      "@_id": "_bar_item_definition",
      "@_name": "Bar",
    });

    const xml = marshaller.builder.build(json);

    expect(xml).toStrictEqual(`<?xml version="1.0" encoding="UTF-8" ?>
<definitions xmlns="https://www.omg.org/spec/DMN/20240513/MODEL/" id="_dmn_1" targetNamespace="http://kie.apache.org/dmn/_dmn_1" xmlns:dmndi="https://www.omg.org/spec/DMN/20230324/DMNDI/" xmlns:dc="http://www.omg.org/spec/DMN/20180521/DC/" xmlns:di="http://www.omg.org/spec/DMN/20180521/DI/" xmlns:kie="https://kie.org/dmn/extensions/1.0">
  <itemDefinition id="_foo_item_definition" name="Foo" />
  <itemDefinition id="_bar_item_definition" name="Bar" />
</definitions>
`);
  });
});
