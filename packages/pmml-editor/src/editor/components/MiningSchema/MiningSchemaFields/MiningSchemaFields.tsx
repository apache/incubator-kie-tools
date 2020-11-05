import * as React from "react";
import { MiningSchemaField } from "../MiningSchemaContainer/MiningSchemaContainer";
import "./MiningSchemaFields.scss";

interface MiningSchemaFieldsProps {
  fields: MiningSchemaField[];
}

const MiningSchemaFields = ({ fields }: MiningSchemaFieldsProps) => {
  return (
    <ul className="mining-schema-list">
      {fields.map(field => {
        if (field.isSelected) {
          return <li className="mining-schema-list__item">{field.name}</li>;
        }
      })}
    </ul>
  );
};

export default MiningSchemaFields;
