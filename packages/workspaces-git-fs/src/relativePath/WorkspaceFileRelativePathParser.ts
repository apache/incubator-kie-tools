/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import { basename, extname, parse } from "path";
import {
  FileTypes,
  isDashbuilderYaml,
  isDashbuilderYml,
  isDecision,
  isScorecard,
  isServerlessDecisionJson,
  isServerlessDecisionYaml,
  isServerlessDecisionYml,
  isServerlessWorkflowJson,
  isServerlessWorkflowYaml,
  isServerlessWorkflowYml,
  isWorkflow,
} from "../constants/ExtensionHelper";

export function parseWorkspaceFileRelativePath(relativePath: string) {
  const extension = extractExtension(relativePath);
  return {
    relativePathWithoutExtension: relativePath.replace(`.${extension}`, ""),
    relativeDirPath: parse(relativePath).dir,
    extension: extension,
    nameWithoutExtension: basename(relativePath, `.${extension}`),
    name: basename(relativePath),
  };
}

export function extractExtension(relativePath: string) {
  const fileName = basename(relativePath).toLowerCase();
  if (fileName.includes(".")) {
    switch (true) {
      case isServerlessWorkflowJson(fileName):
        return FileTypes.SW_JSON;
      case isServerlessWorkflowYml(fileName):
        return FileTypes.SW_YML;
      case isServerlessWorkflowYaml(fileName):
        return FileTypes.SW_YAML;
      case isServerlessDecisionJson(fileName):
        return FileTypes.YARD_JSON;
      case isServerlessDecisionYml(fileName):
        return FileTypes.YARD_YML;
      case isServerlessDecisionYaml(fileName):
        return FileTypes.YARD_YAML;
      case isDashbuilderYml(fileName):
        return FileTypes.DASH_YML;
      case isDashbuilderYaml(fileName):
        return FileTypes.DASH_YAML;
      case isDecision(fileName):
        return FileTypes.DMN;
      case isWorkflow(fileName):
        return FileTypes.BPMN;
      case isScorecard(fileName):
        return FileTypes.PMML;
      default:
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
  } else {
    return extname(relativePath);
  }
}
