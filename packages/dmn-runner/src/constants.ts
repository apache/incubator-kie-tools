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

export const TIME_FEEL_REGEXP = /time\("([^.,\s]*)"\)/g;
export const TIME_ENUM_REGEXP = /time\("([^.,\s]*)"\)[,]{0,1}[\s]*/g;

export const DATE_FEEL_REGEXP = /date\("([^.,\s]*)"\)/g;
export const DATE_ENUM_REGEXP = /date\("([^.,\s]*)"\)[,]{0,1}[\s]*/g;

export const DATE_AND_TIME_FEEL_REGEXP = /date and time\("([^.,\s]*)"\)/g;
export const DATE_AND_TIME_ENUM_REGEXP = /date and time\("([^.,\s]*)"\)[,]{0,1}[\s]*/g;

export const DURATION_FEEL_REGEXP = /duration\("([^.,\s]*)"\)/g;
export const DURATION_ENUM_REGEXP = /duration\("([^.,\s]*)"\)[,]{0,1}[\s]*/g;

export const DAYS_AND_TIME_DURATION_FORMAT = "days and time duration";
export const DAYS_AND_TIME_DURATION_REGEXP =
  /^(-|\+)?P(?:([-+]?[0-9]*)D)?(?:T(?:([-+]?[0-9]*)H)?(?:([-+]?[0-9]*)M)?(?:([-+]?[0-9]*)S)?)?$/;

export const YEARS_AND_MONTHS_DURATION_FORMAT = "years and months duration";
export const YEARS_AND_MONTHS_DURATION_REGEXP = /^(-|\+)?P(?:([-+]?[0-9]*)Y)?(?:([-+]?[0-9]*)M)?$/;

export const X_DMN_DESCRIPTIONS_KEYWORD = "x-dmn-descriptions";
export const X_DMN_ALLOWED_VALUES_KEYWORD = "x-dmn-allowed-values";
export const X_DMN_TYPE_KEYWORD = "x-dmn-type";
export const RECURSION_KEYWORD = "recursion";
export const RECURSION_REF_KEYWORD = "recursionRef";

export const SCHEMA_DRAFT4 = "http://json-schema.org/draft-04/schema#";
export const JSON_SCHEMA_INPUT_SET_PATH = "definitions.InputSet.properties";
