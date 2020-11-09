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
import { CSSProperties, useEffect, useState } from "react";
import { Label, Tooltip, TooltipPosition } from "@patternfly/react-core";
import { FieldName, OutputField } from "@kogito-tooling/pmml-editor-marshaller";

interface OutputLabelsEditModeProps {
  activeOutputField: OutputField;
  viewExtendedProperties: () => void;
}

const PADDING: CSSProperties = { marginRight: "4px" };

export const OutputLabelsEditMode = (props: OutputLabelsEditModeProps) => {
  const { activeOutputField, viewExtendedProperties } = props;

  const [optype, setOptype] = useState<string | undefined>();
  const [targetField, setTargetField] = useState<FieldName | undefined>();
  const [feature, setFeature] = useState<string | undefined>();
  const [value, setValue] = useState<string | undefined>();
  const [rank, setRank] = useState<number | undefined>();
  const [rankOrder, setRankOrder] = useState<string | undefined>();
  const [segmentId, setSegmentId] = useState<string | undefined>();
  const [isFinalResult, setIsFinalResult] = useState<boolean | undefined>();

  useEffect(() => {
    setOptype(activeOutputField.optype);
    setTargetField(activeOutputField.targetField);
    setFeature(activeOutputField.feature);
    setValue(activeOutputField.value);
    setRank(activeOutputField.rank);
    setRankOrder(activeOutputField.rankOrder);
    setSegmentId(activeOutputField.segmentId);
    setIsFinalResult(activeOutputField.isFinalResult);
  }, [activeOutputField]);

  return (
    <>
      {optype && (
        <Tooltip position={TooltipPosition.top} content={<div>Optype</div>}>
          <Label style={PADDING} color="orange" onClose={e => setOptype(undefined)}>
            {optype}
          </Label>
        </Tooltip>
      )}

      {targetField && (
        <Tooltip position={TooltipPosition.top} content={<div>Target field</div>}>
          <Label style={PADDING} color="orange" onClose={e => setTargetField(undefined)}>
            {targetField}
          </Label>
        </Tooltip>
      )}
      {feature && (
        <Tooltip position={TooltipPosition.top} content={<div>Feature</div>}>
          <Label style={PADDING} color="orange" onClose={e => setFeature(undefined)}>
            {feature}
          </Label>
        </Tooltip>
      )}
      {value && (
        <Tooltip position={TooltipPosition.top} content={<div>Value</div>}>
          <Label style={PADDING} color="orange" onClose={e => setValue(undefined)}>
            {value}
          </Label>
        </Tooltip>
      )}
      {rank && (
        <Tooltip position={TooltipPosition.top} content={<div>Rank</div>}>
          <Label style={PADDING} color="orange" onClose={e => setRank(undefined)}>
            {rank}
          </Label>
        </Tooltip>
      )}
      {rankOrder && (
        <Tooltip position={TooltipPosition.top} content={<div>Rank order</div>}>
          <Label style={PADDING} color="orange" onClose={e => setRankOrder(undefined)}>
            {rankOrder}
          </Label>
        </Tooltip>
      )}
      {segmentId && (
        <Tooltip position={TooltipPosition.top} content={<div>Segment Id</div>}>
          <Label style={PADDING} color="orange" onClose={e => setSegmentId(undefined)}>
            {segmentId}
          </Label>
        </Tooltip>
      )}
      {isFinalResult && (
        <Tooltip position={TooltipPosition.top} content={<div>Final result?</div>}>
          <Label style={PADDING} color="orange" onClose={e => setIsFinalResult(undefined)}>
            {isFinalResult}
          </Label>
        </Tooltip>
      )}
      <Label style={PADDING} color="orange" onClick={viewExtendedProperties}>
        ...
      </Label>
    </>
  );
};
