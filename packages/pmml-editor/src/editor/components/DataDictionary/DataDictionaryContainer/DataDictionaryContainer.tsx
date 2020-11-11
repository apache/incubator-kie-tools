import * as React from "react";
import { useEffect, useState } from "react";
import { Bullseye, Button, Flex, FlexItem } from "@patternfly/react-core";
import { BoltIcon, PlusIcon, SortIcon } from "@patternfly/react-icons";
import { v4 as uuid } from "uuid";
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
  const [newType, setNewType] = useState(false);
  const [editing, setEditing] = useState<number | boolean>(false);
  const [viewSection, setViewSection] = useState<dataDictionarySection>("main");
  const [constrainsEdit, setConstraintsEdit] = useState<DataField>();
  const [sorting, setSorting] = useState(false);

  useEffect(() => {
    onUpdate(dataTypes);
  }, [dataTypes]);

  const addDataType = () => {
    setNewType(true);
    setEditing(-1);
  };

  const saveDataType = (dataType: DataField, index: number) => {
    if (index === -1) {
      if (dataType.name.length > 0) {
        setDataTypes([...dataTypes, dataType]);
      } else {
        setNewType(false);
      }
    } else {
      const newTypes = [...dataTypes];
      newTypes[index] = { ...newTypes[index], ...dataType };
      setDataTypes(newTypes);
    }
  };

  const handleSave = (dataType: DataField, index: number) => {
    saveDataType(dataType, index);
    setNewType(false);
    setEditing(false);
  };

  const handleDelete = (index: number) => {
    const newDataTypes = [...dataTypes];
    newDataTypes.splice(index, 1);
    setDataTypes(newDataTypes);
  };

  const handleEdit = (index: number) => {
    setEditing(index);
    if (newType) {
      setNewType(false);
    }
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
      if (editing === -1) {
        setNewType(false);
        setEditing(dataTypes.length);
      }
      // to be improved. waiting for the dataTypes to update instead of passing around dataType
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

  return (
    <div className="data-dictionary">
      <StatusContext.Provider value={editing}>
        <Flex style={{ margin: "1em 0 2em 0" }}>
          <FlexItem>
            <Button
              variant="secondary"
              onClick={addDataType}
              isDisabled={editing !== false || sorting || viewSection !== "main"}
              icon={<PlusIcon />}
              iconPosition="left"
            >
              Add Data Type
            </Button>
          </FlexItem>
          <FlexItem>
            <Button
              variant="secondary"
              onClick={() => setViewSection("batch-add")}
              isDisabled={editing !== false || sorting || viewSection !== "main"}
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
              isDisabled={editing !== false || dataTypes.length < 2 || viewSection !== "main"}
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
                      {dataTypes.length === 0 && !newType && (
                        <Bullseye style={{ height: "40vh" }}>
                          <EmptyDataDictionary />
                        </Bullseye>
                      )}
                      {dataTypes.map((item, index) => (
                        <DataTypeItem
                          dataType={item}
                          index={index}
                          key={uuid()}
                          onSave={handleSave}
                          onEdit={handleEdit}
                          onDelete={handleDelete}
                          onConstraintsEdit={handleConstraintsEdit}
                          onValidate={dataTypeNameValidation}
                        />
                      ))}
                      {newType && (
                        <DataTypeItem
                          dataType={{ name: "", type: "string" }}
                          index={-1}
                          key={uuid()}
                          onSave={handleSave}
                          onConstraintsEdit={handleConstraintsEdit}
                          onValidate={dataTypeNameValidation}
                        />
                      )}
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
