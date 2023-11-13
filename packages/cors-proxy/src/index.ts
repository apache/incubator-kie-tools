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

import { startServer } from "./proxy";

function getPort(): number {
  const port = Number(process.env.CORS_PROXY_HTTP_PORT);
  if (!isNaN(port)) {
    return port;
  }
  return 8080;
}

export const run = () => {
  startServer({
    port: getPort(),
    origin: process.env.CORS_PROXY_ORIGIN ?? "*",
    verbose: process.env.CORS_PROXY_VERBOSE === "true",
  });
};

run();
