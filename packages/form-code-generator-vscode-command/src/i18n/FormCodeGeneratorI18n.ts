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

import { ReferenceDictionary } from "@kie-tools-core/i18n/dist/core";

interface FormCodeGeneratorDictionary extends ReferenceDictionary {
  generateFormCode: {
    selectProjectFolder: string;
    notFoundProjectsTarget: string;
    notFoundJsonSchema: string;
    uiLibraryPlaceholder: string;
    customFormsPlaceholder: string;
    generateForHumanTasks: string;
    generateForSpecificHumanTasks: string;
    optionPlaceholder: string;
    userTaskPlaceholder: string;
    parsingFailed(files: string): string;
    uiLibraryNotAvailable(uiLibrary: string): string;
    successFormGeneration(files: string): string;
    errorFormGeneration(files: string): string;
  };
}

export interface FormCodeGeneratorI18n extends FormCodeGeneratorDictionary {}
