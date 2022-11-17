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

import { Card, CardBody, CardHeader, CardHeaderMain, CardTitle } from "@patternfly/react-core/dist/js/components/Card";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Gallery } from "@patternfly/react-core/dist/js/layouts/Gallery";
import * as React from "react";
import {
  AccountsDispatchActionKind,
  AccountsSection,
  useAccounts,
  useAccountsDispatch,
} from "../AccountsDispatchContext";
import { AuthProviderIcon } from "./AuthProviderIcon";
import { useAuthProviders } from "./AuthProvidersContext";

export function AuthProvidersGallery(props: {
  backActionKind: AccountsDispatchActionKind.GO_HOME | AccountsDispatchActionKind.SELECT_AUTH_PROVIDER;
}) {
  const authProviders = useAuthProviders();
  const accountsDispatch = useAccountsDispatch();
  const accounts = useAccounts();

  return (
    <>
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
                    kind: AccountsDispatchActionKind.SETUP_GITHUB_TOKEN,
                    selectedAuthProvider: authProvider,
                    backActionKind: props.backActionKind,
                    onNewAuthSession:
                      accounts.section === AccountsSection.CONNECT_TO_NEW_ACC ? accounts.onNewAuthSession : undefined,
                  });
                }
              }}
            >
              <CardHeader>
                <CardHeaderMain>
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
                </CardHeaderMain>
              </CardHeader>
              <br />
              <CardBody>
                <AuthProviderIcon authProvider={authProvider} size={"xl"} />
              </CardBody>
            </Card>
          ))}
      </Gallery>
    </>
  );
}
