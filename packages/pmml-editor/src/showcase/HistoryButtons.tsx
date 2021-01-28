/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as React from "react";
import { useEffect, useRef, useState } from "react";
import { Button, Modal, ModalVariant, Split, SplitItem } from "@patternfly/react-core";
import "./HistoryButtons.scss";

interface HistoryButtonsProps {
  undo: () => void;
  redo: () => void;
  get: () => Promise<string>;
}

export const HistoryButtons = (props: HistoryButtonsProps) => {
  return (
    <div className="history-buttons ignore-onclickoutside">
      <Split hasGutter={true}>
        <SplitItem>
          <Button variant="primary" onClick={props.undo}>
            Undo
          </Button>
        </SplitItem>
        <SplitItem>
          <Button variant="secondary" onClick={props.redo}>
            Redo
          </Button>
        </SplitItem>
        <SplitItem>
          <PMMLModal get={props.get} />
        </SplitItem>
      </Split>
      <hr className="history-buttons__divider" />
    </div>
  );
};

const PMMLModal = (props: { get: () => Promise<string> }) => {
  const [isModalOpen, setModalOpen] = useState(false);

  const textRef = useRef<HTMLDivElement | null>(null);

  const handleModalToggle = () => {
    setModalOpen(!isModalOpen);
  };

  useEffect(() => {
    if (isModalOpen) {
      props.get().then((content: string) => {
        if (textRef.current) {
          textRef.current.innerText = content;
        }
      });
    }
  }, [isModalOpen]);

  return (
    <React.Fragment>
      <Button variant="secondary" onClick={handleModalToggle}>
        PMML
      </Button>
      <Modal
        variant={ModalVariant.large}
        title="PMML"
        isOpen={isModalOpen}
        onClose={handleModalToggle}
        actions={[
          <Button key="ok" variant="primary" onClick={handleModalToggle}>
            OK
          </Button>
        ]}
        style={{ overflowX: "scroll" }}
        appendTo={() => document.querySelector(".history-buttons") as HTMLElement}
      >
        <pre>
          <div ref={textRef} />
        </pre>
      </Modal>
    </React.Fragment>
  );
};
