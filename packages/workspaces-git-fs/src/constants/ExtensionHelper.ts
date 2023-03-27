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
  supported:
    /(\.sw\.json|\.sw\.yaml|\.sw\.yml|\.yard\.json|\.yard\.yaml|\.yard\.yml|\.dash\.yml|\.dash\.yaml\.bpmn|\.bpmn2|\.dmn|\.pmml|\.scesim)$/i,
  sw: /^.*\.sw\.(json|yml|yaml)$/i,
  swJson: /^.*\.sw\.json$/i,
  swYml: /^.*\.sw\.yml$/i,
  swYaml: /^.*\.sw\.yaml$/i,
  yardJson: /^.*\.yard\.json$/i,
  yardYml: /^.*\.yard\.yml$/i,
  yardYaml: /^.*\.yard\.yaml$/i,
  dash: /^.*\.dash\.(yml|yaml)$/i,
  dashYml: /^.*\.dash\.yml$/i,
  dashYaml: /^.*\.dash\.yaml$/i,
  dmn: /^.*\.dmn$/i,
  bpmn: /^.*\.(bpmn|bpmn2)$/i,
  scesim: /^.*\.scesim$/i,
  pmml: /^.*\.pmml$/i,
  json: /^.*\.json$/i,
  yaml: /^.*\.(yml|yaml)$/i,
  spec: /^.*(\.spec|\.specs|spec|specs)\.(json|yml|yaml)$/i,
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
  DMN = "dmn",
  BPMN = "bpmn",
  BPMN2 = "bpmn2",
  SCESIM = "scesim",
  PMML = "pmml",
}

export function isServerlessWorkflow(path: string): boolean {
  return REGEX.sw.test(path);
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

export function isDashbuilder(path: string): boolean {
  return REGEX.dash.test(path);
}

export function isDashbuilderYml(path: string): boolean {
  return REGEX.dashYml.test(path);
}

export function isDashbuilderYaml(path: string): boolean {
  return REGEX.dashYaml.test(path);
}

export function isDecision(path: string): boolean {
  return REGEX.dmn.test(path);
}

export function isWorkflow(path: string): boolean {
  return REGEX.bpmn.test(path);
}

export function isTestScenario(path: string): boolean {
  return REGEX.scesim.test(path);
}

export function isScorecard(path: string): boolean {
  return REGEX.pmml.test(path);
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
