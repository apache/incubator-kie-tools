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

import * as React from "react";
import { I18nHtml, I18nWrappedTemplate } from "@kie-tools-core/i18n/dist/react-components";
import { Alert } from "@patternfly/react-core/dist/js/components/Alert";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { ClipboardCopy } from "@patternfly/react-core/dist/js/components/ClipboardCopy";
import { ExpandableSection } from "@patternfly/react-core/dist/js/components/ExpandableSection";
import { Form, FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { HelperText, HelperTextItem } from "@patternfly/react-core/dist/js/components/HelperText";
import { List, ListItem } from "@patternfly/react-core/dist/js/components/List";
import { Modal, ModalVariant } from "@patternfly/react-core/dist/js/components/Modal";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { ExclamationCircleIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-circle-icon";
import { useCallback, useEffect, useMemo, useState } from "react";
import { useEnv } from "../env/hooks/EnvContext";
import { useOnlineI18n } from "../i18n";
import { useRoutes } from "../navigation/Hooks";
import { useSettingsDispatch } from "../settings/SettingsContext";
import { AnimatedTripleDotLabel } from "./AnimatedTripleDotLabel";
import { DependentFeature, useExtendedServices } from "./ExtendedServicesContext";
import { ExtendedServicesStatus } from "./ExtendedServicesStatus";

enum ModalPage {
  INITIAL,
  WIZARD,
  DISABLED,
}

const UBUNTU_APP_INDICATOR_LIB = "apt install libayatana-appindicator3-1";
const FEDORA_APP_INDICATOR_LIB = "dnf install libayatana-appindicator3-1";

export function ExtendedServicesModal() {
  const { i18n } = useOnlineI18n();
  const routes = useRoutes();
  const [modalPage, setModalPage] = useState<ModalPage>(ModalPage.INITIAL);
  const extendedServices = useExtendedServices();
  const { env } = useEnv();

  useEffect(() => {
    if (extendedServices.status === ExtendedServicesStatus.NOT_RUNNING) {
      setModalPage(ModalPage.INITIAL);
    } else if (
      extendedServices.status === ExtendedServicesStatus.STOPPED &&
      env.KIE_SANDBOX_DISABLE_EXTENDED_SERVICES_WIZARD === true
    ) {
      setModalPage(ModalPage.DISABLED);
    } else if (extendedServices.status === ExtendedServicesStatus.STOPPED) {
      setModalPage(ModalPage.WIZARD);
    } else if (extendedServices.status === ExtendedServicesStatus.RUNNING) {
      extendedServices.setModalOpen(false);
    }

    if (extendedServices.outdated) {
      setModalPage(ModalPage.WIZARD);
    }
  }, [
    extendedServices.status,
    extendedServices.outdated,
    extendedServices,
    env.KIE_SANDBOX_DISABLE_EXTENDED_SERVICES_WIZARD,
  ]);

  const onClose = useCallback(() => {
    setModalPage(ModalPage.INITIAL);
    extendedServices.setModalOpen(false);
    if (extendedServices.status === ExtendedServicesStatus.STOPPED || extendedServices.outdated) {
      extendedServices.setStatus(ExtendedServicesStatus.NOT_RUNNING);
    }
  }, [extendedServices]);

  const modalTitle = useMemo(() => {
    switch (modalPage) {
      case ModalPage.INITIAL:
        return "";
      case ModalPage.WIZARD:
        return i18n.dmnRunner.modal.wizard.title;
      case ModalPage.DISABLED:
        return i18n.dmnRunner.modal.wizard.disabled.title;
    }
  }, [modalPage, i18n]);

  const modalVariant = useMemo(() => {
    switch (modalPage) {
      case ModalPage.INITIAL:
        return ModalVariant.medium;
      case ModalPage.WIZARD:
        return ModalVariant.large;
      case ModalPage.DISABLED:
        return ModalVariant.medium;
    }
  }, [modalPage]);

  return (
    <Modal
      ouiaId="extended-services-modal"
      isOpen={extendedServices.isModalOpen}
      onClose={onClose}
      variant={modalVariant}
      aria-label={"Steps to enable Extended Services"}
      title={modalTitle}
      description={modalPage === ModalPage.WIZARD && <p>{i18n.dmnRunner.modal.wizard.description}</p>}
      footer={
        <>
          {modalPage === ModalPage.INITIAL && (
            <Button
              className="pf-v5-u-mt-xl kogito--editor__extended-services-modal-initial-center"
              onClick={() =>
                env.KIE_SANDBOX_DISABLE_EXTENDED_SERVICES_WIZARD === false
                  ? setModalPage(ModalPage.WIZARD)
                  : setModalPage(ModalPage.DISABLED)
              }
            >
              {i18n.terms.setup}
            </Button>
          )}
          {modalPage === ModalPage.WIZARD && (
            <div className={"kogito--editor__extended-services-modal-footer"}>
              <Alert
                variant={"custom"}
                isInline={true}
                className={"kogito--editor__extended-services-modal-footer-alert"}
                title={
                  <AnimatedTripleDotLabel label={i18n.dmnRunner.modal.wizard.footerWaitingToConnect} interval={750} />
                }
              />
            </div>
          )}
        </>
      }
    >
      {modalPage === ModalPage.INITIAL && (
        <div className={"kogito--editor__extended-services-modal-initial"}>
          <div className={"kogito--editor__extended-services-modal-initial-title"}>
            <TextContent>
              <Text component={TextVariants.h1}>
                {extendedServices.installTriggeredBy === DependentFeature.DEV_DEPLOYMENTS
                  ? i18n.names.devDeployments
                  : i18n.names.dmnRunner}
              </Text>
            </TextContent>
          </div>
          <br />
          {extendedServices.installTriggeredBy === DependentFeature.DMN_RUNNER && (
            <div className="pf-v5-u-display-flex pf-v5-u-flex-direction-row">
              <div className="pf-v5-u-w-25 pf-v5-u-ml-sm">
                <TextContent>
                  <Text component={TextVariants.p}>{i18n.dmnRunner.modal.initial.runDmnModels}</Text>
                </TextContent>
                <br />
                <TextContent className="pf-v5-u-mt-md">
                  <Text component={TextVariants.p}>{i18n.dmnRunner.modal.initial.explanation}</Text>
                </TextContent>
                <br />
                <TextContent className="pf-v5-u-mt-md">
                  <Text component={TextVariants.p}>
                    <I18nWrappedTemplate
                      text={i18n.dmnRunner.modal.initial.notificationPanelExplanation}
                      interpolationMap={{
                        icon: <ExclamationCircleIcon />,
                      }}
                    />
                  </Text>
                </TextContent>
              </div>
              <br />
              <div className="pf-v5-u-w-75 pf-v5-u-p-sm">
                <img
                  className="pf-v5-u-h-100"
                  src={routes.static.images.dmnRunnerGif.path({})}
                  alt={"DMN Runner usage"}
                  width={"100%"}
                />
              </div>
            </div>
          )}
          {extendedServices.installTriggeredBy === DependentFeature.DEV_DEPLOYMENTS && (
            <div className="pf-v5-u-mt-xl pf-v5-u-display-flex pf-v5-u-flex-direction-row">
              <div className="pf-v5-u-w-25 pf-v5-u-mr-sm">
                <TextContent>
                  <Text component={TextVariants.p}>{i18n.devDeployments.introduction.explanation}</Text>
                </TextContent>
                <br />
                <TextContent className="pf-v5-u-mt-md">
                  <Text component={TextVariants.p}>
                    <I18nHtml>{i18n.devDeployments.introduction.disclaimer}</I18nHtml>
                  </Text>
                </TextContent>
              </div>
              <br />
              <div className="pf-v5-u-w-75">
                <img
                  className="pf-v5-u-h-100"
                  src={routes.static.images.dmnDevDeploymentGif.path({})}
                  alt={"DMN Dev Deployments usage"}
                  width={"100%"}
                />
              </div>
            </div>
          )}
        </div>
      )}
      {modalPage === ModalPage.WIZARD && (
        <div>
          <br />

          <List>
            <ListItem>
              Install <a href="https://podman-desktop.io/">Podman Desktop</a> or any other local Container management
              platform.
            </ListItem>
            <ListItem>Run the Extended Services container locally.</ListItem>
            <ListItem>
              <ClipboardCopy isReadOnly hoverTip="Copy" clickTip="Copied">
                {`docker run --rm -p ${extendedServices.config.port}:${extendedServices.defaultContainerPort} ${extendedServices.imageUrl}`}
              </ClipboardCopy>
            </ListItem>
          </List>

          <br />
          <ExpandableSection toggleText={i18n.dmnRunner.modal.wizard.advancedSettings.label}>
            <ExtendedServicesPortForm />
          </ExpandableSection>
        </div>
      )}
      {modalPage === ModalPage.DISABLED && (
        <div>
          <Alert variant="danger" title={i18n.dmnRunner.modal.wizard.disabled.alert} aria-live="polite" isInline />
          <br />
          <List>
            <ListItem>
              <Text>{i18n.dmnRunner.modal.wizard.disabled.message}</Text>
            </ListItem>
            <ListItem>
              <Text>{i18n.dmnRunner.modal.wizard.disabled.helper}</Text>
            </ListItem>
          </List>
        </div>
      )}
    </Modal>
  );
}

function ExtendedServicesPortForm() {
  const { config } = useExtendedServices();
  const settingsDispatch = useSettingsDispatch();
  const { i18n } = useOnlineI18n();

  return (
    <>
      <Text component={TextVariants.p}>
        <I18nWrappedTemplate
          text={i18n.dmnRunner.modal.wizard.advancedSettings.title}
          interpolationMap={{
            port: <Text className={"kogito--code"}>{config.port}</Text>,
          }}
        />
      </Text>
      <br />
      <Form isHorizontal={true}>
        <FormGroup fieldId={"extended-services-port"} label={i18n.dmnRunner.modal.wizard.advancedSettings.label}>
          <TextInput
            value={config.port}
            type={"number"}
            onChange={(_event, value) =>
              settingsDispatch.set((settings) => {
                settings.extendedServices.port = `${value}`;
              })
            }
          />
          <HelperText>
            {config.port === "" || parseInt(config.port, 10) < 0 || parseInt(config.port, 10) > 65353 ? (
              <HelperTextItem variant="error" icon={<ExclamationCircleIcon />}>
                {i18n.dmnRunner.modal.wizard.advancedSettings.helperTextInvalid}
              </HelperTextItem>
            ) : (
              <HelperTextItem></HelperTextItem>
            )}
          </HelperText>
        </FormGroup>
      </Form>
    </>
  );
}
