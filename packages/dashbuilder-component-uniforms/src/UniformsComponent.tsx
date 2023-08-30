/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import * as React from "react";
import { ComponentController, DataSet } from "@kie-tools/dashbuilder-component-api";
import { useState, useEffect, useCallback } from "react";
import { Uniforms } from "./Uniforms";
import { Alert, AlertActionCloseButton } from "@patternfly/react-core/dist/js/components/Alert";

interface Props {
  controller: ComponentController;
}

interface Response {
  message: string;
  type: "danger" | "success" | "warning";
}

interface FormState {
  schema: any | undefined;
  formUrl: string | undefined;
  busy?: boolean;
  componentValidationError: string | undefined;
  response?: Response;
}
export function UniformsComponent(props: Props) {
  const [formState, setFormState] = useState<FormState>({
    formUrl: "",
    componentValidationError: "",
    schema: {},
  });

  const closeResponseAlert = useCallback(() => {
    setFormState((prev) => ({
      ...prev,
      response: undefined,
    }));
  }, []);

  const submitForm = useCallback(
    (ref, data) => {
      const _body = JSON.stringify(data);
      setFormState((prev) => ({
        ...prev,
        busy: true,
      }));
      console.log(_body);
      fetch(formState.formUrl!, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: _body,
      })
        .then((response) => {
          if (response.status > 299) {
            setFormState((prev) => ({
              ...prev,
              busy: false,
              response: { message: `Form Submission was not sucessful (${response.status})`, type: "warning" },
            }));
          } else {
            ref.reset();
            setFormState((prev) => ({
              ...prev,
              busy: false,
              response: { message: "Form Submission was sucessful", type: "success" },
            }));
          }
        })
        .catch((response) => {
          setFormState((prev) => ({
            ...prev,
            busy: false,
            response: { message: "Error on form Submission", type: "danger" },
          }));
        });
    },
    [formState]
  );

  useEffect(() => {
    props.controller.setOnInit(async (params: Map<string, any>) => {
      // get params
      let validation: string = "";
      const schemaStr = params.get("schema");
      const url = params.get("url");
      let _schema;

      if (!schemaStr || schemaStr === "") {
        validation = (validation ? validation + " " : "") + "Schema is missing.";
      } else {
        let schemaContent: string = schemaStr;
        if (!schemaStr.trim().startsWith("{")) {
          schemaContent = await fetch(schemaStr)
            .then((r) => r.text())
            .catch((e) => "Error loading schema from URL");
        }
        try {
          _schema = JSON.parse(schemaContent);
        } catch (e) {
          validation = "Error parsing schema: " + e + ".";
        }
      }

      if (!url) {
        validation = (validation ? validation + " " : "") + "Url is missing.";
      }
      setFormState({
        componentValidationError: validation,
        formUrl: url,
        schema: _schema,
      });
    });

    props.controller.setOnDataSet((_dataset: DataSet) => {
      // get dataset
    });
  }, [props.controller, formState]);

  return (
    <>
      {formState.response && (
        <Alert
          variant={formState.response.type}
          title={formState.response.message}
          actionClose={<AlertActionCloseButton onClose={closeResponseAlert} />}
        />
      )}
      {formState.componentValidationError || formState.componentValidationError !== "" ? (
        <Alert variant="warning" title={formState.componentValidationError} />
      ) : (
        <Uniforms schema={formState.schema} onSubmit={submitForm} disabled={formState?.busy} />
      )}
    </>
  );
}
