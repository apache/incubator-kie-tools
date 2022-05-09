/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
import { useMemo } from "react";
import { FormBase } from "./FormBase";
import { FormHook, useForm } from "./FormHook";
import { formI18n } from "./i18n";

export interface FormProps<Input, Schema> {
  id?: string;
  name?: string;
  locale: string;
  formRef?: React.RefObject<HTMLFormElement>;
  showInlineError?: boolean;
  autoSave?: boolean;
  autoSaveDelay?: number;
  placeholder?: boolean;
  onSubmit?: (model: object) => void;
  onValidate?: (model: object, error: object) => void;
  errorsField?: () => React.ReactNode;
  submitField?: () => React.ReactNode;
  notificationsPanel: boolean;
  openValidationTab?: () => void;
  formError: boolean;
  setFormError: React.Dispatch<React.SetStateAction<boolean>>;
  formInputs: Input;
  setFormInputs: React.Dispatch<React.SetStateAction<Input>>;
  formSchema?: Schema;
}

export type FormComponentProps<Input, Schema> = FormProps<Input, Schema> & FormHook<Input, Schema>;

export function FormComponent(props: React.PropsWithChildren<FormComponentProps<object, object>>) {
  const i18n = useMemo(
    () => props.i18n ?? formI18n.setLocale(props.locale ?? navigator.language).getCurrent(),
    [props.i18n, props.locale]
  );

  const { onValidate, onSubmit, formModel, formStatus, jsonSchemaBridge, errorBoundaryRef } = useForm({
    i18n,
    name: props.name,
    formError: props.formError,
    setFormError: props.setFormError,
    formInputs: props.formInputs,
    setFormInputs: props.setFormInputs,
    formSchema: props.formSchema,
    onSubmit: props.onSubmit,
    onValidate: props.onValidate,
    removeRequired: props.removeRequired,
    entryPath: props.entryPath,
    propertiesEntryPath: props.propertiesEntryPath,
    validator: props.validator,
  });

  return (
    <>
      <FormBase
        {...props}
        i18n={i18n}
        formStatus={formStatus}
        errorBoundaryRef={errorBoundaryRef}
        jsonSchemaBridge={jsonSchemaBridge}
        formModel={formModel}
        onSubmit={onSubmit}
        onValidate={onValidate}
      >
        {props.children}
      </FormBase>
    </>
  );
}
