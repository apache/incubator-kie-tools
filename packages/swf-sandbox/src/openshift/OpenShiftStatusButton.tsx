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
import { useMemo } from "react";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { OpenshiftIcon } from "@patternfly/react-icons/dist/js/icons/openshift-icon";
import { OpenShiftInstanceStatus } from "./OpenShiftInstanceStatus";
import { useSettings, useSettingsDispatch } from "../settings/SettingsContext";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { SettingsTabs } from "../settings/SettingsDrawerBody";

export function OpenshiftStatusButton() {
  const settings = useSettings();
  const settingsDispatch = useSettingsDispatch();

  const isOpenshiftSandboxConnected = useMemo(
    () => settings.openshift.status === OpenShiftInstanceStatus.CONNECTED,
    [settings.openshift.status]
  );

  return (
    <Tooltip
      className="kogito--editor__light-tooltip"
      content={
        <div>
          {isOpenshiftSandboxConnected
            ? "You are successfully connected to an OpenShift instance."
            : "You are not connected to any OpenShift instance."}
        </div>
      }
      trigger="mouseenter"
      position="auto"
    >
      <Button
        variant={ButtonVariant.plain}
        onClick={() => settingsDispatch.open(SettingsTabs.OPENSHIFT)}
        aria-label="Openshift Settings"
        className={"kie-tools--masthead-hoverable-dark"}
      >
        <OpenshiftIcon color={isOpenshiftSandboxConnected ? undefined : "gray"} />
      </Button>
    </Tooltip>
  );
}
