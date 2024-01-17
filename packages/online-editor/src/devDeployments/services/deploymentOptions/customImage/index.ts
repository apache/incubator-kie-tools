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

import { DeploymentOptionArgs } from "../../types";
import { DeploymentOption } from "../types";
import { DeploymentYaml } from "./DeploymentYaml";
import { IngressYaml } from "./IngressYaml";
import { ServiceYaml } from "./ServiceYaml";

export function CustomImageOption(args: DeploymentOptionArgs): DeploymentOption {
  return {
    name: "Custom Image",
    content: `
${DeploymentYaml(args)}
---
${ServiceYaml()}
---
${IngressYaml()}
`,
    parameters: [
      {
        id: "dockerImage",
        name: "Docker Image",
        description: "The URL of the Docker Image to be used for the container",
        defaultValue: args.baseImageUrl,
        type: "text",
        resourcePatches: [
          {
            testFilters: [{ op: "test", path: "/kind", value: "Deployment" }],
            jsonPatches: [
              {
                op: "replace",
                path: "/spec/template/spec/containers/0/image",
                value: "${{ parameters.dockerImage }}",
              },
            ],
          },
        ],
      },
      {
        id: "command",
        name: "Command",
        description: "The command to be executed when the container starts",
        defaultValue:
          "mvn quarkus:dev -Dquarkus.http.non-application-root-path=/${{ devDeployment.uniqueName }}/q -Dquarkus.http.root-path=/${{ devDeployment.uniqueName }}",
        type: "text",
        resourcePatches: [
          {
            testFilters: [{ op: "test", path: "/kind", value: "Deployment" }],
            jsonPatches: [
              {
                op: "add",
                path: "/spec/template/spec/containers/0/args",
                value: ["./dev-deployment-upload-service && ${{ parameters.command }}"],
              },
            ],
          },
        ],
      },
    ],
  };
}
