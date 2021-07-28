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
import { useMemo, useState } from "react";
import { isEqual } from "lodash";
import { CSSTransition, SwitchTransition } from "react-transition-group";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { TextContent } from "@patternfly/react-core/dist/js/components/Text";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { Stack, StackItem } from "@patternfly/react-core/dist/js/layouts/Stack";
import { Alert } from "@patternfly/react-core/dist/js/components/Alert";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { ArrowAltCircleLeftIcon } from "@patternfly/react-icons/dist/js/icons/arrow-alt-circle-left-icon";
import { BoltIcon } from "@patternfly/react-icons/dist/js/icons/bolt-icon";
import { PlusIcon } from "@patternfly/react-icons/dist/js/icons/plus-icon";
import { FieldName, MiningSchema, Output, OutputField } from "@kogito-tooling/pmml-editor-marshaller";
import { Actions } from "../../../reducers";
import OutputFieldsTable from "./OutputFieldsTable";
import OutputsBatchAdd from "./OutputsBatchAdd";
import { Operation, useOperation } from "../../EditorScorecard";
import { OutputFieldExtendedProperties } from "./OutputFieldExtendedProperties";
import "./OutputsContainer.scss";
import { findIncrementalName } from "../../../PMMLModelHelper";
import { useBatchDispatch, useHistoryService } from "../../../history";
import { useValidationRegistry } from "../../../validation";
import { Builder } from "../../../paths";
import get = Reflect.get;
import set = Reflect.set;

interface OutputsContainerProps {
  modelIndex: number;
  output?: Output;
  miningSchema?: MiningSchema;
  validateOutputFieldName: (index: number | undefined, name: FieldName) => boolean;
  deleteOutputField: (index: number) => void;
  commitOutputField: (index: number | undefined, outputField: OutputField) => void;
}

type OutputsViewSection = "overview" | "extended-properties" | "batch-add";

