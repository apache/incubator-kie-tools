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
import { useEffect, useMemo, useState } from "react";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { Form, FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { FormSelect, FormSelectOption } from "@patternfly/react-core/dist/js/components/FormSelect";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { OpType, OutputField, RankOrder, ResultFeature } from "@kie-tools/pmml-editor-marshaller";
import { GenericSelector, GenericSelectorOption } from "../../EditorScorecard/atoms";
import { HelpIcon } from "@patternfly/react-icons/dist/js/icons/help-icon";
import { useValidationRegistry } from "../../../validation";
import { Builder } from "../../../paths";

interface OutputFieldExtendedPropertiesProps {
  modelIndex: number;
  activeOutputFieldIndex?: number;
  activeOutputField: OutputField | undefined;
  targetFields: string[];
  commit: (outputField: Partial<OutputField>) => void;
}

export const OutputFieldExtendedProperties = (props: OutputFieldExtendedPropertiesProps) => {
  const { activeOutputField, activeOutputFieldIndex, modelIndex, targetFields, commit } = props;

  const [optype, setOptype] = useState<OpType | undefined>();
  const [targetField, setTargetField] = useState<string | undefined>();
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

  const targetFieldsOptions = useMemo(() => {
    const options = [...targetFields];
    if (options.length) {
      options.sort().unshift("");
    }
    return options;
  }, [targetFields]);

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
    (_selection) => {
      setOptype(_selection === "" ? undefined : (_selection as OpType));
      commit({
        optype: _selection === "" ? undefined : (_selection as OpType),
      });
    }
  );

  const featureEditor = GenericSelectorEditor(
    "output-feature",
    // See http://dmg.org/pmml/v4-4-1/Output.html#xsdType_RESULT-FEATURE ("Outputs Per Model Type")
    [
      { value: "" },
      { value: "decision", isDisabled: true },
      { value: "predictedValue" },
      { value: "reasonCode" },
      { value: "transformedValue", isDisabled: true },
      { value: "warning" },
    ],
    (feature ?? "").toString(),
    (_selection) => {
      setFeature(_selection === "" ? undefined : (_selection as ResultFeature));
      commit({
        feature: _selection === "" ? undefined : (_selection as ResultFeature),
      });
    }
  );

  const rankOrderEditor = GenericSelectorEditor(
    "output-rankOrder",
    ["", "ascending", "descending"],
    (rankOrder ?? "").toString(),
    (_selection) => {
      setRankOrder(_selection === "" ? undefined : (_selection as RankOrder));
      commit({
        rankOrder: _selection === "" ? undefined : (_selection as RankOrder),
      });
    },
    value !== undefined && value.length > 0
  );

  const isFinalResultEditor = GenericSelectorEditor(
    "output-isFinalResult",
    ["", "true", "false"],
    (isFinalResult ?? "").toString(),
    (_selection) => {
      setIsFinalResult(_selection === "" ? undefined : Boolean(_selection));
      commit({
        isFinalResult: _selection === "" ? undefined : Boolean(_selection),
      });
    }
  );

  const { validationRegistry } = useValidationRegistry();
  const validationsTargetField = useMemo(
    () =>
      validationRegistry.get(
        Builder().forModel(modelIndex).forOutput().forOutputField(activeOutputFieldIndex).forTargetField().build()
      ),
    [modelIndex, activeOutputFieldIndex, activeOutputField]
  );

  return (
    <Form>
      <FormGroup
        label="optype"
        fieldId="output-optype-helper"
        helperText="Indicates the admissible operations on the values."
        className="outputs-container__extended-properties__field"
      >
        {optypeEditor}
      </FormGroup>
      <FormGroup
        label="Target field"
        fieldId="output-targetField-helper"
        helperText={validationsTargetField.length === 0 ? "" : validationsTargetField[0].message}
        className="outputs-container__extended-properties__field"
        validated={validationsTargetField.length === 0 ? "default" : "warning"}
        labelIcon={
          !targetFieldsOptions.length ? (
            <Tooltip content={"There are no Mining Schema fields with target usage type."}>
              <button
                aria-label="More info for Target Field"
                onClick={(e) => e.preventDefault()}
                className="pf-c-form__group-label-help"
              >
                <HelpIcon style={{ color: "var(--pf-global--info-color--100)" }} />
              </button>
            </Tooltip>
          ) : (
            <></>
          )
        }
      >
        <FormSelect
          id="output-targetField"
          value={(targetField ?? "").toString()}
          onChange={(selection) => {
            if (selection !== targetField) {
              setTargetField(selection === "" ? undefined : selection);
              commit({ targetField: selection === "" ? undefined : selection });
            }
          }}
          isDisabled={!targetFieldsOptions.length}
          validated={validationsTargetField.length === 0 ? "default" : "warning"}
        >
          {targetFieldsOptions.map((option, index) => (
            <FormSelectOption value={option} key={index} label={option} />
          ))}
        </FormSelect>
      </FormGroup>
      <FormGroup
        label="Feature"
        fieldId="output-feature-helper"
        helperText="Specifies the value the output field takes from the computed mining result."
        className="outputs-container__extended-properties__field"
        labelIcon={
          <Tooltip content={"Decision and Transformed value are not supported by scorecards"}>
            <button
              aria-label="More info about Feature"
              onClick={(e) => e.preventDefault()}
              className="pf-c-form__group-label-help"
            >
              <HelpIcon style={{ color: "var(--pf-global--info-color--100)" }} />
            </button>
          </Tooltip>
        }
      >
        {featureEditor}
      </FormGroup>
      <FormGroup
        label="Value"
        fieldId="output-value-helper"
        helperText="Used in conjunction with result features referring to specific values."
        className="outputs-container__extended-properties__field"
        labelIcon={
          <Tooltip content={"Value property cannot be used together with Rank property"}>
            <button
              aria-label="More info about Feature"
              onClick={(e) => e.preventDefault()}
              className="pf-c-form__group-label-help"
            >
              <HelpIcon style={{ color: "var(--pf-global--info-color--100)" }} />
            </button>
          </Tooltip>
        }
      >
        <TextInput
          type="text"
          id="output-value"
          name="output-value"
          aria-describedby="output-value-helper"
          autoComplete="off"
          value={(value ?? "").toString()}
          onChange={(e) => setValue(e)}
          onBlur={() =>
            commit({
              value: value === "" ? undefined : value,
            })
          }
          isDisabled={rank !== undefined}
        />
      </FormGroup>
      <FormGroup
        label="Rank"
        fieldId="output-rank-helper"
        helperText="Specifies the rank of the feature value from the mining result that should be selected."
        className="outputs-container__extended-properties__field"
        labelIcon={
          <Tooltip content={"Rank property cannot be used together with Value property"}>
            <button
              aria-label="More info about Feature"
              onClick={(e) => e.preventDefault()}
              className="pf-c-form__group-label-help"
            >
              <HelpIcon style={{ color: "var(--pf-global--info-color--100)" }} />
            </button>
          </Tooltip>
        }
      >
        <TextInput
          type="number"
          id="output-rank"
          name="output-rank"
          aria-describedby="output-rank-helper"
          autoComplete="off"
          value={rank ?? ""}
          onChange={(e) => setRank(toNumber(e))}
          onBlur={() =>
            commit({
              rank: rank,
            })
          }
          isDisabled={value !== undefined && value.length > 0}
        />
      </FormGroup>
      <FormGroup
        label="Rank order"
        fieldId="output-rankOrder-helper"
        helperText="Determines the sorting order when ranking the results."
        className="outputs-container__extended-properties__field"
      >
        {rankOrderEditor}
      </FormGroup>
      <FormGroup
        label="Segment Id"
        fieldId="output-segmentId-helper"
        helperText="Provides an approach to deliver results from Segments."
        className="outputs-container__extended-properties__field"
      >
        <TextInput
          type="text"
          id="output-segmentId"
          name="output-segmentId"
          aria-describedby="output-segmentId-helper"
          autoComplete="off"
          value={segmentId ?? ""}
          onChange={(e) => setSegmentId(e)}
          onBlur={() =>
            commit({
              segmentId: segmentId,
            })
          }
        />
      </FormGroup>
      <FormGroup
        label="Final result?"
        fieldId="output-isFinalResult-helper"
        helperText="Should the field be returned to the user or is only used as input."
        className="outputs-container__extended-properties__field"
      >
        {isFinalResultEditor}
      </FormGroup>
    </Form>
  );
};

const GenericSelectorEditor = (
  id: string,
  items: Array<string | GenericSelectorOption>,
  selection: string,
  onSelect: (_selection: string) => void,
  isDisabled?: boolean
) => {
  return <GenericSelector id={id} items={items} selection={selection} onSelect={onSelect} isDisabled={isDisabled} />;
};
