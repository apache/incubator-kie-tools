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
import { Card, CardActions, CardHeader, CardHeaderMain } from "@patternfly/react-core/dist/js/components/Card";
import { Stack } from "@patternfly/react-core/dist/js/layouts/Stack";
import { AuthSessionLabel } from "./AuthSessionLabel";
import { useAuthSessions, useAuthSessionsDispatch } from "./AuthSessionsContext";

export function AuthSessionsList(props: {}) {
  const authSessionsDispatch = useAuthSessionsDispatch();
  const { authSessions } = useAuthSessions();

  return (
    <>
      <Stack hasGutter={true} style={{ height: "auto" }}>
        {[...authSessions.values()].map((authSession) => {
          if (authSession.type === "none") {
            // This is never going to happen, as we don't persist the "none" auth session.
            return <></>;
          }

          return (
            <Card key={authSession.id} isCompact={true}>
              <CardHeader>
                <CardActions>
                  <Button variant={ButtonVariant.link} onClick={() => authSessionsDispatch.remove(authSession)}>
                    Remove
                  </Button>
                </CardActions>
                <CardHeaderMain>
                  <AuthSessionLabel authSession={authSession} />
                </CardHeaderMain>
              </CardHeader>
            </Card>
          );
        })}
      </Stack>
    </>
  );
}
