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

const rootEnv = require("@kie-tools/root-env/env");
const redHatEnv = require("@osl/redhat-env/env");

module.exports = composeEnv([rootEnv, redHatEnv], {
  vars: varsWithName({
    OSL_DATA_INDEX_POSTGRESQL_IMAGE__artifactUrl: {
      default: "",
      description: "The internal repository where to find the Data Index PostgreSQL Quarkus app zip file.",
    },
    OSL_DATA_INDEX_POSTGRESQL_IMAGE__registry: {
      default: "registry.access.redhat.com",
      description: "The image registry.",
    },
    OSL_DATA_INDEX_POSTGRESQL_IMAGE__account: {
      default: "openshift-serverless-1",
      description: "The image registry account.",
    },
    OSL_DATA_INDEX_POSTGRESQL_IMAGE__name: {
      default: "logic-data-index-postgresql-rhel8",
      description: "The image name.",
    },
    OSL_DATA_INDEX_POSTGRESQL_IMAGE__buildTag: {
      default: rootEnv.env.root.streamName,
      description: "The image tag.",
    },
  }),
  get env() {
    return {
      oslDataIndexPostgresqlImage: {
        registry: getOrDefault(this.vars.OSL_DATA_INDEX_POSTGRESQL_IMAGE__registry),
        account: getOrDefault(this.vars.OSL_DATA_INDEX_POSTGRESQL_IMAGE__account),
        name: getOrDefault(this.vars.OSL_DATA_INDEX_POSTGRESQL_IMAGE__name),
        buildTag: getOrDefault(this.vars.OSL_DATA_INDEX_POSTGRESQL_IMAGE__buildTag),
        artifactUrl: getOrDefault(this.vars.OSL_DATA_INDEX_POSTGRESQL_IMAGE__artifactUrl),
      },
    };
  },
});
