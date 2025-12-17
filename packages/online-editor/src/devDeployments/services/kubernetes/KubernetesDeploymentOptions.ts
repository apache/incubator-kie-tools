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

import { DeploymentOptionArgs } from "../types";
import { DeploymentOption, DeploymentOptionOpts } from "../deploymentOptions/types";
import { CustomImageOption } from "../deploymentOptions/customImage";
import { QuarkusBlankAppOption } from "../deploymentOptions/quarkusBlankApp";
import { FormWebappServiceYaml } from "../deploymentOptions/quarkusBlankApp/FormWebappServiceYaml";
import { FormWebappIngressYaml } from "../deploymentOptions/quarkusBlankApp/FormWebappIngressYaml";
import { IngressYaml } from "../deploymentOptions/quarkusBlankApp/IngressYaml";

export function KubernetesDeploymentOptions(args: DeploymentOptionArgs): Array<DeploymentOption> {
  const quarkusBlankAppOpts: DeploymentOptionOpts = {
    parameters: {
      includeDmnFormWebapp: {
        id: "includeDmnFormWebapp",
        name: "Include DMN Form Webapp",
        description: "Whether to deploy the DMN Form Webapp as a sidecar container or not",
        type: "boolean",
        defaultValue: false,
        skipActionsIfFalse: true,
        resourcePatches: [
          {
            testFilters: [{ op: "test", path: "/kind", value: "Deployment" }],
            jsonPatches: [
              {
                op: "add",
                path: "/spec/template/spec/containers/-",
                value: {
                  name: "${{ devDeployment.uniqueName }}-dmn-form-webapp",
                  image: args.dmnFormWebappImageUrl,
                  imagePullPolicy: args.imagePullPolicy,
                  ports: [{ containerPort: 8081, protocol: "TCP" }],
                },
              },
            ],
          },
        ],
        appendYamls: [FormWebappServiceYaml(), FormWebappIngressYaml()],
      },
    },
    resourcePatches: [
      {
        testFilters: [{ op: "test", path: "/kind", value: "Deployment" }],
        jsonPatches: [
          {
            op: "add",
            path: "/spec/template/spec/containers/0/env/-",
            value: { name: "ROOT_PATH", value: "/${{ devDeployment.uniqueName }}" },
          },
          {
            op: "add",
            path: "/spec/template/spec/containers/0/env/-",
            value: { name: "DEV_DEPLOYMENT__UPLOAD_SERVICE_ROOT_PATH", value: "${{ devDeployment.uniqueName }}" },
          },
        ],
      },
    ],
    appendYamls: [IngressYaml()],
  };
  const customImageOptionOpts: DeploymentOptionOpts = {
    parameters: {
      command: {
        id: "command",
        name: "Command",
        description: "The command to be executed when the container starts",
        defaultValue: "./mvnw quarkus:dev -Dquarkus.http.root-path=/${{ devDeployment.uniqueName }}",
        type: "text",
        resourcePatches: [
          {
            testFilters: [{ op: "test", path: "/kind", value: "Deployment" }],
            jsonPatches: [
              {
                op: "add",
                path: "/spec/template/spec/containers/0/args",
                value: ["dev-deployment-upload-service && ${{ parameters.command }}"],
              },
            ],
          },
        ],
      },
    },
    appendYamls: [IngressYaml()],
    resourcePatches: [
      {
        testFilters: [{ op: "test", path: "/kind", value: "Deployment" }],
        jsonPatches: [
          {
            op: "add",
            path: "/spec/template/spec/containers/0/env/-",
            value: { name: "ROOT_PATH", value: "/${{ devDeployment.uniqueName }}" },
          },
          {
            op: "add",
            path: "/spec/template/spec/containers/0/env/-",
            value: { name: "DEV_DEPLOYMENT__UPLOAD_SERVICE_ROOT_PATH", value: "${{ devDeployment.uniqueName }}" },
          },
        ],
      },
    ],
  };
  return [QuarkusBlankAppOption(args, quarkusBlankAppOpts), CustomImageOption(args, customImageOptionOpts)];
}
