/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
import React, { useCallback, useMemo } from "react";
import _ from "lodash";
import {
  EmptyState,
  EmptyStateBody,
  EmptyStateIcon,
  EmptyStateActions,
  EmptyStateHeader,
  EmptyStateFooter,
} from "@patternfly/react-core/dist/js/components/EmptyState";

import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { InfoCircleIcon } from "@patternfly/react-icons/dist/js/icons/info-circle-icon";
import { UserTaskInstance } from "@kie-tools/runtime-tools-process-gateway-api/dist/types";
import { convertActionsToButton } from "@kie-tools/runtime-tools-components/dist/utils";

interface Props {
  userTask: UserTaskInstance;
  formSchema: Record<string, any>;
  enabled: boolean;
  submit: (phase: string) => void;
}

const EmptyTaskForm: React.FC<Props> = ({ userTask, formSchema, enabled, submit }) => {
  const canTransition = useMemo(
    (): boolean => !userTask.completed && !_.isEmpty(formSchema.phases),
    [formSchema.phases, userTask.completed]
  );

  const buildFormActions = useCallback(() => {
    return formSchema.phases.map((phase) => {
      return {
        name: phase,
        execute: () => {
          submit(phase);
        },
      };
    });
  }, [formSchema.phases, submit]);

  return (
    <Bullseye>
      <EmptyState variant={"lg"}>
        <EmptyStateHeader
          titleText={<>{"Cannot show task form"}</>}
          icon={<EmptyStateIcon icon={InfoCircleIcon} color="var(--pf-v5-global--info-color--100)" />}
          headingLevel="h4"
        />
        <EmptyStateBody>
          <p>
            Task{" "}
            <b>
              {userTask.referenceName} ({userTask.id.substring(0, 5)})
            </b>
            &nbsp;doesn&apos;t have a form to show. This usually means that it doesn&apos;t require data to be filled by
            the user.
          </p>
          {canTransition && (
            <>
              <br />
              <p>You can still use the actions bellow to move the task to the next phase.</p>
            </>
          )}
        </EmptyStateBody>
        {canTransition && (
          <EmptyStateFooter>
            <EmptyStateActions>{convertActionsToButton(buildFormActions(), enabled)}</EmptyStateActions>
          </EmptyStateFooter>
        )}
      </EmptyState>
    </Bullseye>
  );
};

export default EmptyTaskForm;
