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
import { useState } from "react";
import {
  Button,
  ButtonVariant,
  Modal,
  ModalVariant,
  Split,
  SplitItem,
  Title,
  TitleSizes
} from "@patternfly/react-core";
import { CloseIcon } from "@patternfly/react-icons";
import { Output, OutputField } from "@kogito-tooling/pmml-editor-marshaller";
import { OutputsContainer } from "./OutputsContainer";
import { Operation, useOperation } from "../../EditorScorecard";

interface OutputsHandlerProps {
  modelIndex: number;
  output?: Output;
  validateOutputFieldName: (index: number | undefined, name: string | undefined) => boolean;
  deleteOutputField: (index: number) => void;
  commitOutputField: (index: number | undefined, outputField: OutputField) => void;
}

export const OutputsHandler = (props: OutputsHandlerProps) => {
  const { modelIndex, output, validateOutputFieldName, deleteOutputField, commitOutputField } = props;

  const [isModalOpen, setIsModalOpen] = useState(false);

  const { setActiveOperation } = useOperation();

  const toggleModal = () => {
    setActiveOperation(Operation.NONE);
    setIsModalOpen(!isModalOpen);
  };

  const header = (
    <Split hasGutter={true}>
      <SplitItem isFilled={true}>
        <Title headingLevel="h1" size={TitleSizes["2xl"]}>
          Outputs
        </Title>
      </SplitItem>
      <SplitItem>
        <Button type="button" variant={ButtonVariant.plain} onClick={toggleModal}>
          <CloseIcon />
        </Button>
      </SplitItem>
    </Split>
  );

  return (
    <>
      <Button variant="secondary" onClick={toggleModal}>
        Set Outputs
      </Button>
      <Modal
        aria-label="outputs"
        title="Outputs"
        header={header}
        isOpen={isModalOpen}
        showClose={false}
        variant={ModalVariant.large}
        onEscapePress={() => false}
      >
        <OutputsContainer
          modelIndex={modelIndex}
          output={output}
          validateOutputFieldName={validateOutputFieldName}
          deleteOutputField={deleteOutputField}
          commitOutputField={commitOutputField}
        />
      </Modal>
    </>
  );
};
