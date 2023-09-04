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
import React from "react";
import { CodeEditor, Language } from "@patternfly/react-code-editor/dist/js/components/CodeEditor";
import { Alert } from "@patternfly/react-core/dist/js/components/Alert";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { EmptyState, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { ActionGroup, Form, FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { Popover } from "@patternfly/react-core/dist/js/components/Popover";
import { Spinner } from "@patternfly/react-core/dist/js/components/Spinner";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { ValidatedOptions } from "@patternfly/react-core/dist/js/helpers";
import { Stack, StackItem } from "@patternfly/react-core/dist/js/layouts/Stack";
import HelpIcon from "@patternfly/react-icons/dist/esm/icons/help-icon";
import { ExclamationCircleIcon } from "@patternfly/react-icons/dist/js/icons";
import { useCallback, useState } from "react";
import { WorkflowDefinition, WorkflowFormDriver } from "../api";
import { ErrorPage } from "../pages/ErrorPage";
import { validateWorkflowData } from "./validateWorkflowData";

export interface WorkflowFormProps {
  workflowDefinition: WorkflowDefinition;
  driver: WorkflowFormDriver;
}

export function WorkflowForm(props: WorkflowFormProps) {
  const { workflowDefinition, driver } = props;
  const [data, setData] = useState<string>("");
  const [isValid, setIsValid] = useState<boolean>(true);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [workflowNetError, setWorkflowNetError] = useState("");

  const resetForm = useCallback(async () => {
    setData("");
    setIsValid(true);
  }, []);

  const onSubmit = useCallback(async () => {
    const valid = validateWorkflowData(data);

    setIsValid(valid);

    if (!valid) {
      return;
    }

    setIsLoading(true);

    try {
      await driver.startWorkflow(workflowDefinition.endpoint, data.trim() ? JSON.parse(data) : {});
    } catch (error) {
      setWorkflowNetError(error);
    }

    setIsLoading(false);
    resetForm();
  }, [resetForm, data, workflowDefinition, driver]);

  if (workflowNetError) {
    return <ErrorPage kind="Workflow" workflowId={workflowDefinition.workflowName} errors={[workflowNetError]} />;
  }

  return (
    <>
      {isLoading ? (
        <EmptyState>
          <EmptyStateIcon variant="container" component={Spinner} />
          <Title size="lg" headingLevel="h4">
            Starting workflow...
          </Title>
        </EmptyState>
      ) : (
        <Stack hasGutter>
          <StackItem>
            <Alert isInline variant="info" title="Couldn't find JSON schema to display the Start Form.">
              <p>
                Please type the data in JSON format in the Code Editor and press the <b>Start</b> to submit it and start
                the Workflow.
              </p>
              <p>
                If you want to use a Form to start the workflow, please provide a valid JSON schema in the{" "}
                <code>dataInputSchema</code> property in your workflow file.
              </p>
            </Alert>
          </StackItem>
          <StackItem>
            <Form isHorizontal>
              <FormGroup
                label="Start Workflow Data"
                isRequired
                fieldId="workflowData"
                helperTextInvalid={!isValid && "The Workflow Data should have a JSON format."}
                helperTextInvalidIcon={<ExclamationCircleIcon />}
                validated={!isValid ? ValidatedOptions.error : ValidatedOptions.default}
                labelIcon={
                  <Popover
                    id="workflow-form-data-help"
                    bodyContent={<div>Fill the data to start the Workflow in JSON format.</div>}
                  >
                    <button
                      type="button"
                      aria-label="More info for data field"
                      onClick={(e) => e.preventDefault()}
                      className="pf-c-form__group-label-help"
                    >
                      <HelpIcon noVerticalAlign />
                    </button>
                  </Popover>
                }
              >
                <CodeEditor
                  isDarkTheme={false}
                  isLineNumbersVisible={true}
                  isReadOnly={false}
                  isCopyEnabled={false}
                  isMinimapVisible={true}
                  isLanguageLabelVisible={false}
                  code={data}
                  language={Language.json}
                  height="400px"
                  onChange={setData}
                />
              </FormGroup>
              <ActionGroup>
                <Button variant="primary" onClick={onSubmit}>
                  Start
                </Button>
                <Button variant="secondary" onClick={resetForm}>
                  Reset
                </Button>
              </ActionGroup>
            </Form>
          </StackItem>
        </Stack>
      )}
    </>
  );
}
