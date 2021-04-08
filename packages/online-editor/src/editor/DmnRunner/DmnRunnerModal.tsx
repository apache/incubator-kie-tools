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
  Alert,
  AlertVariant,
  Button,
  Chip,
  Form,
  FormGroup,
  List,
  ListItem,
  Modal,
  ModalVariant,
  SelectDirection,
  Text,
  TextContent,
  TextVariants,
  Wizard,
  WizardContext,
  WizardContextConsumer,
  WizardFooter
} from "@patternfly/react-core";
import { getOperatingSystem, OperatingSystem } from "../../common/utils";
import { SelectOs } from "../../common/SelectOs";
import { AnimatedTripleDotLabel } from "../../common/AnimatedTripleDotLabel";
import { DmnRunnerStatus } from "./DmnRunnerStatus";
import { useDmnRunner } from "./DmnRunnerContext";
import { DMN_RUNNER_DEFAULT_PORT } from "./DmnRunnerContextProvider";
import { ExclamationCircleIcon } from "@patternfly/react-icons";

enum ModalPage {
  INITIAL,
  WIZARD,
  USE
}

export function DmnRunnerModal() {
  const [operationalSystem, setOperationalSystem] = useState(getOperatingSystem() ?? OperatingSystem.LINUX);
  const dmnRunner = useDmnRunner();

  const downloadDmnRunner = useMemo(() => {
    switch (operationalSystem) {
      case OperatingSystem.MACOS:
        return "$_{WEBPACK_REPLACE__dmnRunnerMacOsUrl}";
      case OperatingSystem.WINDOWS:
        return "$_{WEBPACK_REPLACE__dmnRunnerWindowsUrl}";
      case OperatingSystem.LINUX:
      default:
        return "$_{WEBPACK_REPLACE__dmnRunnerLinuxUrl}";
    }
  }, [operationalSystem]);

  const steps = useMemo(
    () => [
      {
        name: "Install",
        component: (
          <List>
            {dmnRunner.status === DmnRunnerStatus.OUTDATED && (
              <div>
                <Alert variant={AlertVariant.warning} isInline={true} title={"DMN Runner is outdated!"}>
                  It looks like you're using a outdated version of the DMN Runner, please follow the instructions again
                  to have the latest version.
                </Alert>
                <br />
              </div>
            )}
            <ListItem>
              <TextContent>
                <Text component={TextVariants.p}>
                  Download the DMN Runner{" "}
                  <Text component={TextVariants.a} href={downloadDmnRunner}>
                    here
                  </Text>
                  .
                </Text>
              </TextContent>
            </ListItem>
            <br />
            <ListItem>
              <TextContent>
                <Text component={TextVariants.p}>
                  Open your Downloads folder and unzip the <Chip isReadOnly={true}>dmn-runner.zip</Chip> file.
                  {/*The default DMN Runner port is 8080. If you are already using this port you can change it.*/}
                  {/*<Form isHorizontal={true}>*/}
                  {/*  <FormGroup*/}
                  {/*    fieldId={"dmn-runner-port"}*/}
                  {/*    label={"Port"}*/}
                  {/*    validated={*/}
                  {/*      parseInt(dmnRunner.port, 10) < 0 || parseInt(dmnRunner.port, 10) > 65353 ? "error" : "success"*/}
                  {/*    }*/}
                  {/*    helperTextInvalid={"Invalid port. Valid ports: 0 <= port <= 65353"}*/}
                  {/*  >*/}
                  {/*    <TextInput*/}
                  {/*      value={dmnRunner.port}*/}
                  {/*      type={"number"}*/}
                  {/*      onChange={value => dmnRunner.saveNewPort(value)}*/}
                  {/*    />*/}
                  {/*  </FormGroup>*/}
                  {/*</Form>*/}
                </Text>
              </TextContent>
            </ListItem>
          </List>
        )
      },
      {
        name: "Start",
        component: (
          <>
            {dmnRunner.status === DmnRunnerStatus.STOPPED && (
              <div>
                <Alert variant={AlertVariant.warning} isInline={true} title={"DMN Runner has stopped!"}>
                  It looks like the DMN Runner has suddenly stopped, please follow these instructions to get it up start
                  it again.
                </Alert>
                <br />
              </div>
            )}
            <List>
              <ListItem>
                <TextContent>
                  <Text component={TextVariants.p}>
                    You will need Maven 3.6.3 and Java 11 installed to be able to run the DMN Runner.
                  </Text>
                </TextContent>
              </ListItem>
              <br />
              <ListItem>
                <TextContent>
                  <Text component={TextVariants.p}>
                    Open the <Chip isReadOnly={true}>dmn-runner</Chip> folder on a terminal and execute the following
                    command:
                  </Text>
                  {dmnRunner.port === DMN_RUNNER_DEFAULT_PORT ? (
                    <Text component={TextVariants.p} className={"kogito--code"}>
                      java -jar jitexecutor-runner-2.0.0-SNAPSHOT-runner.jar
                    </Text>
                  ) : (
                    <Text component={TextVariants.p} className={"kogito--code"}>
                      ./init.sh --port={dmnRunner.port}
                    </Text>
                  )}
                </TextContent>
              </ListItem>
              <br />
              <ListItem>
                <TextContent>
                  <Text component={TextVariants.p}>
                    To stop the DMN Runner you can press{" "}
                    <Text component={TextVariants.p} className={"kogito--code"}>
                      CTRL+C
                    </Text>{" "}
                    on the terminal.
                  </Text>
                </TextContent>
              </ListItem>
            </List>
          </>
        )
      }
    ],
    [dmnRunner.status, dmnRunner.port, dmnRunner.saveNewPort]
  );

  const [modalPage, setModalPage] = useState<ModalPage>(ModalPage.INITIAL);
  useEffect(() => {
    if (dmnRunner.status === DmnRunnerStatus.NOT_RUNNING) {
      setModalPage(ModalPage.INITIAL);
    } else if (dmnRunner.status === DmnRunnerStatus.STOPPED) {
      setModalPage(ModalPage.WIZARD);
    } else if (dmnRunner.status === DmnRunnerStatus.RUNNING) {
      setModalPage(ModalPage.USE);
    }
  }, [dmnRunner.status]);

  const onClose = useCallback(() => {
    dmnRunner.setModalOpen(false);
    if (dmnRunner.status === DmnRunnerStatus.STOPPED || dmnRunner.status === DmnRunnerStatus.OUTDATED) {
      dmnRunner.setStatus(DmnRunnerStatus.NOT_RUNNING);
    }
  }, [dmnRunner.status]);

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
            marginLeft: "20px"
          }}
        >
          <div>
            <TextContent>
              <Text component={TextVariants.h1}>DMN Runner</Text>
            </TextContent>
          </div>
          <div
            style={{
              display: "flex",
              justifyContent: "center",
              alignItems: "center",
              flexDirection: "column"
            }}
          >
            <div style={{ margin: "10px" }}>
              <TextContent>
                <Text component={TextVariants.p}>
                  The DMN Runner will enable you to run your DMN models and see live results.
                </Text>
              </TextContent>
            </div>
            <div>
              <img src={"./images/dmn-runner2.gif"} alt={"DMN Runner usage"} width={700} />
            </div>
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
              <SelectOs selected={operationalSystem} onSelect={setOperationalSystem} direction={SelectDirection.down} />
            </FormGroup>
          </Form>
          <br />
          <Wizard
            steps={steps}
            height={400}
            footer={<DmnRunnerWizardFooter onClose={onClose} steps={steps} setModalPage={setModalPage} />}
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
            flexDirection: "column"
          }}
        >
          <div style={{ margin: "20px" }}>
            <TextContent>
              <Text component={TextVariants.h1}>All set! You're connected to the DMN Runner! 🎉</Text>
            </TextContent>
          </div>
          <div
            style={{
              display: "flex",
              justifyContent: "center",
              alignItems: "center",
              flexDirection: "column"
            }}
          >
            <TextContent style={{ margin: "10px" }}>
              <Text component={TextVariants.h3} style={{ textAlign: "center" }}>
                Now, you can start using the DMN Runner to run your models.
              </Text>
              <Text component={TextVariants.p} style={{ textAlign: "center" }}>
                Fill the Form on the Inputs column and automatically see the results on the Outputs column.
                <br />
                You can also use the Notifications Panel <ExclamationCircleIcon /> to check for any Validation or
                Execution error.
              </Text>
            </TextContent>
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
