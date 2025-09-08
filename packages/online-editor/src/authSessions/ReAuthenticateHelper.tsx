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

import * as React from "react";
import { useCallback } from "react";
import { AccountsDispatchActionKind, useAccountsDispatch } from "../accounts/AccountsContext";
import { AuthProviderType, isKubernetesAuthProvider } from "../authProviders/AuthProvidersApi";
import { AuthSession, isCloudAuthSession, isGitAuthSession } from "./AuthSessionApi";
import { useAuthProviders } from "../authProviders/AuthProvidersContext";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";

export function ReAuthenticateButton({ authSession }: { authSession: AuthSession }) {
  const authProviders = useAuthProviders();
  const accountsDispatch = useAccountsDispatch();

  const handleReAuthenticate = useCallback(() => {
    if (authSession.id === "none") {
      console.warn("Unauthenticated session can't be reauthenticated.");
      return;
    }

    const matchedProvider =
      (isGitAuthSession(authSession) || isCloudAuthSession(authSession)) &&
      authProviders.find((p) => p.id === authSession.authProviderId);

    if (!matchedProvider) {
      return;
    }

    switch (matchedProvider.type) {
      case AuthProviderType.github:
        accountsDispatch({
          kind: AccountsDispatchActionKind.SETUP_GITHUB_AUTH,
          selectedAuthProvider: matchedProvider,
          selectedAuthSession: authSession,
          backActionKind: AccountsDispatchActionKind.GO_HOME,
        });
        break;

      case AuthProviderType.bitbucket:
        accountsDispatch({
          kind: AccountsDispatchActionKind.SETUP_BITBUCKET_AUTH,
          selectedAuthProvider: matchedProvider,
          selectedAuthSession: authSession,
          backActionKind: AccountsDispatchActionKind.GO_HOME,
        });
        break;

      case AuthProviderType.gitlab:
        accountsDispatch({
          kind: AccountsDispatchActionKind.SETUP_GITLAB_AUTH,
          selectedAuthProvider: matchedProvider,
          selectedAuthSession: authSession,
          backActionKind: AccountsDispatchActionKind.GO_HOME,
        });
        break;

      case AuthProviderType.openshift:
        accountsDispatch({
          kind: AccountsDispatchActionKind.SETUP_OPENSHIFT_AUTH,
          selectedAuthProvider: matchedProvider,
          selectedAuthSession: authSession,
          backActionKind: AccountsDispatchActionKind.GO_HOME,
        });
        break;

      case AuthProviderType.kubernetes:
        accountsDispatch({
          kind: AccountsDispatchActionKind.SETUP_KUBERNETES_AUTH,
          selectedAuthProvider: matchedProvider,
          selectedAuthSession: authSession,
          backActionKind: AccountsDispatchActionKind.GO_HOME,
        });
        break;

      default:
        console.warn("Unsupported provider type for selected session");
    }
  }, [authSession, accountsDispatch, authProviders]);

  return (
    <Button variant={ButtonVariant.link} onClick={handleReAuthenticate}>
      Re-authenticate
    </Button>
  );
}
