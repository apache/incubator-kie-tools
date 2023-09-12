/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import React from "react";
import { KubernetesConnection } from "@kie-tools-core/kubernetes-bridge/dist/service";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { Modal, ModalVariant } from "@patternfly/react-core/dist/js/components/Modal";
import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Text, TextContent } from "@patternfly/react-core/dist/js/components/Text";
import { AddCircleOIcon } from "@patternfly/react-icons/dist/js/icons/add-circle-o-icon";
import { CheckCircleIcon } from "@patternfly/react-icons/dist/js/icons/check-circle-icon";
import { useCallback, useMemo, useState } from "react";
import { OpenShiftInstanceStatus } from "../../openshift/OpenShiftInstanceStatus";
import { obfuscate } from "../github/GitHubSettings";
import { useSettings, useSettingsDispatch } from "../SettingsContext";
import { SettingsPageContainer } from "../SettingsPageContainer";
import { SettingsPageProps } from "../types";
import { saveConfigCookie } from "./OpenShiftSettingsConfig";
import { OpenShiftSettingsSimpleConfig } from "./OpenShiftSettingsSimpleConfig";

const PAGE_TITLE = "OpenShift";

export function OpenShiftSettings(props: SettingsPageProps) {
  const settings = useSettings();
  const settingsDispatch = useSettingsDispatch();
  const [isModalOpen, setIsModalOpen] = useState(false);

  const handleModalToggle = useCallback(() => {
    setIsModalOpen((prevIsModalOpen) => !prevIsModalOpen);
  }, []);

  const onDisconnect = useCallback(() => {
    settingsDispatch.openshift.setStatus(OpenShiftInstanceStatus.DISCONNECTED);
    const newConfig: KubernetesConnection = {
      namespace: settings.openshift.config.namespace,
      host: settings.openshift.config.host,
      token: "",
      insecurelyDisableTlsCertificateValidation: false,
    };
    settingsDispatch.openshift.setConfig(newConfig);
    saveConfigCookie(newConfig);
  }, [settings.openshift.config, settingsDispatch.openshift]);

  const devModeEnabledLabel = useMemo(
    () => (settings.openshift.isDevModeEnabled ? "enabled" : "disabled"),
    [settings.openshift.isDevModeEnabled]
  );

  return (
    <SettingsPageContainer
      pageTitle={PAGE_TITLE}
      subtitle={
        <>
          Data you provide here is necessary for deploying models you design to your OpenShift instance.
          <br />
          All information is locally stored in your browser and never shared with anyone.
        </>
      }
    >
      <PageSection>
        <PageSection variant={"light"}>
          {settings.openshift.status === OpenShiftInstanceStatus.CONNECTED ? (
            <EmptyState>
              <EmptyStateIcon icon={CheckCircleIcon} color={"var(--pf-global--success-color--100)"} />
              <TextContent>
                <Text component={"h2"}>{"You're connected to OpenShift."}</Text>
              </TextContent>
              <EmptyStateBody>
                Deploying models is <b>enabled</b>.
                <br />
                Uploading models to Dev Mode is <b>{devModeEnabledLabel}</b>.
                <br />
                <b>Token: </b>
                <i>{obfuscate(settings.openshift.config.token)}</i>
                <br />
                <b>Host: </b>
                <i>{settings.openshift.config.host}</i>
                <br />
                <b>Namespace (project): </b>
                <i>{settings.openshift.config.namespace}</i>
                <br />
                <b>TLS Certificate Verification: </b>
                <i>{settings.openshift.config.insecurelyDisableTlsCertificateValidation ? "Disabled" : "Enabled"}</i>
                <br />
                <br />
                <Button variant={ButtonVariant.tertiary} onClick={onDisconnect}>
                  Disconnect
                </Button>
              </EmptyStateBody>
            </EmptyState>
          ) : (
            <EmptyState>
              <EmptyStateIcon icon={AddCircleOIcon} />
              <TextContent>
                <Text component={"h2"}>You are not connected to OpenShift.</Text>
              </TextContent>
              <EmptyStateBody>
                You currently have no OpenShift connections. <br />
                <br />
                <Button variant={ButtonVariant.primary} onClick={handleModalToggle} data-testid="add-connection-button">
                  Add connection
                </Button>
              </EmptyStateBody>
            </EmptyState>
          )}
        </PageSection>
      </PageSection>

      {props.pageContainerRef.current && (
        <Modal
          title="Add connection"
          isOpen={
            isModalOpen &&
            (settings.openshift.status === OpenShiftInstanceStatus.DISCONNECTED ||
              settings.openshift.status === OpenShiftInstanceStatus.EXPIRED)
          }
          onClose={handleModalToggle}
          variant={ModalVariant.large}
          appendTo={props.pageContainerRef.current || document.body}
        >
          <OpenShiftSettingsSimpleConfig />
        </Modal>
      )}
    </SettingsPageContainer>
  );
}
