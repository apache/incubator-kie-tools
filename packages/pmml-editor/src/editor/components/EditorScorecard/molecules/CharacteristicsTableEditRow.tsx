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
import { useCallback, useEffect, useMemo, useState } from "react";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { Stack, StackItem } from "@patternfly/react-core/dist/js/layouts/Stack";
import { Split, SplitItem } from "@patternfly/react-core/dist/js/layouts/Split";
import { FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";

import { HelpIcon } from "@patternfly/react-icons/dist/js/icons/help-icon";
import "./CharacteristicsTableRow.scss";
import "../../EditorScorecard/templates/ScorecardEditorPage.scss";
import { ValidatedType } from "../../../types";
import { AttributesTable, IndexedCharacteristic } from "../organisms";
import useOnclickOutside from "react-cool-onclickoutside";
import { Operation } from "../Operation";
import { Actions } from "../../../reducers";
import { useSelector } from "react-redux";
import { Attribute, Characteristic, Model, PMML, Scorecard } from "@kie-tools/pmml-editor-marshaller";
import { useBatchDispatch, useHistoryService } from "../../../history";
import { useOperation } from "../OperationContext";
import { Builder } from "../../../paths";
import { useValidationRegistry } from "../../../validation";
import { isEqual } from "lodash";
import get = Reflect.get;
import set = Reflect.set;
import { FormHelperText } from "@patternfly/react-core/dist/js/components/Form";
import { HelperText, HelperTextItem } from "@patternfly/react-core/dist/js/components/HelperText";

interface CharacteristicsTableEditRowProps {
  modelIndex: number;
  areReasonCodesUsed: boolean;
  scorecardBaselineScore: number | undefined;
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
    areReasonCodesUsed,
    scorecardBaselineScore,
    characteristic,
    validateCharacteristicName,
    viewAttribute,
    onAddAttribute,
    onCommitAndClose,
    onCommit,
    onCancel,
  } = props;

  const characteristicIndex = characteristic.index;

  const { activeOperation } = useOperation();
  const { service, getCurrentState } = useHistoryService();
  const dispatch = useBatchDispatch(service, getCurrentState);

  const [name, setName] = useState<ValidatedType<string | undefined>>({
    value: undefined,
    valid: true,
  });
  const [reasonCode, setReasonCode] = useState<string | undefined>();
  const [baselineScore, setBaselineScore] = useState<number | undefined>();

  const attributes: Attribute[] = useSelector<PMML, Attribute[]>((state: PMML) => {
    const model: Model | undefined = state.models ? state.models[modelIndex] : undefined;
    if (model instanceof Scorecard && characteristicIndex !== undefined) {
      const scorecard: Scorecard = model as Scorecard;
      const _characteristic: Characteristic | undefined = scorecard.Characteristics.Characteristic[characteristicIndex];
      if (_characteristic) {
        return _characteristic.Attribute;
      }
    }
    return [];
  });

  const isReasonCodeProvidedByAttributes = useMemo(() => {
    return attributes.length > 0 && attributes.every((attribute) => attribute.reasonCode !== undefined);
  }, [attributes]);

  const ref = useOnclickOutside(
    () => {
      if (name?.valid) {
        onCommitAndClose();
      } else {
        onCancel();
      }
    },
    {
      disabled: activeOperation !== Operation.UPDATE_CHARACTERISTIC,
      eventTypes: ["click"],
    }
  );

  useEffect(() => {
    setName({
      value: characteristic?.characteristic.name,
      valid: true,
    });
    setReasonCode(characteristic?.characteristic.reasonCode);
    setBaselineScore(characteristic?.characteristic.baselineScore);
  }, [props]);

  const { validationRegistry } = useValidationRegistry();
  const reasonCodeValidation = useMemo(
    () =>
      validationRegistry.get(
        Builder()
          .forModel(modelIndex)
          .forCharacteristics()
          .forCharacteristic(characteristicIndex)
          .forReasonCode()
          .build()
      ),
    [modelIndex, characteristicIndex, areReasonCodesUsed, characteristic]
  );
  const baselineScoreValidation = useMemo(
    () =>
      validationRegistry.get(
        Builder()
          .forModel(modelIndex)
          .forCharacteristics()
          .forCharacteristic(characteristicIndex)
          .forBaselineScore()
          .build()
      ),
    [modelIndex, characteristicIndex, scorecardBaselineScore, characteristic]
  );

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

  const onDeleteAttribute = useCallback(
    (attributeIndex) => {
      //See https://issues.redhat.com/browse/FAI-443
      //if (window.confirm(`Delete Attribute?`)) {
      dispatch({
        type: Actions.Scorecard_DeleteAttribute,
        payload: {
          modelIndex: modelIndex,
          characteristicIndex: characteristicIndex,
          attributeIndex: attributeIndex,
        },
      });
      //}
    },
    [modelIndex, characteristicIndex]
  );

  const onUpdateAttribute = useCallback(
    (attributeIndex, partial) => {
      const attribute = attributes[attributeIndex];
      const existingPartial: Partial<Attribute> = {};
      Object.keys(partial).forEach((key) => set(existingPartial, key, get(attribute, key)));

      if (!isEqual(partial, existingPartial)) {
        dispatch({
          type: Actions.Scorecard_UpdateAttribute,
          payload: {
            modelIndex: modelIndex,
            characteristicIndex: characteristicIndex,
            attributeIndex: attributeIndex,
            ...attribute,
            ...partial,
          },
        });
      }
    },
    [modelIndex, characteristicIndex, attributes]
  );

  return (
    <article ref={ref} className={"editable-item__inner"} tabIndex={0} data-ouia-component-id="edit-characteristic">
      <Stack hasGutter={true}>
        <StackItem>
          <Split hasGutter={true}>
            <SplitItem>
              <FormGroup
                label="Name"
                isRequired={true}
                fieldId="characteristic-form-name-helper"
                style={{ width: "18em" }}
              >
                <TextInput
                  type="text"
                  id="characteristic-name"
                  name="characteristic-name"
                  aria-describedby="characteristic-name-helper"
                  value={name.value ?? ""}
                  placeholder="Name"
                  validated={name.valid ? "default" : "error"}
                  autoFocus={true}
                  onChange={(_event, e) =>
                    setName({
                      value: e,
                      valid: validateCharacteristicName(e),
                    })
                  }
                  onBlur={() => {
                    if (name?.valid) {
                      onCommit({
                        name: name.value,
                      });
                    } else {
                      setName({
                        value: characteristic.characteristic.name,
                        valid: validateCharacteristicName(characteristic.characteristic.name),
                      });
                    }
                  }}
                  ouiaId="characteristic-name-input"
                />
                {name.valid === false ? (
                  <FormHelperText>
                    <HelperText>
                      <HelperTextItem variant="warning">Name is mandatory and must be unique</HelperTextItem>
                    </HelperText>
                  </FormHelperText>
                ) : (
                  <FormHelperText>
                    <HelperText>
                      <HelperTextItem variant="default"></HelperTextItem>
                    </HelperText>
                  </FormHelperText>
                )}
              </FormGroup>
            </SplitItem>
            <SplitItem>
              <FormGroup
                label="Reason code"
                fieldId="characteristic-reason-code-helper"
                style={{ width: "16em" }}
                labelIcon={
                  <Tooltip
                    content={
                      areReasonCodesUsed && isReasonCodeProvidedByAttributes
                        ? `A Reason code is already provided inside all the Attributes of this Characteristic`
                        : `
                          Reason code is available and required when Use reason codes property inside Model Setup is yes. \
                          You can enter Reason code here or provide a Reason code for all the Attributes of this \
                          characteristic as an alternative.`
                    }
                  >
                    <button
                      aria-label="More information for Reason code"
                      onClick={(e) => e.preventDefault()}
                      className="pf-v5-c-form__group-label-help"
                    >
                      <HelpIcon style={{ color: "var(--pf-v5-global--info-color--100)" }} />
                    </button>
                  </Tooltip>
                }
              >
                <TextInput
                  type="text"
                  id="characteristic-reason-code"
                  name="characteristic-reason-code"
                  aria-describedby="characteristic-reason-code-helper"
                  value={reasonCode ?? ""}
                  onChange={(_event, e) => setReasonCode(e)}
                  onBlur={() => {
                    onCommit({
                      reasonCode: reasonCode === "" ? undefined : reasonCode,
                    });
                  }}
                  validated={reasonCodeValidation.length > 0 ? "warning" : "default"}
                  isDisabled={!areReasonCodesUsed || isReasonCodeProvidedByAttributes}
                  ouiaId="characteristic-reason-code-input"
                />
                {reasonCodeValidation.length > 0 ? (
                  <FormHelperText>
                    <HelperText>
                      <HelperTextItem variant="warning">{reasonCodeValidation[0].message}</HelperTextItem>
                    </HelperText>
                  </FormHelperText>
                ) : (
                  <FormHelperText>
                    <HelperText>
                      <HelperTextItem variant="default"></HelperTextItem>
                    </HelperText>
                  </FormHelperText>
                )}
              </FormGroup>
            </SplitItem>
            <SplitItem isFilled={true}>
              <FormGroup
                label="Baseline score"
                fieldId="characteristic-baseline-score-helper"
                labelIcon={
                  <Tooltip
                    content={
                      areReasonCodesUsed && scorecardBaselineScore !== undefined
                        ? `A baseline score is already provided inside Model Setup`
                        : `
                          Baseline score for Characteristics is required when Use reason codes property is true \
                          and no Baseline score is provided inside Model Setup
                          `
                    }
                  >
                    <button
                      aria-label="More information for Baseline score"
                      onClick={(e) => e.preventDefault()}
                      className="pf-v5-c-form__group-label-help"
                    >
                      <HelpIcon style={{ color: "var(--pf-v5-global--info-color--100)" }} />
                    </button>
                  </Tooltip>
                }
                style={{ width: "16em" }}
              >
                <TextInput
                  type="number"
                  id="characteristic-baseline-score"
                  name="characteristic-baseline-score"
                  aria-describedby="characteristic-baseline-score-helper"
                  value={baselineScore ?? ""}
                  validated={baselineScoreValidation.length > 0 ? "warning" : "default"}
                  onChange={(_event, e) => setBaselineScore(toNumber(e))}
                  onBlur={() => {
                    onCommit({
                      baselineScore: baselineScore,
                    });
                  }}
                  isDisabled={scorecardBaselineScore !== undefined}
                  ouiaId="characteristic-baseline-score-input"
                />
                {baselineScoreValidation.length > 0 ? (
                  <FormHelperText>
                    <HelperText>
                      <HelperTextItem variant="warning">{baselineScoreValidation[0].message}</HelperTextItem>
                    </HelperText>
                  </FormHelperText>
                ) : (
                  <FormHelperText>
                    <HelperText>
                      <HelperTextItem variant="default"></HelperTextItem>
                    </HelperText>
                  </FormHelperText>
                )}
              </FormGroup>
            </SplitItem>

            <SplitItem>
              <Button id="add-attribute-button" variant="primary" onClick={onAddAttribute} ouiaId="add-attribute">
                Add Attribute
              </Button>
            </SplitItem>
          </Split>
        </StackItem>
        {attributes.length > 0 && (
          <StackItem>
            <FormGroup label="Attributes" fieldId="output-labels-helper">
              <AttributesTable
                modelIndex={modelIndex}
                characteristicIndex={characteristicIndex}
                characteristic={characteristic.characteristic}
                areReasonCodesUsed={areReasonCodesUsed}
                viewAttribute={viewAttribute}
                deleteAttribute={onDeleteAttribute}
                onCommit={onUpdateAttribute}
              />
            </FormGroup>
          </StackItem>
        )}
      </Stack>
    </article>
  );
};
