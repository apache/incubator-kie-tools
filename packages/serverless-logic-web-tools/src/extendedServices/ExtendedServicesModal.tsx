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

import { Alert, AlertVariant } from "@patternfly/react-core/dist/js/components/Alert";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { ExpandableSection } from "@patternfly/react-core/dist/js/components/ExpandableSection";
import { Form, FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { Label } from "@patternfly/react-core/dist/js/components/Label";
import { List, ListItem } from "@patternfly/react-core/dist/js/components/List";
import { Modal, ModalVariant } from "@patternfly/react-core/dist/js/components/Modal";
import { SelectDirection } from "@patternfly/react-core/dist/js/components/Select";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import {
  Wizard,
  WizardContext,
  WizardContextConsumer,
  WizardFooter,
} from "@patternfly/react-core/dist/js/components/Wizard";
import * as React from "react";
import { useCallback, useContext, useEffect, useMemo, useState } from "react";
import { AnimatedTripleDotLabel } from "./AnimatedTripleDotLabel";
import { useAppI18n } from "../i18n";
import { I18nHtml, I18nWrapped } from "@kie-tools-core/i18n/dist/react-components";
import { SelectOs } from "../os/SelectOs";
import { getOperatingSystem, OperatingSystem } from "@kie-tools-core/operating-system";
import { useExtendedServices } from "./ExtendedServicesContext";
import { ExtendedServicesStatus } from "./ExtendedServicesStatus";
import { ExtendedServicesConfig } from "../settings/SettingsContext";

enum ModalPage {
  INITIAL,
  WIZARD,
  USE,
}

const UBUNTU_APP_INDICATOR_LIB = "apt install libappindicator3-dev";
const FEDORA_APP_INDICATOR_LIB = "dnf install libappindicator-gtk3";

export function ExtendedServicesModal() {
  const { i18n } = useAppI18n();
  const [operatingSystem, setOperatingSystem] = useState(getOperatingSystem() ?? OperatingSystem.LINUX);
  const [modalPage, setModalPage] = useState<ModalPage>(ModalPage.INITIAL);
  const extendedServices = useExtendedServices();

  const KIE_SANDBOX_EXTENDED_SERVICES_MACOS_DMG = useMemo(
    () => `kie_sandbox_extended_services_macos_${extendedServices.version}.dmg`,
    [extendedServices.version]
  );
  const KIE_SANDBOX_EXTENDED_SERVICES_MACOS_APP = useMemo(() => "Extended Services.app", []);
  const KIE_SANDBOX_EXTENDED_SERVICES_WINDOWS_EXE = useMemo(
    () => `kie_sandbox_extended_services_windows_${extendedServices.version}.exe`,
    [extendedServices.version]
  );
  const KIE_SANDBOX_EXTENDED_SERVICES_LINUX_TAG_GZ = useMemo(
    () => `kie_sandbox_extended_services_linux_${extendedServices.version}.tar.gz`,
    [extendedServices.version]
  );
  const KIE_SANDBOX_EXTENDED_SERVICES_BINARIES = useMemo(() => "kie_sandbox_extended_services", []);

  const downloadExtendedServicesUrl = useMemo(() => {
    switch (operatingSystem) {
      case OperatingSystem.MACOS:
        return process.env.WEBPACK_REPLACE__extendedServicesMacOsDownloadUrl;
      case OperatingSystem.WINDOWS:
        return process.env.WEBPACK_REPLACE__extendedServicesWindowsDownloadUrl;
      case OperatingSystem.LINUX:
      default:
        return process.env.WEBPACK_REPLACE__extendedServicesLinuxDownloadUrl;
    }
  }, [operatingSystem]);

  const macOsWizardSteps = useMemo(
    () => [
      {
        name: i18n.terms.install,
        component: (
          <>
            {extendedServices.outdated && (
              <>
                <Alert
                  variant={AlertVariant.warning}
                  isInline={true}
                  title={i18n.extendedServices.modal.wizard.outdatedAlert.title}
                >
                  {i18n.extendedServices.modal.wizard.outdatedAlert.message}
                </Alert>
                <br />
              </>
            )}
            <List>
              <ListItem>
                <TextContent>
                  <Text component={TextVariants.p}>
                    <Text
                      id="extended-services-modal-download-macos"
                      component={TextVariants.a}
                      href={downloadExtendedServicesUrl}
                    >
                      {i18n.terms.download}
                    </Text>
                    {i18n.extendedServices.modal.wizard.macos.install.download}
                  </Text>
                </TextContent>
              </ListItem>
              <ListItem>
                <TextContent>
                  <Text component={TextVariants.p}>
                    <I18nWrapped components={{ file: <Label>{KIE_SANDBOX_EXTENDED_SERVICES_MACOS_DMG}</Label> }}>
                      {i18n.extendedServices.modal.wizard.macos.install.openFile}
                    </I18nWrapped>
                  </Text>
                </TextContent>
              </ListItem>
              <ListItem>
                <TextContent>
                  <Text>
                    <I18nWrapped
                      components={{
                        file: <Label>{KIE_SANDBOX_EXTENDED_SERVICES_MACOS_APP}</Label>,
                        folder: <Label>{i18n.terms.macosApplicationFolder}</Label>,
                      }}
                    >
                      {i18n.extendedServices.modal.wizard.macos.install.dragFileToApplicationsFolder}
                    </I18nWrapped>
                  </Text>
                </TextContent>
              </ListItem>
            </List>
          </>
        ),
      },
      {
        name: i18n.terms.start,
        component: (
          <>
            {extendedServices.status === ExtendedServicesStatus.STOPPED ? (
              <>
                <Alert
                  variant={AlertVariant.warning}
                  isInline={true}
                  title={i18n.extendedServices.modal.wizard.stoppedAlert.title}
                >
                  {i18n.extendedServices.modal.wizard.stoppedAlert.message}
                </Alert>
                <br />
                <List>
                  <ListItem>
                    <TextContent>
                      <Text component={TextVariants.p}>
                        {i18n.extendedServices.modal.wizard.macos.start.stopped.startInstruction}
                      </Text>
                    </TextContent>
                  </ListItem>
                  <ListItem>
                    <TextContent>
                      <Text component={TextVariants.p}>
                        <I18nWrapped components={{ file: <Label>{KIE_SANDBOX_EXTENDED_SERVICES_MACOS_APP}</Label> }}>
                          {i18n.extendedServices.modal.wizard.macos.start.stopped.launchExtendedServices}
                        </I18nWrapped>
                      </Text>
                    </TextContent>
                  </ListItem>
                </List>
              </>
            ) : (
              <>
                <TextContent>
                  <Text component={TextVariants.p}>
                    {i18n.extendedServices.modal.wizard.macos.start.firstTime.title}
                  </Text>
                </TextContent>
                <br />
                <List>
                  <ListItem>
                    <TextContent>
                      <Text component={TextVariants.p}>
                        <I18nWrapped components={{ folder: <Label>{i18n.terms.macosApplicationFolder}</Label> }}>
                          {i18n.extendedServices.modal.wizard.macos.start.firstTime.openApplicationsFolder}
                        </I18nWrapped>
                      </Text>
                    </TextContent>
                  </ListItem>
                  <ListItem>
                    <TextContent>
                      <Text component={TextVariants.p}>
                        <I18nWrapped components={{ file: <Label>{KIE_SANDBOX_EXTENDED_SERVICES_MACOS_APP}</Label> }}>
                          {i18n.extendedServices.modal.wizard.macos.start.firstTime.openAndCancel}
                        </I18nWrapped>
                      </Text>
                    </TextContent>
                  </ListItem>
                  <ListItem>
                    <TextContent>
                      <Text component={TextVariants.p}>
                        <I18nWrapped
                          components={{
                            file: <Label>{KIE_SANDBOX_EXTENDED_SERVICES_MACOS_APP}</Label>,
                            again: <b>{i18n.extendedServices.modal.wizard.macos.start.firstTime.again}</b>,
                          }}
                        >
                          {i18n.extendedServices.modal.wizard.macos.start.firstTime.openInstruction}
                        </I18nWrapped>
                      </Text>
                    </TextContent>
                  </ListItem>
                </List>

                <br />

                <TextContent>
                  <Text component={TextVariants.p}>
                    {i18n.extendedServices.modal.wizard.macos.start.alreadyRanBefore}
                  </Text>
                </TextContent>
                <br />
                <List>
                  <ListItem>
                    <TextContent>
                      <Text component={TextVariants.p}>
                        <I18nWrapped components={{ file: <Label>{KIE_SANDBOX_EXTENDED_SERVICES_MACOS_APP}</Label> }}>
                          {i18n.extendedServices.modal.wizard.macos.start.launchExtendedServices}
                        </I18nWrapped>
                      </Text>
                    </TextContent>
                  </ListItem>
                </List>
                <br />
                <br />
                <hr />
                <br />
                <ExpandableSection
                  toggleTextExpanded={i18n.extendedServices.modal.wizard.macos.start.advanced.title}
                  toggleTextCollapsed={i18n.extendedServices.modal.wizard.macos.start.advanced.title}
                >
                  <ExtendedServicesPortForm />
                  <br />
                  <TextContent>
                    <Text component={TextVariants.p}>
                      {i18n.extendedServices.modal.wizard.macos.start.advanced.runFollowingCommand}
                    </Text>
                  </TextContent>
                  <br />
                  <TextContent>
                    <Text component={TextVariants.p} className={"kogito--code"}>
                      /Applications/KIE\ Tooling\ Extended\ Services.app/Contents/MacOs/kogito -p{" "}
                      {extendedServices.config.port}
                    </Text>
                  </TextContent>
                  <br />
                </ExpandableSection>
              </>
            )}
          </>
        ),
      },
    ],
    [
      i18n,
      extendedServices.outdated,
      extendedServices.status,
      extendedServices.config.port,
      downloadExtendedServicesUrl,
      KIE_SANDBOX_EXTENDED_SERVICES_MACOS_DMG,
      KIE_SANDBOX_EXTENDED_SERVICES_MACOS_APP,
    ]
  );

  const windowsWizardSteps = useMemo(
    () => [
      {
        name: i18n.terms.install,
        component: (
          <>
            {extendedServices.outdated && (
              <>
                <Alert
                  variant={AlertVariant.warning}
                  isInline={true}
                  title={i18n.extendedServices.modal.wizard.outdatedAlert.title}
                >
                  {i18n.extendedServices.modal.wizard.outdatedAlert.message}
                </Alert>
                <br />
              </>
            )}
            <List>
              <ListItem>
                <TextContent>
                  <Text component={TextVariants.p}>
                    <Text
                      id="extended-services-modal-download-windows"
                      component={TextVariants.a}
                      href={downloadExtendedServicesUrl}
                    >
                      {i18n.terms.download}
                    </Text>
                    {i18n.extendedServices.modal.wizard.windows.install.keepDownload}
                  </Text>
                </TextContent>
              </ListItem>
              <ListItem>
                <TextContent>
                  <I18nWrapped components={{ file: <Label>{KIE_SANDBOX_EXTENDED_SERVICES_WINDOWS_EXE}</Label> }}>
                    {i18n.extendedServices.modal.wizard.windows.install.moveTheFile}
                  </I18nWrapped>
                </TextContent>
              </ListItem>
            </List>
          </>
        ),
      },
      {
        name: i18n.terms.start,
        component: (
          <>
            {extendedServices.status === ExtendedServicesStatus.STOPPED ? (
              <>
                <Alert
                  variant={AlertVariant.warning}
                  isInline={true}
                  title={i18n.extendedServices.modal.wizard.stoppedAlert.title}
                >
                  {i18n.extendedServices.modal.wizard.stoppedAlert.message}
                </Alert>
                <br />
                <List>
                  <ListItem>
                    <TextContent>
                      <Text component={TextVariants.p}>
                        {i18n.extendedServices.modal.wizard.windows.start.stopped.startInstruction}
                      </Text>
                    </TextContent>
                  </ListItem>
                  <ListItem>
                    <TextContent>
                      <Text component={TextVariants.p}>
                        <I18nWrapped components={{ file: <Label>{KIE_SANDBOX_EXTENDED_SERVICES_WINDOWS_EXE}</Label> }}>
                          {i18n.extendedServices.modal.wizard.windows.start.stopped.launchExtendedServices}
                        </I18nWrapped>
                      </Text>
                    </TextContent>
                  </ListItem>
                </List>
              </>
            ) : (
              <>
                <TextContent>
                  <Text component={TextVariants.p}>
                    {i18n.extendedServices.modal.wizard.windows.start.firstTime.title}
                  </Text>
                </TextContent>
                <br />
                <List>
                  <ListItem>
                    <TextContent>
                      <Text component={TextVariants.p}>
                        <I18nWrapped components={{ file: <Label>{KIE_SANDBOX_EXTENDED_SERVICES_WINDOWS_EXE}</Label> }}>
                          {i18n.extendedServices.modal.wizard.windows.start.firstTime.openFolder}
                        </I18nWrapped>
                      </Text>
                    </TextContent>
                  </ListItem>
                  <ListItem>
                    <TextContent>
                      <Text component={TextVariants.p}>
                        {i18n.extendedServices.modal.wizard.windows.start.firstTime.runAnyway}
                      </Text>
                    </TextContent>
                  </ListItem>
                </List>

                <br />

                <TextContent>
                  <Text component={TextVariants.p}>
                    {i18n.extendedServices.modal.wizard.windows.start.alreadyRanBefore}
                  </Text>
                </TextContent>
                <br />
                <List>
                  <ListItem>
                    <TextContent>
                      <Text component={TextVariants.p}>
                        <I18nWrapped components={{ file: <Label>{KIE_SANDBOX_EXTENDED_SERVICES_WINDOWS_EXE}</Label> }}>
                          {i18n.extendedServices.modal.wizard.windows.start.launchExtendedServices}
                        </I18nWrapped>
                      </Text>
                    </TextContent>
                  </ListItem>
                </List>
                <br />
                <br />
                <hr />
                <br />
                <ExpandableSection
                  toggleTextExpanded={i18n.extendedServices.modal.wizard.windows.start.advanced.title}
                  toggleTextCollapsed={i18n.extendedServices.modal.wizard.windows.start.advanced.title}
                >
                  <ExtendedServicesPortForm />
                  <br />
                  <TextContent>
                    <Text component={TextVariants.p}>
                      {i18n.extendedServices.modal.wizard.windows.start.advanced.runFollowingCommand}
                    </Text>
                  </TextContent>
                  <br />
                  <TextContent>
                    <Text component={TextVariants.p} className={"kogito--code"}>
                      &quot;kie-sandbox-extended-services_windows_{extendedServices.version}.exe&quot; -p{" "}
                      {extendedServices.config.port}
                    </Text>
                  </TextContent>
                  <br />
                </ExpandableSection>
              </>
            )}
          </>
        ),
      },
    ],
    [
      i18n,
      extendedServices.outdated,
      extendedServices.status,
      extendedServices.version,
      extendedServices.config.port,
      downloadExtendedServicesUrl,
      KIE_SANDBOX_EXTENDED_SERVICES_WINDOWS_EXE,
    ]
  );

  const linuxWizardSteps = useMemo(
    () => [
      {
        name: i18n.terms.install,
        component: (
          <>
            {extendedServices.outdated && (
              <>
                <Alert
                  variant={AlertVariant.warning}
                  isInline={true}
                  title={i18n.extendedServices.modal.wizard.outdatedAlert.title}
                >
                  {i18n.extendedServices.modal.wizard.outdatedAlert.message}
                </Alert>
                <br />
              </>
            )}
            <List>
              <ListItem>
                <TextContent>
                  <Text component={TextVariants.p}>
                    <Text
                      id="extended-services-modal-download-linux"
                      component={TextVariants.a}
                      href={downloadExtendedServicesUrl}
                    >
                      {i18n.terms.download}
                    </Text>{" "}
                    {i18n.extendedServices.modal.wizard.linux.install.download}
                  </Text>
                </TextContent>
              </ListItem>
              <ListItem>
                <TextContent>
                  <Text component={TextVariants.p}>
                    {i18n.extendedServices.modal.wizard.linux.install.installAppIndicator}
                  </Text>
                  <List>
                    <ListItem>
                      <I18nWrapped components={{ package: <Label>{UBUNTU_APP_INDICATOR_LIB}</Label> }}>
                        {i18n.extendedServices.modal.wizard.linux.install.ubuntuDependency}
                      </I18nWrapped>
                    </ListItem>
                    <ListItem>
                      <I18nWrapped components={{ package: <Label>{FEDORA_APP_INDICATOR_LIB}</Label> }}>
                        {i18n.extendedServices.modal.wizard.linux.install.fedoraDependency}
                      </I18nWrapped>
                    </ListItem>
                  </List>
                </TextContent>
              </ListItem>
              <ListItem>
                <TextContent>
                  <Text component={TextVariants.p}>
                    <I18nWrapped components={{ file: <Label>{KIE_SANDBOX_EXTENDED_SERVICES_LINUX_TAG_GZ}</Label> }}>
                      {i18n.extendedServices.modal.wizard.linux.install.extractContent}
                    </I18nWrapped>
                  </Text>
                </TextContent>
              </ListItem>
            </List>
            <br />
            <TextContent>
              <Text component={TextVariants.p}>
                <I18nWrapped components={{ file: <Label>{KIE_SANDBOX_EXTENDED_SERVICES_BINARIES}</Label> }}>
                  {i18n.extendedServices.modal.wizard.linux.install.binaryExplanation}
                </I18nWrapped>
              </Text>
            </TextContent>
          </>
        ),
      },
      {
        name: i18n.terms.start,
        component: (
          <>
            {extendedServices.status === ExtendedServicesStatus.STOPPED && (
              <div>
                <Alert
                  variant={AlertVariant.warning}
                  isInline={true}
                  title={i18n.extendedServices.modal.wizard.stoppedAlert.title}
                >
                  {i18n.extendedServices.modal.wizard.stoppedAlert.message}
                </Alert>
                <br />
              </div>
            )}
            <List>
              <ListItem>
                <TextContent>
                  <Text component={TextVariants.p}>{i18n.extendedServices.modal.wizard.linux.start.openTerminal}</Text>
                </TextContent>
              </ListItem>
              <ListItem>
                <TextContent>
                  <Text component={TextVariants.p}>
                    <I18nWrapped components={{ file: <Label>{KIE_SANDBOX_EXTENDED_SERVICES_BINARIES}</Label> }}>
                      {i18n.extendedServices.modal.wizard.linux.start.goToFolder}
                    </I18nWrapped>
                  </Text>
                </TextContent>
              </ListItem>
              <ListItem>
                <TextContent>
                  <Text component={TextVariants.p}>
                    {i18n.extendedServices.modal.wizard.linux.start.runCommand}
                    <Text component={TextVariants.p} className={"kogito--code"}>
                      ./kie_sandbox_extended_services
                    </Text>
                  </Text>
                </TextContent>
              </ListItem>
              <br />
              <hr />
              <br />
              <ExpandableSection
                toggleTextExpanded={i18n.extendedServices.modal.wizard.linux.start.advanced.title}
                toggleTextCollapsed={i18n.extendedServices.modal.wizard.linux.start.advanced.title}
              >
                <ExtendedServicesPortForm />
                <br />
                <TextContent>
                  <Text component={TextVariants.p}>
                    <I18nWrapped components={{ file: <Label>{KIE_SANDBOX_EXTENDED_SERVICES_BINARIES}</Label> }}>
                      {i18n.extendedServices.modal.wizard.linux.start.advanced.runFollowingCommand}
                    </I18nWrapped>
                  </Text>
                </TextContent>
                <br />
                <TextContent>
                  <Text component={TextVariants.p} className={"kogito--code"}>
                    ./kie-sandbox-extended-services -p {extendedServices.config.port}
                  </Text>
                </TextContent>
                <br />
              </ExpandableSection>
            </List>
          </>
        ),
      },
    ],
    [
      i18n,
      extendedServices.outdated,
      extendedServices.status,
      extendedServices.config.port,
      downloadExtendedServicesUrl,
      KIE_SANDBOX_EXTENDED_SERVICES_LINUX_TAG_GZ,
      KIE_SANDBOX_EXTENDED_SERVICES_BINARIES,
    ]
  );

  const wizardSteps = useMemo(() => {
    switch (operatingSystem) {
      case OperatingSystem.MACOS:
        return macOsWizardSteps;
      case OperatingSystem.WINDOWS:
        return windowsWizardSteps;
      case OperatingSystem.LINUX:
      default:
        return linuxWizardSteps;
    }
  }, [operatingSystem, macOsWizardSteps, windowsWizardSteps, linuxWizardSteps]);

  useEffect(() => {
    if (
      [ExtendedServicesStatus.NOT_RUNNING, ExtendedServicesStatus.AVAILABLE].includes(extendedServices.status) &&
      extendedServices.installTriggeredBy
    ) {
      setModalPage(ModalPage.INITIAL);
    } else if (extendedServices.status === ExtendedServicesStatus.RUNNING) {
      setModalPage(ModalPage.USE);
    } else {
      setModalPage(ModalPage.WIZARD);
    }
  }, [extendedServices.status, extendedServices.outdated, extendedServices.installTriggeredBy]);

  const onClose = useCallback(() => {
    extendedServices.setInstallTriggeredBy(undefined);
    extendedServices.setModalOpen(false);
    if (extendedServices.status === ExtendedServicesStatus.STOPPED || extendedServices.outdated) {
      extendedServices.setStatus(ExtendedServicesStatus.NOT_RUNNING);
    }
  }, [extendedServices]);

  const modalTitle = useMemo(() => {
    switch (modalPage) {
      case ModalPage.INITIAL:
      case ModalPage.USE:
        return "";
      case ModalPage.WIZARD:
        return i18n.extendedServices.modal.wizard.title;
    }
  }, [modalPage, i18n]);

  const modalVariant = useMemo(() => {
    switch (modalPage) {
      case ModalPage.INITIAL:
      case ModalPage.USE:
        return ModalVariant.medium;
      case ModalPage.WIZARD:
        return ModalVariant.large;
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
      description={modalPage === ModalPage.WIZARD && <p>{i18n.extendedServices.modal.wizard.description}</p>}
      footer={
        <>
          {modalPage === ModalPage.INITIAL && (
            <Button
              className="pf-u-mt-xl kogito--editor__extended-services-modal-initial-center"
              onClick={() => setModalPage(ModalPage.WIZARD)}
            >
              {i18n.terms.setup}
            </Button>
          )}
          {modalPage === ModalPage.WIZARD && (
            <div className={"kogito--editor__extended-services-modal-footer"}>
              <Alert
                variant={"default"}
                isInline={true}
                className={"kogito--editor__extended-services-modal-footer-alert"}
                title={
                  <AnimatedTripleDotLabel
                    label={i18n.extendedServices.modal.wizard.footerWaitingToConnect}
                    interval={750}
                  />
                }
              />
            </div>
          )}
          {modalPage === ModalPage.USE && <></>}
        </>
      }
    >
      {modalPage === ModalPage.INITIAL && (
        <div className={"kogito--editor__extended-services-modal-initial"}>
          <div className={"kogito--editor__extended-services-modal-initial-title"}>
            <TextContent>
              <Text component={TextVariants.h1}>Deploy to your OpenShift instance</Text>
            </TextContent>
          </div>
          <div className="pf-u-mt-xl pf-u-display-flex pf-u-flex-direction-row" style={{ marginTop: "16px" }}>
            <div>
              <TextContent>
                <Text component={TextVariants.p}>
                  <I18nHtml>{i18n.openshift.introduction.explanation}</I18nHtml>
                </Text>
              </TextContent>
            </div>
          </div>
        </div>
      )}
      {modalPage === ModalPage.WIZARD && (
        <div>
          <Form isHorizontal={true}>
            <FormGroup fieldId={"select-os"} label={i18n.terms.os.full}>
              <SelectOs selected={operatingSystem} onSelect={setOperatingSystem} direction={SelectDirection.down} />
            </FormGroup>
          </Form>
          <br />
          <Wizard
            steps={wizardSteps}
            height={400}
            footer={<ExtendedServicesWizardFooter onClose={onClose} steps={wizardSteps} setModalPage={setModalPage} />}
          />
        </div>
      )}
      {modalPage === ModalPage.USE && (
        <div className={"kogito--editor__extended-services-modal-use"}>
          <div className={"kogito--editor__extended-services-modal-use-title"}>
            <TextContent>
              <Text component={TextVariants.h1}>{i18n.extendedServices.modal.use.title}</Text>
            </TextContent>
          </div>
          <div className={"kogito--editor__extended-services-modal-use-main-content"}>
            <TextContent className={"kogito--editor__extended-services-modal-use-margin"}>
              <Text component={TextVariants.h3} className={"kogito--editor__extended-services-modal-use-text-align"}>
                {i18n.extendedServices.modal.use.connected}
              </Text>
            </TextContent>
            <br />
            <Button
              variant={"primary"}
              type="submit"
              onClick={onClose}
              className={"kogito--editor__extended-services-modal-use-margin"}
            >
              {i18n.extendedServices.modal.use.backToSandbox}
            </Button>
          </div>
        </div>
      )}
    </Modal>
  );
}

interface WizardImperativeControlProps {
  onClose: () => void;
  steps: Array<{ component: JSX.Element; name: string }>;
  setModalPage: React.Dispatch<ModalPage>;
}

function ExtendedServicesWizardFooter(props: WizardImperativeControlProps) {
  const wizardContext = useContext(WizardContext);
  const { status } = useExtendedServices();
  const { i18n } = useAppI18n();

  useEffect(() => {
    if (status === ExtendedServicesStatus.STOPPED) {
      wizardContext.goToStepByName(props.steps[1].name);
    }
  }, [status, props.setModalPage, props.steps, wizardContext]);

  return (
    <WizardFooter>
      <WizardContextConsumer>
        {({ activeStep, goToStepByName, goToStepById, onNext, onBack }) => {
          if (activeStep.name !== i18n.terms.start) {
            return (
              <>
                <Button variant="primary" type="submit" onClick={onNext}>
                  {i18n.terms.next}
                </Button>
              </>
            );
          } else {
            return (
              <>
                <Button variant="primary" type="submit" onClick={onBack}>
                  {i18n.terms.back}
                </Button>
              </>
            );
          }
        }}
      </WizardContextConsumer>
    </WizardFooter>
  );
}

function ExtendedServicesPortForm() {
  const { config, saveNewConfig } = useExtendedServices();
  const { i18n } = useAppI18n();

  return (
    <>
      <Text component={TextVariants.p}>
        <I18nWrapped
          components={{
            port: <Text className={"kogito--code"}>{config.port}</Text>,
          }}
        >
          {i18n.extendedServices.modal.wizard.advancedSettings.title}
        </I18nWrapped>
      </Text>
      <br />
      <Form isHorizontal={true}>
        <FormGroup
          fieldId={"extended-services-port"}
          label={i18n.extendedServices.modal.wizard.advancedSettings.label}
          validated={
            config.port === "" || parseInt(config.port, 10) < 0 || parseInt(config.port, 10) > 65353
              ? "error"
              : "success"
          }
          helperTextInvalid={i18n.extendedServices.modal.wizard.advancedSettings.helperTextInvalid}
        >
          <TextInput
            value={config.port}
            type={"number"}
            onChange={(value) => saveNewConfig(new ExtendedServicesConfig(config.host, value))}
          />
        </FormGroup>
      </Form>
    </>
  );
}
