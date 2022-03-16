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
import { ErrorBoundary } from "../common/ErrorBoundary";
import AutoForm from "uniforms-patternfly/dist/es6/AutoForm";
import * as React from "react";
import { FormJsonSchemaBridge } from "./uniforms/FormJsonSchemaBridge";
import { FormI18n } from "../i18n/FormI18n";

interface CommonProps {
  setFormError: React.Dispatch<React.SetStateAction<boolean>>;
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
  formStatus: FormStatus;
  errorBoundaryRef: React.RefObject<ErrorBoundary>;
  formModel?: object;
  jsonSchemaBridge?: FormJsonSchemaBridge;
  i18n: FormI18n;
}

interface PropsWithNotificationsPanel extends CommonProps {
  notificationsPanel: true;
  openValidationTab: () => void;
}

interface PropsWithoutNotificationsPanel extends CommonProps {
  notificationsPanel: false;
}

export type Props = PropsWithNotificationsPanel | PropsWithoutNotificationsPanel;

export function FormBaseComponent(props: React.PropsWithChildren<Props>) {
  return (
    <>
      {props.formStatus === FormStatus.VALIDATOR_ERROR && <ValidatorErrorFormStatus i18n={props.i18n} />}
      {props.formStatus === FormStatus.AUTO_GENERATION_ERROR && (
        <AutoGenerationErrorFormStatus
          notificationsPanel={props.notificationsPanel}
          i18n={props.i18n}
          openValidationTab={() => (props.notificationsPanel ? props.openValidationTab() : undefined)}
        />
      )}
      {props.formStatus === FormStatus.EMPTY && <EmptyFormStatus i18n={props.i18n} />}
      {props.formStatus === FormStatus.WITHOUT_ERROR && (
        <div data-testid={"base-form"}>
          <ErrorBoundary
            ref={props.errorBoundaryRef}
            setHasError={props.setFormError}
            error={
              <AutoGenerationErrorFormStatus
                notificationsPanel={props.notificationsPanel}
                i18n={props.i18n}
                openValidationTab={() => (props.notificationsPanel ? props.openValidationTab() : undefined)}
              />
            }
          >
            <AutoForm
              id={props.id}
              model={props.formModel}
              ref={props.formRef}
              showInlineError={props.showInlineError}
              autosave={props.autoSave}
              autosaveDelay={props.autoSaveDelay}
              schema={props.jsonSchemaBridge}
              placeholder={props.placeholder}
              onSubmit={props.onSubmit}
              onValidate={props.onValidate}
              errorsField={props.errorsField}
              submitField={props.submitField}
              validate={"onChange"}
            >
              {props.children}
            </AutoForm>
          </ErrorBoundary>
        </div>
      )}
    </>
  );
}
