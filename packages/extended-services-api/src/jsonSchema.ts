/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

type DmnSchemaDefinitions = "InputSet" | "OutputSet";

export enum X_DMN_TYPE {
  ANY = "FEEL:Any",
  BOOLEAN = "FEEL:boolean",
  CONTEXT = "FEEL:context",
  DATE = "FEEL:date",
  DATE_AND_TIME = "FEEL:date and time",
  DATE_AND_TIME_DURATION = "FEEL:date and time duration",
  NUMBER = "FEEL:number",
  STRING = "FEEL:string",
  TIME = "FEEL:time",
  YEARS_AND_MONTHS_DURATION = "FEEL:years and months duration",
}

export interface DmnInputFieldProperties {
  $ref?: string;
  type?: string;
  placeholder?: string;
  title?: string;
  format?: string;
  "x-dmn-type"?: X_DMN_TYPE | string;
  items?: object[] & { properties: object };
  properties?: DmnInputFieldProperties[];
}

export interface DmnSchemaDefitionProperties {
  type: string;
  properties: Record<string, DmnInputFieldProperties>;
  required?: string[];
  "x-dmn-descriptions"?: object;
  "x-dmn-type"?: X_DMN_TYPE | string;
}

// JSON schema returned from extended-services /schema/form;
export interface ExtendedServicesDmnJsonSchema {
  $ref?: string;
  definitions?: Record<DmnSchemaDefinitions, DmnSchemaDefitionProperties>;
}
