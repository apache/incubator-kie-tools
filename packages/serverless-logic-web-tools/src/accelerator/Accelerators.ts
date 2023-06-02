/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { isProject } from "../project";

export interface Accelerator {
  name: string;
  remote: string;
  url: string;
  ref: string;
  folderToMoveFiles: string;
  folderToMoveBlockedFiles: string;
  fileBlockList: string[];
  canBeUsed: (files: WorkspaceFile[]) => boolean;
  hasConflicts: (files: WorkspaceFile[]) => boolean;
}

const KOGITO_QUARKUS_ACCELERATOR_POTENTIAL_CONFLICT_FILES = ["application.properties", "META-INF/resources/index.html"];
export const KOGITO_QUARKUS_ACCELERATOR: Accelerator = {
  name: "Kogito Quarkus",
  remote: "kogito-quarkus-accelerator",
  url: "https://github.com/kiegroup/serverless-logic-sandbox-deployment",
  ref: "quarkus-accelerator",
  folderToMoveFiles: "src/main/resources",
  folderToMoveBlockedFiles: "src/main/resources/backup",
  fileBlockList: KOGITO_QUARKUS_ACCELERATOR_POTENTIAL_CONFLICT_FILES,
  canBeUsed: (files) => !isProject(files),
  hasConflicts: (files) => files.some((f) => KOGITO_QUARKUS_ACCELERATOR_POTENTIAL_CONFLICT_FILES.includes(f.name)),
};
