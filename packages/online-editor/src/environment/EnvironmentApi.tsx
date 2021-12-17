/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import { EnvironmentVariables } from "./EnvironmentContext";

const ENV_FILE_PATH = "/env.json";

export async function fetchEnvJson(): Promise<EnvironmentVariables> {
  const response = await fetch(ENV_FILE_PATH);

  if (!response.ok) {
    throw new Error(`Failed to fetch ${ENV_FILE_PATH}: ${response.statusText}`);
  }

  return (await response.json()) as EnvironmentVariables;
}
