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
import { OpType, RankOrder, ResultFeature } from "@kie-tools/pmml-editor-marshaller";
import { OutputFieldLabel } from "./OutputFieldLabel";
import { ValidationEntry } from "../../../validation";
import { ValidationIndicatorLabel } from "../../EditorCore/atoms";

interface OutputLabelsProps {
  optype: OpType | undefined;
  targetField: string | undefined;
  targetFieldValidation: ValidationEntry[];
  feature: ResultFeature | undefined;
  value: any | undefined;
  rank: number | undefined;
  rankOrder: RankOrder | undefined;
  segmentId: string | undefined;
  isFinalResult: boolean | undefined;
}

export const OutputLabels = (props: OutputLabelsProps) => {
  const { optype, targetField, targetFieldValidation, feature, value, rank, rankOrder, segmentId, isFinalResult } =
    props;

  return (
    <>
      {optype && OutputFieldLabel("OpType", optype)}
      {targetFieldValidation.length > 0 ? (
        <ValidationIndicatorLabel validations={targetFieldValidation} cssClass="output-fields-list__item__label">
          <strong>TargetField:</strong>&nbsp;
          <em>Missing</em>
        </ValidationIndicatorLabel>
      ) : (
        targetField && OutputFieldLabel("TargetField", targetField)
      )}
      {feature && OutputFieldLabel("Feature", feature)}
      {value && OutputFieldLabel("Value", value)}
      {rank !== undefined && OutputFieldLabel("Rank", rank)}
      {rankOrder && OutputFieldLabel("RankOrder", rankOrder)}
      {segmentId && OutputFieldLabel("SegmentId", segmentId)}
      {isFinalResult && OutputFieldLabel("FinalResult", isFinalResult.toString())}
    </>
  );
};
