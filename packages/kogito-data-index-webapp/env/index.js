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

module.exports = composeEnv([require("@kie-tools/root-env/env")], {
  vars: varsWithName({
    KOGITO_DATA_INDEX_WEBAPP_title: {
      default: "KOGITO DATA INDEX",
      description: "Application title",
    },
    KOGITO_DATA_INDEX_WEBAPP_logo: {
      default: "favicon.svg",
      description: "Path to logo image",
    },
    KOGITO_DATA_INDEX_WEBAPP_docLinkHref: {
      default: "https://graphql.org/learn/",
      description: "Documentation link URL",
    },
    KOGITO_DATA_INDEX_WEBAPP_docLinkText: {
      default: "GraphQL DOCS",
      description: "Documentation link text",
    },
    KOGITO_DATA_INDEX_WEBAPP_version: {
      default: require("../package.json").version,
      description: "Kogito Data Index Version",
    },
    KOGITO_DATA_INDEX_WEBAPP_graphqlUiHref: {
      default: "/q/graphql-ui/",
      description: "GraphQL UI link URL",
    },
    KOGITO_DATA_INDEX_WEBAPP_graphqlUiText: {
      default: "GraphQL UI",
      description: "GraphQL UI link text",
    },
  }),
  get env() {
    return {
      kogitoDataIndexWebapp: {
        dev: {
          port: 9026,
        },
        title: getOrDefault(this.vars.KOGITO_DATA_INDEX_WEBAPP_title),
        logo: getOrDefault(this.vars.KOGITO_DATA_INDEX_WEBAPP_logo),
        docLink: {
          href: getOrDefault(this.vars.KOGITO_DATA_INDEX_WEBAPP_docLinkHref),
          text: getOrDefault(this.vars.KOGITO_DATA_INDEX_WEBAPP_docLinkText),
        },
        version: getOrDefault(this.vars.KOGITO_DATA_INDEX_WEBAPP_version),
        graphqlUi: {
          href: getOrDefault(this.vars.KOGITO_DATA_INDEX_WEBAPP_graphqlUiHref),
          text: getOrDefault(this.vars.KOGITO_DATA_INDEX_WEBAPP_graphqlUiText),
        },
      },
    };
  },
});
