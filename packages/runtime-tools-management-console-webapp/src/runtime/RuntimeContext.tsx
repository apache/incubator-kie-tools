/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
import React, { createContext, ReactElement, useCallback, useContext, useEffect, useMemo, useState } from "react";
import { useQueryParam, useQueryParams } from "../navigation/queryParams/QueryParamsContext";
import { buildRouteUrl, QueryParams } from "../navigation/Routes";
import { useAuthSessions, useAuthSessionsDispatch } from "../authSessions/AuthSessionsContext";
import {
  AuthSession,
  AuthSessionStatus,
  getAuthSessionDisplayInfo,
  isOpenIdConnectAuthSession,
  isUnauthenticatedAuthSession,
  OpenIDConnectAuthSession,
} from "../authSessions/AuthSessionApi";
import { useHistory } from "react-router";
import { useRoutes } from "../navigation/Hooks";
import ApolloClient from "apollo-client";
import { InMemoryCache, NormalizedCacheObject } from "apollo-cache-inmemory";
import { HttpLink } from "apollo-link-http";
import { from } from "apollo-link";
import { onError, ErrorResponse } from "apollo-link-error";
import {
  KogitoAppContextProvider,
  UserContext,
} from "@kie-tools/runtime-tools-components/dist/contexts/KogitoAppContext";
import { ApolloProvider } from "react-apollo";
import { AuthSessionsService } from "../authSessions";
import { ProcessListContextProvider } from "@kie-tools/runtime-tools-process-webapp-components/dist/ProcessList";
import { JobsManagementContextProvider } from "@kie-tools/runtime-tools-process-webapp-components/dist/JobsManagement";
import { ProcessDetailsContextProvider } from "@kie-tools/runtime-tools-process-webapp-components/dist/ProcessDetails";
import { TaskInboxContextProvider } from "@kie-tools/runtime-tools-process-webapp-components/dist/TaskInbox";
import { TaskFormContextProvider } from "@kie-tools/runtime-tools-process-webapp-components/dist/TaskForms";

export type RuntimePathSearchParams = Partial<Record<QueryParams, string>>;
export enum RuntimePathSearchParamsRoutes {
  PROCESSES = "processes",
  JOBS = "jobs",
  TASKS = "tasks",
  TASK_DETAILS = "taskDetails",
}

export type RuntimeContextType = {
  apolloClient?: ApolloClient<NormalizedCacheObject>;
  userContext?: UserContext;
  runtimeUrl?: string;
  username?: string;
  isRefreshingToken: boolean;
  runtimePathSearchParams: Map<string, RuntimePathSearchParams>;
  impersonationUsername?: string;
  impersonationGroups?: string;
};

export const RuntimeContext = createContext({} as RuntimeContextType);

export type RuntimeDispatchContextType = {
  refreshToken: (authSession: OpenIDConnectAuthSession) => Promise<OpenIDConnectAuthSession | undefined>;
  setRuntimePathSearchParams: React.Dispatch<
    React.SetStateAction<Map<RuntimePathSearchParamsRoutes, RuntimePathSearchParams>>
  >;
  setImpersonationUsername: React.Dispatch<React.SetStateAction<string | undefined>>;
  setImpersonationGroups: React.Dispatch<React.SetStateAction<string | undefined>>;
};

export const RuntimeDispatchContext = createContext({} as RuntimeDispatchContextType);

export function useRuntime() {
  return useContext(RuntimeContext);
}

export function useRuntimeDispatch() {
  return useContext(RuntimeDispatchContext);
}

export interface RuntimeContextProviderProps {
  children: ReactElement;
  runtimeUrl?: string;
  fullPath?: string;
}

