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
import { Label, Tooltip, TooltipPosition } from "@patternfly/react-core";
import { OutputField } from "@kogito-tooling/pmml-editor-marshaller";

interface OutputLabelsProps {
  activeOutputField: OutputField;
}

const PADDING: CSSProperties = { marginRight: "4px" };

export const OutputLabels = (props: OutputLabelsProps) => {
  const { activeOutputField } = props;

  return (
    <>
      {activeOutputField.optype && (
        <Tooltip position={TooltipPosition.top} content={<div>Optype</div>}>
          <Label style={PADDING} color="orange">
            OpType:{activeOutputField.optype}
          </Label>
        </Tooltip>
      )}

      {activeOutputField.targetField && (
        <Tooltip position={TooltipPosition.top} content={<div>Target field</div>}>
          <Label style={PADDING} color="orange">
            TargetField:{activeOutputField.targetField}
          </Label>
        </Tooltip>
      )}
      {activeOutputField.feature && (
        <Tooltip position={TooltipPosition.top} content={<div>Feature</div>}>
          <Label style={PADDING} color="orange">
            Feature:{activeOutputField.feature}
          </Label>
        </Tooltip>
      )}
      {activeOutputField.value && (
        <Tooltip position={TooltipPosition.top} content={<div>Value</div>}>
          <Label style={PADDING} color="orange">
            Value:{activeOutputField.value}
          </Label>
        </Tooltip>
      )}
      {activeOutputField.rank && (
        <Tooltip position={TooltipPosition.top} content={<div>Rank</div>}>
          <Label style={PADDING} color="orange">
            Rank:{activeOutputField.rank}
          </Label>
        </Tooltip>
      )}
      {activeOutputField.rankOrder && (
        <Tooltip position={TooltipPosition.top} content={<div>Rank order</div>}>
          <Label style={PADDING} color="orange">
            RankOrder:{activeOutputField.rankOrder}
          </Label>
        </Tooltip>
      )}
      {activeOutputField.segmentId && (
        <Tooltip position={TooltipPosition.top} content={<div>Segment Id</div>}>
          <Label style={PADDING} color="orange">
            SegmentId:{activeOutputField.segmentId}
          </Label>
        </Tooltip>
      )}
      {activeOutputField.isFinalResult && (
        <Tooltip position={TooltipPosition.top} content={<div>Final result?</div>}>
          <Label style={PADDING} color="orange">
            FinalResult:{activeOutputField.isFinalResult.toString()}
          </Label>
        </Tooltip>
      )}
    </>
  );
};
