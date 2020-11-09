import * as React from "react";
import { useEffect, useState } from "react";
import { Bullseye, Button, ButtonVariant, Label, Stack, StackItem, Title, TitleSizes } from "@patternfly/react-core";
import { CSSTransition, SwitchTransition } from "react-transition-group";
import EmptyMiningSchema from "../EmptyMiningSchema/EmptyMiningSchema";
import MiningSchemaFields from "../MiningSchemaFields/MiningSchemaFields";
import MiningSchemaAddFields from "../MiningSchemaAddFields/MiningSchemaAddFields";
import MiningSchemaPropertiesEdit from "../MiningSchemaPropertiesEdit/MiningSchemaPropertiesEdit";
import "./MiningSchemaContainer.scss";
import {
  InvalidValueTreatmentMethod,
  MissingValueTreatmentMethod,
  OpType,
  OutlierTreatmentMethod,
  UsageType
} from "@kogito-tooling/pmml-editor-marshaller/src/marshaller/model/pmml4_4";

const MiningSchemaContainer = () => {
  const fieldsOptions = ["Name", "Age", "City", "Asset Score", "Bank Score", "Debit Score", "Monthly Income"];
  const [fields, setFields] = useState<MiningSchemaField[]>(
    fieldsOptions.map(field => ({ name: field, isSelected: false }))
  );
  const [selectedFieldsCount, setSelectedFieldsCount] = useState(0);
  const [viewSection, setViewSection] = useState<MiningSchemaSection>("overview");
  const [editingField, setEditingField] = useState(-1);

  const handleFieldSelection = (event: React.MouseEvent<HTMLElement>) => {
    const selectedFieldName = (event.target as HTMLElement).innerText;
    const fieldIndex = fields.findIndex(field => field.name === selectedFieldName);
    const updatedFields = [...fields];
    updatedFields[fieldIndex].isSelected = true;
    setFields(updatedFields);
  };

  const addAllFields = () => {
    const updatedFields = [...fields];
    updatedFields.forEach(field => (field.isSelected = true));
    setFields(updatedFields);
  };

  const handleFieldAddition = (fieldsToAdd: string[]) => {
    const updatedFields = [...fields];
    updatedFields.forEach(field => (field.isSelected = fieldsToAdd.includes(field.name) ? true : field.isSelected));
    setFields(updatedFields);
  };

  const handleDeleteField = (index: number) => {
    const updatedFields = [...fields];
    updatedFields[index].isSelected = false;
    setFields(updatedFields);
  };

  const goToProperties = (index: number) => {
    setEditingField(index);
    setViewSection("properties");
  };

  const handlePropertiesSave = (field: MiningSchemaField) => {
    console.log(field);
    setViewSection("overview");
    setEditingField(-1);
  };

  useEffect(() => {
    const count = fields.filter(field => field.isSelected).length;
    setSelectedFieldsCount(count);
  }, [fields]);

  const getTransition = (currentState: MiningSchemaSection) => {
    if (currentState === "overview") {
      return "mining-schema__overview";
    } else if (currentState === "properties") {
      return "mining-schema__properties";
    }
  };

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
                <StackItem style={{ display: "none" }}>
                  <section>
                    {fields.map(field => {
                      let cssClass = "mining-schema__field-option";
                      cssClass += field.isSelected ? " mining-schema__field-option--is-selected" : "";
                      return (
                        <Label
                          key={field.name}
                          variant={field.isSelected ? "filled" : "outline"}
                          color="blue"
                          className={cssClass}
                          onClick={handleFieldSelection}
                        >
                          {field.name}
                        </Label>
                      );
                    })}
                    <Button
                      variant={ButtonVariant.link}
                      isInline={false}
                      onClick={addAllFields}
                      isDisabled={fields.findIndex(field => !field.isSelected) === -1}
                    >
                      Add them all
                    </Button>
                  </section>
                </StackItem>
                <StackItem>
                  <MiningSchemaAddFields options={fields} onAdd={handleFieldAddition} />
                </StackItem>
                <StackItem>
                  <Title headingLevel="h4" size={TitleSizes.xl}>
                    Fields List
                  </Title>
                  <section className="mining-schema__fields">
                    {selectedFieldsCount === 0 && (
                      <Bullseye style={{ height: "30vh" }}>
                        <EmptyMiningSchema />
                      </Bullseye>
                    )}
                    {selectedFieldsCount > 0 && (
                      <MiningSchemaFields
                        fields={fields}
                        onAddProperties={goToProperties}
                        onDelete={handleDeleteField}
                      />
                    )}
                  </section>
                </StackItem>
              </Stack>
            )}
            {viewSection === "properties" && (
              <MiningSchemaPropertiesEdit field={fields[editingField]} onSave={handlePropertiesSave} />
            )}
          </>
        </CSSTransition>
      </SwitchTransition>
    </section>
  );
};

export default MiningSchemaContainer;

export interface MiningSchemaField {
  name: string;
  isSelected: boolean;
  usageType?: UsageType;
  optype?: OpType;
  importance?: number;
  outliers?: OutlierTreatmentMethod;
  lowValue?: number;
  highValue?: number;
  missingValueReplacement?: any;
  missingValueTreatment?: MissingValueTreatmentMethod;
  invalidValueTreatment?: InvalidValueTreatmentMethod;
  invalidValueReplacement?: any;
}

type MiningSchemaSection = "overview" | "properties";
