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
import {
  Select,
  SelectGroup,
  SelectOption,
  SelectPosition,
  SelectProps,
  SelectVariant,
} from "@patternfly/react-core/dist/js/components/Select";
import { AuthProviderIcon } from "../authProviders/AuthProviderIcon";
import { useAuthSession, useAuthSessions } from "./AuthSessionsContext";
import { IconSize } from "@patternfly/react-icons/dist/js/createIcon";
import { AuthProvider, useAuthProviders } from "../authProviders/AuthProvidersContext";
import { useEffect, useMemo, useState } from "react";
import { ValidatedOptions } from "@patternfly/react-core/dist/js/helpers";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { ButtonVariant } from "@patternfly/react-core";
import PlusIcon from "@patternfly/react-icons/dist/js/icons/plus-icon";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { AccountsDispatchActionKind, useAccountsDispatch } from "../AccountsDispatchContext";
import ExclamationCircleIcon from "@patternfly/react-icons/dist/js/icons/exclamation-circle-icon";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { AuthSession, AuthSessionStatus, AUTH_SESSION_NONE } from "./AuthSessionApi";
import { v4 as uuid } from "uuid";

export type AuthSessionSelectItem = {
  groupLabel: string;
  authSession: AuthSession;
  authProvider: AuthProvider | undefined;
};

export type AuthSessionSelectGroup = {
  label: string;
  hidden: boolean;
};

export type AuthSessionSelectFilter = (
  authSessions: Array<{
    authSession: AuthSession;
    authProvider: AuthProvider | undefined;
    status: AuthSessionStatus | undefined;
  }>
) => {
  groups: AuthSessionSelectGroup[];
  items: Array<AuthSessionSelectItem>;
};

