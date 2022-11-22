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
import { useCallback, useContext, useReducer } from "react";
import {
  AuthProvider,
  AuthProviderGroup,
  GitAuthProvider,
  OpenShiftAuthProvider,
} from "../authProviders/AuthProvidersApi";
import { AuthSession } from "../authSessions/AuthSessionApi";
import { useAuthSessionsDispatch } from "../authSessions/AuthSessionsContext";

// State

export enum AccountsSection {
  CLOSED = "CLOSED",
  HOME = "HOME",
  CONNECT_TO_AN_ACCOUNT = "CONNECT_TO_AN_ACCOUNT",
  CONNECT_TO_GITHUB = "CONNECT_TO_GITHUB",
  CONNECT_TO_OPENSHIFT = "CONNECT_TO_OPENSHIFT",
}

export type AccountsState =
  | {
      section: AccountsSection.CLOSED;
    }
  | {
      section: AccountsSection.HOME;
      selectedAuthProvider?: undefined;
    }
  | {
      section: AccountsSection.CONNECT_TO_AN_ACCOUNT;
      selectedAuthProvider?: undefined;
      backActionKind: AccountsDispatchActionKind.GO_HOME;
      onNewAuthSession?: (newAuthSession: AuthSession) => any;
      authProviderGroup?: AuthProviderGroup;
    }
  | {
      section: AccountsSection.CONNECT_TO_GITHUB;
      selectedAuthProvider: GitAuthProvider;
      backActionKind: AccountsDispatchActionKind.SELECT_AUTH_PROVIDER | AccountsDispatchActionKind.GO_HOME;
      onNewAuthSession?: (newAuthSession: AuthSession) => any;
    }
  | {
      section: AccountsSection.CONNECT_TO_OPENSHIFT;
      selectedAuthProvider: OpenShiftAuthProvider;
      backActionKind: AccountsDispatchActionKind.SELECT_AUTH_PROVIDER | AccountsDispatchActionKind.GO_HOME;
      onNewAuthSession?: (newAuthSession: AuthSession) => any;
    };

// Reducer

export enum AccountsDispatchActionKind {
  CLOSE = "CLOSE",
  GO_HOME = "GO_HOME",
  SELECT_AUTH_PROVIDER = "SELECT_AUTH_PROVIDER",
  SETUP_GITHUB_AUTH = "SETUP_GITHUB_AUTH",
  SETUP_OPENSHIFT_AUTH = "SETUP_OPENSHIFT_AUTH",
}

export type AccountsDispatchAction =
  | {
      kind: AccountsDispatchActionKind.CLOSE;
    }
  | {
      kind: AccountsDispatchActionKind.GO_HOME;
    }
  | {
      kind: AccountsDispatchActionKind.SELECT_AUTH_PROVIDER;
      onNewAuthSession?: (newAuthSession: AuthSession) => any;
      authProviderGroup?: AuthProviderGroup;
    }
  | {
      kind: AccountsDispatchActionKind.SETUP_GITHUB_AUTH;
      selectedAuthProvider: GitAuthProvider;
      backActionKind: AccountsDispatchActionKind.SELECT_AUTH_PROVIDER | AccountsDispatchActionKind.GO_HOME;
      onNewAuthSession?: (newAuthSession: AuthSession) => any;
    }
  | {
      kind: AccountsDispatchActionKind.SETUP_OPENSHIFT_AUTH;
      selectedAuthProvider: OpenShiftAuthProvider;
      backActionKind: AccountsDispatchActionKind.SELECT_AUTH_PROVIDER | AccountsDispatchActionKind.GO_HOME;
      onNewAuthSession?: (newAuthSession: AuthSession) => any;
    };

export const AccountsContext = React.createContext<AccountsState>({} as any);
export const AccountsDispatchContext = React.createContext<React.Dispatch<AccountsDispatchAction>>({} as any);

export function AccountsContextProvider(props: React.PropsWithChildren<{}>) {
  const authSessionsDispatch = useAuthSessionsDispatch();

  const reducer = useCallback(
    (state: AccountsState, action: AccountsDispatchAction): AccountsState => {
      const { kind } = action;
      switch (kind) {
        case AccountsDispatchActionKind.CLOSE:
          return {
            section: AccountsSection.CLOSED,
          };
        case AccountsDispatchActionKind.GO_HOME:
          if (state.section === AccountsSection.CLOSED) {
            // Only recalculate when opening the Modal, not when navigating inside it.
            authSessionsDispatch.recalculateAuthSessionStatus();
          }
          return {
            section: AccountsSection.HOME,
            selectedAuthProvider: undefined,
          };
        case AccountsDispatchActionKind.SELECT_AUTH_PROVIDER:
          return {
            section: AccountsSection.CONNECT_TO_AN_ACCOUNT,
            selectedAuthProvider: undefined,
            onNewAuthSession: action.onNewAuthSession,
            backActionKind: AccountsDispatchActionKind.GO_HOME,
            authProviderGroup: action.authProviderGroup,
          };
        case AccountsDispatchActionKind.SETUP_GITHUB_AUTH:
          return {
            section: AccountsSection.CONNECT_TO_GITHUB,
            selectedAuthProvider: action.selectedAuthProvider,
            onNewAuthSession: action.onNewAuthSession,
            backActionKind: action.backActionKind,
          };
        case AccountsDispatchActionKind.SETUP_OPENSHIFT_AUTH:
          return {
            section: AccountsSection.CONNECT_TO_OPENSHIFT,
            selectedAuthProvider: action.selectedAuthProvider,
            onNewAuthSession: action.onNewAuthSession,
            backActionKind: action.backActionKind,
          };
        default:
          assertUnreachable(kind);
      }
    },
    [authSessionsDispatch]
  );

  const [state, dispatch] = useReducer(reducer, { section: AccountsSection.CLOSED });

  return (
    <AccountsContext.Provider value={state}>
      <AccountsDispatchContext.Provider value={dispatch}>
        <>{props.children}</>
      </AccountsDispatchContext.Provider>
    </AccountsContext.Provider>
  );
}

export function useAccounts() {
  return useContext(AccountsContext);
}

export function useAccountsDispatch() {
  return useContext(AccountsDispatchContext);
}

export function assertUnreachable(_x: never): never {
  throw new Error("Didn't expect to get here");
}
