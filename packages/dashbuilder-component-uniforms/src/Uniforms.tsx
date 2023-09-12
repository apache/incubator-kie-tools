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
import { AutoForm } from "@kie-tools/uniforms-patternfly/dist/esm";
import * as Ajv from "ajv";
import * as React from "react";
import { useCallback } from "react";

const ajv = new Ajv({ allErrors: true, useDefaults: true });
const createValidator = (schema: any) => {
  const validator = ajv.compile(schema);
  return (model: any) => {
    validator(model);
    return validator.errors?.length ? { details: validator.errors } : null;
  };
};

interface Props {
  schema: Object;
  onSubmit: (formRef: any, data: Object) => void;
  disabled?: boolean;
}

export const Uniforms = (props: Props) => {
  const bridge = new JSONSchemaBridge(props.schema, createValidator(props.schema));
  let formRef: any;
  const onSubmitWrapper = useCallback((data) => props.onSubmit(formRef, data), [formRef, props]);
  return (
    <>
      <AutoForm
        schema={bridge}
        onSubmit={onSubmitWrapper}
        disabled={props.disabled}
        ref={(ref: any) => (formRef = ref)}
      />
    </>
  );
};
