/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
import { useCallback, useEffect, useMemo, useState } from "react";
import "@patternfly/patternfly/base/patternfly-variables.css";
import "@patternfly/patternfly/patternfly-addons.scss";
import "@patternfly/patternfly/patternfly.scss";
import "../../static/resources/style.css";
import * as electron from "electron";
import {
  Alert,
  AlertActionCloseButton,
  AlertProps,
  Brand,
  Button,
  Card,
  CardBody,
  CardFooter,
  CardHeader,
  ClipboardCopy,
  Dropdown,
  DropdownItem,
  Gallery,
  KebabToggle,
  Modal,
  Page,
  PageHeader,
  PageSection,
  Text,
  TextContent,
  TextList,
  TextListItem,
  TextListVariants,
  TextVariants,
  Title
} from "@patternfly/react-core";
import { Constants } from "../common/Constants";
import { CommandExecutionResult } from "../common/CommandExecutionResult";
import { OperatingSystem } from "@kogito-tooling/editor-envelope-protocol";
import IpcRendererEvent = Electron.IpcRendererEvent;

enum ExtensionStatus {
  UNKNOWN,
  NOT_INSTALLED,
  INSTALLED,
  UNINSTALLING
}

function useElectronIpcResponse<T>(msgKey: string, callback: (data: T) => void, dependencies: any[]) {
  useEffect(() => {
    const ipcCallback = (e: IpcRendererEvent, data: T) => {
      callback(data);
    };

    electron.ipcRenderer.on(msgKey, ipcCallback);
    return () => {
      electron.ipcRenderer.removeListener(msgKey, ipcCallback);
    };
  }, dependencies);
}

