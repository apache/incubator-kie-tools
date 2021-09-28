/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

/**
 * Definition of a File supported by the embedded-editor.
 */
export interface EmbeddedEditorFile {
  fileName: string;
  fileExtension: string;
  getFileContents: () => Promise<string | undefined>;
  isReadOnly: boolean;
  path?: string;
  kind?: "gist" | "external" | "local";
}

/**
 * Default implementation of an empty DMN file; used when creating _new_ DMN files.
 */
export const EMPTY_FILE_DMN = {
  fileName: "new-file",
  fileExtension: "dmn",
  getFileContents: () => Promise.resolve(""),
  isReadOnly: false,
  path: "/new-file.dmn",
};

/**
 * Default implementation of an empty BPMN file; used when creating _new_ BPMN files.
 */
export const EMPTY_FILE_BPMN = {
  fileName: "new-file",
  fileExtension: "bpmn",
  getFileContents: () => Promise.resolve(""),
  isReadOnly: false,
  path: "/new-file.bpmn",
};

/**
 * Default implementation of an empty SCESIM file; used when creating _new_ SCESIM files.
 */
export const EMPTY_FILE_SCESIM = {
  fileName: "new-file",
  fileExtension: "scesim",
  getFileContents: () => Promise.resolve(""),
  isReadOnly: false,
  path: "/new-file.scesim",
};

/**
 * Default implementation of an empty PMML file; used when creating _new_ PMML files.
 */
export const EMPTY_FILE_PMML = {
  fileName: "new-file",
  fileExtension: "pmml",
  getFileContents: () => Promise.resolve(""),
  isReadOnly: false,
  path: "/new-file.pmml",
};

/**
 * Helper method to create new, empty files, for different file extensions.
 * @param fileExtension The extension of the file.
 * @param contextPath Path associated with the context.
 * @param kind The kind of the file.
 */
export function newFile(
  fileExtension: string,
  contextPath: string = "",
  kind?: EmbeddedEditorFile["kind"]
): EmbeddedEditorFile {
  return {
    fileName: "new-file",
    fileExtension: fileExtension,
    getFileContents: () => Promise.resolve(""),
    isReadOnly: false,
    path: `${contextPath}/new-file.${fileExtension}`,
    kind,
  };
}
