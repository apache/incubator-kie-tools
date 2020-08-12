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
export interface File {
  fileName: string;
  fileExtension: string;
  getFileContents: () => Promise<string | undefined>;
  isReadOnly: boolean;
}

/**
 * Default implementation of an empty DMN file; used when creating _new_ DMN files.
 */
export const EMPTY_FILE_DMN = {
  fileName: "new-file",
  fileExtension: "dmn",
  getFileContents: () => Promise.resolve(""),
  isReadOnly: false
};

/**
 * Default implementation of an empty BPMN file; used when creating _new_ BPMN files.
 */
export const EMPTY_FILE_BPMN = {
  fileName: "new-file",
  fileExtension: "bpmn",
  getFileContents: () => Promise.resolve(""),
  isReadOnly: false
};

/**
 * Default implementation of an empty SCESIM file; used when creating _new_ SCESIM files.
 */
export const EMPTY_FILE_SCESIM = {
  fileName: "new-file",
  fileExtension: "scesim",
  getFileContents: () => Promise.resolve(""),
  isReadOnly: false
};

/**
 * Helper method to create new, empty files, for different file extensions.
 * @param fileExtension The extension of the file.
 */
export function newFile(fileExtension: string): File {
  return {
    fileName: "new-file",
    fileExtension: fileExtension,
    getFileContents: () => Promise.resolve(""),
    isReadOnly: false
  };
}