export function App() {
  //
  //
  // ALERTS
  const [alerts, setAlerts] = useState(new Array<AlertProps & { time: number }>());

  const removeAlert = useCallback(
    (time: number) => {
      setAlerts(alerts.filter(a => a.time !== time));
    },
    [alerts]
  );

  const pushNewAlert = useCallback(
    (alert: AlertProps) => {
      const newAlert = {
        ...alert,
        time: new Date().getMilliseconds()
      };

      setAlerts([...alerts, newAlert]);
    },
    [alerts]
  );

  //
  //
  // VSCODE
  const [vscode_kebabOpen, setVscode_kebabOpen] = useState(false);
  const [vscode_status, setVscode_status] = useState(ExtensionStatus.UNKNOWN);

  const vscode_launch = useCallback(() => {
    electron.ipcRenderer.send("vscode__launch", {});
  }, []);

  const vscode_toggleKebab = useCallback(() => {
    setVscode_kebabOpen(!vscode_kebabOpen);
  }, [vscode_kebabOpen]);

  const vscode_install = useCallback((e: React.MouseEvent) => {
    e.preventDefault();
    electron.shell.openExternal(`vscode:extension/${Constants.VSCODE_EXTENSION_PACKAGE_NAME}`);
  }, []);

  const vscode_requestUninstall = useCallback(() => {
    electron.ipcRenderer.send("vscode__uninstall_extension", {});
    setVscode_status(ExtensionStatus.UNINSTALLING);
  }, []);

  useElectronIpcResponse(
    "vscode__list_extensions_complete",
    (data: CommandExecutionResult & { extensions: string[] }) => {
      if (data.extensions.indexOf(Constants.VSCODE_EXTENSION_PACKAGE_NAME) !== -1) {
        setVscode_status(ExtensionStatus.INSTALLED);
      } else {
        setVscode_status(ExtensionStatus.NOT_INSTALLED);
      }
    },
    [pushNewAlert]
  );

  useElectronIpcResponse(
    "vscode__uninstall_extension_complete",
    (data: CommandExecutionResult) => {
      if (data.success) {
        pushNewAlert({ variant: "info", title: "VS Code extension successfully uninstalled." });
      } else {
        pushNewAlert({ variant: "danger", title: "Error while uninstalling VS Code extension." });
        console.info(data.output);
      }

      electron.ipcRenderer.send("vscode__list_extensions", {});
    },
    [pushNewAlert]
  );

  const vscode_message = useMemo(() => {
    switch (vscode_status) {
      case ExtensionStatus.INSTALLED:
        return "Installed";
      case ExtensionStatus.UNINSTALLING:
        return "Uninstalling..";
      case ExtensionStatus.NOT_INSTALLED:
        return "Available";
      case ExtensionStatus.UNKNOWN:
        return "Loading..";
      default:
        return "";
    }
  }, [vscode_status]);

  useEffect(() => {
    electron.ipcRenderer.send("vscode__list_extensions", {});
  }, []);

  //
  //
  // DESKTOP
  const [desktop_kebabOpen, setDesktop_kebabOpen] = useState(false);
  const desktop_toggleKebab = useCallback(() => {
    setDesktop_kebabOpen(!desktop_kebabOpen);
  }, [desktop_kebabOpen]);

  const desktop_launch = useCallback((e: React.MouseEvent) => {
    e.preventDefault();
    electron.ipcRenderer.send("desktop__launch", {});
  }, []);

  useElectronIpcResponse(
    "desktop__launch_complete",
    (data: CommandExecutionResult) => {
      console.info(data.output);
    },
    []
  );

  //
  //
  // CHROME
  const [chrome_modalOpen, setChrome_modalOpen] = useState(false);

  const chrome_toggleModal = useCallback(() => {
    setChrome_modalOpen(!chrome_modalOpen);
  }, [chrome_modalOpen]);

  const chrome_openDownloadGoogleChrome = useCallback((e: React.MouseEvent) => {
    e.preventDefault();
    electron.shell.openExternal(Constants.DOWNLOAD_GOOGLE_CHROME_URL);
  }, []);

  const chrome_openKogitoToolingReleasesPage = useCallback((e: React.MouseEvent) => {
    e.preventDefault();
    electron.shell.openExternal(Constants.GITHUB_EXTENSION_CHROME_STORE_URL);
  }, []);

  const chrome_openGitHub = useCallback((e: React.MouseEvent) => {
    e.preventDefault();
    electron.shell.openExternal(Constants.GITHUB_URL);
  }, []);

  //
  //
  // ONLINE
  const online_open = useCallback((e: React.MouseEvent) => {
    e.preventDefault();
    electron.shell.openExternal(Constants.ONLINE_EDITOR_URL);
  }, []);

  useElectronIpcResponse(
    "desktop__launch_complete",
    (data: CommandExecutionResult) => {
      if (data.os === OperatingSystem.MACOS && !data.success) {
        pushNewAlert({
          variant: "danger",
          width: "90%",
          title: (
            <>
              <p>Error while launching Business Modeler Preview Desktop. This is a known issue on macOS.</p>
              <br />
              <p>Try again after executing the command below on a Terminal window.</p>
              <p>You have to be at the same directory as 'Business Modeler Hub Preview.app'.</p>
              <ClipboardCopy isReadOnly={true}>{`chmod -R u+x "Business Modeler Hub Preview.app" `}</ClipboardCopy>
            </>
          )
        });
      }
    },
    []
  );

  return (
    <Page
      header={
        <PageHeader logo={<Brand src={"images/BusinessModelerHub_Logo.svg"} alt="Business Modeler Hub Preview" />} />
      }
      className={"kogito--editor-landing"}
    >
      <PageSection isFilled={true}>
        <div className={"kogito--alert-container"}>
          {alerts.map(alert => (
            <React.Fragment key={alert.time}>
              <Alert
                style={{ marginBottom: "10px", width: alert.width ?? "500px" }}
                variant={alert.variant}
                title={alert.title}
                actionClose={<AlertActionCloseButton onClose={() => removeAlert(alert.time)} />}
              />
            </React.Fragment>
          ))}
        </div>
        <Gallery hasGutter={true} className={"kogito-desktop__file-gallery"}>
          <Card>
            <CardHeader style={{ display: "flex", justifyContent: "space-between", alignItems: "start" }}>
              <img style={{ height: "52px" }} src={"images/vscode-logo.svg"} />
              <Dropdown
                position="right"
                onSelect={vscode_toggleKebab}
                toggle={<KebabToggle onToggle={vscode_toggleKebab} />}
                isOpen={vscode_kebabOpen}
                isPlain={true}
                dropdownItems={[
                  <DropdownItem key="update" component="button" isDisabled={true}>
                    No updates available
                  </DropdownItem>,
                  <DropdownItem
                    key="uninstall"
                    component="button"
                    onClick={vscode_requestUninstall}
                    isDisabled={vscode_status !== ExtensionStatus.INSTALLED}
                  >
                    Uninstall
                  </DropdownItem>
                ]}
              />
            </CardHeader>
            <CardBody>
              <Title headingLevel={"h1"} size={"xl"}>Kogito Bundle VS Code extension</Title>
              <br />
              <TextContent>
                <Text>Launches VS Code ready to use with Kogito BPMN, DMN and Test Scenario Editors</Text>
              </TextContent>
            </CardBody>
            <CardFooter style={{ display: "flex", justifyContent: "space-between" }}>
              {vscode_status === ExtensionStatus.NOT_INSTALLED && (
                <Button variant={"secondary"} onClick={vscode_install}>
                  Install
                </Button>
              )}
              {vscode_status === ExtensionStatus.INSTALLED && (
                <Button variant={"secondary"} onClick={vscode_launch}>
                  Launch
                </Button>
              )}
              <Text style={{ display: "flex", alignItems: "center" }}>{vscode_message}</Text>
            </CardFooter>
          </Card>
          {/*CHROME*/}
          <Card>
            <CardHeader style={{ display: "flex", justifyContent: "space-between", alignItems: "start" }}>
              <img style={{ height: "52px" }} src={"images/chrome-github-logo.svg"} />
            </CardHeader>
            <CardBody>
              <Title headingLevel={"h1"} size={"xl"}>BPMN, DMN & Test Scenario Editors for GitHub</Title>
              <br />
              <TextContent>
                <Text>Install the BPMN, DMN & Test Scenario Editors for GitHub on Chrome browser</Text>
              </TextContent>
            </CardBody>
            <CardFooter style={{ display: "flex", justifyContent: "space-between" }}>
              <Button variant={"secondary"} onClick={chrome_toggleModal}>
                Install
              </Button>
              <Text style={{ display: "flex", alignItems: "center" }} />
            </CardFooter>
          </Card>
          <Modal
            width={"70%"}
            title="Install BPMN, DMN & Test Scenario Editor for GitHub"
            isOpen={chrome_modalOpen}
            onClose={chrome_toggleModal}
            actions={[
              <Button key="cancel" variant="link" onClick={chrome_toggleModal}>
                Done
              </Button>
            ]}
          >
            <TextContent>
              <Text component={TextVariants.p}>
                To be able to install and use the BPMN, DMN and Test Scenario Editor for GitHub it's necessary to have
                the Chrome browser installed on your computer.
              </Text>
              <Text component={TextVariants.p}>
                In case you don't have it, you can download it{" "}
                <Button variant={"link"} isInline={true} onClick={chrome_openDownloadGoogleChrome}>
                  here
                </Button>
                .
              </Text>
              <Text component={TextVariants.p}>
                If you already have the Chrome browser or have just downloaded it, follow this steps:
              </Text>
              <TextList component={TextListVariants.ol}>
                <TextListItem>
                  Open the BPMN, DMN and Test Scenario Editor for GitHub on the{" "}
                  <Button variant={"link"} isInline={true} onClick={chrome_openKogitoToolingReleasesPage}>
                    Chrome Store
                  </Button>{" "}
                  using your Chrome browser
                </TextListItem>
                <TextListItem>Click on "Add to Chrome".</TextListItem>
                <TextListItem>Read the permissions and in case you agree, click on “Add Extension”</TextListItem>
                <TextListItem>
                  Done! You can go to{" "}
                  <Button variant={"link"} isInline={true} onClick={chrome_openGitHub}>
                    GitHub
                  </Button>{" "}
                  now and start using it.
                </TextListItem>
              </TextList>
            </TextContent>
          </Modal>
          {/*DESKTOP*/}
          <Card>
            <CardHeader style={{ display: "flex", justifyContent: "space-between", alignItems: "start" }}>
              <img style={{ height: "52px" }} src={"images/desktop-logo.svg"} />
              <Dropdown
                position="right"
                onSelect={desktop_toggleKebab}
                toggle={<KebabToggle onToggle={desktop_toggleKebab} />}
                isOpen={desktop_kebabOpen}
                isPlain={true}
                dropdownItems={[
                  <DropdownItem key="action" component="button" isDisabled={true}>
                    No updates available
                  </DropdownItem>
                ]}
              />
            </CardHeader>
            <CardBody>
              <Title headingLevel={"h1"} size={"xl"}>Business Modeler Desktop Preview</Title>
              <br />
              <TextContent>
                <Text>Launches the desktop version of Business Modeler Preview</Text>
              </TextContent>
            </CardBody>
            <CardFooter>
              <Button variant={"secondary"} onClick={desktop_launch}>
                Launch
              </Button>
            </CardFooter>
          </Card>
          {/**/}
          <Card>
            <CardHeader style={{ display: "flex", justifyContent: "space-between", alignItems: "start" }}>
              <img style={{ height: "52px" }} src={"images/online-logo.svg"} />
            </CardHeader>
            <CardBody>
              <Title headingLevel={"h1"} size={"xl"}>Business Modeler Preview</Title>
              <br />
              <TextContent>
                <Text>Navigates to the Online Modeler Preview site</Text>
              </TextContent>
            </CardBody>
            <CardFooter>
              <Button variant={"secondary"} onClick={online_open}>
                Launch
              </Button>
            </CardFooter>
          </Card>
        </Gallery>
      </PageSection>
    </Page>
  );
}
