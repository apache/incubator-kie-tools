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

const REGEX = {
  swJson: /^.*\.sw\.json$/i,
  swYml: /^.*\.sw\.yml$/i,
  swYaml: /^.*\.sw\.yaml$/i,
  yardJson: /^.*\.yard\.json$/i,
  yardYml: /^.*\.yard\.yml$/i,
  yardYaml: /^.*\.yard\.yaml$/i,
  dashYml: /^.*\.dash\.yml$/i,
  dashYaml: /^.*\.dash\.yaml$/i,
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

export function isServerlessWorkflowJson(path: string): boolean {
  return REGEX.swJson.test(path);
}

export function isServerlessWorkflowYml(path: string): boolean {
  return REGEX.swYml.test(path);
}

export function isServerlessWorkflowYaml(path: string): boolean {
  return REGEX.swYaml.test(path);
}

export function isServerlessDecisionJson(path: string): boolean {
  return REGEX.yardJson.test(path);
}

export function isServerlessDecisionYml(path: string): boolean {
  return REGEX.yardYml.test(path);
}

export function isServerlessDecisionYaml(path: string): boolean {
  return REGEX.yardYaml.test(path);
}

export function isDashbuilderYml(path: string): boolean {
  return REGEX.dashYml.test(path);
}

export function isDashbuilderYaml(path: string): boolean {
  return REGEX.dashYaml.test(path);
}
