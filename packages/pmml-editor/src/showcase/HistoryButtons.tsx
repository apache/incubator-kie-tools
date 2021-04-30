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
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Split, SplitItem } from "@patternfly/react-core/dist/js/layouts/Split";
import { Modal, ModalVariant } from "@patternfly/react-core/dist/js/components/Modal";
import { Switch } from "@patternfly/react-core/dist/js/components/Switch";
import "./HistoryButtons.scss";

export enum Theme {
  LIGHT,
  DARK,
}

interface HistoryButtonsProps {
  undo: () => void;
  redo: () => void;
  get: () => Promise<string>;
  setTheme: (theme: Theme) => void;
  validate: () => void;
}

export const HistoryButtons = (props: HistoryButtonsProps) => {
  const [theme, setTheme] = useState<Theme>(Theme.LIGHT);

  return (
    <div className="history-buttons ignore-onclickoutside">
      <Split hasGutter={true}>
        <SplitItem>
          <Button variant="primary" onClick={props.undo} ouiaId="undo-button">
            Undo
          </Button>
        </SplitItem>
        <SplitItem>
          <Button variant="secondary" onClick={props.redo} ouiaId="redo-button">
            Redo
          </Button>
        </SplitItem>
        <SplitItem>
          <PMMLModal get={props.get} />
        </SplitItem>
        <SplitItem>
          <Button variant="secondary" onClick={props.validate} ouiaId="validate-button">
            Validate
          </Button>
        </SplitItem>
        <SplitItem className="history-buttons__theme-switch">
          <Switch
            id="theme"
            label="Dark"
            labelOff="Light"
            checked={theme === Theme.DARK}
            onChange={(checked) => {
              setTheme(checked ? Theme.DARK : Theme.LIGHT);
              props.setTheme(checked ? Theme.DARK : Theme.LIGHT);
            }}
          />
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
      <Button variant="secondary" onClick={handleModalToggle} ouiaId="pmml-button">
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
          </Button>,
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
