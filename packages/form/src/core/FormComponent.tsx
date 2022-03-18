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
import { useEffect, useMemo } from "react";
import cloneDeep from "lodash/cloneDeep";
import { FormBaseComponent } from "./FormBaseComponent";
import { useForm } from "./Form";
import { formI18n } from "../i18n";
import { Validator } from "./Validator";

export type Object = Record<string, object>;

export interface FormComponentProps {
  name?: string;
  formInputs: object;
  setFormInputs: React.Dispatch<object>;
  formError: boolean;
  setFormError: React.Dispatch<React.SetStateAction<boolean>>;
  formSchema?: object;
  id?: string;
  formRef?: React.RefObject<HTMLFormElement>;
  showInlineError?: boolean;
  autoSave?: boolean;
  autoSaveDelay?: number;
  placeholder?: boolean;
  onSubmit?: (model: object) => void;
  onValidate?: (model: object, error: object) => void;
  errorsField?: () => React.ReactNode;
  submitField?: () => React.ReactNode;
  locale?: string;
  propertiesPath?: string;
  notificationsPanel: boolean;
  openValidationTab: () => void;
}

export function FormComponent(props: FormComponentProps) {
  const i18n = useMemo(() => {
    formI18n.setLocale(props.locale ?? navigator.language);
    return formI18n.getCurrent();
  }, [props.locale]);

  const { onValidate, onSubmit, formModel, formStatus, jsonSchemaBridge, errorBoundaryRef } = useForm({
    name: props.name,
    formError: props.formError,
    setFormError: props.setFormError,
    formInputs: props.formInputs,
    setFormInputs: props.setFormInputs,
    formSchema: props.formSchema,
    onSubmit: props.onSubmit,
    onValidate: props.onValidate,
    propertiesPath: props.propertiesPath ?? "definitions.properties",
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
    />
  );
}
