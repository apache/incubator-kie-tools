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

import * as React from "react";
import { DmnRunnerDrawerPanelContent } from "./DmnRunnerDrawerPanelContent";
import { Drawer, DrawerContent, DrawerContentBody } from "@patternfly/react-core/dist/js/components/Drawer";
import { useDmnRunnerState } from "./DmnRunnerContext";
import { DmnRunnerMode } from "./DmnRunnerStatus";
import { DmnRunnerErrorBoundary } from "./DmnRunnerErrorBoundary";

export function DmnRunnerDrawer(props: React.PropsWithChildren<{}>) {
  const dmnRunnerState = useDmnRunnerState();
  return (
    <Drawer isInline={true} isExpanded={dmnRunnerState.isExpanded && dmnRunnerState.mode === DmnRunnerMode.FORM}>
      <DrawerContent
        className={
          !dmnRunnerState.isExpanded ? "kogito--editor__drawer-content-onClose" : "kogito--editor__drawer-content-open"
        }
        panelContent={
          <DmnRunnerErrorBoundary>
            <DmnRunnerDrawerPanelContent />
          </DmnRunnerErrorBoundary>
        }
      >
        <DrawerContentBody className={"kogito--editor__drawer-content-body"}>{props.children}</DrawerContentBody>
      </DrawerContent>
    </Drawer>
  );
}
