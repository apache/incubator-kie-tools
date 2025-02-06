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
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { AuthSession } from "./AuthSessionApi";
import { useAuthProvider } from "../authProviders/AuthProvidersContext";
import { AuthProviderIcon } from "../authProviders/AuthProviderIcon";
import { Flex } from "@patternfly/react-core/dist/js/layouts/Flex";
import { Icon } from "@patternfly/react-core/dist/js/components/Icon";

export function AuthSessionLabel(props: { authSession: AuthSession }) {
  const authProvider = useAuthProvider(props.authSession);

  return (
    <>
      <Flex alignItems={{ default: "alignItemsCenter" }} style={{ display: "inline-flex" }}>
        <Icon iconSize="md">
          <AuthProviderIcon authProvider={authProvider} />
        </Icon>
        <TextContent>
          <Text component={TextVariants.h3}>
            {props.authSession.type === "git"
              ? props.authSession.login
              : props.authSession.type === "openshift" || props.authSession.type === "kubernetes"
                ? props.authSession.namespace
                : props.authSession.login}
          </Text>
        </TextContent>
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
