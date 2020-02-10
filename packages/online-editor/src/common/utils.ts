/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

export function extractFileExtension(fileName: string) {
  return fileName
    .split(".")
    .pop()
    ?.match(/[\w\d]+/)
    ?.pop();
}

export function removeFileExtension(fileName: string) {
  const fileExtension = extractFileExtension(fileName);

  if (!fileExtension) {
    return fileName;
  }

  return fileName.substr(0, fileName.length - fileExtension.length - 1);
}

export function removeDirectories(filePath: string) {
  return filePath.split("/").pop();
}
