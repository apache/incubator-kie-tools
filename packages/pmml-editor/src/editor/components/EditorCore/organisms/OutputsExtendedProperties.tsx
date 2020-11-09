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
import "../organisms/OutputsTable.scss";
import { OutputField } from "@kogito-tooling/pmml-editor-marshaller";

interface OutputsExtendedPropertiesProps {
  activeOutputField: OutputField;
  setActiveOutputField: (_output: OutputField) => void;
}

export const OutputsExtendedProperties = (props: OutputsExtendedPropertiesProps) => {
  const { activeOutputField } = props;

  return (
    <Form>
      <FormGroup fieldId="output-optype-helper" helperText="Indicates the admissible operations on the values.">
        <TextInput
          type="text"
          id="output-optype"
          name="output-optype"
          aria-describedby="output-optype-helper"
          value={activeOutputField.optype ?? ""}
          // onChange={e => setOptype(e)}
        />
      </FormGroup>
      <FormGroup fieldId="output-targetField-helper" helperText="Target field for the Output field.">
        <TextInput
          type="text"
          id="output-targetField"
          name="output-targetField"
          aria-describedby="output-targetField-helper"
          value={(activeOutputField.targetField ?? "").toString()}
          // onChange={e => setTargetField(e)}
        />
      </FormGroup>
      <FormGroup
        fieldId="output-feature-helper"
        helperText="Specifies the value the output field takes from the computed mining result."
      >
        <TextInput
          type="text"
          id="output-feature"
          name="output-v"
          aria-describedby="output-feature-helper"
          value={activeOutputField.feature}
          // onChange={e => setFeature(e)}
        />
      </FormGroup>
      <FormGroup
        fieldId="output-value-helper"
        helperText="Used in conjunction with result features referring to specific values."
      >
        <TextInput
          type="text"
          id="output-value"
          name="output-value"
          aria-describedby="output-value-helper"
          value={(activeOutputField.value ?? "").toString()}
          // onChange={e => setValue(e)}
        />
      </FormGroup>
      <FormGroup
        fieldId="output-rank-helper"
        helperText="Specifies the rank of the feature value from the mining result that should be selected."
      >
        <TextInput
          type="number"
          id="output-rank"
          name="output-rank"
          aria-describedby="output-rank-helper"
          value={activeOutputField.rank}
          // onChange={e => setRank(toNumber(e))}
        />
      </FormGroup>
      <FormGroup fieldId="output-rankOrder-helper" helperText="Determines the sorting order when ranking the results.">
        <TextInput
          type="text"
          id="output-rankOrder"
          name="output-rankOrder"
          aria-describedby="output-rankOrder-helper"
          value={activeOutputField.rankOrder}
          // onChange={e => setRankOrder(e)}
        />
      </FormGroup>
      <FormGroup fieldId="output-segmentId-helper" helperText="Provides an approach to deliver results from Segments.">
        <TextInput
          type="text"
          id="output-segmentId"
          name="output-segmentId"
          aria-describedby="output-segmentId-helper"
          value={activeOutputField.segmentId}
          // onChange={e => setSegmentId(e)}
        />
      </FormGroup>
      <FormGroup fieldId="output-isFinalResult-helper" helperText="A Reason Code is mapped to a Business reason.">
        <TextInput
          type="text"
          id="output-isFinalResult"
          name="output-isFinalResult"
          aria-describedby="output-isFinalResult-helper"
          value={activeOutputField.isFinalResult?.toString()}
          // onChange={e => setIsFinalResult(Boolean(e))}
        />
      </FormGroup>
    </Form>
  );
};