export const OutputsContainer = (props: OutputsContainerProps) => {
  const { modelIndex, output, miningSchema, validateOutputFieldName, deleteOutputField, commitOutputField } = props;

  const [selectedOutputIndex, setSelectedOutputIndex] = useState<number | undefined>(undefined);
  const [viewSection, setViewSection] = useState<OutputsViewSection>("overview");

  const { activeOperation, setActiveOperation } = useOperation();
  const { service, getCurrentState } = useHistoryService();
  const dispatch = useBatchDispatch(service, getCurrentState);

  const targetFields = useMemo(() => getMiningSchemaTargetFields(miningSchema), [miningSchema]);

  const editItem: OutputField | undefined = useMemo(() => {
    if (selectedOutputIndex === undefined) {
      return undefined;
    }
    const outputs = output?.OutputField;
    if (outputs === undefined) {
      return undefined;
    }
    return outputs[selectedOutputIndex];
  }, [output, selectedOutputIndex]);

  const getTransition = (_viewSection: OutputsViewSection) => {
    let cssClass;
    switch (_viewSection) {
      case "overview":
        cssClass = "outputs-container__overview";
        break;
      case "extended-properties":
        cssClass = "outputs-container__extended-properties";
        break;
      case "batch-add":
        cssClass = "outputs-container__batch-add";
        break;
    }
    return cssClass;
  };

  const addOutputField = () => {
    const numberOfOutputFields = output?.OutputField.length;
    if (numberOfOutputFields !== undefined) {
      const existingNames: string[] = output?.OutputField.map((of) => of.name.toString()) ?? [];
      const newOutputFieldName: FieldName = findIncrementalName("New output", existingNames, 1) as FieldName;
      const newOutputField: OutputField = {
        name: newOutputFieldName,
        dataType: "string",
        optype: undefined,
        targetField: undefined,
        feature: undefined,
        value: undefined,
        rank: undefined,
        rankOrder: undefined,
        segmentId: undefined,
        isFinalResult: undefined,
      };

      setSelectedOutputIndex(numberOfOutputFields);
      setActiveOperation(Operation.UPDATE_OUTPUT);

      commitOutputField(undefined, newOutputField);
    }
  };

  const addBatchOutputs = (outputs: string) => {
    const outputsNames = outputs.split("\n").filter((item) => item.trim().length > 0);
    dispatch({
      type: Actions.AddBatchOutputs,
      payload: {
        modelIndex: modelIndex,
        outputFields: outputsNames,
      },
    });
    setViewSection("overview");
  };

  const onCommitAndClose = () => {
    onCommit({});
    onCancel();
  };

  const onCommit = (partial: Partial<OutputField>) => {
    if (output !== undefined && selectedOutputIndex !== undefined) {
      const outputField = output.OutputField[selectedOutputIndex];
      const existingPartial: Partial<OutputField> = {};
      Object.keys(partial).forEach((key) => set(existingPartial, key, get(outputField, key)));

      if (!isEqual(partial, existingPartial)) {
        commitOutputField(selectedOutputIndex, { ...outputField, ...partial });
      }
    }
  };

  const onCancel = () => {
    setSelectedOutputIndex(undefined);
    setActiveOperation(Operation.NONE);
  };

  const { validationRegistry } = useValidationRegistry();
  const validations = useMemo(
    () => validationRegistry.get(Builder().forModel(modelIndex).forOutput().build()),
    [modelIndex, output?.OutputField]
  );

  return (
    <div className="outputs-container">
      <SwitchTransition mode={"out-in"}>
        <CSSTransition
          timeout={{
            enter: 230,
            exit: 100,
          }}
          classNames={getTransition(viewSection)}
          key={viewSection}
        >
          <>
            {viewSection === "overview" && (
              <Stack hasGutter={true} className="outputs-container__overview">
                <StackItem>
                  <Flex data-ouia-component-id="outputs-toolbar">
                    <FlexItem>
                      <Button
                        variant="primary"
                        onClick={addOutputField}
                        isDisabled={activeOperation !== Operation.NONE}
                        icon={<PlusIcon />}
                        iconPosition="left"
                        ouiaId="add-output"
                      >
                        Add Output
                      </Button>
                    </FlexItem>
                    <FlexItem>
                      <Button
                        variant="secondary"
                        onClick={() => setViewSection("batch-add")}
                        isDisabled={activeOperation !== Operation.NONE}
                        icon={<BoltIcon />}
                        iconPosition="left"
                      >
                        Add Multiple Outputs
                      </Button>
                    </FlexItem>
                  </Flex>
                </StackItem>
                {validations && validations.length > 0 && (
                  <StackItem>
                    <Alert
                      variant="warning"
                      isInline={true}
                      title={
                        output?.OutputField.length
                          ? "Some items are invalid and need attention."
                          : "At least one Output Field is required."
                      }
                    />
                  </StackItem>
                )}
                <StackItem className="outputs-container__fields-list" data-ouia-component-id="outputs-overview">
                  <OutputFieldsTable
                    modelIndex={modelIndex}
                    outputs={output?.OutputField as OutputField[]}
                    selectedOutputIndex={selectedOutputIndex}
                    setSelectedOutputIndex={setSelectedOutputIndex}
                    validateOutputFieldName={validateOutputFieldName}
                    viewExtendedProperties={() => setViewSection("extended-properties")}
                    onAddOutputField={addOutputField}
                    onDeleteOutputField={deleteOutputField}
                    onCommitAndClose={onCommitAndClose}
                    onCommit={onCommit}
                    onCancel={onCancel}
                  />
                </StackItem>
              </Stack>
            )}
            {viewSection === "extended-properties" && (
              <Stack hasGutter={true}>
                <StackItem>
                  <TextContent>
                    <Title size="lg" headingLevel="h1">
                      <a onClick={() => setViewSection("overview")}>{editItem?.name}</a>&nbsp;/&nbsp;Properties
                    </Title>
                  </TextContent>
                </StackItem>
                <StackItem className="outputs-container__extended-properties">
                  <OutputFieldExtendedProperties
                    modelIndex={modelIndex}
                    activeOutputFieldIndex={selectedOutputIndex}
                    activeOutputField={editItem}
                    targetFields={targetFields}
                    commit={onCommit}
                  />
                </StackItem>
                <StackItem>
                  <Button
                    variant="primary"
                    onClick={() => setViewSection("overview")}
                    icon={<ArrowAltCircleLeftIcon />}
                    iconPosition="left"
                  >
                    Back
                  </Button>
                </StackItem>
              </Stack>
            )}
            {viewSection === "batch-add" && (
              <OutputsBatchAdd onAdd={addBatchOutputs} onCancel={() => setViewSection("overview")} />
            )}
          </>
        </CSSTransition>
      </SwitchTransition>
    </div>
  );
};

const getMiningSchemaTargetFields = (miningSchema?: MiningSchema) => {
  return miningSchema?.MiningField.filter((field) => field.usageType === "target").map(
    (field) => field.name
  ) as string[];
};
