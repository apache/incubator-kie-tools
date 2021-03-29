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
      },
      {
        name: "Use",
        component: (
          <div>
            <List>
              <ListItem>
                <TextContent>
                  <Text component={TextVariants.p}>All set! 🎉</Text>
                </TextContent>
              </ListItem>
            </List>
            <br />
            <List>
              <ListItem>
                <TextContent>
                  <Text component={TextVariants.p}>
                    Fill the Form on the Inputs column and see the results on the Outputs column.
                  </Text>
                </TextContent>
              </ListItem>
            </List>
          </div>
        )
      }
    ],
    [dmnRunner.status, dmnRunner.port, dmnRunner.saveNewPort]
  );

  const onClose = useCallback(() => {
    dmnRunner.setModalOpen(false);
    if (dmnRunner.status === DmnRunnerStatus.STOPPED) {
      dmnRunner.setStatus(DmnRunnerStatus.NOT_RUNNING);
    }
  }, [dmnRunner.status]);

  return (
    <Modal
      isOpen={dmnRunner.isModalOpen}
      onClose={onClose}
      variant={ModalVariant.large}
      aria-label={"Steps to enable the DMN Runner"}
      title={"DMN Runner setup"}
      description={
        <>
          {"Choose your Operating System and follow the instructions to install and start the DMN Runner."}
          <br />
          {" This will enable you to run your DMN models and see live results."}
        </>
      }
      footer={
        <>
          {dmnRunner.status === DmnRunnerStatus.RUNNING ? (
            <>
              <div className={"kogito--editor__dmn-runner-modal-footer"}>
                <Alert
                  variant={"success"}
                  isInline={true}
                  className={"kogito--editor__dmn-runner-modal-footer-alert"}
                  title={
                    <div className={"kogito--editor__dmn-runner-modal-footer-alert-success"}>
                      <span>Connected to DMN Runner</span>
                    </div>
                  }
                />
              </div>
            </>
          ) : (
            <Alert
              variant={"default"}
              isInline={true}
              className={"kogito--editor__dmn-runner-modal-footer-alert"}
              title={<AnimatedTripleDotLabel label={"Waiting to connect to DMN Runner"} interval={750} />}
            />
          )}
        </>
      }
    >
      <Form isHorizontal={true}>
        <FormGroup fieldId={"select-os"} label={"Operating system"}>
          <SelectOs selected={operationalSystem} onSelect={setOperationalSystem} direction={SelectDirection.down} />
        </FormGroup>
      </Form>
      <br />
      <Wizard
        steps={steps}
        height={400}
        footer={
          <DmnRunnerWizardFooter
            onClose={onClose}
            shouldGoToStep={dmnRunner.status === DmnRunnerStatus.RUNNING}
            hasStopped={dmnRunner.status === DmnRunnerStatus.STOPPED}
            steps={steps}
          />
        }
      />
    </Modal>
  );
}

interface WizardImperativeControlProps {
  onClose: () => void;
  shouldGoToStep: boolean;
  hasStopped: boolean;
  steps: Array<{ component: JSX.Element; name: string }>;
}

function DmnRunnerWizardFooter(props: WizardImperativeControlProps) {
  const wizardContext = useContext(WizardContext);
  const { status } = useDmnRunner();

  useEffect(() => {
    if (props.hasStopped) {
      wizardContext.goToStepByName(props.steps[1].name);
    } else if (props.shouldGoToStep) {
      wizardContext.goToStepByName(props.steps[props.steps.length - 1].name);
    }
  }, [props.shouldGoToStep, props.hasStopped]);

  return (
    <WizardFooter>
      <WizardContextConsumer>
        {({ activeStep, goToStepByName, goToStepById, onNext, onBack }) => {
          if (activeStep.name !== "Use") {
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
                <Button
                  isDisabled={status !== DmnRunnerStatus.RUNNING}
                  variant="primary"
                  type="submit"
                  onClick={props.onClose}
                >
                  Back to Editor
                </Button>
              </>
            );
          }
        }}
      </WizardContextConsumer>
    </WizardFooter>
  );
}
