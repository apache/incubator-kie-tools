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
  Label,
  TextInput
} from "@patternfly/react-core";
import { ExclamationCircleIcon } from "@patternfly/react-icons";
import "../organisms/CharacteristicsTable.scss";
import { CharacteristicsTableEditModeAction } from "../atoms";
import { ValidatedType } from "../../../types";
import { IndexedCharacteristic } from "../organisms";

interface CharacteristicsTableEditRowProps {
  characteristic: IndexedCharacteristic;
  validateCharacteristicName: (name: string | undefined) => boolean;
  onCommit: (name: string | undefined, reasonCode: string | undefined, baselineScore: number | undefined) => void;
  onCancel: () => void;
}

export const CharacteristicsTableEditRow = (props: CharacteristicsTableEditRowProps) => {
  const { characteristic, onCommit, onCancel } = props;

  const index = characteristic.index;

  const [name, setName] = useState<ValidatedType<string | undefined>>({
    value: undefined,
    valid: true
  });
  const [reasonCode, setReasonCode] = useState<string | undefined>();
  const [baselineScore, setBaselineScore] = useState<number | undefined>();

  useEffect(() => {
    setName({
      value: characteristic?.characteristic.name,
      valid: props.validateCharacteristicName(characteristic?.characteristic.name)
    });
    setReasonCode(characteristic?.characteristic.reasonCode);
    setBaselineScore(characteristic?.characteristic.baselineScore);
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
    <DataListItem
      id={characteristic.index?.toString()}
      className="characteristics__list-item"
      aria-labelledby={"characteristic-" + index}
    >
      <DataListItemRow style={{ minHeight: "64 px" }}>
        <DataListItemCells
          dataListCells={[
            <DataListCell key="0" width={2}>
              <FormGroup
                fieldId="characteristic-form-name-helper"
                helperText="Please provide a name for the Characteristic."
                helperTextInvalid="Name must be unique and present"
                helperTextInvalidIcon={<ExclamationCircleIcon />}
                validated={name.valid ? "default" : "error"}
              >
                <TextInput
                  type="text"
                  id="characteristic-name"
                  name="characteristic-name"
                  aria-describedby="characteristic-name-helper"
                  value={name.value ?? ""}
                  validated={name.valid ? "default" : "error"}
                  autoFocus={true}
                  onChange={e =>
                    setName({
                      value: e,
                      valid: props.validateCharacteristicName(e)
                    })
                  }
                />
              </FormGroup>
            </DataListCell>,
            <DataListCell key="1" width={2}>
              <Label>{characteristic.characteristic.Attribute.length}</Label>
            </DataListCell>,
            <DataListCell key="2" width={2}>
              <FormGroup
                fieldId="characteristic-reason-code-helper"
                helperText="A Reason Code is mapped to a Business reason."
              >
                <TextInput
                  type="text"
                  id="characteristic-reason-code"
                  name="characteristic-reason-code"
                  aria-describedby="characteristic-reason-code-helper"
                  value={reasonCode ?? ""}
                  onChange={e => setReasonCode(e)}
                />
              </FormGroup>
            </DataListCell>,
            <DataListCell key="3" width={2}>
              <FormGroup
                fieldId="characteristic-baseline-score-helper"
                helperText="Helps to determine the ranking of Reason Codes."
              >
                <TextInput
                  type="number"
                  id="characteristic-baseline-score"
                  name="characteristic-baseline-score"
                  aria-describedby="characteristic-baseline-score-helper"
                  value={baselineScore ?? ""}
                  onChange={e => setBaselineScore(toNumber(e))}
                />
              </FormGroup>
            </DataListCell>,
            <DataListAction
              id="characteristic-actions"
              aria-label="actions"
              aria-labelledby="characteristic-actions"
              key="4"
              width={1}
            >
              <CharacteristicsTableEditModeAction
                onCommit={() => onCommit(name.value, reasonCode, baselineScore)}
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
