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
  Button,
  DataList,
  DataListAction,
  DataListCell,
  DataListItem,
  DataListItemCells,
  DataListItemRow,
  Tooltip,
  TooltipPosition
} from "@patternfly/react-core";
import { Attribute } from "@kogito-tooling/pmml-editor-marshaller";
import { AttributesTableEditRow, AttributesTableRow, EmptyStateNoAttributes } from "../molecules";
import "./AttributesTable.scss";

import { InfoCircleIcon, TrashIcon } from "@patternfly/react-icons";
import { Operation } from "../Operation";

interface AttributesTableProps {
  index: number | undefined;
  activeOperation: Operation;
  setActiveOperation: (operation: Operation) => void;
  attributes: Attribute[];
  onRowDelete: (index: number) => void;
  onAddAttribute: () => void;
  commit: (index: number | undefined) => void;
}

export const AttributesTable = (props: AttributesTableProps) => {
  const { activeOperation, setActiveOperation, attributes, onRowDelete, onAddAttribute } = props;

  const [selectedItemIndex, setSelectedItemIndex] = useState<number | undefined>(undefined);
  const [editItemIndex, setEditItemIndex] = useState<number | undefined>(undefined);

  const addAttributeRowRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    if (activeOperation === Operation.CREATE_ATTRIBUTE && addAttributeRowRef.current) {
      addAttributeRowRef.current.scrollIntoView({ behavior: "smooth" });
    }
  }, [activeOperation]);

  const onEdit = (index: number | undefined) => {
    setEditItemIndex(index);
    setActiveOperation(Operation.UPDATE);
  };

  const onDelete = (index: number | undefined) => {
    if (index !== undefined) {
      onRowDelete(index);
    }
  };

  const onCommit = (
    index: number | undefined,
    text: string | undefined,
    partialScore: number | undefined,
    reasonCode: string | undefined
  ) => {
    //Avoid commits with no change
    let attribute: Attribute;
    if (index === undefined) {
      attribute = {};
    } else {
      attribute = attributes[index];
    }
    if (attribute.partialScore !== partialScore || attribute.reasonCode !== reasonCode) {
      //TODO {manstis} Commit changes!
      props.commit(index);
    }

    onCancel();
  };

  const onCancel = () => {
    setEditItemIndex(undefined);
    setActiveOperation(Operation.NONE);
  };

  return (
    <div>
      <DataList className="attributes__header" aria-label="attributes header">
        <DataListItem className="attributes__header__row" key={"none"} aria-labelledby="attributes-header">
          <DataListItemRow>
            <DataListItemCells
              dataListCells={[
                <DataListCell key="0" width={4}>
                  <div>Attribute</div>
                </DataListCell>,
                <DataListCell key="1" width={2}>
                  <div>
                    <span className={"attributes__header__text"}>Partial Score</span>
                    <Tooltip position={TooltipPosition.top} content={<div>Score points awarded to the Attribute</div>}>
                      <InfoCircleIcon className={"attributes__header__icon"} />
                    </Tooltip>
                  </div>
                </DataListCell>,
                <DataListCell key="2" width={2}>
                  <div>
                    <span className={"attributes__header__text"}>Reason Code</span>
                    <Tooltip
                      position={TooltipPosition.top}
                      content={
                        <div>
                          Attribute's reason code. If the reason code is used at this level, it takes precedence over
                          the reason code attribute associated with the Characteristic
                        </div>
                      }
                    >
                      <InfoCircleIcon className={"attributes__header__icon"} />
                    </Tooltip>
                  </div>
                </DataListCell>,
                <DataListAction
                  id="delete-attribute-header"
                  aria-label="delete header"
                  aria-labelledby="delete-attribute-header"
                  key="3"
                  width={1}
                >
                  {/*This is a hack to ensure the column layout is correct*/}
                  <Button variant="link" icon={<TrashIcon />} isDisabled={true} style={{ visibility: "hidden" }}>
                    &nbsp;
                  </Button>
                </DataListAction>
              ]}
            />
          </DataListItemRow>
        </DataListItem>
      </DataList>
      <DataList aria-label="attributes list">
        {attributes.map((attribute, index) => {
          if (editItemIndex === index) {
            return (
              <AttributesTableEditRow
                key={index}
                index={index}
                attribute={attribute}
                onCommit={(_text, _partialScore, _reasonCode) => onCommit(index, _text, _partialScore, _reasonCode)}
                onCancel={() => onCancel()}
              />
            );
          } else {
            return (
              <AttributesTableRow
                index={index}
                attribute={attribute}
                onEdit={() => onEdit(index)}
                onDelete={() => onDelete(index)}
                isDisabled={
                  !(editItemIndex === undefined || editItemIndex === index) || activeOperation !== Operation.NONE
                }
              />
            );
          }
        })}
        {activeOperation === Operation.CREATE_ATTRIBUTE && (
          <div key={undefined} ref={addAttributeRowRef}>
            <AttributesTableEditRow
              index={undefined}
              attribute={{}}
              onCommit={(_text, _partialScore, _reasonCode) => onCommit(undefined, _text, _partialScore, _reasonCode)}
              onCancel={() => onCancel()}
            />
          </div>
        )}
      </DataList>
      {attributes.length === 0 && <EmptyStateNoAttributes createAttribute={onAddAttribute} />}
    </div>
  );
};
