/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

export type AcceleratorConfig = {
  name: string;
  gitRepositoryUrl: string;
  gitRepositoryGitRef: string;
  dmnDestinationFolder: string;
  bpmnDestinationFolder: string;
  otherFilesDestinationFolder: string;
  iconUrl?: string;
  appliedAt?: string;
};

export function validateAcceleratorDestinationFolderPaths(path: string) {
  if (path.startsWith(".")) {
    return "Accelerator destination folder misconfigured: Please only use absolute paths.";
  } else if (path.includes("*") || path.includes("?")) {
    return "Accelerator destination folder misconfigured: Wildcards and patterns are not allowed.";
  }
  return;
}

export const KIE_SANDBOX_PROJECT_FILES_PATH = ".kie-sandbox";
export const ACCELERATOR_CONFIG_FILE_NAME = "accelerator";
export const ACCELERATOR_CONFIG_FILE_EXTENSION = "yaml";
export const ACCELERATOR_CONFIG_FILE_RELATIVE_PATH = `${KIE_SANDBOX_PROJECT_FILES_PATH}/${ACCELERATOR_CONFIG_FILE_NAME}.${ACCELERATOR_CONFIG_FILE_EXTENSION}`;
