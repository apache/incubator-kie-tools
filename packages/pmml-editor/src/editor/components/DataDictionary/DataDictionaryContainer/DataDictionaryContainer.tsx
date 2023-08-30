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
import { useEffect, useRef, useState } from "react";
import { CSSTransition, SwitchTransition } from "react-transition-group";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { Alert } from "@patternfly/react-core/dist/js/components/Alert";
import { SortIcon } from "@patternfly/react-icons/dist/js/icons/sort-icon";
import { PlusIcon } from "@patternfly/react-icons/dist/js/icons/plus-icon";
import { BoltIcon } from "@patternfly/react-icons/dist/js/icons/bolt-icon";
import DataTypeItem from "../DataTypeItem/DataTypeItem";
import MultipleDataTypeAdd from "../MultipleDataTypeAdd/MultipleDataTypeAdd";
import DataTypesSort from "../DataTypesSort/DataTypesSort";
import EmptyDataDictionary from "../EmptyDataDictionary/EmptyDataDictionary";
import { findIncrementalName } from "../../../PMMLModelHelper";
import "./DataDictionaryContainer.scss";
import DataDictionaryPropertiesEdit from "../DataDictionaryPropertiesEdit/DataDictionaryPropertiesEdit";
import { isEqual } from "lodash";
import { useValidationRegistry } from "../../../validation";
import { Builder } from "../../../paths";
import { Interaction } from "../../../types";

interface DataDictionaryContainerProps {
  dataDictionary: DDDataField[];
  onAdd: (name: string, type: DDDataField["type"], optype: DDDataField["optype"]) => void;
  onEdit: (index: number, originalName: string, field: DDDataField) => void;
  onDelete: (index: number) => void;
  onReorder: (oldIndex: number, newIndex: number) => void;
  onBatchAdd: (fields: string[]) => void;
  onEditingPhaseChange: (status: boolean) => void;
}

