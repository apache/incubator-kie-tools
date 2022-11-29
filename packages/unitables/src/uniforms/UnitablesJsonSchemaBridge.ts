/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { SelectDirection } from "@patternfly/react-core/dist/js/components/Select";
import { UnitablesI18n } from "../i18n";
import { JSONSchemaBridge } from "uniforms-bridge-json-schema";
import { DmnBuiltInDataType } from "@kie-tools/boxed-expression-component/dist/api";
import * as React from "react";
import { joinName } from "uniforms";
import { AutoField } from "./AutoField";
import { CELL_MINIMUM_WIDTH } from "../boxed";

export const FORMS_ID = "unitables-forms";

const DEFAULT_DATE_TIME_CELL_WDITH = 296;
const DEFAULT_DATE_CELL_WIDTH = 180;

interface InputField {
  dataType: DmnBuiltInDataType;
  width: number;
  name: string;
  cellDelegate: (formId: string) => React.ReactNode;
}

interface InputWithInsideProperties extends InputField {
  insideProperties: Array<InputField>;
}

export type InputFields = InputField | InputWithInsideProperties;

export function isInputWithInsideProperties(toBeDetermined: InputFields): toBeDetermined is InputWithInsideProperties {
  return (toBeDetermined as InputWithInsideProperties).insideProperties !== undefined;
}

export class UnitablesJsonSchemaBridge extends JSONSchemaBridge {
  constructor(
    public readonly formSchema: object,
    public readonly validator: (model: object) => void,
    public readonly i18n: UnitablesI18n
  ) {
    super(formSchema, validator);
  }
  public getProps(name: string, props: Record<string, any> = {}) {
    const finalProps = super.getProps(name, props);
    finalProps.label = "";
    finalProps.style = { height: "100%" };
    if (finalProps.required) {
      finalProps.required = false;
    }
    return finalProps;
  }

  public getField(name: string) {
    const field = super.getField(name);

    if (field.type === "object") {
      field.default = {};
    }
    if (field.type === "array") {
      field.default = [];
    }
    if (field.type === "boolean") {
      field.default = false;
    }
    if ((field.type === "string" || field.type === "number") && field.enum) {
      field.placeholder = this.i18n.schema.selectPlaceholder;
      field.direction = SelectDirection.up;
      field.menuAppendTo = document.body;
    }
    if (!field.type) {
      field.type = "string";
    }
    return field;
  }

  private static removeInputName(fullName: string) {
    return fullName.match(/\./) ? fullName.split(".").slice(1).join("-") : fullName;
  }

  public getBoxedFieldType(field: Record<string, any>): string {
    return field.type ?? "string";
  }

  public getBoxedDataType(field: Record<string, any>) {
    const type = this.getBoxedFieldType(field);

    switch (type) {
      case "<Undefined>":
        return { dataType: DmnBuiltInDataType.Undefined, width: CELL_MINIMUM_WIDTH };
      case "Any":
        return { dataType: DmnBuiltInDataType.Any, width: CELL_MINIMUM_WIDTH };
      case "boolean":
        return { dataType: DmnBuiltInDataType.Boolean, width: CELL_MINIMUM_WIDTH };
      case "context":
        return { dataType: DmnBuiltInDataType.Context, width: CELL_MINIMUM_WIDTH };
      case "date":
        return { dataType: DmnBuiltInDataType.Date, width: DEFAULT_DATE_CELL_WIDTH };
      case "date and time":
        return { dataType: DmnBuiltInDataType.DateTime, width: DEFAULT_DATE_TIME_CELL_WDITH };
      case "days and time duration":
        return { dataType: DmnBuiltInDataType.DateTimeDuration, width: CELL_MINIMUM_WIDTH };
      case "number":
        return { dataType: DmnBuiltInDataType.Number, width: CELL_MINIMUM_WIDTH };
      case "string":
        return { dataType: DmnBuiltInDataType.String, width: CELL_MINIMUM_WIDTH };
      case "time":
        return { dataType: DmnBuiltInDataType.Time, width: CELL_MINIMUM_WIDTH };
      case "years and months duration":
        return { dataType: DmnBuiltInDataType.YearsMonthsDuration, width: CELL_MINIMUM_WIDTH };
      default:
        return { dataType: (type as DmnBuiltInDataType) ?? DmnBuiltInDataType.Undefined, width: CELL_MINIMUM_WIDTH };
    }
  }

  private deepTransformToBoxedInputs(fieldName: string, parentName = "") {
    const joinedName = joinName(parentName, fieldName);
    const field = this.getField(joinedName);

    if (field.type === "object") {
      const insideProperties: Array<InputField> = this.getSubfields(joinedName).reduce(
        (insideProperties: Array<InputField>, subField: string) => {
          const field = this.deepTransformToBoxedInputs(subField, joinedName) as InputWithInsideProperties;
          if (field && field.insideProperties) {
            return [...insideProperties, ...field.insideProperties];
          }
          return [...insideProperties, field];
        },
        []
      );
      return {
        ...this.getBoxedDataType(field),
        insideProperties,
        name: joinedName,
        width: insideProperties.reduce((acc, insideProperty) => acc + insideProperty.width, 0),
      } as InputWithInsideProperties;
    }
    return {
      ...this.getBoxedDataType(field),
      name: UnitablesJsonSchemaBridge.removeInputName(joinedName),
      cellDelegate: (formId: string) => AutoField({ key: joinedName, name: joinedName, form: formId }),
    } as InputField;
  }

  public getBoxedHeaderInputs(): Array<InputFields> {
    return (
      super.getSubfields().reduce((inputs: Array<InputFields>, fieldName: string) => {
        const generateInputFields = this.deepTransformToBoxedInputs(fieldName);
        if (generateInputFields) {
          return [...inputs, generateInputFields];
        }
      }, [] as Array<InputFields>) ?? []
    );
  }
}
