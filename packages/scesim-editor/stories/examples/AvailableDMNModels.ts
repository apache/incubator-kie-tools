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

import * as TestScenarioEditor from "../../src/TestScenarioEditor";
import { getMarshaller } from "@kie-tools/dmn-marshaller";
import { normalize } from "@kie-tools/dmn-marshaller/dist/normalization/normalize";
import { COLLECTION, EMPTY, LOAN_PRE_QUALIFICATION, MIXED, SIMPLE, TRAFFIC_VIOLATION } from "./ExternalDmnModels";

export const availableModels: TestScenarioEditor.ExternalDmn[] = [
  {
    model: normalize(getMarshaller(COLLECTION, { upgradeTo: "latest" }).parser.parse()),
    svg: "",
    normalizedPosixPathRelativeToTheOpenFile: "dev-webapp/available-dmn-models/collection.dmn",
  },
  {
    model: normalize(getMarshaller(EMPTY, { upgradeTo: "latest" }).parser.parse()),
    svg: "",
    normalizedPosixPathRelativeToTheOpenFile: "dev-webapp/available-dmn-models/empty.dmn",
  },
  {
    model: normalize(getMarshaller(LOAN_PRE_QUALIFICATION, { upgradeTo: "latest" }).parser.parse()),
    svg: "",
    normalizedPosixPathRelativeToTheOpenFile: "dev-webapp/available-dmn-models/loan-pre-qualification.dmn",
  },
  {
    model: normalize(getMarshaller(MIXED, { upgradeTo: "latest" }).parser.parse()),
    svg: "",
    normalizedPosixPathRelativeToTheOpenFile: "dev-webapp/available-dmn-models/mixed.dmn",
  },
  {
    model: normalize(getMarshaller(SIMPLE, { upgradeTo: "latest" }).parser.parse()),
    svg: "",
    normalizedPosixPathRelativeToTheOpenFile: "dev-webapp/available-dmn-models/simple.dmn",
  },
  {
    model: normalize(getMarshaller(TRAFFIC_VIOLATION, { upgradeTo: "latest" }).parser.parse()),
    svg: "",
    normalizedPosixPathRelativeToTheOpenFile: "dev-webapp/available-dmn-models/traffic-violation.dmn",
  },
];

export const availableModelsByPath: Record<string, TestScenarioEditor.ExternalDmn> = Object.values(
  availableModels
).reduce(
  (acc, v) => {
    acc[v.normalizedPosixPathRelativeToTheOpenFile] = v;
    return acc;
  },
  {} as Record<string, TestScenarioEditor.ExternalDmn>
);
