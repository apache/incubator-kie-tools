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
import { useImperativeHandle, useState } from "react";
import JSONSchemaBridge from "uniforms-bridge-json-schema";
import { AutoFields, AutoForm, ErrorsField } from "uniforms-patternfly";
import { DefaultFormValidator, FormValidator, ModelConversionTool } from "./utils";

export interface Props {
  formSchema: object;
  model?: object;
  readOnly?: boolean;
  showErrorsHeader?: boolean;
  onSubmit?: (model: object) => void;
  autoSave?: boolean;
  autoSaveDelay?: number;
}

/**
 * Api to expose the forms methods
 */
export interface FormApi {
  /**
   * Submits the current form
   */
  submit: () => void;

  /**
   * Resets the form to its original state
   */
  reset: () => void;

  /**
   * Sets a value to the field identified by the key. Supports nesting by using the '.' separator, ej: 'user.name'
   * @param key the field we want modify
   * @param value to set
   */
  change: (key: string, value: any) => void;
}

const FormRenderer = React.forwardRef<FormApi, Props>(
  (
    { formSchema, model, readOnly, showErrorsHeader: showErrorsHeader, onSubmit, autoSave, autoSaveDelay = 0 },
    forwardedRef
  ) => {
    const [formRef, setFormRef] = useState<FormApi>();

    // Converting Dates that are in string format into JS Dates so they can be correctly bound to the uniforms DateField
    const [formData] = useState<object>(ModelConversionTool.convertStringToDate(model, formSchema));

    const [validator] = useState<FormValidator>(new DefaultFormValidator(formSchema));

    const [bridge] = useState(
      new JSONSchemaBridge(formSchema, (formModel) => {
        // Converting back all the JS Dates into String before validating the model
        const newModel = ModelConversionTool.convertDateToString(formModel, formSchema);
        return validator.validate(newModel);
      })
    );

    useImperativeHandle(
      forwardedRef,
      () => {
        return {
          submit: () => {
            formRef?.submit();
          },

          reset: () => {
            formRef?.reset();
          },

          change: (key, value) => {
            formRef?.change(key, value);
          },
        };
      },
      [formRef]
    );

    return (
      <AutoForm
        placeholder={true}
        ref={setFormRef}
        model={formData}
        disabled={readOnly}
        schema={bridge}
        showInlineError={true}
        autosave={autoSave}
        autosaveDelay={autoSaveDelay}
        onSubmit={(submitData: object) => {
          if (onSubmit) {
            onSubmit(submitData);
          }
        }}
        role={"form"}
      >
        {showErrorsHeader && <ErrorsField />}
        <AutoFields />
      </AutoForm>
    );
  }
);

export default FormRenderer;
