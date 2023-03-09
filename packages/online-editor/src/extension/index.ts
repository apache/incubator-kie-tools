/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";

const REGEX = {
  supported: /(\.bpmn|\.bpmn2|\.dmn|\.pmml|\.scesim)$/i,
  dmn: /^.*\.dmn$/i,
  bpmn: /^.*\.(bpmn|bpmn2)$/i,
  scesim: /^.*\.scesim$/i,
  pmml: /^.*\.pmml$/i,
};

export const GLOB_PATTERN = {
  all: "**/*",
  dmn: "**/*.dmn",
  bpmn: "**/*.(bpmn|bpmn2)",
  scesim: "**/*.scesim",
  pmml: "**/*.pmml",
};

export enum FileTypes {
  DMN = "dmn",
  BPMN = "bpmn",
  BPMN2 = "bpmn2",
  SCESIM = "scesim",
  PMML = "pmml",
}

export const supportedFileExtensionArray = [
  FileTypes.DMN,
  FileTypes.BPMN,
  FileTypes.BPMN2,
  FileTypes.SCESIM,
  FileTypes.PMML,
];

export function isDecision(path: string): boolean {
  return REGEX.dmn.test(path);
}

export function isBusinessProcessModel(path: string): boolean {
  return REGEX.bpmn.test(path);
}

export function isTestScenario(path: string): boolean {
  return REGEX.scesim.test(path);
}

export function isScorecard(path: string): boolean {
  return REGEX.pmml.test(path);
}

export function isModel(path: string): boolean {
  return isDecision(path) || isBusinessProcessModel(path) || isScorecard(path);
}

export function isEditable(path: string): boolean {
  return isModel(path) || isTestScenario(path);
}

export type SupportedFileExtensions = typeof supportedFileExtensionArray[number];

export function splitFiles(files: WorkspaceFile[]): {
  editableFiles: WorkspaceFile[];
  readonlyFiles: WorkspaceFile[];
} {
  const [editableFiles, readonlyFiles] = files.reduce(
    ([editableFiles, readonlyFiles], f: WorkspaceFile) =>
      isEditable(f.relativePath) ? [[...editableFiles, f], readonlyFiles] : [editableFiles, [...readonlyFiles, f]],
    [[], []]
  );
  return { editableFiles, readonlyFiles };
}
