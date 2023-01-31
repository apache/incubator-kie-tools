/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

import React, { useState, useCallback, useMemo } from "react";
import { PromiseModalChildren } from "./PromiseModal";
import { Text, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Form, FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { TextArea } from "@patternfly/react-core/dist/js/components/TextArea";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Flex } from "@patternfly/react-core/dist/js/layouts/Flex";
import { useOnlineI18n } from "../../i18n";
import { useEnv } from "../../env/hooks/EnvContext";
import { CommitMessageValidationService } from "../commitMessageValidation/CommitMessageValidationService";
import { ValidatedOptions } from "@patternfly/react-core/dist/js/helpers";

const CommitValidationErrorMessages = (props: { validations?: string }) => {
  const messages = useMemo(() => props.validations && props.validations.split("\n"), [props.validations]);
  if (!messages) {
    return null;
  }
  return (
    <ul>
      {messages.map(
        (message) =>
          message && (
            <li key={message}>
              <Text
                component={TextVariants.small}
                style={{ whiteSpace: "pre-line", color: "var(--pf-global--danger-color--100)" }}
              >
                {message}
              </Text>
            </li>
          )
      )}
    </ul>
  );
};

export const WorkspaceCommitModal: PromiseModalChildren<string> = ({ onReturn, onClose }) => {
  const { i18n } = useOnlineI18n();
  const { env } = useEnv();
  const [commitMessage, setCommitMessage] = useState<string>("");
  const [validation, setValidation] = useState<{ validated: ValidatedOptions; helperText?: string }>({
    validated: ValidatedOptions.default,
  });
  const [loading, setLoading] = useState(false);

  const commitMessageValidationService = useMemo(
    () =>
      env.KIE_SANDBOX_CUSTOM_COMMIT_MESSAGES_VALIDATION_SERVICE_URL &&
      new CommitMessageValidationService({
        commitMessageValidationServiceUrl: env.KIE_SANDBOX_CUSTOM_COMMIT_MESSAGES_VALIDATION_SERVICE_URL,
      }),
    [env.KIE_SANDBOX_CUSTOM_COMMIT_MESSAGES_VALIDATION_SERVICE_URL]
  );

  const onValidate = useCallback(
    async (message: string) => {
      if (!message) {
        return { result: false, reason: i18n.commitModal.inputHelper };
      }
      if (!commitMessageValidationService) {
        return { result: true };
      }
      return await commitMessageValidationService.validateCommitMessage(message);
    },
    [i18n, commitMessageValidationService]
  );

  const onSubmit = useCallback(
    async (e) => {
      setLoading(true);
      e.preventDefault();
      e.stopPropagation();
      const validation = await onValidate(commitMessage);

      setValidation({
        validated: validation.result ? ValidatedOptions.success : ValidatedOptions.error,
        helperText: validation.reason,
      });

      if (validation.result) {
        onReturn(commitMessage);
      }
      setLoading(false);
    },
    [commitMessage, onReturn, onValidate]
  );

  return (
    <>
      <Text component={TextVariants.p}>{i18n.commitModal.description}</Text>
      <br />
      <Form onSubmit={onSubmit}>
        <FormGroup
          fieldId={"kie-sandbox-custom-commit-message"}
          validated={validation.validated}
          helperTextInvalid={<CommitValidationErrorMessages validations={validation.helperText} />}
        >
          <TextArea
            value={commitMessage}
            type={"text"}
            id={"kie-sandbox-custom-commit-message"}
            onChange={(value) => setCommitMessage(value)}
            isRequired={true}
            style={{ minHeight: "10vw" }}
            placeholder={i18n.commitModal.placeholder}
          />
        </FormGroup>
        <Flex justifyContent={{ default: "justifyContentFlexEnd" }}>
          <Button
            id="kie-sandbox-custom-commit-message-submit"
            key="submit"
            variant="primary"
            disabled={commitMessage.length === 0}
            onClick={onSubmit}
            isLoading={loading}
            data-testid="save-commit-message-button"
          >
            Commit
          </Button>
          <Button
            id="kie-sandbox-custom-commit-message-cancel"
            key="cancel"
            variant="secondary"
            onClick={onClose}
            data-testid="cancel-commit-message-button"
          >
            Cancel
          </Button>
        </Flex>
      </Form>
    </>
  );
};
