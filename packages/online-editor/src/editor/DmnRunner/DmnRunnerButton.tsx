/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import { Button, Tooltip } from "@patternfly/react-core";
import { ConnectedIcon, DisconnectedIcon } from "@patternfly/react-icons";
import * as React from "react";
import { useCallback } from "react";
import { DmnRunnerStatus } from "./DmnRunnerContextProvider";
import { useDmnRunner } from "./DmnRunnerContext";

export function DmnRunnerButton() {
  const dmnRunner = useDmnRunner();

  const onDmnRunner = useCallback(() => {
    if (dmnRunner.status === DmnRunnerStatus.RUNNING) {
      dmnRunner.setDrawerOpen(true);
    } else {
      dmnRunner.setModalOpen(true);
    }
  }, [dmnRunner.status, dmnRunner.setDrawerOpen, dmnRunner.setModalOpen]);

  return (
    <Button
      data-testid="run-button"
      variant={"tertiary"}
      onClick={onDmnRunner}
      aria-label={"Run"}
      className={"kogito--editor__toolbar"}
      icon={
        dmnRunner.status === DmnRunnerStatus.RUNNING ? (
          <Tooltip
            key={"connected"}
            content={"The DMN Runner is connected"}
            flipBehavior={["bottom"]}
            children={<ConnectedIcon />}
          />
        ) : (
          <Tooltip
            key={"disconnected"}
            content={"The DMN Runner is not connected"}
            flipBehavior={["bottom"]}
            children={<DisconnectedIcon />}
          />
        )
      }
    >
      DMN Runner
    </Button>
  );
}
