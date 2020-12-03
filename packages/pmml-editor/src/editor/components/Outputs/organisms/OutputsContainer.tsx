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
import { useDispatch } from "react-redux";
import { isEqual } from "lodash";
import { CSSTransition, SwitchTransition } from "react-transition-group";
import { Button, Flex, FlexItem, Stack, StackItem, TextContent, Title } from "@patternfly/react-core";
import { ArrowAltCircleLeftIcon, BoltIcon, PlusIcon } from "@patternfly/react-icons";
import {
  DataType,
  FieldName,
  OpType,
  Output,
  OutputField,
  RankOrder,
  ResultFeature
} from "@kogito-tooling/pmml-editor-marshaller";
import { ValidatedType } from "../../../types";
import { Actions } from "../../../reducers";
import OutputFieldsTable from "./OutputFieldsTable";
import OutputsBatchAdd from "./OutputsBatchAdd";
import { Operation } from "../../EditorScorecard";
import { OutputFieldExtendedProperties } from "./OutputFieldExtendedProperties";
import "./OutputsContainer.scss";
import get = Reflect.get;
import set = Reflect.set;

interface OutputsContainerProps {
  modelIndex: number;
  activeOperation: Operation;
  setActiveOperation: (operation: Operation) => void;
  output?: Output;
  validateOutputFieldName: (index: number | undefined, name: string | undefined) => boolean;
  deleteOutputField: (index: number) => void;
  commitOutputField: (index: number | undefined, outputField: OutputField) => void;
}

type OutputsViewSection = "overview" | "extended-properties" | "batch-add";

