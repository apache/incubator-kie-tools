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

import React, { useEffect, useRef, useState } from "react";
import { useAuthSessions, useAuthSessionsDispatch } from "../AuthSessionsContext";
import { useNavigate } from "react-router-dom";
import { AuthSessionsService } from "../AuthSessionsService";
import { ManagementConsolePageLayout } from "../../managementConsole/ManagementConsolePageLayout";
import { useRoutes } from "../../navigation/Hooks";
import { AuthSession } from "../AuthSessionApi";
import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import {
  EmptyState,
  EmptyStateBody,
  EmptyStateVariant,
  EmptyStateActions,
  EmptyStateFooter,
} from "@patternfly/react-core/dist/js/components/EmptyState";

type Props = {
  onAddAuthSession?: (authSession: AuthSession) => void;
};

export const NewAuthSessionLoginSuccessPage: React.FC<Props> = ({ onAddAuthSession }) => {
  const { add } = useAuthSessionsDispatch();
  const { isAuthSessionsReady } = useAuthSessions();
  const navigate = useNavigate();
  const routes = useRoutes();
  const [error, setError] = useState(false);

  // Since Code Grants can only be used once we want to make sure that the
  // addAuthSession function in the useEffect is only called once.
  const isGettingTokens = useRef(false);

  useEffect(() => {
    if (!isAuthSessionsReady || isGettingTokens.current) {
      return;
    }
    const addAuthSession = async () => {
      isGettingTokens.current = true;
      try {
        const authSession = await AuthSessionsService.buildAuthSession(
          AuthSessionsService.getTemporaryAuthSessionData()
        );
        await add(authSession);
        AuthSessionsService.cleanTemporaryAuthSessionData();
        if (onAddAuthSession) {
          onAddAuthSession(authSession);
        } else {
          navigate(routes.home.path({}));
        }
      } catch (e) {
        setError(true);
      }
    };

    addAuthSession();
  }, [add, navigate, isAuthSessionsReady, onAddAuthSession, routes.home]);

  return (
    <ManagementConsolePageLayout>
      <PageSection>
        <Bullseye>
          <EmptyState variant={EmptyStateVariant.lg}>
            <br />
            <br />
            <EmptyStateBody>
              {error ? (
                <>
                  <p>Failed to get a token from the Identity Provider.</p>
                  <p>Check your settings and try again!</p>
                </>
              ) : (
                <p>Login success! Redirecting...</p>
              )}
            </EmptyStateBody>
            <EmptyStateFooter>
              {error && (
                <EmptyStateActions>
                  <Button onClick={() => navigate(routes.home.path({}))}>OK</Button>
                </EmptyStateActions>
              )}
            </EmptyStateFooter>
          </EmptyState>
        </Bullseye>
      </PageSection>
    </ManagementConsolePageLayout>
  );
};
