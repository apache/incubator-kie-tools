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
import { useCallback, useEffect, useMemo, useRef } from "react";
import { DmnValidator } from "./DmnValidator";
import { formI18n } from "../i18n";
import cloneDeep from "lodash/cloneDeep";
import { FormBaseComponent } from "../core/FormBaseComponent";
import { useForm } from "../core/Form";
import { FormComponentProps } from "../core/FormComponent";

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
  const contextPath = useMemo(() => new Map<string, string[]>(), []);
  const dmnValidator = useMemo(() => new DmnValidator(i18n), [i18n]);

  const { onValidate, onSubmit, formModel, setFormModel, formStatus, jsonSchemaBridge, errorBoundaryRef } = useForm({
    validator: dmnValidator,
    formError: props.formError,
    formSchema: props.formSchema,
    onSubmit: props.onSubmit,
    onValidate: props.onValidate,
    propertiesPath: "definitions.InputSet.properties",
  });

  // const formDeepPreprocessing = useCallback(
  //   (form: DmnFormData, value: DmnDeepProperty, title = [""]) => {
  //     if (value.$ref) {
  //       const property = value.$ref!.split("/").pop()! as keyof DmnFormDefinitions;
  //       if (form.definitions[property] && form.definitions[property]?.properties) {
  //         Object.entries(form.definitions[property]!.properties).forEach(
  //           ([key, deepValue]: [string, DmnDeepProperty]) => {
  //             formDeepPreprocessing(form, deepValue, [...title, key]);
  //           }
  //         );
  //       } else if (form.definitions[property] && form.definitions[property]?.type === "array") {
  //         if (form.definitions[property]?.items.properties) {
  //           Object.entries(form.definitions[property]?.items.properties).forEach(
  //             ([key, deepValue]: [string, DmnDeepProperty]) => {
  //               formDeepPreprocessing(form, deepValue, [...title, key]);
  //             }
  //           );
  //         } else {
  //           formDeepPreprocessing(form, form.definitions[property]!.items as DmnDeepProperty, [...title]);
  //         }
  //       } else if (form?.definitions?.[property]?.["x-dmn-type"] === "FEEL:context") {
  //         form.definitions[property]!.placeholder = `{ "x": <value> }`;
  //         contextPath.set(title.join(""), title);
  //       }
  //     }
  //     if (value?.["x-dmn-type"] === "FEEL:context") {
  //       value!.placeholder = `{ "x": <value> }`;
  //       contextPath.set(title.join(""), title);
  //     }
  //   },
  //   [contextPath]
  // );
  //
  // // Remove required property and make deep preprocessing
  // const formPreprocessing = useCallback(
  //   (form: DmnFormData) => {
  //     delete form.definitions?.InputSet?.required;
  //     if (Object.hasOwnProperty.call(form.definitions.InputSet, "properties")) {
  //       Object.entries(form.definitions.InputSet?.properties ?? {}).forEach(
  //         ([key, value]: [string, DmnDeepProperty]) => {
  //           formDeepPreprocessing(form, value, [key]);
  //         }
  //       );
  //     }
  //   },
  //   [formDeepPreprocessing]
  // );

  // FIXME DMN -> CONTEXT PATH
  // contextPath -> map of FEEL:context
  // formData -> object with form information
  // formModel -> object used by uniforms
  // FEEL:context is a written object in the form (formModel), and it's parsed to set the formData
  const handleContextPath: (obj: any, path: string[], operation?: "parse" | "stringify") => void = useCallback(
    (obj, path, operation) => {
      const key = path?.shift();
      if (!key) {
        return;
      }

      const prop: any = obj[key];
      if (!prop) {
        return;
      }
      if (prop && path.length !== 0) {
        if (Array.isArray(prop)) {
          prop.forEach((e, index) => {
            const nextKey = path?.[0];
            if (Object.hasOwnProperty.call(e, nextKey)) {
              try {
                if (operation === "parse") {
                  obj[key][index] = JSON.parse(e[nextKey]);
                } else if (operation === "stringify") {
                  obj[key][index] = JSON.stringify(e[nextKey]);
                }
              } catch (err) {
                obj[key][index] = prop;
              }
            }
          });
          return;
        }
        return handleContextPath(prop, path, operation);
      }

      try {
        if (operation === "parse") {
          obj[key] = JSON.parse(prop);
        } else if (operation === "stringify") {
          obj[key] = JSON.stringify(prop);
        }
      } catch (err) {
        obj[key] = prop;
      }
    },
    []
  );

  // When the formModel changes, stringify all context inputs and set the formData and reset the formError
  useEffect(() => {
    props.setFormError((previousFormError: boolean) => {
      if (!previousFormError && formModel && Object.keys(formModel).length > 0) {
        const newFormData = cloneDeep(formModel);
        contextPath.forEach((path) => {
          const pathCopy = [...path];
          handleContextPath(newFormData, pathCopy, "parse");
        });
        props.setFormInputs(newFormData);
      }
      return false;
    });
  }, [contextPath, formModel, handleContextPath]);

  // on firstRender stringify all context inputs and set the formModel
  useEffect(() => {
    const newFormModel = cloneDeep(props.formInputs);
    contextPath.forEach((path) => {
      const pathCopy = [...path];
      handleContextPath(newFormModel, pathCopy, "stringify");
    });
    setFormModel(newFormModel);
  }, [props.name]);

  return (
    <>
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
    </>
  );
}
