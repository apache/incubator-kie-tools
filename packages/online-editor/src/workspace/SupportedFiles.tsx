/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

export const SUPPORTED_FILES_TECHNICAL = ["wid", "scesim", "java", "xml", "md", "png", "svg"];
export const SUPPORTED_FILES_EDITABLE = ["dmn", "bpmn", "bpmn2", "pmml"];
export const SUPPORTED_FILES = [...SUPPORTED_FILES_EDITABLE, ...SUPPORTED_FILES_TECHNICAL];

export const SUPPORTED_FILES_TECHNICAL_PATTERN = globPattern(SUPPORTED_FILES_TECHNICAL);
export const SUPPORTED_FILES_EDITABLE_PATTERN = globPattern(SUPPORTED_FILES_EDITABLE);
export const SUPPORTED_FILES_PATTERN = globPattern(SUPPORTED_FILES);

function globPattern(extensions: string[]) {
  return `*.{${extensions.join(",")}}`;
}
