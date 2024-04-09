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

const corsProxyEnv = require("@kie-tools/cors-proxy/env");

module.exports = composeEnv([require("@kie-tools/root-env/env")], {
  vars: varsWithName({
    CORS_PROXY_IMAGE__imageRegistry: {
      default: "quay.io",
      description: "",
    },
    CORS_PROXY_IMAGE__imageAccount: {
      default: "kie-tools",
      description: "",
    },
    CORS_PROXY_IMAGE__imageName: {
      default: "cors-proxy-image",
      description: "",
    },
    CORS_PROXY_IMAGE__imageBuildTags: {
      default: "latest",
      description: "",
    },
    CORS_PROXY_IMAGE__imagePort: {
      default: corsProxyEnv.env.corsProxy.dev.port,
      description: "",
    },
    CORS_PROXY_IMAGE__imageOrigin: {
      default: corsProxyEnv.env.corsProxy.dev.origin,
      description: "",
    },
    CORS_PROXY_IMAGE__imageVerbose: {
      default: false,
      description: "",
    },
  }),
  get env() {
    return {
      corsProxyImage: {
        image: {
          registry: getOrDefault(this.vars.CORS_PROXY_IMAGE__imageRegistry),
          account: getOrDefault(this.vars.CORS_PROXY_IMAGE__imageAccount),
          name: getOrDefault(this.vars.CORS_PROXY_IMAGE__imageName),
          buildTags: getOrDefault(this.vars.CORS_PROXY_IMAGE__imageBuildTags),
          port: getOrDefault(this.vars.CORS_PROXY_IMAGE__imagePort),
          origin: getOrDefault(this.vars.CORS_PROXY_IMAGE__imageOrigin),
          verbose: getOrDefault(this.vars.CORS_PROXY_IMAGE__imageVerbose),
        },
      },
    };
  },
});
