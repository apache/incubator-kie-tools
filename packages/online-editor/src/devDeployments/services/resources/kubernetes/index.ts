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

import { deploymentWithFormWebappYaml } from "./DeploymentWithFormWebappYaml";
import { deploymentYaml } from "./DeploymentYaml";
import { formWebappIngressYaml } from "./FormWebappIngressYaml";
import { formWebappServiceYaml } from "./FormWebappServiceYaml";
import { ingressYaml } from "./IngressYaml";
import { serviceYaml } from "./ServiceYaml";

export const createDeploymentYamls = [
  {
    name: "DMN Dev deployment",
    content: `
${deploymentYaml}
---
${serviceYaml}
---
${ingressYaml}
`,
  },
  {
    name: "DMN Dev deployment with Form Webapp",
    content: `
${deploymentWithFormWebappYaml}
---
${serviceYaml}
---
${formWebappServiceYaml}
---
${ingressYaml}
---
${formWebappIngressYaml}
`,
  },
];
