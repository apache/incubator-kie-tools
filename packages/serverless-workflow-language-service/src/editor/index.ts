/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
 * File language for an opened file in the text editor.
 */
export enum FileLanguage {
  JSON = "json",
  YAML = "yaml",
}

/**
 * Position with coordinates in a text editor.
 */
export type Position = {
  line: number;
  character: number;
};

/**
 * Find position (row number) of a node in a JSON by state name.
 *
 * @param fullText the full text where to search
 * @param stateName the name of the node
 * @param fileLanguage the current file type
 * @returns the position of the node, null if not found
 */
export const findPositionByStateName = (
  fullText: string,
  stateName: string,
  fileLanguage = FileLanguage.JSON
): Position | null => {
  if (!fullText || !stateName) {
    return null;
  }

  const fullTextSplit = fullText.split("\n");
  const nameRegExp = new RegExp(
    fileLanguage === FileLanguage.YAML ? `name\\s*:\\s*${stateName}` : `"name"\\s*:\\s*"${stateName}"`
  );

  for (let lineNum = 0, end = fullTextSplit.length; lineNum < end; lineNum++) {
    const line = fullTextSplit[lineNum];

    if (nameRegExp.test(line)) {
      const charNum = line.indexOf("name") - 1;
      return { line: lineNum + 1, character: charNum + 1 };
    }
  }

  return null;
};

/**
 * Get the file language from a filename or path
 *
 * @param fileName the filename or path
 * @returns the file language, null if found
 */
export const getFileLanguage = (fileName: string): FileLanguage | null => {
  if (/\.sw\.json$/i.test(fileName)) {
    return FileLanguage.JSON;
  }

  if (/\.sw\.(yml|yaml)/i.test(fileName)) {
    return FileLanguage.YAML;
  }

  return null;
};
