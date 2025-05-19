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

module.exports = composeEnv([require("@kie-tools/root-env/env")], {
  vars: varsWithName({
    EXTENDED_SERVICES_JAVA__host: {
      default: "127.0.0.1",
      description:
        "Quarkus HTTP Host. Configures the IP address or host to which a Quarkus application binds for incoming HTTP requests.",
    },
    EXTENDED_SERVICES_JAVA__port: {
      default: "21345",
      description:
        "Quarkus HTTP Port. Configures the network port on which a Quarkus application accepts incoming HTTP requests.",
    },
  }),
  get env() {
    return {
      extendedServicesJava: {
        version: require("../package.json").version,
        host: getOrDefault(this.vars.EXTENDED_SERVICES_JAVA__host),
        port: getOrDefault(this.vars.EXTENDED_SERVICES_JAVA__port),
      },
    };
  },
});
