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
import { FileTypes, isOfKind } from "@kie-tools-core/workspaces-git-fs/dist/constants/ExtensionHelper";
import { EditorEnvelopeConfig } from "./EditorEnvelopeLocatorApi";

export const GLOB_PATTERN = {
  all: "**/*",
  dmn: "**/*.dmn",
  bpmn: "**/*.bpmn?(2)",
  scesim: "**/*.scesim",
  pmml: "**/*.pmml",
};

export const supportedFileExtensionArray = [
  FileTypes.DMN,
  FileTypes.BPMN,
  FileTypes.BPMN2,
  FileTypes.SCESIM,
  FileTypes.PMML,
];

export type SupportedFileExtensions = typeof supportedFileExtensionArray[number];

export function isModel(path: string): boolean {
  return isOfKind("dmn", path) || isOfKind("bpmn", path) || isOfKind("pmml", path);
}

export function isEditable(path: string): boolean {
  return isModel(path) || isOfKind("scesim", path);
}

export class EditorEnvelopeLocatorFactory {
  public create(args: { targetOrigin: string; editorEnvelopeConfig: EditorEnvelopeConfig[] }) {
    return new EditorEnvelopeLocator(
      args.targetOrigin,
      args.editorEnvelopeConfig.map((config) => {
        return new EnvelopeMapping({
          type: config.type,
          filePathGlob: config.filePathGlob,
          resourcesPathPrefix: config.resourcesPathPrefix,
          envelopeContent: { type: EnvelopeContentType.PATH, path: config.path },
        });
      })
    );
  }
}
