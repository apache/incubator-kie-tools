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
import {
  DataListAction,
  DataListCell,
  DataListItem,
  DataListItemCells,
  DataListItemRow,
  FormGroup,
  TextInput
} from "@patternfly/react-core";
import "../organisms/OutputsTable.scss";
import { OutputField } from "@kogito-tooling/pmml-editor-marshaller";
import { OutputsTableEditModeAction } from "../atoms";
import { ValidatedType } from "../../../types";
import { ExclamationCircleIcon } from "@patternfly/react-icons";

interface OutputsTableEditRowProps {
  index: number | undefined;
  output: OutputField;
  validateName: (name: string) => boolean;
  onCommit: (text: string | undefined, dataType: string | undefined) => void;
  onCancel: () => void;
}

export const OutputsTableEditRow = (props: OutputsTableEditRowProps) => {
  const { index, output, validateName, onCommit, onCancel } = props;

  const [name, setName] = useState<ValidatedType<string | undefined>>({
    value: undefined,
    valid: true
  });
  const [optype, setOptype] = useState<string | undefined>();
  const [dataType, setDataType] = useState<string | undefined>();
  const [targetField, setTargetField] = useState<string | undefined>();
  const [feature, setFeature] = useState<string | undefined>();
  const [value, setValue] = useState<string | undefined>();
  const [rank, setRank] = useState<number | undefined>();
  const [rankOrder, setRankOrder] = useState<string | undefined>();
  const [segmentId, setSegmentId] = useState<string | undefined>();
  const [isFinalResult, setIsFinalResult] = useState<boolean | undefined>();

  useEffect(() => {
    const _name = output.name.toString();
    setName({
      value: _name,
      valid: validateName(_name)
    });
    setOptype(output.optype);
    setDataType(output.dataType);
    setTargetField((output?.targetField ?? "").toString());
    setFeature(output.feature);
    setValue(output.value);
    setRank(output.rank);
    setRankOrder(output.rankOrder);
    setSegmentId(output.segmentId);
    setIsFinalResult(output.isFinalResult);
  }, [props]);

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

  return (
    <DataListItem id={index?.toString()} className="outputs__list-item" aria-labelledby={"output-" + index}>
      <DataListItemRow>
        <DataListItemCells
          dataListCells={[
            <DataListCell key="0" width={4}>
              <FormGroup
                fieldId="output-name-helper"
                helperText="Please provide a name for the Output Field."
                helperTextInvalid="Name must be unique and present."
                helperTextInvalidIcon={<ExclamationCircleIcon />}
                validated={name.valid ? "default" : "error"}
              >
                <TextInput
                  type="text"
                  id="output-name"
                  name="output-name"
                  aria-describedby="output-name-helper"
                  value={(name.value ?? "").toString()}
                  validated={name.valid ? "default" : "error"}
                  autoFocus={true}
                  onChange={e =>
                    setName({
                      value: e,
                      valid: validateName(e)
                    })
                  }
                />
              </FormGroup>
            </DataListCell>,
            <DataListCell key="1" width={2}>
              <FormGroup fieldId="output-optype-helper" helperText="Indicates the admissible operations on the values.">
                <TextInput
                  type="text"
                  id="output-optype"
                  name="output-optype"
                  aria-describedby="output-optype-helper"
                  value={optype}
                  onChange={e => setOptype(e)}
                />
              </FormGroup>
            </DataListCell>,
            <DataListCell key="2" width={2}>
              <FormGroup fieldId="output-dataType-helper" helperText="A Reason Code is mapped to a Business reason.">
                <TextInput
                  type="text"
                  id="output-dataType"
                  name="output-dataType"
                  aria-describedby="output-dataType-helper"
                  value={dataType}
                  onChange={e => setDataType(e)}
                />
              </FormGroup>
            </DataListCell>,

            <DataListCell key="3" width={2}>
              <FormGroup fieldId="output-targetField-helper" helperText="Target field for the Output field.">
                <TextInput
                  type="text"
                  id="output-targetField"
                  name="output-targetField"
                  aria-describedby="output-targetField-helper"
                  value={targetField}
                  onChange={e => setTargetField(e)}
                />
              </FormGroup>
            </DataListCell>,
            <DataListCell key="4" width={2}>
              <FormGroup
                fieldId="output-feature-helper"
                helperText="Specifies the value the output field takes from the computed mining result."
              >
                <TextInput
                  type="text"
                  id="output-feature"
                  name="output-v"
                  aria-describedby="output-feature-helper"
                  value={feature}
                  onChange={e => setFeature(e)}
                />
              </FormGroup>
            </DataListCell>,
            <DataListCell key="5" width={2}>
              <FormGroup
                fieldId="output-value-helper"
                helperText="Used in conjunction with result features referring to specific values."
              >
                <TextInput
                  type="text"
                  id="output-value"
                  name="output-value"
                  aria-describedby="output-value-helper"
                  value={value}
                  onChange={e => setValue(e)}
                />
              </FormGroup>
            </DataListCell>,
            <DataListCell key="6" width={2}>
              <FormGroup
                fieldId="output-rank-helper"
                helperText="Specifies the rank of the feature value from the mining result that should be selected."
              >
                <TextInput
                  type="text"
                  id="output-rank"
                  name="output-rank"
                  aria-describedby="output-rank-helper"
                  value={rank}
                  onChange={e => setRank(toNumber(e))}
                />
              </FormGroup>
            </DataListCell>,
            <DataListCell key="7" width={2}>
              <FormGroup
                fieldId="output-rankOrder-helper"
                helperText="Determines the sorting order when ranking the results."
              >
                <TextInput
                  type="text"
                  id="output-rankOrder"
                  name="output-rankOrder"
                  aria-describedby="output-rankOrder-helper"
                  value={rankOrder}
                  onChange={e => setRankOrder(e)}
                />
              </FormGroup>
            </DataListCell>,
            <DataListCell key="8" width={2}>
              <FormGroup
                fieldId="output-segmentId-helper"
                helperText="Provides an approach to deliver results from Segments."
              >
                <TextInput
                  type="text"
                  id="output-segmentId"
                  name="output-segmentId"
                  aria-describedby="output-segmentId-helper"
                  value={segmentId}
                  onChange={e => setSegmentId(e)}
                />
              </FormGroup>
            </DataListCell>,
            <DataListCell key="9" width={2}>
              <FormGroup
                fieldId="output-isFinalResult-helper"
                helperText="A Reason Code is mapped to a Business reason."
              >
                <TextInput
                  type="text"
                  id="output-isFinalResult"
                  name="output-isFinalResult"
                  aria-describedby="output-isFinalResult-helper"
                  value={isFinalResult?.toString()}
                  onChange={e => setIsFinalResult(Boolean(e))}
                />
              </FormGroup>
            </DataListCell>,

            <DataListAction id="delete-output" aria-label="delete" aria-labelledby="delete-output" key="11" width={1}>
              <OutputsTableEditModeAction
                onCommit={() => onCommit(name.value, dataType)}
                onCancel={() => onCancel()}
                disableCommit={!name.valid}
              />
            </DataListAction>
          ]}
        />
      </DataListItemRow>
    </DataListItem>
  );
};
