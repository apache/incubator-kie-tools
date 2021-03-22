/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as React from "react";
import { useEffect, useLayoutEffect, useRef, useState } from "react";
import { useCallback } from "react";
import { DecisionResult, DmnResult, DmnRunner, EvaluationStatus } from "../common/DmnRunner";
import { AutoForm } from "uniforms-patternfly";
import * as metaSchemaDraft04 from "ajv/lib/refs/json-schema-draft-04.json";
import JSONSchemaBridge from "uniforms-bridge-json-schema";
import {
  DescriptionList,
  DescriptionListTerm,
  DescriptionListGroup,
  DescriptionListDescription,
  Page,
  PageSection,
  TextContent,
  Text,
  Card,
  CardTitle,
  CardBody,
  EmptyState,
  EmptyStateIcon,
  EmptyStateBody,
  TextVariants,
  Switch,
  DrawerCloseButton,
  CardFooter
} from "@patternfly/react-core";
import {
  CubesIcon,
  CheckCircleIcon,
  ExclamationCircleIcon,
  ExclamationTriangleIcon,
  InfoCircleIcon
} from "@patternfly/react-icons";
import { diff } from "deep-object-diff";
import { flatten } from "../common/utils";
import { ErrorBoundary } from "../common/ErrorBoundry";
import { Decision } from "@kogito-tooling/pmml-editor-marshaller";

enum ButtonPosition {
  INPUT,
  OUTPUT
}

interface Props {
  getEditorContent: (() => Promise<string>) | undefined;
  jsonSchemaBridge: JSONSchemaBridge | undefined;
  onStopRunDmn: (e: React.MouseEvent<HTMLButtonElement>) => void;
  flexDirection: "column" | "row";
}

const PF_BREAKPOINT_XL = 1200;

export function DmnRunnerDrawer(props: Props) {
  const [dmnRunnerResults, setDmnRunnerResults] = useState<DecisionResult[]>();
  const [isAutoSubmit, setIsAutoSubmit] = useState(true);
  const autoFormRef = useRef<HTMLFormElement>();
  const [dmnRunnerResponseDiffs, setDmnRunnerResponseDiffs] = useState<object[]>();
  const [buttonPosition, setButtonPosition] = useState<ButtonPosition>(() =>
    window.innerWidth <= PF_BREAKPOINT_XL ? ButtonPosition.INPUT : ButtonPosition.OUTPUT
  );
  const [dmnRunnerContentStyles, setDmnRunnerContentStyles] = useState<{ width: string; height: string }>(() =>
    props.flexDirection === "row" ? { width: "50%", height: "100%" } : { width: "100%", height: "50%" }
  );
  const [formContext, setFormContext] = useState();
  const errorBoundaryRef = useRef<ErrorBoundary>(null);

  const onSubmit = useCallback(
    async data => {
      setFormContext(data);
      if (props.getEditorContent) {
        try {
          const content = await props.getEditorContent();
          const result = await DmnRunner.result({ context: data, model: content });
          if (Object.hasOwnProperty.call(result, "details") && Object.hasOwnProperty.call(result, "stack")) {
            // DMN Runner Error
            return;
          }
          const differences = result?.decisionResults?.map((decisionResult, index) =>
            diff(dmnRunnerResults?.[index] ?? {}, decisionResult ?? {})
          );
          if (differences?.length !== 0) {
            setDmnRunnerResponseDiffs(differences);
          }
          setDmnRunnerResults(result?.decisionResults);
        } catch (err) {
          setDmnRunnerResults(undefined);
        }
      }
    },
    [props.getEditorContent, dmnRunnerResults]
  );

  useEffect(() => {
    if (isAutoSubmit) {
      autoFormRef.current?.submit();
    }
  }, [isAutoSubmit]);

  useEffect(() => {
    switch (props.flexDirection) {
      case "row":
        setButtonPosition(ButtonPosition.OUTPUT);
        setDmnRunnerContentStyles({ width: "50%", height: "100%" });
        return;
      case "column":
        setButtonPosition(ButtonPosition.INPUT);
        setDmnRunnerContentStyles({ width: "100%", height: "50%" });

        return;
    }
  }, [props.flexDirection]);

  useLayoutEffect(() => {
    autoFormRef.current?.change("context", formContext);
  }, []);

  const tweakAutoSubmit = useCallback(value => {
    autoFormRef.current?.submit();
    setIsAutoSubmit(value);
  }, []);

  useEffect(() => {
    errorBoundaryRef.current?.reset();
  }, [props.jsonSchemaBridge]);

  return (
    <>
      <div className={"kogito--editor__dmn-runner"} style={{ flexDirection: props.flexDirection }}>
        <div className={"kogito--editor__dmn-runner-content"} style={dmnRunnerContentStyles}>
          <Page className={"kogito--editor__dmn-runner-content-page"}>
            <PageSection className={"kogito--editor__dmn-runner-content-header"}>
              <TextContent>
                <Text component={"h2"}>Inputs</Text>
              </TextContent>
              <Switch label={"Auto-submit"} onChange={tweakAutoSubmit} isChecked={isAutoSubmit} />
              {buttonPosition === ButtonPosition.INPUT && (
                <DrawerCloseButton onClick={(e: any) => props.onStopRunDmn(e)} />
              )}
            </PageSection>

            <div className={"kogito--editor__dmn-runner-drawer-content-body"}>
              <PageSection className={"kogito--editor__dmn-runner-drawer-content-body-input"}>
                {props.jsonSchemaBridge ? (
                  <ErrorBoundary ref={errorBoundaryRef} error={<h1>Something went wrong.</h1>}>
                    <AutoForm
                      id={"form"}
                      ref={autoFormRef}
                      showInlineError={true}
                      autosave={isAutoSubmit}
                      autosaveDelay={500}
                      schema={props.jsonSchemaBridge}
                      onSubmit={onSubmit}
                      errorsField={() => <></>}
                      submitField={isAutoSubmit ? () => <></> : undefined}
                    />
                  </ErrorBoundary>
                ) : (
                  <div>
                    <EmptyState>
                      <EmptyStateIcon icon={CubesIcon} />
                      <TextContent>
                        <Text component={"h2"}>No Form</Text>
                      </TextContent>
                      <EmptyStateBody>
                        <TextContent>
                          <Text component={TextVariants.p}>Associated DMN Model doesn't have a Form to render</Text>
                        </TextContent>
                      </EmptyStateBody>
                    </EmptyState>
                  </div>
                )}
              </PageSection>
            </div>
          </Page>
        </div>
        <div className={"kogito--editor__dmn-runner-content"} style={dmnRunnerContentStyles}>
          <Page className={"kogito--editor__dmn-runner-content-page"}>
            <PageSection className={"kogito--editor__dmn-runner-content-header"}>
              <TextContent>
                <Text component={"h2"}>Outputs</Text>
              </TextContent>
              {buttonPosition === ButtonPosition.OUTPUT && (
                <DrawerCloseButton onClick={(e: any) => props.onStopRunDmn(e)} />
              )}
            </PageSection>

            <div className={"kogito--editor__dmn-runner-drawer-content-body"}>
              <PageSection className={"kogito--editor__dmn-runner-drawer-content-body-output"}>
                {dmnRunnerResults ? (
                  <DmnRunnerResult dmnRunnerResults={dmnRunnerResults!} differences={dmnRunnerResponseDiffs} />
                ) : (
                  <EmptyState>
                    <EmptyStateIcon icon={InfoCircleIcon} />
                    <TextContent>
                      <Text component={"h2"}>No Response</Text>
                    </TextContent>
                    <EmptyStateBody>
                      <TextContent>
                        <Text>Response appears after the Form is filled and valid</Text>
                      </TextContent>
                    </EmptyStateBody>
                  </EmptyState>
                )}
              </PageSection>
            </div>
          </Page>
        </div>
      </div>
    </>
  );
}

