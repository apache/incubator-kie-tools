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
import { DmnRunner } from "../common/DmnRunner";
import { AutoForm } from "uniforms-patternfly";
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
  DrawerCloseButton
} from "@patternfly/react-core";
import { CubesIcon, InfoCircleIcon } from "@patternfly/react-icons";
import { diff } from "deep-object-diff";
import { flatten } from "../common/utils";

enum ButtonPosition {
  INPUT,
  OUTPUT
}

interface Props {
  editorContent: (() => Promise<string>) | undefined;
  jsonSchemaBridge: JSONSchemaBridge | undefined;
  onStopRunDmn: (e: React.MouseEvent<HTMLButtonElement>) => void;
  formContext: any;
  setFormContext: React.Dispatch<any>;
  flexDirection: "column" | "row";
}

const PF_BREAKPOINT_XL = 1200;

export function DmnRunnerDrawer(props: Props) {
  const [dmnRunnerResponse, setDmnRunnerResponse] = useState();
  const [isAutoSubmit, setIsAutoSubmit] = useState(true);
  const autoFormRef = useRef<HTMLFormElement>();
  const [dmnRunnerResponseDiffs, setDmnRunnerResponseDiffs] = useState<string[]>();
  const [buttonPosition, setButtonPosition] = useState<ButtonPosition>(() =>
    window.innerWidth <= PF_BREAKPOINT_XL ? ButtonPosition.INPUT : ButtonPosition.OUTPUT
  );
  const [dmnRunnerContentStyles, setDmnRunnerContentStyles] = useState<{ width: string; height: string }>(() =>
    props.flexDirection === "row" ? { width: "50%", height: "100%" } : { width: "100%", height: "50%" }
  );

  const onSubmit = useCallback(
    async ({ context }) => {
      props.setFormContext(context);
      if (props.editorContent) {
        try {
          const model = await props.editorContent();
          const dmnRunnerRes = await DmnRunner.sendForm({ context, model });
          const dmnRunnerJson = await dmnRunnerRes.json();
          if (
            Object.hasOwnProperty.call(dmnRunnerJson, "details") &&
            Object.hasOwnProperty.call(dmnRunnerJson, "stack")
          ) {
            // DMN Runner Error
            return;
          }
          const differences = diff(dmnRunnerResponse ?? {}, dmnRunnerJson);
          if (Object.keys(differences).length !== 0) {
            setDmnRunnerResponseDiffs([...Object.keys(flatten(diff(dmnRunnerResponse ?? {}, dmnRunnerJson)))]);
          }
          setDmnRunnerResponse(dmnRunnerJson);
        } catch (err) {
          setDmnRunnerResponse(undefined);
        }
      }
    },
    [props, dmnRunnerResponse]
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
    autoFormRef.current?.change("context", props.formContext);
  }, []);

  const tweakAutoSubmit = useCallback((value) => {
    autoFormRef.current?.submit();
    setIsAutoSubmit(value)
  }, [])

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
                    placeholder={true}
                  />
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
                {dmnRunnerResponse ? (
                  <DmnRunnerResponse diffs={dmnRunnerResponseDiffs} responseObject={dmnRunnerResponse!} depth={0} />
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

interface DmnRunnerResponseProps {
  responseObject: object;
  depth: number;
  diffs?: string[];
}

function DmnRunnerResponse(props: DmnRunnerResponseProps) {
  return (
    <div>
      {[...Object.entries(props.responseObject)].reverse().map(([key, value]: any[], index) => (
        <div key={`${key}-${index}-dmn-runner-response`}>
          {typeof value === "object" && value !== null ? (
            <Card isFlat={true} className={"kogito--editor__dmn-runner-drawer-content-body-output-card"}>
              <CardTitle>{key}</CardTitle>
              <CardBody
                isFilled={true}
                className={props.depth > 0 ? "kogito--editor__dmn-runner-drawer-content-body-output-card-body" : ""}
              >
                <DmnRunnerResponse diffs={props.diffs} responseObject={value} depth={props.depth + 1} />
              </CardBody>
            </Card>
          ) : (
            <>
              {props.depth === 0 ? (
                <Card isFlat={true} className={"kogito--editor__dmn-runner-drawer-content-body-output-card"}>
                  <CardBody
                    isFilled={true}
                    className={"kogito--editor__dmn-runner-drawer-content-body-output-card-body-leaf"}
                  >
                    <ResultCardLeaf diffs={props.diffs} label={key} value={value} />
                  </CardBody>
                </Card>
              ) : (
                <ResultCardLeaf diffs={props.diffs} label={key} value={value} />
              )}
            </>
          )}
        </div>
      ))}
    </div>
  );
}

interface ResultCardLeafProps {
  label: string;
  value: string;
  diffs?: string[];
}

function ResultCardLeaf(props: ResultCardLeafProps) {
  const [ariaLabel, setAriaLabel] = useState<string>(props.label);
  const [className, setClassName] = useState<string>("kogito--editor__dmn-runner-drawer-output-leaf");

  useEffect(() => {
    const hasKey = props.diffs?.find(key => key === props.label);
    if (hasKey) {
      setAriaLabel(`${props.label} field updated`);
      setClassName("kogito--editor__dmn-runner-drawer-output-leaf-updated");
    } else {
      setAriaLabel(`${props.label}`);
      setClassName("kogito--editor__dmn-runner-drawer-output-leaf");
    }
  }, [props.diffs, props.label]);

  const onAnimationEnd = useCallback((e: React.AnimationEvent<HTMLDListElement>) => {
    e.preventDefault();
    e.stopPropagation();
    setClassName("kogito--editor__dmn-runner-drawer-output-leaf");
  }, []);

  return (
    <div>
      <DescriptionList
        id={props.label}
        aria-label={ariaLabel}
        className={className}
        onAnimationEnd={onAnimationEnd}
        isHorizontal={true}
      >
        <DescriptionListGroup>
          <DescriptionListTerm>{props.label}</DescriptionListTerm>
          {props.value ? (
            <DescriptionListDescription>{props.value}</DescriptionListDescription>
          ) : (
            <DescriptionListDescription>
              <i>(null)</i>
            </DescriptionListDescription>
          )}
        </DescriptionListGroup>
      </DescriptionList>
    </div>
  );
}
