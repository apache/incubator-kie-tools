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

import { QuickStart } from "@patternfly/quickstarts";

export const OpenShiftIntegrationQuickStart: QuickStart = {
  metadata: {
    name: "serverless-logic-web-tools-openshift-integration",
    instructional: true,
  },
  spec: {
    icon: null,
    displayName: "Integrating your Serverless Workflow project with OpenShift using Serverless Logic Web Tools",
    durationMinutes: 15,
    prerequisites: [],
    description: "",
    introduction: `You can integrate your Serverless Workflow project with Red Hat OpenShift. OpenShift is an enterprise-ready Kubernetes container platform, enabling your Serverless Workflow projects to be deployed and tested online.`,

    conclusion: `**Found an issue?**

If you find an issue or any misleading information, please feel free to report it [here](https://github.com/apache/incubator-kie-kogito-docs/issues/new). We really appreciate it!`,

    tasks: [
      {
        title: "Connecting to OpenShift instance using Serverless Logic Web Tools",
        description: `<div class="sect1">
          <h2 id="proc-connecting-openshift-instance-serverless-logic-web-tools">Connecting to OpenShift instance using Serverless Logic Web Tools</h2>
          <div class="sectionbody">
            <div class="paragraph">
              <p>You can connect to your OpenShift instance to deploy your Serverless Workflow projects with Serverless Logic Web Tools.</p>
            </div>
            <div class="ulist">
              <div class="title">
                <em>
                  <strong>Prerequisites</strong>
                </em>
              </div>
              <ul>
                <li>
                  <p>OpenShift instance is active.</p>
                  <div class="paragraph">
                    <p>You can create a free developer sandbox. For more information, see <a href="https://developers.redhat.com/developer-sandbox">OpenShift Developer Sandbox</a>. </p>
                  </div>
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
                  <p>Log in to your OpenShift instance console interface.</p>
                </li>
                <li>
                  <p>In the OpenShift instance console interface, you need your OpenShift project name (also known as namespace), API server, and an access token.</p>
                  <div class="openblock">
                    <div class="content">
                      <div class="ulist">
                        <ul>
                          <li>
                            <p>For the OpenShift project name, go to the <strong>Topology</strong> tab and in the top-left corner you see your project name. </p>
                            <div class="imageblock">
                              <div class="content">
                                <img src="images/quickstarts/serverless-logic-web-tools-openshift-project.png" alt="serverless logic web tools openshift project">
                              </div>
                              <div class="title">Figure 1. OpenShift project name in OpenShift instance console</div>
                            </div>
                          </li>
                          <li>
                            <p>To obtain the API server and access token, click on your username and <strong>Copy login command</strong>. </p>
                            <div class="paragraph">
                              <p>A new page opens containing your new API token along with <code>oc cli</code> login command. From the <code>oc cli</code> command, copy the value of <code>--server=</code>. </p>
                            </div>
                            <div class="imageblock">
                              <div class="content">
                                <img src="images/quickstarts/serverless-logic-web-tools-openshift-info.png" alt="serverless logic web tools openshift info">
                              </div>
                              <div class="title">Figure 2. OpenShift access token and API server</div>
                            </div>
                          </li>
                        </ul>
                      </div>
                    </div>
                  </div>
                </li>
                <li>
                  <p>Go to the Serverless Logic Web Tools web application, click the <strong>Cogwheel</strong> (⚙️) on the top-right corner and go to the <strong>OpenShift</strong> tab. </p>
                </li>
                <li>
                  <p>Click the <strong>Add connection</strong> button and a window will be shown.</p>
                </li>
                <li>
                  <p>Enter your OpenShift project name in the <strong>Namespace (project)</strong> field. </p>
                </li>
                <li>
                  <p>Enter the value copied value of <code>--server</code> flag in the <strong>Host</strong> field. </p>
                </li>
                <li>
                  <p>Enter the value of API token in the <strong>Token</strong> field. </p>
                </li>
                <li>
                  <p>If your OpenShift instance uses Self-Signed certificates, then you must also check the <code>Insecurely disable TLS certificate validation</code> option.</p>
                </li>
                <li>
                  <p>Click <strong>Connect</strong>. </p>
                  <div class="paragraph">
                    <p>If the entered values are correct, then the tab updates and displays <strong>You&rsquo;re connected to OpenShift</strong> message. </p>
                  </div>
                </li>
              </ol>
            </div>
            <div class="paragraph">
              <p>After connecting to OpenShift, you are ready to deploy your Serverless Workflow projects using Serverless Logic Web Tools. For more information about deploying your projects, see <a class="xref page" href="serverless-logic-web-tools-deploy-projects.html" aria-invalid="true">Deploying your Serverless Workflow projects using Serverless Logic Web Tools</a>. </p>
            </div>
          </div>
        </div>`,
      },
    ],
  },
};
