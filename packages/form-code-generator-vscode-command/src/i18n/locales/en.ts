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

import { FormCodeGeneratorI18n } from "../FormCodeGeneratorI18n";

export const en: FormCodeGeneratorI18n = {
  generateFormCode: {
    selectProjectFolder: "Select project folder",
    notFoundProjectsTarget: `Couldn't find project's "target" folder. Please install your project before using this command.`,
    notFoundJsonSchema: `Couldn't find any JSON Schema, did you install your project ("mvn clean install")?`,
    uiLibraryPlaceholder: "Select the UI library for the generated form(s)",
    customFormsPlaceholder: "You already have custom forms in this project. Do you want to override them?",
    generateForHumanTasks: "Generate form code for all User Tasks",
    generateForSpecificHumanTasks: "Generate form code for specific User Tasks",
    optionPlaceholder: "Select an option",
    userTaskPlaceholder: "Choose the User Tasks",
    parsingFailed: (files: string): string => `JSON Schema parsing failed for the following files: ${files}`,
    uiLibraryNotAvailable: (uiLibrary: string): string => `The "${uiLibrary}" UI library isn't available.`,
    successFormGeneration: (files: string): string => `Success generating form code for the following files: ${files}`,
    errorFormGeneration: (files: string): string => `Error generating form code for the following files: ${files}`,
  },
};
