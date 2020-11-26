import * as React from "react";
import { useEffect, useState } from "react";
import { Bullseye, Button, Flex, FlexItem } from "@patternfly/react-core";
import { BoltIcon, PlusIcon, SortIcon } from "@patternfly/react-icons";
import { CSSTransition, SwitchTransition } from "react-transition-group";

import DataTypeItem from "../DataTypeItem/DataTypeItem";
import MultipleDataTypeAdd from "../MultipleDataTypeAdd/MultipleDataTypeAdd";
import ConstraintsEdit from "../ConstraintsEdit/ConstraintsEdit";
import DataTypesSort from "../DataTypesSort/DataTypesSort";
import "./DataDictionaryContainer.scss";
import EmptyDataDictionary from "../EmptyDataDictionary/EmptyDataDictionary";

interface DataDictionaryContainerProps {
  dataDictionary: DataField[];
  onUpdate: (updatedDictionary: DataField[]) => void;
}

const DataDictionaryContainer = ({ dataDictionary, onUpdate }: DataDictionaryContainerProps) => {
  const [dataTypes, setDataTypes] = useState<DataField[]>(dataDictionary);
  const [editing, setEditing] = useState<number | boolean>(false);
  const [viewSection, setViewSection] = useState<dataDictionarySection>("main");
  const [constrainsEdit, setConstraintsEdit] = useState<DataField>();
  const [sorting, setSorting] = useState(false);

  useEffect(() => {
    onUpdate(dataTypes);
  }, [dataTypes]);

  const handleOutsideClick = () => {
    handleEmptyFields();
    setEditing(false);
  };

  const addDataType = () => {
    const newTypes = dataTypes;
    newTypes.push({ name: "", type: "string" });
    setDataTypes(newTypes);
    setEditing(newTypes.length - 1);
  };

  const handleEmptyFields = () => {
    if (dataTypes[dataTypes.length - 1].name.trim().length === 0) {
      const newDataTypes = dataTypes;
      newDataTypes[newDataTypes.length - 1].name = findIncrementalName("New Data Type", 1);
      setDataTypes(newDataTypes);
    }
  };

  const saveDataType = (dataType: DataField, index: number) => {
    if (!dataTypeNameValidation(dataType.name)) {
      dataType.name = findIncrementalName(dataType.name, 2);
    }
    const newTypes = dataTypes;
    newTypes[index] = { ...newTypes[index], ...dataType };
    console.log("updating data type");
    setDataTypes(newTypes);
  };

  const handleSave = (dataType: DataField, index: number) => {
    saveDataType(dataType, index);
  };

  const handleDelete = (index: number) => {
    const newDataTypes = [...dataTypes];
    newDataTypes.splice(index, 1);
    setDataTypes(newDataTypes);
  };

  const handleEdit = (index: number) => {
    console.log("setting editing to " + index);
    handleEmptyFields();
    setEditing(index);
  };

  const handleMultipleAdd = (types: string) => {
    const typesNames = types.split("\n").filter(item => item.trim().length > 0);
    const newDataTypes: DataField[] = typesNames.map(name => {
      return { name: name.trim(), type: "string" };
    });
    setDataTypes([...dataTypes, ...newDataTypes]);
    setViewSection("main");
  };

  const handleConstraintsEdit = (dataType: DataField) => {
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
    handleEmptyFields();
    setEditing(false);
    setSorting(!sorting);
  };

  const handleSorting = (sortedDataTypes: DataField[]) => {
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

  // TODO {kelvah} rough implementation for demoing purposes. to be done properly.
  const findIncrementalName = (name: string, startsFrom: number): string => {
    let newName = "";
    let counter = startsFrom;
    do {
      const potentialName = `${name}${counter !== 1 ? ` ${counter}` : ""}`;
      const found = dataTypes.filter(item => item.name === potentialName);
      if (found.length === 0) {
        newName = potentialName;
      }
      counter++;
    } while (newName.length === 0);
    return newName;
  };

  return (
    <div className="data-dictionary">
      <StatusContext.Provider value={editing}>
        <Flex style={{ margin: "1em 0 2em 0" }}>
          <FlexItem>
            <Button variant="primary" onClick={addDataType} icon={<PlusIcon />} iconPosition="left">
              Add Data Type
            </Button>
          </FlexItem>
          <FlexItem>
            <Button
              variant="secondary"
              onClick={() => setViewSection("batch-add")}
              icon={<BoltIcon />}
              iconPosition="left"
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
            >
              {sorting ? "End Sorting" : "Sort"}
            </Button>
          </FlexItem>
        </Flex>
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
                <>
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
                          key={item.name}
                          onSave={handleSave}
                          onEdit={handleEdit}
                          onDelete={handleDelete}
                          onConstraintsEdit={handleConstraintsEdit}
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
                </>
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

export interface DataField {
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
