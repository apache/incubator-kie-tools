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

export const GitHubTokenQuickStart: QuickStart = {
  metadata: {
    name: "github-token-quick-start",
    instructional: true,
  },
  spec: {
    icon: null,
    displayName: "Integrating your Serverless Workflow project in GitHub using Serverless Logic Web Tools",
    durationMinutes: 10,
    prerequisites: ["You have an account in GitHub."],
    description: "",
    introduction: `The Serverless Logic Web Tools implements a web version of a Git client, enabling you to clone, create, commit, push, and pull repositories. This process synchronizes your workspaces remotely.

This document describes how you can configure the integration and sychronize your projects.`,

    conclusion: `**Found an issue?**
    
If you find an issue or any misleading information, please feel free to report it [here](https://github.com/kiegroup/kogito-docs/issues/new). We really appreciate it!`,

    tasks: [
      {
        title: "Setting your GitHub token in Serverless Logic Web Tools",
        description: `You can generate a token from your GitHub account and add the token to the Serverless Logic Web Tools.

***Procedure***

1.  Click on the **Cog wheel** (⚙️) on the top-right corner of the screen.

2.  Go to the **GitHub** page.

3.  In the **GitHub** tab, click **Create a new token** option.

Ensure that you select the **repo** option.

4.  Optionally, select **gist**, which enables you to import and update gists.

5.  Copy the generated token and paste it into the **Token** field in Serverless Logic Web Tools GitHub **Settings**.

The contents of the tab are updated and displays that you are signed into the GitHub and contains all the required permissions.`,
      },
    ],
  },
};
