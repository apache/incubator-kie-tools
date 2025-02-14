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
import * as React from "react";
import * as ReactDOM from "react-dom";
import ApolloClient from "apollo-client";
import "@patternfly/patternfly/patternfly.css";
import "@patternfly/patternfly/patternfly-addons.css";
import "@patternfly/react-core/dist/styles/base.css";
import { HttpLink } from "apollo-link-http";
import { setContext } from "apollo-link-context";
import { onError } from "apollo-link-error";
import { InMemoryCache, NormalizedCacheObject } from "apollo-cache-inmemory";
import ManagementConsole from "./components/console/ManagementConsole/ManagementConsole";
import ManagementConsoleRoutes from "./components/console/ManagementConsoleRoutes/ManagementConsoleRoutes";
import { KeycloakUnavailablePage } from "@kie-tools/runtime-tools-components/src/common/components/KeycloakUnavailablePage";
import { ServerUnavailablePage } from "@kie-tools/runtime-tools-shared-webapp-components/dist/ServerUnavailablePage";
import { UserContext } from "@kie-tools/runtime-tools-components/dist/contexts/KogitoAppContext";
import {
  isAuthEnabled,
  updateKeycloakToken,
  getToken,
  appRenderWithAxiosInterceptorConfig,
} from "@kie-tools/runtime-tools-components/dist/utils/KeycloakClient";
import { initEnv } from "./env/Env";
import { ENV_PREFIX } from "./env/EnvConstants";
import { EnvJson } from "./env/EnvJson";
import { DATA_INDEX_ENDPOINT } from "./AppConstants";

window["DATA_INDEX_ENDPOINT"] = DATA_INDEX_ENDPOINT;

const onLoadFailure = (): void => {
  ReactDOM.render(<KeycloakUnavailablePage />, document.getElementById("root"));
};

const appRender = async (ctx: UserContext) => {
  const httpLink = new HttpLink({
    uri: (window as any)["DATA_INDEX_ENDPOINT"],
  });
  const fallbackUI = onError(({ networkError }: any) => {
    if (networkError && networkError.stack === "TypeError: Failed to fetch") {
      // eslint-disable-next-line react/no-render-return-value
      return ReactDOM.render(
        <ManagementConsole apolloClient={client} userContext={ctx}>
          <ServerUnavailablePage displayName={"Management Console"} reload={() => window.location.reload()} />
        </ManagementConsole>,
        document.getElementById("root")
      );
    }
  });

  const setGQLContext = setContext((_, { headers }) => {
    if (!isAuthEnabled()) {
      return {
        headers,
      };
    }
    return new Promise((resolve, reject) => {
      updateKeycloakToken()
        .then(() => {
          const token = getToken();
          resolve({
            headers: {
              ...headers,
              authorization: token ? `Bearer ${token}` : "",
            },
          });
        })
        .catch(() => {
          reject();
        });
    });
  });

  const cache = new InMemoryCache();
  const client: ApolloClient<NormalizedCacheObject> = new ApolloClient({
    cache,
    link: setGQLContext.concat(fallbackUI.concat(httpLink)),
  });
  ReactDOM.render(
    <ManagementConsole apolloClient={client} userContext={ctx}>
      <ManagementConsoleRoutes />
    </ManagementConsole>,
    document.getElementById("root")
  );
};

initEnv().then((env) => {
  if (env) {
    Object.keys(env).forEach((key) => {
      (window as any)[key.replace(`${ENV_PREFIX}_`, "")] = env[key as keyof EnvJson];
    });
  }
  appRenderWithAxiosInterceptorConfig((ctx: UserContext) => appRender(ctx), onLoadFailure);
});
