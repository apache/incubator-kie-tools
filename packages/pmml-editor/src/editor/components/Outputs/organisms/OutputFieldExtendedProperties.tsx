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
import { useEffect, useState } from "react";
import { Form, FormGroup, TextInput } from "@patternfly/react-core";
import "../organisms/OutputFieldsTable.scss";
import { FieldName, OpType, OutputField, RankOrder, ResultFeature } from "@redhat/pmml-editor-marshaller";
import { GenericSelector } from "../../EditorScorecard/atoms";

interface OutputFieldExtendedPropertiesProps {
  activeOutputField: OutputField | undefined;
  commit: (outputField: Partial<OutputField>) => void;
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
  const { activeOutputField, commit } = props;

  const [optype, setOptype] = useState<OpType | undefined>();
  const [targetField, setTargetField] = useState<FieldName | undefined>();
  const [feature, setFeature] = useState<ResultFeature | undefined>();
  const [value, setValue] = useState<any | undefined>();
  const [rank, setRank] = useState<number | undefined>();
  const [rankOrder, setRankOrder] = useState<RankOrder | undefined>();
  const [segmentId, setSegmentId] = useState<string | undefined>();
  const [isFinalResult, setIsFinalResult] = useState<boolean | undefined>();

  useEffect(() => {
    if (activeOutputField === undefined) {
      return;
    }
    setOptype(activeOutputField.optype);
    setTargetField(activeOutputField.targetField);
    setFeature(activeOutputField.feature);
    setValue(activeOutputField.value);
    setRank(activeOutputField.rank);
    setRankOrder(activeOutputField.rankOrder);
    setSegmentId(activeOutputField.segmentId);
    setIsFinalResult(activeOutputField.isFinalResult);
  }, [props]);

  const toNumber = (_value: string): number | undefined => {
    if (_value === "") {
      return undefined;
    }
    const n = Number(_value);
    if (isNaN(n)) {
      return undefined;
    }
    return n;
  };

  const optypeEditor = GenericSelectorEditor(
    "output-optype",
    ["", "categorical", "continuous", "ordinal"],
    (optype ?? "").toString(),
    _selection => {
      setOptype(_selection === "" ? undefined : (_selection as OpType));
      commit({
        optype: _selection === "" ? undefined : (_selection as OpType)
      });
    }
  );

  const featureEditor = GenericSelectorEditor(
    "output-feature",
    // See http://dmg.org/pmml/v4-4-1/Output.html#xsdType_RESULT-FEATURE ("Outputs Per Model Type")
    ["", "decision", "predictedValue", "reasonCode", "transformedValue", "warning"],
    (feature ?? "").toString(),
    _selection => {
      setFeature(_selection === "" ? undefined : (_selection as ResultFeature));
      commit({
        feature: _selection === "" ? undefined : (_selection as ResultFeature)
      });
    }
  );

  const rankOrderEditor = GenericSelectorEditor(
    "output-rankOrder",
    ["", "ascending", "descending"],
    (rankOrder ?? "").toString(),
    _selection => {
      setRankOrder(_selection === "" ? undefined : (_selection as RankOrder));
      commit({
        rankOrder: _selection === "" ? undefined : (_selection as RankOrder)
      });
    }
  );

  const isFinalResultEditor = GenericSelectorEditor(
    "output-isFinalResult",
    ["", "true", "false"],
    (isFinalResult ?? "").toString(),
    _selection => {
      setIsFinalResult(_selection === "" ? undefined : Boolean(_selection));
      commit({
        isFinalResult: _selection === "" ? undefined : Boolean(_selection)
      });
    }
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
          value={(targetField ?? "").toString()}
          onChange={e => setTargetField(e as FieldName)}
          onBlur={e =>
            commit({
              targetField: targetField
            })
          }
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
          value={(value ?? "").toString()}
          onChange={e => setValue(e)}
          onBlur={e =>
            commit({
              value: value
            })
          }
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
          value={rank ?? ""}
          onChange={e => setRank(toNumber(e))}
          onBlur={e =>
            commit({
              rank: rank
            })
          }
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
          value={segmentId ?? ""}
          onChange={e => setSegmentId(e)}
          onBlur={e =>
            commit({
              segmentId: segmentId
            })
          }
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
