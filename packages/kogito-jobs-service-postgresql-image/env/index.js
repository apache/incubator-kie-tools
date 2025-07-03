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
const sonataflowImageCommonEnv = require("@kie-tools/sonataflow-image-common/env");

module.exports = composeEnv([rootEnv, sonataflowImageCommonEnv], {
  vars: varsWithName({
    KOGITO_JOBS_SERVICE_POSTGRESQL_IMAGE__registry: {
      default: "docker.io",
      description: "The image registry.",
    },
    KOGITO_JOBS_SERVICE_POSTGRESQL_IMAGE__account: {
      default: "apache",
      description: "The image registry account.",
    },
    KOGITO_JOBS_SERVICE_POSTGRESQL_IMAGE__name: {
      default: "incubator-kie-kogito-jobs-service-postgresql",
      description: "The image name.",
    },
    KOGITO_JOBS_SERVICE_POSTGRESQL_IMAGE__buildTag: {
      default: rootEnv.env.root.streamName,
      description: "The image tag.",
    },
  }),
  get env() {
    return {
      kogitoJobsServicePostgresqlImage: {
        registry: getOrDefault(this.vars.KOGITO_JOBS_SERVICE_POSTGRESQL_IMAGE__registry),
        account: getOrDefault(this.vars.KOGITO_JOBS_SERVICE_POSTGRESQL_IMAGE__account),
        name: getOrDefault(this.vars.KOGITO_JOBS_SERVICE_POSTGRESQL_IMAGE__name),
        buildTag: getOrDefault(this.vars.KOGITO_JOBS_SERVICE_POSTGRESQL_IMAGE__buildTag),
        version: require("../package.json").version,
      },
    };
  },
});
