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

/* dmn-testing-models module Directories */
export const DMN_1_5_DIRECTORY = "DMNv1_5";
export const DMN_1_x_DIRECTORY = "DMNv1_x";
export const VALID_MODELS_DIRECTORY = "valid_models";
export const MULTIPLE_MODELS_DIRECTORY = "multiple";
export const FULL_1_5_DIRECTORY = ".." + path.sep + VALID_MODELS_DIRECTORY + path.sep + DMN_1_5_DIRECTORY + path.sep;
export const FULL_1_x_DIRECTORY = ".." + path.sep + VALID_MODELS_DIRECTORY + path.sep + DMN_1_x_DIRECTORY + path.sep;

/* jbang scripts */
export const JBANG_SCRIPTS_DIRECTORY = "tests" + path.sep + "jbang" + path.sep;
export const JBANG_DMN_VALIDATION_SCRIPT_PATH = JBANG_SCRIPTS_DIRECTORY + "dmnValidation.java";