export const OutputsContainer = (props: OutputsContainerProps) => {
  const {
    modelIndex,
    activeOperation,
    setActiveOperation,
    output,
    validateOutputFieldName,
    deleteOutputField,
    commitOutputField
  } = props;

  const [editItemIndex, setEditItemIndex] = useState<number | undefined>(undefined);
  const [name, setName] = useState<ValidatedType<FieldName> | undefined>();
  const [dataType, setDataType] = useState<DataType>("boolean");
  const [optype, setOptype] = useState<OpType | undefined>();
  const [targetField, setTargetField] = useState<FieldName | undefined>();
  const [feature, setFeature] = useState<ResultFeature | undefined>();
  const [value, setValue] = useState<any | undefined>();
  const [rank, setRank] = useState<number | undefined>();
  const [rankOrder, setRankOrder] = useState<RankOrder | undefined>();
  const [segmentId, setSegmentId] = useState<string | undefined>();
  const [isFinalResult, setIsFinalResult] = useState<boolean | undefined>();
  const [viewSection, setViewSection] = useState<OutputsViewSection>("overview");

  const dispatch = useDispatch();

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
      //Index of the new row is equal to the number of existing rows
      setEditItemIndex(numberOfOutputFields);
      //TODO {manstis} This will need some more magic to ensure the new default does not already exist
      const newOutputFieldName: FieldName = "New output" as FieldName;
      commitOutputField(undefined, {
        name: newOutputFieldName,
        dataType: "string",
        optype: undefined,
        targetField: undefined,
        feature: undefined,
        value: undefined,
        rank: undefined,
        rankOrder: undefined,
        segmentId: undefined,
        isFinalResult: undefined
      });
      setName({ value: newOutputFieldName, valid: true });
      setDataType("string");
      setOptype(undefined);
      setTargetField(undefined);
      setFeature(undefined);
      setValue(undefined);
      setRank(undefined);
      setRankOrder(undefined);
      setSegmentId(undefined);
      setIsFinalResult(undefined);
      setActiveOperation(Operation.UPDATE_OUTPUT);
    }
  };

  const onEditOutputField = (index: number) => {
    setEditItemIndex(index);
    const outputField: OutputField = (output?.OutputField as OutputField[])[index];
    setName({ value: outputField.name, valid: true });
    setDataType(outputField.dataType);
    setOptype(outputField.optype);
    setTargetField(outputField.targetField);
    setFeature(outputField.feature);
    setValue(outputField.value);
    setRank(outputField.rank);
    setRankOrder(outputField.rankOrder);
    setSegmentId(outputField.segmentId);
    setIsFinalResult(outputField.isFinalResult);
    setActiveOperation(Operation.UPDATE_OUTPUT);
  };

  const addBatchOutputs = (outputs: string) => {
    const outputsNames = outputs.split("\n").filter(item => item.trim().length > 0);
    dispatch({
      type: Actions.AddBatchOutputs,
      payload: {
        modelIndex: modelIndex,
        outputFields: outputsNames
      }
    });
    setViewSection("overview");
  };

  const onCommitAndClose = () => {
    onCommit({});
    onCancel();
  };

  const onCommit = (partial: Partial<OutputField>) => {
    if (output !== undefined && editItemIndex !== undefined) {
      const outputField = output.OutputField[editItemIndex];
      const existingPartial: Partial<OutputField> = {};
      Object.keys(partial).forEach(key => set(existingPartial, key, get(outputField, key)));

      if (!isEqual(partial, existingPartial)) {
        commitOutputField(editItemIndex, { ...outputField, ...partial });
      }
    }
  };

  const onCancel = () => {
    setEditItemIndex(undefined);
    setActiveOperation(Operation.NONE);
  };

  return (
    <div className="outputs-container">
      <SwitchTransition mode={"out-in"}>
        <CSSTransition
          timeout={{
            enter: 230,
            exit: 100
          }}
          classNames={getTransition(viewSection)}
          key={viewSection}
        >
          <>
            {viewSection === "overview" && (
              <Stack hasGutter={true}>
                <StackItem>
                  <Flex>
                    <FlexItem>
                      <Button
                        variant="primary"
                        onClick={addOutputField}
                        isDisabled={activeOperation !== Operation.NONE}
                        icon={<PlusIcon />}
                        iconPosition="left"
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
                <StackItem className="outputs-container__overview">
                  <OutputFieldsTable
                    activeOperation={activeOperation}
                    onEditOutputField={onEditOutputField}
                    activeOutputFieldIndex={editItemIndex}
                    name={name}
                    setName={setName}
                    dataType={dataType}
                    setDataType={setDataType}
                    optype={optype}
                    setOptype={setOptype}
                    targetField={targetField}
                    setTargetField={setTargetField}
                    feature={feature}
                    setFeature={setFeature}
                    value={value}
                    setValue={setValue}
                    rank={rank}
                    setRank={setRank}
                    rankOrder={rankOrder}
                    setRankOrder={setRankOrder}
                    segmentId={segmentId}
                    setSegmentId={setSegmentId}
                    isFinalResult={isFinalResult}
                    setIsFinalResult={setIsFinalResult}
                    outputs={output?.OutputField as OutputField[]}
                    onAddOutputField={addOutputField}
                    validateOutputFieldName={validateOutputFieldName}
                    viewExtendedProperties={() => setViewSection("extended-properties")}
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
                      <a onClick={() => setViewSection("overview")}>{name?.value}</a>&nbsp;/&nbsp;Properties
                    </Title>
                  </TextContent>
                </StackItem>
                <StackItem className="outputs-container__extended-properties">
                  <OutputFieldExtendedProperties
                    optype={optype}
                    setOptype={setOptype}
                    targetField={targetField}
                    setTargetField={setTargetField}
                    feature={feature}
                    setFeature={setFeature}
                    value={value}
                    setValue={setValue}
                    rank={rank}
                    setRank={setRank}
                    rankOrder={rankOrder}
                    setRankOrder={setRankOrder}
                    segmentId={segmentId}
                    setSegmentId={setSegmentId}
                    isFinalResult={isFinalResult}
                    setIsFinalResult={setIsFinalResult}
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
                    Done
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
