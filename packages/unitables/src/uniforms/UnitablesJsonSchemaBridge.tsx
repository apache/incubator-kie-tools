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

import * as React from "react";
import { SelectDirection } from "@patternfly/react-core/dist/js/components/Select";
import { UnitablesI18n } from "../i18n";
import { JSONSchemaBridge } from "uniforms-bridge-json-schema";
import { joinName } from "uniforms";
import { UnitablesColumnType } from "../UnitablesTypes";

export const FORMS_ID = "unitables-forms";
export const AUTO_ROW_ID = "unitables-row";

export class UnitablesJsonSchemaBridge extends JSONSchemaBridge {
  constructor(
    public readonly formSchema: Record<string, any>,
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
    if ((field.type === "string" || field.type === "number") && field.enum) {
      field.placeholder = this.i18n.schema.selectPlaceholder;
      field.direction = SelectDirection.up;
      field.menuAppendTo = document.body;
    } else if (!field.type) {
      field.type = "string";
    }
    return field;
  }

  public getFieldDataType(field: Record<string, any>) {
    return field.type ?? "string";
  }

  private deepTransformToUnitablesColumns(fieldName: string, parentName = ""): UnitablesColumnType {
    const joinedName = joinName(parentName, fieldName);
    const field = this.getField(joinedName);

    if (field.type !== "object") {
      return {
        ...this.getFieldDataType(field),
        name: removeFieldName(joinedName),
        joinedName: joinedName,
      };
    }

    const insideProperties: UnitablesColumnType[] = this.getSubfields(joinedName).reduce(
      (insideProperties: UnitablesColumnType[], subField: string) => {
        const field = this.deepTransformToUnitablesColumns(subField, joinedName);
        if (field.insideProperties) {
          return [...insideProperties, ...field.insideProperties];
        } else {
          return [...insideProperties, field];
        }
      },
      []
    );

    return {
      ...this.getFieldDataType(field),
      insideProperties,
      name: joinedName,
      joinedName: joinedName,
      width: insideProperties.reduce((acc, insideProperty) => acc + (insideProperty.width ?? 0), 0),
    };
  }

  public getUnitablesColumns(): UnitablesColumnType[] {
    return (super.getSubfields() ?? []).reduce((fields, fieldName) => {
      const generateInputFields = this.deepTransformToUnitablesColumns(fieldName);
      if (generateInputFields) {
        return [...fields, generateInputFields];
      } else {
        return fields;
      }
    }, []);
  }
}

function removeFieldName(fullName: string) {
  return fullName.match(/\./) ? fullName.split(".").slice(1).join("-") : fullName;
}
