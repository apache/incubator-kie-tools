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

import { FileLanguage, getFileLanguageEditorLS } from "@kie-tools/json-yaml-language-service/dist/api";

/**
 * Get the file language from a filename or path
 *
 * @param fileName the filename or path
 * @returns the file language, null if not found
 */
export const getFileLanguage = (fileName: string): FileLanguage | null => {
  return /\.sw\.\w+$/i.test(fileName) ? getFileLanguageEditorLS(fileName) : null;
};

/**
 * Get the file language from a filename or path or throw an Error if it can't be determined
 *
 * @param fileName the filename or path
 * @returns the file language, null if not found
 * @throws Error if it can't be determined
 */
export const getFileLanguageOrThrow = (fileName: string): FileLanguage => {
  const fileLanguage = getFileLanguage(fileName);
  if (!fileLanguage) {
    throw new Error("Couldn't determine FileLanguage for " + fileName);
  }
  return fileLanguage;
};

export { FileLanguage };
export * from "./SwfLanguageServiceChannelApi";
