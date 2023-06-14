/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { I18nWrapped } from "@kie-tools-core/i18n/dist/react-components";
import { FormDmn, FormDmnOutputs, extractDifferences } from "@kie-tools/form-dmn";
import {
  DecisionResult,
  DmnInputFieldProperties,
  ExtendedServicesDmnJsonSchema,
} from "@kie-tools/extended-services-api";
import { Alert, AlertActionCloseButton } from "@patternfly/react-core/dist/js/components/Alert";
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { ExclamationTriangleIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-triangle-icon";
import * as React from "react";
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { FormData } from "./DmnDevDeploymentFormWebAppDataApi";
import { fetchDmnResult } from "./DmnDevDeploymentRuntimeApi";
import { DmnFormToolbar } from "./DmnFormToolbar";
import { ErrorBoundary } from "./ErrorBoundary";
import { useDmnFormI18n } from "./i18n";
import {
  get as getObjectValueByPath,
  set as setObjectValueByPath,
  unset as unsetObjectValueByPath,
  cloneDeep,
} from "lodash";
import { resolveRefs, pathFromPtr } from "json-refs";
import { useCancelableEffect } from "@kie-tools-core/react-hooks/dist/useCancelableEffect";
import { RECURSION_KEYWORD, RECURSION_REF_KEYWORD, X_DMN_TYPE_KEYWORD } from "@kie-tools/dmn-runner/dist/constants";
import { Holder } from "@kie-tools-core/react-hooks/dist/Holder";

interface Props {
  formData: FormData;
}

enum AlertTypes {
  NONE = "NONE",
  ERROR = "ERROR",
}

const AUTO_SAVE_DELAY = 500;
const KOGITO_JIRA_LINK = "https://issues.jboss.org/projects/KOGITO";

export function DmnFormPage(props: Props) {
  const { i18n, locale } = useDmnFormI18n();
  const [formInputs, setFormInputs] = useState({});
  const [formOutputs, setFormOutputs] = useState<DecisionResult[]>();
  const [formOutputDiffs, setFormOutputDiffs] = useState<object[]>();
  const [formError, setFormError] = useState(false);
  const [jsonSchema, setJsonSchema] = useState<ExtendedServicesDmnJsonSchema | undefined>(undefined);
  const [openAlert, setOpenAlert] = useState(AlertTypes.NONE);
  const [pageError, setPageError] = useState<boolean>(false);
  const errorBoundaryRef = useRef<ErrorBoundary>(null);

  const getFieldDefaultValue = useCallback((dmnField: DmnInputFieldProperties):
    | string
    | boolean
    | []
    | object
    | undefined => {
    if (dmnField?.type === "string" && dmnField?.format === undefined) {
      return undefined;
    }
    if (dmnField?.type === "number") {
      return undefined;
    }
    if (dmnField?.type === "boolean") {
      return false;
    }
    if (dmnField?.type === "array") {
      return [];
    }
    if (dmnField?.type === "object") {
      return {};
    }
    return undefined;
  }, []);

  const getDefaultValues = useCallback(
    (jsonSchema?: ExtendedServicesDmnJsonSchema) => {
      if (!jsonSchema) {
        return {};
      }

      return Object.entries(getObjectValueByPath(jsonSchema, "definitions.InputSet.properties") ?? {})?.reduce(
        (acc, [key, field]: [string, Record<string, string>]) => {
          acc[key] = getFieldDefaultValue(field);
          return acc;
        },
        {} as Record<string, any>
      );
    },
    [getFieldDefaultValue]
  );

  const formInputsWithDefaultValues = useMemo(
    () => ({
      ...getDefaultValues(jsonSchema),
      ...formInputs,
    }),
    [formInputs, getDefaultValues, jsonSchema]
  );

  const resolveReferencesAndCheckForRecursion = useCallback(
    async (jsonSchema: ExtendedServicesDmnJsonSchema, canceled: Holder<boolean>) => {
      try {
        const jsonSchemaCopy = cloneDeep(jsonSchema);
        const $ref = getObjectValueByPath(jsonSchemaCopy, "$ref");
        unsetObjectValueByPath(jsonSchemaCopy, "$ref");

        const { refs, resolved } = await resolveRefs(jsonSchemaCopy as any);
        if (canceled.get()) {
          return;
        }

        let reResolve = false;
        Object.entries(refs).forEach(([ptr, properties]) => {
          if (properties?.circular) {
            const path = pathFromPtr(ptr);
            const recursiveRefPath = pathFromPtr(properties.def.$ref);
            setObjectValueByPath(resolved, path.join("."), {
              [`${RECURSION_KEYWORD}`]: true,
              [`${RECURSION_REF_KEYWORD}`]: properties.def.$ref,
              [`${X_DMN_TYPE_KEYWORD}`]: recursiveRefPath[recursiveRefPath.length - 1],
            });
            reResolve = true;
          }
        });

        if (reResolve) {
          const { resolved: reResolved } = await resolveRefs(resolved);
          if (canceled.get()) {
            return;
          }

          if ($ref) {
            setObjectValueByPath(reResolved, "$ref", $ref);
          }
          return reResolved;
        }

        if ($ref) {
          setObjectValueByPath(resolved, "$ref", $ref);
        }
        return resolved;
      } catch (err) {
        console.log(err);
        return;
      }
    },
    []
  );

  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        resolveReferencesAndCheckForRecursion(props.formData.schema, canceled).then((resolvedJsonSchema) => {
          setJsonSchema(resolvedJsonSchema);
        });
      },
      [props.formData.schema, resolveReferencesAndCheckForRecursion]
    )
  );

  const closeAlert = useCallback(() => setOpenAlert(AlertTypes.NONE), []);

  const onSubmit = useCallback(async () => {
    try {
      const formOutputs = await fetchDmnResult({
        modelName: props.formData.modelName,
        inputs: formInputsWithDefaultValues,
      });

      setFormOutputs((previousOutputs: DecisionResult[]) => {
        // extractDifferences was refactor to accept an array of inputs;
        const [differences] = extractDifferences([formOutputs], [previousOutputs]);
        if (differences?.length !== 0) {
          setFormOutputDiffs(differences);
        }
        return formOutputs;
      });
    } catch (error) {
      setFormOutputs(undefined);
      setOpenAlert(AlertTypes.ERROR);
      console.error(error);
    }
  }, [formInputsWithDefaultValues, props.formData.modelName]);

  const pageErrorMessage = useMemo(
    () => (
      <div>
        <EmptyState>
          <EmptyStateIcon icon={ExclamationTriangleIcon} />
          <TextContent>
            <Text component={"h2"}>{i18n.page.error.title}</Text>
          </TextContent>
          <EmptyStateBody>
            <TextContent>
              <Text component={TextVariants.p}>{i18n.page.error.explanation}</Text>
            </TextContent>
            <br />
            <TextContent>
              {i18n.page.error.dmnNotSupported}
              <I18nWrapped
                components={{
                  jira: (
                    <a href={KOGITO_JIRA_LINK} target={"_blank"}>
                      {KOGITO_JIRA_LINK}
                    </a>
                  ),
                }}
              >
                {i18n.page.error.referToJira}
              </I18nWrapped>
            </TextContent>
            <br />
            <TextContent>{i18n.page.error.uploadFiles}</TextContent>
          </EmptyStateBody>
        </EmptyState>
      </div>
    ),
    [i18n]
  );

  useEffect(() => {
    errorBoundaryRef.current?.reset();
    setPageError(false);
  }, [props.formData.schema]);

  useEffect(() => {
    onSubmit();
  }, [onSubmit]);

  return (
    <Page data-testid="dmn-form-page" header={<DmnFormToolbar uri={props.formData.uri} />}>
      {openAlert === AlertTypes.ERROR && (
        <div className={"kogito--alert-container"}>
          <Alert
            className={"kogito--alert"}
            variant="danger"
            title={i18n.error.title}
            actionClose={<AlertActionCloseButton onClose={closeAlert} />}
          />
        </div>
      )}
      {pageError ? (
        pageErrorMessage
      ) : (
        <ErrorBoundary error={pageErrorMessage} setHasError={setPageError} ref={errorBoundaryRef}>
          <div className="kogito--dmn-form">
            <div className="kogito--dmn-form__content">
              <Page className={"kogito--dmn-form__content-page"}>
                <PageSection className={"kogito--dmn-form__content-header inputs"}>
                  <TextContent>
                    <Text component={TextVariants.h3}>{i18n.terms.inputs}</Text>
                  </TextContent>
                </PageSection>
                <div className={"kogito--dmn-form__content-body"}>
                  <PageSection className={"kogito--dmn-form__content-body-input"}>
                    <FormDmn
                      formInputs={formInputsWithDefaultValues}
                      setFormInputs={setFormInputs}
                      formError={formError}
                      setFormError={setFormError}
                      formSchema={props.formData.schema}
                      id={"form"}
                      showInlineError={true}
                      notificationsPanel={false}
                      onSubmit={onSubmit}
                      placeholder={true}
                      autoSave={true}
                      autoSaveDelay={AUTO_SAVE_DELAY}
                      submitField={() => <></>}
                      errorsField={() => <></>}
                      locale={locale}
                    />
                  </PageSection>
                </div>
              </Page>
            </div>
            <div className="kogito--dmn-form__content">
              <Page className={"kogito--dmn-form__content-page"}>
                <PageSection className={"kogito--dmn-form__content-header"}>
                  <TextContent>
                    <Text component={TextVariants.h3}>{i18n.terms.outputs}</Text>
                  </TextContent>
                </PageSection>
                <div className={"kogito--dmn-form__content-body"}>
                  <PageSection isFilled={true} className="kogito--dmn-form__content-body-output">
                    <FormDmnOutputs
                      results={formOutputs}
                      differences={formOutputDiffs}
                      locale={locale}
                      notificationsPanel={false}
                    />
                  </PageSection>
                </div>
              </Page>
            </div>
          </div>
        </ErrorBoundary>
      )}
    </Page>
  );
}
