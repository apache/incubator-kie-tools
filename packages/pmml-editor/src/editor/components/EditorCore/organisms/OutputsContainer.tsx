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
import { Button, Flex, FlexItem } from "@patternfly/react-core";
import { PlusIcon } from "@patternfly/react-icons";
import { OutputsTable } from "./OutputsTable";
import { Operation } from "../../EditorScorecard";
import "./OutputsContainer.scss";
import { CSSTransition, SwitchTransition } from "react-transition-group";
import { OutputsExtendedProperties } from "./OutputsExtendedProperties";

interface OutputsContainerProps {
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

type OutputsViewSection = "overview" | "extended-properties";

export const OutputsContainer = (props: OutputsContainerProps) => {
  const { activeOperation, setActiveOperation, output, validateOutputFieldName, deleteOutputField, commit } = props;

  const [editItemIndex, setEditItemIndex] = useState<number | undefined>(undefined);
  const [outputField, setOutputField] = useState<OutputField>({ name: "" as FieldName, dataType: "boolean" });

  const [viewSection, setViewSection] = useState<OutputsViewSection>("overview");

  const getTransition = (_viewSection: OutputsViewSection) => {
    if (_viewSection === "overview") {
      return "outputs-container__overview";
    } else {
      return "outputs-container__extended-properties";
    }
  };

  const onEditOutputField = (index: number) => {
    setEditItemIndex(index);
    setOutputField((output?.OutputField as OutputField[])[index]);
    setActiveOperation(Operation.UPDATE);
  };

  const addOutputField = () => {
    setEditItemIndex(undefined);
    setOutputField({ name: "" as FieldName, dataType: "boolean" });
    setActiveOperation(Operation.CREATE_OUTPUT);
  };

  const onCommit = () => {
    let _output: OutputField;
    if (editItemIndex === undefined) {
      commit(
        undefined,
        outputField.name,
        outputField.dataType,
        outputField.optype,
        outputField.targetField,
        outputField.feature,
        outputField.value,
        outputField.rank,
        outputField.rankOrder,
        outputField.segmentId,
        outputField.isFinalResult
      );
    } else {
      _output = (output?.OutputField ?? [])[editItemIndex];
      if (
        _output.name !== outputField.name ||
        _output.dataType !== outputField.dataType ||
        _output.optype !== outputField.optype ||
        _output.targetField !== outputField.targetField ||
        _output.feature !== outputField.feature ||
        _output.value !== outputField.value ||
        _output.rank !== outputField.rank ||
        _output.rankOrder !== outputField.rankOrder ||
        _output.segmentId !== outputField.segmentId ||
        _output.isFinalResult !== outputField.isFinalResult
      ) {
        commit(
          editItemIndex,
          outputField.name,
          outputField.dataType,
          outputField.optype,
          outputField.targetField,
          outputField.feature,
          outputField.value,
          outputField.rank,
          outputField.rankOrder,
          outputField.segmentId,
          outputField.isFinalResult
        );
      }
    }

    onCancel();
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
            {viewSection == "overview" && (
              <div style={{ height: "100%" }}>
                <Flex style={{ margin: "1em 0 2em 0" }}>
                  <FlexItem>
                    <Button
                      variant="secondary"
                      onClick={addOutputField}
                      isDisabled={activeOperation !== Operation.NONE}
                      icon={<PlusIcon />}
                      iconPosition="left"
                    >
                      Add Output
                    </Button>
                  </FlexItem>
                </Flex>
                <div className="outputs-container__body">
                  <OutputsTable
                    activeOperation={activeOperation}
                    onEditOutputField={onEditOutputField}
                    activeOutputFieldIndex={editItemIndex}
                    activeOutputField={outputField}
                    setActiveOutputField={setOutputField}
                    outputs={output?.OutputField as OutputField[]}
                    onAddOutputField={addOutputField}
                    validateOutputFieldName={validateOutputFieldName}
                    viewExtendedProperties={() => setViewSection("extended-properties")}
                    onDeleteOutputField={deleteOutputField}
                    onCommit={onCommit}
                    onCancel={onCancel}
                  />
                </div>
              </div>
            )}
            {viewSection == "extended-properties" && (
              <div style={{ height: "100%" }}>
                <Flex style={{ margin: "1em 0 2em 0" }}>
                  <FlexItem>
                    <Button
                      variant="secondary"
                      onClick={e => setViewSection("overview")}
                      icon={<PlusIcon />}
                      iconPosition="left"
                    >
                      Done
                    </Button>
                  </FlexItem>
                </Flex>
                <div className="outputs-container__body">
                  <OutputsExtendedProperties activeOutputField={outputField} setActiveOutputField={setOutputField} />
                </div>
              </div>
            )}
          </>
        </CSSTransition>
      </SwitchTransition>
    </div>
  );
};
