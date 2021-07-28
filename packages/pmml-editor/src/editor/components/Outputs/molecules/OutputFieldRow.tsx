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
import { BaseSyntheticEvent, useMemo } from "react";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { Split, SplitItem } from "@patternfly/react-core/dist/js/layouts/Split";
import { Label } from "@patternfly/react-core/dist/js/components/Label";
import {
  DataType,
  FieldName,
  OpType,
  OutputField,
  RankOrder,
  ResultFeature,
} from "@kogito-tooling/pmml-editor-marshaller";
import { OutputFieldRowAction, OutputLabels } from "../atoms";
import "./OutputFieldRow.scss";
import { ValidationIndicator } from "../../EditorCore/atoms";
import { useValidationRegistry } from "../../../validation";
import { Builder } from "../../../paths";

interface OutputFieldRowProps {
  modelIndex: number;
  outputFieldIndex: number;
  outputField: OutputField | undefined;
  onEditOutputField: () => void;
  onDeleteOutputField: () => void;
}

interface Values {
  name: FieldName | undefined;
  dataType: DataType | undefined;
  optype?: OpType;
  targetField?: FieldName;
  feature?: ResultFeature;
  value?: any;
  rank?: number;
  rankOrder?: RankOrder;
  segmentId?: string;
  isFinalResult?: boolean;
}

const OutputFieldRow = (props: OutputFieldRowProps) => {
  const { modelIndex, outputFieldIndex, outputField, onEditOutputField, onDeleteOutputField } = props;

  const { name, dataType, optype, targetField, feature, value, rank, rankOrder, segmentId, isFinalResult } =
    useMemo<Values>(() => {
      return {
        name: outputField?.name,
        dataType: outputField?.dataType,
        optype: outputField?.optype,
        targetField: outputField?.targetField,
        feature: outputField?.feature,
        value: outputField?.value,
        rank: outputField?.rank,
        rankOrder: outputField?.rankOrder,
        segmentId: outputField?.segmentId,
        isFinalResult: outputField?.isFinalResult,
      };
    }, [outputField]);

  const { validationRegistry } = useValidationRegistry();
  const validations = useMemo(
    () => validationRegistry.get(Builder().forModel(modelIndex).forOutput().forOutputField(outputFieldIndex).build()),
    [outputFieldIndex, modelIndex, outputField]
  );
  const targetFieldValidation = useMemo(
    () =>
      validationRegistry.get(
        Builder().forModel(modelIndex).forOutput().forOutputField(outputFieldIndex).forTargetField().build()
      ),
    [outputFieldIndex, modelIndex, outputField]
  );

  const handleEdit = (event: BaseSyntheticEvent) => {
    event.preventDefault();
    event.stopPropagation();
    onEditOutputField();
  };

  return (
    <section
      tabIndex={0}
      className={"editable-item__inner"}
      onClick={(event) => handleEdit(event)}
      onKeyDown={(event) => {
        if (event.key === "Enter") {
          handleEdit(event);
        }
      }}
      data-ouia-component-type="output-field"
    >
      <Split hasGutter={true} style={{ height: "100%" }}>
        <SplitItem>
          <Flex
            alignItems={{ default: "alignItemsCenter" }}
            justifyContent={{ default: "justifyContentCenter" }}
            style={{ height: "100%" }}
          >
            <FlexItem>
              <ValidationIndicator validations={validations} />
            </FlexItem>
          </Flex>
        </SplitItem>
        <SplitItem data-ouia-component-type="output-field-name">
          <strong>{name}</strong>
        </SplitItem>
        <SplitItem isFilled={true}>
          <Label color="blue" className="output-item__type-label">
            {dataType}
          </Label>
          <OutputLabels
            optype={optype}
            targetField={targetField}
            targetFieldValidation={targetFieldValidation}
            feature={feature}
            value={value}
            rank={rank}
            rankOrder={rankOrder}
            segmentId={segmentId}
            isFinalResult={isFinalResult}
          />
        </SplitItem>
        <SplitItem>
          <OutputFieldRowAction
            onDeleteOutputField={onDeleteOutputField}
            data-ouia-component-type="output-field-delete"
          />
        </SplitItem>
      </Split>
    </section>
  );
};

export default OutputFieldRow;
