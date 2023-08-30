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

import dateFunctions from "!!raw-loader!../../resources/dateFunctions.txt";
import timeFunctions from "!!raw-loader!../../resources/timeFunctions.txt";
import selectFunctions from "!!raw-loader!../../resources/selectFunctions.txt";
import multipleSelectFunctions from "!!raw-loader!../../resources/multipleSelectFunctions.txt";
import checkboxGroupFunctions from "!!raw-loader!../../resources/checkboxGroupFunctions.txt";

export const DATE_FUNCTIONS = "date_functions";
export const TIME_FUNCTIONS = "time_functions";
export const SELECT_FUNCTIONS = "select_functions";
export const MULTIPLE_SELECT_FUNCTIONS = "multiple_select_functions";
export const CHECKBOX_GROUP_FUNCTIONS = "checkbox_group_functions";

const _staticBlocks: Map<string, string> = new Map<string, string>();

_staticBlocks.set(DATE_FUNCTIONS, dateFunctions);
_staticBlocks.set(TIME_FUNCTIONS, timeFunctions);
_staticBlocks.set(SELECT_FUNCTIONS, selectFunctions);
_staticBlocks.set(MULTIPLE_SELECT_FUNCTIONS, multipleSelectFunctions);
_staticBlocks.set(CHECKBOX_GROUP_FUNCTIONS, checkboxGroupFunctions);

export const getStaticCodeBlock = (blockName: string): string | undefined => {
  return _staticBlocks.get(blockName);
};
