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

import { basename } from "path";
import { PROJECT_FILES } from "../project";

const EDIT_NON_MODEL_ALLOW_LIST = [PROJECT_FILES.applicationProperties];

const REGEX = {
  supported: /(\.sw\.json|\.sw\.yaml|\.sw\.yml|\.yard\.json|\.yard\.yaml|\.yard\.yml|\.dash\.yml|\.dash\.yaml)$/i,
  sw: /^.*\.sw\.(json|yml|yaml)$/i,
  swJson: /^.*\.sw\.json$/i,
  swYaml: /^.*\.sw\.(yml|yaml)$/i,
  yard: /^.*\.yard\.(json|yml|yaml)$/i,
  dash: /^.*\.dash\.(yml|yaml)$/i,
  json: /^.*\.json$/i,
  yaml: /^.*\.(yml|yaml)$/i,
  spec: /^.*(\.spec|\.specs|spec|specs)\.(json|yml|yaml)$/i,
};

export const GLOB_PATTERN = {
  all: "**/*",
  allExceptDockerfiles: "**/!(Dockerfile|.dockerignore)",
  sw: "**/*.sw.+(json|yml|yaml)",
  yard: "**/*.yard.+(json|yml|yaml)",
  dash: "**/*.dash.+(yml|yaml)",
  spec: "**/+(*.spec?(s)|spec?(s)).+(yml|yaml|json)",
  sw_spec: "**/+(*.sw|*.spec?(s)|spec?(s)).+(yml|yaml|json)",
};

export enum FileTypes {
  SW_JSON = "sw.json",
  SW_YML = "sw.yml",
  SW_YAML = "sw.yaml",
  YARD_JSON = "yard.json",
  YARD_YML = "yard.yml",
  YARD_YAML = "yard.yaml",
  DASH_YAML = "dash.yaml",
  DASH_YML = "dash.yml",
}

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

export function isServerlessWorkflow(path: string): boolean {
  return REGEX.sw.test(path);
}

export function isServerlessWorkflowJson(path: string): boolean {
  return REGEX.swJson.test(path);
}

export function isServerlessWorkflowYaml(path: string): boolean {
  return REGEX.swYaml.test(path);
}

export function isServerlessDecision(path: string): boolean {
  return REGEX.yard.test(path);
}

export function isDashbuilder(path: string): boolean {
  return REGEX.dash.test(path);
}

export function isModel(path: string): boolean {
  return isServerlessWorkflow(path) || isServerlessDecision(path) || isDashbuilder(path);
}

export function isEditable(path: string): boolean {
  return isModel(path) || EDIT_NON_MODEL_ALLOW_LIST.includes(basename(path));
}

export function isSupportedByVirtualServiceRegistry(path: string): boolean {
  return isServerlessWorkflow(path) || isSpec(path);
}

export function isSpec(path: string): boolean {
  return REGEX.spec.test(path);
}

export function isJson(path: string): boolean {
  return REGEX.json.test(path);
}

export function isYaml(path: string): boolean {
  return REGEX.yaml.test(path);
}

export type SupportedFileExtensions = typeof supportedFileExtensionArray[number];
