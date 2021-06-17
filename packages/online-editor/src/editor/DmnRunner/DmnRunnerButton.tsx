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

import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { ConnectedIcon } from "@patternfly/react-icons/dist/js/icons/connected-icon";
import { DisconnectedIcon } from "@patternfly/react-icons/dist/js/icons/disconnected-icon";
import { ExclamationTriangleIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-triangle-icon";
import * as React from "react";
import { useCallback, useContext, useMemo } from "react";
import { DmnRunnerStatus } from "./DmnRunnerStatus";
import { useDmnRunner } from "./DmnRunnerContext";
import { GlobalContext } from "../../common/GlobalContext";
import { useOnlineI18n } from "../../common/i18n";

export function DmnRunnerButton() {
  const dmnRunner = useDmnRunner();
  const { i18n } = useOnlineI18n();
  const context = useContext(GlobalContext);

  const onDmnRunner = useCallback(() => {
    if (dmnRunner.status !== DmnRunnerStatus.RUNNING) {
      dmnRunner.setModalOpen(true);
    } else {
      if (dmnRunner.isDrawerExpanded) {
        dmnRunner.setDrawerExpanded(false);
      } else {
        dmnRunner.setDrawerExpanded(true);
      }
    }
  }, [dmnRunner.status, dmnRunner.isDrawerExpanded]);

  const shouldBlinkDmnRunnerConnectedIcon = useMemo(() => {
    return dmnRunner.status === DmnRunnerStatus.RUNNING && !dmnRunner.isDrawerExpanded;
  }, [dmnRunner.status, dmnRunner.isDrawerExpanded]);

  return (
    <>
      <Tooltip
        key={"is-chrome"}
        flipBehavior={["left"]}
        trigger={!context.isChrome ? "mouseenter focus" : ""}
        content={<p>{i18n.dmnRunner.button.available}</p>}
      >
        <Button
          id={"dmn-runner-button"}
          data-testid="run-button"
          variant={"tertiary"}
          onClick={onDmnRunner}
          aria-label={"Run"}
          className={"kogito--dmn-runner-button kogito--editor__toolbar"}
          isAriaDisabled={!context.isChrome}
          icon={
            <>
              {dmnRunner.outdated && (
                <Tooltip
                  key={"outdated"}
                  content={i18n.dmnRunner.button.tooltip.outdated}
                  flipBehavior={["left"]}
                  distance={20}
                  trigger={context.isChrome ? "mouseenter focus" : ""}
                >
                  <ExclamationTriangleIcon id={"dmn-runner-outdated-icon"} />
                </Tooltip>
              )}
              {!dmnRunner.outdated && (
                <>
                  {dmnRunner.status === DmnRunnerStatus.RUNNING ? (
                    <Tooltip
                      key={"connected"}
                      content={i18n.dmnRunner.button.tooltip.connected}
                      flipBehavior={["left"]}
                      distance={20}
                      trigger={context.isChrome ? "mouseenter focus" : ""}
                    >
                      <ConnectedIcon
                        id={"dmn-runner-connected-icon"}
                        className={shouldBlinkDmnRunnerConnectedIcon ? "blink-opacity" : ""}
                      />
                    </Tooltip>
                  ) : (
                    <Tooltip
                      key={"disconnected"}
                      content={i18n.dmnRunner.button.tooltip.disconnected}
                      flipBehavior={["left"]}
                      distance={20}
                      trigger={context.isChrome ? "mouseenter focus" : ""}
                    >
                      <DisconnectedIcon id={"dmn-runner-disconnected-icon"} />
                    </Tooltip>
                  )}
                </>
              )}
            </>
          }
        >
          {i18n.names.dmnRunner}
        </Button>
      </Tooltip>
    </>
  );
}
