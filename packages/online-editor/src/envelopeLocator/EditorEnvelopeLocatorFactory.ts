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

import { EditorEnvelopeLocator, EnvelopeContentType, EnvelopeMapping } from "@kie-tools-core/editor/dist/api";
import { FileTypes, isOfKind } from "@kie-tools-core/workspaces-git-fs/dist/constants/ExtensionHelper";
import { EditorConfig } from "./EditorEnvelopeLocatorApi";

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

export type SupportedFileExtensions = (typeof supportedFileExtensionArray)[number];

export function isModel(path: string): boolean {
  return isOfKind("dmn", path) || isOfKind("bpmn", path) || isOfKind("pmml", path);
}

export function isEditable(path: string): boolean {
  return isModel(path) || isOfKind("scesim", path);
}

export class EditorEnvelopeLocatorFactory {
  public create(args: { targetOrigin: string; editorsConfig: EditorConfig[] }) {
    return new EditorEnvelopeLocator(
      args.targetOrigin,
      args.editorsConfig.map((config) => {
        return new EnvelopeMapping({
          type: config.extension,
          filePathGlob: config.filePathGlob,
          resourcesPathPrefix: config.editor.resourcesPathPrefix,
          envelopeContent: { type: EnvelopeContentType.PATH, path: config.editor.path },
        });
      })
    );
  }
}
