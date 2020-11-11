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
import { Label } from "@patternfly/react-core";
import { OutputField } from "@kogito-tooling/pmml-editor-marshaller";
import { ArrowAltCircleRightIcon } from "@patternfly/react-icons";
import { OutputFieldLabel } from "./OutputFieldLabel";

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
      {activeOutputField.optype &&
        OutputFieldLabel("OpType", activeOutputField.optype, () =>
          setActiveOutputField({
            ...activeOutputField,
            optype: undefined
          })
        )}
      {activeOutputField.targetField &&
        OutputFieldLabel("TargetField", activeOutputField.targetField, () =>
          setActiveOutputField({
            ...activeOutputField,
            targetField: undefined
          })
        )}
      {activeOutputField.feature &&
        OutputFieldLabel("Feature", activeOutputField.feature, () =>
          setActiveOutputField({
            ...activeOutputField,
            feature: undefined
          })
        )}
      {activeOutputField.value &&
        OutputFieldLabel("Value", activeOutputField.value, () =>
          setActiveOutputField({
            ...activeOutputField,
            value: undefined
          })
        )}
      {activeOutputField.rank &&
        OutputFieldLabel("Rank", activeOutputField.rank, () =>
          setActiveOutputField({
            ...activeOutputField,
            rank: undefined
          })
        )}
      {activeOutputField.rankOrder &&
        OutputFieldLabel("RankOrder", activeOutputField.rankOrder, () =>
          setActiveOutputField({
            ...activeOutputField,
            rankOrder: undefined
          })
        )}
      {activeOutputField.segmentId &&
        OutputFieldLabel("SegmentId", activeOutputField.segmentId, () =>
          setActiveOutputField({
            ...activeOutputField,
            segmentId: undefined
          })
        )}
      {activeOutputField.isFinalResult &&
        OutputFieldLabel("FinalResult", activeOutputField.isFinalResult.toString(), () =>
          setActiveOutputField({
            ...activeOutputField,
            isFinalResult: undefined
          })
        )}
      <Label
        style={PADDING}
        variant="outline"
        color="orange"
        href="#outline"
        icon={<ArrowAltCircleRightIcon />}
        onClick={e => {
          e.preventDefault();
          viewExtendedProperties();
        }}
      >
        Edit properties...
      </Label>
    </>
  );
};
