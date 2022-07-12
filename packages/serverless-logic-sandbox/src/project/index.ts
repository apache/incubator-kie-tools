/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import { WorkspaceFile } from "../workspace/WorkspacesContext";

export const PROJECT_FILES = {
  pomXml: "pom.xml",
  applicationProperties: "application.properties",
  dockerFile: "Dockerfile",
  dockerIgnore: ".dockerignore",
};

export function isProject(files: WorkspaceFile[]): boolean {
  return !!files.find((f) => f.relativePath === PROJECT_FILES.pomXml);
}

export function isSingleModuleProject(files: WorkspaceFile[]): boolean {
  const pomFiles = files.filter((f) => f.name === PROJECT_FILES.pomXml);
  return pomFiles.length === 1 && pomFiles[0].relativeDirPath === "";
}
