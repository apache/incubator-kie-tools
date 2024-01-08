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

import { EnvJson } from "../src/env/EnvJson";
import { routes } from "../src/navigation/Routes";

// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import { env } from "../env";
import { AuthProviderGroup, AuthProviderType } from "../src/authProviders/AuthProvidersApi";
import { FileTypes } from "@kie-tools-core/workspaces-git-fs/dist/constants/ExtensionHelper";
import { GLOB_PATTERN } from "../src/envelopeLocator/EditorEnvelopeLocatorFactory";
const buildEnv: any = env; // build-env is not typed

function getDevDeploymentBaseImageUrl() {
  const baseImageRegistry = buildEnv.devDeployments.baseImage.registry;
  const baseImageAccount = buildEnv.devDeployments.baseImage.account;
  const baseImageName = buildEnv.devDeployments.baseImage.name;
  const baseImageTag = buildEnv.devDeployments.baseImage.tag;

  return baseImageRegistry && baseImageAccount
    ? `${baseImageRegistry}/${baseImageAccount}/${baseImageName}:${baseImageTag}`
    : `${baseImageName}:${baseImageTag}`;
}

function getDevDeploymentFormWebappImageUrl() {
  const dmnFormWebappImageRegistry = buildEnv.devDeployments.dmnFormWebappImage.registry;
  const dmnFormWebappImageAccount = buildEnv.devDeployments.dmnFormWebappImage.account;
  const dmnFormWebappImageName = buildEnv.devDeployments.dmnFormWebappImage.name;
  const dmnFormWebappImageTag = buildEnv.devDeployments.dmnFormWebappImage.tag;

  return dmnFormWebappImageRegistry && dmnFormWebappImageAccount
    ? `${dmnFormWebappImageRegistry}/${dmnFormWebappImageAccount}/${dmnFormWebappImageName}:${dmnFormWebappImageTag}`
    : `${dmnFormWebappImageName}:${dmnFormWebappImageTag}`;
}

export const defaultEnvJson: EnvJson = {
  KIE_SANDBOX_VERSION: buildEnv.root.version,
  KIE_SANDBOX_CORS_PROXY_URL: buildEnv.onlineEditor.corsProxyUrl,
  KIE_SANDBOX_EXTENDED_SERVICES_URL: buildEnv.onlineEditor.extendedServicesUrl,
  KIE_SANDBOX_DEV_DEPLOYMENT_BASE_IMAGE_URL: getDevDeploymentBaseImageUrl(),
  KIE_SANDBOX_DEV_DEPLOYMENT_IMAGE_PULL_POLICY: buildEnv.devDeployments.imagePullPolicy,
  KIE_SANDBOX_DEV_DEPLOYMENT_DMN_FORM_WEBAPP_IMAGE_URL: getDevDeploymentFormWebappImageUrl(),
  KIE_SANDBOX_REQUIRE_CUSTOM_COMMIT_MESSAGE: buildEnv.onlineEditor.requireCustomCommitMessage,
  KIE_SANDBOX_CUSTOM_COMMIT_MESSAGE_VALIDATION_SERVICE_URL:
    buildEnv.onlineEditor.customCommitMessageValidationServiceUrl,
  KIE_SANDBOX_APP_NAME: buildEnv.onlineEditor.appName,
  KIE_SANDBOX_AUTH_PROVIDERS: [
    {
      id: "github_dot_com",
      domain: "github.com",
      supportedGitRemoteDomains: ["github.com", "gist.github.com"],
      type: AuthProviderType.github,
      name: "GitHub",
      enabled: true,
      iconPath: "",
      group: AuthProviderGroup.GIT,
    },
    {
      id: "gitlab_dot_com",
      domain: "gitlab.com",
      supportedGitRemoteDomains: ["gitlab.com"],
      type: AuthProviderType.gitlab,
      name: "GitLab",
      enabled: false,
      iconPath: routes.static.images.gitlabLogo.path({}),
      group: AuthProviderGroup.GIT,
    },
    {
      id: "bitbucket_dot_org",
      domain: "bitbucket.org",
      supportedGitRemoteDomains: ["bitbucket.org"],
      type: AuthProviderType.bitbucket,
      name: "Bitbucket",
      enabled: true,
      iconPath: routes.static.images.bitbucketLogo.path({}),
      group: AuthProviderGroup.GIT,
    },
    {
      enabled: true,
      id: "openshift",
      type: AuthProviderType.openshift,
      name: "OpenShift",
      domain: undefined,
      iconPath: routes.static.images.openshiftLogo.path({}),
      group: AuthProviderGroup.CLOUD,
    },
    {
      enabled: true,
      id: "kubernetes",
      type: AuthProviderType.kubernetes,
      name: "Kubernetes",
      domain: undefined,
      iconPath: routes.static.images.kubernetesLogo.path({}),
      group: AuthProviderGroup.CLOUD,
    },
  ],
  KIE_SANDBOX_ACCELERATORS: [
    {
      name: "Quarkus",
      iconUrl: `https://github.com/kiegroup/kie-sandbox-quarkus-accelerator/raw/${buildEnv.root.version}/quarkus-logo.png`,
      gitRepositoryUrl: "https://github.com/kiegroup/kie-sandbox-quarkus-accelerator",
      gitRepositoryGitRef: buildEnv.root.version,
      dmnDestinationFolder: "src/main/resources/dmn",
      bpmnDestinationFolder: "src/main/resources/bpmn",
      otherFilesDestinationFolder: "src/main/resources/others",
    },
  ],
  KIE_SANDBOX_EDITORS: [
    {
      extension: FileTypes.BPMN,
      filePathGlob: GLOB_PATTERN.bpmn,
      editor: {
        resourcesPathPrefix: "gwt-editors/bpmn",
        path: "bpmn-envelope.html",
      },
      card: {
        title: "Workflow",
        description: "BPMN files are used to generate business workflows.",
      },
    },
    {
      extension: FileTypes.DMN,
      filePathGlob: GLOB_PATTERN.dmn,
      editor: {
        resourcesPathPrefix: "",
        path: "new-dmn-editor-envelope.html",
      },
      card: {
        title: "Decision",
        description: "DMN files are used to generate decision models",
      },
    },
    {
      extension: FileTypes.PMML,
      filePathGlob: GLOB_PATTERN.pmml,
      editor: {
        resourcesPathPrefix: "",
        path: "pmml-envelope.html",
      },
      card: {
        title: "Scorecard",
        description: "PMML files are used to generate scorecards",
      },
    },
  ],
};
