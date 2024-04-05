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

import * as path from "path";

/* Local Directories */
export const LOCAL_MODELS_DIRECTORY = "tests-data--manual";
export const LOCAL_MODELS_1_4_DIRECTORY = "dmn-1_4--examples";
export const LOCAL_MODELS_OTHER_DIRECTORY = "other";
export const LOCAL_MODELS_1_4_DIRECTORY_FULL_PATH =
  ".." + path.sep + LOCAL_MODELS_DIRECTORY + path.sep + LOCAL_MODELS_1_4_DIRECTORY + path.sep;
export const LOCAL_MODELS_OTHER_DIRECTORY_FULL_PATH =
  ".." + path.sep + LOCAL_MODELS_DIRECTORY + path.sep + LOCAL_MODELS_OTHER_DIRECTORY + path.sep;

/* dmn-testing-models module Directories */
export const DMN_1_5_DIRECTORY = "DMNv1_5";
export const DMN_1_x_DIRECTORY = "DMNv1_x";
export const VALID_MODELS_DIRECTORY = "valid_models";
export const MULTIPLE_MODELS_DIRECTORY = "multiple";
export const FULL_1_5_DIRECTORY = ".." + path.sep + VALID_MODELS_DIRECTORY + path.sep + DMN_1_5_DIRECTORY + path.sep;
export const FULL_1_x_DIRECTORY = ".." + path.sep + VALID_MODELS_DIRECTORY + path.sep + DMN_1_x_DIRECTORY + path.sep;
export const FULL_1_x_MULTIPLE_DIRECTORY = FULL_1_x_DIRECTORY + MULTIPLE_MODELS_DIRECTORY + path.sep;

/* jbang scripts */
export const JBANG_SCRIPTS_DIRECTORY = "." + path.sep + "tests" + path.sep + "jbang" + path.sep;
export const JBANG_DMN_VALIDATION_SCRIPT_PATH = JBANG_SCRIPTS_DIRECTORY + "dmnValidation.java";

/* XSD Schemas */
export const XSD_SCHEMAS_DIRECTORY = ".." + path.sep + "src" + path.sep + "schemas" + path.sep;
export const DMN_1_5_XSD = XSD_SCHEMAS_DIRECTORY + "dmn-1_5" + path.sep + "DMN15.xsd";
