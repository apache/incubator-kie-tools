import * as React from "react";
import { useState } from "react";
import { Breadcrumb, BreadcrumbItem, Button, Flex, FlexItem, Stack, StackItem } from "@patternfly/react-core";
import { v4 as uuid } from "uuid";
import { BoltIcon, PlusCircleIcon, SortIcon } from "@patternfly/react-icons";
import "./DataDictionaryContainer.scss";
import DataTypeItem from "../DataTypeItem/DataTypeItem";
import MultipleDataTypeAdd from "../MultipleDataTypeAdd/MultipleDataTypeAdd";

const DataDictionaryContainer = () => {
  const [dataTypes, setDataTypes] = useState<DataType[]>([
    {
      name: "Name",
      type: "String",
      list: false
    }
  ]);
  const [newType, setNewType] = useState(false);
  const [editing, setEditing] = useState<number | boolean>(false);
  const [showBatchAdd, setShowBatchAdd] = useState(false);

  const addDataType = () => {
    setNewType(true);
    setEditing(-1);
  };

  const handleSave = (dataType: DataType, index: number) => {
    if (index === -1) {
      if (dataType.name.length > 0) {
        setDataTypes([...dataTypes, dataType]);
      } else {
        setNewType(false);
      }
    } else {
      const newTypes = [...dataTypes];
      newTypes[index] = dataType;
      setDataTypes(newTypes);
    }
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
    console.log("new data types following");
    console.log(newDataTypes);
    setDataTypes([...dataTypes, ...newDataTypes]);
    setShowBatchAdd(false);
  };

  return (
    <div className="data-dictionary">
      <StatusContext.Provider value={editing}>
        {!showBatchAdd && (
          <Stack hasGutter={true}>
            <StackItem style={{ margin: "1em 1em 2em 0" }}>
              <Flex>
                <FlexItem>
                  <Button
                    variant="secondary"
                    onClick={addDataType}
                    isDisabled={editing !== false}
                    icon={<PlusCircleIcon />}
                    iconPosition="right"
                  >
                    Add Data Type
                  </Button>
                </FlexItem>
                <FlexItem>
                  <Button
                    variant="secondary"
                    onClick={() => setShowBatchAdd(true)}
                    isDisabled={editing !== false}
                    icon={<BoltIcon />}
                    iconPosition="right"
                  >
                    Add Multiple Data Types
                  </Button>
                </FlexItem>
                <FlexItem align={{ default: "alignRight" }}>
                  <Button variant="secondary" isDisabled={editing !== false} icon={<SortIcon />} iconPosition="right">
                    Sort
                  </Button>
                </FlexItem>
              </Flex>
            </StackItem>
            <StackItem className="data-dictionary__types-list">
              {dataTypes.map((item, index) => (
                <DataTypeItem
                  dataType={item}
                  index={index}
                  key={uuid()}
                  onSave={handleSave}
                  onEdit={handleEdit}
                  onDelete={handleDelete}
                />
              ))}
              {newType && (
                <DataTypeItem
                  dataType={{ name: "", type: "String", list: false }}
                  index={-1}
                  key={uuid()}
                  onSave={handleSave}
                />
              )}
            </StackItem>
          </Stack>
        )}
        {showBatchAdd && (
          <Stack hasGutter={true}>
            <StackItem>
              <Breadcrumb>
                <BreadcrumbItem to="#" onClick={() => setShowBatchAdd(false)}>
                  Data Dictionary
                </BreadcrumbItem>
                <BreadcrumbItem isActive={true}>Add Multiple Data Types</BreadcrumbItem>
              </Breadcrumb>
            </StackItem>
            <StackItem>
              <MultipleDataTypeAdd onAdd={handleMultipleAdd} onCancel={() => setShowBatchAdd(false)} />
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
}
