/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import * as React from "react";
import { useEffect, useMemo, useRef } from "react";
import { DmnValidator } from "./DmnValidator";
import { formI18n } from "../i18n";
import cloneDeep from "lodash/cloneDeep";
import { FormBaseComponent } from "../core/FormBaseComponent";
import { useForm } from "../core/Form";
import { FormComponentProps } from "../core/FormComponent";
import { DmnAutoFieldProvider } from "./uniforms/DmnAutoFieldProvider";

export type InputRow = Record<string, string>;

type DmnDecisionNodes = "InputSet" | string;

export interface DmnSchema {
  definitions?: {
    [x in DmnDecisionNodes]?: {
      type: string;
      properties: { [x: string]: DmnDeepProperty };
    };
  };
}

export interface DmnFormData {
  definitions: DmnFormDefinitions;
}

type DmnFormDefinitions = {
  [x in DmnDecisionNodes]?: {
    required?: string[];
    properties: object;
    type: string;
    placeholder?: string;
    title?: string;
    format?: string;
    items: any[] & { properties: any };
    "x-dmn-type"?: string;
  };
};

interface DmnDeepProperty {
  $ref?: string;
  type?: string;
  placeholder?: string;
  title?: string;
  format?: string;
  "x-dmn-type"?: string;
  properties?: DmnDeepProperty;
}

export function usePrevious(value: any) {
  const ref = useRef();

  useEffect(() => {
    ref.current = value;
  }, [value]);

  return ref.current;
}

interface DmnFormComponentProps extends FormComponentProps {
  formInputs: InputRow;
  formSchema?: DmnSchema;
}

export function DmnFormComponent(props: DmnFormComponentProps) {
  const i18n = useMemo(() => {
    formI18n.setLocale(props.locale ?? navigator.language);
    return formI18n.getCurrent();
  }, [props.locale]);
  const dmnValidator = useMemo(() => new DmnValidator(i18n), [i18n]);

  const { onValidate, onSubmit, formModel, formStatus, jsonSchemaBridge, errorBoundaryRef } = useForm({
    validator: dmnValidator,
    name: props.name,
    formError: props.formError,
    setFormError: props.setFormError,
    formInputs: props.formInputs,
    setFormInputs: props.setFormInputs,
    formSchema: props.formSchema,
    onSubmit: props.onSubmit,
    onValidate: props.onValidate,
    propertiesPath: "definitions.InputSet.properties",
  });

  return (
    <FormBaseComponent
      i18n={i18n}
      onValidate={onValidate}
      onSubmit={onSubmit}
      formModel={formModel}
      formStatus={formStatus}
      jsonSchemaBridge={jsonSchemaBridge}
      errorBoundaryRef={errorBoundaryRef}
      setFormError={props.setFormError}
      id={props.id}
      formRef={props.formRef}
      showInlineError={props.showInlineError}
      autoSave={props.autoSave}
      autoSaveDelay={props.autoSaveDelay}
      placeholder={props.placeholder}
      errorsField={props.errorsField}
      submitField={props.submitField}
      locale={props.locale}
      notificationsPanel={props.notificationsPanel}
      openValidationTab={props.openValidationTab}
    >
      <DmnAutoFieldProvider />
    </FormBaseComponent>
  );
}
