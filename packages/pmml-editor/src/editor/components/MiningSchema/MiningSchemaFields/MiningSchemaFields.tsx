/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import * as React from "react";
import { BaseSyntheticEvent, useContext, useMemo } from "react";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { Split, SplitItem } from "@patternfly/react-core/dist/js/layouts/Split";
import { TrashIcon } from "@patternfly/react-icons/dist/js/icons/trash-icon";
import { DataDictionary, MiningField } from "@kie-tools/pmml-editor-marshaller";
import { MiningSchemaContext } from "../MiningSchemaContainer/MiningSchemaContainer";
import useOnclickOutside from "react-cool-onclickoutside";
import MiningSchemaFieldLabels from "../MiningSchemaFieldLabels/MiningSchemaFieldLabels";
import "./MiningSchemaFields.scss";
import { useValidationRegistry } from "../../../validation";
import { Builder } from "../../../paths";
import { ValidationIndicator } from "../../EditorCore/atoms";
import { Interaction } from "../../../types";

interface MiningSchemaFieldsProps {
  modelIndex: number;
  dataDictionary?: DataDictionary;
  fields: MiningField[] | undefined;
  onAddProperties: (index: number) => void;
  onDelete: (index: number, interaction: Interaction) => void;
  onPropertyDelete: (index: number, updatedField: MiningField) => void;
  onEdit: (index: number) => void;
  onCancel: () => void;
}

const MiningSchemaFields: React.FC<MiningSchemaFieldsProps> = ({
  modelIndex,
  dataDictionary,
  fields,
  onAddProperties,
  onDelete,
  onPropertyDelete,
  onEdit,
  onCancel,
}) => {
  //const { dataDictionary, fields, modelIndex, onAddProperties, onDelete, onPropertyDelete, onEdit, onCancel } = props;
  return (
    <ul className="mining-schema-list" data-ouia-component-type="mining-schema-list">
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
  onDelete: (index: number, interaction: Interaction) => void;
  onPropertyDelete: (index: number, field: MiningField) => void;
  onEdit: (index: number) => void;
  onCancel: () => void;
}

const MiningSchemaItem: React.FC<MiningSchemaFieldProps> = (props) => {
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

  const deleteProperty = (updatedField: MiningField) => {
    onPropertyDelete(index, updatedField);
  };

  const handleEdit = (event: BaseSyntheticEvent) => {
    event.preventDefault();
    event.stopPropagation();
    onEdit(index);
  };

  const handleDelete = (event: React.MouseEvent | React.KeyboardEvent, interaction: Interaction) => {
    event.stopPropagation();
    event.preventDefault();
    if (onDelete) {
      onDelete(index, interaction);
    }
  };

  const { validationRegistry } = useValidationRegistry();
  const validations = useMemo(
    () => validationRegistry.get(Builder().forModel(modelIndex).forMiningSchema().forMiningField(index).build()),
    [index, modelIndex, dataDictionary, field]
  );

  return (
    <>
      {index === editing && (
        <li
          id={`mining-schema-field-n${index}`}
          data-testid={`mining-schema-field-n${index}`}
          data-ouia-component-id={field.name}
          data-ouia-component-type="edit-mining-field-row-"
          className={`editable-item ${editing === index ? "editable-item--editing" : ""}`}
          key={field.name}
          ref={ref}
          tabIndex={0}
          onKeyDown={(event) => {
            if (event.key === "Escape") {
              onCancel();
            }
          }}
        >
          <section className={"editable-item__inner"}>
            <Split hasGutter={true}>
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
            </Split>
          </section>
        </li>
      )}
      {index !== editing && (
        <li
          id={`mining-schema-field-n${index}`}
          data-testid={`mining-schema-field-n${index}`}
          data-ouia-component-id={field.name}
          data-ouia-component-type="mining-field-row"
          className={`editable-item ${editing === index ? "editable-item--editing" : ""}`}
          key={field.name}
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
              {validations.length > 0 && (
                <SplitItem>
                  <Flex
                    alignItems={{ default: "alignItemsCenter" }}
                    justifyContent={{ default: "justifyContentCenter" }}
                    style={{ height: "100%" }}
                  >
                    <FlexItem data-ouia-component-id="validation-issue">
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
                <Button
                  id={`mining-schema-field-n${index}__delete`}
                  data-testid={`mining-schema-field-n${index}__delete`}
                  ouiaId="delete-field"
                  className="editable-item__delete"
                  variant="plain"
                  onClick={(e) => handleDelete(e, "mouse")}
                  onKeyDown={(event) => {
                    if (event.key === "Enter") {
                      handleDelete(event, "keyboard");
                    }
                  }}
                >
                  <TrashIcon />
                </Button>
              </SplitItem>
            </Split>
          </section>
        </li>
      )}
    </>
  );
};
