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

import { CodegenConfig } from "@graphql-codegen/cli";
import { env } from "./env";

const gatewayApiEnv: any = env;

const config: CodegenConfig = {
  overwrite: true,
  schema: gatewayApiEnv.runtimeToolsSWFGatewayApi.graphqlCodegen.dataIndexUrl,
  documents: ["src/graphql/queries.tsx"],
  emitLegacyCommonJSImports: false,
  generates: {
    "./src/graphql/types.tsx": {
      plugins: [
        {
          add: {
            content: ["/* eslint-disable */"],
          },
        },
        {
          add: {
            content: ["", "export namespace GraphQL {"],
          },
        },
        {
          add: {
            placement: "append",
            content: "}",
          },
        },
        "typescript",
        "typescript-operations",
        "typescript-react-apollo",
      ],
      config: {
        withHOC: false,
        withHooks: true,
        withComponent: false,
        apolloReactHooksImportFrom: "@apollo/react-hooks",
        apolloReactCommonImportFrom: "@apollo/react-common",
        gqlImport: "graphql-tag",
      },
    },
    "./src/graphql/graphql.schema.json": {
      plugins: ["introspection"],
    },
  },
};

export default config;
