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
import { Stack, StackItem } from "@patternfly/react-core/dist/js/layouts/Stack";
import { Split, SplitItem } from "@patternfly/react-core/dist/js/layouts/Split";
import { Form, FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import {
  Attribute,
  Characteristic,
  DataField,
  Model,
  PMML,
  Predicate,
  Scorecard,
} from "@kie-tools/pmml-editor-marshaller";
import { HelpIcon } from "@patternfly/react-icons/dist/js/icons/help-icon";
import { PredicateEditor } from "./PredicateEditor";
import { Operation } from "../Operation";
import { useSelector } from "react-redux";
import { isEqual } from "lodash";
import useOnclickOutside from "react-cool-onclickoutside";
import { useOperation } from "../OperationContext";
import { fromText, toText } from "../organisms";
import { useValidationRegistry } from "../../../validation";
import { Builder } from "../../../paths";
import "./AttributeEditor.scss";
import { ValidationIndicator } from "../../EditorCore/atoms";
import set = Reflect.set;
import get = Reflect.get;
import { FormHelperText } from "@patternfly/react-core/dist/js/components/Form";
import { HelperText, HelperTextItem } from "@patternfly/react-core/dist/js/components/HelperText";

interface AttributeEditorContent {
  partialScore?: number;
  reasonCode?: string;
  predicate?: Predicate;
}

interface AttributeEditorProps {
  modelIndex: number;
  characteristicIndex: number | undefined;
  attributeIndex: number | undefined;
  areReasonCodesUsed: boolean;
  onCancel: () => void;
  onCommit: (index: number | undefined, content: AttributeEditorContent) => void;
}

export const AttributeEditor = (props: AttributeEditorProps) => {
  const { modelIndex, characteristicIndex, attributeIndex, areReasonCodesUsed, onCancel, onCommit } = props;

  const [text, setText] = useState<string | undefined>();
  const [partialScore, setPartialScore] = useState<number | undefined>();
  const [reasonCode, setReasonCode] = useState<string | undefined>();
  const [originalText, setOriginalText] = useState<string>();

  const { activeOperation } = useOperation();

  const dataFields: DataField[] = useSelector<PMML, DataField[]>((state: PMML) => {
    return state.DataDictionary.DataField;
  });

  const characteristic = useSelector<PMML, Characteristic | undefined>((state: PMML) => {
    const model: Model | undefined = state.models ? state.models[modelIndex] : undefined;
    if (model instanceof Scorecard && characteristicIndex !== undefined) {
      return model.Characteristics.Characteristic[characteristicIndex]!;
    }
  });

  const attribute = useMemo(() => {
    return characteristic && attributeIndex !== undefined
      ? characteristic.Attribute[attributeIndex]
      : new Attribute({});
  }, [characteristic, attributeIndex]);

  const commit = (partial: Partial<AttributeEditorContent>) => {
    const existingPartial: Partial<AttributeEditorContent> = {};
    Object.keys(partial).forEach((key) => set(existingPartial, key, get(attribute, key)));

    if (!isEqual(partial, existingPartial)) {
      onCommit(attributeIndex, { ...attribute, ...partial });
    }
  };

  // Emulate onBlur for the Monaco editor.
  // TODO {manstis} It'd be nice to have real onBlur support....
  // When there is a click-away from the Attribute editor the Predicate
  // text will be committed if it has changed from the original..
  const ref = useOnclickOutside(
    () => {
      if (text !== originalText) {
        commit({ predicate: fromText(text) });
      }
    },
    {
      disabled: activeOperation !== Operation.UPDATE_ATTRIBUTE,
      eventTypes: ["mousedown"],
    }
  );

  const { validationRegistry } = useValidationRegistry();
  const reasonCodeValidation = useMemo(
    () =>
      validationRegistry.get(
        Builder()
          .forModel(modelIndex)
          .forCharacteristics()
          .forCharacteristic(characteristicIndex)
          .forAttribute(attributeIndex)
          .forReasonCode()
          .build()
      ),
    [modelIndex, characteristicIndex, areReasonCodesUsed, attribute.reasonCode]
  );
  const partialScoreValidation = useMemo(
    () =>
      validationRegistry.get(
        Builder()
          .forModel(modelIndex)
          .forCharacteristics()
          .forCharacteristic(characteristicIndex)
          .forAttribute(attributeIndex)
          .forPartialScore()
          .build()
      ),
    [modelIndex, characteristicIndex, attribute.partialScore]
  );
  const predicateValidation = useMemo(
    () =>
      validationRegistry.get(
        Builder()
          .forModel(modelIndex)
          .forCharacteristics()
          .forCharacteristic(characteristicIndex)
          .forAttribute(attributeIndex)
          .forPredicate()
          .build()
      ),
    [modelIndex, characteristicIndex, attribute.predicate]
  );

  useEffect(() => {
    const _text = toText(attribute.predicate, dataFields);
    setText(_text);
    setPartialScore(attribute.partialScore);
    setReasonCode(attribute.reasonCode);
    setOriginalText(_text);
  }, [modelIndex, characteristicIndex, attributeIndex, attribute.predicate]);

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
    <article tabIndex={0} data-ouia-component-id="edit-attribute">
      <Form>
        <Split hasGutter={true}>
          <SplitItem isFilled={true}>
            <FormGroup label="Predicate" isRequired={true} fieldId="attribute-predicate-helper">
              <div ref={ref} data-ouia-component-id="predicate">
                <PredicateEditor text={text} setText={setText} />
                <>
                  {predicateValidation.length > 0 && (
                    <div>
                      <ValidationIndicator validations={predicateValidation} />
                      <span className="pf-v5-c-form__helper-text pf-m-warning attribute-editor__validation-message">
                        {predicateValidation[0].message}
                      </span>
                    </div>
                  )}
                  {predicateValidation.length === 0 && (
                    <div className="pf-v5-c-form__helper-text">
                      The condition upon which the mapping between input attribute and partial score takes place.
                    </div>
                  )}
                </>
              </div>
              {predicateValidation.length > 0 ? (
                <FormHelperText>
                  <HelperText>
                    <HelperTextItem variant="warning"></HelperTextItem>
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
            <Stack hasGutter={true}>
              <StackItem>
                <FormGroup
                  label="Reason code"
                  fieldId="attribute-reason-code-helper"
                  labelIcon={
                    <Tooltip
                      content={
                        areReasonCodesUsed && characteristic?.reasonCode !== undefined
                          ? `Reason code already provided at the Characteristic level (${characteristic.reasonCode})`
                          : `When Use Reason Codes is set to yes in the Model Setup, a reason code must be provided \
                              for characteristics or it must be provided for all its attributes.`
                      }
                    >
                      <button
                        aria-label="More information for Partial Score"
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
                    id="attribute-reason-code"
                    name="attribute-reason-code"
                    aria-describedby="attribute-reason-code-helper"
                    value={reasonCode ?? ""}
                    onChange={(_event, e) => setReasonCode(e)}
                    onBlur={() => {
                      commit({ reasonCode: reasonCode !== "" ? reasonCode : undefined });
                    }}
                    validated={reasonCodeValidation.length > 0 ? "warning" : "default"}
                    isDisabled={!areReasonCodesUsed || characteristic?.reasonCode !== undefined}
                    ouiaId="attribute-reason-code"
                  />
                  {reasonCodeValidation.length > 0 ? (
                    <FormHelperText>
                      <HelperText>
                        <HelperTextItem variant="warning">
                          {reasonCodeValidation.length > 0
                            ? reasonCodeValidation[0].message
                            : "A Reason Code is mapped to a Business reason."}
                        </HelperTextItem>
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
              </StackItem>
              <StackItem>
                <FormGroup
                  label="Partial score"
                  fieldId="attribute-partial-score-helper"
                  labelIcon={
                    <Tooltip
                      content={
                        "If one of the Attributes of a Characteristic provides a Partial score value, all the attributes are required to provide a Partial score as well."
                      }
                    >
                      <button
                        aria-label="More information for Partial Score"
                        onClick={(e) => e.preventDefault()}
                        className="pf-v5-c-form__group-label-help"
                      >
                        <HelpIcon style={{ color: "var(--pf-v5-global--info-color--100)" }} />
                      </button>
                    </Tooltip>
                  }
                >
                  <TextInput
                    type="number"
                    id="attribute-partial-score"
                    name="attribute-partial-score"
                    aria-describedby="attribute-partial-score-helper"
                    value={partialScore ?? ""}
                    onChange={(_event, e) => setPartialScore(toNumber(e))}
                    onBlur={() => {
                      commit({
                        partialScore: partialScore,
                      });
                    }}
                    validated={partialScoreValidation.length > 0 ? "warning" : "default"}
                    ouiaId="attribute-partial-score"
                  />
                  {partialScoreValidation.length > 0 ? (
                    <FormHelperText>
                      <HelperText>
                        <HelperTextItem variant="warning">
                          {partialScoreValidation.length > 0
                            ? partialScoreValidation[0].message
                            : "Defines the score points awarded to the Attribute."}
                        </HelperTextItem>
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
              </StackItem>
            </Stack>
          </SplitItem>
        </Split>
      </Form>
    </article>
  );
};
