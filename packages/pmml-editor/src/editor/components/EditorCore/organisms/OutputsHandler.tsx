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
import { DataType, FieldName, OpType, Output, RankOrder, ResultFeature } from "@kogito-tooling/pmml-editor-marshaller";
import { OutputsContainer } from "./OutputsContainer";
import { Operation } from "../../EditorScorecard";

interface OutputsHandlerProps {
  modelIndex: number;
  activeOperation: Operation;
  setActiveOperation: (operation: Operation) => void;
  output?: Output;
  validateOutputFieldName: (index: number | undefined, name: string | undefined) => boolean;
  deleteOutputField: (index: number) => void;
  commit: (
    index: number | undefined,
    name: FieldName | undefined,
    dataType: DataType | undefined,
    optype: OpType | undefined,
    targetField: FieldName | undefined,
    feature: ResultFeature | undefined,
    value: any | undefined,
    rank: number | undefined,
    rankOrder: RankOrder | undefined,
    segmentId: string | undefined,
    isFinalResult: boolean | undefined
  ) => void;
}

export const OutputsHandler = (props: OutputsHandlerProps) => {
  const {
    modelIndex,
    activeOperation,
    setActiveOperation,
    output,
    validateOutputFieldName,
    deleteOutputField,
    commit
  } = props;

  const [isModalOpen, setIsModalOpen] = useState(false);

  const toggleModal = () => {
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
        <Button
          type="button"
          variant={ButtonVariant.plain}
          isDisabled={activeOperation !== Operation.NONE}
          onClick={toggleModal}
        >
          <CloseIcon />
        </Button>
      </SplitItem>
    </Split>
  );

  return (
    <>
      <Button variant="secondary" isDisabled={activeOperation !== Operation.NONE} onClick={toggleModal}>
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
          activeOperation={activeOperation}
          setActiveOperation={setActiveOperation}
          output={output}
          validateOutputFieldName={validateOutputFieldName}
          deleteOutputField={deleteOutputField}
          commit={commit}
        />
      </Modal>
    </>
  );
};
