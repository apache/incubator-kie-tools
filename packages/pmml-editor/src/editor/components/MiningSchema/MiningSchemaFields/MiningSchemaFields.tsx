import * as React from "react";
import { BaseSyntheticEvent, useContext, useMemo } from "react";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { Split, SplitItem } from "@patternfly/react-core/dist/js/layouts/Split";
import { TrashIcon } from "@patternfly/react-icons/dist/js/icons/trash-icon";
import { DataDictionary, MiningField } from "@kogito-tooling/pmml-editor-marshaller";
import { MiningSchemaContext } from "../MiningSchemaContainer/MiningSchemaContainer";
import useOnclickOutside from "react-cool-onclickoutside";
import MiningSchemaFieldLabels from "../MiningSchemaFieldLabels/MiningSchemaFieldLabels";
import "./MiningSchemaFields.scss";
import { useValidationRegistry } from "../../../validation";
import { Builder } from "../../../paths";
import { ValidationIndicator } from "../../EditorCore/atoms";

interface MiningSchemaFieldsProps {
  modelIndex: number;
  dataDictionary?: DataDictionary;
  fields: MiningField[] | undefined;
  onAddProperties: (index: number) => void;
  onDelete: (index: number, name: string) => void;
  onPropertyDelete: (index: number, updatedField: MiningField) => void;
  onEdit: (index: number) => void;
  onCancel: () => void;
}

const MiningSchemaFields = (props: MiningSchemaFieldsProps) => {
  const { dataDictionary, fields, modelIndex, onAddProperties, onDelete, onPropertyDelete, onEdit, onCancel } = props;
  return (
    <ul className="mining-schema-list">
      {fields?.map((field, index) => {
        return (
          <MiningSchemaItem
            key={field.name as string}
            dataDictionary={dataDictionary}
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
  dataDictionary?: DataDictionary;
  field: MiningField;
  onAddProperties: (index: number) => void;
  onDelete: (index: number, name: string) => void;
  onPropertyDelete: (index: number, field: MiningField) => void;
  onEdit: (index: number) => void;
  onCancel: () => void;
}

const MiningSchemaItem = (props: MiningSchemaFieldProps) => {
  const { index, modelIndex, dataDictionary, field, onAddProperties, onDelete, onPropertyDelete, onEdit, onCancel } =
    props;
  const editing = useContext(MiningSchemaContext);

  const ref = useOnclickOutside(
    () => {
      onCancel();
    },
    {
      disabled: editing !== index,
      eventTypes: ["click"],
    }
  );

  const addProperties = () => {
    onAddProperties(index);
  };

  const deleteField = (event: React.MouseEvent<HTMLButtonElement, MouseEvent>) => {
    event.preventDefault();
    event.stopPropagation();
    onDelete(index, field.name as string);
  };

  const deleteProperty = (updatedField: MiningField) => {
    onPropertyDelete(index, updatedField);
  };

  const handleEdit = (event: BaseSyntheticEvent) => {
    event.preventDefault();
    event.stopPropagation();
    onEdit(index);
  };

  const { validationRegistry } = useValidationRegistry();
  const validations = useMemo(
    () => validationRegistry.get(Builder().forModel(modelIndex).forMiningSchema().forMiningField(index).build()),
    [index, modelIndex, dataDictionary, field]
  );

  return (
    <li
      className={`editable-item ${editing === index ? "editable-item--editing" : ""}`}
      key={field.name.value}
      onClick={(event) => handleEdit(event)}
      ref={ref}
      tabIndex={0}
      onKeyDown={(event) => {
        if (event.key === "Enter") {
          handleEdit(event);
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
