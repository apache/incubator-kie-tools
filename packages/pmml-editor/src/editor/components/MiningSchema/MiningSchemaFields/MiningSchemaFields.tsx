import * as React from "react";
import { useContext, useMemo } from "react";
import { Button, Flex, FlexItem, Split, SplitItem } from "@patternfly/react-core";
import { TrashIcon } from "@patternfly/react-icons";
import { MiningField } from "@kogito-tooling/pmml-editor-marshaller";
import { MiningSchemaContext } from "../MiningSchemaContainer/MiningSchemaContainer";
import useOnclickOutside from "react-cool-onclickoutside";
import MiningSchemaFieldLabels from "../MiningSchemaFieldLabels/MiningSchemaFieldLabels";
import "./MiningSchemaFields.scss";
import { useValidationService } from "../../../validation";
import { ValidationIndicator } from "../../EditorCore/atoms";

interface MiningSchemaFieldsProps {
  modelIndex: number;
  fields: MiningField[] | undefined;
  onAddProperties: (index: number) => void;
  onDelete: (index: number) => void;
  onPropertyDelete: (index: number, updatedField: MiningField) => void;
  onEdit: (index: number) => void;
  onCancel: () => void;
}

const MiningSchemaFields = (props: MiningSchemaFieldsProps) => {
  const { fields, modelIndex, onAddProperties, onDelete, onPropertyDelete, onEdit, onCancel } = props;
  return (
    <ul className="mining-schema-list">
      {fields?.map((field, index) => {
        return (
          <MiningSchemaItem
            key={field.name as string}
            field={field}
            index={index}
            modelIndex={modelIndex}
            onAddProperties={onAddProperties}
            onDelete={onDelete}
            onPropertyDelete={onPropertyDelete}
            onEdit={onEdit}
            onCancel={onCancel}
          />
        );
      })}
    </ul>
  );
};

export default MiningSchemaFields;

interface MiningSchemaFieldProps {
  index: number;
  modelIndex: number;
  field: MiningField;
  onAddProperties: (index: number) => void;
  onDelete: (index: number) => void;
  onPropertyDelete: (index: number, field: MiningField) => void;
  onEdit: (index: number) => void;
  onCancel: () => void;
}

const MiningSchemaItem = (props: MiningSchemaFieldProps) => {
  const { index, modelIndex, field, onAddProperties, onDelete, onPropertyDelete, onEdit, onCancel } = props;
  const editing = useContext(MiningSchemaContext);

  const ref = useOnclickOutside(
    () => {
      onCancel();
    },
    {
      disabled: editing !== index,
      eventTypes: ["click"]
    }
  );

  const addProperties = () => {
    onAddProperties(index);
  };

  const deleteField = (event: React.MouseEvent<HTMLButtonElement, MouseEvent>) => {
    event.preventDefault();
    event.stopPropagation();
    onDelete(index);
  };

  const deleteProperty = (updatedField: MiningField) => {
    onPropertyDelete(index, updatedField);
  };

  const handleEdit = () => {
    onEdit(index);
  };

  const { service } = useValidationService();
  const validations = useMemo(() => service.get(`models[${modelIndex}].MiningSchema.MiningField[${index}]`), [
    index,
    modelIndex,
    field
  ]);

  return (
    <li
      className={`editable-item ${editing === index ? "editable-item--editing" : ""}`}
      key={field.name.value}
      onClick={handleEdit}
      ref={ref}
      tabIndex={0}
      onKeyDown={event => {
        if (event.key === "Enter") {
          event.preventDefault();
          event.stopPropagation();
          handleEdit();
        }
        if (event.key === "Escape") {
          onCancel();
        }
      }}
    >
      <section className={"editable-item__inner"}>
        <Split hasGutter={true}>
          {index !== editing && validations.length > 0 && (
            <SplitItem>
              <Flex
                alignItems={{ default: "alignItemsCenter" }}
                justifyContent={{ default: "justifyContentCenter" }}
                style={{ height: "100%" }}
              >
                <FlexItem>
                  <ValidationIndicator validations={validations} />
                </FlexItem>
              </Flex>
            </SplitItem>
          )}
          <SplitItem>
            <span className="mining-schema-list__item__name">{field.name}</span>
          </SplitItem>
          <SplitItem isFilled={true}>
            <MiningSchemaFieldLabels
              modelIndex={modelIndex}
              index={index}
              field={field}
              onEdit={addProperties}
              onDelete={deleteProperty}
              editing={index === editing}
            />
          </SplitItem>
          <SplitItem>
            <Button variant="plain" onClick={deleteField}>
              <TrashIcon />
            </Button>
          </SplitItem>
        </Split>
      </section>
    </li>
  );
};
