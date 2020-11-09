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
import { ArrowAltCircleRightIcon } from "@patternfly/react-icons";

interface OutputLabelsEditModeProps {
  activeOutputField: OutputField;
  setActiveOutputField: (_output: OutputField) => void;
  viewExtendedProperties: () => void;
}

const PADDING: CSSProperties = { marginRight: "4px" };

export const OutputLabelsEditMode = (props: OutputLabelsEditModeProps) => {
  const { activeOutputField, setActiveOutputField, viewExtendedProperties } = props;

  return (
    <>
      {activeOutputField.optype && (
        <Tooltip position={TooltipPosition.top} content={<div>Optype</div>}>
          <Label
            style={PADDING}
            color="orange"
            onClose={e => setActiveOutputField({ ...activeOutputField, optype: undefined })}
          >
            OpType:{activeOutputField.optype}
          </Label>
        </Tooltip>
      )}

      {activeOutputField.targetField && (
        <Tooltip position={TooltipPosition.top} content={<div>Target field</div>}>
          <Label
            style={PADDING}
            color="orange"
            onClose={e => setActiveOutputField({ ...activeOutputField, targetField: undefined })}
          >
            TargetField:{activeOutputField.targetField}
          </Label>
        </Tooltip>
      )}
      {activeOutputField.feature && (
        <Tooltip position={TooltipPosition.top} content={<div>Feature</div>}>
          <Label
            style={PADDING}
            color="orange"
            onClose={e => setActiveOutputField({ ...activeOutputField, feature: undefined })}
          >
            Feature:{activeOutputField.feature}
          </Label>
        </Tooltip>
      )}
      {activeOutputField.value && (
        <Tooltip position={TooltipPosition.top} content={<div>Value</div>}>
          <Label
            style={PADDING}
            color="orange"
            onClose={e => setActiveOutputField({ ...activeOutputField, value: undefined })}
          >
            Value:{activeOutputField.value}
          </Label>
        </Tooltip>
      )}
      {activeOutputField.rank && (
        <Tooltip position={TooltipPosition.top} content={<div>Rank</div>}>
          <Label
            style={PADDING}
            color="orange"
            onClose={e => setActiveOutputField({ ...activeOutputField, rank: undefined })}
          >
            Rank:{activeOutputField.rank}
          </Label>
        </Tooltip>
      )}
      {activeOutputField.rankOrder && (
        <Tooltip position={TooltipPosition.top} content={<div>Rank order</div>}>
          <Label
            style={PADDING}
            color="orange"
            onClose={e => setActiveOutputField({ ...activeOutputField, rankOrder: undefined })}
          >
            RankOrder:{activeOutputField.rankOrder}
          </Label>
        </Tooltip>
      )}
      {activeOutputField.segmentId && (
        <Tooltip position={TooltipPosition.top} content={<div>Segment Id</div>}>
          <Label
            style={PADDING}
            color="orange"
            onClose={e => setActiveOutputField({ ...activeOutputField, segmentId: undefined })}
          >
            SegmentId:{activeOutputField.segmentId}
          </Label>
        </Tooltip>
      )}
      {activeOutputField.isFinalResult && (
        <Tooltip position={TooltipPosition.top} content={<div>Final result?</div>}>
          <Label
            style={PADDING}
            color="orange"
            onClose={e => setActiveOutputField({ ...activeOutputField, isFinalResult: undefined })}
          >
            FinalResult:{activeOutputField.isFinalResult.toString()}
          </Label>
        </Tooltip>
      )}
      <Label
        style={PADDING}
        color="orange"
        href="#filled"
        icon={<ArrowAltCircleRightIcon />}
        onClick={e => {
          e.preventDefault();
          viewExtendedProperties();
        }}
      >
        More...
      </Label>
    </>
  );
};
