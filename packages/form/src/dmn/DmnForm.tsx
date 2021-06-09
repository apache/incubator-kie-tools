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
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { AutoForm } from "uniforms-patternfly";
import { ErrorBoundary } from "../common/ErrorBoundary";
import { dataPathToFormFieldPath } from "./uniforms/utils";
import { DmnFormJsonSchemaBridge } from "./uniforms";
import { DmnValidator } from "./DmnValidator";
import { dmnFormI18n } from "./i18n";

export interface DmnRunnerForm {
  definitions: DmnRunnerFormDefinitions;
}

interface DmnRunnerFormDefinitions {
  InputSet?: {
    required?: string[];
    properties: object;
    type: string;
    placeholder?: string;
    title?: string;
    format?: string;
  };
}

interface DmnRunnerDeepProperty {
  $ref?: string;
  type?: string;
  placeholder?: string;
  title?: string;
  format?: string;
}

export interface Props {
  formData: any;
  setFormData: React.Dispatch<any>;
  formError: boolean;
  setFormError: React.Dispatch<boolean>;
  formErrorMessage: React.ReactNode;
  formSchema?: any;
  updateDmnRunnerResults: (model: any) => void;
  id?: string;
  formRef?: React.RefObject<HTMLFormElement>;
  showInlineError?: boolean;
  autosave?: boolean;
  autosaveDelay?: number;
  placeholder?: boolean;
  onSubmit?: (model: any) => void;
  onValidate?: (model: any, error: any) => void;
  errorsField?: () => React.ReactNode;
  submitField?: () => React.ReactNode;
  locale?: string;
}

export function DmnForm(props: Props) {
  const errorBoundaryRef = useRef<ErrorBoundary>(null);
  const [jsonSchemaBridge, setJsonSchemaBridge] = useState<DmnFormJsonSchemaBridge>();
  const i18n = useMemo(() => {
    dmnFormI18n.setLocale(props.locale ?? navigator.language);
    return dmnFormI18n.getCurrent();
  }, [props.locale]);
  const validator = useMemo(() => new DmnValidator(i18n), []);

  const setCustomPlaceholders = useCallback((value: DmnRunnerDeepProperty) => {
    if (value?.format === "days and time duration") {
      value!.placeholder = i18n.form.preProcessing.daysAndTimePlaceholder;
    }
    if (value?.format === "years and months duration") {
      value!.placeholder = i18n.form.preProcessing.yearsAndMonthsPlaceholder;
    }
  }, []);

  const formDeepPreprocessing = useCallback(
    (form: DmnRunnerForm, value: DmnRunnerDeepProperty, title = "") => {
      if (Object.hasOwnProperty.call(value, "$ref")) {
        const property = value.$ref!.split("/").pop()! as keyof DmnRunnerFormDefinitions;
        if (form.definitions[property] && Object.hasOwnProperty.call(form.definitions[property], "properties")) {
          Object.entries(form.definitions[property]!.properties).forEach(
            ([key, deepValue]: [string, DmnRunnerDeepProperty]) => {
              formDeepPreprocessing(form, deepValue, key);
            }
          );
        } else if (!Object.hasOwnProperty.call(form.definitions[property], "type")) {
          form.definitions[property]!.type = "string";
        } else if (Object.hasOwnProperty.call(form.definitions[property], "enum")) {
          form.definitions[property]!.placeholder = i18n.form.preProcessing.selectPlaceholder;
        } else if (Object.hasOwnProperty.call(form.definitions[property], "format")) {
          setCustomPlaceholders(form.definitions[property]!);
        }
        form.definitions[property]!.title = title;
        return;
      }
      value.title = title;
      if (!Object.hasOwnProperty.call(value, "type")) {
        value.type = "string";
        return;
      }
      if (Object.hasOwnProperty.call(value, "format")) {
        setCustomPlaceholders(value);
      }
    },
    [setCustomPlaceholders]
  );

  // Remove required property and make deep preprocessing
  const formPreprocessing = useCallback(
    (form: DmnRunnerForm) => {
      delete form.definitions?.InputSet?.required;
      if (Object.hasOwnProperty.call(form.definitions.InputSet, "properties")) {
        Object.entries(form.definitions.InputSet?.properties ?? {}).forEach(
          ([key, value]: [string, DmnRunnerDeepProperty]) => {
            formDeepPreprocessing(form, value, key);
          }
        );
      }
    },
    [formDeepPreprocessing]
  );

  useEffect(() => {
    const form: DmnRunnerForm = Object.assign(props.formSchema ?? {}, {});
    if (Object.keys(form).length > 0) {
      formPreprocessing(form);
    }
    setJsonSchemaBridge(validator.getBridge(form));
  }, [props.formSchema, formPreprocessing, validator]);

  const onSubmit = useCallback(
    (model) => {
      props.onSubmit?.(model);
      props.setFormData(model);
    },
    [props.setFormData]
  );

  // Validation occurs on every change and submit.
  const onValidate = useCallback(
    (model, error: any) => {
      props.onValidate?.(model, error);
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
      // Update formData with the current change.
      changes.forEach(([formFieldPath, fieldValue]) => {
        formFieldPath?.split(".")?.reduce((deeper, field, index, array) => {
          if (index === array.length - 1) {
            deeper[field] = fieldValue;
          } else {
            return deeper[field];
          }
        }, model);
      });
      props.setFormData(model);
      return { details };
    },
    [props.setFormData]
  );

  // Resets the ErrorBoundary everytime the JsonSchemaBridge is updated
  useEffect(() => {
    errorBoundaryRef.current?.reset();
  }, [props.formSchema]);

  return (
    <div data-testid={"dmn-form"}>
      {jsonSchemaBridge && (
        <ErrorBoundary ref={errorBoundaryRef} setHasError={props.setFormError} error={props.formErrorMessage}>
          <AutoForm
            id={props.id}
            model={props.formData}
            ref={props.formRef}
            showInlineError={props.showInlineError}
            autosave={props.autosave}
            autosaveDelay={props.autosaveDelay}
            schema={jsonSchemaBridge}
            placeholder={props.placeholder}
            onSubmit={onSubmit}
            onValidate={onValidate}
            errorsField={props.errorsField ?? (() => <></>)}
            submitField={props.submitField ?? (() => <></>)}
          />
        </ErrorBoundary>
      )}
    </div>
  );
}
