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
import { useCallback, useContext, useEffect, useMemo, useState } from "react";
import {
  Wizard,
  WizardContext,
  WizardContextConsumer,
  WizardFooter,
} from "@patternfly/react-core/dist/js/components/Wizard";
import { getOperatingSystem, OperatingSystem } from "../../common/utils";
import { SelectOs } from "../../common/SelectOs";
import { AnimatedTripleDotLabel } from "../../common/AnimatedTripleDotLabel";
import { DmnRunnerStatus } from "./DmnRunnerStatus";
import { useDmnRunner } from "./DmnRunnerContext";
import { DMN_RUNNER_DEFAULT_PORT } from "./DmnRunnerContextProvider";
import { Alert, AlertVariant } from "@patternfly/react-core/dist/js/components/Alert";
import { List, ListItem } from "@patternfly/react-core/dist/js/components/List";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Label } from "@patternfly/react-core/dist/js/components/Label";
import { ExpandableSection } from "@patternfly/react-core/dist/js/components/ExpandableSection";
import { Form, FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { Modal, ModalVariant } from "@patternfly/react-core/dist/js/components/Modal";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { SelectDirection } from "@patternfly/react-core/dist/js/components/Select";
import { ExclamationCircleIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-circle-icon";

enum ModalPage {
  INITIAL,
  WIZARD,
  USE,
}

export function DmnRunnerModal() {
  const [operatingSystem, setOperatingSystem] = useState(getOperatingSystem() ?? OperatingSystem.LINUX);
  const [modalPage, setModalPage] = useState<ModalPage>(ModalPage.INITIAL);
  const dmnRunner = useDmnRunner();

  const downloadDmnRunnerUrl = useMemo(() => {
    switch (operatingSystem) {
      case OperatingSystem.MACOS:
        return "$_{WEBPACK_REPLACE__dmnRunnerMacOsDownloadUrl}";
      case OperatingSystem.WINDOWS:
        return "$_{WEBPACK_REPLACE__dmnRunnerWindowsDownloadUrl}";
      case OperatingSystem.LINUX:
      default:
        return "$_{WEBPACK_REPLACE__dmnRunnerLinuxDownloadUrl}";
    }
  }, [operatingSystem]);

  const macOsWizardSteps = useMemo(
    () => [
      {
        name: "Install",
        component: (
          <>
            {dmnRunner.outdated && (
              <>
                <Alert variant={AlertVariant.warning} isInline={true} title={"DMN Runner is outdated!"}>
                  It looks like you're using a outdated version of the DMN Runner. Follow the instructions below to
                  update.
                </Alert>
                <br />
              </>
            )}
            <List>
              <ListItem>
                <TextContent>
                  <Text component={TextVariants.p}>
                    <Text component={TextVariants.a} href={downloadDmnRunnerUrl}>
                      Download
                    </Text>{" "}
                    DMN Runner.
                  </Text>
                </TextContent>
              </ListItem>
              <ListItem>
                <TextContent>
                  <Text component={TextVariants.p}>
                    Open the <Label>dmn-runner-macos-v{dmnRunner.version}.dmg</Label> file.
                  </Text>
                </TextContent>
              </ListItem>
              <ListItem>
                <TextContent>
                  <Text>
                    Drag <Label>Kogito DMN Runner.app</Label> to the <Label>Applications</Label> folder.
                  </Text>
                </TextContent>
              </ListItem>
            </List>
          </>
        ),
      },
      {
        name: "Start",
        component: (
          <>
            {dmnRunner.status === DmnRunnerStatus.STOPPED ? (
              <>
                <Alert variant={AlertVariant.warning} isInline={true} title={"DMN Runner has stopped!"}>
                  It looks like the DMN Runner has suddenly stopped, please follow these instructions to start it again.
                </Alert>
                <br />
                <List>
                  <ListItem>
                    <TextContent>
                      <Text component={TextVariants.p}>
                        If you see the DMN Runner icon on your system bar, simply click it and select "Start".
                      </Text>
                    </TextContent>
                  </ListItem>
                  <ListItem>
                    <TextContent>
                      <Text component={TextVariants.p}>
                        If not, start the DMN Runner app by launching <Label>Kogito DMN Runner.app</Label>.
                      </Text>
                    </TextContent>
                  </ListItem>
                </List>
              </>
            ) : (
              <>
                <TextContent>
                  <Text component={TextVariants.p}>If you just installed DMN Runner:</Text>
                </TextContent>
                <br />
                <List>
                  <ListItem>
                    <TextContent>
                      <Text component={TextVariants.p}>
                        Open the <Label>Applications</Label> folder.
                      </Text>
                    </TextContent>
                  </ListItem>
                  <ListItem>
                    <TextContent>
                      <Text component={TextVariants.p}>
                        Right-click on <Label>Kogito DMN Runner.app</Label>, select "Open" and then "Cancel".
                      </Text>
                    </TextContent>
                  </ListItem>
                  <ListItem>
                    <TextContent>
                      <Text component={TextVariants.p}>
                        Right-click on <Label>Kogito DMN Runner.app</Label> <b>again</b> and then select "Open".
                      </Text>
                    </TextContent>
                  </ListItem>
                </List>

                <br />

                <TextContent>
                  <Text component={TextVariants.p}>If you already installed and ran the DMN Runner before:</Text>
                </TextContent>
                <br />
                <List>
                  <ListItem>
                    <TextContent>
                      <Text component={TextVariants.p}>
                        Launch the <Label>Kogito DMN Runner.app</Label>.
                      </Text>
                    </TextContent>
                  </ListItem>
                </List>
                <br />
                <br />
                <hr />
                <br />
                <ExpandableSection toggleTextExpanded="Advanced Settings" toggleTextCollapsed="Advanced Settings">
                  <DmnRunnerPortForm />
                  <br />
                  <TextContent>
                    <Text component={TextVariants.p}>
                      Run the following command on a Terminal tab to start DMN Runner on a different port:
                    </Text>
                  </TextContent>
                  <br />
                  <TextContent>
                    <Text component={TextVariants.p} className={"kogito--code"}>
                      /Applications/Kogito\ DMN\ Runner.app/Contents/MacOs/kogito -p {dmnRunner.port}
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
    [dmnRunner.status, dmnRunner.port, dmnRunner.saveNewPort, dmnRunner.outdated]
  );

  const windowsWizardSteps = useMemo(
    () => [
      {
        name: "Install",
        component: (
          <>
            {dmnRunner.outdated && (
              <>
                <Alert variant={AlertVariant.warning} isInline={true} title={"DMN Runner is outdated!"}>
                  It looks like you're using a outdated version of the DMN Runner. Follow the instructions below to
                  update.
                </Alert>
                <br />
              </>
            )}
            <List>
              <ListItem>
                <TextContent>
                  <Text component={TextVariants.p}>
                    <Text component={TextVariants.a} href={downloadDmnRunnerUrl}>
                      Download
                    </Text>{" "}
                    DMN Runner.
                  </Text>
                </TextContent>
              </ListItem>
              <br />
              <ListItem>
                <TextContent>
                  <Text component={TextVariants.p}>
                    Open the <Label>dmn-runner-macos-v{dmnRunner.version}.exe</Label> file.
                  </Text>
                </TextContent>
              </ListItem>
              <br />
              <ListItem>
                <TextContent>
                  <Text>
                    Drag <Label>Kogito DMN Runner.app</Label> to the <Label>Applications</Label> folder.
                  </Text>
                </TextContent>
              </ListItem>
            </List>
          </>
        ),
      },
      {
        name: "Start",
        component: (
          <>
            {dmnRunner.status === DmnRunnerStatus.STOPPED && (
              <div>
                <Alert variant={AlertVariant.warning} isInline={true} title={"DMN Runner has stopped!"}>
                  It looks like the DMN Runner has suddenly stopped, please follow these instructions to start it again.
                </Alert>
                <br />
              </div>
            )}
            <List>
              <ListItem>
                <TextContent>
                  <Text component={TextVariants.p}>
                    Open the <Label>Applications</Label> folder.
                  </Text>
                </TextContent>
              </ListItem>
              <br />
              <ListItem>
                <TextContent>
                  <Text component={TextVariants.p}>
                    Right-click on <Label>Kogito DMN Runner.app</Label>, select "Open" and then "Cancel".
                  </Text>
                </TextContent>
              </ListItem>
              <br />
              <ListItem>
                <TextContent>
                  <Text component={TextVariants.p}>
                    Right-click on <Label>Kogito DMN Runner.app</Label> <b>again</b> and then select "Open".
                  </Text>
                </TextContent>
              </ListItem>
              <br />
              <hr />
              <br />
              <ExpandableSection toggleTextExpanded="Advanced Settings" toggleTextCollapsed="Advanced Settings">
                <DmnRunnerPortForm />
                <br />
                <TextContent>
                  <Text component={TextVariants.p}>Run the following command on a Terminal tab:</Text>
                </TextContent>
                <br />
                <TextContent>
                  <Text component={TextVariants.p} className={"kogito--code"}>
                    /Applications/Kogito\ DMN\ Runner.app/Contents/MacOs/kogito -p {dmnRunner.port}
                  </Text>
                </TextContent>
                <br />
              </ExpandableSection>
            </List>
          </>
        ),
      },
    ],
    [dmnRunner.status, dmnRunner.port, dmnRunner.saveNewPort, dmnRunner.outdated]
  );

  const linuxWizardSteps = useMemo(
    () => [
      {
        name: "Install",
        component: (
          <>
            {dmnRunner.outdated && (
              <>
                <Alert variant={AlertVariant.warning} isInline={true} title={"DMN Runner is outdated!"}>
                  It looks like you're using a outdated version of the DMN Runner. Follow the instructions below to
                  update.
                </Alert>
                <br />
              </>
            )}
            <List>
              <ListItem>
                <TextContent>
                  <Text component={TextVariants.p}>
                    <Text component={TextVariants.a} href={downloadDmnRunnerUrl}>
                      Download
                    </Text>{" "}
                    DMN Runner.
                  </Text>
                </TextContent>
              </ListItem>
              <ListItem>
                <TextContent>
                  <Text component={TextVariants.p}>
                    Extract the contents of <Label>dmn-runner-linux-v{dmnRunner.version}.tar.gz</Label> to your location
                    of choice.
                  </Text>
                </TextContent>
              </ListItem>
            </List>
            <br />
            <TextContent>
              <Text component={TextVariants.p}>
                The DMN Runner binary, <Label>dmn-runner</Label>, is a single binary file, which means you can add it to
                your PATH or even configure it to execute when your computer starts.
              </Text>
            </TextContent>
          </>
        ),
      },
      {
        name: "Start",
        component: (
          <>
            {dmnRunner.status === DmnRunnerStatus.STOPPED && (
              <div>
                <Alert variant={AlertVariant.warning} isInline={true} title={"DMN Runner has stopped!"}>
                  It looks like the DMN Runner has suddenly stopped, please follow these instructions to start it again.
                </Alert>
                <br />
              </div>
            )}
            <List>
              <ListItem>
                <TextContent>
                  <Text component={TextVariants.p}>Open a Terminal window.</Text>
                </TextContent>
              </ListItem>
              <ListItem>
                <TextContent>
                  <Text component={TextVariants.p}>
                    Go to the folder where you placed the <Label>dmn-runner</Label> binary.
                  </Text>
                </TextContent>
              </ListItem>
              <ListItem>
                <TextContent>
                  <Text component={TextVariants.p}>
                    Run{" "}
                    <Text component={TextVariants.p} className={"kogito--code"}>
                      ./dmn-runner
                    </Text>
                  </Text>
                </TextContent>
              </ListItem>
              <br />
              <hr />
              <br />
              <ExpandableSection toggleTextExpanded="Advanced Settings" toggleTextCollapsed="Advanced Settings">
                <DmnRunnerPortForm />
                <br />
                <TextContent>
                  <Text component={TextVariants.p}>
                    Open a Terminal window and run the following command on the directory where you placed the{" "}
                    <Label>dmn-runner</Label> binary:
                  </Text>
                </TextContent>
                <br />
                <TextContent>
                  <Text component={TextVariants.p} className={"kogito--code"}>
                    ./dmn-runner -p {dmnRunner.port}
                  </Text>
                </TextContent>
                <br />
              </ExpandableSection>
            </List>
          </>
        ),
      },
    ],
    [dmnRunner.status, dmnRunner.port, dmnRunner.saveNewPort, dmnRunner.outdated]
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
    if (dmnRunner.status === DmnRunnerStatus.NOT_RUNNING) {
      setModalPage(ModalPage.INITIAL);
    } else if (dmnRunner.status === DmnRunnerStatus.STOPPED) {
      setModalPage(ModalPage.WIZARD);
    } else if (dmnRunner.status === DmnRunnerStatus.RUNNING) {
      setModalPage(ModalPage.USE);
    }

    if (dmnRunner.outdated) {
      setModalPage(ModalPage.WIZARD);
    }
  }, [dmnRunner.status, dmnRunner.outdated]);

  const onClose = useCallback(() => {
    dmnRunner.setModalOpen(false);
    if (dmnRunner.status === DmnRunnerStatus.STOPPED || dmnRunner.outdated) {
      dmnRunner.setStatus(DmnRunnerStatus.NOT_RUNNING);
    }
  }, [dmnRunner.status, dmnRunner.outdated]);

  const modalTitle = useMemo(() => {
    switch (modalPage) {
      case ModalPage.INITIAL:
      case ModalPage.USE:
        return "";
      case ModalPage.WIZARD:
        return "DMN Runner Setup";
    }
  }, [modalPage]);

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
      isOpen={dmnRunner.isModalOpen}
      onClose={onClose}
      variant={modalVariant}
      aria-label={"Steps to enable the DMN Runner"}
      title={modalTitle}
      description={
        modalPage === ModalPage.WIZARD && (
          <p>Choose your Operating System and follow the instructions to install and start the DMN Runner.</p>
        )
      }
      footer={
        <>
          {modalPage === ModalPage.INITIAL && <></>}
          {modalPage === ModalPage.WIZARD && (
            <div className={"kogito--editor__dmn-runner-modal-footer"}>
              <Alert
                variant={"default"}
                isInline={true}
                className={"kogito--editor__dmn-runner-modal-footer-alert"}
                title={<AnimatedTripleDotLabel label={"Waiting to connect to DMN Runner"} interval={750} />}
              />
            </div>
          )}
          {modalPage === ModalPage.USE && <></>}
        </>
      }
    >
      {modalPage === ModalPage.INITIAL && (
        <div
          style={{
            width: "100%",
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
            flexDirection: "column",
            marginLeft: "20px",
          }}
        >
          <div style={{ margin: "20px" }}>
            <TextContent>
              <Text component={TextVariants.h1}>DMN Runner</Text>
            </TextContent>
          </div>
          <div
            style={{
              display: "flex",
              justifyContent: "center",
              alignItems: "center",
              flexDirection: "column",
            }}
          >
            <div style={{ margin: "10px" }}>
              <TextContent>
                <Text component={TextVariants.p}>Run your DMN models and see live forms and results as you edit.</Text>
              </TextContent>
            </div>
            <br />
            <div>
              <img src={"./images/dmn-runner2.gif"} alt={"DMN Runner usage"} width={"100%"} />
            </div>
            <br />
            <div>
              <TextContent>
                <Text component={TextVariants.p}>
                  With its validation and execution capabilities, DMN Runner helps you create assertive DMN decisions.
                  Input nodes become interactive fields on an auto-generated form, and the results are displayed as
                  easy-to-read cards.
                </Text>
              </TextContent>
            </div>
            <br />
            <div>
              <TextContent>
                <Text component={TextVariants.p}>
                  The Notifications Panel <ExclamationCircleIcon />, at the right-bottom side of the Editor, displays
                  live Execution messages to assist during the modeling stage of your decisions.
                </Text>
              </TextContent>
            </div>
            <br />
            <div style={{ margin: "10px" }}>
              <Button onClick={() => setModalPage(ModalPage.WIZARD)}>Setup</Button>
            </div>
          </div>
        </div>
      )}
      {modalPage === ModalPage.WIZARD && (
        <div>
          <Form isHorizontal={true}>
            <FormGroup fieldId={"select-os"} label={"Operating system"}>
              <SelectOs selected={operatingSystem} onSelect={setOperatingSystem} direction={SelectDirection.down} />
            </FormGroup>
          </Form>
          <br />
          <Wizard
            steps={wizardSteps}
            height={400}
            footer={<DmnRunnerWizardFooter onClose={onClose} steps={wizardSteps} setModalPage={setModalPage} />}
          />
        </div>
      )}
      {modalPage === ModalPage.USE && (
        <div
          style={{
            width: "100%",
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
            flexDirection: "column",
          }}
        >
          <div style={{ margin: "20px" }}>
            <TextContent>
              <Text component={TextVariants.h1}>All set! 🎉</Text>
            </TextContent>
          </div>
          <div
            style={{
              display: "flex",
              justifyContent: "center",
              alignItems: "center",
              flexDirection: "column",
            }}
          >
            <TextContent style={{ margin: "10px" }}>
              <Text component={TextVariants.h3} style={{ textAlign: "center" }}>
                You're connected to the DMN Runner.
              </Text>
              <Text component={TextVariants.p} style={{ textAlign: "center" }}>
                Fill the Form on the Inputs column and automatically see the results on the Outputs column.
              </Text>
            </TextContent>
            <br />
            <Button variant="primary" type="submit" onClick={onClose} style={{ margin: "10px" }}>
              Back to Editor
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

function DmnRunnerWizardFooter(props: WizardImperativeControlProps) {
  const wizardContext = useContext(WizardContext);
  const { status } = useDmnRunner();

  useEffect(() => {
    if (status === DmnRunnerStatus.STOPPED) {
      wizardContext.goToStepByName(props.steps[1].name);
    }
  }, [status, props.setModalPage]);

  return (
    <WizardFooter>
      <WizardContextConsumer>
        {({ activeStep, goToStepByName, goToStepById, onNext, onBack }) => {
          if (activeStep.name !== "Start") {
            return (
              <>
                <Button variant="primary" type="submit" onClick={onNext}>
                  Next
                </Button>
              </>
            );
          } else {
            return (
              <>
                <Button variant="primary" type="submit" onClick={onBack}>
                  Back
                </Button>
              </>
            );
          }
        }}
      </WizardContextConsumer>
    </WizardFooter>
  );
}

function DmnRunnerPortForm() {
  const dmnRunner = useDmnRunner();

  return (
    <>
      <Text component={TextVariants.p}>
        The default DMN Runner port is <Text className={"kogito--code"}>{DMN_RUNNER_DEFAULT_PORT}</Text>. If you're
        already using this port for another application, you can change the port used to connect with the DMN Runner.
      </Text>
      <br />
      <Form isHorizontal={true}>
        <FormGroup
          fieldId={"dmn-runner-port"}
          label={"Port"}
          validated={
            dmnRunner.port === "" || parseInt(dmnRunner.port, 10) < 0 || parseInt(dmnRunner.port, 10) > 65353
              ? "error"
              : "success"
          }
          helperTextInvalid={"Invalid port. Valid ports: 0 <= port <= 65353"}
        >
          <TextInput value={dmnRunner.port} type={"number"} onChange={(value) => dmnRunner.saveNewPort(value)} />
        </FormGroup>
      </Form>
    </>
  );
}
