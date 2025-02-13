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
    RUNTIME_TOOLS_MANAGEMENT_CONSOLE_WEBAPP__dev_host: {
      default: "localhost",
      description: "Webpack dev server hostname",
    },
    RUNTIME_TOOLS_MANAGEMENT_CONSOLE_WEBAPP__dev_port: {
      default: "9025",
      description: "Webpack dev server port",
    },
    RUNTIME_TOOLS_MANAGEMENT_CONSOLE_WEBAPP__buildInfo: {
      default: `dev (${process.env.USER}) @ ${new Date().toISOString()}`,
      description: "Build information to be shown in the 'About' modal.",
    },
    RUNTIME_TOOLS_MANAGEMENT_CONSOLE_WEBAPP__appName: {
      default: "Apache KIE™ Management Console",
      description: "The name used to refer to a particular KIE Management Console distribution.",
    },
    RUNTIME_TOOLS_MANAGEMENT_CONSOLE_WEBAPP__oidcClientClientId: {
      default: "management-console-dev-webapp",
      description: "Client ID used for OpenID Connect client configuration.",
    },
  }),
  get env() {
    return {
      runtimeToolsManagementConsoleWebapp: {
        dev: {
          host: getOrDefault(this.vars.RUNTIME_TOOLS_MANAGEMENT_CONSOLE_WEBAPP__dev_host),
          port: getOrDefault(this.vars.RUNTIME_TOOLS_MANAGEMENT_CONSOLE_WEBAPP__dev_port),
          idp: {
            port: "9071",
          },
          securedRuntime: {
            port: "8080",
          },
          unsecuredRuntime: {
            port: "8081",
          },
        },
        oidcClient: {
          clientId: getOrDefault(this.vars.RUNTIME_TOOLS_MANAGEMENT_CONSOLE_WEBAPP__oidcClientClientId),
        },
        appName: getOrDefault(this.vars.RUNTIME_TOOLS_MANAGEMENT_CONSOLE_WEBAPP__appName),
        buildInfo: getOrDefault(this.vars.RUNTIME_TOOLS_MANAGEMENT_CONSOLE_WEBAPP__buildInfo),
        version: require("../package.json").version,
      },
    };
  },
});
