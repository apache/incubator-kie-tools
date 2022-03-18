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
import { ErrorBoundary } from "./ErrorBoundary";
import { dataPathToFormFieldPath } from "./uniforms/utils";
import { diff } from "deep-object-diff";
import cloneDeep from "lodash/cloneDeep";
import { FormStatus } from "./FormStatus";
import { FormJsonSchemaBridge } from "./uniforms/FormJsonSchemaBridge";
import { Validator } from "./Validator";

interface FormHook {
  name?: string;
  formError: boolean;
  setFormError: React.Dispatch<React.SetStateAction<boolean>>;
  formInputs: object;
  setFormInputs: React.Dispatch<React.SetStateAction<object>>;
  formSchema?: object;
  onSubmit?: (model: object) => void;
  onValidate?: (model: object, error: object) => void;
  propertiesPath?: string;
  validator?: Validator;
}

const getObjectByPath = (obj: Record<string, Record<string, object>>, path: string) =>
  path.split(".").reduce((acc: Record<string, Record<string, object>>, key: string) => acc[key], obj);

export function useForm({
  name,
  formError,
  setFormError,
  formInputs,
  setFormInputs,
  formSchema,
  onSubmit,
  onValidate,
  propertiesPath = "definitions.properties",
  validator,
}: FormHook) {
  const errorBoundaryRef = useRef<ErrorBoundary>(null);
  const [jsonSchemaBridge, setJsonSchemaBridge] = useState<FormJsonSchemaBridge>();
  const [formModel, setFormModel] = useState<object>();
  const [formStatus, setFormStatus] = useState<FormStatus>(FormStatus.EMPTY);
  const formValidator = useMemo(() => (validator ? validator : new Validator()), [validator]);

  const removeDeletedPropertiesAndAddDefaultValues = useCallback(
    (model: object, bridge: FormJsonSchemaBridge, previousBridge?: FormJsonSchemaBridge) => {
      const propertiesDifference = diff(
        getObjectByPath(previousBridge?.schema ?? {}, propertiesPath) ?? {},
        getObjectByPath(bridge.schema ?? {}, propertiesPath) ?? {}
      );

      const defaultFormValues = Object.keys(bridge?.schema?.properties ?? {}).reduce((acc, property) => {
        const field = bridge.getField(property);
        if (field.default) {
          acc[`${property}`] = field.default;
        }
        return acc;
      }, {} as Record<string, any>);

      // Remove property that has been deleted;
      return Object.entries(propertiesDifference).reduce(
        (form, [property, value]) => {
          if (!value || value.type || value.$ref) {
            delete (form as any)[property];
          }
          if (value?.format) {
            (form as any)[property] = undefined;
          }
          return form;
        },
        { ...defaultFormValues, ...model }
      );
    },
    [propertiesPath]
  );

  // When the schema is updated it's necessary to update the bridge and the model (remove deleted properties and
  // add default values to it)
  useEffect(() => {
    const form = cloneDeep(formSchema ?? {});
    if (Object.keys(form).length > 0) {
      // formPreprocessing(form);
    }
    try {
      const bridge = formValidator.getBridge(form);
      setJsonSchemaBridge((previousBridge) => {
        if (formModel) {
          const newFormModel = removeDeletedPropertiesAndAddDefaultValues(formModel, bridge, previousBridge);
          if (Object.keys(diff(formModel ?? {}, newFormModel ?? {})).length > 0) {
            setFormModel(newFormModel);
          } else {
            setFormModel(formModel);
          }
        }
        return bridge;
      });

      setFormStatus(FormStatus.WITHOUT_ERROR);
    } catch (err) {
      setFormStatus(FormStatus.VALIDATOR_ERROR);
    }
  }, [formModel, formSchema, formValidator, removeDeletedPropertiesAndAddDefaultValues]);

  // Manage form status
  useEffect(() => {
    if (formError) {
      setFormStatus(FormStatus.AUTO_GENERATION_ERROR);
    } else if (
      !formSchema ||
      Object.keys(getObjectByPath((formSchema as any) ?? {}, propertiesPath) ?? {}).length === 0
    ) {
      setFormStatus(FormStatus.EMPTY);
    } else if (jsonSchemaBridge) {
      setFormStatus(FormStatus.WITHOUT_ERROR);
      errorBoundaryRef.current?.reset();
    }
  }, [formError, formSchema, jsonSchemaBridge, formModel, propertiesPath]);

  // Resets the ErrorBoundary everytime the FormSchema is updated
  useEffect(() => {
    errorBoundaryRef.current?.reset();
  }, [formSchema]);

  // When form name changes, update the formModel
  useEffect(() => {
    const newFormModel = cloneDeep(formInputs);
    setFormModel(newFormModel);
  }, [name]);

  // When the formModel changes, update the formData and reset the formError
  useEffect(() => {
    setFormError((previousFormError) => {
      if (!previousFormError && formModel && Object.keys(formModel).length > 0) {
        const newFormInputs = cloneDeep(formModel);
        setFormInputs(newFormInputs);
      }
      return false;
    });
  }, [formModel, setFormError, setFormInputs]);

  const onFormSubmit = useCallback(
    (model) => {
      onSubmit?.(model);
    },
    [onSubmit]
  );

  // Validation occurs on every change and submit.
  const onFormValidate = useCallback(
    (model, error: any) => {
      onValidate?.(model, error);
      setFormModel((previousModel: any) => {
        if (Object.keys(diff(model, previousModel)).length > 0) {
          return model;
        }
        return previousModel;
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
        }, model);
      });
      return { details };
    },
    [onValidate]
  );

  return {
    onSubmit: onFormSubmit,
    onValidate: onFormValidate,
    formModel,
    formStatus,
    jsonSchemaBridge,
    errorBoundaryRef,
  };
}
