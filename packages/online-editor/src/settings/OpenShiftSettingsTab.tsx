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
import { useCallback, useState } from "react";
import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { useSettings, useSettingsDispatch } from "./SettingsContext";
import { OpenShiftInstanceStatus } from "../openshift/OpenShiftInstanceStatus";
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { CheckCircleIcon } from "@patternfly/react-icons/dist/js/icons/check-circle-icon";
import { Text, TextContent } from "@patternfly/react-core/dist/js/components/Text";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { OpenShiftSettingsTabSimpleConfig } from "./OpenShiftSettingsTabSimpleConfig";
import { saveConfigCookie } from "../openshift/OpenShiftSettingsConfig";
import { OpenShiftSettingsTabWizardConfig } from "./OpenShiftSettingsTabWizardConfig";
import { obfuscate } from "../accounts/ConnectToGitHubSection";

export enum OpenShiftSettingsTabMode {
  SIMPLE,
  WIZARD,
}

export function OpenShiftSettingsTab() {
  const settings = useSettings();
  const settingsDispatch = useSettingsDispatch();

  const [mode, setMode] = useState(OpenShiftSettingsTabMode.SIMPLE);

  const onDisconnect = useCallback(() => {
    settingsDispatch.openshift.setStatus(OpenShiftInstanceStatus.DISCONNECTED);
    const newConfig = {
      namespace: settings.openshift.config.namespace,
      host: settings.openshift.config.host,
      token: "",
    };
    settingsDispatch.openshift.setConfig(newConfig);
    saveConfigCookie(newConfig);
  }, [settings.openshift.config, settingsDispatch.openshift]);

  return (
    <Page>
      <PageSection>
        {settings.openshift.status === OpenShiftInstanceStatus.CONNECTED && (
          <EmptyState>
            <EmptyStateIcon icon={CheckCircleIcon} color={"var(--pf-global--success-color--100)"} />
            <TextContent>
              <Text component={"h2"}>{"You're connected to OpenShift."}</Text>
            </TextContent>
            <EmptyStateBody>
              <TextContent>
                Deploying DMN decisions is <b>enabled</b>.
              </TextContent>
              <br />
              <TextContent>
                <b>Token: </b>
                <i>{obfuscate(settings.openshift.config.token)}</i>
              </TextContent>
              <TextContent>
                <b>Host: </b>
                <i>{settings.openshift.config.host}</i>
              </TextContent>
              <TextContent>
                <b>Namespace (project): </b>
                <i>{settings.openshift.config.namespace}</i>
              </TextContent>
              <br />
              <Button variant={ButtonVariant.tertiary} onClick={onDisconnect}>
                Disconnect
              </Button>
            </EmptyStateBody>
          </EmptyState>
        )}
        {(settings.openshift.status === OpenShiftInstanceStatus.DISCONNECTED ||
          settings.openshift.status === OpenShiftInstanceStatus.EXPIRED) && (
          <>
            {mode === OpenShiftSettingsTabMode.SIMPLE && <OpenShiftSettingsTabSimpleConfig setMode={setMode} />}
            {mode === OpenShiftSettingsTabMode.WIZARD && <OpenShiftSettingsTabWizardConfig setMode={setMode} />}
          </>
        )}
      </PageSection>
    </Page>
  );
}
