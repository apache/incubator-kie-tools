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
import { useEffect, useState } from "react";
import {
  Attribute,
  Characteristic,
  DataField,
  MiningField,
  Model,
  PMML,
  Scorecard,
} from "@kie-tools/pmml-editor-marshaller";
import { AttributesTableRow } from "../molecules";
import "./AttributesTable.scss";
import { Operation } from "../Operation";
import { useSelector } from "react-redux";
import { useOperation } from "../OperationContext";
import { Interaction } from "../../../types";

interface AttributesTableProps {
  modelIndex: number;
  characteristicIndex: number;
  characteristic: Characteristic;
  areReasonCodesUsed: boolean;
  viewAttribute: (index: number | undefined) => void;
  deleteAttribute: (index: number) => void;
  onCommit: (index: number, partial: Partial<Attribute>) => void;
}

export const AttributesTable = (props: AttributesTableProps) => {
  const {
    modelIndex,
    characteristicIndex,
    characteristic,
    areReasonCodesUsed,
    viewAttribute,
    deleteAttribute,
    onCommit,
  } = props;

  const { setActiveOperation } = useOperation();

  const [attributeFocusIndex, setAttributeFocusIndex] = useState<number | undefined>(undefined);

  const dataFields: DataField[] = useSelector<PMML, DataField[]>((state: PMML) => {
    return state.DataDictionary.DataField;
  });
  const miningFields: MiningField[] = useSelector<PMML, MiningField[]>((state: PMML) => {
    const _model: Model | undefined = state.models ? state.models[props.modelIndex] : undefined;
    if (_model && _model instanceof Scorecard) {
      return (_model as Scorecard).MiningSchema.MiningField;
    }
    return [];
  });

  //Set the focus on a Attribute as required
  useEffect(() => {
    if (attributeFocusIndex !== undefined) {
      document.querySelector<HTMLElement>(`#attribute-n${attributeFocusIndex}`)?.focus();
    }
  }, [characteristic.Attribute, attributeFocusIndex]);

  const onEdit = (index: number | undefined) => {
    setActiveOperation(Operation.UPDATE_ATTRIBUTE);
    viewAttribute(index);
  };

  const handleDelete = (index: number, interaction: Interaction) => {
    onDelete(index);
    if (interaction === "mouse") {
      //If the Attribute was deleted by clicking on the delete icon we need to blur
      //the element otherwise the CSS :focus-within persists on the deleted element.
      //See https://issues.redhat.com/browse/FAI-570 for the root cause.
      if (document.activeElement instanceof HTMLElement) {
        document.activeElement?.blur();
      }
    } else if (interaction === "keyboard") {
      //If the Attribute was deleted by pressing enter on the delete icon when focused
      //we need to set the focus to the next Attribute. The index of the _next_ item
      //is identical to the index of the deleted item.
      setAttributeFocusIndex(index);
    }
  };

  const onDelete = (index: number | undefined) => {
    if (index !== undefined) {
      deleteAttribute(index);
    }
  };

  return (
    <section data-testid="attributes-table">
      {characteristic.Attribute.map((attribute, index) => {
        return (
          <article key={index} className={`editable-item attribute-item-n${index}`}>
            <AttributesTableRow
              key={index}
              modelIndex={modelIndex}
              characteristicIndex={characteristicIndex}
              characteristic={characteristic}
              attributeIndex={index}
              attribute={attribute}
              areReasonCodesUsed={areReasonCodesUsed}
              characteristicReasonCode={characteristic.reasonCode}
              dataFields={dataFields}
              miningFields={miningFields}
              onEdit={() => onEdit(index)}
              onDelete={(interaction) => handleDelete(index, interaction)}
              onCommit={(partial) => onCommit(index, partial)}
            />
          </article>
        );
      })}
    </section>
  );
};
