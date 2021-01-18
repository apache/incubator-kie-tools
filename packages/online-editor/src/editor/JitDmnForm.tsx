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
            .then(setJitResponse)
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
      <div style={{ border: "solid", borderColor: "black" }}>
        {props.jsonSchemaBridge && <AutoForm schema={props.jsonSchemaBridge} onSubmit={onSubmit} />}
      </div>
      {jitResponse && (
        <>
          <h2>Response</h2>
          <div>
            {[...Object.entries(jitResponse)].map(([key, value]: any[]) => (
              <div key={key} style={{ border: "solid", borderColor: "blue" }}>
                {typeof value === "object" ? (
                  <>
                    <p>{key}</p>
                    {[...Object.entries(value)].map(([key2, value2]: any[]) => (
                      <div key={key2}>
                        <p style={{ display: "inline" }}>- {key2} : </p>
                        {value2 ? (
                          <p style={{ display: "inline" }}>{value2}</p>
                        ) : (
                          <p style={{ display: "inline" }}>Not provided</p>
                        )}
                      </div>
                    ))}
                  </>
                ) : (
                  <>
                    <p style={{ display: "inline" }}>{key} : </p>
                    {value ? (
                      <p style={{ display: "inline" }}>{value}</p>
                    ) : (
                      <p style={{ display: "inline" }}>Not provided</p>
                    )}
                  </>
                )}
              </div>
            ))}
          </div>
        </>
      )}
    </div>
  );
}
