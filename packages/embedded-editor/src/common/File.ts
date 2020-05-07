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
import { EditorType } from "./EditorTypes";

export interface File {
  fileName: string;
  editorType: EditorType;
  getFileContents: () => Promise<string | undefined>;
  isReadOnly: boolean;
}

export const EMPTY_FILE_DMN = {
  fileName: "new-file",
  editorType: EditorType.DMN,
  getFileContents: () => Promise.resolve(""),
  isReadOnly: false
};

export const EMPTY_FILE_BPMN = {
  fileName: "new-file",
  editorType: EditorType.BPMN,
  getFileContents: () => Promise.resolve(""),
  isReadOnly: false
};

export const EMPTY_FILE_SCESIM = {
  fileName: "new-file",
  editorType: EditorType.SCESIM,
  getFileContents: () => Promise.resolve(""),
  isReadOnly: false
};

export function newFile(editorType: EditorType): File {
  return {
    fileName: "new-file",
    editorType: editorType,
    getFileContents: () => Promise.resolve(""),
    isReadOnly: false
  };
}