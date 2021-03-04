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
import { useContext, useEffect, useMemo, useState } from "react";
import {
  Alert,
  Form,
  FormGroup,
  Modal,
  ModalVariant,
  SelectDirection,
  TextContent,
  TextVariants,
  Text,
  Wizard,
  WizardFooter,
  WizardContext,
  WizardContextConsumer,
  AlertVariant,
  List,
  ListItem,
  Button
} from "@patternfly/react-core";
import { getOperatingSystem, OperatingSystem } from "../common/utils";
import { SelectOs } from "../common/SelectOs";
import { AnimatedTripleDotLabel } from "../common/AnimatedTripleDotLabel";

interface Props {
  isOpen: boolean;
  onClose: () => void;
  isDmnRunning: boolean;
  stopped: boolean;
}

// const DMN_RUNNER_LINK = "https://kiegroup.github.io/kogito-online-ci/temp/runner.zip";
const DMN_RUNNER_LINK = `files/dmn-runner.zip`;

export function DmnRunnerModal(props: Props) {
  const [operationalSystem, setOperationalSystem] = useState(getOperatingSystem() ?? OperatingSystem.LINUX);

  const downloadDmnRunner = useMemo(() => {
    switch (operationalSystem) {
      case OperatingSystem.MACOS:
        return DMN_RUNNER_LINK;
      case OperatingSystem.WINDOWS:
        return DMN_RUNNER_LINK;
      case OperatingSystem.LINUX:
      default:
        return DMN_RUNNER_LINK;
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
            <ListItem>
              <TextContent>
                <Text component={TextVariants.p}>Open the folder containing the dmn-runner.zip file and unzip it.</Text>
              </TextContent>
            </ListItem>
          </List>
        )
      },
      {
        name: "Start",
        component: (
          <>
            {props.stopped && (
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
                    Open the dmn-runner folder on a terminal and execute the following command to start the DMN Runner:
                  </Text>
                  <Text component={TextVariants.p} className={"kogito--code"}>
                    ./init.sh
                  </Text>
                </TextContent>
              </ListItem>
              <br />
              <ListItem>
                <TextContent>
                  <Text component={TextVariants.p}>
                    To stop the DMN Runner you can press{" "}
                    <Text component={TextVariants.p} className={"kogito--code"}>
                      CTRL + C
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
                  <Text component={TextVariants.p}>
                    Fill the Form on the Inputs column and see the results on the Outputs column.
                  </Text>
                </TextContent>
              </ListItem>
            </List>
            <br />
            <img src="images/dmn-runner.gif" alt="DMN Runner running" width={"450px"} />
          </div>
        )
      }
    ],
    [props.stopped]
  );

  return (
    <Modal
      isOpen={props.isOpen}
      onClose={props.onClose}
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
          {props.isDmnRunning ? (
            <>
              <div className={"kogito--editor__dmn-runner-modal-footer"}>
                <Alert
                  variant={"success"}
                  isInline={true}
                  className={"kogito--editor__dmn-runner-modal-footer-alert"}
                  title={
                    <div className={"kogito--editor__dmn-runner-modal-footer-alert-success"}>
                      <p>Connected to DMN Runner</p>
                      <a
                        key="back-to-editor"
                        onClick={() => {
                          props.onClose();
                        }}
                      >
                        Back to Editor
                      </a>
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
        footer={<DmnRunnerWizardFooter shouldGoToStep={props.isDmnRunning} hasStopped={props.stopped} steps={steps} />}
      />
    </Modal>
  );
}

interface WizardImperativeControlProps {
  shouldGoToStep: boolean;
  hasStopped: boolean;
  steps: Array<{ component: JSX.Element; name: string }>;
}

function DmnRunnerWizardFooter(props: WizardImperativeControlProps) {
  const wizardContext = useContext(WizardContext);

  useEffect(() => {
    if (props.hasStopped) {
      wizardContext.goToStepByName(props.steps[1].name);
    } else if (props.shouldGoToStep) {
      wizardContext.goToStepByName(props.steps[props.steps.length - 1].name);
    }
  }, [props]);

  return (
    <WizardFooter>
      <WizardContextConsumer>{() => <></>}</WizardContextConsumer>
    </WizardFooter>
  );
}
