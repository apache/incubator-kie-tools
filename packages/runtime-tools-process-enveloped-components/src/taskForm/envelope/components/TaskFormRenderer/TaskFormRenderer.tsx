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
import React, { useEffect, useState } from "react";
import _ from "lodash";
import { generateFormData } from "../utils/TaskFormDataUtils";
import { UserTaskInstance } from "@kie-tools/runtime-tools-process-gateway-api/dist/types";
import { OUIAProps, componentOuiaProps } from "@kie-tools/runtime-tools-components/dist/ouiaTools";
import { FormAction } from "@kie-tools/runtime-tools-components/dist/utils";
import { FormRenderer } from "@kie-tools/runtime-tools-components/dist/components/FormRenderer";

interface IOwnProps {
  userTask: UserTaskInstance;
  formData?: any;
  formSchema: Record<string, any>;
  enabled: boolean;
  submit: (phase: string, payload: any) => void;
}

const TaskFormRenderer: React.FC<IOwnProps & OUIAProps> = ({
  userTask,
  formData,
  formSchema,
  enabled,
  submit,
  ouiaId,
  ouiaSafe,
}) => {
  const [selectedPhase, setSelectedPhase] = useState<string>();
  const [formActions, setFormActions] = useState<FormAction[]>([]);

  useEffect(() => {
    if (formSchema.phases) {
      const actions = formSchema.phases.map((phase) => {
        return {
          name: phase,
          execute: () => {
            setSelectedPhase(phase);
          },
        };
      });
      setFormActions(actions);
    }
  }, []);

  const isReadOnly = (): boolean => {
    if (!enabled || formActions.length === 0) {
      return true;
    }

    if (userTask.completed) {
      return true;
    }

    if (_.isEmpty(formSchema.phases)) {
      return true;
    }

    return false;
  };

  const doSubmit = (data: any) => {
    submit(selectedPhase!, data);
  };

  return (
    <div {...componentOuiaProps(ouiaId, "task-form-renderer", ouiaSafe)}>
      <FormRenderer
        formSchema={formSchema}
        model={formData || generateFormData(userTask)}
        readOnly={isReadOnly()}
        onSubmit={doSubmit}
        formActions={formActions}
      />
    </div>
  );
};

export default TaskFormRenderer;
