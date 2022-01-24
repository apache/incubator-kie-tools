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
import { AutoForm } from "uniforms-patternfly/dist/es6";
import { ErrorBoundary } from "../common/ErrorBoundary";
import { dataPathToFormFieldPath } from "./uniforms/utils";
import { DmnFormJsonSchemaBridge } from "./uniforms";
import { DmnValidator } from "./DmnValidator";
import { dmnFormI18n } from "./i18n";
import { diff } from "deep-object-diff";
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { ExclamationTriangleIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-triangle-icon";
import { I18nWrapped } from "@kie-tooling-core/i18n/dist/react-components";
import { ExclamationIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-icon";
import { CubesIcon } from "@patternfly/react-icons/dist/js/icons/cubes-icon";
import cloneDeep from "lodash/cloneDeep";

const KOGITO_JIRA_LINK = "https://issues.jboss.org/projects/KOGITO";

enum FormStatus {
  WITHOUT_ERROR,
  VALIDATOR_ERROR,
  AUTO_GENERATION_ERROR,
  EMPTY,
}

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

interface CommonProps {
  name?: string;
  formData: object;
  setFormData: React.Dispatch<object>;
  formError: boolean;
  setFormError: React.Dispatch<any>;
  formSchema?: any;
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
  const [formModel, setFormModel] = useState<any>();
  const [formStatus, setFormStatus] = useState<FormStatus>(FormStatus.EMPTY);
  const contextPath = useMemo(() => new Map<string, string[]>(), []);

  const setCustomPlaceholders = useCallback(
    (value: DmnDeepProperty) => {
      if (value?.format === "days and time duration") {
        value!.placeholder = i18n.form.preProcessing.daysAndTimePlaceholder;
      }
      if (value?.format === "years and months duration") {
        value!.placeholder = i18n.form.preProcessing.yearsAndMonthsPlaceholder;
      }
      if (value?.format === "time") {
        value!.placeholder = "hh:mm:ss";
      }
    },
    [i18n.form.preProcessing.daysAndTimePlaceholder, i18n.form.preProcessing.yearsAndMonthsPlaceholder]
  );

  const formDeepPreprocessing = useCallback(
    (form: DmnFormData, value: DmnDeepProperty, title = [""]) => {
      if (Object.hasOwnProperty.call(value, "$ref")) {
        const property = value.$ref!.split("/").pop()! as keyof DmnFormDefinitions;
        if (form.definitions[property] && Object.hasOwnProperty.call(form.definitions[property], "properties")) {
          Object.entries(form.definitions[property]!.properties).forEach(
            ([key, deepValue]: [string, DmnDeepProperty]) => {
              formDeepPreprocessing(form, deepValue, [...title, key]);
            }
          );
        } else if (form.definitions[property] && form.definitions[property]?.type === "array") {
          if (Object.hasOwnProperty.call(form.definitions[property]?.items, "properties")) {
            Object.entries(form.definitions[property]?.items.properties).forEach(
              ([key, deepValue]: [string, DmnDeepProperty]) => {
                formDeepPreprocessing(form, deepValue, [...title, key]);
              }
            );
          } else {
            formDeepPreprocessing(form, form.definitions[property]!.items as DmnDeepProperty, [...title]);
          }
        } else if (!Object.hasOwnProperty.call(form.definitions[property], "type")) {
          form.definitions[property]!.type = "string";
        } else if (form?.definitions?.[property]?.["x-dmn-type"] === "FEEL:context") {
          form.definitions[property]!.placeholder = `{ "x": <value> }`;
          contextPath.set(title.join(""), title);
        } else if (Object.hasOwnProperty.call(form.definitions[property], "enum")) {
          form.definitions[property]!.placeholder = i18n.form.preProcessing.selectPlaceholder;
        } else if (Object.hasOwnProperty.call(form.definitions[property], "format")) {
          setCustomPlaceholders(form.definitions[property]!);
        }
        return;
      }
      if (!Object.hasOwnProperty.call(value, "type")) {
        value.type = "string";
      }
      if (value?.["x-dmn-type"] === "FEEL:context") {
        value!.placeholder = `{ "x": <value> }`;
        contextPath.set(title.join(""), title);
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
            formDeepPreprocessing(form, value, [key]);
          }
        );
      }
    },
    [formDeepPreprocessing]
  );

  const defaultFormValues = useCallback((jsonSchemaBridge: any) => {
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
  }, []);

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

  const removeDeletedPropertiesAndAddDefaultValues = useCallback(
    (model: object, bridge: DmnFormJsonSchemaBridge, previousBridge?: DmnFormJsonSchemaBridge) => {
      const propertiesDifference = diff(
        previousBridge?.schema?.definitions?.InputSet?.properties ?? {},
        bridge?.schema?.definitions?.InputSet?.properties ?? {}
      );

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
        { ...defaultFormValues(bridge), ...model }
      );
    },
    [defaultFormValues]
  );

  useEffect(() => {
    props.setFormError((previousFormError: boolean) => {
      if (!previousFormError && formModel && Object.keys(formModel).length > 0) {
        const newFormData = cloneDeep(formModel);
        contextPath.forEach((path) => {
          const pathCopy = [...path];
          handleContextPath(newFormData, pathCopy, "parse");
        });
        props.setFormData(newFormData);
      }
      return false;
    });
  }, [contextPath, formModel, handleContextPath]);

  useEffect(() => {
    const form: DmnFormData = cloneDeep(props.formSchema ?? {});
    if (Object.keys(form).length > 0) {
      formPreprocessing(form);
    }
    try {
      const bridge = validator.getBridge(form);
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
      console.error(err);
      setFormStatus(FormStatus.VALIDATOR_ERROR);
    }
  }, [
    formModel,
    props.formSchema,
    formPreprocessing,
    validator,
    handleContextPath,
    removeDeletedPropertiesAndAddDefaultValues,
    contextPath,
  ]);

  // on first render, if model is undefined adds a value on it.
  useEffect(() => {
    const newFormModel = cloneDeep(props.formData);
    contextPath.forEach((path) => {
      const pathCopy = [...path];
      handleContextPath(newFormModel, pathCopy, "stringify");
    });
    setFormModel(newFormModel);
  }, [props.name]);

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
    [props.onValidate]
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
    } else if (jsonSchemaBridge) {
      setFormStatus(FormStatus.WITHOUT_ERROR);
      errorBoundaryRef.current?.reset();
    }
  }, [props.formError, props.formSchema, jsonSchemaBridge, formModel]);

  // Resets the ErrorBoundary everytime the FormSchema is updated
  useEffect(() => {
    errorBoundaryRef.current?.reset();
  }, [props.formSchema]);

  return (
    <>
      {formStatus === FormStatus.VALIDATOR_ERROR && (
        <div>
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
        </div>
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
          <ErrorBoundary ref={errorBoundaryRef} setHasError={props.setFormError} error={formErrorMessage}>
            <AutoForm
              id={props.id}
              model={formModel}
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
        </div>
      )}
    </>
  );
}
