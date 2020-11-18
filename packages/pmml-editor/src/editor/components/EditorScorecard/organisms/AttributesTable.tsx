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
  DataList,
  DataListAction,
  DataListCell,
  DataListItem,
  DataListItemCells,
  DataListItemRow,
  Form,
  FormGroup,
  Tooltip,
  TooltipPosition
} from "@patternfly/react-core";
import { Attribute, Characteristic, Model, PMML, Scorecard } from "@kogito-tooling/pmml-editor-marshaller";
import { AttributesTableEditRow, AttributesTableRow, EmptyStateNoAttributes } from "../molecules";
import "./AttributesTable.scss";

import { InfoCircleIcon } from "@patternfly/react-icons";
import { Operation } from "../Operation";
import { ActionSpacer } from "../../EditorCore/atoms";
import { useSelector } from "react-redux";
import { IndexedCharacteristic } from "./CharacteristicsTable";

interface AttributesTableProps {
  modelIndex: number;
  characteristic: IndexedCharacteristic | undefined;
  activeOperation: Operation;
  setActiveOperation: (operation: Operation) => void;
  addAttribute: () => void;
  deleteAttribute: (index: number) => void;
  commit: (
    index: number | undefined,
    text: string | undefined,
    partialScore: number | undefined,
    reasonCode: string | undefined
  ) => void;
}

export const AttributesTable = (props: AttributesTableProps) => {
  const {
    modelIndex,
    characteristic,
    activeOperation,
    setActiveOperation,
    addAttribute,
    deleteAttribute,
    commit
  } = props;

  const [editItemIndex, setEditItemIndex] = useState<number | undefined>(undefined);
  const addAttributeRowRef = useRef<HTMLDivElement | null>(null);

  const attributes: Attribute[] = useSelector<PMML, Attribute[]>((state: PMML) => {
    const model: Model | undefined = state.models ? state.models[modelIndex] : undefined;
    if (model !== undefined && characteristic?.index !== undefined && model instanceof Scorecard) {
      const scorecard: Scorecard = model as Scorecard;
      const _characteristic: Characteristic | undefined =
        scorecard.Characteristics.Characteristic[characteristic.index];
      if (_characteristic) {
        return _characteristic.Attribute;
      }
    }
    return [];
  });

  useEffect(() => {
    if (activeOperation === Operation.CREATE_ATTRIBUTE && addAttributeRowRef.current) {
      addAttributeRowRef.current.scrollIntoView({ behavior: "smooth" });
    }
  }, [activeOperation]);

  const onEdit = (index: number | undefined) => {
    setEditItemIndex(index);
    setActiveOperation(Operation.UPDATE_ATTRIBUTE);
  };

  const onDelete = (index: number | undefined) => {
    if (index !== undefined) {
      deleteAttribute(index);
    }
  };

  const onCommit = (
    index: number | undefined,
    text: string | undefined,
    partialScore: number | undefined,
    reasonCode: string | undefined
  ) => {
    //Avoid commits with no change
    if (index === undefined) {
      commit(index, text, partialScore, reasonCode);
    } else {
      const attribute = attributes[index];
      if (attribute.partialScore !== partialScore || attribute.reasonCode !== reasonCode) {
        commit(index, text, partialScore, reasonCode);
      }
    }

    onCancel();
  };

  const onCancel = () => {
    setEditItemIndex(undefined);
    setActiveOperation(Operation.NONE);
  };

  const validateText = (text: string | undefined) => {
    return text !== undefined && text.trim() !== "";
  };

  return (
    <div style={{ height: "100%", overflowY: "auto" }}>
      {(attributes.length > 0 || activeOperation === Operation.CREATE_ATTRIBUTE) && (
        <>
          <div style={{ paddingRight: "16px" }}>
            <Form>
              <DataList className="attributes__header" aria-label="attributes header">
                <DataListItem className="attributes__header__row" key={"none"} aria-labelledby="attributes-header">
                  <DataListItemRow>
                    <DataListItemCells
                      dataListCells={[
                        <DataListCell key="0" width={5}>
                          <FormGroup fieldId="Predicate" label="Predicate" isRequired={true} />
                        </DataListCell>,
                        <DataListCell key="1" width={2}>
                          <FormGroup
                            fieldId="PartialScore"
                            label="Partial Score"
                            labelIcon={
                              <Tooltip
                                position={TooltipPosition.top}
                                content={<div>Score points awarded to the Attribute</div>}
                              >
                                <InfoCircleIcon className={"attributes__header__icon"} />
                              </Tooltip>
                            }
                          />
                        </DataListCell>,
                        <DataListCell key="2" width={2}>
                          <FormGroup
                            fieldId="ReasonCode"
                            label="Reason Code"
                            labelIcon={
                              <Tooltip
                                position={TooltipPosition.top}
                                content={
                                  <div>
                                    Attribute's reason code. If the reason code is used at this level, it takes
                                    precedence over the reason code attribute associated with the Characteristic
                                  </div>
                                }
                              >
                                <InfoCircleIcon className={"attributes__header__icon"} />
                              </Tooltip>
                            }
                          />
                        </DataListCell>,
                        <DataListAction
                          id="delete-attribute-header"
                          aria-label="delete header"
                          aria-labelledby="delete-attribute-header"
                          key="3"
                          width={1}
                        >
                          <ActionSpacer />
                        </DataListAction>
                      ]}
                    />
                  </DataListItemRow>
                </DataListItem>
              </DataList>
            </Form>
          </div>
          <div className="attributes__body">
            <Form>
              <DataList aria-label="attributes list">
                {attributes.map((attribute, index) => {
                  if (editItemIndex === index) {
                    return (
                      <AttributesTableEditRow
                        key={index}
                        index={index}
                        attribute={attribute}
                        validateText={validateText}
                        onCommit={(_text, _partialScore, _reasonCode) =>
                          onCommit(index, _text, _partialScore, _reasonCode)
                        }
                        onCancel={() => onCancel()}
                      />
                    );
                  } else {
                    return (
                      <AttributesTableRow
                        key={index}
                        index={index}
                        attribute={attribute}
                        onEdit={() => onEdit(index)}
                        onDelete={() => onDelete(index)}
                        isDisabled={
                          !(editItemIndex === undefined || editItemIndex === index) ||
                          activeOperation !== Operation.NONE
                        }
                      />
                    );
                  }
                })}
                {activeOperation === Operation.CREATE_ATTRIBUTE && (
                  <div key={undefined} ref={addAttributeRowRef}>
                    <AttributesTableEditRow
                      key={"add"}
                      index={undefined}
                      attribute={{}}
                      validateText={validateText}
                      onCommit={(_text, _partialScore, _reasonCode) =>
                        onCommit(undefined, _text, _partialScore, _reasonCode)
                      }
                      onCancel={() => onCancel()}
                    />
                  </div>
                )}
              </DataList>
            </Form>
          </div>
        </>
      )}
      {attributes.length === 0 && activeOperation !== Operation.CREATE_ATTRIBUTE && <EmptyStateNoAttributes />}
    </div>
  );
};
