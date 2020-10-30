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
import { DataListAction, DataListCell, DataListItem, DataListItemCells, DataListItemRow } from "@patternfly/react-core";
import { Attribute } from "@kogito-tooling/pmml-editor-marshaller";
import "../organisms/AttributesTable.scss";
import { AttributesTableAction } from "../atoms";

interface AttributesTableRowProps {
  index: number;
  attribute: Attribute;
  onEdit: () => void;
  onDelete: () => void;
  isDisabled: boolean;
}

export const AttributesTableRow = (props: AttributesTableRowProps) => {
  const { index, attribute, onEdit, onDelete, isDisabled } = props;

  return (
    <DataListItem
      key={index}
      id={index.toString()}
      className="attributes__list-item"
      aria-labelledby={"attribute-" + index}
    >
      <DataListItemRow>
        <DataListItemCells
          dataListCells={[
            <DataListCell key="0" width={4}>
              <div>{JSON.stringify(attribute.predicate, undefined, 2)}</div>
            </DataListCell>,
            <DataListCell key="1" width={2}>
              <div>{attribute.partialScore}</div>
            </DataListCell>,
            <DataListCell key="2" width={2}>
              <div>{attribute.reasonCode}</div>
            </DataListCell>,
            <DataListAction
              id="delete-attribute"
              aria-label="delete"
              aria-labelledby="delete-attribute"
              key="4"
              width={1}
            >
              <AttributesTableAction onEdit={() => onEdit()} onDelete={() => onDelete()} disabled={isDisabled} />
            </DataListAction>
          ]}
        />
      </DataListItemRow>
    </DataListItem>
  );
};
