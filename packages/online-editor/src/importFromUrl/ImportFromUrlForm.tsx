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

import { ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Form, FormGroup, FormHelperText } from "@patternfly/react-core/dist/js/components/Form";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { ValidatedOptions } from "@patternfly/react-core/dist/js/helpers/constants";
import { CheckCircleIcon } from "@patternfly/react-icons/dist/js/icons/check-circle-icon";
import { ExclamationCircleIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-circle-icon";
import * as React from "react";
import { FormEvent, useCallback, useMemo } from "react";
import { ImportableUrl } from "./ImportableUrlHooks";

export function ImportFromUrlForm(props: {
  defaultBranch?: string;
  url?: string;
  onChange: (url: string) => void;
  importingError?: string;
  onSubmit: () => void;
  urlInputRef?: React.RefObject<HTMLInputElement>;
  importableUrl: ImportableUrl;
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

  const displayError = useMemo(() => {
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
        helperTextInvalid={displayError}
        helperText={
          <FormHelperText
            icon={<CheckCircleIcon />}
            isHidden={false}
            style={props.defaultBranch ? {} : { visibility: "hidden" }}
          >
            <>
              {`Cloning default branch '${props.defaultBranch}'`}
              <Button isSmall={true} variant={ButtonVariant.link} style={{ paddingTop: 0, paddingBottom: 0 }}>
                Change...
              </Button>
            </>
          </FormHelperText>
        }
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
          placeholder={"URL"}
          value={props.url}
          onChange={props.onChange}
        />
      </FormGroup>
    </Form>
  );
}
