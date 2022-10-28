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
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { Modal, ModalVariant } from "@patternfly/react-core/dist/js/components/Modal";
import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import AngleLeftIcon from "@patternfly/react-icons/dist/js/icons/angle-left-icon";
import UserIcon from "@patternfly/react-icons/dist/js/icons/user-icon";
import { useCallback, useReducer, useState } from "react";
import { ConnectToGitHubSection } from "./ConnectToGitHubSection";
import { AuthProvidersGallery } from "./authProviders/AuthProvidersGallery";
import { AuthProviderIcon } from "./authProviders/AuthProviderIcon";
import { AuthSessionsList } from "./authSessions/AuthSessionsList";
import { GitAuthProvider, useAuthProviders } from "./authProviders/AuthProvidersContext";
import { useAuthSessions, useAuthSessionsDispatch } from "./authSessions/AuthSessionsContext";
import PlusIcon from "@patternfly/react-icons/dist/js/icons/plus-icon";

// State

export enum AccountsModalSection {
  HOME = "HOME",
  CONNECT_TO_NEW_ACC = "NEW_ACC",
  CONNECT_TO_NEW_GITHUB_ACC = "NEW_GITHUB_ACC",
}

export type AccountsModalState =
  | {
      section: AccountsModalSection.HOME;
      selectedAuthProvider?: undefined;
    }
  | {
      section: AccountsModalSection.CONNECT_TO_NEW_ACC;
      selectedAuthProvider?: undefined;
      backActionKind: AccountsModalDispatchActionKind.GO_HOME;
    }
  | {
      section: AccountsModalSection.CONNECT_TO_NEW_GITHUB_ACC;
      selectedAuthProvider: GitAuthProvider;
      backActionKind: AccountsModalDispatchActionKind.SELECT_AUTH_PROVDER | AccountsModalDispatchActionKind.GO_HOME;
    };

// Reducer

export enum AccountsModalDispatchActionKind {
  GO_HOME = "GO_HOME",
  SELECT_AUTH_PROVDER = "SELECT_AUTH_PROVDER",
  SETUP_GITHUB_TOKEN = "SETUP_GITHUB_TOKEN",
}

export type AccountsModalDispatchAction =
  | {
      kind: AccountsModalDispatchActionKind.GO_HOME;
    }
  | {
      kind: AccountsModalDispatchActionKind.SELECT_AUTH_PROVDER;
    }
  | {
      kind: AccountsModalDispatchActionKind.SETUP_GITHUB_TOKEN;
      selectedAuthProvider: GitAuthProvider;
      backActionKind: AccountsModalDispatchActionKind.SELECT_AUTH_PROVDER | AccountsModalDispatchActionKind.GO_HOME;
    };

export function reducer(state: AccountsModalState, action: AccountsModalDispatchAction): AccountsModalState {
  const { kind } = action;
  switch (kind) {
    case AccountsModalDispatchActionKind.GO_HOME:
      return {
        section: AccountsModalSection.HOME,
        selectedAuthProvider: undefined,
      };
    case AccountsModalDispatchActionKind.SELECT_AUTH_PROVDER:
      return {
        section: AccountsModalSection.CONNECT_TO_NEW_ACC,
        selectedAuthProvider: undefined,
        backActionKind: AccountsModalDispatchActionKind.GO_HOME,
      };
    case AccountsModalDispatchActionKind.SETUP_GITHUB_TOKEN:
      return {
        section: AccountsModalSection.CONNECT_TO_NEW_GITHUB_ACC,
        selectedAuthProvider: action.selectedAuthProvider,
        backActionKind: action.backActionKind,
      };
    default:
      assertUnreachable(kind);
  }
}

export function assertUnreachable(_x: never): never {
  throw new Error("Didn't expect to get here");
}

