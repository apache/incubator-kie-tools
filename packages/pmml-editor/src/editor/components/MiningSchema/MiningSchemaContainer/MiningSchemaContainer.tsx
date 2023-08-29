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
import { useEffect, useMemo, useState } from "react";
import { Stack, StackItem } from "@patternfly/react-core/dist/js/layouts/Stack";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { Alert } from "@patternfly/react-core/dist/js/components/Alert";
import { Title, TitleSizes } from "@patternfly/react-core/dist/js/components/Title";
import { CSSTransition, SwitchTransition } from "react-transition-group";
import { isEqual, pickBy } from "lodash";
import EmptyMiningSchema from "../EmptyMiningSchema/EmptyMiningSchema";
import MiningSchemaFields from "../MiningSchemaFields/MiningSchemaFields";
import MiningSchemaAddFields from "../MiningSchemaAddFields/MiningSchemaAddFields";
import MiningSchemaPropertiesEdit from "../MiningSchemaPropertiesEdit/MiningSchemaPropertiesEdit";
import "./MiningSchemaContainer.scss";

import { DataDictionary, MiningField, MiningSchema } from "@kie-tools/pmml-editor-marshaller";
import { useValidationRegistry } from "../../../validation";
import { Builder } from "../../../paths";
import { Interaction } from "../../../types";
import NoMiningSchemaFieldsOptions from "../NoMiningSchemaFieldsOptions/NoMiningSchemaFieldsOptions";

interface MiningSchemaContainerProps {
  modelIndex: number;
  dataDictionary?: DataDictionary;
  miningSchema?: MiningSchema;
  onAddField: (name: string[]) => void;
  onDeleteField: (index: number) => void;
  onUpdateField: (index: number, originalName: string | undefined, field: MiningField) => void;
}