const DataDictionaryContainer = (props: DataDictionaryContainerProps) => {
  const { dataDictionary, onAdd, onEdit, onDelete, onReorder, onBatchAdd, onEditingPhaseChange } = props;
  const [dataTypes, setDataTypes] = useState<DDDataField[]>(dataDictionary);
  const [editing, setEditing] = useState<number | undefined>();
  const [viewSection, setViewSection] = useState<dataDictionarySection>("main");
  const [editingDataType, setEditingDataType] = useState<DDDataField>();
  const [sorting, setSorting] = useState(false);
  const [dataTypeFocusIndex, setDataTypeFocusIndex] = useState<number | undefined>(undefined);

  useEffect(() => {
    // undoing a recently created data field force to exit the editing mode for that field
    if (editing === dataDictionary.length) {
      setEditing(undefined);
      if (viewSection !== "main") {
        setViewSection("main");
      }
      onEditingPhaseChange(false);
    }
    // updating constraintsEdit when dictionary changes
    if (viewSection === "properties" && editing !== undefined) {
      setEditingDataType(dataDictionary[editing]);
    }
    setDataTypes(dataDictionary);
  }, [dataDictionary, editing, viewSection]);

  const handleOutsideClick = () => {
    setEditing(undefined);
    onEditingPhaseChange(false);
  };

  const addDataType = () => {
    onAdd(
      findIncrementalName(
        "New Data Type",
        dataTypes.map((dt) => dt.name),
        1
      ),
      "string",
      "categorical"
    );
    setEditing(dataTypes.length);
    onEditingPhaseChange(true);
  };

  const saveDataType = (dataType: DDDataField, index: number) => {
    onEdit(index, dataTypes[index].name, dataType);
  };

  const handleSave = (dataType: DDDataField, index: number) => {
    saveDataType(dataType, index);
  };

  const handleDelete = (index: number, interaction: Interaction) => {
    onDelete(index);
    if (interaction === "mouse") {
      //If the DataTypeItem was deleted by clicking on the delete icon we need to blur
      //the element otherwise the CSS :focus-within persists on the deleted element.
      //See https://issues.redhat.com/browse/FAI-570 for the root cause.
      if (document.activeElement instanceof HTMLElement) {
        document.activeElement?.blur();
      }
    } else if (interaction === "keyboard") {
      //If the DataTypeItem was deleted by pressing enter on the delete icon when focused
      //we need to set the focus to the next DataTypeItem. The index of the _next_ item
      //is identical to the index of the deleted item.
      setDataTypeFocusIndex(index);
    }
    setEditing(undefined);
    onEditingPhaseChange(false);
  };

  const handleEdit = (index: number) => {
    setEditing(index);
    onEditingPhaseChange(true);
  };

  const handleMultipleAdd = (fields: string) => {
    const fieldsNames = fields.split("\n").filter((item) => item.trim().length > 0);
    onBatchAdd(fieldsNames);
    setViewSection("main");
  };

  const handleConstraintsEdit = (dataType: DDDataField) => {
    if (editing !== undefined) {
      setEditingDataType(dataType);
      setViewSection("properties");
      onEditingPhaseChange(true);
    }
  };

  const handleConstraintsSave = (payload: DDDataField) => {
    if (editing !== undefined) {
      onEdit(editing, dataTypes[editing].name, payload);
    }
  };

  const handlePropertiesSave = (payload: Partial<DDDataField>) => {
    if (editing !== undefined) {
      const dataType = dataTypes[editing];
      const existingPartial = {};
      Object.keys(payload).forEach((key) => Reflect.set(existingPartial, key, Reflect.get(dataType, key)));

      if (!isEqual(payload, existingPartial)) {
        onEdit(editing, dataType.name, Object.assign(dataType, payload));
      }
    }
  };

  const exitFromPropertiesEdit = () => {
    setViewSection("main");
  };

  const toggleSorting = () => {
    setEditing(undefined);
    setSorting(!sorting);
  };

  const dataTypeNameValidation = (dataTypeName: string) => {
    let isValid = true;
    if (dataTypeName.trim().length === 0) {
      return false;
    }
    const match = dataTypes.find((item, index) => item.name === dataTypeName.trim() && index !== editing);
    if (match !== undefined) {
      isValid = false;
    }
    return isValid;
  };

  const getTransition = (currentState: dataDictionarySection) => {
    if (currentState === "main") {
      return "data-dictionary__overview";
    } else if (currentState === "batch-add") {
      return "enter-from-above";
    } else {
      return "enter-from-right";
    }
  };

  const { validationRegistry } = useValidationRegistry();
  const validations = useRef(validationRegistry.get(Builder().forDataDictionary().build()));
  useEffect(() => {
    if (editing === undefined) {
      validations.current = validationRegistry.get(Builder().forDataDictionary().build());
    }
  }, [dataDictionary, editing]);

  //Set the focus on a DataTypeItem as required
  useEffect(() => {
    if (dataTypeFocusIndex !== undefined) {
      document.querySelector<HTMLElement>(`#data-type-item-n${dataTypeFocusIndex}`)?.focus();
    }
  }, [dataDictionary, dataTypeFocusIndex]);

  return (
    <div className="data-dictionary" data-testid="data-dictionary-container">
      <SwitchTransition mode={"out-in"}>
        <CSSTransition
          timeout={{
            enter: 230,
            exit: 100,
          }}
          classNames={getTransition(viewSection)}
          key={viewSection}
        >
          <>
            {viewSection === "main" && (
              <section className="data-dictionary__overview">
                <Flex className="data-dictionary__toolbar" data-ouia-component-id="dd-toolbar">
                  <FlexItem>
                    <Button
                      variant="primary"
                      onClick={(event) => {
                        event.stopPropagation();
                        event.preventDefault();
                        addDataType();
                      }}
                      icon={<PlusIcon />}
                      iconPosition="left"
                      isDisabled={editing !== undefined || sorting}
                      ouiaId="add-data-type"
                    >
                      Add Data Type
                    </Button>
                  </FlexItem>
                  <FlexItem>
                    <Button
                      variant="secondary"
                      onClick={() => setViewSection("batch-add")}
                      icon={<BoltIcon />}
                      iconPosition="left"
                      isDisabled={editing !== undefined || sorting}
                      ouiaId="add-multiple-data-type"
                    >
                      Add Multiple Data Types
                    </Button>
                  </FlexItem>
                  <FlexItem align={{ default: "alignRight" }}>
                    <Button
                      variant={sorting ? "primary" : "secondary"}
                      onClick={toggleSorting}
                      icon={<SortIcon />}
                      iconPosition="left"
                      isDisabled={editing !== undefined}
                      ouiaId="order-toggle"
                    >
                      {sorting ? "End Ordering" : "Order"}
                    </Button>
                  </FlexItem>
                </Flex>
                {!sorting && (
                  <>
                    {validations.current && validations.current.length > 0 && (
                      <section className="data-dictionary__validation-alert">
                        <Alert variant="warning" isInline={true} title="Some items are invalid and need attention." />
                      </section>
                    )}
                    <section className="data-dictionary__types-list" data-ouia-component-id="dd-types-list">
                      {dataTypes.length === 0 && (
                        <Bullseye style={{ height: "40vh" }}>
                          <EmptyDataDictionary />
                        </Bullseye>
                      )}
                      {dataTypes.map((item, index) => (
                        <DataTypeItem
                          dataType={item}
                          editingIndex={editing}
                          index={index}
                          key={index}
                          onSave={handleSave}
                          onEdit={handleEdit}
                          onDelete={handleDelete}
                          onConstraintsEdit={handleConstraintsEdit}
                          onConstraintsSave={handleConstraintsSave}
                          onValidate={dataTypeNameValidation}
                          onOutsideClick={handleOutsideClick}
                        />
                      ))}
                    </section>
                  </>
                )}
                {sorting && (
                  <section className="data-dictionary__types-list">
                    <DataTypesSort dataTypes={dataTypes} onReorder={onReorder} />
                  </section>
                )}
              </section>
            )}
            {viewSection === "batch-add" && (
              <MultipleDataTypeAdd onAdd={handleMultipleAdd} onCancel={() => setViewSection("main")} />
            )}
            {viewSection === "properties" && (
              <DataDictionaryPropertiesEdit
                dataType={editingDataType!}
                dataFieldIndex={editing}
                onClose={exitFromPropertiesEdit}
                onSave={handlePropertiesSave}
              />
            )}
          </>
        </CSSTransition>
      </SwitchTransition>
    </div>
  );
};

export default DataDictionaryContainer;

export interface DDDataField {
  name: string;
  type: "string" | "integer" | "float" | "double" | "boolean";
  optype: "categorical" | "ordinal" | "continuous";
  constraints?: Constraints;
  displayName?: string;
  isCyclic?: boolean;
  missingValue?: string;
  invalidValue?: string;
}

type dataDictionarySection = "main" | "batch-add" | "properties";

export type Constraints =
  | {
      type: ConstraintType.RANGE;
      value: RangeConstraint[];
    }
  | { type: ConstraintType.ENUMERATION; value: string[] };

export interface RangeConstraint {
  start: {
    value: string;
    included: boolean;
  };
  end: {
    value: string;
    included: boolean;
  };
}

export enum ConstraintType {
  RANGE = "Range",
  ENUMERATION = "Enumeration",
  NONE = "",
}
