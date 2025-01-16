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

import * as pingresponse from "./PingResponse";

export async function ping(extendedServicesURL: URL): Promise<pingresponse.PingResponse> {
  const extendedServicesPingURL = new URL("/ping", extendedServicesURL);

  try {
    console.debug("[Extended Services Extension] Pinging: " + extendedServicesPingURL.toString());
    const response = await fetch(extendedServicesPingURL.toString());
    if (response.ok) {
      const responseData = (await response.json()) as pingresponse.PingResponse;
      return responseData;
    } else {
      throw new Error(
        "Failed to ping service at " +
          extendedServicesURL +
          " with error " +
          response.status +
          "and message " +
          response.statusText
      );
    }
  } catch (error) {
    throw new Error("Failed to ping service at " + extendedServicesURL + " with error " + error.message);
  }
}
