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

import { Form, FormGroup, FormHelperText } from "@patternfly/react-core/dist/js/components/Form";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { ValidatedOptions } from "@patternfly/react-core/dist/js/helpers/constants";

import * as React from "react";
import { FormEvent, useCallback, useMemo } from "react";
import { AuthProviderGroup } from "../authProviders/AuthProvidersApi";
import { AuthSessionSelect } from "../authSessions/AuthSessionSelect";
import { gitAuthSessionSelectFilter } from "../authSessions/CompatibleAuthSessions";
import { ImportableUrl } from "./ImportableUrlHooks";
import { HelperText, HelperTextItem } from "@patternfly/react-core/dist/js/components/HelperText";

export function ImportSingleFileFromUrlForm(props: {
  url: string;
  setUrl: React.Dispatch<React.SetStateAction<string>>;
  authSessionId: string | undefined;
  setAuthSessionId: React.Dispatch<React.SetStateAction<string | undefined>>;
  importingError?: string;
  onSubmit: () => void;
  urlInputRef?: React.RefObject<HTMLInputElement>;
  importableUrl: ImportableUrl;
  authSessionSelectHelperText: string;
}) {
  const onSubmit = useCallback(
    (e: FormEvent) => {
      e.preventDefault();
      e.stopPropagation();

      if (props.importableUrl.error) {
        return;
      }

      props.onSubmit();
    },
    [props]
  );

  const validatedOption = useMemo(() => {
    if (!props.url) {
      return ValidatedOptions.default;
    }

    if (props.importableUrl.error || props.importingError) {
      return ValidatedOptions.error;
    }

    return ValidatedOptions.success;
  }, [props.url, props.importingError, props.importableUrl.error]);

  const helperTextInvalid = useMemo(() => {
    if (props.importableUrl.error) {
      return props.importableUrl.error;
    }

    if (props.importingError) {
      return `Error: ${props.importingError}`;
    }

    return "";
  }, [props.importableUrl.error, props.importingError]);

  return (
    <Form onSubmit={onSubmit}>
      <FormGroup fieldId="auth-source" label="Authentication" isRequired={true}>
        <AuthSessionSelect
          menuAppendTo={document.body}
          title={"Select authentication source for importing..."}
          authSessionId={props.authSessionId}
          setAuthSessionId={props.setAuthSessionId}
          isPlain={false}
          filter={gitAuthSessionSelectFilter()}
          showOnlyThisAuthProviderGroupWhenConnectingToNewAccount={AuthProviderGroup.GIT}
        />
      </FormGroup>
      <FormGroup autoFocus={true} label={"URL"} isRequired={true} fieldId="url">
        <TextInput
          ref={props.urlInputRef}
          id={"url"}
          ouiaId={"url"}
          validated={validatedOption}
          isRequired={true}
          placeholder={"File URL"}
          value={props.url}
          onChange={(_event, val) => props.setUrl}
        />
        {validatedOption === "error" ? (
          <FormHelperText>
            <HelperText>
              <HelperTextItem variant="error">{helperTextInvalid}</HelperTextItem>
            </HelperText>
          </FormHelperText>
        ) : (
          <FormHelperText>
            <HelperText>
              <HelperTextItem variant="success"></HelperTextItem>
            </HelperText>
          </FormHelperText>
        )}
      </FormGroup>
    </Form>
  );
}
