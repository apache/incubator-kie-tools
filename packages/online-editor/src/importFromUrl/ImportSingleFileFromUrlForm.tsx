/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import { Form, FormGroup, FormHelperText } from "@patternfly/react-core/dist/js/components/Form";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { ValidatedOptions } from "@patternfly/react-core/dist/js/helpers/constants";
import { CheckCircleIcon } from "@patternfly/react-icons/dist/js/icons/check-circle-icon";
import { ExclamationCircleIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-circle-icon";
import * as React from "react";
import { FormEvent, useCallback, useMemo } from "react";
import { AuthSessionSelect } from "../accounts/authSessions/AuthSessionSelect";
import { noOpAuthSessionSelectFilter } from "../accounts/authSessions/CompatibleAuthSessions";
import { ImportableUrl } from "./ImportableUrlHooks";

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
      <FormGroup
        fieldId="auth-source"
        label="Authentication"
        isRequired={true}
        helperText={props.authSessionSelectHelperText}
      >
        <AuthSessionSelect
          menuAppendTo={document.body}
          title={"Select authentication source for importing..."}
          authSessionId={props.authSessionId}
          setAuthSessionId={props.setAuthSessionId}
          isPlain={false}
          filter={noOpAuthSessionSelectFilter()}
        />
      </FormGroup>
      <FormGroup
        autoFocus={true}
        label={"URL"}
        isRequired={true}
        helperTextInvalid={helperTextInvalid}
        helperText={<FormHelperText icon={<CheckCircleIcon />} isHidden={false} style={{ visibility: "hidden" }} />}
        helperTextInvalidIcon={<ExclamationCircleIcon />}
        fieldId="url"
        validated={validatedOption}
      >
        <TextInput
          ref={props.urlInputRef}
          id={"url"}
          ouiaId={"url"}
          validated={validatedOption}
          isRequired={true}
          placeholder={"File URL"}
          value={props.url}
          onChange={props.setUrl}
        />
      </FormGroup>
    </Form>
  );
}
