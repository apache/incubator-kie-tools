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
import { OutputField } from "@kogito-tooling/pmml-editor-marshaller";
import { OutputFieldLabel } from "./OutputFieldLabel";

interface OutputLabelsProps {
  activeOutputField: OutputField;
}

export const OutputLabels = (props: OutputLabelsProps) => {
  const { activeOutputField } = props;

  return (
    <>
      {activeOutputField.optype && OutputFieldLabel("OpType", activeOutputField.optype)}
      {activeOutputField.targetField && OutputFieldLabel("TargetField", activeOutputField.targetField)}
      {activeOutputField.feature && OutputFieldLabel("Feature", activeOutputField.feature)}
      {activeOutputField.value && OutputFieldLabel("Value", activeOutputField.value)}
      {activeOutputField.rank && OutputFieldLabel("Rank", activeOutputField.rank)}
      {activeOutputField.rankOrder && OutputFieldLabel("RankOrder", activeOutputField.rankOrder)}
      {activeOutputField.segmentId && OutputFieldLabel("SegmentId", activeOutputField.segmentId)}
      {activeOutputField.isFinalResult && OutputFieldLabel("FinalResult", activeOutputField.isFinalResult.toString())}
    </>
  );
};
