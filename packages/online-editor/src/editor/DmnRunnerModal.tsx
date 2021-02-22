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
import { useContext, useEffect, useMemo, useRef } from "react";
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
  AlertVariant
} from "@patternfly/react-core";
import { OperatingSystem } from "../common/utils";
import { SelectOs, SelectOsRef } from "../common/SelectOs";
import { AnimatedTripleDotLabel } from "../common/AnimatedTripleDotLabel";

interface Props {
  isOpen: boolean;
  onClose: () => void;
  isDmnRunning: boolean;
  stopped: boolean;
  setRunDmn: React.Dispatch<boolean>;
}

// const filePath = `samples/${fileName}.${fileExtension}`;
// props.onFileOpened({
//   isReadOnly: false,
//   fileExtension: fileExtension,
//   fileName: fileName,
//   getFileContents: () => fetch(filePath).then(response => response.text())
// });

// const DMN_RUNNER_LINK = "https://kiegroup.github.io/kogito-online-ci/temp/runner.zip";
const DMN_RUNNER_LINK = `samples/dmn-runner.zip`;

export function DmnRunnerModal(props: Props) {
  const selectRef = useRef<SelectOsRef>(null);

  const downloadDmnRunner = useMemo(() => {
    switch (selectRef.current?.getOperationalSystem()) {
      case OperatingSystem.MACOS:
        return DMN_RUNNER_LINK;
      case OperatingSystem.WINDOWS:
        return DMN_RUNNER_LINK;
      case OperatingSystem.LINUX:
      default:
        return DMN_RUNNER_LINK;
    }
  }, [selectRef]);

  const steps = useMemo(
    () => [
      {
        name: "Install",
        component: (
          <TextContent>
            <Text component={TextVariants.p}>
              Download the DMN Runner{" "}
              <Text component={TextVariants.a} href={downloadDmnRunner}>
                here
              </Text>
            </Text>
            <Text component={TextVariants.p}>Open its folder and unzip it.</Text>
          </TextContent>
        )
      },
      {
        name: "Start",
        component: (
          <>
            {props.stopped && (
              <Alert variant={AlertVariant.warning} isInline={true} title={"DMN Runner has stopped!"}>
                It looks like the DMN Runner has suddenly stopped, please follow these instructions to get it up start
                it again.
              </Alert>
            )}
            <br />
            <TextContent>
              <Text component={TextVariants.p}>
                Open a terminal on the DMN Runner folder and execute the folliwing command:
                <Text component={TextVariants.p} className={"kogito-code"}>
                  java -jar dmn-runner.jar
                </Text>
              </Text>
            </TextContent>
          </>
        )
      },
      {
        name: "Use",
        component: (
          <TextContent>
            <Text component={TextVariants.p}>Fill the form and see what happens :-)</Text>
          </TextContent>
        )
      }
    ],
    [props.stopped]
  );

  return (
    <Modal
      isOpen={props.isOpen}
      onClose={() => {
        props.onClose();
        if (!props.isDmnRunning) {
          props.setRunDmn(false);
        }
      }}
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
          <>
            {props.isDmnRunning ? (
              <>
                <div style={{ display: "flex", flexDirection: "column", width: "100%" }}>
                  <Alert
                    variant={"success"}
                    isInline={true}
                    style={{ width: "100%" }}
                    title={
                      <div style={{ display: "flex", justifyContent: "space-between" }}>
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
                style={{ width: "100%" }}
                title={<AnimatedTripleDotLabel label={"Waiting to connect to DMN Runner"} interval={750} />}
              />
            )}
          </>
        </>
      }
    >
      <Form isHorizontal={true}>
        <FormGroup fieldId={"select-os"} label={"Operating system"}>
          <SelectOs ref={selectRef} direction={SelectDirection.down} />
        </FormGroup>
      </Form>
      <br />
      <Wizard
        steps={steps}
        height={400}
        startAtStep={props.isDmnRunning ? 2 : 1}
        footer={
          <WizardFooter>
            <DmnRunnerWizardFooter shouldGoToStep={props.isDmnRunning} stepName={steps[steps.length - 1].name} />
          </WizardFooter>
        }
      />
    </Modal>
  );
}

interface WizardImperativeControlProps {
  shouldGoToStep: boolean;
  stepName: string;
}

function DmnRunnerWizardFooter(props: WizardImperativeControlProps) {
  const wizardContext = useContext(WizardContext);

  useEffect(() => {
    if (props.shouldGoToStep) {
      wizardContext.goToStepByName(props.stepName);
    }
  }, [props]);

  return <WizardContextConsumer>{() => <></>}</WizardContextConsumer>;
}
