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

import { useMemo } from "react";
import { CorsProxyHeaderKeys } from "@kie-tools/cors-proxy-api";
import { GitlabClient, GitlabClientApi } from "./GitlabClient";
import { useAuthProviders } from "../authProviders/AuthProvidersContext";
import { useEnv } from "../env/hooks/EnvContext";
import { AuthSession } from "../authSessions/AuthSessionApi";

export function getGitlabClient(args: {
  appName: string;
  token?: string;
  domain?: string;
  proxyUrl?: string;
  insecurelyDisableTlsCertificateValidation?: boolean;
  disableEncoding?: boolean;
}) {
  return new GitlabClient({
    appName: args.appName,
    token: args.token,
    domain: args.domain,
    proxyUrl: args.insecurelyDisableTlsCertificateValidation ? args.proxyUrl : undefined,
    headers: {
      ...(args.insecurelyDisableTlsCertificateValidation
        ? {
            [CorsProxyHeaderKeys.INSECURELY_DISABLE_TLS_CERTIFICATE_VALIDATION]: Boolean(
              args.insecurelyDisableTlsCertificateValidation
            ).toString(),
          }
        : {}),
      // If disableEncoding is true, force proxy/server to skip compression
      ...(args.disableEncoding
        ? {
            [CorsProxyHeaderKeys.DISABLE_ENCODING]: Boolean(args.disableEncoding).toString(),
          }
        : {}),
    },
  });
}

export function useGitlabClient(authSession: AuthSession | undefined): GitlabClientApi {
  const authProviders = useAuthProviders();
  const { env } = useEnv();

  return useMemo(() => {
    if (authSession?.type !== "git") {
      return getGitlabClient({ appName: env.KIE_SANDBOX_APP_NAME });
    }

    const authProvider = authProviders.find((a) => a.id === authSession.authProviderId);
    if (authProvider?.type !== "gitlab") {
      return getGitlabClient({ appName: env.KIE_SANDBOX_APP_NAME });
    }

    return getGitlabClient({
      appName: env.KIE_SANDBOX_APP_NAME,
      token: authSession?.token,
      domain: authProvider.domain,
      proxyUrl: env.KIE_SANDBOX_CORS_PROXY_URL,
      insecurelyDisableTlsCertificateValidation: authProvider.insecurelyDisableTlsCertificateValidation,
      disableEncoding: authProvider.disableEncoding,
    });
  }, [authProviders, authSession, env.KIE_SANDBOX_APP_NAME, env.KIE_SANDBOX_CORS_PROXY_URL]);
}
