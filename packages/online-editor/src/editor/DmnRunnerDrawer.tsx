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

import React, { useEffect, useRef, useState } from "react";
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
}

const PF_BREAKPOINT_XL = 1200;

export function DmnRunnerDrawer(props: Props) {
  const [dmnRunnerResponse, setDmnRunnerResponse] = useState();
  const [isAutoSubmit, setIsAutoSubmit] = useState(true);
  const autoFormRef = useRef<HTMLFormElement>();

  const onSubmit = useCallback(
    ({ context }) => {
      props.setFormContext(context);
      if (props.editorContent) {
        props.editorContent().then((model: string) => {
          DmnRunner.sendForm({ context, model })
            .then(res => res.json())
            .then(json => {
              console.log(json)
              setDmnRunnerResponse(json);
            })
            .catch(() => setDmnRunnerResponse(undefined));
        });
      }
    },
    [props]
  );

  const [buttonPosition, setButtonPosition] = useState<ButtonPosition>(() => {
    const width = window.innerWidth;

    if (width <= PF_BREAKPOINT_XL) {
      return ButtonPosition.INPUT;
    }
    return ButtonPosition.OUTPUT;
  });

  const handleResize = useCallback(() => {
    const width = window.innerWidth;

    if (width <= PF_BREAKPOINT_XL) {
      setButtonPosition(ButtonPosition.INPUT);
    } else {
      setButtonPosition(ButtonPosition.OUTPUT);
    }
  }, []);

  useEffect(() => {
    if (isAutoSubmit) {
      autoFormRef.current?.submit();
    }
  }, [isAutoSubmit]);

  useEffect(() => {
    window.addEventListener("resize", handleResize);

    autoFormRef.current?.change("context", props.formContext);
  }, []);

  return (
    <>
      <div className={"kogito--editor__dmn-runner-drawer-div-page"}>
        <div className={"kogito--editor__dmn-runner-drawer-div-page-div"}>
          <Page className={"kogito--editor__dmn-runner-drawer-page"}>
            <PageSection className={"kogito--editor__dmn-runner-drawer-page-section"} style={{ height: "60px" }}>
              <TextContent>
                <Text component={"h2"}>Inputs</Text>
              </TextContent>
              <Switch label={"Auto-submit"} onChange={setIsAutoSubmit} isChecked={isAutoSubmit} />
              {buttonPosition === ButtonPosition.INPUT && (
                <DrawerCloseButton onClick={(e: any) => props.onStopRunDmn(e)} />
              )}
            </PageSection>

            <div className={"kogito--editor__dmn-runner-drawer-page-section-div"}>
              <PageSection style={{ paddingTop: 0 }}>
                {props.jsonSchemaBridge ? (
                  <AutoForm
                    style={{ maxWidth: "350px" }}
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
        <div className={"kogito--editor__dmn-runner-drawer-div-page-div"}>
          <Page className={"kogito--editor__dmn-runner-drawer-page"}>
            <PageSection style={{ height: "60px" }} className={"kogito--editor__dmn-runner-drawer-page-section"}>
              <TextContent>
                <Text component={"h2"}>Outputs</Text>
              </TextContent>
              {buttonPosition === ButtonPosition.OUTPUT && (
                <DrawerCloseButton onClick={(e: any) => props.onStopRunDmn(e)} />
              )}
            </PageSection>

            <div className={"kogito--editor__dmn-runner-drawer-page-section-div"}>
              <PageSection style={{ paddingLeft: 0, paddingTop: 0 }}>
                {dmnRunnerResponse ? (
                  <DmnRunnerResponse responseObject={dmnRunnerResponse!} depth={0} />
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

interface RecursiveDmnRunnerResponse {
  responseObject: object;
  depth: number;
}

function DmnRunnerResponse(props: RecursiveDmnRunnerResponse) {
  return (
    <div>
      {[...Object.entries(props.responseObject)].reverse().map(([key, value]: any[], index) => (
        <div key={`${key}-${index}-dmn-runner-response`}>
          {typeof value === "object" && value !== null ? (
            <Card isFlat={true} style={{ border: 0, background: "transparent" }}>
              <CardTitle>{key}</CardTitle>
              <CardBody isFilled={true} style={props.depth > 0 ? { paddingBottom: 0 } : {}}>
                <DmnRunnerResponse responseObject={value} depth={props.depth + 1} />
              </CardBody>
            </Card>
          ) : (
            <>
              {props.depth === 0 ? (
                <Card isFlat={true} style={{ border: 0, background: "transparent" }}>
                  <CardBody isFilled={true} style={{ paddingBottom: 0, paddingTop: 0 }}>
                    <ResultCardLeaf label={key} value={value} />
                  </CardBody>
                </Card>
              ) : (
                <ResultCardLeaf label={key} value={value} />
              )}
            </>
          )}
        </div>
      ))}
    </div>
  );
}

function ResultCardLeaf(props: { label: string; value: string }) {
  return (
    <DescriptionList isHorizontal={true}>
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
  );
}
