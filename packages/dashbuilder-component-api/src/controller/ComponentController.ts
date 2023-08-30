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

import { DataSet, FilterRequest } from "../dataset";
import { FunctionCallRequest } from "../function";

/*
 * Controls component lifecycle and allows component communicate with Dashbuilder
 */
export interface ComponentController {
  /*
   * Set on init callback
   */
  setOnInit(onInit: (params: Map<string, any>) => void): void;

  /*
   * Sets the method to be called when dashbuilder sends a dataset update after a filter or when using pooling.
   * Params is optional and is basically the same params sent after onInit
   */
  setOnDataSet(onDataSet: (dataSet: DataSet, params?: Map<string, any>) => void): void;

  /*
   * Components must send ready their finish their setup (unless it is sent automatically by the controller implementation)
   */
  ready(): void;

  /*
   * Components can call this if the dataset or any other configuration is wrong. The message can detail what is wrong.
   */
  requireConfigurationFix(message: string): void;

  /*
   * Components must call this after the configuration is fixed by users.
   */
  configurationOk(): void;

  /*
   * Used to send a filter request to Dashbuilder
   */
  filter(filterRequest: FilterRequest): void;

  /*
   * Calls a function and returns a Promise that will be fulfilled when the response is received from DB.
   */
  callFunction(functionCallRequest: FunctionCallRequest): Promise<any>;
}
