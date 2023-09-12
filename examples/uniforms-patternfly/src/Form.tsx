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
import { useState, useMemo } from "react";
import { SchemaType } from "./App";
import { bridge as jsonSchema } from "./schemas/json-schema";
import { bridge as simpleSchema } from "./schemas/simple-schema-2";
import { CodeBlock } from "./CodeBlock";
import AutoForm from "@kie-tools/uniforms-patternfly/dist/esm/AutoForm";

interface Props {
  schemaType: SchemaType;
}

export function Form(props: Props) {
  const [model, setModel] = useState<any>(undefined);

  const schema = useMemo(() => {
    if (props.schemaType === SchemaType.SimpleSchema) {
      return simpleSchema;
    }
    return jsonSchema;
  }, [props.schemaType]);

  return (
    <div className={"form--page"}>
      <div className={"form--container"}>
        <CodeBlock model={model} />
        <AutoForm
          placeholder
          model={model}
          schema={schema}
          onSubmit={(model: any) => setModel(model)}
          showInlineError
        />
      </div>
    </div>
  );
}
