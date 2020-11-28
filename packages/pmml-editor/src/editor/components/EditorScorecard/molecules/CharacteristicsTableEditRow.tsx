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
import { Button, FormGroup, Split, SplitItem, Stack, StackItem, TextInput } from "@patternfly/react-core";
import { ExclamationCircleIcon } from "@patternfly/react-icons";
import "./CharacteristicsTableRow.scss";
import "../../EditorScorecard/templates/ScorecardEditorPage.scss";
import { ValidatedType } from "../../../types";
import { AttributesTable, IndexedCharacteristic } from "../organisms";
import useOnclickOutside from "react-cool-onclickoutside";
import { Operation } from "../Operation";
import { Actions } from "../../../reducers";
import { useDispatch } from "react-redux";
import { Characteristic } from "@kogito-tooling/pmml-editor-marshaller";

interface CharacteristicsTableEditRowProps {
  modelIndex: number;
  activeOperation: Operation;
  setActiveOperation: (operation: Operation) => void;
  useReasonCodes: boolean;
  isBaselineScoreRequired: boolean;
  characteristic: IndexedCharacteristic;
  validateCharacteristicName: (name: string | undefined) => boolean;
  viewAttribute: (index: number | undefined) => void;
  onAddAttribute: () => void;
  onCommitAndClose: () => void;
  onCommit: (partial: Partial<Characteristic>) => void;
  onCancel: () => void;
}

export const CharacteristicsTableEditRow = (props: CharacteristicsTableEditRowProps) => {
  const {
    modelIndex,
    activeOperation,
    setActiveOperation,
    useReasonCodes,
    isBaselineScoreRequired,
    characteristic,
    validateCharacteristicName,
    viewAttribute,
    onAddAttribute,
    onCommitAndClose,
    onCommit,
    onCancel
  } = props;

  const characteristicIndex = characteristic.index;

  const dispatch = useDispatch();

  const [name, setName] = useState<ValidatedType<string | undefined>>({
    value: undefined,
    valid: true
  });
  const [reasonCode, setReasonCode] = useState<string | undefined>();
  const [baselineScore, setBaselineScore] = useState<ValidatedType<number | undefined>>({
    value: undefined,
    valid: true
  });

  const ref = useOnclickOutside(
    event => {
      if (name?.valid && baselineScore?.valid) {
        onCommitAndClose();
      } else {
        onCancel();
      }
    },
    {
      disabled: activeOperation !== Operation.UPDATE_CHARACTERISTIC,
      eventTypes: ["click"]
    }
  );

  useEffect(() => {
    setName({
      value: characteristic?.characteristic.name,
      valid: true
    });
    setReasonCode(characteristic?.characteristic.reasonCode);
    setBaselineScore({
      value: characteristic?.characteristic.baselineScore,
      valid:
        (isBaselineScoreRequired && characteristic?.characteristic.baselineScore !== undefined) ||
        (!isBaselineScoreRequired && characteristic?.characteristic.baselineScore === undefined)
    });
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
      className={`characteristic-item characteristic-item-n${characteristicIndex} editable editing`}
      tabIndex={0}
      onKeyDown={e => {
        if (e.key === "Escape") {
          onCancel();
        }
      }}
    >
      <Stack hasGutter={true}>
        <StackItem>
          <Split hasGutter={true}>
            <SplitItem isFilled={!useReasonCodes}>
              <FormGroup
                label="Name"
                isRequired={true}
                fieldId="characteristic-form-name-helper"
                helperTextInvalid="Name must be unique and present"
                helperTextInvalidIcon={<ExclamationCircleIcon />}
                validated={name.valid ? "default" : "error"}
                style={!useReasonCodes ? { width: "16em" } : {}}
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
                  onBlur={e => {
                    if (name?.valid) {
                      onCommit({
                        name: name.value
                      });
                    }
                  }}
                />
              </FormGroup>
            </SplitItem>
            {useReasonCodes && (
              <>
                <SplitItem>
                  <FormGroup label="Reason code" fieldId="characteristic-reason-code-helper">
                    <TextInput
                      type="text"
                      id="characteristic-reason-code"
                      name="characteristic-reason-code"
                      aria-describedby="characteristic-reason-code-helper"
                      value={reasonCode ?? ""}
                      onChange={e => setReasonCode(e)}
                      onBlur={e => {
                        onCommit({
                          reasonCode: reasonCode
                        });
                      }}
                    />
                  </FormGroup>
                </SplitItem>
                <SplitItem isFilled={true}>
                  <FormGroup
                    label="Baseline score"
                    fieldId="characteristic-baseline-score-helper"
                    required={isBaselineScoreRequired}
                    helperTextInvalid={
                      isBaselineScoreRequired && baselineScore.value === undefined
                        ? "Baseline score required"
                        : !isBaselineScoreRequired && baselineScore.value !== undefined
                        ? "Baseline score is not required"
                        : ""
                    }
                    helperTextInvalidIcon={<ExclamationCircleIcon />}
                    validated={baselineScore.valid ? "default" : "error"}
                    style={{ width: "16em" }}
                  >
                    <TextInput
                      type="number"
                      id="characteristic-baseline-score"
                      name="characteristic-baseline-score"
                      aria-describedby="characteristic-baseline-score-helper"
                      value={baselineScore.value ?? ""}
                      validated={baselineScore.valid ? "default" : "error"}
                      onChange={e =>
                        setBaselineScore({
                          value: toNumber(e),
                          valid:
                            (isBaselineScoreRequired && toNumber(e) !== undefined) ||
                            (!isBaselineScoreRequired && toNumber(e) === undefined)
                        })
                      }
                      onBlur={e => {
                        if (baselineScore?.valid) {
                          onCommit({
                            baselineScore: baselineScore.value
                          });
                        }
                      }}
                    />
                  </FormGroup>
                </SplitItem>
              </>
            )}
            <SplitItem>
              <Button id="add-attribute-button" variant="primary" onClick={e => onAddAttribute()}>
                Add Attribute
              </Button>
            </SplitItem>
          </Split>
        </StackItem>
        <StackItem>
          <FormGroup label="Attributes" fieldId="output-labels-helper">
            <AttributesTable
              modelIndex={modelIndex}
              characteristicIndex={characteristicIndex}
              setActiveOperation={setActiveOperation}
              viewAttribute={viewAttribute}
              deleteAttribute={attributeIndex => {
                if (window.confirm(`Delete Attribute "${attributeIndex}"?`)) {
                  dispatch({
                    type: Actions.Scorecard_DeleteAttribute,
                    payload: {
                      modelIndex: modelIndex,
                      characteristicIndex: characteristicIndex,
                      attributeIndex: attributeIndex
                    }
                  });
                }
              }}
            />
          </FormGroup>
        </StackItem>
      </Stack>
    </article>
  );
};
