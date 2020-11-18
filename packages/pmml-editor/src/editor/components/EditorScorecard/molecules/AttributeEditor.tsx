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
import { Form, FormGroup, Split, SplitItem, Stack, StackItem, TextInput } from "@patternfly/react-core";
import { Attribute, Characteristic, DataField, Model, PMML, Scorecard } from "@kogito-tooling/pmml-editor-marshaller";
import { ValidatedType } from "../../../types";
import { toText } from "../../../reducers";
import { PredicateEditor } from "./PredicateEditor";
import { Operation } from "../Operation";
import useOnclickOutside from "react-cool-onclickoutside";
import { useSelector } from "react-redux";

interface AttributeEditorProps {
  activeOperation: Operation;
  setActiveOperation: (operation: Operation) => void;
  modelIndex: number;
  characteristicIndex: number | undefined;
  attributeIndex: number | undefined;
  onCancel: () => void;
  onCommit: (
    index: number | undefined,
    text: string | undefined,
    partialScore: number | undefined,
    reasonCode: string | undefined
  ) => void;
}

export const AttributeEditor = (props: AttributeEditorProps) => {
  const { activeOperation, modelIndex, characteristicIndex, attributeIndex, onCommit, onCancel } = props;

  const [text, setText] = useState<ValidatedType<string | undefined>>({
    value: undefined,
    valid: true
  });
  const [partialScore, setPartialScore] = useState<number | undefined>();
  const [reasonCode, setReasonCode] = useState<string | undefined>();

  const dataFields: DataField[] = useSelector<PMML, DataField[]>((state: PMML) => {
    return state.DataDictionary.DataField;
  });

  const attribute: Attribute = useSelector<PMML, Attribute>((state: PMML) => {
    const model: Model | undefined = state.models ? state.models[modelIndex] : undefined;
    if (model instanceof Scorecard && characteristicIndex !== undefined) {
      const scorecard: Scorecard = model as Scorecard;
      const _characteristic: Characteristic | undefined = scorecard.Characteristics.Characteristic[characteristicIndex];
      if (_characteristic && attributeIndex !== undefined) {
        return _characteristic.Attribute[attributeIndex];
      }
    }
    return new Attribute({});
  });

  const ref = useOnclickOutside(
    event => {
      if (text.valid) {
        onCommit(attributeIndex, text.value, partialScore, reasonCode);
      } else {
        onCancel();
      }
    },
    {
      disabled: activeOperation !== Operation.UPDATE_ATTRIBUTE && activeOperation !== Operation.CREATE_ATTRIBUTE,
      eventTypes: ["click"]
    }
  );

  useEffect(() => {
    const _text = toText(attribute.predicate, dataFields);
    setText({
      value: _text,
      valid: true
    });
    setPartialScore(attribute.partialScore);
    setReasonCode(attribute.reasonCode);
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

  const validateText = (text: string | undefined) => {
    return text !== undefined && text.trim() !== "";
  };

  return (
    <article ref={ref}>
      <Form>
        <Split hasGutter={true}>
          <SplitItem isFilled={true}>
            <FormGroup
              label="Predicate"
              fieldId="attribute-predicate-helper"
              helperText="Expression editor for the predicate."
            >
              <PredicateEditor text={text} setText={setText} validateText={validateText} />
            </FormGroup>
          </SplitItem>
          <SplitItem>
            <Stack hasGutter={true}>
              <StackItem>
                <FormGroup
                  label="Partial score"
                  fieldId="attribute-partial-score-helper"
                  helperText="Defines the score points awarded to the Attribute."
                >
                  <TextInput
                    type="number"
                    id="attribute-partial-score"
                    name="attribute-partial-score"
                    aria-describedby="attribute-partial-score-helper"
                    value={partialScore ?? ""}
                    onChange={e => setPartialScore(toNumber(e))}
                  />
                </FormGroup>
              </StackItem>
              <StackItem>
                <FormGroup
                  label="Reason code"
                  fieldId="attribute-reason-code-helper"
                  helperText="A Reason Code is mapped to a Business reason."
                >
                  <TextInput
                    type="text"
                    id="attribute-reason-code"
                    name="attribute-reason-code"
                    aria-describedby="attribute-reason-code-helper"
                    value={reasonCode ?? ""}
                    onChange={e => setReasonCode(e)}
                  />
                </FormGroup>
              </StackItem>
            </Stack>
          </SplitItem>
        </Split>
      </Form>
    </article>
  );
};
