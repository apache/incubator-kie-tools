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
import { useOnlineI18n } from "../../common/i18n";

export function DmnRunnerDropdownGroup() {
  const dmnRunner = useDmnRunner();
  const { i18n } = useOnlineI18n();

  return (
    <DropdownGroup label={i18n.dmnRunner.dropdown.label}>
      <>
        {dmnRunner.status !== DmnRunnerStatus.RUNNING && (
          <DropdownItem
            id={"dmn-runner-dropdown-setup"}
            key={"setup-dmn-runner"}
            component={"button"}
            onClick={() => {
              dmnRunner.closeDmnTour();
              dmnRunner.setModalOpen(true);
            }}
          >
            {i18n.dmnRunner.dropdown.setup}
          </DropdownItem>
        )}
        {dmnRunner.status === DmnRunnerStatus.RUNNING && !dmnRunner.isDrawerExpanded && (
          <DropdownItem
            id={"dmn-runner-dropdown-open"}
            key={"open-dmn-runner"}
            component={"button"}
            onClick={() => dmnRunner.setDrawerExpanded(true)}
          >
            {i18n.dmnRunner.dropdown.open}
          </DropdownItem>
        )}
        {dmnRunner.status === DmnRunnerStatus.RUNNING && dmnRunner.isDrawerExpanded && (
          <DropdownItem
            id={"dmn-runner-dropdown-close"}
            key={"open-dmn-runner"}
            component={"button"}
            onClick={() => dmnRunner.setDrawerExpanded(false)}
          >
            {i18n.dmnRunner.dropdown.close}
          </DropdownItem>
        )}
      </>
    </DropdownGroup>
  );
}
