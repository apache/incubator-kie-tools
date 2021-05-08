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

import { DropdownGroup, DropdownItem } from "@patternfly/react-core/dist/js/components/Dropdown";

import React from "react";
import { DmnRunnerStatus } from "./DmnRunnerStatus";
import { useDmnRunner } from "./DmnRunnerContext";

export function DmnRunnerDropdownGroup() {
  const dmnRunner = useDmnRunner();

  return (
    <DropdownGroup label={"DMN Runner"}>
      <>
        {dmnRunner.status !== DmnRunnerStatus.RUNNING && (
          <DropdownItem key={"setup-dmn-runner"} component={"button"} onClick={() => dmnRunner.setModalOpen(true)}>
            Setup DMN Runner
          </DropdownItem>
        )}
        {dmnRunner.status === DmnRunnerStatus.RUNNING && !dmnRunner.isDrawerExpanded && (
          <DropdownItem key={"open-dmn-runner"} component={"button"} onClick={() => dmnRunner.setDrawerExpanded(true)}>
            Open DMN Runner panel
          </DropdownItem>
        )}
        {dmnRunner.status === DmnRunnerStatus.RUNNING && dmnRunner.isDrawerExpanded && (
          <DropdownItem key={"open-dmn-runner"} component={"button"} onClick={() => dmnRunner.setDrawerExpanded(false)}>
            Close DMN Runner panel
          </DropdownItem>
        )}
      </>
    </DropdownGroup>
  );
}
