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

import React, { useCallback } from "react";
import { Select, SelectOption, SelectPosition, SelectProps, SelectVariant } from "@patternfly/react-core/deprecated";
import { useAuthSessionsDispatch, useAuthSessions } from "../AuthSessionsContext";
import { useMemo, useState } from "react";
import { ValidatedOptions } from "@patternfly/react-core/dist/js/helpers";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { PlusIcon } from "@patternfly/react-icons/dist/js/icons/plus-icon";
import { ExclamationCircleIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-circle-icon";
import { UserIcon } from "@patternfly/react-icons/dist/js/icons/user-icon";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { AuthSessionStatus, getAuthSessionDisplayInfo } from "../AuthSessionApi";
import { useRoutes } from "../../navigation/Hooks";
import { useNavigate } from "react-router-dom";

export type AuthSessionSelectProps = {
  isPlain: boolean;
  position?: SelectPosition;
  menuAppendTo?: SelectProps["menuAppendTo"];
};

export function AuthSessionSelect({ isPlain, position, menuAppendTo }: AuthSessionSelectProps) {
  const [isAuthSessionSelectorOpen, setAuthSessionSelectorOpen] = useState(false);
  const { authSessions } = useAuthSessions();
  const { setIsNewAuthSessionModalOpen } = useAuthSessionsDispatch();
  const { currentAuthSession, onSelectAuthSession } = useAuthSessions();
  const routes = useRoutes();
  const navigate = useNavigate();

  const validated = useMemo(() => {
    if (!currentAuthSession) {
      return ValidatedOptions.warning; // no authSession selected
    }
    return ValidatedOptions.default;
  }, [currentAuthSession]);

  const unfilteredItems = useMemo(() => {
    return [...authSessions.values()].map((authSession) => {
      return {
        authSession,
        status: authSession.status,
      };
    });
  }, [authSessions]);

  const onSelect = useCallback(
    (e: React.ChangeEvent<Element>, value: string) => {
      e.stopPropagation();
      setAuthSessionSelectorOpen(false);
      if (!value) {
        return;
      }

      const selectedAuthSession = authSessions.get(value);
      if (selectedAuthSession) {
        onSelectAuthSession?.(selectedAuthSession);
      }
    },
    [authSessions, onSelectAuthSession]
  );

  return (
    <Select
      toggleIcon={<UserIcon />}
      position={position}
      validated={validated}
      variant={SelectVariant.single}
      selections={currentAuthSession?.id ?? "Select connected runtime"}
      isOpen={isAuthSessionSelectorOpen}
      onToggle={(_event, val) => setAuthSessionSelectorOpen(val)}
      isPlain={validated === ValidatedOptions.default ? isPlain : false}
      onSelect={onSelect}
      className={`kogito-management-console__auth-session-select ${isPlain ? "kie-tools--masthead-hoverable-dark" : ""}`}
      menuAppendTo={menuAppendTo ?? "parent"}
      maxHeight={"400px"}
      style={{ minWidth: "400px" }}
      footer={
        <Button
          variant={ButtonVariant.link}
          style={{ width: "100%", textAlign: "left", padding: "0" }}
          icon={<PlusIcon />}
          onClick={() => {
            setIsNewAuthSessionModalOpen(true);
          }}
        >
          Connect to a runtime...
        </Button>
      }
    >
      {[
        <Button
          key={"title"}
          style={{ width: "100%", textAlign: "left" }}
          onClick={() => {
            navigate(routes.home.path({}));
          }}
          variant={ButtonVariant.link}
        >
          Manage...
        </Button>,
        <div
          key={"divider"}
          style={{ boxShadow: "var(--pf-v5-global--BoxShadow--sm-top)", marginTop: "8px", height: "8px" }}
        />,
        ...unfilteredItems.map((item) => (
          <SelectOption
            key={item.authSession.id}
            value={item.authSession.id}
            disabled={item.authSession.status === AuthSessionStatus.INVALID}
            className={
              item.authSession.status === AuthSessionStatus.INVALID
                ? "kogito-management-console__auth-session-select-disabled"
                : ""
            }
            description={
              <div>
                <i>{item.authSession.runtimeUrl}</i>
              </div>
            }
          >
            <Flex justifyContent={{ default: "justifyContentSpaceBetween" }}>
              <FlexItem>{getAuthSessionDisplayInfo(item.authSession).userFriendlyName}</FlexItem>
              {item.authSession.status === AuthSessionStatus.INVALID && (
                <FlexItem style={{ zIndex: 99999 }}>
                  <InvalidAuthSessionIcon />
                </FlexItem>
              )}
            </Flex>
          </SelectOption>
        )),
      ]}
    </Select>
  );
}

export function InvalidAuthSessionIcon() {
  return (
    <Tooltip
      position={"bottom"}
      content={"Could not authenticate using this session. Its Token was probably revoked, or expired."}
    >
      <>
        <ExclamationCircleIcon style={{ color: "var(--pf-v5-global--palette--red-100)" }} />
      </>
    </Tooltip>
  );
}
