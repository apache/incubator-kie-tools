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
import { Split, SplitItem } from "@patternfly/react-core";
import { Attribute, DataField } from "@kogito-tooling/pmml-editor-marshaller";
import "./AttributesTableRow.scss";
import { AttributeLabels, AttributesTableAction } from "../atoms";
import { toText } from "../../../reducers";

interface AttributesTableRowProps {
  index: number;
  attribute: Attribute;
  dataFields: DataField[];
  onEdit: () => void;
  onDelete: () => void;
}

export const AttributesTableRow = (props: AttributesTableRowProps) => {
  const { index, attribute, dataFields, onEdit, onDelete } = props;

  return (
    <article
      className={`attribute-item attribute-item-n${index} editable`}
      tabIndex={0}
      onClick={e => onEdit()}
      onKeyDown={e => {
        if (e.key === "Enter") {
          e.preventDefault();
          e.stopPropagation();
          onEdit();
        }
      }}
    >
      <Split hasGutter={true} style={{ height: "100%" }}>
        <SplitItem>
          <pre>{toText(attribute.predicate, dataFields)}</pre>
        </SplitItem>
        <SplitItem isFilled={true}>
          <AttributeLabels activeAttribute={attribute} />
        </SplitItem>
        <SplitItem>
          <AttributesTableAction onDelete={onDelete} />
        </SplitItem>
      </Split>
    </article>
  );
};
