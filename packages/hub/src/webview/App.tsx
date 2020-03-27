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
import "@patternfly/patternfly/patternfly-variables.css";
import "@patternfly/patternfly/patternfly-addons.css";
import "@patternfly/patternfly/patternfly.css";
import "../../static/resources/style.css";
import * as electron from "electron";
import {
  Alert,
  AlertActionCloseButton,
  AlertProps,
  Brand,
  Button,
  ButtonVariant,
  Card,
  CardBody,
  CardFooter,
  CardHead,
  Dropdown,
  DropdownItem,
  Gallery,
  InputGroup,
  KebabToggle,
  Modal,
  Page,
  PageHeader,
  PageSection,
  Text,
  TextContent,
  TextInput,
  TextList,
  TextListItem,
  TextListVariants,
  TextVariants
} from "@patternfly/react-core";
import { Constants } from "../common/Constants";
import { SearchIcon } from "@patternfly/react-icons";
import { CommandExecutionResult } from "../common/CommandExecutionResult";
import IpcRendererEvent = Electron.IpcRendererEvent;

enum ExtensionStatus {
  UNKNOWN,
  NOT_INSTALLED,
  INSTALLING,
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
  const [vscode_installModalOpen, setVscode_installModalOpen] = useState(false);
  const [vscode_alreadyOpenModalOpen, setVscode_alreadyOpenModalOpen] = useState(false);
  const [vscode_stillOpenAfterRelaunch, setVscode_stillOpenAfterRelaunch] = useState(false);
  const [vscode_kebabOpen, setVscode_kebabOpen] = useState(false);
  const [vscode_location, setVscode_location] = useState("");
  const [vscode_status, setVscode_status] = useState(ExtensionStatus.UNKNOWN);

  const vscode_launch = useCallback(() => {
    electron.ipcRenderer.send("vscode__launch", {});
  }, []);

  const vscode_launchAfterToldToClose = useCallback(() => {
    setVscode_stillOpenAfterRelaunch(false);
    electron.ipcRenderer.send("vscode__launch_after_told_to_close", {});
  }, []);

  const vscode_toggleInstallModal = useCallback(() => {
    setVscode_installModalOpen(!vscode_installModalOpen);
  }, [vscode_installModalOpen]);

  const vscode_toggleAlreadyOpenModal = useCallback(() => {
    setVscode_alreadyOpenModalOpen(!vscode_alreadyOpenModalOpen);
  }, [vscode_alreadyOpenModalOpen]);

  const vscode_toggleKebab = useCallback(() => {
    setVscode_kebabOpen(!vscode_kebabOpen);
  }, [vscode_kebabOpen]);

  const vscode_requestInstall = useCallback(() => {
    electron.ipcRenderer.send("vscode__install_extension", { location: vscode_location });
    setVscode_installModalOpen(false);
    setVscode_status(ExtensionStatus.INSTALLING);
  }, [vscode_location]);

  const vscode_requestUninstall = useCallback(() => {
    electron.ipcRenderer.send("vscode__uninstall_extension", {});
    setVscode_installModalOpen(false);
    setVscode_status(ExtensionStatus.UNINSTALLING);
  }, [vscode_location]);

  useElectronIpcResponse(
    "vscode__already_open",
    () => {
      setVscode_alreadyOpenModalOpen(true);
    },
    []
  );

  useElectronIpcResponse(
    "vscode__still_open_after_told_to_close",
    () => {
      setVscode_stillOpenAfterRelaunch(true);
    },
    []
  );

