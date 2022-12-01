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
import { AuthProviderGroup } from "../src/authProviders/AuthProvidersApi";
const buildEnv: any = env; // build-env is not typed

export const defaultEnvJson: EnvJson = {
  KIE_SANDBOX_GIT_CORS_PROXY_URL: buildEnv.onlineEditor.gitCorsProxyUrl,
  KIE_SANDBOX_EXTENDED_SERVICES_URL: buildEnv.onlineEditor.extendedServicesUrl,
  KIE_SANDBOX_AUTH_PROVIDERS: [
    {
      id: "github_dot_com",
      domain: "github.com",
      supportedGitRemoteDomains: ["github.com", "gist.github.com"],
      type: "github",
      name: "GitHub",
      enabled: true,
      iconPath: "",
      group: AuthProviderGroup.GIT,
    },
    {
      id: "gitlab_dot_com",
      domain: "gitlab.com",
      supportedGitRemoteDomains: ["gitlab.com"],
      type: "gitlab",
      name: "GitLab",
      enabled: false,
      iconPath: routes.static.images.gitlabLogo.path({}),
      group: AuthProviderGroup.GIT,
    },
    {
      id: "bitbucket_dot_com",
      domain: "bitbucket.com",
      supportedGitRemoteDomains: ["bitbucket.com"],
      type: "bitbucket",
      name: "Bitbucket",
      enabled: false,
      iconPath: routes.static.images.bitbucketLogo.path({}),
      group: AuthProviderGroup.GIT,
    },
    {
      enabled: true,
      id: "openshift",
      type: "openshift",
      name: "OpenShift",
      domain: undefined,
      iconPath: routes.static.images.openshiftLogo.path({}),
      group: AuthProviderGroup.CLOUD,
    },
    {
      enabled: false,
      id: "kubernetes",
      type: "openshift",
      name: "Kubernetes",
      domain: undefined,
      iconPath: routes.static.images.kubernetesLogo.path({}),
      group: AuthProviderGroup.CLOUD,
    },
  ],
};
