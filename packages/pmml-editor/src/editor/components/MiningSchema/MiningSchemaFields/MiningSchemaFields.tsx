import * as React from "react";
import { Button, ButtonVariant, Flex, FlexItem } from "@patternfly/react-core";
import { AngleRightIcon, TrashIcon } from "@patternfly/react-icons";
import { MiningSchemaField } from "../MiningSchemaContainer/MiningSchemaContainer";
import "./MiningSchemaFields.scss";

interface MiningSchemaFieldsProps {
  fields: MiningSchemaField[];
  onAddProperties: (index: number) => void;
  onDelete: (index: number) => void;
}

const MiningSchemaFields = ({ fields, onAddProperties, onDelete }: MiningSchemaFieldsProps) => {
  return (
    <ul className="mining-schema-list">
      {fields.map((field, index) => {
        if (field.isSelected) {
          return <MiningSchemaItem field={field} index={index} onAddProperties={onAddProperties} onDelete={onDelete} />;
        }
      })}
    </ul>
  );
};

export default MiningSchemaFields;

interface MiningSchemaFieldProps {
  field: MiningSchemaField;
  index: number;
  onAddProperties: (index: number) => void;
  onDelete: (index: number) => void;
}

const MiningSchemaItem = ({ field, index, onAddProperties, onDelete }: MiningSchemaFieldProps) => {
  const addProperties = () => {
    onAddProperties(index);
  };
  const deleteField = () => {
    onDelete(index);
  };
  return (
    <li className="mining-schema-list__item" key={field.name}>
      <Flex>
        <FlexItem>{field.name}</FlexItem>
        <FlexItem>
          <Button variant={ButtonVariant.link} icon={<AngleRightIcon />} iconPosition="right" onClick={addProperties}>
            Add Properties
          </Button>
        </FlexItem>
        <FlexItem align={{ default: "alignRight" }}>
          <Button variant="plain" onClick={deleteField}>
            <TrashIcon />
          </Button>
        </FlexItem>
      </Flex>
    </li>
  );
};
