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

export enum CorsProxyHeaderKeys {
  TARGET_URL = "target-url",
  INSECURELY_DISABLE_TLS_CERTIFICATE_VALIDATION = "insecurely-disable-tls-certificate-validation",
  DISABLE_ENCODING = "disable-encoding",
}

export type CorsConfig = {
  exposeHeaders: string[];
  allowMethods: string[];
  allowHeaders: string[];
  customHeaders: Record<string, string>;
};

export interface CorsProxy<Req, Res> {
  handle(req: Req, res: Res, next: Function): void;
}