export const RuntimeContextProvider: React.FC<RuntimeContextProviderProps> = (props) => {
  const { authSessions } = useAuthSessions();
  const history = useHistory();
  const routes = useRoutes();
  const user = useQueryParam(QueryParams.USER);
  const impersonationUsernameQueryParam = useQueryParam(QueryParams.IMPERSONATION_USER);
  const impersonationGroupsQueryParam = useQueryParam(QueryParams.IMPERSONATION_GROUPS);
  const queryParams = useQueryParams();
  const [runtimeUrl, setRuntimeUrl] = useState<string>();
  const [username, setUsername] = useState<string>();
  const [impersonationUsername, setImpersonationUsername] = useState<string | undefined>(
    impersonationUsernameQueryParam
  );
  const [impersonationGroups, setImpersonationGroups] = useState<string | undefined>(impersonationGroupsQueryParam);
  const [accessToken, setAccessToken] = useState<string>();
  const [apolloClient, setApolloClient] = useState<ApolloClient<NormalizedCacheObject>>();
  const [userContext, setUserContext] = useState<UserContext>();
  const [isRefreshingToken, setIsRefreshingToken] = useState(false);
  const [runtimePathSearchParams, setRuntimePathSearchParams] = useState<
    Map<RuntimePathSearchParamsRoutes, RuntimePathSearchParams>
  >(new Map<RuntimePathSearchParamsRoutes, RuntimePathSearchParams>([]));
  const { add: updateAuthSession, setCurrentAuthSession } = useAuthSessionsDispatch();
  const { currentAuthSession } = useAuthSessions();

  useEffect(() => {
    if (isRefreshingToken) {
      return;
    }

    if (!props.runtimeUrl) {
      // TODO: No runtimeUrl set, show some warning.
      // runtimeUrl not set, going home
      history.push(routes.home.path({}));
      setCurrentAuthSession(undefined);
      return;
    }

    const validRuntimeAuthSessions = [...authSessions]
      .map(([_, session]) => session)
      .filter((session) => {
        return session.runtimeUrl === props.runtimeUrl && session.status === AuthSessionStatus.VALID;
      });

    // Compatible AuthSession not found
    if (!validRuntimeAuthSessions.length) {
      // TODO: Navigate to page to add the runtime as an AuthSession
      // Going home for now.
      history.push(routes.home.path({}));
      setCurrentAuthSession(undefined);
      return;
    }

    // User not specified via queryParam.
    if (!user) {
      // Compatible AuthSession found but it's authenticated. Redirect to the same route with the first matched AuthSession's user.
      if (isOpenIdConnectAuthSession(validRuntimeAuthSessions[0])) {
        const updatedQueryParams = queryParams.with("user", validRuntimeAuthSessions[0].username);
        history.push({ pathname: history.location.pathname, search: updatedQueryParams.toString() });
        return;
      }
      // First matched AuthSession is not authenticated, use it.
      setCurrentAuthSession(validRuntimeAuthSessions[0]);
      return;
    }

    // User is specified, look for it in the matched AuthSessions.
    const currentUserAuthSession = validRuntimeAuthSessions.find(
      (session) => isOpenIdConnectAuthSession(session) && session.username === user
    );

    // Perfect match! Stay on the page.
    if (currentUserAuthSession) {
      setCurrentAuthSession(currentUserAuthSession);
      return;
    } else {
      // Look for authenticated AuthSession for the same runtimeUrl but with another user
      const possibleAuthenticatedAuthSession = validRuntimeAuthSessions.find((session) =>
        isOpenIdConnectAuthSession(session)
      );
      // Compatible AuthSession found (with different user)
      // Redirecting to it.
      if (possibleAuthenticatedAuthSession) {
        const updatedQueryParams = queryParams.with("user", possibleAuthenticatedAuthSession.username);
        history.push({
          pathname: history.location.pathname,
          search: updatedQueryParams.toString(),
        });
        return;
      }
    }

    // Couldn't find an AuthSession with the same user.
    // Look for unauthenticated AuthSession for the same runtime
    const unauthenticatedAuthSession = validRuntimeAuthSessions.find((session) =>
      isUnauthenticatedAuthSession(session)
    );
    if (unauthenticatedAuthSession) {
      // Compatible AuthSession found but it's unauthenticated. Redirect to the same route without specifying user.
      const updatedQueryParams = queryParams.without("user");
      history.push({ pathname: history.location.pathname, search: updatedQueryParams.toString() });
      setCurrentAuthSession(undefined);
      return;
    }

    // Compatible AuthSession not found
    // TODO: Navigate to page to add the runtime as an AuthSession
    // Going home for now.
    history.push(routes.home.path({}));
    setCurrentAuthSession(undefined);
  }, [
    authSessions,
    history,
    props.runtimeUrl,
    user,
    isRefreshingToken,
    routes.home,
    queryParams,
    setCurrentAuthSession,
  ]);

  const refreshToken = useCallback(
    async (authSession: OpenIDConnectAuthSession) => {
      try {
        setIsRefreshingToken(true);
        const reauthResponse = await AuthSessionsService.reauthenticate({
          authSession,
          fromUnauthorizedRequest: true,
        });
        const updatedAuthSession: AuthSession = {
          ...authSession,
          ...reauthResponse,
        };

        await updateAuthSession(updatedAuthSession);

        return updatedAuthSession;
      } catch (e) {
        console.log("Failed to get new tokens: ", e);
        await updateAuthSession({
          ...authSession,
          status: AuthSessionStatus.INVALID,
        });
        history.push(routes.home.path({}));
      } finally {
        setIsRefreshingToken(false);
      }
    },
    [updateAuthSession, history, routes.home]
  );

  const onUnauthorized = useCallback(
    async (networkError: ErrorResponse["networkError"]) => {
      if (isOpenIdConnectAuthSession(currentAuthSession)) {
        await refreshToken(currentAuthSession);
      } else {
        throw new Error(`Got unauthorized response for unauthenticated runtime! ${networkError}`);
      }
    },
    [currentAuthSession, refreshToken]
  );

  const createApolloClient = useCallback(
    (runtimeUrl: string, token?: string) => {
      if (!runtimeUrl) {
        return;
      }
      const httpLink = new HttpLink({
        uri: `${runtimeUrl}/graphql`,
        ...(token
          ? {
              headers: {
                authorization: token ? `Bearer ${token}` : "",
              },
            }
          : {}),
      });

      const apolloNetworkErrorLink = onError(({ graphQLErrors, networkError, response, operation, forward }) => {
        if (networkError && (networkError as any).statusCode === 401) {
          onUnauthorized(networkError);
          // forward(operation);
        }
      });

      const cache = new InMemoryCache();
      const client: ApolloClient<NormalizedCacheObject> = new ApolloClient({
        cache,
        link: from([apolloNetworkErrorLink, httpLink]),
      });

      return client;
    },
    [onUnauthorized]
  );

  const createUserContext = useCallback(
    (username?: string, impersonationUsername?: string, impersonationGroup?: string): UserContext => {
      return {
        getCurrentUser: () => ({
          id: impersonationUsername ?? (impersonationGroup ? "" : username) ?? "",
          groups: impersonationGroup ? [impersonationGroup] : [],
        }),
      };
    },
    []
  );

  useEffect(() => {
    if (isOpenIdConnectAuthSession(currentAuthSession)) {
      setUsername(currentAuthSession.username);
      setAccessToken(currentAuthSession.tokens.access_token);
      setRuntimeUrl(currentAuthSession.runtimeUrl);
      return;
    }

    if (isUnauthenticatedAuthSession(currentAuthSession)) {
      setUsername(undefined);
      setAccessToken(undefined);
      setRuntimeUrl(currentAuthSession.runtimeUrl);
      return;
    }

    setUsername(undefined);
    setAccessToken(undefined);
    setRuntimeUrl(undefined);
  }, [currentAuthSession]);

  useEffect(() => {
    setApolloClient(runtimeUrl ? createApolloClient(runtimeUrl, accessToken) : undefined);
  }, [createApolloClient, runtimeUrl, accessToken]);

  useEffect(() => {
    setUserContext(createUserContext(username, impersonationUsername, impersonationGroups));
  }, [createUserContext, username, impersonationUsername, impersonationGroups]);

  const value = useMemo(
    () => ({
      apolloClient,
      userContext,
      runtimeUrl,
      username,
      isRefreshingToken,
      runtimePathSearchParams,
      impersonationUsername,
      impersonationGroups,
    }),
    [
      apolloClient,
      userContext,
      runtimeUrl,
      username,
      isRefreshingToken,
      runtimePathSearchParams,
      impersonationUsername,
      impersonationGroups,
    ]
  );

  const dispatch = useMemo(
    () => ({
      refreshToken,
      setRuntimePathSearchParams,
      setImpersonationUsername,
      setImpersonationGroups,
    }),
    [refreshToken, setRuntimePathSearchParams]
  );

  const providerOptions = useMemo(
    () => ({
      transformEndpointBaseUrl: (url?: string) => {
        if (!url) {
          return undefined;
        }
        if (!runtimeUrl) {
          return url;
        }
        const urlOrigin = new URL(url).origin;
        const runtimeUrlOrigin = new URL(runtimeUrl).origin;

        // Replacing only the origin keeps the URLSeachParameters intact
        return url.replace(urlOrigin, runtimeUrlOrigin);
      },
    }),
    [runtimeUrl]
  );

  return (
    <RuntimeDispatchContext.Provider value={dispatch}>
      <RuntimeContext.Provider value={value}>
        {apolloClient && userContext && !isRefreshingToken ? (
          <ApolloProvider client={apolloClient}>
            <KogitoAppContextProvider userContext={userContext}>
              <ProcessListContextProvider apolloClient={apolloClient} options={providerOptions}>
                <ProcessDetailsContextProvider apolloClient={apolloClient} options={providerOptions}>
                  <JobsManagementContextProvider apolloClient={apolloClient}>
                    <TaskInboxContextProvider apolloClient={apolloClient}>
                      <TaskFormContextProvider options={providerOptions}>{props.children}</TaskFormContextProvider>
                    </TaskInboxContextProvider>
                  </JobsManagementContextProvider>
                </ProcessDetailsContextProvider>
              </ProcessListContextProvider>
            </KogitoAppContextProvider>
          </ApolloProvider>
        ) : (
          <div>Loading...</div>
        )}
      </RuntimeContext.Provider>
    </RuntimeDispatchContext.Provider>
  );
};

