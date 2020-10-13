import * as React from "react";
import { useState } from "react";
import { Button, Stack, StackItem } from "@patternfly/react-core";
import { v4 as uuid } from "uuid";
import "./DataDictionaryContainer.scss";
import DataTypeItem from "../DataTypeItem/DataTypeItem";

const DataDictionaryContainer = () => {
  const [dataTypes, setDataTypes] = useState<DataType[]>([
    {
      name: "Age",
      type: "Number"
    }
  ]);
  const [newType, setNewType] = useState(false);
  const [editing, setEditing] = useState<number | boolean>(false);

  const addDataType = () => {
    setNewType(true);
    setEditing(-1);
  };

  const onSaveDataType = (dataType: DataType, index: number) => {
    if (index === -1) {
      if (dataType.name.length > 0 && dataType.type !== "Select Type") {
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

  const handleEdit = (index: number) => {
    setEditing(index);
    if (newType) {
      setNewType(false);
    }
  };

  return (
    <div className="data-dictionary">
      <StatusContext.Provider value={editing}>
        <Stack hasGutter={true}>
          <StackItem>
            <Button onClick={addDataType} isDisabled={newType}>
              Add Data Type
            </Button>
          </StackItem>
          <StackItem>
            {dataTypes.map((item, index) => (
              <DataTypeItem dataType={item} index={index} key={uuid()} onSave={onSaveDataType} onEdit={handleEdit} />
            ))}
            {newType && (
              <DataTypeItem
                dataType={{ name: "", type: "Select Type" }}
                index={-1}
                key={uuid()}
                onSave={onSaveDataType}
              />
            )}
          </StackItem>
        </Stack>
      </StatusContext.Provider>
    </div>
  );
};

export const StatusContext = React.createContext<number | boolean>(false);

export default DataDictionaryContainer;

export interface DataType {
  name: string;
  type: string;
}
