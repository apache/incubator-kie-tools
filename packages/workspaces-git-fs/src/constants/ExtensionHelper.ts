/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

const REGEX = {
  supportedSingleExtensions: /(\.bpmn|bpmn2|\.dmn|\.pmml)$/i,
  supportedDoubleExtensions: /(\.sw\.(json|yml|yaml)|\.yard\.(json|yml|yaml)|\.dash\.(yml|yaml))$/i,
  sw: /^.*\.sw\.(json|yml|yaml)$/i,
  swJson: /^.*\.sw\.json$/i,
  swYaml: /^.*\.sw\.yaml$/i,
  yard: /^.*\.yard\.(yml|yaml)$/i,
  dash: /^.*\.dash\.(yml|yaml)$/i,
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

type FileRegexKind = keyof typeof REGEX;
const matchers: Record<FileRegexKind, (path: string) => boolean> = {} as any;
for (const key in REGEX) {
  const kind = key as FileRegexKind;
  matchers[kind] = (path: string): boolean => REGEX[kind].test(path);
}

export function isOfKind(kind: FileRegexKind, path: string) {
  return matchers[kind](path);
}