export function AuthSessionSelect(props: {
  authSessionId: string | undefined;
  setAuthSessionId: React.Dispatch<React.SetStateAction<string | undefined>>;
  isPlain: boolean;
  title: string;
  position?: SelectPosition;
  filter: AuthSessionSelectFilter;
  menuAppendTo?: SelectProps["menuAppendTo"];
}) {
  const [isAuthSessionSelectorOpen, setAuthSessionSelectorOpen] = useState(false);
  const [showMore, setShowMore] = useState(false);

  const { authSessions, authSessionStatus } = useAuthSessions();
  const authProviders = useAuthProviders();

  const { authSession } = useAuthSession(props.authSessionId);
  const accountsDispatch = useAccountsDispatch();

  const selectedAuthSessionId = useMemo(() => {
    if (props.authSessionId && !authSession) {
      return "Authentication expired"; // authSession doesn't exist anymore
    } else if (!props.authSessionId) {
      return "Select authentication"; // no authSession selected
    } else if (authSession) {
      return props.authSessionId;
    }
  }, [authSession, props.authSessionId]);

  const validated = useMemo(() => {
    if (props.authSessionId && !authSession) {
      return ValidatedOptions.warning; // authSession doesn't exist anymore
    } else if (!props.authSessionId) {
      return ValidatedOptions.warning; // no authSession selected
    } else if (authSession) {
      return ValidatedOptions.default;
    }
  }, [authSession, props.authSessionId]);

  const unfilteredItems = useMemo(() => {
    return [AUTH_SESSION_NONE, ...authSessions.values()].map((authSession) => {
      return {
        authSession,
        authProvider: authProviders.find(({ id }) => authSession.type !== "none" && id === authSession.authProviderId),
        status: authSessionStatus.get(authSession.id),
      };
    });
  }, [authProviders, authSessionStatus, authSessions]);

  const { filteredItemsByGroup, groups } = useMemo(() => {
    const filtered = props.filter?.(unfilteredItems);

    const filteredItemsByGroup = (filtered?.items ?? []).reduce((acc, auth) => {
      return acc.set(auth.groupLabel, [...(acc.get(auth.groupLabel) ?? []), auth]);
    }, new Map<string, AuthSessionSelectItem[]>());

    return { filteredItemsByGroup, groups: filtered?.groups };
  }, [props, unfilteredItems]);

  // Always start the Select with showMore = false.
  useEffect(() => {
    if (!isAuthSessionSelectorOpen) {
      setShowMore(false);
    }
  }, [isAuthSessionSelectorOpen]);

  const showedGroups = groups?.filter((s) => !s.hidden);
  const shouldShowGroups = showMore || (showedGroups?.length ?? 0) > 1;

  return (
    <Select
      position={props.position}
      validated={validated}
      variant={SelectVariant.single}
      selections={selectedAuthSessionId}
      isOpen={isAuthSessionSelectorOpen}
      onToggle={setAuthSessionSelectorOpen}
      isPlain={validated === ValidatedOptions.default ? props.isPlain : false}
      onSelect={(e, value) => {
        e.stopPropagation();
        props.setAuthSessionId(value as string);
        setAuthSessionSelectorOpen(false);
      }}
      className={props.isPlain ? "kie-tools--masthead-hoverable" : ""}
      menuAppendTo={props.menuAppendTo ?? "parent"}
      maxHeight={"400px"}
      style={{ minWidth: "400px" }}
      footer={
        <>
          {showedGroups?.length !== groups?.length && (
            <>
              <Button
                variant={ButtonVariant.link}
                isInline={true}
                onClick={(e) => {
                  e.stopPropagation();
                  setShowMore((prev) => !prev);
                }}
              >
                Show {showMore ? "less" : "more"}
              </Button>
              <Divider style={{ margin: "16px 0" }} />
            </>
          )}
          <Button
            variant={ButtonVariant.link}
            isInline={true}
            icon={<PlusIcon />}
            onClick={() =>
              accountsDispatch({
                kind: AccountsDispatchActionKind.SELECT_AUTH_PROVIDER,
                onNewAuthSession: (newAuthSession) => props.setAuthSessionId(newAuthSession.id),
              })
            }
          >
            Connect to an account...
          </Button>
        </>
      }
    >
      {[
        // Title
        <div key={"title"}>
          <TextContent style={{ fontStyle: "italic", padding: "8px", opacity: "0.8" }}>
            <Text component={TextVariants.small}>{props.title}</Text>
          </TextContent>
        </div>,

        ...[...filteredItemsByGroup.entries()].flatMap(([groupLabel, items], indexGroups) => {
          // The selected item should always be rendered as an option.
          if (
            !items.find((a) => a.authSession.id === props.authSessionId) &&
            groups?.find(({ label }) => label === groupLabel)?.hidden &&
            !showMore
          ) {
            return [];
          }

          return [
            ...[
              ...(shouldShowGroups
                ? [
                    <SelectGroup
                      key={groupLabel}
                      label={groupLabel}
                      style={{ boxShadow: "var(--pf-global--BoxShadow--sm-top)", marginTop: "8px" }}
                    ></SelectGroup>,
                  ]
                : [
                    <div
                      key={uuid()}
                      style={{ boxShadow: "var(--pf-global--BoxShadow--sm-top)", marginTop: "8px", height: "8px" }}
                    />,
                  ]),
            ],

            ...intercalate(
              (id) => <Divider key={`${id}-divider`} inset={{ default: "insetMd" }} />,
              items.flatMap(({ authSession, authProvider }) => {
                // The selected item should always be rendered as an option.
                if (
                  authSession.id !== props.authSessionId &&
                  groups?.find(({ label }) => label === groupLabel)?.hidden &&
                  !showMore
                ) {
                  return [];
                }

                if (authSession.type === "none") {
                  return [
                    <SelectOption key={AUTH_SESSION_NONE.id} value={AUTH_SESSION_NONE.id} description={<i>{}</i>}>
                      <AuthProviderIcon authProvider={undefined} size={IconSize.sm} />
                      &nbsp;&nbsp;
                      {AUTH_SESSION_NONE.login}
                    </SelectOption>,
                  ];
                }

                return [
                  <SelectOption key={authSession.id} value={authSession.id} description={<i>{authProvider?.name}</i>}>
                    <Flex justifyContent={{ default: "justifyContentSpaceBetween" }}>
                      <FlexItem>
                        <AuthProviderIcon authProvider={authProvider} size={IconSize.sm} />
                        &nbsp;&nbsp;
                        {authSession.login}
                      </FlexItem>
                      {authSessionStatus.get(authSession.id) === AuthSessionStatus.INVALID && (
                        <FlexItem style={{ zIndex: 99999 }}>
                          <InvalidAuthSessionIcon />
                        </FlexItem>
                      )}
                    </Flex>
                  </SelectOption>,
                ];
              })
            ),
          ];
        }),
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
        {/* Color copied from PF4 */}
        <ExclamationCircleIcon color={"#c9190b"} />
      </>
    </Tooltip>
  );
}

export function intercalate<T>(divider: (id: string) => T, arr: T[]) {
  const ret: T[] = [];

  let index = 0;
  for (const elem of arr) {
    if (index === 0) {
      ret.push(elem);
    } else {
      ret.push(divider(uuid()));
      ret.push(elem);
    }

    index++;
  }

  return ret;
}
