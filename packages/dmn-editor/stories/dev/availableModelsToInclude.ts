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

import { getMarshaller } from "@kie-tools/dmn-marshaller";
import { XML2PMML } from "@kie-tools/pmml-editor-marshaller";
import * as DmnEditor from "../../src/DmnEditor";
import { getPmmlNamespace } from "../../src/pmml/pmml";
import { sumBkm, sumDiffDs, testTreePmml } from "./externalModels";

export const sumBkmModel = getMarshaller(sumBkm, { upgradeTo: "latest" }).parser.parse();
export const sumDiffDsModel = getMarshaller(sumDiffDs, { upgradeTo: "latest" }).parser.parse();
export const testTreePmmlModel = XML2PMML(testTreePmml);

export const avaiableModels: DmnEditor.ExternalModel[] = [
  {
    type: "dmn",
    model: sumBkmModel,
    svg: "",
    normalizedPosixPathRelativeToTheOpenFile: "dev-webapp/available-models-to-include/sumBkm.dmn",
  },
  {
    type: "dmn",
    model: sumDiffDsModel,
    svg: "",
    normalizedPosixPathRelativeToTheOpenFile: "dev-webapp/available-models-to-include/sumDiffDs.dmn",
  },
  {
    type: "dmn",
    model: getMarshaller(`<definitions xmlns="https://www.omg.org/spec/DMN/20230324/MODEL/" />`, {
      upgradeTo: "latest",
    }).parser.parse(),
    svg: "",
    normalizedPosixPathRelativeToTheOpenFile: "dev-webapp/available-models-to-include/empty.dmn",
  },
  {
    type: "pmml",
    model: testTreePmmlModel,
    normalizedPosixPathRelativeToTheOpenFile: "dev-webapp/available-models-to-include/testTree.pmml",
  },
];

export const availableModelsByPath: Record<string, DmnEditor.ExternalModel> = Object.values(avaiableModels).reduce(
  (acc, v) => {
    acc[v.normalizedPosixPathRelativeToTheOpenFile] = v;
    return acc;
  },
  {} as Record<string, DmnEditor.ExternalModel>
);

export const modelsByNamespace = Object.values(avaiableModels).reduce((acc, v) => {
  if (v.type === "dmn") {
    acc[v.model.definitions["@_namespace"]] = v;
  } else if (v.type === "pmml") {
    acc[getPmmlNamespace({ normalizedPosixPathRelativeToTheOpenFile: v.normalizedPosixPathRelativeToTheOpenFile })] = v;
  }
  return acc;
}, {} as DmnEditor.ExternalModelsIndex);
