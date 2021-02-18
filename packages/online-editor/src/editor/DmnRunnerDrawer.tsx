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

import React, { useEffect, useMemo, useRef, useState } from "react";
import { useCallback } from "react";
import { DmnRunner } from "../common/DmnRunner";
import { AutoForm } from "uniforms-patternfly";
import JSONSchemaBridge from "uniforms-bridge-json-schema";
import {
  Alert,
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
  Button,
  ButtonVariant
} from "@patternfly/react-core";
import { CubesIcon } from "@patternfly/react-icons";

enum DmnRunnerStatusResponse {
  SUCCESS,
  WARNING,
  NONE
}

interface Props {
  editorContent: (() => Promise<string>) | undefined;
  jsonSchemaBridge: JSONSchemaBridge | undefined;
  onStopRunDmn: (e: React.MouseEvent<HTMLButtonElement>) => void;
}

const PF_BREAKPOINT_XL = 1200;

export function DmnRunnerDrawer(props: Props) {
  const [dmnRunnerResponse, setDmnRunnerResponse] = useState();
  const [dmnRunnerResponseStatus, setDmnRunnerResponseStatus] = useState(DmnRunnerStatusResponse.NONE);
  const autoFormRef = useRef<HTMLFormElement>();

  const alertMessage = useMemo(() => {
    switch (dmnRunnerResponseStatus) {
      case DmnRunnerStatusResponse.SUCCESS:
        return <Alert title={"Your request has been successfully processed"} variant={"success"} isInline={true} />;
      case DmnRunnerStatusResponse.WARNING:
        return <Alert title={"Your request couldn't be processed"} variant={"warning"} isInline={true} />;
      case DmnRunnerStatusResponse.NONE:
        return;
    }
  }, [dmnRunnerResponseStatus]);

  const onSubmit = useCallback(
    ({ context }) => {
      if (props.editorContent) {
        props.editorContent().then((model: string) => {
          DmnRunner.validateForm({ context, model })
            .then(res => res.json())
            .then(json => {
              setDmnRunnerResponse(json);
              setDmnRunnerResponseStatus(DmnRunnerStatusResponse.SUCCESS);
            })
            .catch(() => setDmnRunnerResponseStatus(DmnRunnerStatusResponse.WARNING));
        });
      }
    },
    [props.editorContent]
  );

  const [buttonPosition, setButtonPosition] = useState<"input" | "output">(() => {
    const width = window.innerWidth;

    if (width <= PF_BREAKPOINT_XL) {
      return "input";
    }
    return "output";
  });

  const handleResize = useCallback(() => {
    const width = window.innerWidth;

    if (width <= PF_BREAKPOINT_XL) {
      setButtonPosition("input");
    } else {
      setButtonPosition("output");
    }
  }, []);

  useEffect(() => {
    window.addEventListener("resize", handleResize);
  }, []);

  return (
    <>
      <div className={"kogito--editor__dmn-runner-drawer-div-page"}>
        <div className={"kogito--editor__dmn-runner-drawer-div-page-div"}>
          <Page className={"kogito--editor__dmn-runner-drawer-page"}>
            <PageSection
              style={{
                display: "flex",
                justifyContent: "space-between",
                alignItems: "center",
                paddingBottom: 0
              }}
              // height={buttonPosition !== "input" ? "60px" : undefined}
            >
              <TextContent>
                <Text component={"h2"}>Inputs</Text>
              </TextContent>
              {buttonPosition === "input" && (
                <Button variant={ButtonVariant.secondary} onClick={e => props.onStopRunDmn(e)}>
                  Stop Running
                </Button>
              )}
            </PageSection>
            <PageSection>
              {props.jsonSchemaBridge ? (
                <AutoForm
                  style={{ maxWidth: "350px" }}
                  id={"form"}
                  ref={autoFormRef}
                  showInlineError={true}
                  autosave={true}
                  autosaveDelay={500}
                  schema={props.jsonSchemaBridge}
                  onSubmit={onSubmit}
                  errorsField={() => <></>}
                  submitField={() => <></>}
                  placeholder={true}
                />
              ) : (
                <div>
                  <EmptyState>
                    <EmptyStateIcon icon={CubesIcon} />
                    <TextContent>
                      <Text component={"h2"}>Create a Model!</Text>
                    </TextContent>
                  </EmptyState>
                </div>
              )}
            </PageSection>
          </Page>
        </div>
        <div className={"kogito--editor__dmn-runner-drawer-div-page-div"}>
          <Page className={"kogito--editor__dmn-runner-drawer-page"}>
            <PageSection
              style={{ display: "flex", justifyContent: "space-between", alignItems: "center", paddingBottom: 0 }}
            >
              <TextContent>
                <Text component={"h2"}>Outputs</Text>
              </TextContent>
              {buttonPosition === "output" && (
                <Button variant={ButtonVariant.secondary} onClick={e => props.onStopRunDmn(e)}>
                  Stop Running
                </Button>
              )}
            </PageSection>
            <PageSection style={{ width: "350px", maxWidth: "350px", paddingLeft: 0 }}>
              {dmnRunnerResponse ? (
                <JitResponse responseObject={dmnRunnerResponse!} depth={0} />
              ) : (
                <EmptyState>
                  <EmptyStateIcon icon={CubesIcon} />
                  <TextContent>
                    <Text component={"h2"}>Without response yet</Text>
                  </TextContent>
                </EmptyState>
              )}
            </PageSection>
          </Page>
        </div>
      </div>
    </>
  );
}

interface RecursiveJitResponseProps {
  responseObject: object;
  depth: number;
}

function JitResponse(props: RecursiveJitResponseProps) {
  return (
    <div>
      {[...Object.entries(props.responseObject)].reverse().map(([key, value]: any[], index) => (
        <div key={`${key}-${index}-jit-response`}>
          {typeof value === "object" && value !== null ? (
            <Card isFlat={true} style={{ border: 0, background: "transparent" }}>
              <CardTitle>{key}</CardTitle>
              <CardBody isFilled={true} style={props.depth > 0 ? { paddingBottom: 0 } : {}}>
                <JitResponse responseObject={value} depth={props.depth + 1} />
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
