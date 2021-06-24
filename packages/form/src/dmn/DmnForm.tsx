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
import { diff } from "deep-object-diff";
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { ExclamationTriangleIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-triangle-icon";
import { I18nWrapped } from "@kogito-tooling/i18n/dist/react-components";
import { ExclamationIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-icon";
import { CubesIcon } from "@patternfly/react-icons/dist/js/icons/cubes-icon";

const KOGITO_JIRA_LINK = "https://issues.jboss.org/projects/KOGITO";

enum FormStatus {
  WITHOUT_ERROR,
  VALIDATOR_ERROR,
  AUTO_GENERATION_ERROR,
  EMPTY,
}

export interface DmnFormData {
  definitions: DmnFormDefinitions;
}

interface DmnFormDefinitions {
  InputSet?: {
    required?: string[];
    properties: object;
    type: string;
    placeholder?: string;
    title?: string;
    format?: string;
    items: any[];
  };
}

interface DmnDeepProperty {
  $ref?: string;
  type?: string;
  placeholder?: string;
  title?: string;
  format?: string;
}

interface CommonProps {
  formData: any;
  setFormData: React.Dispatch<any>;
  formError: boolean;
  setFormError: React.Dispatch<any>;
  formSchema?: any;
  updateDmnResults: (model: any) => void;
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

interface PropsWithNotificationsPanel extends CommonProps {
  notificationsPanel: true;
  openValidationTab: () => void;
}

interface PropsWithoutNotificationsPanel extends CommonProps {
  notificationsPanel: false;
}

export type Props = PropsWithNotificationsPanel | PropsWithoutNotificationsPanel;

export function usePrevious(value: any) {
  const ref = useRef();

  useEffect(() => {
    ref.current = value;
  }, [value]);

  return ref.current;
}

export function DmnForm(props: Props) {
  const errorBoundaryRef = useRef<ErrorBoundary>(null);
  const [jsonSchemaBridge, setJsonSchemaBridge] = useState<DmnFormJsonSchemaBridge>();
  const i18n = useMemo(() => {
    dmnFormI18n.setLocale(props.locale ?? navigator.language);
    return dmnFormI18n.getCurrent();
  }, [props.locale]);
  const validator = useMemo(() => new DmnValidator(i18n), []);
  const [formModel, setFormModel] = useState<any>(props.formData);
  const [formStatus, setFormStatus] = useState<FormStatus>(FormStatus.EMPTY);

  const setCustomPlaceholders = useCallback((value: DmnDeepProperty) => {
    if (value?.format === "days and time duration") {
      value!.placeholder = i18n.form.preProcessing.daysAndTimePlaceholder;
    }
    if (value?.format === "years and months duration") {
      value!.placeholder = i18n.form.preProcessing.yearsAndMonthsPlaceholder;
    }
  }, []);

  const formDeepPreprocessing = useCallback(
    (form: DmnFormData, value: DmnDeepProperty, title = "") => {
      if (Object.hasOwnProperty.call(value, "$ref")) {
        const property = value.$ref!.split("/").pop()! as keyof DmnFormDefinitions;
        if (form.definitions[property] && Object.hasOwnProperty.call(form.definitions[property], "properties")) {
          Object.entries(form.definitions[property]!.properties).forEach(
            ([key, deepValue]: [string, DmnDeepProperty]) => {
              formDeepPreprocessing(form, deepValue, key);
            }
          );
        } else if (
          form.definitions[property] &&
          form.definitions[property]?.type === "array" &&
          Object.hasOwnProperty.call(form.definitions[property]?.items, "$ref")
        ) {
          formDeepPreprocessing(form, form.definitions[property]!.items as DmnDeepProperty);
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
      if (Object.hasOwnProperty.call(value, "enum")) {
        value.placeholder = i18n.form.preProcessing.selectPlaceholder;
      }
      if (Object.hasOwnProperty.call(value, "format")) {
        setCustomPlaceholders(value);
      }
    },
    [setCustomPlaceholders]
  );

  // Remove required property and make deep preprocessing
  const formPreprocessing = useCallback(
    (form: DmnFormData) => {
      delete form.definitions?.InputSet?.required;
      if (Object.hasOwnProperty.call(form.definitions.InputSet, "properties")) {
        Object.entries(form.definitions.InputSet?.properties ?? {}).forEach(
          ([key, value]: [string, DmnDeepProperty]) => {
            formDeepPreprocessing(form, value, key);
          }
        );
      }
    },
    [formDeepPreprocessing]
  );

  const defaultFormValues = useMemo(() => {
    return Object.keys(jsonSchemaBridge?.schema?.properties ?? {}).reduce((acc, property) => {
      if (Object.hasOwnProperty.call(jsonSchemaBridge?.schema?.properties[property], "$ref")) {
        const refPath = jsonSchemaBridge?.schema?.properties[property].$ref!.split("/").pop() ?? "";
        if (jsonSchemaBridge?.schema?.definitions?.[refPath].type === "object") {
          acc[`${property}`] = {};
          return acc;
        }
        if (jsonSchemaBridge?.schema?.definitions?.[refPath]?.type === "array") {
          acc[`${property}`] = [];
          return acc;
        }
        if (jsonSchemaBridge?.schema?.definitions?.[refPath]?.type === "boolean") {
          acc[`${property}`] = false;
          return acc;
        }
      }
      if (jsonSchemaBridge?.schema?.properties?.[property]?.type === "object") {
        acc[`${property}`] = {};
        return acc;
      }
      if (jsonSchemaBridge?.schema?.properties?.[property]?.type === "array") {
        acc[`${property}`] = [];
        return acc;
      }
      if (jsonSchemaBridge?.schema?.properties?.[property]?.type === "boolean") {
        acc[`${property}`] = false;
        return acc;
      }
      return acc;
    }, {} as { [x: string]: any });
  }, [jsonSchemaBridge]);

  const previousFormSchema: any = usePrevious(props.formSchema);
  useEffect(() => {
    const propertiesDifference = diff(
      previousFormSchema?.definitions?.InputSet?.properties ?? {},
      props.formSchema?.definitions?.InputSet?.properties ?? {}
    );

    // Remove an formData property that has been deleted;
    props.setFormError((previous: boolean) => {
      if (!previous) {
        const formData = Object.entries(propertiesDifference).reduce(
          (newFormData, [property, value]) => {
            if (!value || value.type) {
              delete (newFormData as any)[property];
            }
            if (value?.format) {
              (newFormData as any)[property] = undefined;
            }
            return newFormData;
          },
          { ...defaultFormValues, ...formModel }
        );
        props.setFormData(formData);
      }
      return false;
    });
  }, [props.setFormData, props.setFormError, props.onSubmit, defaultFormValues, props.formSchema, formModel]);

  useEffect(() => {
    const form: DmnFormData = Object.assign(props.formSchema ?? {}, {});
    if (Object.keys(form).length > 0) {
      formPreprocessing(form);
    }
    try {
      const bridge = validator.getBridge(form);
      setJsonSchemaBridge(bridge);
      setFormStatus(FormStatus.WITHOUT_ERROR);
    } catch (err) {
      console.error(err);
      setFormStatus(FormStatus.VALIDATOR_ERROR);
    }
  }, [props.formSchema, formPreprocessing, validator]);

  const onSubmit = useCallback(
    (model) => {
      props.onSubmit?.(model);
    },
    [props.onSubmit]
  );

  // Validation occurs on every change and submit.
  const onValidate = useCallback(
    (model, error: any) => {
      props.onValidate?.(model, error);
      setFormModel(model);
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
      return { details };
    },
    [setFormModel, props.onValidate]
  );

  const formErrorMessage = useMemo(
    () => (
      <div>
        <EmptyState>
          <EmptyStateIcon icon={ExclamationIcon} />
          <TextContent>
            <Text component={"h2"}>{i18n.form.status.autoGenerationError.title}</Text>
          </TextContent>
          <EmptyStateBody>
            <TextContent>{i18n.form.status.autoGenerationError.explanation}</TextContent>
            <br />
            <TextContent>
              {props.notificationsPanel && (
                <I18nWrapped components={{ link: <a onClick={props?.openValidationTab}>{i18n.terms.validation}</a> }}>
                  {i18n.form.status.autoGenerationError.checkNotificationPanel}
                </I18nWrapped>
              )}
            </TextContent>
          </EmptyStateBody>
        </EmptyState>
      </div>
    ),
    [props.notificationsPanel, i18n]
  );

  useEffect(() => {
    if (props.formError) {
      setFormStatus(FormStatus.AUTO_GENERATION_ERROR);
    } else if (
      !props.formSchema ||
      Object.keys(props.formSchema?.definitions?.InputSet?.properties ?? {}).length === 0
    ) {
      setFormStatus(FormStatus.EMPTY);
    } else {
      setFormStatus(FormStatus.WITHOUT_ERROR);
    }
  }, [props.formError, props.formSchema]);

  // Resets the ErrorBoundary everytime the FormSchema is updated
  useEffect(() => {
    errorBoundaryRef.current?.reset();
  }, [props.formSchema, props.formSchema]);

  return (
    <>
      {formStatus === FormStatus.VALIDATOR_ERROR && (
        <EmptyState>
          <EmptyStateIcon icon={ExclamationTriangleIcon} />
          <TextContent>
            <Text component={"h2"}>{i18n.form.status.validatorError.title}</Text>
          </TextContent>
          <EmptyStateBody>
            <TextContent>
              <Text>
                <I18nWrapped
                  components={{
                    jira: (
                      <a href={KOGITO_JIRA_LINK} target={"_blank"}>
                        {KOGITO_JIRA_LINK}
                      </a>
                    ),
                  }}
                >
                  {i18n.form.status.validatorError.message}
                </I18nWrapped>
              </Text>
            </TextContent>
          </EmptyStateBody>
        </EmptyState>
      )}
      {formStatus === FormStatus.AUTO_GENERATION_ERROR && formErrorMessage}
      {formStatus === FormStatus.EMPTY && (
        <div>
          <EmptyState>
            <EmptyStateIcon icon={CubesIcon} />
            <TextContent>
              <Text component={"h2"}>{i18n.form.status.emptyForm.title}</Text>
            </TextContent>
            <EmptyStateBody>
              <TextContent>
                <Text component={TextVariants.p}>{i18n.form.status.emptyForm.explanation}</Text>
              </TextContent>
            </EmptyStateBody>
          </EmptyState>
        </div>
      )}
      {formStatus === FormStatus.WITHOUT_ERROR && (
        <div data-testid={"dmn-form"}>
          {jsonSchemaBridge && (
            <ErrorBoundary ref={errorBoundaryRef} setHasError={props.setFormError} error={formErrorMessage}>
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
                errorsField={props.errorsField}
                submitField={props.submitField}
                validate={"onChange"}
              />
            </ErrorBoundary>
          )}
        </div>
      )}
    </>
  );
}
