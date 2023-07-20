/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import { EnvJson } from "../src/env/EnvJson";
import { routes } from "../src/navigation/Routes";

// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import { env } from "../env";
import { AuthProviderGroup, AuthProviderType } from "../src/authProviders/AuthProvidersApi";
import { FileTypes } from "@kie-tools-core/workspaces-git-fs/dist/constants/ExtensionHelper";
import { GLOB_PATTERN } from "../src/envelopeLocator/EditorEnvelopeLocatorFactory";
const buildEnv: any = env; // build-env is not typed

function getDmnDevDeploymentBaseImageUrl() {
  const baseImageRegistry = buildEnv.devDeployments.dmn.baseImage.registry;
  const baseImageAccount = buildEnv.devDeployments.dmn.baseImage.account;
  const baseImageName = buildEnv.devDeployments.dmn.baseImage.name;
  const baseImageTag = buildEnv.devDeployments.dmn.baseImage.tag;

  return baseImageRegistry && baseImageAccount
    ? `${baseImageRegistry}/${baseImageAccount}/${baseImageName}:${baseImageTag}`
    : `${baseImageName}:${baseImageTag}`;
}

export const defaultEnvJson: EnvJson = {
  KIE_SANDBOX_VERSION: buildEnv.root.version,
  KIE_SANDBOX_GIT_CORS_PROXY_URL: buildEnv.onlineEditor.gitCorsProxyUrl,
  KIE_SANDBOX_EXTENDED_SERVICES_URL: buildEnv.onlineEditor.extendedServicesUrl,
  KIE_SANDBOX_DMN_DEV_DEPLOYMENT_BASE_IMAGE_URL: getDmnDevDeploymentBaseImageUrl(),
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
        resourcesPathPrefix: "gwt-editors/dmn",
        path: "dmn-envelope.html",
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
