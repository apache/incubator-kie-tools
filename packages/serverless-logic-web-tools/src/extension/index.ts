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
import { basename } from "path";
import { PROJECT_FILES } from "../project";
import { FileTypes, isOfKind } from "@kie-tools-core/workspaces-git-fs/dist/constants/ExtensionHelper";

const EDIT_NON_MODEL_ALLOW_LIST = [PROJECT_FILES.applicationProperties];

export const GLOB_PATTERN = {
  all: "**/*",
  allExceptDockerfiles: "**/!(Dockerfile|.dockerignore)",
  sw: "**/*.sw.+(json|yml|yaml)",
  yard: "**/*.yard.+(json|yml|yaml)",
  dash: "**/*.dash.+(yml|yaml)",
  spec: "**/+(*.spec?(s)|spec?(s)).+(yml|yaml|json)",
  sw_spec: "**/+(*.sw|*.spec?(s)|spec?(s)).+(yml|yaml|json)",
};

export const supportedFileExtensionArray = [
  FileTypes.SW_JSON,
  FileTypes.SW_YML,
  FileTypes.SW_YAML,
  FileTypes.YARD_JSON,
  FileTypes.YARD_YML,
  FileTypes.YARD_YAML,
  FileTypes.DASH_YAML,
  FileTypes.DASH_YML,
];

export function isModel(path: string): boolean {
  return isOfKind("sw", path) || isOfKind("yard", path) || isOfKind("dash", path);
}

export function isEditable(path: string): boolean {
  return (
    isModel(path) ||
    EDIT_NON_MODEL_ALLOW_LIST.includes(basename(path)) ||
    isOfKind("yaml", path) ||
    isOfKind("json", path)
  );
}

export function isSupportedByVirtualServiceRegistry(path: string): boolean {
  return isOfKind("sw", path) || isOfKind("spec", path);
}

export function isApplicationProperties(path: string): boolean {
  return /.*\/?application\.properties$/i.test(path);
}

export function isSupportingFileForDevMode(args: { path: string; targetFolder: string }): boolean {
  const regexStr = args.targetFolder.trim().length > 0 ? `^${args.targetFolder}(/.*)?$` : "^.*$";
  const isInFolderRegex = new RegExp(regexStr, "i");
  return (
    !isOfKind("sw", args.path) &&
    isInFolderRegex.test(args.path) &&
    (isOfKind("json", args.path) || isOfKind("yaml", args.path))
  );
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
