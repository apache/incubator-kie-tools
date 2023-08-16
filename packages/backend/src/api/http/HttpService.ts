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

import { Service } from "..";
import { HttpBridge } from "./HttpBridge";
import { HttpResponse } from "./HttpResponse";

/**
 * A service that makes HTTP requests.
 */
export abstract class HttpService implements Service {
  private bridge: HttpBridge | undefined;

  public abstract identify(): string;

  public async start(): Promise<void> {
    /* Nothing to do here as default implementation */
  }

  public stop(): void {
    /* Nothing to do here as default implementation */
  }

  public async satisfyRequirements(): Promise<boolean> {
    return true;
  }

  /**
   * Register the HTTP bridge to be used when making requests.
   * @param bridge The HTTP bridge.
   */
  public registerHttpBridge(bridge: HttpBridge): void {
    this.bridge = bridge;
  }

  /**
   * Execute a request.
   * @param endpoint Full path endpoint.
   * @param body Optional request body.
   * @returns The Http response.
   */
  public execute(endpoint: string, body?: any): Promise<HttpResponse> {
    if (!this.bridge) {
      return Promise.reject("Service bridge is not registered.");
    }
    return this.bridge.request({ endpoint: endpoint, body: body });
  }
}
