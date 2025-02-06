/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
import * as React from "react";
import { useMemo, useState } from "react";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { Split, SplitItem } from "@patternfly/react-core/dist/js/layouts/Split";
import { Modal, ModalVariant } from "@patternfly/react-core/dist/js/components/Modal";
import { Title, TitleSizes } from "@patternfly/react-core/dist/js/components/Title";
import { CloseIcon } from "@patternfly/react-icons/dist/js/icons/close-icon";
import { WarningTriangleIcon } from "@patternfly/react-icons/dist/js/icons/warning-triangle-icon";
import { MiningSchema, Output, OutputField } from "@kie-tools/pmml-editor-marshaller";
import { OutputsContainer } from "./OutputsContainer";
import { Operation, useOperation } from "../../EditorScorecard";
import { useValidationRegistry } from "../../../validation";
import { Builder } from "../../../paths";
import { ValidationIndicatorTooltip } from "../../EditorCore/atoms";
import { Icon } from "@patternfly/react-core/dist/js/components/Icon";

interface OutputsHandlerProps {
  modelIndex: number;
  output?: Output;
  miningSchema?: MiningSchema;
  validateOutputFieldName: (index: number | undefined, name: string) => boolean;
  deleteOutputField: (index: number) => void;
  commitOutputField: (index: number | undefined, outputField: OutputField) => void;
}

export const OutputsHandler = (props: OutputsHandlerProps) => {
  const { modelIndex, output, miningSchema, validateOutputFieldName, deleteOutputField, commitOutputField } = props;

  const [isModalOpen, setIsModalOpen] = useState(false);

  const { setActiveOperation } = useOperation();

  const { validationRegistry } = useValidationRegistry();
  const validations = useMemo(
    () => validationRegistry.get(Builder().forModel(modelIndex).forOutput().build()),
    [modelIndex, output, miningSchema]
  );

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
        <Button type="button" variant={ButtonVariant.plain} onClick={toggleModal} data-title="OutputsModalClose">
          <CloseIcon />
        </Button>
      </SplitItem>
    </Split>
  );

  return (
    <>
      {validations.length === 0 && (
        <Button variant="secondary" onClick={toggleModal} data-title="Outputs">
          Set Outputs
        </Button>
      )}
      {validations.length > 0 && (
        <ValidationIndicatorTooltip validations={validations}>
          <Button variant="secondary" onClick={toggleModal} data-title="Outputs">
            <Icon size="sm">
              <WarningTriangleIcon color={"orange"} />
            </Icon>
            Set Outputs
          </Button>
        </ValidationIndicatorTooltip>
      )}
      <Modal
        aria-label="outputs"
        title="Outputs"
        header={header}
        isOpen={isModalOpen}
        showClose={false}
        variant={ModalVariant.large}
        onEscapePress={() => false}
        data-title="OutputsModal"
      >
        <OutputsContainer
          modelIndex={modelIndex}
          output={output}
          miningSchema={miningSchema}
          validateOutputFieldName={validateOutputFieldName}
          deleteOutputField={deleteOutputField}
          commitOutputField={commitOutputField}
        />
      </Modal>
    </>
  );
};
