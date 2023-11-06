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

import { routes } from "../routes";

export interface AppData {
  appName: string;
  showDisclaimer: boolean;
  dataIndexUrl: string;
}

export async function fetchAppData(): Promise<AppData> {
  const response = await fetch(routes.dataJson.path({}));
  return (await response.json()) as AppData;
}

export async function verifyDataIndex(dataIndexUrl?: string): Promise<boolean> {
  if (!dataIndexUrl) {
    return false;
  }

  try {
    const response = await fetch(dataIndexUrl, {
      headers: {
        "Content-Type": "application/json",
      },
      method: "POST",
      body: '{"query":""}',
    });
    return response.status === 200;
  } catch (e) {
    return false;
  }
}
