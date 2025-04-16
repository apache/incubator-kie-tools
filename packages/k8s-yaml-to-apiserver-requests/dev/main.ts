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

import * as jsYaml from "js-yaml";
import { K8sResourceYaml } from "../src/common";
import { callK8sApiServer } from "../src/k8sApiServerCalls";
import { buildK8sApiServerEndpointsByResourceKind } from "../src/k8sApiServerEndpointsByResourceKind";
import * as fs from "fs";

async function main(args: {
  k8sApiServerUrl: string;
  k8sNamespace: string;
  k8sServiceAccountToken: string;
  k8sYamlFilepath: string;
}) {
  if (!args.k8sApiServerUrl || !args.k8sNamespace || !args.k8sServiceAccountToken || !args.k8sYamlFilepath) {
    console.info(`USAGE:

pnpm start <k8sApiServerUrl> <k8sNamespace> <k8sServiceAccountToken> <k8sYamlFilepath>

EXAMPLE:

pnpm start https://api.to.my.openshift.cluster.com:6443 my-project sha256~MGnPXMPsi1YJkCV6kr970gQYI6KtQWztIObm3jQxUJI ../myDeployment.yaml
    `);
    return;
  }
  // PARAMETERS

  console.info(`Kubernetes API Server:\t ${args.k8sApiServerUrl}`);
  console.info(`Namespace:\t\t ${args.k8sNamespace}`);
  console.info(`Token:\t\t\t ${args.k8sServiceAccountToken}`);
  console.info(`YAML Path:\t\t ${args.k8sYamlFilepath}`);
  console.info("");

  // Fetch the YAML file
  const k8sYamlResourcesString = fs.readFileSync(args.k8sYamlFilepath).toString();
  console.info(`Done reading YAML from '${args.k8sYamlFilepath}'.`);
  console.info("");

  // Parse YAML (can be multiple, separated by `---`)
  console.info("Start parsing YAML...");
  const rawK8sResourceYamls: K8sResourceYaml[] = jsYaml.loadAll(k8sYamlResourcesString) as any;
  console.info("Done.");
  console.info("");

  // Build map of API endpoints by Resource kinds.
  console.info("Start mapping API Server endpoints by Resource kinds...");
  const k8sApiServerEndpointsByResourceKind = await buildK8sApiServerEndpointsByResourceKind(
    args.k8sApiServerUrl,
    false,
    args.k8sServiceAccountToken
  );
  console.info("Done.");
  console.info("");

  // Map all API calls before starting to fire them so we prevent partially applying the YAML in the case some of them fails
  console.info("Start mapping API calls based on parsed YAML...");
  await callK8sApiServer({
    k8sApiServerEndpointsByResourceKind,
    k8sResourceYamls: rawK8sResourceYamls,
    k8sApiServerUrl: args.k8sApiServerUrl,
    k8sNamespace: args.k8sNamespace,
    k8sServiceAccountToken: args.k8sServiceAccountToken,
  });
}

main({
  k8sApiServerUrl: process.argv[2],
  k8sNamespace: process.argv[3],
  k8sServiceAccountToken: process.argv[4],
  k8sYamlFilepath: process.argv[5],
});
