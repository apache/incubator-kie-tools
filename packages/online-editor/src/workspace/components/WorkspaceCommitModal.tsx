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

import React, { useState, useCallback } from "react";
import { PromiseModalChildren } from "./PromiseModal";
import { Text, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Form, FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { TextArea } from "@patternfly/react-core/dist/js/components/TextArea";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Flex } from "@patternfly/react-core/dist/js/layouts/Flex";
import { useOnlineI18n } from "../../i18n";

export const WorkspaceCommitModal: PromiseModalChildren<string> = ({ onReturn, onClose }) => {
  const { i18n } = useOnlineI18n();
  const [commitMessage, setCommitMessage] = useState<string>("");

  const onSubmit = useCallback(
    (e) => {
      e.preventDefault();
      e.stopPropagation();
      if (!commitMessage) {
        return;
      }
      onReturn(commitMessage);
    },
    [commitMessage, onReturn]
  );

  return (
    <>
      <Text component={TextVariants.p}>{i18n.commitModal.description}</Text>
      <br />
      <Form onSubmit={onSubmit}>
        <FormGroup
          fieldId={"kie-sandbox-custom-commit-message"}
          validated={!commitMessage ? "error" : "success"}
          helperTextInvalid={i18n.commitModal.inputHelper}
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
