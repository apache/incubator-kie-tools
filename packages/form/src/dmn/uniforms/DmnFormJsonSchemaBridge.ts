/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import { JSONSchemaBridge } from "uniforms-bridge-json-schema";

export enum Duration {
  DaysAndTimeDuration,
  YearsAndMonthsDuration,
}

export class DmnFormJsonSchemaBridge extends JSONSchemaBridge {
  public getProps(name: string, props: Record<string, any>) {
    const finalProps = super.getProps(name, props);
    if (finalProps.label) {
      finalProps.label = name;
    }
    return finalProps;
  }

  public getType(name: string) {
    const { format: fieldFormat } = super.getField(name);
    // TODO: create custom components
    if (fieldFormat === "days and time duration") {
      return String;
    }
    if (fieldFormat === "years and months duration") {
      return String;
    }
    return super.getType(name);
  }
}
