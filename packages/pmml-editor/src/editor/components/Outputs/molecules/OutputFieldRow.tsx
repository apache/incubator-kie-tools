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
import { Label, Split, SplitItem } from "@patternfly/react-core";
import { DataType, FieldName, OpType, RankOrder, ResultFeature } from "@kogito-tooling/pmml-editor-marshaller";
import { OutputFieldRowAction, OutputLabels } from "../atoms";
import "./OutputFieldRow.scss";
import { ValidatedType } from "../../../types";

interface OutputFieldRowProps {
  activeOutputFieldIndex: number;
  name: FieldName | undefined;
  dataType: DataType;
  optype: OpType | undefined;
  targetField: FieldName | undefined;
  feature: ResultFeature | undefined;
  value: any | undefined;
  rank: number | undefined;
  rankOrder: RankOrder | undefined;
  segmentId: string | undefined;
  isFinalResult: boolean | undefined;
  onEditOutputField: () => void;
  onDeleteOutputField: () => void;
}

export const OutputFieldRow = (props: OutputFieldRowProps) => {
  const {
    activeOutputFieldIndex,
    name,
    dataType,
    optype,
    targetField,
    feature,
    value,
    rank,
    rankOrder,
    segmentId,
    isFinalResult,
    onEditOutputField,
    onDeleteOutputField
  } = props;

  return (
    <article
      className={`output-item output-item-n${activeOutputFieldIndex} editable`}
      onClick={onEditOutputField}
      tabIndex={0}
      onKeyDown={e => {
        if (e.key === "Enter") {
          e.preventDefault();
          e.stopPropagation();
          onEditOutputField();
        }
      }}
    >
      <Split hasGutter={true} style={{ height: "100%" }}>
        <SplitItem>
          <strong>{name}</strong>
        </SplitItem>
        <SplitItem isFilled={true}>
          <Label color="blue" className="output-item__type-label">
            {dataType}
          </Label>
          <OutputLabels
            optype={optype}
            targetField={targetField}
            feature={feature}
            value={value}
            rank={rank}
            rankOrder={rankOrder}
            segmentId={segmentId}
            isFinalResult={isFinalResult}
          />
        </SplitItem>
        <SplitItem>
          <OutputFieldRowAction onDeleteOutputField={onDeleteOutputField} />
        </SplitItem>
      </Split>
    </article>
  );
};
