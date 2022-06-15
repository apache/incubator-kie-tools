/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as React from "react";
import { useCallback } from "react";
import { Text, TextContent } from "@patternfly/react-core/dist/js/components/Text";
import {
  EmptyState,
  EmptyStateBody,
  EmptyStateSecondaryActions,
} from "@patternfly/react-core/dist/js/components/EmptyState";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { useHistory } from "react-router";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { useRoutes } from "./Hooks";

export function NoMatchPage() {
  const routes = useRoutes();
  const history = useHistory();

  const returnHome = useCallback(() => {
    history.replace({ pathname: routes.home.path({}) });
  }, [history, routes]);

  return (
    <Bullseye>
      <EmptyState>
        <TextContent>
          <Text component={"h1"}>{"404"}</Text>
        </TextContent>
        <EmptyStateBody>{`The requested page could not be found.`}</EmptyStateBody>
        <EmptyStateSecondaryActions>
          <Button variant={ButtonVariant.link} onClick={returnHome}>
            Return home
          </Button>
        </EmptyStateSecondaryActions>
      </EmptyState>
    </Bullseye>
  );
}
