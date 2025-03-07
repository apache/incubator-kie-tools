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

const { getOrDefault, varsWithName, composeEnv } = require("@kie-tools-scripts/build-env");

const rootEnv = require("@kie-tools/root-env/env");

module.exports = composeEnv([rootEnv], {
  vars: varsWithName({
    EXTENDED_SERVICES__kieSandboxUrl: {
      default: "https://localhost:9001",
      description: "KIE Sandbox URL",
    },
    EXTENDED_SERVICES__version: {
      default: require("../package.json").version,
      description: "Extended Services version. Used by clients for checking compatibility.",
    },
    EXTENDED_SERVICES__ip: {
      default: "127.0.0.1",
      description: "Extended Services IP",
    },
    EXTENDED_SERVICES__port: {
      default: "21345",
      description: "HTTP port",
    },
    EXTENDED_SERVICES__nativeBinaryPath_macOS: {
      default: `./dist-dev/jitexecutor-native-darwin-${rootEnv.env.versions.kogito}-runner`,
      description: "",
    },
    EXTENDED_SERVICES__nativeBinaryPath_linux: {
      default: `./dist-dev/jitexecutor-native-linux-${rootEnv.env.versions.kogito}-runner`,
      description: "",
    },
    EXTENDED_SERVICES__nativeBinaryPath_win32: {
      default: `.\\dist-dev\\jitexecutor-native-win32-${rootEnv.env.versions.kogito}-runner.exe`,
      description: "",
    },
  }),
  get env() {
    return {
      extendedServices: {
        version: getOrDefault(this.vars.EXTENDED_SERVICES__version),
        kieSandboxUrl: getOrDefault(this.vars.EXTENDED_SERVICES__kieSandboxUrl),
        ip: getOrDefault(this.vars.EXTENDED_SERVICES__ip),
        port: getOrDefault(this.vars.EXTENDED_SERVICES__port),
        jitexecutor: {
          nativeBinaryPath: {
            macOS: getOrDefault(this.vars.EXTENDED_SERVICES__nativeBinaryPath_macOS),
            linux: getOrDefault(this.vars.EXTENDED_SERVICES__nativeBinaryPath_linux),
            win32: getOrDefault(this.vars.EXTENDED_SERVICES__nativeBinaryPath_win32),
          },
        },
      },
    };
  },
});
