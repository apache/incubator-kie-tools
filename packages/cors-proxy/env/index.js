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

const { varsWithName, composeEnv, getOrDefault } = require("@kie-tools-scripts/build-env");

module.exports = composeEnv([require("@kie-tools/root-env/env")], {
  vars: varsWithName({
    CORS_PROXY__allowedOrigins: {
      default: "http://localhost:9001",
      description: "Comma-separated list of allowed origins used to set on the 'Access-Control-Allow-Origin' header",
    },
    CORS_PROXY__port: {
      default: 8080,
      description: "HTTP Port the proxy should listen to",
    },
    CORS_PROXY__verbose: {
      default: true,
      description: "Allows the proxy to run in verbose mode... useful to trace requests on development environments",
    },
    CORS_PROXY__useHttpForHosts: {
      default: true,
      description: "Use `http` as default protocol for proxied requests. If `false`, `https` is used.",
    },
    CORS_PROXY__allowHosts: {
      default: "localhost,*.github.com",
      description: "Comma-separated list of allowed host patterns. Supports wildcards (e.g., '*.example.com').",
    },
  }),
  get env() {
    return {
      corsProxy: {
        dev: {
          allowedOrigins: getOrDefault(this.vars.CORS_PROXY__allowedOrigins),
          port: getOrDefault(this.vars.CORS_PROXY__port),
          verbose: getOrDefault(this.vars.CORS_PROXY__verbose),
          useHttpForHosts: getOrDefault(this.vars.CORS_PROXY__useHttpForHosts),
          allowHosts: getOrDefault(this.vars.CORS_PROXY__allowHosts),
        },
      },
    };
  },
});
