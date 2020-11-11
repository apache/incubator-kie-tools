import * as React from "react";
import { Button, Split, SplitItem } from "@patternfly/react-core";
import { TrashIcon } from "@patternfly/react-icons";
import "./MiningSchemaFields.scss";
import { MiningField } from "@kogito-tooling/pmml-editor-marshaller";
import MiningSchemaFieldLabels from "../MiningSchemaFieldLabels/MiningSchemaFieldLabels";

interface MiningSchemaFieldsProps {
  fields: MiningField[] | undefined;
  onAddProperties: (index: number) => void;
  onDelete: (index: number) => void;
  onPropertyDelete: (index: number, updatedField: MiningField) => void;
}

const MiningSchemaFields = ({ fields, onAddProperties, onDelete, onPropertyDelete }: MiningSchemaFieldsProps) => {
  return (
    <ul className="mining-schema-list">
      {fields?.map((field, index) => {
        return (
          <MiningSchemaItem
            key={field.name as string}
            field={field}
            index={index}
            onAddProperties={onAddProperties}
            onDelete={onDelete}
            onPropertyDelete={onPropertyDelete}
          />
        );
      })}
    </ul>
  );
};

export default MiningSchemaFields;

interface MiningSchemaFieldProps {
  field: MiningField;
  index: number;
  onAddProperties: (index: number) => void;
  onDelete: (index: number) => void;
  onPropertyDelete: (index: number, field: MiningField) => void;
}

const MiningSchemaItem = ({ field, index, onAddProperties, onDelete, onPropertyDelete }: MiningSchemaFieldProps) => {
  const addProperties = () => {
    onAddProperties(index);
  };
  const deleteField = () => {
    onDelete(index);
  };
  const deleteProperty = (updatedField: MiningField) => {
    onPropertyDelete(index, updatedField);
  };
  return (
    <li className="mining-schema-list__item" key={field.name as string}>
      <Split hasGutter={true}>
        <SplitItem>
          <span className="mining-schema-list__item__name">{field.name}</span>
        </SplitItem>
        <SplitItem isFilled={true}>
          <MiningSchemaFieldLabels field={field} onEdit={addProperties} onDelete={deleteProperty} />
        </SplitItem>
        <SplitItem>
          <Button variant="plain" onClick={deleteField}>
            <TrashIcon />
          </Button>
        </SplitItem>
      </Split>
    </li>
  );
};
