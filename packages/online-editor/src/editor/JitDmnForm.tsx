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

import React, { useEffect, useMemo, useState } from "react";
import { useCallback } from "react";
import { JitDmn } from "../common/JitDmn";
import { AutoForm } from "uniforms-patternfly";
import JSONSchemaBridge from "uniforms-bridge-json-schema";
import {
  Alert,
  DescriptionList,
  DescriptionListTerm,
  DescriptionListGroup,
  DescriptionListDescription,
  Title
} from "@patternfly/react-core";

enum JitResponseStatus {
  SUCCESS,
  WARNING,
  NONE
}

interface Props {
  editorContent: (() => Promise<string>) | undefined;
  jsonSchemaBridge: JSONSchemaBridge | undefined;
}

export function JitDmnForm(props: Props) {
  const [jitResponse, setJitResponse] = useState();
  const [jitResponseStatus, setJitResponseStatus] = useState(JitResponseStatus.NONE);

  const alertMessage = useMemo(() => {
    switch (jitResponseStatus) {
      case JitResponseStatus.SUCCESS:
        return <Alert title={"Your request has been successfully processed"} variant={"success"} isInline={true} />;
      case JitResponseStatus.WARNING:
        return <Alert title={"Your request couldn't be processed"} variant={"warning"} isInline={true} />;
      case JitResponseStatus.NONE:
        return;
    }
  }, [jitResponseStatus]);

  const onSubmit = useCallback(
    ({ context }: any) => {
      if (props.editorContent) {
        props.editorContent().then((model: string) => {
          JitDmn.validateForm({ context, model })
            .then(res => res.json())
            .then(json => {
              setJitResponse(json);
              setJitResponseStatus(JitResponseStatus.SUCCESS);
            })
            .catch(() => setJitResponseStatus(JitResponseStatus.WARNING));
        });
      }
    },
    [props.editorContent]
  );

  useEffect(() => {
    console.log(jitResponse);
  }, [jitResponse]);

  return (
    <div>
      {props.jsonSchemaBridge && <AutoForm schema={props.jsonSchemaBridge} onSubmit={onSubmit} />}
      {alertMessage}
      {jitResponse && (
        <DescriptionList isHorizontal={true}>
          <JitResponse responseObject={jitResponse!} withoutPadding={false} />
        </DescriptionList>
      )}
    </div>
  );
}

interface RecursiveJitResponseProps {
  responseObject: object;
  withoutPadding: boolean;
}

function JitResponse(props: RecursiveJitResponseProps) {
  return (
    <div style={{ padding: "10px" }}>
      {[...Object.entries(props.responseObject)].map(([key, value]: any[]) => (
        <>
          {typeof value === "object" && value !== null ? (
            <>
              <Title headingLevel={"h5"}>{key}</Title>
              <JitResponse responseObject={value} withoutPadding={true} />
            </>
          ) : (
            <div style={props.withoutPadding ? {} : { padding: "10px" }}>
              <DescriptionListGroup>
                <DescriptionListTerm>{key}</DescriptionListTerm>
                {value ? (
                  <DescriptionListDescription>{value}</DescriptionListDescription>
                ) : (
                  <DescriptionListDescription>Not provided</DescriptionListDescription>
                )}
              </DescriptionListGroup>
            </div>
          )}
        </>
      ))}
    </div>
  );
}
