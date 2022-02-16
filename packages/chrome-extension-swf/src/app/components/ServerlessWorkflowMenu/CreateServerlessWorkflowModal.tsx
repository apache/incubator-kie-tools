/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import { ChannelType } from "@kie-tools-core/editor/dist/api";
import { EmbeddedEditor, useEditorRef } from "@kie-tools-core/editor/dist/embedded";
import { Alert } from "@patternfly/react-core/dist/js/components/Alert";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Form, FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { InputGroup, InputGroupText } from "@patternfly/react-core/dist/js/components/InputGroup";
import { Modal, ModalVariant } from "@patternfly/react-core/dist/js/components/Modal";
import { Popover } from "@patternfly/react-core/dist/js/components/Popover";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import HelpIcon from "@patternfly/react-icons/dist/js/icons/help-icon";
import { TimesIcon } from "@patternfly/react-icons/dist/js/icons/times-icon";
import * as React from "react";
import { useCallback, useMemo, useState } from "react";
import { useChromeExtensionI18n } from "../../i18n";
import { SW_JSON_EXTENSION, useOpenShift } from "../../openshift/OpenShiftContext";
import { isConfigValid, OpenShiftSettingsConfig } from "../../openshift/OpenShiftSettingsConfig";
import { useGlobals } from "../common/GlobalContext";
import { LoadingSpinner } from "../common/LoadingSpinner";

interface CreateServerlessWorkflowModalProps {
  isOpen: boolean;
  onClose: () => void;
  config: OpenShiftSettingsConfig;
}

enum FormValiationOptions {
  INITIAL = "INITIAL",
  ERROR = "ERROR",
  SUCCESS = "SUCCESS",
}

const DEFAULT_NAME = "Untitled";
const DEFAULT_FILENAME = `${DEFAULT_NAME}.${SW_JSON_EXTENSION}`;

export function CreateServerlessWorkflowModal(props: CreateServerlessWorkflowModalProps) {
  const globals = useGlobals();
  const openshift = useOpenShift();
  const { locale } = useChromeExtensionI18n();
  const { editor, editorRef } = useEditorRef();
  const [workflowName, setWorkflowName] = useState(DEFAULT_NAME);
  const [isLoading, setLoading] = useState(false);
  const [deployStatus, setDeployStatus] = useState(FormValiationOptions.INITIAL);

  const fileName = useMemo(() => `${workflowName}.${SW_JSON_EXTENSION}`, [workflowName]);

  const isEditorReady = useMemo(() => editor?.isReady, [editor]);

  const file = useMemo(() => {
    return {
      fileName: DEFAULT_FILENAME,
      fileExtension: SW_JSON_EXTENSION,
      getFileContents: async () => "",
      isReadOnly: false,
      path: DEFAULT_FILENAME,
    };
  }, []);

  const close = useCallback(() => {
    setDeployStatus(FormValiationOptions.INITIAL);
    props.onClose();
  }, [props]);

  const onDeploy = useCallback(async () => {
    setDeployStatus(FormValiationOptions.INITIAL);

    const content = await editor?.getContent();

    if (!content) {
      setDeployStatus(FormValiationOptions.ERROR);
      return;
    }

    setLoading(true);
    const success = await openshift.deploy(props.config, {
      name: fileName,
      content: content,
    });
    setLoading(false);

    setDeployStatus(success ? FormValiationOptions.SUCCESS : FormValiationOptions.ERROR);
  }, [editor, openshift, props.config, fileName]);

  const onClearWorkflowName = useCallback(() => setWorkflowName(""), []);

  const onWorkflowNameChanged = useCallback((newValue: string) => {
    setWorkflowName(newValue);
  }, []);

  return (
    <Modal
      width={"75%"}
      showClose={true}
      variant={ModalVariant.large}
      title={"Serverless Workflow"}
      isOpen={props.isOpen}
      onClose={close}
      actions={[
        <Button
          isDisabled={!isConfigValid(props.config) || workflowName.trim().length === 0}
          key="deploy"
          variant="primary"
          onClick={onDeploy}
          isLoading={isLoading}
          spinnerAriaValueText={isLoading ? "Loading" : undefined}
        >
          {isLoading ? "Deploying" : "Deploy"}
        </Button>,
      ]}
    >
      {deployStatus === FormValiationOptions.ERROR && (
        <Alert
          className="pf-u-mb-md"
          variant="danger"
          title={"Something went wrong while deploying. Check your OpenShift connection and try again."}
          aria-live="polite"
          isInline
          data-testid="alert-deploy-error"
        />
      )}
      {deployStatus === FormValiationOptions.SUCCESS && (
        <Alert
          className="pf-u-mb-md"
          variant="info"
          title={"The deployment has been started successfully"}
          aria-live="polite"
          isInline
          data-testid="alert-deploy-success"
        />
      )}
      <div style={{ marginTop: "24px" }}>
        <Form>
          <FormGroup
            label={"Workflow Name"}
            labelIcon={
              <Popover bodyContent={"Workflow Name"}>
                <button
                  type="button"
                  aria-label="More info for workflow name field"
                  onClick={(e) => e.preventDefault()}
                  aria-describedby="workflow-name-field"
                  className="pf-c-form__group-label-help"
                >
                  <HelpIcon noVerticalAlign />
                </button>
              </Popover>
            }
            isRequired
            fieldId="workflow-name-field"
          >
            <InputGroup>
              <TextInput
                autoComplete={"off"}
                type="text"
                id="workflow-name-field"
                name="workflow-name-field"
                aria-label="Workflow name field"
                aria-describedby="workflow-name-field-helper"
                value={workflowName}
                onChange={onWorkflowNameChanged}
                tabIndex={5}
                data-testid="workflow-name-text-field"
              />
              <InputGroupText>
                <Button isSmall variant="plain" aria-label="Clear workflow name button" onClick={onClearWorkflowName}>
                  <TimesIcon />
                </Button>
              </InputGroupText>
            </InputGroup>
          </FormGroup>
        </Form>
        <div style={{ height: "600px", marginTop: "24px" }}>
          {!isEditorReady && <LoadingSpinner />}
          <div style={{ display: isEditorReady ? "inline" : "none" }}>
            <EmbeddedEditor
              ref={editorRef}
              file={file}
              channelType={ChannelType.EMBEDDED}
              editorEnvelopeLocator={globals.envelopeLocator}
              locale={locale}
            />
          </div>
        </div>
      </div>
    </Modal>
  );
}
