import * as React from "react";
import { useEffect, useState } from "react";
import { CSSTransition, SwitchTransition } from "react-transition-group";
import { Bullseye, Button, Flex, FlexItem } from "@patternfly/react-core";
import { BoltIcon, PlusIcon, SortIcon } from "@patternfly/react-icons";
import DataTypeItem from "../DataTypeItem/DataTypeItem";
import MultipleDataTypeAdd from "../MultipleDataTypeAdd/MultipleDataTypeAdd";
import DataTypesSort from "../DataTypesSort/DataTypesSort";
import EmptyDataDictionary from "../EmptyDataDictionary/EmptyDataDictionary";
import { findIncrementalName } from "../../../PMMLModelHelper";
import "./DataDictionaryContainer.scss";
import DataDictionaryPropertiesEdit from "../DataDictionaryPropertiesEdit/DataDictionaryPropertiesEdit";
import { isEqual } from "lodash";

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
        dataTypes.map(dt => dt.name),
        1
      ),
      "string",
      "categorical"
    );
    setEditing(dataTypes.length);
    onEditingPhaseChange(true);
  };

  const saveDataType = (dataType: DDDataField, index: number) => {
    console.log("updating data type");
    onEdit(index, dataTypes[index].name, dataType);
  };

  const handleSave = (dataType: DDDataField, index: number) => {
    saveDataType(dataType, index);
  };

  const handleDelete = (index: number) => {
    onDelete(index);
  };

  const handleEdit = (index: number) => {
    setEditing(index);
    onEditingPhaseChange(true);
  };

  const handleMultipleAdd = (fields: string) => {
    const fieldsNames = fields.split("\n").filter(item => item.trim().length > 0);
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
      Object.keys(payload).forEach(key => Reflect.set(existingPartial, key, Reflect.get(dataType, key)));

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

  return (
    <div className="data-dictionary">
      <SwitchTransition mode={"out-in"}>
        <CSSTransition
          timeout={{
            enter: 230,
            exit: 100
          }}
          classNames={getTransition(viewSection)}
          key={viewSection}
        >
          <>
            {viewSection === "main" && (
              <section style={{ height: "100%" }}>
                <Flex style={{ padding: "1em 0" }}>
                  <FlexItem>
                    <Button
                      variant="primary"
                      onClick={addDataType}
                      icon={<PlusIcon />}
                      iconPosition="left"
                      isDisabled={editing !== undefined || sorting}
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
                    >
                      {sorting ? "End Ordering" : "Order"}
                    </Button>
                  </FlexItem>
                </Flex>
                {!sorting && (
                  <section className="data-dictionary__types-list">
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
      type: "Range";
      value: RangeConstraint[];
    }
  | { type: "Enumeration"; value: string[] };

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
