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

import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { Card, CardActions, CardHeader, CardHeaderMain } from "@patternfly/react-core/dist/js/components/Card";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { Stack } from "@patternfly/react-core/dist/js/layouts/Stack";
import { IconSize } from "@patternfly/react-icons/dist/js/createIcon";
import * as React from "react";
import { AuthProviderIcon } from "../authProviders/AuthProviderIcon";
import { useAuthProviders } from "../authProviders/AuthProvidersContext";
import { AuthSession, GitAuthSession, useAuthSessions, useAuthSessionsDispatch } from "./AuthSessionsContext";

export function AuthSessionsList(props: {}) {
  const authSessionsDispatch = useAuthSessionsDispatch();
  const authProviders = useAuthProviders();
  const { authSessions } = useAuthSessions();

  return (
    <>
      <Stack hasGutter={true} style={{ height: "auto" }}>
        {[...authSessions.values()].map((authSession) => {
          if (authSession.type === "none") {
            // This is never going to happen, as we don't save the "None" auth session.
            return <></>;
          }

          const authProvider = authProviders.find((a) => a.id === authSession.authProviderId)!;
          return (
            <Card key={authSession.id} isCompact={true}>
              <CardHeader>
                <CardActions>
                  <Button variant={ButtonVariant.link} onClick={() => authSessionsDispatch.remove(authSession)}>
                    Remove
                  </Button>
                </CardActions>
                <CardHeaderMain>
                  <Flex alignItems={{ default: "alignItemsCenter" }}>
                    <AuthProviderIcon authProvider={authProvider} size={IconSize.md} />
                    <FlexItem>
                      <TextContent>
                        <Text component={TextVariants.h3} style={{ display: "inline" }}>
                          {authSession.login}
                          {authSession.email && (
                            <>
                              &nbsp;
                              <Text component={TextVariants.small} style={{ display: "inline" }}>
                                <i>({authSession.email})</i>
                              </Text>
                            </>
                          )}
                        </Text>
                      </TextContent>
                      <TextContent>
                        <Text component={TextVariants.small}>
                          {authProvider.name}
                          &nbsp;
                        </Text>
                      </TextContent>
                    </FlexItem>
                  </Flex>
                </CardHeaderMain>
              </CardHeader>
            </Card>
          );
        })}
      </Stack>
    </>
  );
}
