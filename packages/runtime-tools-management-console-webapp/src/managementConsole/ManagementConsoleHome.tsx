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

import React, { useCallback, useEffect, useLayoutEffect } from "react";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { useAuthSessions, useAuthSessionsDispatch } from "../authSessions/AuthSessionsContext";
import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import {
  EmptyState,
  EmptyStateBody,
  EmptyStateIcon,
  EmptyStateVariant,
  EmptyStateActions,
  EmptyStateHeader,
  EmptyStateFooter,
} from "@patternfly/react-core/dist/js/components/EmptyState";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { CubesIcon } from "@patternfly/react-icons/dist/js/icons/cubes-icon";
import { PlusIcon } from "@patternfly/react-icons/dist/js/icons/plus-icon";
import { useEnv } from "../env/hooks/EnvContext";
import { AuthSession, AuthSessionsList, isOpenIdConnectAuthSession } from "../authSessions";
import { useHistory } from "react-router";
import { useRoutes } from "../navigation/Hooks";
import { Flex } from "@patternfly/react-core/dist/js/layouts/Flex";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { ManagementConsolePageLayout } from "./ManagementConsolePageLayout";

export const ManagementConsoleHome = () => {
  const { env } = useEnv();
  const { setIsNewAuthSessionModalOpen, setOnSelectAuthSession, setCurrentAuthSession } = useAuthSessionsDispatch();
  const { authSessions } = useAuthSessions();
  const history = useHistory();
  const routes = useRoutes();

  useEffect(() => {
    setOnSelectAuthSession(undefined);
    setCurrentAuthSession(undefined);
  }, [setCurrentAuthSession, setOnSelectAuthSession]);

  const onSelectAuthSession = useCallback(
    (authSession: AuthSession) => {
      const user = isOpenIdConnectAuthSession(authSession) ? authSession.username : undefined;
      const encodedRuntimeUrl = encodeURIComponent(authSession.runtimeUrl);
      const path = routes.runtime.processes.path({
        runtimeUrl: encodedRuntimeUrl,
      });
      history.push({
        pathname: path,
        search: user ? `?user=${encodeURIComponent(user)}` : "",
      });
    },
    [history, routes.runtime.processes]
  );

  useEffect(() => {
    document.title = `${env.RUNTIME_TOOLS_MANAGEMENT_CONSOLE_APP_NAME} :: Welcome`;
  }, [env.RUNTIME_TOOLS_MANAGEMENT_CONSOLE_APP_NAME]);

  return (
    <ManagementConsolePageLayout>
      <PageSection>
        {authSessions.size <= 0 ? (
          <EmptyState variant={EmptyStateVariant.lg}>
            <br />
            <br />
            <EmptyStateHeader
              titleText={<>{`Welcome to ${env.RUNTIME_TOOLS_MANAGEMENT_CONSOLE_APP_NAME}`}</>}
              icon={<EmptyStateIcon icon={CubesIcon} />}
              headingLevel="h4"
            />
            <EmptyStateBody>
              Start by connecting to a runtime to manage Process Instances, Tasks, and Jobs.
            </EmptyStateBody>
            <EmptyStateFooter>
              <EmptyStateActions>
                <Button onClick={() => setIsNewAuthSessionModalOpen(true)} icon={<PlusIcon />}>
                  Connect to a runtime...
                </Button>
              </EmptyStateActions>
            </EmptyStateFooter>
          </EmptyState>
        ) : (
          <Flex justifyContent={{ default: "justifyContentCenter" }}>
            <PageSection style={{ maxWidth: "800px" }} variant={"light"}>
              <PageSection>
                <Flex justifyContent={{ default: "justifyContentSpaceBetween" }}>
                  <Title headingLevel="h1" size="4xl">
                    Select connected runtime
                  </Title>
                  <Button
                    variant={ButtonVariant.link}
                    icon={<PlusIcon />}
                    onClick={() => setIsNewAuthSessionModalOpen(true)}
                  >
                    Add
                  </Button>
                </Flex>
                <br />
                <Divider inset={{ default: "insetMd" }} />
                <br />
                <br />
                <AuthSessionsList onSelectAuthSession={onSelectAuthSession} />
              </PageSection>
            </PageSection>
          </Flex>
        )}
      </PageSection>
    </ManagementConsolePageLayout>
  );
};
