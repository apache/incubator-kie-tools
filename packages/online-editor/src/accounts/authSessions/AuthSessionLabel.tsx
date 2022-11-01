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

import * as React from "react";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { AuthSession } from "./AuthSessionsContext";
import { useAuthProviders } from "../authProviders/AuthProvidersContext";
import { AuthProviderIcon } from "../authProviders/AuthProviderIcon";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { IconSize } from "@patternfly/react-icons/dist/js/createIcon";

export function AuthSessionLabel(props: { authSession: AuthSession }) {
  const authProviders = useAuthProviders();
  const authProvider = authProviders.find(
    (a) => props.authSession.type !== "none" && a.id === props.authSession.authProviderId
  );

  return (
    <>
      <Flex alignItems={{ default: "alignItemsCenter" }}>
        <AuthProviderIcon authProvider={authProvider} size={IconSize.md} />
        <TextContent>
          <Text component={TextVariants.h3}>{props.authSession.login}</Text>
        </TextContent>
        {props.authSession.type === "git" && props.authSession.email && (
          <TextContent>
            <Text component={TextVariants.small}>
              <i>({props.authSession.email})</i>
            </Text>
          </TextContent>
        )}
        {authProvider && (
          <TextContent>
            <Text component={TextVariants.small}>
              {authProvider.name}
              &nbsp;
            </Text>
          </TextContent>
        )}
      </Flex>
    </>
  );
}
