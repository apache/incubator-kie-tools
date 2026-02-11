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

import { JSONSchemaBridge } from "uniforms-bridge-json-schema";
import { FormI18n } from "../i18n";

export class FormJsonSchemaBridge extends JSONSchemaBridge {
  constructor(
    public readonly formSchema: object,
    public readonly validator: (model: object) => void,
    public i18n: FormI18n
  ) {
    super(formSchema, validator);
  }

  public getProps(name: string, props: Record<string, any>) {
    const finalProps = super.getProps(name, props);
    if (finalProps.label) {
      finalProps.label = name.split(".").pop() ?? name;
    }
    return finalProps;
  }

  public getField(name: string): Record<string, any> {
    const field = super.getField(name);
    if (!field.type) {
      field.type = "string";
    }
    if (field.enum) {
      field.placeholder = this.i18n.schema.selectPlaceholder;
    }
    return field;
  }
}