export function AccountsIcon() {
  const [isAccountsModalOpen, setAccountsModalOpen] = useState(false);
  const [state, dispatch] = useReducer(reducer, { section: AccountsModalSection.HOME });

  const { authSessions } = useAuthSessions();

  const goBack = useCallback(() => {
    if (state.section !== AccountsModalSection.HOME) {
      dispatch({ kind: state.backActionKind });
    }
  }, [state]);

  return (
    <>
      <Button
        variant={ButtonVariant.plain}
        onClick={() => {
          dispatch({ kind: AccountsModalDispatchActionKind.GO_HOME });
          setAccountsModalOpen((prev) => !prev);
        }}
        className={"kie-tools--masthead-hoverable-dark"}
      >
        <UserIcon />
      </Button>
      <Modal
        aria-label={"Accounts"}
        variant={ModalVariant.medium}
        isOpen={isAccountsModalOpen}
        onClose={() => setAccountsModalOpen(false)}
        header={
          <div>
            {state.section !== AccountsModalSection.HOME && (
              <>
                <Button
                  key={"back"}
                  onClick={goBack}
                  variant={ButtonVariant.link}
                  style={{ paddingLeft: 0 }}
                  icon={<AngleLeftIcon />}
                >
                  {`Back`}
                </Button>
                <br />
                <br />
              </>
            )}
            {state.section === AccountsModalSection.HOME && (
              <>
                <TextContent>
                  <Text component={TextVariants.h1}>Accounts</Text>
                </TextContent>
              </>
            )}
            {state.section === AccountsModalSection.CONNECT_TO_NEW_ACC && (
              <>
                <TextContent>
                  <Text component={TextVariants.h1}>Connect to a new account</Text>
                </TextContent>
              </>
            )}
            {state.section === AccountsModalSection.CONNECT_TO_NEW_GITHUB_ACC && (
              <>
                <Flex justifyContent={{ default: "justifyContentSpaceBetween" }}>
                  <FlexItem>
                    <Flex
                      justifyContent={{ default: "justifyContentFlexStart" }}
                      spaceItems={{ default: "spaceItemsSm" }}
                    >
                      <TextContent>
                        <Text component={TextVariants.h2}>
                          {`Connect to`}
                          &nbsp;
                          {state.selectedAuthProvider.name}
                        </Text>
                      </TextContent>
                      <TextContent>
                        <Text component={TextVariants.small}>
                          <i>{state.selectedAuthProvider.domain}</i>
                        </Text>
                      </TextContent>
                    </Flex>
                  </FlexItem>
                  <AuthProviderIcon authProvider={state.selectedAuthProvider} size={"sm"} />
                </Flex>
              </>
            )}
            <br />
            <Divider inset={{ default: "insetMd" }} />
          </div>
        }
      >
        <Page>
          <PageSection variant={"light"}>
            <>
              {state.section === AccountsModalSection.HOME && (
                <>
                  {authSessions.size <= 0 && (
                    <>
                      {`Looks like you don't have any accounts connected yet. Select a provider below to connect an account.`}
                      <br />
                      <br />
                      <br />
                      <AuthProvidersGallery
                        dispatch={dispatch}
                        backActionKind={AccountsModalDispatchActionKind.GO_HOME}
                      />
                    </>
                  )}
                  {authSessions.size > 0 && (
                    <>
                      <Flex justifyContent={{ default: "justifyContentSpaceBetween" }}>
                        <FlexItem>&nbsp;</FlexItem>
                        <Button
                          icon={<PlusIcon />}
                          variant={ButtonVariant.link}
                          onClick={() => dispatch({ kind: AccountsModalDispatchActionKind.SELECT_AUTH_PROVDER })}
                        >
                          Add
                        </Button>
                      </Flex>
                      <br />
                      <AuthSessionsList />
                    </>
                  )}
                </>
              )}
              {state.section === AccountsModalSection.CONNECT_TO_NEW_ACC && (
                <AuthProvidersGallery
                  dispatch={dispatch}
                  backActionKind={AccountsModalDispatchActionKind.SELECT_AUTH_PROVDER}
                />
              )}
              {state.section === AccountsModalSection.CONNECT_TO_NEW_GITHUB_ACC && (
                <ConnectToGitHubSection dispatch={dispatch} authProvider={state.selectedAuthProvider} />
              )}
            </>
          </PageSection>
        </Page>
      </Modal>
    </>
  );
}
