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

import React, { useEffect, useState } from "react";
import { useCallback } from "react";
import { JitDmn } from "../common/JitDmn";
import { AutoForm } from "uniforms-unstyled";
import JSONSchemaBridge from "uniforms-bridge-json-schema";
import {
  DescriptionList,
  DescriptionListTerm,
  DescriptionListGroup,
  DescriptionListDescription,
  Title
} from "@patternfly/react-core";

interface Props {
  editorContent: (() => Promise<string>) | undefined;
  jsonSchemaBridge: JSONSchemaBridge | undefined;
}

export function JitDmnForm(props: Props) {
  const [jitResponse, setJitResponse] = useState<object>();

  const onSubmit = useCallback(
    ({ context }: any) => {
      if (props.editorContent) {
        props.editorContent().then((model: string) => {
          JitDmn.validateForm({ context, model })
            .then(res => res.json())
            .then(json => {
              setJitResponse(json);
              console.log(json);
            })
            .catch(() => console.log("Failed to load validate the form"));
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
      {jitResponse && (
        <DescriptionList isHorizontal={true}>
          <RecursiveJitResponse someObject={jitResponse} withoutPadding={false} />
        </DescriptionList>
      )}
    </div>
  );
}

interface RecursiveJitResponseProps {
  someObject: object;
  withoutPadding: boolean;
}

function RecursiveJitResponse(props: RecursiveJitResponseProps) {
  return (
    <div style={{ padding: "10px" }}>
      {[...Object.entries(props.someObject)].map(([key, value]: any[]) => (
        <>
          {typeof value === "object" && value !== null ? (
            <>
              <Title headingLevel={"h5"}>{key}</Title>
              <RecursiveJitResponse someObject={value} withoutPadding={true} />
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
