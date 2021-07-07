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

import { ErrorBoundary } from "@kogito-tooling/form/dist/common";
import { DecisionResult, DmnForm, DmnFormResult } from "@kogito-tooling/form/dist/dmn";
import { I18nWrapped } from "@kogito-tooling/i18n/dist/react-components";
import { Alert, AlertActionCloseButton } from "@patternfly/react-core/dist/js/components/Alert";
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { ExclamationTriangleIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-triangle-icon";
import { diff } from "deep-object-diff";
import * as React from "react";
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { DmnFormToolbar } from "./DmnFormToolbar";
import { FormData } from "./FormData";
import { useDmnFormI18n } from "./i18n";

interface Props {
  formData: FormData;
}

export enum AlertTypes {
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
  const [openAlert, setOpenAlert] = useState(AlertTypes.NONE);
  const [pageError, setPageError] = useState<boolean>(false);
  const errorBoundaryRef = useRef<ErrorBoundary>(null);

  const closeAlert = useCallback(() => setOpenAlert(AlertTypes.NONE), []);

  const onOpenSwaggerUI = useCallback(() => {
    window.open(props.formData.swaggerUIUrl, "_blank");
  }, [props.formData.swaggerUIUrl]);

  const onOpenOnlineEditor = useCallback(() => {
    window.open(props.formData.modelUrl, "_blank");
  }, [props.formData.modelUrl]);

  const onSubmit = useCallback(async () => {
    try {
      const response = await fetch(`${props.formData.formUrl}/${props.formData.modelName}/dmnresult`, {
        method: "POST",
        headers: {
          Accept: "application/json",
          "Content-Type": "application/json",
        },
        body: JSON.stringify(formInputs),
      });

      const formOutputs = (await response.json()).decisionResults;
      setFormOutputs((previousOutputs: DecisionResult[]) => {
        const differences = formOutputs
          .map((decisionResult: DecisionResult, index: number) =>
            diff(previousOutputs?.[index] ?? {}, decisionResult ?? {})
          )
          .map((difference: any) => {
            delete difference.messages;
            return difference;
          });
        if (differences?.length !== 0) {
          setFormOutputDiffs(differences);
        }
        return formOutputs;
      });
    } catch (e) {
      setFormOutputs(undefined);
      setOpenAlert(AlertTypes.ERROR);
      console.error(e);
    }
  }, [formInputs, props.formData.formUrl, props.formData.modelName]);

  const pageErrorMessage = useMemo(
    () => (
      <div>
        <EmptyState>
          <EmptyStateIcon icon={ExclamationTriangleIcon} />
          <TextContent>
            <Text component={"h2"}>{i18n.page.error.title}</Text>
          </TextContent>
          <EmptyStateBody>
            <TextContent>{i18n.page.error.explanation}</TextContent>
            <br />
            <TextContent>
              {" "}
              <I18nWrapped
                components={{
                  jira: (
                    <a href={KOGITO_JIRA_LINK} target={"_blank"}>
                      {KOGITO_JIRA_LINK}
                    </a>
                  ),
                }}
              >
                {i18n.page.error.message}
              </I18nWrapped>
            </TextContent>
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
    <Page
      header={
        <DmnFormToolbar
          filename={props.formData.filename}
          onOpenOnlineEditor={onOpenOnlineEditor}
          onOpenSwaggerUI={onOpenSwaggerUI}
        />
      }
    >
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
                    <DmnForm
                      formData={formInputs}
                      setFormData={setFormInputs}
                      formError={formError}
                      setFormError={setFormError}
                      formSchema={props.formData.schema}
                      id={"form"}
                      showInlineError={true}
                      notificationsPanel={false}
                      onSubmit={onSubmit}
                      autosave={true}
                      autosaveDelay={AUTO_SAVE_DELAY}
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
                    <DmnFormResult
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