export function useRuntimeApolloClient() {
  const { apolloClient } = useContext(RuntimeContext);
  return useMemo(() => apolloClient, [apolloClient]);
}

export function useRuntimeUser() {
  const { userContext } = useContext(RuntimeContext);
  return useMemo(() => userContext, [userContext]);
}

export function useRuntimeInfo() {
  const { runtimeUrl, username, isRefreshingToken } = useRuntime();
  const { currentAuthSession } = useAuthSessions();

  const runtimeDisplayInfo = useMemo(() => {
    if (!currentAuthSession) {
      return undefined;
    }
    return getAuthSessionDisplayInfo(currentAuthSession);
  }, [currentAuthSession]);

  const accessToken = useMemo(() => {
    if (!currentAuthSession || isUnauthenticatedAuthSession(currentAuthSession)) {
      return undefined;
    }
    return currentAuthSession.tokens.access_token;
  }, [currentAuthSession]);

  const canImpersonate = useMemo(() => currentAuthSession?.impersonator ?? false, [currentAuthSession?.impersonator]);

  return useMemo(
    () => ({ runtimeUrl, username, accessToken, runtimeDisplayInfo, isRefreshingToken, canImpersonate }),
    [runtimeUrl, username, runtimeDisplayInfo, accessToken, isRefreshingToken, canImpersonate]
  );
}

