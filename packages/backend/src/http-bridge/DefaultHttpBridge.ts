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

import axios, { AxiosError } from "axios";
import { HttpBridge, HttpRequest, HttpResponse } from "../api";

/**
 * Default bridge between channels and external services that are accessed through HTTP.
 */
export class DefaultHttpBridge implements HttpBridge {
  public async request(request: HttpRequest): Promise<HttpResponse> {
    try {
      const response = request.body
        ? await axios.post(request.endpoint, request.body)
        : await axios.get(request.endpoint);
      return { body: response.data };
    } catch (e) {
      let message = e.message;

      const axiosError = e as AxiosError;
      if (axiosError.config?.url) {
        message += " " + axiosError.config.url;
      }

      return Promise.reject(message);
    }
  }
}