const MiningSchemaContainer = (props: MiningSchemaContainerProps) => {
  const { modelIndex, dataDictionary, miningSchema, onAddField, onDeleteField, onUpdateField } = props;

  const [fields, setFields] = useState<MiningSchemaOption[]>(prepareFieldOptions(dataDictionary, miningSchema));
  const [viewSection, setViewSection] = useState<MiningSchemaSection>("overview");
  const [editingField, setEditingField] = useState(-1);
  const [miningFieldFocusIndex, setMiningFieldFocusIndex] = useState<number | undefined>(undefined);

  const handleAddFields = (fieldsToAdd: string[]) => {
    if (fieldsToAdd.length) {
      onAddField(fieldsToAdd);
    }
  };

  const handleDeleteField = (index: number, interaction: Interaction) => {
    onDeleteField(index);
    if (interaction === "mouse") {
      //If the MiningField was deleted by clicking on the delete icon we need to blur
      //the element otherwise the CSS :focus-within persists on the deleted element.
      //See https://issues.redhat.com/browse/FAI-570 for the root cause.
      if (document.activeElement instanceof HTMLElement) {
        document.activeElement?.blur();
      }
    } else if (interaction === "keyboard") {
      //If the MiningField was deleted by pressing enter on the delete icon when focused
      //we need to set the focus to the next MiningField. The index of the _next_ item
      //is identical to the index of the deleted item.
      setMiningFieldFocusIndex(index);
    }
    setEditingField(-1);
  };

  const handleEditField = (index: number) => {
    setEditingField(index);
  };

  const handleCancelEditing = () => {
    setEditingField(-1);
  };

  const goToProperties = () => {
    setViewSection("properties");
  };

  const handlePropertiesSave = (field: MiningField) => {
    if (
      !isEqual(
        field,
        pickBy(miningSchema?.MiningField[editingField], (value) => value !== undefined)
      )
    ) {
      onUpdateField(editingField, miningSchema?.MiningField[editingField].name, field);
    }
  };

  const handlePropertiesClose = () => {
    setViewSection("overview");
  };

  const handlePropertyDelete = (index: number, updatedField: MiningField) => {
    onUpdateField(index, miningSchema?.MiningField[editingField].name, updatedField);
  };

  const getTransition = (currentState: MiningSchemaSection) => {
    if (currentState === "overview") {
      return "mining-schema__overview";
    } else if (currentState === "properties") {
      return "mining-schema__properties";
    }
  };

  useEffect(() => {
    setFields(prepareFieldOptions(dataDictionary, miningSchema));
  }, [dataDictionary, miningSchema]);

  const { validationRegistry } = useValidationRegistry();
  const validations = useMemo(
    () => validationRegistry.get(Builder().forModel(modelIndex).forMiningSchema().build()),
    [dataDictionary, miningSchema]
  );

  //Set the focus on a MiningField as required
  useEffect(() => {
    if (miningFieldFocusIndex !== undefined) {
      document.querySelector<HTMLElement>(`#mining-schema-field-n${miningFieldFocusIndex}`)?.focus();
    }
  }, [miningSchema, miningFieldFocusIndex]);

  const isDisabled = useMemo(() => {
    return fields.length === 0 || editingField !== -1;
  }, [fields, editingField]);

  return (
    <section
      className="mining-schema"
      data-testid="mining-schema-container"
      data-ouia-component-id="mining-container"
      data-ouia-component-type="editor-container"
    >
      <MiningSchemaContext.Provider value={editingField}>
        <SwitchTransition mode={"out-in"}>
          <CSSTransition
            timeout={{
              enter: 230,
              exit: 100,
            }}
            classNames={getTransition(viewSection)}
            key={viewSection}
          >
            <>
              {viewSection === "overview" && (
                <Stack hasGutter={true} className="mining-schema__overview">
                  <StackItem>
                    <Title headingLevel="h4" size={TitleSizes.xl}>
                      Add Fields
                    </Title>
                  </StackItem>
                  <StackItem>
                    <MiningSchemaAddFields options={fields} onAdd={handleAddFields} isDisabled={isDisabled} />
                  </StackItem>
                  {validations.length > 0 && (
                    <section
                      className="mining-schema__validation-alert"
                      data-ouia-component-id="validation-container"
                      data-ouia-component-type="validation-alerts"
                    >
                      <Alert variant="warning" isInline={true} title="Some items are invalid and need attention." />
                    </section>
                  )}
                  <StackItem className="mining-schema__fields">
                    <section>
                      {(miningSchema === undefined || miningSchema?.MiningField.length === 0) && (
                        <>
                          {fields.length === 0 && (
                            <Bullseye style={{ height: "40vh" }}>
                              <NoMiningSchemaFieldsOptions />
                            </Bullseye>
                          )}{" "}
                          {fields.length > 0 && (
                            <Bullseye style={{ height: "40vh" }}>
                              <EmptyMiningSchema />
                            </Bullseye>
                          )}
                        </>
                      )}
                      {miningSchema && miningSchema.MiningField.length > 0 && (
                        <>
                          <MiningSchemaFields
                            modelIndex={modelIndex}
                            dataDictionary={dataDictionary}
                            fields={miningSchema?.MiningField}
                            onAddProperties={goToProperties}
                            onDelete={handleDeleteField}
                            onPropertyDelete={handlePropertyDelete}
                            onEdit={handleEditField}
                            onCancel={handleCancelEditing}
                          />
                        </>
                      )}
                    </section>
                  </StackItem>
                </Stack>
              )}
              {viewSection === "properties" && (
                <MiningSchemaPropertiesEdit
                  modelIndex={modelIndex}
                  miningFieldIndex={editingField}
                  field={miningSchema!.MiningField[editingField]}
                  onSave={handlePropertiesSave}
                  onClose={handlePropertiesClose}
                />
              )}
            </>
          </CSSTransition>
        </SwitchTransition>
      </MiningSchemaContext.Provider>
    </section>
  );
};

export default MiningSchemaContainer;

export const MiningSchemaContext = React.createContext<number>(-1);

const prepareFieldOptions = (dictionary: DataDictionary | undefined, miningSchema: MiningSchema | undefined) => {
  if (dictionary) {
    return dictionary.DataField.filter((field) => field.name !== undefined).map((field) => ({
      name: field.name as string,
      isSelected: miningSchema
        ? miningSchema?.MiningField.findIndex((miningField) => miningField.name === field.name) > -1
        : false,
    }));
  } else {
    return [];
  }
};

export interface MiningSchemaOption {
  name: string;
  isSelected: boolean;
}

type MiningSchemaSection = "overview" | "properties";
