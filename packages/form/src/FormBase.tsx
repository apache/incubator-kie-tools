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

import { AutoGenerationErrorFormStatus, EmptyFormStatus, FormStatus, ValidatorErrorFormStatus } from "./FormStatus";
import { ErrorBoundary } from "@kie-tools/dmn-runner/dist/ErrorBoundary";
import AutoForm from "@kie-tools/uniforms-patternfly/dist/esm/AutoForm";
import * as React from "react";
import { FormI18n } from "./i18n";
import { FormJsonSchemaBridge } from "./uniforms/FormJsonSchemaBridge";

export interface FormBaseProps {
  id?: string;
  i18n: FormI18n;
  setFormError: React.Dispatch<React.SetStateAction<boolean>>;
  setFormRef: React.Dispatch<React.SetStateAction<HTMLFormElement | null>>;
  showInlineError?: boolean;
  autoSave?: boolean;
  autoSaveDelay?: number;
  placeholder?: boolean;
  onSubmit?: (model: object) => void;
  onValidate?: (model: object, error: object) => void;
  errorsField?: () => React.ReactNode;
  submitField?: () => React.ReactNode;
  formStatus: FormStatus;
  notificationsPanel: boolean;
  openValidationTab?: () => void;
  errorBoundaryRef: React.RefObject<ErrorBoundary>;
  formModel?: object;
  jsonSchemaBridge?: FormJsonSchemaBridge;
}

export function FormBase(props: React.PropsWithChildren<FormBaseProps>) {
  return (
    <>
      {props.formStatus === FormStatus.VALIDATOR_ERROR && <ValidatorErrorFormStatus i18n={props.i18n} />}
      {props.formStatus === FormStatus.AUTO_GENERATION_ERROR && (
        <AutoGenerationErrorFormStatus
          notificationsPanel={props.notificationsPanel}
          i18n={props.i18n}
          openValidationTab={() => props.openValidationTab?.()}
        />
      )}
      {props.formStatus === FormStatus.EMPTY && <EmptyFormStatus i18n={props.i18n} />}
      {props.formStatus === FormStatus.WITHOUT_ERROR && (
        <div data-testid={"form-base"}>
          <ErrorBoundary
            ref={props.errorBoundaryRef}
            setHasError={props.setFormError}
            error={
              <AutoGenerationErrorFormStatus
                notificationsPanel={props.notificationsPanel}
                i18n={props.i18n}
                openValidationTab={() => props.openValidationTab?.()}
              />
            }
          >
            <AutoForm
              id={props.id}
              model={props.formModel}
              ref={(node: HTMLFormElement) => props.setFormRef(node)}
              showInlineError={props.showInlineError}
              autosave={props.autoSave}
              autosaveDelay={props.autoSaveDelay}
              schema={props.jsonSchemaBridge}
              placeholder={props.placeholder}
              onSubmit={props.onSubmit}
              onValidate={props.onValidate}
              errorsField={props.errorsField}
              submitField={props.submitField}
              validate={"onSubmit"}
            >
              {props.children}
            </AutoForm>
          </ErrorBoundary>
        </div>
      )}
    </>
  );
}
