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
import { FormGroup, Split, SplitItem, TextInput } from "@patternfly/react-core";
import { ExclamationCircleIcon } from "@patternfly/react-icons";
import { CharacteristicLabelsEditMode, CharacteristicsTableAction } from "../atoms";
import "./CharacteristicsTableRow.scss";
import "../../EditorScorecard/templates/ScorecardEditorPage.scss";
import { ValidatedType } from "../../../types";
import { IndexedCharacteristic } from "../organisms";
import useOnclickOutside from "react-cool-onclickoutside";
import { Operation } from "../Operation";

interface CharacteristicsTableEditRowProps {
  activeOperation: Operation;
  setActiveOperation: (operation: Operation) => void;
  characteristic: IndexedCharacteristic;
  validateCharacteristicName: (name: string | undefined) => boolean;
  viewAttributes: () => void;
  onCommit: (name: string | undefined, reasonCode: string | undefined, baselineScore: number | undefined) => void;
  onCancel: () => void;
  onDelete?: () => void;
}

export const CharacteristicsTableEditRow = (props: CharacteristicsTableEditRowProps) => {
  const {
    activeOperation,
    setActiveOperation,
    characteristic,
    viewAttributes,
    validateCharacteristicName,
    onCommit,
    onCancel,
    onDelete
  } = props;

  const index = characteristic.index;

  const [name, setName] = useState<ValidatedType<string | undefined>>({
    value: undefined,
    valid: true
  });
  const [reasonCode, setReasonCode] = useState<string | undefined>();
  const [baselineScore, setBaselineScore] = useState<number | undefined>();

  const ref = useOnclickOutside(
    event => {
      if (name.valid) {
        onCommit(name.value, reasonCode, baselineScore);
      } else {
        onCancel();
      }
    },
    { disabled: activeOperation !== Operation.UPDATE_CHARACTERISTIC }
  );

  useEffect(() => {
    setName({
      value: characteristic?.characteristic.name,
      valid: true
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
    <article
      ref={ref}
      className={`characteristic-item characteristic-item-n${index} editable editing`}
      tabIndex={0}
      onKeyDown={e => {
        if (e.key === "Escape") {
          onCancel();
        }
      }}
    >
      <Split hasGutter={true}>
        <SplitItem>
          <FormGroup
            label="Name"
            isRequired={true}
            fieldId="characteristic-form-name-helper"
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
                  valid: validateCharacteristicName(e)
                })
              }
            />
          </FormGroup>
        </SplitItem>
        <SplitItem>
          <FormGroup label="Reason code" fieldId="characteristic-reason-code-helper">
            <TextInput
              type="text"
              id="characteristic-reason-code"
              name="characteristic-reason-code"
              aria-describedby="characteristic-reason-code-helper"
              value={reasonCode ?? ""}
              onChange={e => setReasonCode(e)}
            />
          </FormGroup>
        </SplitItem>
        <SplitItem>
          <FormGroup label="Baseline score" fieldId="characteristic-baseline-score-helper" style={{ width: "12em" }}>
            <TextInput
              type="number"
              id="characteristic-baseline-score"
              name="characteristic-baseline-score"
              aria-describedby="characteristic-baseline-score-helper"
              value={baselineScore ?? ""}
              onChange={e => setBaselineScore(toNumber(e))}
            />
          </FormGroup>
        </SplitItem>
        <SplitItem isFilled={true}>
          <CharacteristicLabelsEditMode
            viewAttributes={() => {
              onCommit(name.value, reasonCode, baselineScore);
              setActiveOperation(Operation.NONE);
              viewAttributes();
            }}
          />
        </SplitItem>
        {onDelete && (
          <SplitItem>
            <CharacteristicsTableAction onDelete={onDelete} />
          </SplitItem>
        )}
      </Split>
    </article>
  );
};
