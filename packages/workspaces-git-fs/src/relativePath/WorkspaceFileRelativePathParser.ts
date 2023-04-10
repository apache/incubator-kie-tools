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
import { basename, extname, parse } from "path";
import { isOfKind, FileTypes } from "../constants/ExtensionHelper";

export function parseWorkspaceFileRelativePath(relativePath: string) {
  const extension = extractExtension(relativePath);
  return {
    relativePathWithoutExtension: relativePath.substring(0, relativePath.lastIndexOf("." + extension)) || relativePath,
    relativeDirPath: parse(relativePath).dir,
    extension: extension,
    nameWithoutExtension: basename(relativePath, `.${extension}`),
    name: basename(relativePath),
  };
}

export function extractExtension(relativePath: string) {
  const fileName = basename(relativePath).toLowerCase();
  if (fileName.includes(".")) {
    if (isOfKind("swJson", fileName)) {
      return FileTypes.SW_JSON;
    }
    if (isOfKind("swYml", fileName)) {
      return FileTypes.SW_YML;
    }
    if (isOfKind("swYaml", fileName)) {
      return FileTypes.SW_YAML;
    }
    if (isOfKind("yardJson", fileName)) {
      return FileTypes.YARD_JSON;
    }
    if (isOfKind("yardYml", fileName)) {
      return FileTypes.YARD_YML;
    }
    if (isOfKind("yardYaml", fileName)) {
      return FileTypes.YARD_YAML;
    }
    if (isOfKind("dashYml", fileName)) {
      return FileTypes.DASH_YML;
    }
    if (isOfKind("dashYaml", fileName)) {
      return FileTypes.DASH_YAML;
    }
    if (isOfKind("dmn", fileName)) {
      return FileTypes.DMN;
    }
    if (isOfKind("bpmn", fileName)) {
      return FileTypes.BPMN;
    }
    if (isOfKind("pmml", fileName)) {
      return FileTypes.PMML;
    } else {
      return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
  } else {
    return extname(relativePath);
  }
}
