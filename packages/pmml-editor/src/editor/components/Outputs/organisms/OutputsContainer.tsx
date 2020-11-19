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
  DataType,
  FieldName,
  OpType,
  Output,
  OutputField,
  RankOrder,
  ResultFeature
} from "@kogito-tooling/pmml-editor-marshaller";
import { Button, Stack, StackItem, Title, TitleSizes } from "@patternfly/react-core";
import { ArrowAltCircleLeftIcon, PlusIcon } from "@patternfly/react-icons";
import { OutputFieldsTable } from "./OutputFieldsTable";
import { Operation } from "../../EditorScorecard";
import "./OutputsContainer.scss";
import { CSSTransition, SwitchTransition } from "react-transition-group";
import { OutputFieldExtendedProperties } from "./OutputFieldExtendedProperties";
import { ValidatedType } from "../../../types";
import { isEqual } from "lodash";
import get = Reflect.get;
import set = Reflect.set;

interface OutputsContainerProps {
  modelIndex: number;
  activeOperation: Operation;
  setActiveOperation: (operation: Operation) => void;
  output?: Output;
  validateOutputFieldName: (index: number | undefined, name: string | undefined) => boolean;
  deleteOutputField: (index: number) => void;
  commit: (index: number | undefined, outputField: OutputField) => void;
}

type OutputsViewSection = "overview" | "extended-properties";

export const OutputsContainer = (props: OutputsContainerProps) => {
  const { activeOperation, setActiveOperation, output, validateOutputFieldName, deleteOutputField, commit } = props;

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

  const getTransition = (_viewSection: OutputsViewSection) => {
    if (_viewSection === "overview") {
      return "outputs-container__overview";
    } else {
      return "outputs-container__extended-properties";
    }
  };

  const addOutputField = () => {
    setEditItemIndex(undefined);
    setName({ value: "" as FieldName, valid: true });
    setDataType("boolean");
    setOptype(undefined);
    setTargetField(undefined);
    setFeature(undefined);
    setValue(undefined);
    setRank(undefined);
    setRankOrder(undefined);
    setSegmentId(undefined);
    setIsFinalResult(undefined);
    setActiveOperation(Operation.CREATE_OUTPUT);
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

  const onCommitAndClose = () => {
    onCommit({});
    onCancel();
  };

  const onCommit = (partial: Partial<OutputField>) => {
    if (editItemIndex === undefined) {
      commit(undefined, {
        name: name?.value ?? { value: "" },
        dataType,
        optype,
        targetField,
        feature,
        value,
        rank,
        rankOrder,
        segmentId,
        isFinalResult
      });
    } else if (output !== undefined) {
      const outputField = output.OutputField[editItemIndex];
      const existingPartial: Partial<OutputField> = {};
      Object.keys(partial).forEach(key => set(existingPartial, key, get(outputField, key)));

      if (!isEqual(partial, existingPartial)) {
        commit(editItemIndex, { ...outputField, ...partial });
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
                  <Button
                    variant="primary"
                    onClick={addOutputField}
                    isDisabled={activeOperation !== Operation.NONE}
                    icon={<PlusIcon />}
                    iconPosition="left"
                  >
                    Add Output
                  </Button>
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
                  <Title headingLevel="h4" size={TitleSizes.xl}>
                    Editing Properties{" "}
                    {name?.value !== "" ? (
                      <span>
                        for <em>{name?.value}</em>
                      </span>
                    ) : (
                      ""
                    )}
                  </Title>
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
                    onClick={e => setViewSection("overview")}
                    icon={<ArrowAltCircleLeftIcon />}
                    iconPosition="left"
                  >
                    Done
                  </Button>
                </StackItem>
              </Stack>
            )}
          </>
        </CSSTransition>
      </SwitchTransition>
    </div>
  );
};
