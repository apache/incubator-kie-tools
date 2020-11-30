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
import { useEffect, useRef } from "react";
import { Bullseye, Form } from "@patternfly/react-core";
import {
  DataType,
  FieldName,
  OpType,
  OutputField,
  RankOrder,
  ResultFeature
} from "@kogito-tooling/pmml-editor-marshaller";
import "./OutputFieldsTable.scss";
import { Operation } from "../../EditorScorecard";
import { EmptyStateNoOutput } from "./EmptyStateNoOutput";
import { ValidatedType } from "../../../types";
import OutputFieldRow from "../molecules/OutputFieldRow";
import OutputFieldEditRow from "../molecules/OutputFieldEditRow";

interface OutputFieldsTableProps {
  activeOperation: Operation;
  onAddOutputField: () => void;
  onEditOutputField: (index: number) => void;
  onDeleteOutputField: (index: number) => void;
  activeOutputFieldIndex: number | undefined;
  name: ValidatedType<FieldName> | undefined;
  setName: (name: ValidatedType<FieldName>) => void;
  dataType: DataType;
  setDataType: (dataType: DataType) => void;
  optype: OpType | undefined;
  setOptype: (optype: OpType | undefined) => void;
  targetField: FieldName | undefined;
  setTargetField: (targetField: FieldName | undefined) => void;
  feature: ResultFeature | undefined;
  setFeature: (feature: ResultFeature | undefined) => void;
  value: any | undefined;
  setValue: (value: any | undefined) => void;
  rank: number | undefined;
  setRank: (rank: number | undefined) => void;
  rankOrder: RankOrder | undefined;
  setRankOrder: (rankOrder: RankOrder | undefined) => void;
  segmentId: string | undefined;
  setSegmentId: (segmentId: string | undefined) => void;
  isFinalResult: boolean | undefined;
  setIsFinalResult: (isFinalResult: boolean | undefined) => void;
  outputs: OutputField[];
  validateOutputFieldName: (index: number | undefined, name: string | undefined) => boolean;
  viewExtendedProperties: () => void;
  onCommitAndClose: () => void;
  onCommit: (partial: Partial<OutputField>) => void;
  onCancel: () => void;
}

const OutputFieldsTable = (props: OutputFieldsTableProps) => {
  const {
    activeOperation,
    onAddOutputField,
    onEditOutputField,
    onDeleteOutputField,
    activeOutputFieldIndex,
    name,
    setName,
    dataType,
    setDataType,
    optype,
    setOptype,
    targetField,
    setTargetField,
    feature,
    setFeature,
    value,
    setValue,
    rank,
    setRank,
    rankOrder,
    setRankOrder,
    segmentId,
    setSegmentId,
    isFinalResult,
    setIsFinalResult,
    outputs,
    validateOutputFieldName,
    viewExtendedProperties,
    onCommitAndClose,
    onCommit,
    onCancel
  } = props;

  const addOutputRowRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    if (activeOperation === Operation.UPDATE_OUTPUT && addOutputRowRef.current) {
      addOutputRowRef.current.scrollIntoView({ behavior: "smooth" });
    }
  }, [activeOperation]);

  const onDelete = (index: number | undefined) => {
    if (index !== undefined) {
      onDeleteOutputField(index);
    }
  };

  const onValidateOutputFieldName = (index: number | undefined, nameToValidate: string | undefined): boolean => {
    return validateOutputFieldName(index, nameToValidate);
  };

  return (
    <Form
      onSubmit={e => {
        e.stopPropagation();
        e.preventDefault();
      }}
    >
      <section>
        {outputs.map((o, index) => (
          <article
            className={`output-item output-item-n${activeOutputFieldIndex} editable ${
              activeOutputFieldIndex === index ? "editing" : ""
            }`}
            key={o.name as string}
          >
            {activeOutputFieldIndex === index && (
              <OutputFieldEditRow
                activeOperation={activeOperation}
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
                validateOutputName={_name => onValidateOutputFieldName(index, _name)}
                viewExtendedProperties={viewExtendedProperties}
                onCommitAndClose={onCommitAndClose}
                onCommit={onCommit}
                onCancel={onCancel}
              />
            )}
            {activeOutputFieldIndex !== index && (
              <OutputFieldRow
                name={o.name}
                dataType={o.dataType}
                optype={o.optype}
                targetField={o.targetField}
                feature={o.feature}
                value={o.value}
                rank={o.rank}
                rankOrder={o.rankOrder}
                segmentId={o.segmentId}
                isFinalResult={o.isFinalResult}
                onEditOutputField={() => onEditOutputField(index)}
                onDeleteOutputField={() => onDelete(index)}
              />
            )}
          </article>
        ))}
      </section>
      {outputs.length === 0 && (
        <Bullseye>
          <EmptyStateNoOutput onAddOutputField={onAddOutputField} />
        </Bullseye>
      )}
    </Form>
  );
};

export default OutputFieldsTable;
