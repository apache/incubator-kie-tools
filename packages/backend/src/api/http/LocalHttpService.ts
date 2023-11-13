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

import { HttpResponse } from "./HttpResponse";
import { HttpService } from "./HttpService";

/**
 * A service that makes local HTTP requests.
 */
export abstract class LocalHttpService extends HttpService {
  private readonly hostname = "http://localhost";
  private port: number | undefined;

  /**
   * Register the local HTTP port to use when making requests.
   * @param port The port to be accessed.
   */
  public registerPort(port: number): void {
    this.port = port;
  }

  /**
   * Execute a local request.
   * @param path Path of the local endpoint.
   * @param body Optional request body.
   * @returns The Http response.
   */
  public execute(path: string, body?: any): Promise<HttpResponse> {
    if (!this.port) {
      return Promise.reject("Local port not registered.");
    }
    return super.execute(`${this.hostname}:${this.port}${path}`, body);
  }
}
