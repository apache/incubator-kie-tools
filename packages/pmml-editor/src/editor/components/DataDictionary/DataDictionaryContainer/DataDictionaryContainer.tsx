import * as React from "react";
import { useEffect, useState } from "react";
import { Breadcrumb, BreadcrumbItem, Button, Flex, FlexItem, Stack, StackItem } from "@patternfly/react-core";
import { v4 as uuid } from "uuid";
import { BoltIcon, PlusIcon, SortIcon } from "@patternfly/react-icons";
import "./DataDictionaryContainer.scss";
import DataTypeItem from "../DataTypeItem/DataTypeItem";
import MultipleDataTypeAdd from "../MultipleDataTypeAdd/MultipleDataTypeAdd";
import ConstraintsEdit from "../ConstraintsEdit/ConstraintsEdit";
import DataTypesSort from "../DataTypesSort/DataTypesSort";

let dataDictionary = [
  {
    name: "Name",
    type: "String",
    list: false
  }
];

const DataDictionaryContainer = () => {
  const [dataTypes, setDataTypes] = useState<DataType[]>(dataDictionary);
  const [newType, setNewType] = useState(false);
  const [editing, setEditing] = useState<number | boolean>(false);
  const [showBatchAdd, setShowBatchAdd] = useState(false);
  const [constrainsEdit, setConstraintsEdit] = useState<{ show: true; dataType: DataType } | { show: false }>({
    show: false
  });
  const [sorting, setSorting] = useState(false);

  useEffect(() => {
    dataDictionary = dataTypes;
  }, [dataTypes]);

  const addDataType = () => {
    setNewType(true);
    setEditing(-1);
  };

  const saveDataType = (dataType: DataType, index: number) => {
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

  const handleSave = (dataType: DataType, index: number) => {
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
    const newDataTypes = typesNames.map(name => {
      return { name: name.trim(), type: "String", list: false };
    });
    setDataTypes([...dataTypes, ...newDataTypes]);
    setShowBatchAdd(false);
  };

  const handleConstraintsEdit = (dataType: DataType) => {
    if (typeof editing === "number") {
      saveDataType(dataType, editing);
      if (editing === -1) {
        setNewType(false);
        setEditing(dataTypes.length);
      }
      // not sure about the following. passing around dataType instead of waiting for the dataTypes to update
      setConstraintsEdit({ show: true, dataType: dataType });
    }
  };

  const handleConstraintsSave = (payload: Constraints) => {
    setConstraintsEdit({ show: false });
    if (typeof editing === "number") {
      const newTypes = [...dataTypes];
      newTypes[editing] = { ...newTypes[editing], constraints: payload };
      setDataTypes(newTypes);
    }
  };
  const handleConstraintsDelete = () => {
    setConstraintsEdit({ show: false });
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

  const handleSorting = (sortedDataTypes: DataType[]) => {
    setDataTypes(sortedDataTypes);
  };

  return (
    <div className="data-dictionary">
      <StatusContext.Provider value={editing}>
        {!showBatchAdd && !constrainsEdit.show && (
          <Stack hasGutter={true}>
            <StackItem style={{ margin: "1em 0 2em 0" }}>
              <Flex>
                <FlexItem>
                  <Button
                    variant="secondary"
                    onClick={addDataType}
                    isDisabled={editing !== false || sorting}
                    icon={<PlusIcon />}
                    iconPosition="left"
                  >
                    Add Data Type
                  </Button>
                </FlexItem>
                <FlexItem>
                  <Button
                    variant="secondary"
                    onClick={() => setShowBatchAdd(true)}
                    isDisabled={editing !== false || sorting}
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
                    isDisabled={editing !== false || dataTypes.length < 2}
                    icon={<SortIcon />}
                    iconPosition="left"
                  >
                    {sorting ? "End Sorting" : "Sort"}
                  </Button>
                </FlexItem>
              </Flex>
            </StackItem>
            {!sorting && (
              <StackItem className="data-dictionary__types-list">
                {dataTypes.map((item, index) => (
                  <DataTypeItem
                    dataType={item}
                    index={index}
                    key={uuid()}
                    onSave={handleSave}
                    onEdit={handleEdit}
                    onDelete={handleDelete}
                    onConstraintsEdit={handleConstraintsEdit}
                  />
                ))}
                {newType && (
                  <DataTypeItem
                    dataType={{ name: "", type: "String", list: false }}
                    index={-1}
                    key={uuid()}
                    onSave={handleSave}
                    onConstraintsEdit={handleConstraintsEdit}
                  />
                )}
              </StackItem>
            )}
            {sorting && (
              <StackItem className="data-dictionary__types-list">
                <DataTypesSort dataTypes={dataTypes} onSort={handleSorting} />
              </StackItem>
            )}
          </Stack>
        )}
        {showBatchAdd && (
          <Stack hasGutter={true}>
            <StackItem>
              <Breadcrumb>
                <BreadcrumbItem component="span" onClick={() => setShowBatchAdd(false)}>
                  <Button variant={"link"} isInline={true}>
                    Data Dictionary
                  </Button>
                </BreadcrumbItem>
                <BreadcrumbItem isActive={true}>Add Multiple Data Types</BreadcrumbItem>
              </Breadcrumb>
            </StackItem>
            <StackItem>
              <MultipleDataTypeAdd onAdd={handleMultipleAdd} onCancel={() => setShowBatchAdd(false)} />
            </StackItem>
          </Stack>
        )}
        {constrainsEdit.show && (
          <Stack hasGutter={true}>
            <StackItem>
              <Breadcrumb>
                <BreadcrumbItem component="span" onClick={() => setConstraintsEdit({ show: false })}>
                  <Button variant={"link"} isInline={true}>
                    Data Dictionary
                  </Button>
                </BreadcrumbItem>
                <BreadcrumbItem isActive={true}>Constraints</BreadcrumbItem>
              </Breadcrumb>
            </StackItem>
            <StackItem>
              <ConstraintsEdit
                dataType={constrainsEdit!.dataType}
                onAdd={handleConstraintsSave}
                onDelete={handleConstraintsDelete}
              />
            </StackItem>
          </Stack>
        )}
      </StatusContext.Provider>
    </div>
  );
};

export const StatusContext = React.createContext<number | boolean>(false);

export default DataDictionaryContainer;

export interface DataType {
  name: string;
  type: string;
  list: boolean;
  constraints?: Constraints;
}

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
  | { type: "Enumeration"; value: string };
