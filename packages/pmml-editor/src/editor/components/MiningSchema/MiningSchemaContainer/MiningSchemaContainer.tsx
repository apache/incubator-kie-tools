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

import { DataDictionary, FieldName, MiningField, MiningSchema } from "@kogito-tooling/pmml-editor-marshaller";
import NoMiningSchemaFieldsOptions from "../NoMiningSchemaFieldsOptions/NoMiningSchemaFieldsOptions";
import { useValidationRegistry } from "../../../validation";
import { Builder } from "../../../paths";

interface MiningSchemaContainerProps {
  modelIndex: number;
  dataDictionary?: DataDictionary;
  miningSchema?: MiningSchema;
  onAddField: (name: string[]) => void;
  onDeleteField: (index: number) => void;
  onUpdateField: (index: number, originalName: FieldName | undefined, field: MiningField) => void;
}

const MiningSchemaContainer = (props: MiningSchemaContainerProps) => {
  const { modelIndex, dataDictionary, miningSchema, onAddField, onDeleteField, onUpdateField } = props;

  const [fields, setFields] = useState<MiningSchemaOption[]>(prepareFieldOptions(dataDictionary, miningSchema));
  const [viewSection, setViewSection] = useState<MiningSchemaSection>("overview");
  const [editingField, setEditingField] = useState(-1);

  const handleAddFields = (fieldsToAdd: string[]) => {
    if (fieldsToAdd.length) {
      onAddField(fieldsToAdd);
    }
  };

  const handleDeleteField = (index: number) => {
    onDeleteField(index);
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

  return (
    <section className="mining-schema">
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
                    <MiningSchemaAddFields options={fields} onAdd={handleAddFields} />
                  </StackItem>
                  {validations.length > 0 && (
                    <section className="mining-schema__validation-alert">
                      <Alert variant="warning" isInline={true} title="Some items are invalid and need attention." />
                    </section>
                  )}
                  <StackItem className="mining-schema__fields">
                    <section>
                      {fields.length === 0 && (
                        <Bullseye style={{ height: "40vh" }}>
                          <NoMiningSchemaFieldsOptions />
                        </Bullseye>
                      )}
                      {fields.length > 0 && (
                        <>
                          {miningSchema === undefined ||
                            (miningSchema?.MiningField.length === 0 && (
                              <Bullseye style={{ height: "40vh" }}>
                                <EmptyMiningSchema />
                              </Bullseye>
                            ))}
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
