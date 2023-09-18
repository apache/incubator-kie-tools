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
import React, { useState } from "react";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { WorkflowDefinition, WorkflowFormDriver } from "../apis";
import { FormRendererApi } from "../types";
import { ActionType, FormAction } from "./utils";
import { EmptyState, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Spinner } from "@patternfly/react-core/dist/js/components/Spinner";
import { FormRenderer } from "./FormRenderer";

export interface CustomWorkflowFormProps {
  customFormSchema: Record<string, any>;
  driver: WorkflowFormDriver;
  workflowDefinition: WorkflowDefinition;
}

export function CustomWorkflowForm(props: CustomWorkflowFormProps) {
  const { driver, workflowDefinition, customFormSchema } = props;
  const formRendererApi = React.useRef<FormRendererApi>(null);
  const [isLoading, setIsLoading] = useState<boolean>(false);

  const formAction: FormAction[] = [
    {
      name: "Start",
    },
    {
      name: "Reset",
      execute: () => {
        formRendererApi?.current?.doReset();
      },
      actionType: ActionType.RESET,
    },
  ];

  const startWorkflow = (data: Record<string, any>): void => {
    setIsLoading(true);
    driver
      .startWorkflow(workflowDefinition.endpoint, data)
      .then(() => {
        formRendererApi?.current?.doReset();
      })
      .finally(() => {
        setIsLoading(false);
      });
  };

  if (isLoading) {
    return (
      <Bullseye>
        <EmptyState>
          <EmptyStateIcon variant="container" component={Spinner} />
          <Title size="lg" headingLevel="h4">
            Starting workflow...
          </Title>
        </EmptyState>
      </Bullseye>
    );
  }

  return (
    <FormRenderer
      formSchema={customFormSchema}
      readOnly={false}
      onSubmit={startWorkflow}
      formActions={formAction}
      ref={formRendererApi}
    />
  );
}
