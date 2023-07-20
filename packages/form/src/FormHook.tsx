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
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { ErrorBoundary } from "@kie-tools/dmn-runner/dist/ErrorBoundary";
import { dataPathToFormFieldPath } from "./uniforms/utils";
import { diff } from "deep-object-diff";
import cloneDeep from "lodash/cloneDeep";
import { FormStatus } from "./FormStatus";
import { FormJsonSchemaBridge } from "./uniforms/FormJsonSchemaBridge";
import { Validator } from "./Validator";
import { FormI18n } from "./i18n";

export interface FormHook<Input extends Record<string, any>, Schema extends Record<string, any>> {
  formError: boolean;
  setFormError: React.Dispatch<React.SetStateAction<boolean>>;
  formInputs: Input;
  setFormInputs: React.Dispatch<React.SetStateAction<Input>>;
  formSchema?: Schema;
  onSubmit?: (model: object) => void;
  onValidate?: (model: object, error: object) => void;
  entryPath?: string;
  propertiesEntryPath?: string;
  validator?: Validator;
  removeRequired?: boolean;
  i18n: FormI18n;
  setFormRef?: (formElement: HTMLFormElement | null) => void;
}

const getObjectByPath = (obj: Record<string, Record<string, object>>, path: string) =>
  path.split(".").reduce((acc: Record<string, Record<string, object>>, key: string) => acc?.[key] ?? {}, obj);

export function useForm<Input extends Record<string, any>, Schema extends Record<string, any>>({
  formError,
  setFormError,
  formInputs,
  setFormInputs,
  formSchema,
  onSubmit,
  onValidate,
  entryPath = "definitions",
  propertiesEntryPath = "definitions",
  validator,
  removeRequired = false,
  i18n,
  setFormRef,
}: FormHook<Input, Schema>) {
  const errorBoundaryRef = useRef<ErrorBoundary>(null);
  const [jsonSchemaBridge, setJsonSchemaBridge] = useState<FormJsonSchemaBridge>();
  const [formStatus, setFormStatus] = useState<FormStatus>(FormStatus.EMPTY);
  const formValidator = useMemo(() => (validator ? validator : new Validator(i18n)), [validator, i18n]);
  const [ref, setRef] = useState<HTMLFormElement | null>(null);

  const onFormSubmit = useCallback(
    (formInputs) => {
      onSubmit?.(formInputs);
    },
    [onSubmit]
  );

  // Validation occurs on every change and submit.
  const onFormValidate = useCallback(
    (formInputs, error: any) => {
      onValidate?.(formInputs, error);
      setFormInputs((previousInputs) => {
        if (Object.keys(diff(formInputs, previousInputs ?? {})).length > 0) {
          return formInputs;
        }
        return previousInputs ?? {};
      });
      if (!error) {
        return;
      }
      // if the form has an error, the error should be displayed and the outputs column should be updated anyway.
      const {
        details,
        changes,
      }: {
        details: object[];
        changes: Array<[string, string | number | undefined]>;
      } = error.details.reduce(
        (infos: any, detail: any) => {
          if (detail.keyword === "type") {
            // If it's a type error, it's handled by replacing the current value with a undefined value.
            const formFieldPath = dataPathToFormFieldPath(detail.dataPath);
            infos.changes = [...infos.changes, [formFieldPath, undefined]];
            return infos;
          } else if (detail.keyword === "enum") {
            // A enum error is caused by a type error.
            const formFieldPath = dataPathToFormFieldPath(detail.dataPath);
            infos.changes = [...infos.changes, [formFieldPath, undefined]];
            return infos;
          }
          infos.details = [...infos.details, detail];
          return infos;
        },
        { details: [], changes: [] }
      );
      // Update formInputs with the current change.
      changes.forEach(([formFieldPath, fieldValue]) => {
        formFieldPath?.split(".")?.reduce((deeper, field, index, array) => {
          if (index === array.length - 1) {
            deeper[field] = fieldValue;
          } else {
            return deeper[field];
          }
        }, formInputs);
      });
      return { details };
    },
    [onValidate, setFormInputs]
  );

  // When the schema is updated it's necessary to update the bridge and the formInputs (remove deleted properties and
  // add default values to it)
  useEffect(() => {
    try {
      const form = cloneDeep(formSchema ?? {}) as Record<string, Record<string, object>>;
      if (removeRequired) {
        const entry = getObjectByPath(form, entryPath);
        delete entry.required;
        delete form.required;
      }
      setJsonSchemaBridge(formValidator.getBridge(form));

      setFormStatus(FormStatus.WITHOUT_ERROR);
    } catch (err) {
      console.error(err);
      setFormStatus(FormStatus.VALIDATOR_ERROR);
    }
  }, [setFormInputs, formSchema, formValidator, entryPath, removeRequired]);

  // Manage form status
  useEffect(() => {
    if (formError) {
      setFormStatus(FormStatus.AUTO_GENERATION_ERROR);
    } else if (
      !formSchema ||
      Object.keys(getObjectByPath((formSchema as any) ?? {}, propertiesEntryPath) ?? {}).length === 0
    ) {
      setFormStatus(FormStatus.EMPTY);
    } else if (jsonSchemaBridge) {
      setFormStatus(FormStatus.WITHOUT_ERROR);
      errorBoundaryRef.current?.reset();
    }
  }, [formError, formSchema, jsonSchemaBridge, propertiesEntryPath]);

  // Resets the ErrorBoundary everytime the FormSchema is updated
  useEffect(() => {
    errorBoundaryRef.current?.reset();
  }, [formSchema]);

  // When the formInput changes, reset the formError
  useEffect(() => {
    setFormError(false);
  }, [formInputs]);

  // Submits the form in the first render triggering the onValidate function
  useEffect(() => {
    ref?.submit();
    setFormRef?.(ref);
  }, [setFormRef, ref]);

  return {
    onSubmit: onFormSubmit,
    onValidate: onFormValidate,
    formStatus,
    jsonSchemaBridge,
    errorBoundaryRef,
    setFormRef: setRef,
  };
}
