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

import { JSONSchemaBridge } from "uniforms-bridge-json-schema/esm";
import { SelectDirection } from "@patternfly/react-core/dist/js/components/Select";

export class DmnTableJsonSchemaBridge extends JSONSchemaBridge {
  public getProps(name: string, props: Record<string, any> = {}) {
    const ready = super.getProps(name, props);
    ready.label = "";
    ready.style = { height: "100%" };
    if (ready.required) {
      ready.required = false;
    }
    return ready;
  }

  public getField(name: string) {
    const field = super.getField(name);
    if (field.format === "days and time duration") {
      field.placeholder = "P1DT5H or P2D or PT1H2M10S";
    }
    if (field.format === "years and months duration") {
      field.placeholder = "P1Y5M or P2Y or P1M";
    }
    if (field.type === "string" && field.enum) {
      field.placeholder = "Select...";
      field.direction = SelectDirection.up;
      field.menuAppendTo = document.body;
    }
    if (!field.type && field["x-dmn-type"] === "FEEL:context") {
      field.placeholder = `{ "x": <value> }`;
    }
    if (!field.type) {
      field.type = "string";
    }
    return field;
  }
}
