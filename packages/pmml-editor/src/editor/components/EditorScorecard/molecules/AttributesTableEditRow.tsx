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
import { useEffect, useRef, useState } from "react";
import {
  DataListAction,
  DataListCell,
  DataListItem,
  DataListItemCells,
  DataListItemRow,
  FormGroup,
  TextInput
} from "@patternfly/react-core";
import "../organisms/CharacteristicsTable.scss";
import { Attribute } from "@kogito-tooling/pmml-editor-marshaller";
import { AttributesTableEditModeAction } from "../atoms/AttributesTableEditModeAction";

interface AttributesTableEditRowProps {
  index: number | undefined;
  attribute: Attribute;
  onCommit: (text: string | undefined, partialScore: number | undefined, reasonCode: string | undefined) => void;
  onCancel: () => void;
}

export const AttributesTableEditRow = (props: AttributesTableEditRowProps) => {
  const { index, attribute, onCommit, onCancel } = props;

  const [text, setText] = useState<string | undefined>(undefined);
  const [partialScore, setPartialScore] = useState<number | undefined>();
  const [reasonCode, setReasonCode] = useState<string | undefined>();

  const textFieldRef = useRef<HTMLInputElement | null>(null);

  useEffect(() => {
    setText(JSON.stringify(attribute.predicate, undefined, 2));
    setPartialScore(attribute.partialScore);
    setReasonCode(attribute.reasonCode);

    if (textFieldRef.current) {
      textFieldRef.current.focus();
    }
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
      key={index}
      id={index?.toString()}
      className="attributes__list-item"
      aria-labelledby={"attribute-" + index}
    >
      <DataListItemRow>
        <DataListItemCells
          dataListCells={[
            <DataListCell key="0" width={4}>
              <FormGroup fieldId="attribute-text-helper" helperText="This will be Monaco and predicate definition.">
                <TextInput
                  type="text"
                  id="attribute-text"
                  name="attribute-text"
                  ref={textFieldRef}
                  aria-describedby="attribute-text-helper"
                  value={JSON.stringify(attribute.predicate, undefined, 2)}
                  onChange={e => setText(e)}
                />
              </FormGroup>
            </DataListCell>,
            <DataListCell key="1" width={2}>
              <FormGroup
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
            </DataListCell>,
            <DataListCell key="2" width={2}>
              <FormGroup
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
            </DataListCell>,
            <DataListAction
              id="delete-attribute"
              aria-label="delete"
              aria-labelledby="delete-attribute"
              key="4"
              width={1}
            >
              <AttributesTableEditModeAction
                onCommit={() => onCommit(text, partialScore, reasonCode)}
                onCancel={() => onCancel()}
                disableCommit={false}
              />
            </DataListAction>
          ]}
        />
      </DataListItemRow>
    </DataListItem>
  );
};
