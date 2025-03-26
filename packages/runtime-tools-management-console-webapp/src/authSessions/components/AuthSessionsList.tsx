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

import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { Card, CardBody, CardExpandableContent, CardHeader } from "@patternfly/react-core/dist/js/components/Card";
import { Stack } from "@patternfly/react-core/dist/js/layouts/Stack";
import { useAuthSessions, useAuthSessionsDispatch } from "../AuthSessionsContext";
import React, { useCallback, useState } from "react";
import {
  DescriptionList,
  DescriptionListDescription,
  DescriptionListGroup,
  DescriptionListTerm,
} from "@patternfly/react-core/dist/js/components/DescriptionList";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import ExclamationCircleIcon from "@patternfly/react-icons/dist/js/icons/exclamation-circle-icon";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import {
  AuthSession,
  AuthSessionStatus,
  getAuthSessionDisplayInfo,
  isOpenIdConnectAuthSession,
} from "../AuthSessionApi";
import { Flex } from "@patternfly/react-core/dist/js/layouts/Flex";

export function AuthSessionsList({
  onSelectAuthSession,
}: {
  onSelectAuthSession: undefined | ((authSession: AuthSession) => void);
}) {
  const { authSessions } = useAuthSessions();

  return (
    <>
      <Stack hasGutter={true} style={{ height: "auto" }}>
        {[...authSessions.values()].map((authSession) => (
          <AuthSessionCard key={authSession.id} authSession={authSession} onSelectAuthSession={onSelectAuthSession} />
        ))}
      </Stack>
    </>
  );
}

function AuthSessionCard({
  authSession,
  onSelectAuthSession,
}: {
  authSession: AuthSession;
  onSelectAuthSession: undefined | ((authSession: AuthSession) => void);
}) {
  const authSessionsDispatch = useAuthSessionsDispatch();
  const [isExpanded, setExpanded] = useState(false);

  const onSelect = useCallback(() => {
    if (authSession.status === AuthSessionStatus.VALID) {
      onSelectAuthSession?.(authSession);
    }
  }, [authSession, onSelectAuthSession]);

  return (
    <Card
      key={authSession.id}
      isCompact={true}
      isExpanded={isExpanded}
      isSelectable={authSession.status === AuthSessionStatus.VALID && !!onSelectAuthSession}
      onClick={onSelect}
    >
      <CardHeader
        actions={{
          actions: (
            <>
              {authSession.status === AuthSessionStatus.INVALID && (
                <Tooltip
                  content={"Could not authenticate using this session. Its Token was probably revoked, or expired."}
                >
                  <ExclamationCircleIcon style={{ color: "var(--pf-v5-global--palette--red-100)" }} />
                </Tooltip>
              )}
              <Button
                variant={ButtonVariant.link}
                onClick={(e) => {
                  e.stopPropagation();
                  return authSessionsDispatch.remove(authSession);
                }}
              >
                Remove
              </Button>
            </>
          ),
          hasNoOffset: false,
          className: undefined,
        }}
        onExpand={(e) => {
          e.stopPropagation();
          return setExpanded((prev) => !prev);
        }}
      >
        actions=
        {
          <>
            <Flex alignItems={{ default: "alignItemsCenter" }} style={{ display: "inline-flex" }}>
              <TextContent>
                <Text component={TextVariants.h3}>{`${getAuthSessionDisplayInfo(authSession).userFriendlyName}`}</Text>
              </TextContent>
            </Flex>
          </>
        }
      </CardHeader>
      <CardExpandableContent>
        <CardBody>
          <>
            <br />
            <Divider inset={{ default: "insetXl" }} />
            <br />
            <AuthSessionDescriptionList authSession={authSession} />
          </>
        </CardBody>
      </CardExpandableContent>
    </Card>
  );
}

export function AuthSessionDescriptionList(props: { authSession: AuthSession }) {
  return (
    <>
      <DescriptionList isHorizontal={true} isCompact={true} isFluid={true}>
        <DescriptionListGroup>
          <DescriptionListTerm>Name:</DescriptionListTerm>
          <DescriptionListDescription>{props.authSession.name}</DescriptionListDescription>
        </DescriptionListGroup>
        <>
          <DescriptionListGroup>
            <DescriptionListTerm>Runtime URL:</DescriptionListTerm>
            <DescriptionListDescription>{props.authSession.runtimeUrl}</DescriptionListDescription>
          </DescriptionListGroup>
          {isOpenIdConnectAuthSession(props.authSession) && (
            <>
              <DescriptionListGroup>
                <DescriptionListTerm>Token:</DescriptionListTerm>
                <DescriptionListDescription>
                  {obfuscate(props.authSession.tokens.access_token)}
                  &nbsp;
                  <small>{`(...plus ${(props.authSession.tokens.access_token ?? "").length - 16} hidden characters)`}</small>
                </DescriptionListDescription>
              </DescriptionListGroup>
              <DescriptionListGroup>
                <DescriptionListTerm>Refresh Token:</DescriptionListTerm>
                <DescriptionListDescription>
                  {props.authSession.tokens.refresh_token ? (
                    <>
                      {obfuscate(props.authSession.tokens.refresh_token ?? "")}
                      &nbsp;
                      <small>{`(...plus ${(props.authSession.tokens.refresh_token ?? "").length - 16} hidden characters)`}</small>
                    </>
                  ) : (
                    "Not available. Manual reauthentication required once the access_token expires."
                  )}
                </DescriptionListDescription>
              </DescriptionListGroup>
              <DescriptionListGroup>
                <DescriptionListTerm>Issuer:</DescriptionListTerm>
                <DescriptionListDescription>{props.authSession.issuer}</DescriptionListDescription>
              </DescriptionListGroup>
            </>
          )}
          <DescriptionListGroup>
            <DescriptionListTerm>Can impersonate:</DescriptionListTerm>
            <DescriptionListDescription>{props.authSession.impersonator ? "Yes" : "No"}</DescriptionListDescription>
          </DescriptionListGroup>
          <DescriptionListGroup>
            <DescriptionListTerm>Created at:</DescriptionListTerm>
            <DescriptionListDescription>{props.authSession.createdAtDateISO}</DescriptionListDescription>
          </DescriptionListGroup>
        </>
      </DescriptionList>
    </>
  );
}

export function obfuscate(token: string) {
  if (token.length <= 8) {
    return token;
  }

  const stars = new Array(token.length - 8).join("*");
  const pieceToObfuscate = token.substring(4, token.length - 4);
  return token.replace(pieceToObfuscate, stars).replace(/[*]{8,}/g, "********");
}