type DeepPartial<T> = {
  [P in keyof T]?: DeepPartial<T[P]>;
};

interface DmnRunnerResponseProps {
  dmnRunnerResults: DecisionResult[];
  differences?: Array<DeepPartial<DecisionResult>>;
}

function DmnRunnerResult(props: DmnRunnerResponseProps) {
  useEffect(() => {
    props.differences?.forEach((difference, index) => {
      if (Object.keys(difference).length === 0) {
        return;
      }

      const updatedResult = document.getElementById(`${index}-dmn-runner-result`);
      updatedResult?.classList.add("kogito--editor__dmn-runner-drawer-output-leaf-updated");
    });
  }, [props.differences]);

  const onAnimationEnd = useCallback((e: React.AnimationEvent<HTMLElement>, index) => {
    e.preventDefault();
    e.stopPropagation();

    const updatedResult = document.getElementById(`${index}-dmn-runner-result`);
    updatedResult?.classList.remove("kogito--editor__dmn-runner-drawer-output-leaf-updated");
  }, []);

  const dmnRunnerResultStatus = useCallback((evaluationStatus: EvaluationStatus) => {
    switch (evaluationStatus) {
      case EvaluationStatus.SUCCEEDED:
        return (
          <>
            <div style={{ display: "flex", alignItems: "center" }}>
              <CheckCircleIcon />
              <p style={{ paddingLeft: "5px" }}>Evaluated with success</p>
            </div>
          </>
        );
      case EvaluationStatus.SKIPPED:
        return (
          <>
            <div style={{ display: "flex", alignItems: "center" }}>
              <InfoCircleIcon />
              <p style={{ paddingLeft: "5px" }}>Evaluation skipped</p>
            </div>
          </>
        );
      case EvaluationStatus.FAILED:
        return (
          <>
            <div style={{ display: "flex", alignItems: "center" }}>
              <ExclamationCircleIcon />
              <p style={{ paddingLeft: "5px" }}>Evaluation failed</p>
            </div>
          </>
        );
    }
  }, []);

  return (
    <div>
      {props.dmnRunnerResults.map((dmnRunnerResult, index) => (
        <div key={`${index}-dmn-runner-result`} style={{ padding: "10px" }}>
          <Card
            id={`${index}-dmn-runner-result`}
            isFlat={true}
            className={"kogito--editor__dmn-runner-drawer-content-body-output-card"}
            onAnimationEnd={e => onAnimationEnd(e, index)}
          >
            <CardTitle>{dmnRunnerResult.decisionName}</CardTitle>
            <CardBody isFilled={true}>
              {typeof dmnRunnerResult.result === "object" && dmnRunnerResult.result !== null ? (
                <DescriptionList>
                  {Object.entries(dmnRunnerResult.result).map(([key, value]) => (
                    <DescriptionListGroup>
                      <DescriptionListTerm>{key}</DescriptionListTerm>
                      <DescriptionListDescription>{value}</DescriptionListDescription>
                    </DescriptionListGroup>
                  ))}
                </DescriptionList>
              ) : dmnRunnerResult.result === null ? (
                <i>(null)</i>
              ) : (
                dmnRunnerResult.result
              )}
            </CardBody>
            <CardFooter>{dmnRunnerResultStatus(dmnRunnerResult.evaluationStatus)}</CardFooter>
          </Card>
        </div>
      ))}
    </div>
  );
}
