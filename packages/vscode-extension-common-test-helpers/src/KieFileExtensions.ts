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

const BPMN_EXTENSIONS = ".bpmn";
const DMN_EXTENSIONS = ".dmn";
const SCESIM_EXTENSIONS = ".scesim";
const PMML_EXTENSIONS = ".pmml";

/**
 * Gets supported kie editors file extensions that opens in single view mode.
 *
 * @returns regular expression with supported extensions.
 */
export function getSingleViewExtensionsRegExp(): RegExp {
  return new RegExp(`${BPMN_EXTENSIONS}|${DMN_EXTENSIONS}|${SCESIM_EXTENSIONS}|${PMML_EXTENSIONS}$`);
}

/**
 * Checks if the file is kie editor with single view.
 *
 * @param fileName a name of the file to be checked.
 * @returns true or false.
 */
export function isKieEditorWithSingleView(fileName: string): boolean {
  return getSingleViewExtensionsRegExp().test(fileName);
}
