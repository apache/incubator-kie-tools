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
import { useMemo, useRef, useState } from "react";
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
  WizardContextConsumer
} from "@patternfly/react-core";
import { useOnlineI18n } from "../common/i18n";
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

const DMN_RUNNER_LINK = "https://kiegroup.github.io/kogito-online-ci/temp/runner.zip";

export function DmnRunnerModal(props: Props) {
  const { i18n } = useOnlineI18n();
  const selectRef = useRef<SelectOsRef>(null);
  const [installDmnRunnerSection, setInstallDmnRunnerSection] = useState(!props.stopped);
  const [startDmnRunnerSection, setStartDmnRunnerSection] = useState(true);

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
              You can download the zip containing the server{" "}
              <Text component={TextVariants.a} href={downloadDmnRunner}>
                here
              </Text>
            </Text>
          </TextContent>
        )
      },
      {
        name: "Start",
        component: (
          <TextContent>
            <Text component={TextVariants.p}>
              You can download the zip containing the server{" "}
              <Text component={TextVariants.p} className={"kogito-code"}>
                here
              </Text>
            </Text>
          </TextContent>
        )
      },
      {
        name: "Use",
        component: <></>
      }
    ],
    []
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
        startAtStep={props.isDmnRunning ? steps.length : 1}
        footer={
          <WizardFooter>
            <WizardContextConsumer>
              {({ activeStep, goToStepByName, goToStepById, onNext, onBack, onClose }) => {
                return (
                  <>
                    {/*<Button*/}
                    {/*  variant="primary"*/}
                    {/*  type="submit"*/}
                    {/*  onClick={onNext}*/}
                    {/*  className={activeStep.name === steps[steps.length - 1].name ? "pf-m-disabled" : ""}*/}
                    {/*>*/}
                    {/*  Next*/}
                    {/*</Button>*/}
                    {/*<Button*/}
                    {/*  variant="secondary"*/}
                    {/*  onClick={onBack}*/}
                    {/*  className={activeStep.name === steps[0].name ? "pf-m-disabled" : ""}*/}
                    {/*>*/}
                    {/*  Back*/}
                    {/*</Button>*/}
                  </>
                );
              }}
            </WizardContextConsumer>
          </WizardFooter>
        }
      />

      {/*<Card isFlat={true}>*/}
      {/*  <CardBody>*/}
      {/*    <ExpandableSection*/}
      {/*      toggleText={"Install DMN Runner"}*/}
      {/*      isExpanded={installDmnRunnerSection}*/}
      {/*      onToggle={setInstallDmnRunnerSection}*/}
      {/*    >*/}
      {/*      <TextContent>*/}
      {/*        <Text component={TextVariants.p}>*/}
      {/*          You can download the zip containing the server{" "}*/}
      {/*          <Text component={TextVariants.a} href={downloadDmnRunner}>*/}
      {/*            here*/}
      {/*          </Text>*/}
      {/*        </Text>*/}
      {/*      </TextContent>*/}
      {/*    </ExpandableSection>*/}
      {/*    <br />*/}
      {/*    <ExpandableSection*/}
      {/*      toggleText={"Starting DMN Runner"}*/}
      {/*      isExpanded={startDmnRunnerSection}*/}
      {/*      onToggle={setStartDmnRunnerSection}*/}
      {/*    >*/}
      {/*      <p style={{ display: "inline" }}>To start the server you need execute the</p>*/}
      {/*      <p style={{ display: "inline" }} className={"kogito-code"}>*/}
      {/*        ./install.sh*/}
      {/*      </p>*/}
      {/*      <p style={{ display: "inline" }}> script.</p>*/}
      {/*    </ExpandableSection>*/}
      {/*  </CardBody>*/}
      {/*</Card>*/}
    </Modal>
  );
}
