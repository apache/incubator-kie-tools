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
  isServerlessDecisionJson,
  isServerlessDecisionYaml,
  isServerlessDecisionYml,
  isServerlessWorkflowJson,
  isServerlessWorkflowYaml,
  isServerlessWorkflowYml,
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
    let extensionFinder = 0;
    switch (true) {
      case isServerlessWorkflowJson(fileName):
        extensionFinder = fileName.lastIndexOf(FileTypes.SW_JSON);
        return fileName.substring(extensionFinder);
      case isServerlessWorkflowYml(fileName):
        extensionFinder = fileName.lastIndexOf(FileTypes.SW_YML);
        return fileName.substring(extensionFinder);
      case isServerlessWorkflowYaml(fileName):
        extensionFinder = fileName.lastIndexOf(FileTypes.SW_YAML);
        return fileName.substring(extensionFinder);
      case isServerlessDecisionJson(fileName):
        extensionFinder = fileName.lastIndexOf(FileTypes.YARD_JSON);
        return fileName.substring(extensionFinder);
      case isServerlessDecisionYml(fileName):
        extensionFinder = fileName.lastIndexOf(FileTypes.YARD_YML);
        return fileName.substring(extensionFinder);
      case isServerlessDecisionYaml(fileName):
        extensionFinder = fileName.lastIndexOf(FileTypes.YARD_YAML);
        return fileName.substring(extensionFinder);
      case isDashbuilderYml(fileName):
        extensionFinder = fileName.lastIndexOf(FileTypes.DASH_YML);
        return fileName.substring(extensionFinder);
      case isDashbuilderYaml(fileName):
        extensionFinder = fileName.lastIndexOf(FileTypes.DASH_YAML);
        return fileName.substring(extensionFinder);
      default:
        extensionFinder = fileName.lastIndexOf(".");
        return fileName.substring(extensionFinder + 1);
    }
  } else {
    return extname(relativePath);
  }
}
