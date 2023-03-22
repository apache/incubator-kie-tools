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

import {
  EditorEnvelopeLocator,
  EnvelopeContentType,
  EnvelopeMapping,
} from "@kie-tools-core/editor/dist/api/EditorEnvelopeLocator";

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
  bpmn: "**/*.bpmn?(2)",
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

export type SupportedFileExtensions = typeof supportedFileExtensionArray[number];

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

export function isModel(path: string): boolean {
  return isDecision(path) || isWorkflow(path) || isScorecard(path);
}

export function isEditable(path: string): boolean {
  return isModel(path) || isTestScenario(path);
}

export class EditorEnvelopeLocatorFactory {
  public create(args: { targetOrigin: string }) {
    return new EditorEnvelopeLocator(args.targetOrigin, [
      new EnvelopeMapping({
        type: FileTypes.BPMN,
        filePathGlob: GLOB_PATTERN.bpmn,
        resourcesPathPrefix: "gwt-editors/bpmn",
        envelopeContent: { type: EnvelopeContentType.PATH, path: "bpmn-envelope.html" },
      }),
      new EnvelopeMapping({
        type: FileTypes.DMN,
        filePathGlob: GLOB_PATTERN.dmn,
        resourcesPathPrefix: "gwt-editors/dmn",
        envelopeContent: { type: EnvelopeContentType.PATH, path: "dmn-envelope.html" },
      }),
      new EnvelopeMapping({
        type: FileTypes.PMML,
        filePathGlob: GLOB_PATTERN.pmml,
        resourcesPathPrefix: "",
        envelopeContent: { type: EnvelopeContentType.PATH, path: "pmml-envelope.html" },
      }),
    ]);
  }
}
