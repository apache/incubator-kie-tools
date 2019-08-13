/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import { LanguageData } from "./LangaugeData";

/**
 * Responsible for returning a LanguageData for a file extension. When opening an Editor,
 * the LanguageData returned is used to find the resources needed for that editor to work.
 *
 * The LanguageData returned should be a custom class that implements LanguageData.
 */
export interface Router {
  /**
   * Returns the custom LanguageData class for a specific file extension.
   * @param fileExtension The file extension (i.e. "txt", or "png")
   */
  getLanguageData(fileExtension: string): LanguageData | undefined;

  /**
   * Responsible for transforming a relative URI path to an absolute URL inside the context of the application.
   * @param uri The relative path URI.
   */
  getRelativePathTo(uri: string): string;
}

/**
 * Exports the routes for a file extension
 */
export interface Routes {
  /**
   * Returns the routes for a file extension as a Map
   * @param router Router that will decide the relative path to prepend on the resources URLs
   */
  getRoutes(router: Router): Map<string, LanguageData>;
}
