import * as React from "react";
import { useEffect, useState } from "react";
import { Bullseye, Stack, StackItem, Title, TitleSizes } from "@patternfly/react-core";
import { CSSTransition, SwitchTransition } from "react-transition-group";
import { pickBy, isEqual } from "lodash";
import EmptyMiningSchema from "../EmptyMiningSchema/EmptyMiningSchema";
import MiningSchemaFields from "../MiningSchemaFields/MiningSchemaFields";
import MiningSchemaAddFields from "../MiningSchemaAddFields/MiningSchemaAddFields";
import MiningSchemaPropertiesEdit from "../MiningSchemaPropertiesEdit/MiningSchemaPropertiesEdit";
import "./MiningSchemaContainer.scss";

import { DataDictionary, MiningField, MiningSchema } from "@kogito-tooling/pmml-editor-marshaller";
import NoMiningSchemaFieldsOptions from "../NoMiningSchemaFieldsOptions/NoMiningSchemaFieldsOptions";

interface MiningSchemaContainerProps {
  dataDictionary?: DataDictionary;
  miningSchema?: MiningSchema;
  onAddField: (name: string[]) => void;
  onDeleteField: (index: number) => void;
  onUpdateField: (index: number, field: MiningField) => void;
}

const MiningSchemaContainer = (props: MiningSchemaContainerProps) => {
  const { dataDictionary, miningSchema, onAddField, onDeleteField, onUpdateField } = props;

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

  const goToProperties = (index: number) => {
    setEditingField(index);
    setViewSection("properties");
  };

  const handlePropertiesSave = (field: MiningField) => {
    if (
      !isEqual(
        field,
        pickBy(miningSchema?.MiningField[editingField], value => value !== undefined)
      )
    ) {
      onUpdateField(editingField, field);
    }
    setViewSection("overview");
    setEditingField(-1);
  };

  const handlePropertyDelete = (index: number, updatedField: MiningField) => {
    onUpdateField(index, updatedField);
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

  return (
    <section className="mining-schema">
      <SwitchTransition mode={"out-in"}>
        <CSSTransition
          timeout={{
            enter: 230,
            exit: 100
          }}
          classNames={getTransition(viewSection)}
          key={viewSection}
        >
          <>
            {viewSection === "overview" && (
              <Stack hasGutter={true}>
                <StackItem>
                  <Title headingLevel="h4" size={TitleSizes.xl}>
                    Add Fields
                  </Title>
                </StackItem>
                <StackItem>
                  <MiningSchemaAddFields options={fields} onAdd={handleAddFields} />
                </StackItem>
                <StackItem>
                  <Title headingLevel="h4" size={TitleSizes.xl}>
                    Fields List
                  </Title>
                  <section className="mining-schema__fields">
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
                          <MiningSchemaFields
                            fields={miningSchema?.MiningField}
                            onAddProperties={goToProperties}
                            onDelete={handleDeleteField}
                            onPropertyDelete={handlePropertyDelete}
                          />
                        )}
                      </>
                    )}
                  </section>
                </StackItem>
              </Stack>
            )}
            {viewSection === "properties" && (
              <MiningSchemaPropertiesEdit
                field={miningSchema!.MiningField[editingField]}
                onSave={handlePropertiesSave}
              />
            )}
          </>
        </CSSTransition>
      </SwitchTransition>
    </section>
  );
};

export default MiningSchemaContainer;

export interface MiningSchemaOption {
  name: string;
  isSelected: boolean;
}

type MiningSchemaSection = "overview" | "properties";

const prepareFieldOptions = (dictionary: DataDictionary | undefined, miningSchema: MiningSchema | undefined) => {
  if (dictionary) {
    return dictionary.DataField.filter(field => field.name !== undefined).map(field => ({
      name: field.name as string,
      isSelected: miningSchema
        ? miningSchema?.MiningField.findIndex(miningField => miningField.name === field.name) > -1
        : false
    }));
  } else {
    return [];
  }
};
