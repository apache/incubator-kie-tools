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
import { ValidatedType } from "../../../types";

interface OutputLabelsEditModeProps {
  activeOutputField: ValidatedType<OutputField>;
  setActiveOutputField: (_output: ValidatedType<OutputField>) => void;
  viewExtendedProperties: () => void;
}

const PADDING: CSSProperties = { marginRight: "4px" };

export const OutputLabelsEditMode = (props: OutputLabelsEditModeProps) => {
  const { activeOutputField, setActiveOutputField, viewExtendedProperties } = props;

  return (
    <>
      {activeOutputField.value.optype &&
        OutputFieldLabel("OpType", activeOutputField.value.optype, () =>
          setActiveOutputField({
            ...activeOutputField,
            value: {
              ...activeOutputField.value,
              optype: undefined
            }
          })
        )}
      {activeOutputField.value.targetField &&
        OutputFieldLabel("TargetField", activeOutputField.value.targetField, () =>
          setActiveOutputField({
            ...activeOutputField,
            value: {
              ...activeOutputField.value,
              targetField: undefined
            }
          })
        )}
      {activeOutputField.value.feature &&
        OutputFieldLabel("Feature", activeOutputField.value.feature, () =>
          setActiveOutputField({
            ...activeOutputField,
            value: {
              ...activeOutputField.value,
              feature: undefined
            }
          })
        )}
      {activeOutputField.value.value &&
        OutputFieldLabel("Value", activeOutputField.value.value, () =>
          setActiveOutputField({
            ...activeOutputField,
            value: {
              ...activeOutputField.value,
              value: undefined
            }
          })
        )}
      {activeOutputField.value.rank &&
        OutputFieldLabel("Rank", activeOutputField.value.rank, () =>
          setActiveOutputField({
            ...activeOutputField,
            value: {
              ...activeOutputField.value,
              rank: undefined
            }
          })
        )}
      {activeOutputField.value.rankOrder &&
        OutputFieldLabel("RankOrder", activeOutputField.value.rankOrder, () =>
          setActiveOutputField({
            ...activeOutputField,
            value: {
              ...activeOutputField.value,
              rankOrder: undefined
            }
          })
        )}
      {activeOutputField.value.segmentId &&
        OutputFieldLabel("SegmentId", activeOutputField.value.segmentId, () =>
          setActiveOutputField({
            ...activeOutputField,
            value: {
              ...activeOutputField.value,
              segmentId: undefined
            }
          })
        )}
      {activeOutputField.value.isFinalResult &&
        OutputFieldLabel("FinalResult", activeOutputField.value.isFinalResult.toString(), () =>
          setActiveOutputField({
            ...activeOutputField,
            value: {
              ...activeOutputField.value,
              isFinalResult: undefined
            }
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
