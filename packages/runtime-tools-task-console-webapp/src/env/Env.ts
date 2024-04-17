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

import { ENV_FILE_PATH } from "./EnvConstants";
import { EnvJson } from "./EnvJson";

export async function initEnv() {
  return fetch(ENV_FILE_PATH)
    .then(async (response) => {
      if (!response.ok) {
        throw new Error(`Failed to fetch ${ENV_FILE_PATH}: ${response.statusText}`);
      }

      const envJson = await response.json();
      return envJson as EnvJson;
    })
    .catch((e) => {
      console.error(e);
    });
}
