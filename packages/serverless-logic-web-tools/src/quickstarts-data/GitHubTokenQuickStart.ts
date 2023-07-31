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
    name: "serverless-logic-web-tools-github-integration",
    instructional: true,
  },
  spec: {
    icon: null,
    displayName: "Integrating your Serverless Workflow project in GitHub using Serverless Logic Web Tools",
    durationMinutes: 10,
    prerequisites: ["You have an account in GitHub."],
    description: "",
    introduction: `The Serverless Logic Web Tools implements a web version of a Git client, enabling you to clone, create, commit, push, and pull repositories. This process synchronizes your workspaces remotely.

This document describes how you can configure the integration and synchronize your projects.`,

    conclusion: `**Found an issue?**

If you find an issue or any misleading information, please feel free to report it [here](https://github.com/kiegroup/kogito-docs/issues/new). We really appreciate it!`,

    tasks: [
      {
        title: "Setting your GitHub token in Serverless Logic Web Tools",
        description: `<div class="sectionbody">
          <div class="paragraph">
            <p>You can generate a token from your GitHub account and add the token to the Serverless Logic Web Tools.</p>
          </div>
          <div class="ulist">
            <div class="title">
              <em>
                <strong>Prerequisites</strong>
              </em>
            </div>
            <ul>
              <li>
                <p>You have an account in GitHub.</p>
              </li>
            </ul>
          </div>
          <div class="olist arabic">
            <div class="title">
              <em>
                <strong>Procedure</strong>
              </em>
            </div>
            <ol class="arabic">
              <li>
                <p>Go to <a href="https://start.kubesmarts.org/">Serverless Logic Web Tools</a> web application, and click the <strong>Cogwheel</strong> (⚙️) on the top-right corner of the screen. </p>
              </li>
              <li>
                <p>Go to the <strong>GitHub</strong> tab. </p>
              </li>
              <li>
                <p>In the <strong>GitHub</strong> tab, click the <strong>Add access token</strong> button and a window will be shown.</p>
              </li>
              <li>
                <p>Click <strong>Create a new token</strong> option.</p>
                <div class="openblock">
                  <div class="content">
                    <div class="paragraph">
                      <p>Ensure that you select the <strong>repo</strong> option. </p>
                    </div>
                  </div>
                </div>
              </li>
              <li>
                <p>Optionally, select <strong>gist</strong>, which enables you to import and update gists. </p>
              </li>
              <li>
                <p>Copy the generated token and paste it into the <strong>Token</strong> field in Serverless Logic Web Tools GitHub <strong>Settings</strong>. </p>
                <div class="paragraph">
                  <p>The contents of the tab are updated and displays that you are signed into the GitHub and contains all the required permissions.</p>
                </div>
              </li>
            </ol>
          </div>
        </div>`,
      },
      {
        title: "Synchronizing your workspaces with GitHub",
        description: `<div class="sectionbody">
          <div class="paragraph">
            <p>After your GitHub token is set, you can synchronize your workspaces with remote repositories.</p>
          </div>
          <div class="ulist">
            <div class="title">
              <em>
                <strong>Prerequisites</strong>
              </em>
            </div>
            <ul>
              <li>
                <p>Your GitHub token is configured in the Serverless Logic Web Tools.</p>
                <div class="paragraph">
                  <p>For more information, see <a href="https://kiegroup.github.io/kogito-docs/serverlessworkflow/latest/tooling/serverless-logic-web-tools/serverless-logic-web-tools-github-integration.html#proc-setting-github-token-serverless-logic-web-tools">Setting your GitHub token in Serverless Logic Web Tools</a>. </p>
                </div>
              </li>
            </ul>
          </div>
          <div class="olist arabic">
            <div class="title">
              <strong>
                <em>Procedure</em>
              </strong>
            </div>
            <ol class="arabic">
              <li>
                <p>In the Serverless Logic Web Tools web application, create or open a workspace.</p>
              </li>
              <li>
                <p>Add or edit the existing files in the workspace.</p>
              </li>
              <li>
                <p>Click <strong>Share &rarr; Github: Create Repository</strong>. </p>
              </li>
              <li>
                <p>Name your repository and set the repository as <strong>Public</strong> or <strong>Private</strong>. </p>
              </li>
              <li>
                <p>(Optional) Select the <strong>Use Quarkus Accelerator</strong> to create a repository with a base Quarkus project and move the workspace files to <code>src/main/resources</code> folder. </p>
                <div class="openblock">
                  <div class="content">
                    <div class="imageblock">
                      <div class="content">
                        <img src="images/quickstarts/serverless-logic-web-tools-github-repo.png" alt="serverless logic web tools github repo">
                      </div>
                      <div class="title">Figure 1. Create a repository for your workspace</div>
                    </div>
                  </div>
                </div>
              </li>
              <li>
                <p>Click <strong>Sync &rarr; Push</strong> to update the remote repository with your local changes. </p>
              </li>
              <li>
                <p>To get new updates from the remote repository, click <strong>Sync &rarr; Pull</strong>. </p>
                <div class="admonitionblock note">
                  <table>
                    <tbody>
                      <tr>
                        <td class="icon">&nbsp;</td>
                        <td class="content">
                          <div class="paragraph">
                            <p>
                              <strong>NOTE</strong>
                              <br>Currently, Serverless Logic Web Tools cannot resolve the merge conflicts. Therefore, ensure that you always pull changes before working on your files.
                            </p>
                          </div>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </li>
            </ol>
          </div>
        </div>`,
      },
      {
        title: "Importing a workspace from GitHub",
        description: `<div class="sectionbody">
          <div class="paragraph">
            <p>You can import a workspace from GitHub in Serverless Logic Web Tools when you need to work from another computer or need to use someone else’s workspace.</p>
          </div>
          <div class="ulist">
            <div class="title">
              <strong>
                <em>Prerequisites</em>
              </strong>
            </div>
            <ul>
              <li>
                <p>Your GitHub token is configured in the Serverless Logic Web Tools.</p>
                <div class="paragraph">
                  <p>For more information, see <a href="https://kiegroup.github.io/kogito-docs/serverlessworkflow/latest/tooling/serverless-logic-web-tools/serverless-logic-web-tools-github-integration.html#proc-setting-github-token-serverless-logic-web-tools">Setting your GitHub token in Serverless Logic Web Tools</a>. </p>
                </div>
              </li>
              <li>
                <p>You have a repository containing workflow files.</p>
              </li>
            </ul>
          </div>
          <div class="olist arabic">
            <div class="title">
              <strong>
                <em>Procedure</em>
              </strong>
            </div>
            <ol class="arabic">
              <li>
                <p>Go to GitHub, find the repository with your project, and copy the repository URL.</p>
              </li>
              <li>
                <p>In Serverless Logic Web Tools web application, paste the repository URL in the <strong>Import → From URL</strong> field and click <strong>Clone</strong>. </p>
                <div class="paragraph">
                  <p>The page loads your imported project, defaulting to a workflow file, if present.</p>
                </div>
              </li>
              <li>
                <p>If applicable, you can push to the imported repository by clicking on the <strong>Sync → Push</strong>. </p>
              </li>
            </ol>
          </div>
        </div>`,
      },
    ],
  },
};
