import { DropdownGroup, DropdownItem } from "@patternfly/react-core";

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
        {dmnRunner.status === DmnRunnerStatus.RUNNING && !dmnRunner.isDrawerOpen && (
          <DropdownItem key={"open-dmn-runner"} component={"button"} onClick={() => dmnRunner.setDrawerOpen(true)}>
            Open DMN Runner panel
          </DropdownItem>
        )}
        {dmnRunner.status === DmnRunnerStatus.RUNNING && dmnRunner.isDrawerOpen && (
          <DropdownItem key={"open-dmn-runner"} component={"button"} onClick={() => dmnRunner.setDrawerOpen(false)}>
            Close DMN Runner panel
          </DropdownItem>
        )}
      </>
    </DropdownGroup>
  );
}
