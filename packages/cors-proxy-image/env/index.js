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

const { varsWithName, getOrDefault, composeEnv } = require("@kie-tools-scripts/build-env");

const rootEnv = require("@kie-tools/root-env/env");
const corsProxyEnv = require("@kie-tools/cors-proxy/env");

module.exports = composeEnv([rootEnv], {
  vars: varsWithName({
    CORS_PROXY_IMAGE__imageRegistry: {
      default: "docker.io",
      description: "E.g., `docker.io` or `quay.io`.",
    },
    CORS_PROXY_IMAGE__imageAccount: {
      default: "apache",
      description: "E.g,. `apache` or `kie-tools-bot`",
    },
    CORS_PROXY_IMAGE__imageName: {
      default: "incubator-kie-cors-proxy",
      description: "Name of the image itself.",
    },
    CORS_PROXY_IMAGE__imageBuildTag: {
      default: rootEnv.env.root.streamName,
      description: "Tag version of this image. E.g., `main` or `10.0.x` or `10.0.0",
    },
    CORS_PROXY_IMAGE__imagePort: {
      default: corsProxyEnv.env.corsProxy.dev.port,
      description: "HTTP port where the CORS proxy will run inside this image.",
    },
    CORS_PROXY_IMAGE__imageAllowedOrigins: {
      default: corsProxyEnv.env.corsProxy.dev.allowedOrigins,
      description: "Comma-separated list of allowed origins for the CORS proxy running inside this image.",
    },
    CORS_PROXY_IMAGE__imageVerbose: {
      default: false,
      description: "Toggle verbose mode on the CORS proxy logs.",
    },
    CORS_PROXY_IMAGE__imageAllowHosts: {
      default: corsProxyEnv.env.corsProxy.dev.allowHosts,
      description: "Comma-separated list of allowed host patterns for domain filtering. Supports wildcards.",
    },
  }),
  get env() {
    return {
      corsProxyImage: {
        image: {
          registry: getOrDefault(this.vars.CORS_PROXY_IMAGE__imageRegistry),
          account: getOrDefault(this.vars.CORS_PROXY_IMAGE__imageAccount),
          name: getOrDefault(this.vars.CORS_PROXY_IMAGE__imageName),
          buildTag: getOrDefault(this.vars.CORS_PROXY_IMAGE__imageBuildTag),
          port: getOrDefault(this.vars.CORS_PROXY_IMAGE__imagePort),
          allowedOrigins: getOrDefault(this.vars.CORS_PROXY_IMAGE__imageAllowedOrigins),
          verbose: getOrDefault(this.vars.CORS_PROXY_IMAGE__imageVerbose),
          allowHosts: getOrDefault(this.vars.CORS_PROXY_IMAGE__imageAllowHosts),
        },
      },
    };
  },
});
