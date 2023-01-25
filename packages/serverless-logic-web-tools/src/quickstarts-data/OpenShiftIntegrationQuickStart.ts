/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { QuickStart } from "@patternfly/quickstarts";

export const OpenShiftIntegrationQuickStart: QuickStart = {
  metadata: {
    name: "open-shift-integration-quick-start",
    instructional: true,
  },
  spec: {
    icon: null,
    displayName: "Integrating your Serverless Workflow project with OpenShift using Serverless Logic Web Tools",
    durationMinutes: 15,
    prerequisites: ["KIE Sandbox Extended Services tool installed and running", "OpenShift instance is active."],
    description: "",
    introduction: `You can integrate your Serverless Workflow project with Red Hat OpenShift. OpenShift is an enterprise-ready Kubernetes container platform, enabling your Serverless Workflow projects to be deployed and tested online.`,

    conclusion: `**Found an issue?**
    
If you find an issue or any misleading information, please feel free to report it [here](https://github.com/kiegroup/kogito-docs/issues/new). We really appreciate it!`,

    tasks: [
      {
        title: "Connecting to OpenShift instance using Serverless Logic Web Tools",
        description: `After setting the KIE Sandbox Extended Services, you can connect to your OpenShift instance to deploy your Serverless Workflow projects with Serverless Logic Web Tools.

You can create a free developer sandbox. For more information, see [OpenShift Developer Sandbox](https://developers.redhat.com/developer-sandbox).


_**Procedure**_

1.  Log in to your OpenShift instance console interface.
2.  In the OpenShift instance console interface, you need your OpenShift project name (also known as namespace), API server, and an access token.

	*   For the OpenShift project name, go to the **Topology** tab and in the top-left corner you see your project name.

	![serverless logic web tools openshift project](images/quickstarts/serverless-logic-web-tools-openshift-project.png)

	Figure 1. OpenShift project name in OpenShift instance console

	*   To obtain the API server and access token, click on your username and **Copy login command**.  
		A new page opens containing your new API token along with \`oc cli\` login command. From the \`oc cli\` command, copy the value of \`--server=\`.

	![serverless logic web tools openshift info](https://kiegroup.github.io/kogito-docs/serverlessworkflow/latest/_images/tooling/serverless-logic-web-tools/serverless-logic-web-tools-openshift-info.png)

	Figure 2. OpenShift access token and API server

3.  Go to the Serverless Logic Web Tools web application, click on the **Cog wheel** (⚙️) on the top-right corner and go to the **OpenShift** tab.
4.  Enter your OpenShift project name in the **Namespace (project)** field.
5.  Enter the value copied value of \`--server\` flag in the **Host** field.
6.  Enter the value of API token in the **Token** field.
7.  Click **Connect**.

    If the entered values are correct, then the tab updates and displays **You’re connected to OpenShift** message.

After connecting to OpenShift, you are ready to deploy your Serverless Workflow projects using Serverless Logic Web Tools. For more information about deploying your projects, see [Deploying your Serverless Workflow projects using Serverless Logic Web Tools](https://kiegroup.github.io/kogito-docs/serverlessworkflow/latest/tooling/serverless-logic-web-tools/serverless-logic-web-tools-deploy-projects.html).

<table border="1"><tbody><tr><td>If your OpenShift instance uses self-signed certificates, then you might need to enable <code>InsecureSkipVerify</code> on KIE Sandbox Extended Services.</td></tr></tbody></table>`,
      },
    ],
  },
};
