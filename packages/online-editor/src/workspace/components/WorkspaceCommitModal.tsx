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

import React, { useState, useCallback, useMemo } from "react";
import { PromiseModalChildren } from "./PromiseModal";
import { Text, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Form, FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { TextArea } from "@patternfly/react-core/dist/js/components/TextArea";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Flex } from "@patternfly/react-core/dist/js/layouts/Flex";
import { useOnlineI18n } from "../../i18n";
import { useEnv } from "../../env/hooks/EnvContext";
import {
  CommitMessageValidation,
  CommitMessageValidationService,
} from "../commitMessageValidationService/CommitMessageValidationService";
import { ValidatedOptions } from "@patternfly/react-core/dist/js/helpers";
import { HelperText, HelperTextItem } from "@patternfly/react-core/dist/js/components/HelperText";

const CommitValidationErrorMessages = (props: { validations?: string[] }) => {
  if (!props.validations) {
    return null;
  }
  return (
    <ul>
      {props.validations.map(
        (validation) =>
          validation && (
            <li key={validation}>
              <Text
                component={TextVariants.small}
                style={{ whiteSpace: "pre-line", color: "var(--pf-v5-global--danger-color--100)" }}
              >
                {validation}
              </Text>
            </li>
          )
      )}
    </ul>
  );
};

export type WorkspaceCommitModalArgs = {
  defaultCommitMessage?: string;
};

export const WorkspaceCommitModal: PromiseModalChildren<string, WorkspaceCommitModalArgs> = ({
  onReturn,
  onClose,
  args,
}) => {
  const { i18n } = useOnlineI18n();
  const { env } = useEnv();
  const [commitMessage, setCommitMessage] = useState<string>(args?.defaultCommitMessage ?? "");
  const [validation, setValidation] = useState<CommitMessageValidation>({
    result: true,
  });
  const [loading, setLoading] = useState(false);

  const commitMessageValidationService = useMemo(
    () =>
      env.KIE_SANDBOX_CUSTOM_COMMIT_MESSAGE_VALIDATION_SERVICE_URL &&
      new CommitMessageValidationService({
        commitMessageValidationServiceUrl: env.KIE_SANDBOX_CUSTOM_COMMIT_MESSAGE_VALIDATION_SERVICE_URL,
      }),
    [env.KIE_SANDBOX_CUSTOM_COMMIT_MESSAGE_VALIDATION_SERVICE_URL]
  );

  const onValidate = useCallback(
    async (message: string): Promise<CommitMessageValidation> => {
      if (!message) {
        return { result: false, reasons: [i18n.commitModal.emptyMessageValidation] };
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
      const validationResult = await onValidate(commitMessage);

      setValidation(validationResult);

      if (validationResult.result) {
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
        <FormGroup fieldId={"kie-sandbox-custom-commit-message"}>
          <TextArea
            value={commitMessage}
            type={"text"}
            id={"kie-sandbox-custom-commit-message"}
            onChange={(_event, value) => setCommitMessage(value)}
            isRequired={true}
            style={{ minHeight: "10vw" }}
            placeholder={i18n.commitModal.placeholder}
          />
          <HelperText>
            {validation.result === false ? (
              <HelperTextItem variant="error" icon={ValidatedOptions.error}>
                {<CommitValidationErrorMessages validations={validation.reasons} />}
              </HelperTextItem>
            ) : (
              <HelperTextItem icon={ValidatedOptions.success}></HelperTextItem>
            )}
          </HelperText>
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
