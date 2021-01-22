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
import { Attribute, DataField, PMML } from "@kogito-tooling/pmml-editor-marshaller";
import { AttributesTableRow } from "../molecules";
import "./AttributesTable.scss";
import { Operation } from "../Operation";
import { useSelector } from "react-redux";
import { useOperation } from "../OperationContext";

interface AttributesTableProps {
  attributes: Attribute[];
  areReasonCodesUsed: boolean;
  viewAttribute: (index: number | undefined) => void;
  deleteAttribute: (index: number) => void;
}

export const AttributesTable = (props: AttributesTableProps) => {
  const { attributes, areReasonCodesUsed, viewAttribute, deleteAttribute } = props;

  const { setActiveOperation } = useOperation();

  const dataFields: DataField[] = useSelector<PMML, DataField[]>((state: PMML) => {
    return state.DataDictionary.DataField;
  });

  const onEdit = (index: number | undefined) => {
    setActiveOperation(Operation.UPDATE_ATTRIBUTE);
    viewAttribute(index);
  };

  const onDelete = (index: number | undefined) => {
    if (index !== undefined) {
      deleteAttribute(index);
    }
  };

  return (
    <section>
      {attributes.map((attribute, index) => {
        return (
          <AttributesTableRow
            key={index}
            index={index}
            attribute={attribute}
            areReasonCodesUsed={areReasonCodesUsed}
            dataFields={dataFields}
            onEdit={() => onEdit(index)}
            onDelete={() => onDelete(index)}
          />
        );
      })}
    </section>
  );
};
