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
import { CSSProperties } from "react";
import { Label } from "@patternfly/react-core/dist/js/components/Label";
import { FieldName, OpType, OutputField, RankOrder, ResultFeature } from "@kogito-tooling/pmml-editor-marshaller";
import { ArrowAltCircleRightIcon } from "@patternfly/react-icons/dist/js/icons/arrow-alt-circle-right-icon";
import { OutputFieldLabel } from "./OutputFieldLabel";
import { ValidationEntry } from "../../../validation";
import { ValidationIndicatorLabel } from "../../EditorCore/atoms";

interface OutputLabelsEditModeProps {
  optype: OpType | undefined;
  setOptype: (optype: OpType | undefined) => void;
  targetField: FieldName | undefined;
  setTargetField: (targetField: FieldName | undefined) => void;
  targetFieldValidation: ValidationEntry[];
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
  commit: (outputField: Partial<OutputField>) => void;
  viewExtendedProperties: () => void;
  isDisabled: boolean;
}

const PADDING: CSSProperties = { marginRight: "4px" };

export const OutputLabelsEditMode = (props: OutputLabelsEditModeProps) => {
  const {
    optype,
    setOptype,
    targetField,
    setTargetField,
    targetFieldValidation,
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
    commit,
    viewExtendedProperties,
    isDisabled,
  } = props;

  return (
    <>
      {optype &&
        OutputFieldLabel("OpType", optype, () => {
          setOptype(undefined);
          commit({
            optype: undefined,
          });
        })}
      {targetFieldValidation.length > 0 ? (
        <ValidationIndicatorLabel validations={targetFieldValidation} cssClass="output-fields-list__item__label">
          <strong>TargetField:</strong>&nbsp;
          <em>Missing</em>
        </ValidationIndicatorLabel>
      ) : (
        targetField &&
        OutputFieldLabel("TargetField", targetField, () => {
          setTargetField(undefined);
          commit({
            targetField: undefined,
          });
        })
      )}
      {feature &&
        OutputFieldLabel("Feature", feature, () => {
          setFeature(undefined);
          commit({
            feature: undefined,
          });
        })}
      {value &&
        OutputFieldLabel("Value", value, () => {
          setValue(undefined);
          commit({
            value: undefined,
          });
        })}
      {rank !== undefined &&
        OutputFieldLabel("Rank", rank, () => {
          setRank(undefined);
          commit({
            rank: undefined,
          });
        })}
      {rankOrder &&
        OutputFieldLabel("RankOrder", rankOrder, () => {
          setRankOrder(undefined);
          commit({
            rankOrder: undefined,
          });
        })}
      {segmentId &&
        OutputFieldLabel("SegmentId", segmentId, () => {
          setSegmentId(undefined);
          commit({
            segmentId: undefined,
          });
        })}
      {isFinalResult &&
        OutputFieldLabel("FinalResult", isFinalResult.toString(), () => {
          setIsFinalResult(undefined);
          commit({
            isFinalResult: undefined,
          });
        })}
      {isDisabled && (
        <Label style={PADDING} variant="outline" icon={<ArrowAltCircleRightIcon />}>
          Edit properties
        </Label>
      )}
      {!isDisabled && (
        <Label
          style={PADDING}
          variant="outline"
          color="cyan"
          href="#outline"
          icon={<ArrowAltCircleRightIcon />}
          onClick={(e) => {
            e.preventDefault();
            viewExtendedProperties();
          }}
        >
          Edit properties
        </Label>
      )}
    </>
  );
};
