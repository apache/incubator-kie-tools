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

import React, { useEffect, useRef } from "react";
import { useAuthSessions, useAuthSessionsDispatch } from "../AuthSessionsContext";
import { useHistory } from "react-router";
import { AuthSessionsService } from "../AuthSessionsService";
import { ManagementConsolePageLayout } from "../../managementConsole/ManagementConsolePageLayout";
import { useRoutes } from "../../navigation/Hooks";
import { AuthSession } from "../AuthSessionApi";
import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";

type Props = {
  onAddAuthSession?: (authSession: AuthSession) => void;
};

export const NewAuthSessionLoginSuccessPage: React.FC<Props> = ({ onAddAuthSession }) => {
  const { add } = useAuthSessionsDispatch();
  const { isAuthSessionsReady } = useAuthSessions();
  const history = useHistory();
  const routes = useRoutes();

  // Since Code Grants can only be used once we want to make sure that the
  // addAuthSession function in the useEffect is only called once.
  const isGettingTokens = useRef(false);

  useEffect(() => {
    if (!isAuthSessionsReady || isGettingTokens.current) {
      return;
    }
    const addAuthSession = async () => {
      isGettingTokens.current = true;
      const authSession = await AuthSessionsService.buildAuthSession(AuthSessionsService.getTemporaryAuthSessionData());
      await add(authSession);
      AuthSessionsService.cleanTemporaryAuthSessionData();
      if (onAddAuthSession) {
        onAddAuthSession(authSession);
      } else {
        history.push(routes.home.path({}));
      }
    };

    addAuthSession();
  }, [add, history, isAuthSessionsReady, onAddAuthSession, routes.home]);

  return (
    <ManagementConsolePageLayout>
      <PageSection>
        <Bullseye>
          <h2>Login success!</h2>
          <h3>Redirecting...</h3>
        </Bullseye>
      </PageSection>
    </ManagementConsolePageLayout>
  );
};
