import * as React from "react";
import { useEffect, useState } from "react";
import { Bullseye, Button, Flex, FlexItem } from "@patternfly/react-core";
import { BoltIcon, PlusIcon, SortIcon } from "@patternfly/react-icons";
import { CSSTransition, SwitchTransition } from "react-transition-group";
import { differenceWith, isEqual, findIndex } from "lodash";
import DataTypeItem from "../DataTypeItem/DataTypeItem";
import MultipleDataTypeAdd from "../MultipleDataTypeAdd/MultipleDataTypeAdd";
import ConstraintsEdit from "../ConstraintsEdit/ConstraintsEdit";
import DataTypesSort from "../DataTypesSort/DataTypesSort";
import EmptyDataDictionary from "../EmptyDataDictionary/EmptyDataDictionary";
import { findIncrementalName } from "../../../PMMLModelHelper";
import "./DataDictionaryContainer.scss";

interface DataDictionaryContainerProps {
  dataDictionary: DDDataField[];
  onUpdate: (updatedDictionary: DDDataField[]) => void;
  onAdd: (name: string, type: DDDataField["type"]) => void;
  onEdit: (index: number, field: DDDataField) => void;
  onDelete: (index: number) => void;
  onBatchAdd: (fields: string[]) => void;
}

const DataDictionaryContainer = (props: DataDictionaryContainerProps) => {
  const { dataDictionary, onUpdate, onAdd, onEdit, onDelete, onBatchAdd } = props;
  const [dataTypes, setDataTypes] = useState<DDDataField[]>(dataDictionary);
  const [editing, setEditing] = useState<number | boolean>(false);
  const [viewSection, setViewSection] = useState<dataDictionarySection>("main");
  const [constrainsEdit, setConstraintsEdit] = useState<DDDataField>();
  const [sorting, setSorting] = useState(false);

  useEffect(() => {
    // onUpdate(dataTypes);
    // console.table(dataTypes);
  }, [dataTypes]);

  useEffect(() => {
    // before reflecting datadictionary updates to local state,
    // let's see if there are changes OUTSIDE of the currently edited item or
    // if the changes involve deleted new items
    // console.log("DIFF");
    // const diff: DDDataField[] = differenceWith(dataTypes, dataDictionary, isEqual);
    // console.table(diff);
    // if (diff.length) {
    //   const position = findIndex(dataTypes, item => item.name === diff[0].name);
    //   if (position === dataDictionary.length || position !== editing) {
    //     setEditing(false);
    //   }
    // }
    setDataTypes(dataDictionary);
  }, [dataDictionary]);

  const handleOutsideClick = () => {
    //handleEmptyFields();
    setEditing(false);
  };

  const addDataType = () => {
    // const newTypes = dataTypes;
    // newTypes.push({ name: "", type: "string" });
    // setDataTypes(newTypes);
    onAdd(
      findIncrementalName(
        "New Data Type",
        dataTypes.map(dt => dt.name),
        1
      ),
      "string"
    );
    setEditing(dataTypes.length);
  };

  const handleEmptyFields = () => {
    if (dataTypes[dataTypes.length - 1].name.trim().length === 0) {
      const newDataTypes = dataTypes;
      newDataTypes[newDataTypes.length - 1].name = findIncrementalName(
        "New Data Type",
        dataTypes.map(dt => dt.name),
        1
      );
      setDataTypes(newDataTypes);
    }
  };

  const saveDataType = (dataType: DDDataField, index: number) => {
    // if (!dataTypeNameValidation(dataType.name)) {
    //   dataType.name = findIncrementalName(
    //     dataType.name,
    //     dataTypes.map(dt => dt.name),
    //     2
    //   );
    // }
    console.log("updating data type");
    onEdit(index, dataType);
    // const newTypes = dataTypes;
    // newTypes[index] = { ...newTypes[index], ...dataType };
    // setDataTypes(newTypes);
  };

  const handleSave = (dataType: DDDataField, index: number) => {
    saveDataType(dataType, index);
  };

  const handleDelete = (index: number) => {
    // const newDataTypes = [...dataTypes];
    // newDataTypes.splice(index, 1);
    // setDataTypes(newDataTypes);
    onDelete(index);
  };

  const handleEdit = (index: number) => {
    console.log("setting editing to " + index);
    // handleEmptyFields();
    setEditing(index);
  };

  const handleMultipleAdd = (fields: string) => {
    const fieldsNames = fields.split("\n").filter(item => item.trim().length > 0);
    // const newDataTypes: DDDataField[] = typesNames.map(name => {
    //   return { name: name.trim(), type: "string" };
    // });
    // setDataTypes([...dataTypes, ...newDataTypes]);
    onBatchAdd(fieldsNames);
    setViewSection("main");
  };

  const handleConstraintsEdit = (dataType: DDDataField) => {
    if (typeof editing === "number") {
      saveDataType(dataType, editing);
      setConstraintsEdit(dataType);
      setViewSection("constraints");
    }
  };

  const handleConstraintsSave = (payload: Constraints) => {
    setViewSection("main");
    if (typeof editing === "number") {
      const newTypes = [...dataTypes];
      newTypes[editing] = { ...newTypes[editing], constraints: payload };
      setDataTypes(newTypes);
    }
  };
  const handleConstraintsDelete = () => {
    setViewSection("main");
    if (typeof editing === "number") {
      const newTypes = [...dataTypes];
      newTypes[editing] = { ...newTypes[editing] };
      delete newTypes[editing].constraints;
      setDataTypes(newTypes);
    }
  };

  const toggleSorting = () => {
    // handleEmptyFields();
    setEditing(false);
    setSorting(!sorting);
  };

  const handleSorting = (sortedDataTypes: DDDataField[]) => {
    setDataTypes(sortedDataTypes);
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
      <StatusContext.Provider value={editing}>
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
                        isDisabled={editing !== false || sorting}
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
                        isDisabled={editing !== false || sorting}
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
                        isDisabled={editing !== false}
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
                          index={index}
                          key={index}
                          onSave={handleSave}
                          onEdit={handleEdit}
                          onDelete={handleDelete}
                          onConstraintsEdit={handleConstraintsEdit}
                          onConstraintsDelete={handleConstraintsDelete}
                          onValidate={dataTypeNameValidation}
                          onOutsideClick={handleOutsideClick}
                        />
                      ))}
                    </section>
                  )}
                  {sorting && (
                    <section className="data-dictionary__types-list">
                      <DataTypesSort dataTypes={dataTypes} onSort={handleSorting} />
                    </section>
                  )}
                </section>
              )}
              {viewSection === "batch-add" && (
                <>
                  <MultipleDataTypeAdd onAdd={handleMultipleAdd} onCancel={() => setViewSection("main")} />
                </>
              )}
              {viewSection === "constraints" && (
                <ConstraintsEdit
                  dataType={constrainsEdit!}
                  onAdd={handleConstraintsSave}
                  onDelete={handleConstraintsDelete}
                />
              )}
            </>
          </CSSTransition>
        </SwitchTransition>
      </StatusContext.Provider>
    </div>
  );
};

export const StatusContext = React.createContext<number | boolean>(false);

export default DataDictionaryContainer;

export interface DDDataField {
  name: string;
  type: "string" | "integer" | "float" | "double" | "boolean";
  constraints?: Constraints;
}

type dataDictionarySection = "main" | "batch-add" | "constraints";

export type Constraints =
  | {
      type: "Range";
      start: {
        value: "";
        included: false;
      };
      end: {
        value: "";
        included: false;
      };
    }
  | { type: "Enumeration"; value: EnumConstraint[] };

export interface EnumConstraint {
  value: string;
  id: string;
}