export function useRuntimeSpecificRoutes() {
  const { runtimePathSearchParams, runtimeUrl, username } = useRuntime();
  const routes = useRoutes();

  return useMemo(
    () => ({
      processes: (authSession?: AuthSession) => {
        const pathRuntimeUrl = authSession ? authSession.runtimeUrl : runtimeUrl;
        const pathUsername = authSession && isOpenIdConnectAuthSession(authSession) ? authSession.username : username;
        if (pathRuntimeUrl) {
          return buildRouteUrl(
            routes.runtime.processes,
            { runtimeUrl: pathRuntimeUrl },
            { user: pathUsername },
            runtimePathSearchParams.get(RuntimePathSearchParamsRoutes.PROCESSES)
          );
        }
        // Should never come to this...
        return buildRouteUrl(routes.home);
      },
      processDetails: (processInstanceId: string, authSession?: AuthSession) => {
        const pathRuntimeUrl = authSession ? authSession.runtimeUrl : runtimeUrl;
        const pathUsername = authSession && isOpenIdConnectAuthSession(authSession) ? authSession.username : username;
        if (pathRuntimeUrl) {
          return buildRouteUrl(
            routes.runtime.processDetails,
            { runtimeUrl: pathRuntimeUrl, processInstanceId },
            { user: pathUsername }
          );
        }
        // Should never come to this...
        return buildRouteUrl(routes.home);
      },
      jobs: (authSession?: AuthSession) => {
        const pathRuntimeUrl = authSession ? authSession.runtimeUrl : runtimeUrl;
        const pathUsername = authSession && isOpenIdConnectAuthSession(authSession) ? authSession.username : username;
        if (pathRuntimeUrl) {
          return buildRouteUrl(
            routes.runtime.jobs,
            { runtimeUrl: pathRuntimeUrl },
            { user: pathUsername },
            runtimePathSearchParams.get(RuntimePathSearchParamsRoutes.JOBS)
          );
        }
        // Should never come to this...
        return buildRouteUrl(routes.home);
      },
      tasks: (authSession?: AuthSession) => {
        const pathRuntimeUrl = authSession ? authSession.runtimeUrl : runtimeUrl;
        const pathUsername = authSession && isOpenIdConnectAuthSession(authSession) ? authSession.username : username;
        if (pathRuntimeUrl) {
          return buildRouteUrl(
            routes.runtime.tasks,
            { runtimeUrl: pathRuntimeUrl },
            { user: pathUsername },
            runtimePathSearchParams.get(RuntimePathSearchParamsRoutes.TASKS)
          );
        }
        // Should never come to this...
        return buildRouteUrl(routes.home);
      },
      taskDetails: (taskId: string, authSession?: AuthSession) => {
        const pathRuntimeUrl = authSession ? authSession.runtimeUrl : runtimeUrl;
        const pathUsername = authSession && isOpenIdConnectAuthSession(authSession) ? authSession.username : username;
        if (pathRuntimeUrl) {
          return buildRouteUrl(
            routes.runtime.taskDetails,
            { runtimeUrl: pathRuntimeUrl, taskId },
            { user: pathUsername }
          );
        }
        // Should never come to this...
        return buildRouteUrl(routes.home);
      },
    }),
    [runtimeUrl, username, routes, runtimePathSearchParams]
  );
}
