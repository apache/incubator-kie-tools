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
import { Form, FormGroup, TextInput } from "@patternfly/react-core";
import "../organisms/OutputFieldsTable.scss";
import { FieldName, OpType, OutputField, RankOrder, ResultFeature } from "@kogito-tooling/pmml-editor-marshaller";
import { GenericSelector } from "../../EditorScorecard/atoms";

interface OutputFieldExtendedPropertiesProps {
  activeOutputField: OutputField;
  setActiveOutputField: (_output: OutputField) => void;
}

const GenericSelectorEditor = (
  id: string,
  items: string[],
  selection: string,
  onSelect: (_selection: string) => void
) => {
  return <GenericSelector id={id} items={items} selection={selection} onSelect={onSelect} />;
};

export const OutputFieldExtendedProperties = (props: OutputFieldExtendedPropertiesProps) => {
  const { activeOutputField, setActiveOutputField } = props;

  const toNumber = (value: string): number | undefined => {
    if (value === "") {
      return undefined;
    }
    const n = Number(value);
    if (isNaN(n)) {
      return undefined;
    }
    return n;
  };

  const optypeEditor = GenericSelectorEditor(
    "output-optype",
    ["", "categorical", "continuous", "ordinal"],
    (activeOutputField.optype ?? "").toString(),
    _selection =>
      setActiveOutputField({
        ...activeOutputField,
        optype: _selection === "" ? undefined : (_selection as OpType)
      })
  );

  const featureEditor = GenericSelectorEditor(
    "output-feature",
    [
      "",
      "affinity",
      "antecedent",
      "clusterAffinity",
      "clusterId",
      "confidence",
      "consequent",
      "decision",
      "entityAffinity",
      "entityId",
      "leverage",
      "lift",
      "predictedDisplayValue",
      "predictedValue",
      "probability",
      "reasonCode",
      "residual",
      "rule",
      "ruleId",
      "ruleValue",
      "standardDeviation",
      "standardError",
      "support",
      "transformedValue",
      "warning"
    ],
    (activeOutputField.feature ?? "").toString(),
    _selection =>
      setActiveOutputField({
        ...activeOutputField,
        feature: _selection === "" ? undefined : (_selection as ResultFeature)
      })
  );

  const rankOrderEditor = GenericSelectorEditor(
    "output-rankOrder",
    ["", "ascending", "descending"],
    (activeOutputField.rankOrder ?? "").toString(),
    _selection =>
      setActiveOutputField({
        ...activeOutputField,
        rankOrder: _selection === "" ? undefined : (_selection as RankOrder)
      })
  );

  const isFinalResultEditor = GenericSelectorEditor(
    "output-isFinalResult",
    ["", "true", "false"],
    (activeOutputField.isFinalResult ?? "").toString(),
    _selection =>
      setActiveOutputField({
        ...activeOutputField,
        isFinalResult: _selection === "" ? undefined : Boolean(_selection)
      })
  );

  return (
    <Form>
      <FormGroup
        label="optype"
        fieldId="output-optype-helper"
        helperText="Indicates the admissible operations on the values."
      >
        {optypeEditor}
      </FormGroup>
      <FormGroup
        label="Target field"
        fieldId="output-targetField-helper"
        helperText="Target field for the Output field."
      >
        <TextInput
          type="text"
          id="output-targetField"
          name="output-targetField"
          aria-describedby="output-targetField-helper"
          value={(activeOutputField.targetField ?? "").toString()}
          onChange={e => setActiveOutputField({ ...activeOutputField, targetField: e as FieldName })}
        />
      </FormGroup>
      <FormGroup
        label="Feature"
        fieldId="output-feature-helper"
        helperText="Specifies the value the output field takes from the computed mining result."
      >
        {featureEditor}
      </FormGroup>
      <FormGroup
        label="Value"
        fieldId="output-value-helper"
        helperText="Used in conjunction with result features referring to specific values."
      >
        <TextInput
          type="text"
          id="output-value"
          name="output-value"
          aria-describedby="output-value-helper"
          value={(activeOutputField.value ?? "").toString()}
          onChange={e => setActiveOutputField({ ...activeOutputField, value: e })}
        />
      </FormGroup>
      <FormGroup
        label="Rank"
        fieldId="output-rank-helper"
        helperText="Specifies the rank of the feature value from the mining result that should be selected."
      >
        <TextInput
          type="number"
          id="output-rank"
          name="output-rank"
          aria-describedby="output-rank-helper"
          value={activeOutputField.rank}
          onChange={e => setActiveOutputField({ ...activeOutputField, rank: toNumber(e) })}
        />
      </FormGroup>
      <FormGroup
        label="Rank order"
        fieldId="output-rankOrder-helper"
        helperText="Determines the sorting order when ranking the results."
      >
        {rankOrderEditor}
      </FormGroup>
      <FormGroup
        label="Segment Id"
        fieldId="output-segmentId-helper"
        helperText="Provides an approach to deliver results from Segments."
      >
        <TextInput
          type="text"
          id="output-segmentId"
          name="output-segmentId"
          aria-describedby="output-segmentId-helper"
          value={activeOutputField.segmentId}
          onChange={e => setActiveOutputField({ ...activeOutputField, segmentId: e })}
        />
      </FormGroup>
      <FormGroup
        label="Final result?"
        fieldId="output-isFinalResult-helper"
        helperText="Should the field be returned to the user or is only used as input."
      >
        {isFinalResultEditor}
      </FormGroup>
    </Form>
  );
};
