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

function getAllowedOrigins(): string[] {
  const origins = process.env.CORS_PROXY_ALLOWED_ORIGINS || "";
  const originsList = origins.split(",").map((o) => o.trim());

  if (originsList.some((o) => o === "")) {
    throw new Error("Invalid origin: empty origins are not allowed in CORS_PROXY_ALLOWED_ORIGINS.");
  }

  if (originsList.some((o) => o === "*")) {
    throw new Error('Invalid origin: wildcard "*" is not allowed in CORS_PROXY_ALLOWED_ORIGINS.');
  }

  return originsList;
}

function getAllowedHosts(): string[] {
  const hosts = process.env.CORS_PROXY_ALLOWED_HOSTS || "localhost,*.github.com";
  const hostsList = hosts.split(",").map((o) => o.trim());

  if (hostsList.some((o) => o === "")) {
    throw new Error("Invalid host: empty hosts are not allowed in CORS_PROXY_ALLOWED_HOSTS.");
  }

  if (hostsList.some((o) => o === "*")) {
    throw new Error('Invalid host: wildcard "*" is not allowed in CORS_PROXY_ALLOWED_HOSTS.');
  }

  return hostsList;
}

export const run = () => {
  startServer({
    allowedOrigins: getAllowedOrigins(),
    port: getPort(),
    verbose: process.env.CORS_PROXY_VERBOSE === "true",
    hostsToUseHttp: (process.env.CORS_PROXY_USE_HTTP_FOR_HOSTS || undefined)?.split(",") ?? [],
    allowedHosts: getAllowedHosts(),
  });
};

run();
