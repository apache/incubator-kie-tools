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
import { Button, ExpandableSection, Modal, ModalVariant, SelectDirection, Spinner } from "@patternfly/react-core";
import { useOnlineI18n } from "../common/i18n";
import { OperatingSystem } from "../common/utils";
import { SelectOs, SelectOsRef } from "../common/SelectOs";
import { CheckIcon } from "@patternfly/react-icons";

//
// Install and start DMN Runner                                   [Close icon]
//
// (select) [macOS \/]
//
// (collapsable) Installing DMN Runner on macOS - arcordion
// (collapsable) Starting DMN Runner on macOS - arcordoin
//
// Always present either:
//   “Waiting for DMN Runner to start [loading spinner]”; or
//   “Connected to DMN Runner [green-check]”. Primary button -> Back to the Editor.
//
//

interface Props {
  isOpen: boolean;
  onClose: () => void;
  isDmnRunning: boolean;
}

const DMN_RUNNER_LINK = "https://kiegroup.github.io/kogito-online-ci/temp/runner.zip";

export function DmnRunnerModal(props: Props) {
  const { i18n } = useOnlineI18n();
  const selectRef = useRef<SelectOsRef>(null);
  const [installDmnRunnerSection, setInstallDmnRunnerSection] = useState(true);
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

  return (
    <Modal
      isOpen={props.isOpen}
      onClose={props.onClose}
      variant={ModalVariant.medium}
      aria-label={"Steps to enable the DMN Runner"}
      title={"DMN Runner is not running"}
      description={"Install and start DMN Runner "}
      footer={
        <div>
          <div>
            {props.isDmnRunning ? (
              <div style={{ display: "flex" }}>
                <p>DMN Runner connected</p>
                <CheckIcon style={{ marginLeft: "10px" }} size={"md"} />
              </div>
            ) : (
              <div style={{ display: "flex" }}>
                <p>Waiting to connect to DMN Runner server</p>
                <Spinner style={{ marginLeft: "10px" }} size="md" />
              </div>
            )}
          </div>
          <br />
          <div>
            <a key="download" href={downloadDmnRunner} download={true}>
              <Button variant="primary">{i18n.terms.download}</Button>
            </a>
            <Button key="cancel" variant="link" onClick={props.onClose}>
              {i18n.terms.close}
            </Button>
          </div>
        </div>
      }
    >
      <p>Select your operating system: </p>
      <SelectOs ref={selectRef} direction={SelectDirection.down} />
      <br />
      <ExpandableSection
        toggleText={"Install DMN Runner"}
        isExpanded={installDmnRunnerSection}
        onToggle={setInstallDmnRunnerSection}
      >
        <p style={{ display: "inline" }}>You can download the zip containing the server </p>
        <a style={{ display: "inline" }} href={"https://kiegroup.github.io/kogito-online-ci/temp/runner.zip"}>
          here
        </a>
        <p>Unzip it ... </p>
      </ExpandableSection>
      <br />
      <ExpandableSection
        toggleText={"Starting DMN Runner"}
        isExpanded={startDmnRunnerSection}
        onToggle={setStartDmnRunnerSection}
      >
        <p style={{ display: "inline" }}>To start the server you need execute the</p>
        <p style={{ display: "inline" }} className={"kogito-code"}>
          ./install.sh
        </p>
        <p style={{ display: "inline" }}> script.</p>
      </ExpandableSection>
    </Modal>
  );
}