  useElectronIpcResponse(
    "vscode__launch_complete",
    () => {
      setVscode_stillOpenAfterRelaunch(false);
      setVscode_alreadyOpenModalOpen(false);
    },
    []
  );

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
    "vscode__install_extension_complete",
    (data: CommandExecutionResult) => {
      if (data.success) {
        setVscode_status(ExtensionStatus.INSTALLED);
        pushNewAlert({ variant: "success", title: "VSCode extension successfully installed." });
      } else {
        setVscode_status(ExtensionStatus.NOT_INSTALLED);
        pushNewAlert({ variant: "danger", title: "Error while installing VSCode extension." });
        console.info(data.output);
      }
    },
    [pushNewAlert]
  );

  useElectronIpcResponse(
    "vscode__uninstall_extension_complete",
    (data: CommandExecutionResult) => {
      if (data.success) {
        setVscode_status(ExtensionStatus.NOT_INSTALLED);
        pushNewAlert({ variant: "info", title: "VSCode extension successfully uninstalled." });
      } else {
        setVscode_status(ExtensionStatus.INSTALLED);
        pushNewAlert({ variant: "danger", title: "Error while uninstalling VSCode extension." });
        console.info(data.output);
      }
    },
    [pushNewAlert]
  );

  const vscode_chooseLocation = useCallback(() => {
    return electron.remote.dialog.showOpenDialog({ properties: ["openDirectory", "openFile"] }).then(res => {
      if (res.canceled) {
        return;
      }

      setVscode_location(res.filePaths[0]);
    });
  }, []);

  const vscode_locationChange = useCallback((value: string) => {
    setVscode_location(value);
  }, []);

  const vscode_message = useMemo(() => {
    switch (vscode_status) {
      case ExtensionStatus.INSTALLED:
        return "Installed";
      case ExtensionStatus.UNINSTALLING:
        return "Uninstalling..";
      case ExtensionStatus.INSTALLING:
        return "Installing..";
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
  //
  // DESKTOP
  const [desktop_kebabOpen, setDesktop_kebabOpen] = useState(false);
  const desktop_toggleKebab = useCallback(() => {
    setDesktop_kebabOpen(!desktop_kebabOpen);
  }, [desktop_kebabOpen]);

  const desktop_launch = useCallback((e: React.MouseEvent) => {
    e.preventDefault();
    electron.ipcRenderer.send("desktop_launch", {});
  }, []);

  useElectronIpcResponse(
    "desktop__launch_complete",
    (data: CommandExecutionResult) => {
      console.info(data.success);
      console.info(data.output);
    },
    []
  );

  //
  //
  //
  // CHROME
  const [chrome_modalOpen, setChrome_modalOpen] = useState(false);
  const [chrome_status, setChrome_status] = useState(ExtensionStatus.UNKNOWN);

  const chrome_toggleModal = useCallback(() => {
    setChrome_modalOpen(!chrome_modalOpen);
  }, [chrome_modalOpen]);

  const chrome_message = useMemo(() => {
    switch (chrome_status) {
      case ExtensionStatus.INSTALLED:
        return;
      case ExtensionStatus.UNINSTALLING:
        return "";
      case ExtensionStatus.INSTALLING:
        return "";
      case ExtensionStatus.NOT_INSTALLED:
        return "";
      case ExtensionStatus.UNKNOWN:
        return "";
      default:
        return "";
    }
  }, [chrome_status]);

  const chrome_openKogitoToolingReleasesPage = useCallback((e: React.MouseEvent) => {
    e.preventDefault();
    electron.shell.openExternal(Constants.KOGITO_TOOLING_RELEASES_URL);
  }, []);

  const chrome_openGitHub = useCallback((e: React.MouseEvent) => {
    e.preventDefault();
    electron.shell.openExternal(Constants.GITHUB_URL);
  }, []);

  //
  //
  //
  // ONLINE
  const online_open = useCallback((e: React.MouseEvent) => {
    e.preventDefault();
    electron.shell.openExternal(Constants.ONLINE_EDITOR_URL);
  }, []);

  return (
    <Page
      header={
        <PageHeader logo={<Brand src={"images/BusinessModeler_Logo.svg"} alt="Business Modeler Hub Preview" />} />
      }
      className={"kogito--editor-landing"}
    >
      <PageSection variant="dark" noPadding={true} style={{ flexBasis: "100%" }}>
        <div className={"kogito--alert-container"}>
          <div style={{ width: "500px" }}>
            {alerts.map(alert => (
              <React.Fragment key={alert.time}>
                <Alert
                  style={{ marginBottom: "10px" }}
                  variant={alert.variant}
                  title={alert.title}
                  action={<AlertActionCloseButton onClose={() => removeAlert(alert.time)} />}
                />
              </React.Fragment>
            ))}
          </div>
        </div>
      </PageSection>
      <PageSection isFilled={true}>
        <Gallery gutter="lg" className={"kogito-desktop__file-gallery"}>
          <Card className={"kogito--desktop__files-card"}>
            <CardHead style={{ display: "flex", justifyContent: "space-between", alignItems: "start" }}>
              <img src={"images/desktop-logo.svg"} />
              <Dropdown
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
            </CardHead>
            <CardBody>
              <TextContent>
                <Text component={TextVariants.h3}>Kogito VSCode extension</Text>
                <br />
                <Text>Launches VSCode ready to use with Kogito</Text>
              </TextContent>
            </CardBody>
            <CardFooter style={{ display: "flex", justifyContent: "space-between" }}>
              {(vscode_status === ExtensionStatus.NOT_INSTALLED || vscode_status === ExtensionStatus.INSTALLING) && (
                <Button
                  variant={"secondary"}
                  isDisabled={vscode_status === ExtensionStatus.INSTALLING}
                  onClick={vscode_toggleInstallModal}
                >
                  Install
                </Button>
              )}
              {(vscode_status === ExtensionStatus.INSTALLED || vscode_status === ExtensionStatus.UNINSTALLING) && (
                <Button
                  variant={"secondary"}
                  isDisabled={vscode_status === ExtensionStatus.UNINSTALLING}
                  onClick={vscode_launch}
                >
                  Launch
                </Button>
              )}
              <Text style={{ display: "flex", alignItems: "center" }}>{vscode_message}</Text>
            </CardFooter>
          </Card>
          <Modal
            isSmall={true}
            title="Install VSCode extension"
            isOpen={vscode_installModalOpen}
            onClose={vscode_toggleInstallModal}
            actions={[
              <Button
                key="confirm"
                variant="primary"
                onClick={vscode_requestInstall}
                isDisabled={vscode_location === ""}
              >
                Install
              </Button>,
              <Button key="cancel" variant="link" onClick={vscode_toggleInstallModal}>
                Cancel
              </Button>
            ]}
          >
            <Text>Choose VSCode to install extension</Text>
            <InputGroup>
              <TextInput
                type="search"
                aria-label="search input example"
                value={vscode_location}
                onChange={vscode_locationChange}
              />
              <Button
                onClick={vscode_chooseLocation}
                variant={ButtonVariant.plain}
                aria-label="search button for search input"
              >
                <SearchIcon />
              </Button>
            </InputGroup>
          </Modal>
          <Modal
            isSmall={true}
            title={"Oops!"}
            isOpen={vscode_alreadyOpenModalOpen}
            onClose={vscode_toggleAlreadyOpenModal}
            actions={[
              <Button key="confirm" variant="primary" onClick={vscode_launchAfterToldToClose}>
                Launch
              </Button>,
              <Button key="cancel" variant="link" onClick={vscode_toggleAlreadyOpenModal}>
                Cancel
              </Button>
            ]}
          >
            {vscode_stillOpenAfterRelaunch && (
              <>
                <Alert variant="danger" isInline={true} title="Please close VSCode" />
                <br />
              </>
            )}
            <Text>Looks like VSCode is already open and was not started by Business Modeler Hub Preview.</Text>
            <br />
            <Text>Please close VSCode and retry.</Text>
          </Modal>
          {/*CHROME*/}
          <Card className={"kogito--desktop__files-card"}>
            <CardHead style={{ display: "flex", justifyContent: "space-between", alignItems: "start" }}>
              <img src={"images/chrome-github-logo.svg"} />
            </CardHead>
            <CardBody>
              <Text component={TextVariants.h3}>Kogito GitHub extension for Chrome</Text>
              <br />
              <Text>Installs the Kogito extension to an existing version of Chrome</Text>
            </CardBody>
            <CardFooter style={{ display: "flex", justifyContent: "space-between" }}>
              <Button variant={"secondary"} onClick={chrome_toggleModal}>
                Install
              </Button>
              <Text style={{ display: "flex", alignItems: "center" }}>{chrome_message}</Text>
            </CardFooter>
          </Card>
          <Modal
            width={"70%"}
            title="Install Kogito extension on Chrome"
            isOpen={chrome_modalOpen}
            onClose={chrome_toggleModal}
            actions={[
              <Button key="cancel" variant="link" onClick={chrome_toggleModal}>
                Done
              </Button>
            ]}
          >
            <TextContent>
              <TextList component={TextListVariants.ol}>
                <TextListItem>Follow these instructions to install the Kogito GitHub Chrome extension</TextListItem>
                <TextListItem>
                  Download the Chrome Extension ZIP from our{" "}
                  <Button variant={"link"} isInline={true} onClick={chrome_openKogitoToolingReleasesPage}>
                    releases page
                  </Button>
                  .
                </TextListItem>
                <TextListItem>
                  Open Chrome and go to <b>{Constants.CHROME_EXTENSIONS_TAB}</b>.
                </TextListItem>
                <br />
                <img
                  width={"550px"}
                  src={"images/chrome-extension-installation-guide.gif"}
                  alt={"Chrome Extension installation guide"}
                />
                <br />
                <br />
                <TextListItem>Enable developer mode on the top-right corner.</TextListItem>
                <TextListItem>Click in Load Unpacked and choose the unziped Chrome Extension directory.</TextListItem>
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
          <Card className={"kogito--desktop__files-card"}>
            <CardHead style={{ display: "flex", justifyContent: "space-between", alignItems: "start" }}>
              <img src={"images/desktop-logo.svg"} />
              <Dropdown
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
            </CardHead>
            <CardBody>
              <Text component={TextVariants.h3}>Business Modeler Desktop Preview</Text>
              <br />
              <Text>Launches the desktop version of Business Modeler Preview</Text>
            </CardBody>
            <CardFooter>
              <Button variant={"secondary"} onClick={desktop_launch}>
                Launch
              </Button>
            </CardFooter>
          </Card>
          {/**/}
          <Card className={"kogito--desktop__files-card"}>
            <CardHead style={{ display: "flex", justifyContent: "space-between", alignItems: "start" }}>
              <img src={"images/online-logo.svg"} />
            </CardHead>
            <CardBody>
              <TextContent>
                <Text component={TextVariants.h3}>Business Modeler Preview</Text>
                <br />
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
