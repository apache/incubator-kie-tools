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

const filesForAddition = [
  { path: "../tests-data--manual/other/decisionAndInput.dmn" },
  { path: "../tests-data--manual/other/decisionAndInputWithAddition.dmn" },
];

const filesForNormalization = [
  { path: "../tests-data--manual/other/decisionAndInput_wrongSequenceOrder.dmn" },
  { path: "../tests-data--manual/other/decisionAndInput.dmn" },
];

describe("build always add elements in the same order", () => {
  test("Addition", () => {
    const fileSource = filesForAddition[0];
    const fileExpected = filesForAddition[1];
    const xml = fs.readFileSync(path.join(__dirname, fileSource.path), "utf-8");
    const marshaller = getMarshaller(xml, { upgradeTo: "1.5" });
    const json = marshaller.parser.parse();

    // Adding some props in an arbitrary order shouldn't alter the end result.

    json.definitions.import = [
      {
        "@_name": "some-import",
        "@_namespace": "some-namespace",
        "@_importType": "some-import-type",
      },
    ];

    json.definitions.artifact = [
      {
        __$$element: "group",
        "@_name": "some-group",
      },
    ];

    const xmlRetrieved = marshaller.builder.build(json).replace(/\s/g, "");
    const xmlExpected = fs.readFileSync(path.join(__dirname, fileExpected.path), "utf-8").replace(/\s/g, "");
    expect(xmlRetrieved).toEqual(xmlExpected);
  });
});

describe("build always produces elements in the same order", () => {
  test("Normalization", () => {
    const fileSource = filesForNormalization[0];
    const fileExpected = filesForNormalization[1];
    const xmlSource = fs.readFileSync(path.join(__dirname, fileSource.path), "utf-8");
    const marshaller = getMarshaller(xmlSource, { upgradeTo: "1.5" });
    const json = marshaller.parser.parse();

    const xmlRetrieved = marshaller.builder.build(json).replace(/\s/g, "");
    const xmlExpected = fs.readFileSync(path.join(__dirname, fileExpected.path), "utf-8").replace(/\s/g, "");
    expect(xmlRetrieved).toStrictEqual(xmlExpected);
  });
});
