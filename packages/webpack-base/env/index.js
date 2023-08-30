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

const { varsWithName, get, str2bool, composeEnv } = require("@kie-tools-scripts/build-env");

module.exports = composeEnv([], {
  vars: varsWithName({
    WEBPACK__minimize: {
      default: undefined,
      description: "",
    },
    WEBPACK__tsLoaderTranspileOnly: {
      default: undefined,
      description: "",
    },
    WEBPACK__sourceMaps: {
      default: undefined,
      description: "",
    },
    WEBPACK__mode: {
      default: undefined,
      description: "",
    },
  }),
  get env() {
    return {
      webpack: {
        dev: {
          minimize: str2bool(get(this.vars.WEBPACK__minimize) ?? `${false}`),
          transpileOnly: str2bool(get(this.vars.WEBPACK__tsLoaderTranspileOnly) ?? `${false}`),
          sourceMaps: str2bool(get(this.vars.WEBPACK__sourceMaps) ?? `${true}`),
          mode: get(this.vars.WEBPACK__mode) ?? "development",
        },
        prod: {
          minimize: str2bool(get(this.vars.WEBPACK__minimize) ?? `${true}`),
          transpileOnly: str2bool(get(this.vars.WEBPACK__tsLoaderTranspileOnly) ?? `${false}`),
          sourceMaps: str2bool(get(this.vars.WEBPACK__sourceMaps) ?? `${false}`),
          mode: get(this.vars.WEBPACK__mode) ?? "production",
        },
      },
    };
  },
});
