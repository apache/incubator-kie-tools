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

const sonataflowBuilderImageEnv = require("@kie-tools/sonataflow-builder-image/env");
const sonataflowDevModeImageEnv = require("@kie-tools/sonataflow-devmode-image/env");
const kogitoJobsServiceEphemeralImageEnv = require("@kie/kogito-jobs-service-ephemeral-image/env");
const kogitoJobsServicePostgresqlImageEnv = require("@kie/kogito-jobs-service-postgresql-image/env");
const kogitoDataIndexEphemeralImageEnv = require("@kie/kogito-data-index-ephemeral-image/env");
const kogitoDataIndexPostgresqlImageEnv = require("@kie/kogito-data-index-postgresql-image/env");
const kogitoDBMigratorToolImageEnv = require("@kie-tools/kogito-db-migrator-tool-image/env");
const rootEnv = require("@kie-tools/root-env/env");

module.exports = composeEnv([rootEnv, sonataflowBuilderImageEnv, sonataflowDevModeImageEnv], {
  vars: varsWithName({
    SONATAFLOW_OPERATOR__registry: {
      default: "docker.io",
      description: "E.g., `docker.io` or `quay.io`.",
    },
    SONATAFLOW_OPERATOR__account: {
      default: "apache",
      description: "E.g,. `apache` or `kie-tools-bot`",
    },
    SONATAFLOW_OPERATOR__name: {
      default: "incubator-kie-sonataflow-operator",
      description: "Name of the image itself.",
    },
    SONATAFLOW_OPERATOR__buildTag: {
      default: rootEnv.env.root.streamName,
      description: "Tag version of this image. E.g., `main` or `10.0.x` or `10.0.0",
    },
    SONATAFLOW_OPERATOR__platformTag: {
      default: rootEnv.env.root.streamName,
      description:
        "Tag version the platform Tag - The default tag used for all the images managed by the operator. It changes the version.go file. E.g., `main` or `10.0.x` or `10.0.0",
    },
    SONATAFLOW_OPERATOR__sonataflowBuilderImage: {
      default: `${sonataflowBuilderImageEnv.env.sonataflowBuilderImage.registry}/${sonataflowBuilderImageEnv.env.sonataflowBuilderImage.account}/${sonataflowBuilderImageEnv.env.sonataflowBuilderImage.name}:${sonataflowBuilderImageEnv.env.sonataflowBuilderImage.buildTag}`,
      description: "Sonataflow Builder image",
    },
    SONATAFLOW_OPERATOR__sonataflowDevModeImage: {
      default: `${sonataflowDevModeImageEnv.env.sonataflowDevModeImage.registry}/${sonataflowDevModeImageEnv.env.sonataflowDevModeImage.account}/${sonataflowDevModeImageEnv.env.sonataflowDevModeImage.name}:${sonataflowDevModeImageEnv.env.sonataflowDevModeImage.buildTag}`,
      description: "Sonataflow DevMode image",
    },
    SONATAFLOW_OPERATOR__kogitoJobsServiceEphemeralImage: {
      default: `${kogitoJobsServiceEphemeralImageEnv.env.kogitoJobsServiceEphemeralImage.registry}/${kogitoJobsServiceEphemeralImageEnv.env.kogitoJobsServiceEphemeralImage.account}/${kogitoJobsServiceEphemeralImageEnv.env.kogitoJobsServiceEphemeralImage.name}:${kogitoJobsServiceEphemeralImageEnv.env.kogitoJobsServiceEphemeralImage.buildTag}`,
      description: "Kogito Jobs Service Ephemeral image",
    },
    SONATAFLOW_OPERATOR__kogitoJobsServicePostgresqlImage: {
      default: `${kogitoJobsServicePostgresqlImageEnv.env.kogitoJobsServicePostgresqlImage.registry}/${kogitoJobsServicePostgresqlImageEnv.env.kogitoJobsServicePostgresqlImage.account}/${kogitoJobsServicePostgresqlImageEnv.env.kogitoJobsServicePostgresqlImage.name}:${kogitoJobsServicePostgresqlImageEnv.env.kogitoJobsServicePostgresqlImage.buildTag}`,
      description: "Kogito Jobs Service PostgreSQL image",
    },
    SONATAFLOW_OPERATOR__kogitoDataIndexEphemeralImage: {
      default: `${kogitoDataIndexEphemeralImageEnv.env.kogitoDataIndexEphemeralImage.registry}/${kogitoDataIndexEphemeralImageEnv.env.kogitoDataIndexEphemeralImage.account}/${kogitoDataIndexEphemeralImageEnv.env.kogitoDataIndexEphemeralImage.name}:${kogitoDataIndexEphemeralImageEnv.env.kogitoDataIndexEphemeralImage.buildTag}`,
      description: "Kogito Data Index Ephemeral image",
    },
    SONATAFLOW_OPERATOR__kogitoDataIndexPostgresqlImage: {
      default: `${kogitoDataIndexPostgresqlImageEnv.env.kogitoDataIndexPostgresqlImage.registry}/${kogitoDataIndexPostgresqlImageEnv.env.kogitoDataIndexPostgresqlImage.account}/${kogitoDataIndexPostgresqlImageEnv.env.kogitoDataIndexPostgresqlImage.name}:${kogitoDataIndexPostgresqlImageEnv.env.kogitoDataIndexPostgresqlImage.buildTag}`,
      description: "Kogito Data Index PostgreSQL image",
    },
    SONATAFLOW_OPERATOR__kogitoDBMigratorToolImage: {
      default: `${kogitoDBMigratorToolImageEnv.env.kogitoDbMigratorToolImage.registry}/${kogitoDBMigratorToolImageEnv.env.kogitoDbMigratorToolImage.account}/${kogitoDBMigratorToolImageEnv.env.kogitoDbMigratorToolImage.name}:${kogitoDBMigratorToolImageEnv.env.kogitoDbMigratorToolImage.buildTag}`,
      description: "Kogito DB Migrator image",
    },
    SONATAFLOW_OPERATOR_pinImageSHABundleTool: {
      default: "",
      description:
        "Which tool to pin related images when generating the operator bundle. If empty, no SHA is generated from the images. Possible values are docker|podman|skopeo.",
    },
  }),
  get env() {
    return {
      sonataFlowOperator: {
        registry: getOrDefault(this.vars.SONATAFLOW_OPERATOR__registry),
        account: getOrDefault(this.vars.SONATAFLOW_OPERATOR__account),
        name: getOrDefault(this.vars.SONATAFLOW_OPERATOR__name),
        buildTag: getOrDefault(this.vars.SONATAFLOW_OPERATOR__buildTag),
        platformTag: getOrDefault(this.vars.SONATAFLOW_OPERATOR__platformTag),
        version: require("../package.json").version,
        sonataflowBuilderImage: getOrDefault(this.vars.SONATAFLOW_OPERATOR__sonataflowBuilderImage),
        sonataflowDevModeImage: getOrDefault(this.vars.SONATAFLOW_OPERATOR__sonataflowDevModeImage),
        kogitoJobsServiceEphemeralImage: getOrDefault(this.vars.SONATAFLOW_OPERATOR__kogitoJobsServiceEphemeralImage),
        kogitoJobsServicePostgresqlImage: getOrDefault(this.vars.SONATAFLOW_OPERATOR__kogitoJobsServicePostgresqlImage),
        kogitoDataIndexEphemeralImage: getOrDefault(this.vars.SONATAFLOW_OPERATOR__kogitoDataIndexEphemeralImage),
        kogitoDataIndexPostgresqlImage: getOrDefault(this.vars.SONATAFLOW_OPERATOR__kogitoDataIndexPostgresqlImage),
        kogitoDBMigratorToolImage: getOrDefault(this.vars.SONATAFLOW_OPERATOR__kogitoDBMigratorToolImage),
        pinImageSHABundleTool: getOrDefault(this.vars.SONATAFLOW_OPERATOR_pinImageSHABundleTool),
      },
    };
  },
});
