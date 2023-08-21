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

export const ApplicationServicesIntegrationQuickStart: QuickStart = {
  metadata: {
    name: "serverless-logic-web-tools-redhat-application-services-integration",
    instructional: true,
  },
  spec: {
    icon: null,
    displayName: "Integrating with Red Hat OpenShift Application and Data Services",
    durationMinutes: 30,
    prerequisites: ["You have access to the Red Hat OpenShift Application and Data Services console."],
    description: "",
    introduction: ` Some of the features in Serverless Logic Web Tools require integration with Red Hat OpenShift Application and Data Services. Consider uploading OpenAPI specifications to a service registry and deploying Serverless Workflow as examples of integration with Red Hat OpenShift Application and Data Services.

This document describes how you can configure the required settings to complete the integration with Red Hat OpenShift Application and Data Services.  `,

    conclusion: `**Found an issue?**

If you find an issue or any misleading information, please feel free to report it [here](https://github.com/kiegroup/kogito-docs/issues/new). We really appreciate it!`,

    tasks: [
      {
        title: "Creating a service account in Red Hat OpenShift application and Data Services",
        description: `<div class="sectionbody">
          <div class="paragraph">
            <p>You can create or use a service account from your Red Hat OpenShift Application and Data Services console and add the service account to the Serverless Logic Web Tools.</p>
          </div>
          <div class="ulist">
            <div class="title">
              <strong>
                <em>Prerequisites</em>
              </strong>
            </div>
            <ul>
              <li>
                <p>You have access to the Red Hat OpenShift Application and Data Services console.</p>
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
                <p>To create a service account in Red Hat Openshift Application and Data Services, perform the following steps:</p>
                <div class="openblock">
                  <div class="content">
                    <div class="admonitionblock note">
                      <table>
                        <tbody>
                          <tr>
                            <td class="icon">
                              <i class="fa icon-note" title="Note"></i>
                            </td>
                            <td class="content">
                              <div class="paragraph">
                                <p>
                                  <b>NOTE</b>
                                  <br />You can skip this step if you already have a service account.
                                </p>
                              </div>
                            </td>
                          </tr>
                        </tbody>
                      </table>
                    </div>
                    <div class="olist loweralpha">
                      <ol class="loweralpha" type="a">
                        <li>
                          <p>Go to <a href="https://console.redhat.com/application-services/service-accounts">Service Accounts | Red Hat OpenShift Application and Data Services</a>. </p>
                        </li>
                        <li>
                          <p>Click <strong>Create service account</strong>. </p>
                        </li>
                        <li>
                          <p>In the <strong>Create a service account</strong> window, enter a service account name in the <strong>Short description</strong> field. </p>
                        </li>
                        <li>
                          <p>Click <strong>Create</strong>. </p>
                          <div class="paragraph">
                            <p>A modal displaying your <strong>Client ID</strong> and <strong>Client Secret</strong> appears. </p>
                          </div>
                        </li>
                        <li>
                          <p>Copy and save the Client ID and Client Secret.</p>
                        </li>
                        <li>
                          <p>Check the <strong>I have copied the client ID and secret</strong> checkbox and click <strong>Close</strong>. </p>
                        </li>
                      </ol>
                    </div>
                  </div>
                </div>
              </li>
              <li>
                <p>If you already have a service account, find your <strong>Client ID</strong> and <strong>Client Secret</strong>. </p>
              </li>
              <li>
                <p>In the Serverless Logic Web Tools, click the <strong>Cogwheel</strong> (⚙️) on the top-right corner and go to the <strong>Service Account</strong> tab. </p>
              </li>
              <li>
                <p>Click the <strong>Add service account</strong> button and a window will be shown.</p>
              </li>
              <li>
                <p>Enter your <strong>Client ID</strong> and <strong>Client Secret</strong> in the respective fields. </p>
              </li>
              <li>
                <p>Click <strong>Apply</strong>. </p>
                <div class="paragraph">
                  <p>The content in the <strong>Service Account</strong> tab updates and displays <strong>Your Service Account information is set</strong> message. </p>
                </div>
              </li>
            </ol>
          </div>
        </div>`,
      },
      {
        title: "Creating a Service Registry in Red Hat OpenShift application and Data Services",
        description: `<div class="sectionbody">
          <div class="paragraph">
            <p>You can create or use a Service Registry instance from your Red Hat OpenShift Application and Data Services console and add the Service Registry to Serverless Logic Web Tools.</p>
          </div>
          <div class="ulist">
            <div class="title">
              <em>
                <strong>Prerequisites</strong>
              </em>
            </div>
            <ul>
              <li>
                <p>You have access to the Red Hat OpenShift Application and Data Services console.</p>
              </li>
              <li>
                <p>You have created a service account.</p>
                <div class="paragraph">
                  <p>For information about creating a service account, see <a href="https://kiegroup.github.io/kogito-docs/serverlessworkflow/latest/tooling/serverless-logic-web-tools/serverless-logic-web-tools-redhat-application-services-integration.html#proc-create-service-account-serverless-logic-web-tools">Creating a service account in Red Hat OpenShift application and Data Services</a>. </p>
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
                <p>To create a Service Registry instance in Red Hat Openshift Application and Data Services console, perform the following steps:</p>
                <div class="openblock">
                  <div class="content">
                    <div class="admonitionblock note">
                      <table>
                        <tbody>
                          <tr>
                            <td class="icon">&nbsp;</td>
                            <td class="content">
                              <div class="paragraph">
                                <p>
                                  <strong>NOTE</strong>
                                  <br>You can skip this step if you already have a Service Registry instance.
                                </p>
                              </div>
                            </td>
                          </tr>
                        </tbody>
                      </table>
                    </div>
                    <div class="olist loweralpha">
                      <ol class="loweralpha" type="a">
                        <li>
                          <p>Go to <a href="https://console.redhat.com/application-services/service-registry">Service Registry | Red Hat OpenShift Application and Data Services</a>. </p>
                        </li>
                        <li>
                          <p>Click <strong>Create Service Registry instance</strong> button. </p>
                        </li>
                        <li>
                          <p>In the <strong>Create a Service Registry instance</strong> window, enter a Service Registry instance name and click <strong>Create</strong>. </p>
                          <div class="paragraph">
                            <p>The list of Service Registry instances updates with your instance.</p>
                          </div>
                        </li>
                        <li>
                          <p>Find the Service Registry instance you created in the list and click on the instance.</p>
                        </li>
                        <li>
                          <p>Go to the <strong>Settings</strong> tab and click on <strong>Grant access</strong>. </p>
                        </li>
                        <li>
                          <p>In the drop-down, select the service account you created in the previous procedure.</p>
                        </li>
                        <li>
                          <p>Select a role for your service account.</p>
                          <div class="admonitionblock important">
                            <table>
                              <tbody>
                                <tr>
                                  <td class="icon">&nbsp;</td>
                                  <td class="content">
                                    <div class="paragraph">
                                      <p>
                                        <strong>IMPORTANT <br>
                                        </strong>You must select the role as Manager or Administrator to have the read and write access.
                                      </p>
                                    </div>
                                  </td>
                                </tr>
                              </tbody>
                            </table>
                          </div>
                        </li>
                        <li>
                          <p>Click <strong>Save</strong>. </p>
                        </li>
                        <li>
                          <p>Click on the menu on the top-right corner of the screen.</p>
                        </li>
                        <li>
                          <p>Click <strong>Connection</strong>. </p>
                          <div class="paragraph">
                            <p>A drawer opens containing the required connection and authentication information.</p>
                          </div>
                        </li>
                        <li>
                          <p>Copy the value of <strong>Core Registry API</strong>. </p>
                        </li>
                      </ol>
                    </div>
                  </div>
                </div>
              </li>
              <li>
                <p>If you already have a Service Registry, find the value of <strong>Core Registry API</strong> of your Service Registry. </p>
              </li>
              <li>
                <p>In the Serverless Logic Web Tools web application, click the <strong>Cogwheel</strong> (⚙️) on the top-right corner and go to the <strong>Service Registry</strong> tab. </p>
              </li>
              <li>
                <p>Click the <strong>Add service registry</strong> button and a window will be shown.</p>
              </li>
              <li>
                <p>Enter a name for your registry.</p>
                <div class="paragraph">
                  <p>You can enter the same name that you used while creating the Service Registry instance.</p>
                </div>
              </li>
              <li>
                <p>Enter the value of <strong>Core Registry API</strong> and click <strong>Apply</strong>. </p>
                <div class="paragraph">
                  <p>The content in the <strong>Service Registry</strong> tab updates and displays <strong>Your Service Registry information is set</strong> message. </p>
                </div>
              </li>
            </ol>
          </div>
        </div>`,
      },
    ],
  },
};
