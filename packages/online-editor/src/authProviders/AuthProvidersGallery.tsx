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

import { Card, CardBody, CardHeader, CardTitle } from "@patternfly/react-core/dist/js/components/Card";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Gallery } from "@patternfly/react-core/dist/js/layouts/Gallery";
import * as React from "react";
import { useMemo } from "react";
import {
  AccountsDispatchActionKind,
  AccountsSection,
  assertUnreachable,
  useAccounts,
  useAccountsDispatch,
} from "../accounts/AccountsContext";
import { AuthProviderIcon } from "./AuthProviderIcon";
import { AuthProvider, AuthProviderGroup } from "./AuthProvidersApi";
import { useAuthProviders } from "./AuthProvidersContext";

export function AuthProvidersGallery(props: {
  backActionKind: AccountsDispatchActionKind.GO_HOME | AccountsDispatchActionKind.SELECT_AUTH_PROVIDER;
  authProviderGroup: AuthProviderGroup | undefined;
}) {
  const authProviders = useAuthProviders();
  const accountsDispatch = useAccountsDispatch();
  const accounts = useAccounts();

  const authProvidersByGroup = useMemo(
    () =>
      authProviders
        .filter((authProvider) => (props.authProviderGroup ? authProvider.group === props.authProviderGroup : true)) // If no group provided, enable all.
        .reduce(
          (acc, next) => acc.set(next.group, [...(acc.get(next.group) ?? []), next]),
          new Map<AuthProviderGroup, AuthProvider[]>()
        ),
    [authProviders, props.authProviderGroup]
  );

  return (
    <>
      {[...authProvidersByGroup.entries()].map(([group, authProviders]) => {
        return (
          <React.Fragment key={group}>
            <>
              <TextContent>
                <Text component={TextVariants.h2} style={{ display: "inline-block" }}>
                  {group.charAt(0).toUpperCase() + group.slice(1)}
                </Text>
                &nbsp; &nbsp;
                <Text component={TextVariants.small} style={{ display: "inline-block", fontStyle: "italic" }}>
                  <AuthProviderGroupDescription group={group} />
                </Text>
              </TextContent>
              <br />
            </>
            <Gallery hasGutter={true} minWidths={{ default: "150px" }}>
              {authProviders
                .sort((a, b) => (a.name > b.name ? -1 : 1))
                .sort((a) => (a.enabled ? -1 : 1))
                .map((authProvider) => (
                  <Card
                    key={authProvider.id}
                    isSelectable={authProvider.enabled}
                    isRounded={true}
                    style={{
                      opacity: authProvider.enabled ? 1 : 0.5,
                    }}
                    onClick={() => {
                      if (authProvider.enabled && authProvider.type === "github") {
                        accountsDispatch({
                          kind: AccountsDispatchActionKind.SETUP_GITHUB_AUTH,
                          selectedAuthProvider: authProvider,
                          backActionKind: props.backActionKind,
                          onNewAuthSession:
                            accounts.section === AccountsSection.CONNECT_TO_AN_ACCOUNT
                              ? accounts.onNewAuthSession
                              : undefined,
                        });
                      } else if (authProvider.enabled && authProvider.type === "bitbucket") {
                        accountsDispatch({
                          kind: AccountsDispatchActionKind.SETUP_BITBUCKET_AUTH,
                          selectedAuthProvider: authProvider,
                          backActionKind: props.backActionKind,
                          onNewAuthSession:
                            accounts.section === AccountsSection.CONNECT_TO_AN_ACCOUNT
                              ? accounts.onNewAuthSession
                              : undefined,
                        });
                      } else if (authProvider.enabled && authProvider.type === "openshift") {
                        accountsDispatch({
                          kind: AccountsDispatchActionKind.SETUP_OPENSHIFT_AUTH,
                          selectedAuthProvider: authProvider,
                          backActionKind: props.backActionKind,
                          onNewAuthSession:
                            accounts.section === AccountsSection.CONNECT_TO_AN_ACCOUNT
                              ? accounts.onNewAuthSession
                              : undefined,
                        });
                      } else if (authProvider.enabled && authProvider.type === "kubernetes") {
                        accountsDispatch({
                          kind: AccountsDispatchActionKind.SETUP_KUBERNETES_AUTH,
                          selectedAuthProvider: authProvider,
                          backActionKind: props.backActionKind,
                          onNewAuthSession:
                            accounts.section === AccountsSection.CONNECT_TO_AN_ACCOUNT
                              ? accounts.onNewAuthSession
                              : undefined,
                        });
                      }
                    }}
                  >
                    <CardHeader>
                      {
                        <>
                          <CardTitle>{authProvider.name}</CardTitle>
                          <TextContent>
                            {(!authProvider.enabled && (
                              <TextContent>
                                <Text component={TextVariants.small}>
                                  <i>Available soon!</i>
                                </Text>
                              </TextContent>
                            )) || (
                              <Text component={TextVariants.small}>
                                <i>{authProvider.domain ?? <>&nbsp;</>}</i>
                              </Text>
                            )}
                          </TextContent>
                        </>
                      }
                    </CardHeader>
                    <br />
                    <CardBody>
                      <AuthProviderIcon authProvider={authProvider} size="xl" />
                    </CardBody>
                  </Card>
                ))}
            </Gallery>
            <br />
          </React.Fragment>
        );
      })}
    </>
  );
}
function AuthProviderGroupDescription(props: { group: AuthProviderGroup }) {
  const group = props.group;
  switch (group) {
    case AuthProviderGroup.CLOUD:
      return <>{"Allows Dev Deployments to be created in your Cloud infrastructure."}</>;
    case AuthProviderGroup.GIT:
      return <>{"Allows integration with private repositories and provider-specific features, like GitHub Gists."}</>;
    default:
      assertUnreachable(group);
  }
}
