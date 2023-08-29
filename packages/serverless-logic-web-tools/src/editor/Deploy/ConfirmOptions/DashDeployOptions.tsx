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

import * as React from "react";
import { forwardRef, ForwardRefRenderFunction, useImperativeHandle, useState } from "react";
import { DeploymentStrategyKind } from "../../../openshift/deploy/types";
import { useOpenShift } from "../../../openshift/OpenShiftContext";
import { ConfirmDeployOptionsProps, ConfirmDeployOptionsRef } from "../ConfirmDeployModal";
import { Checkbox } from "@patternfly/react-core/dist/js/components/Checkbox";

const RefForwardingDashDeployOptions: ForwardRefRenderFunction<ConfirmDeployOptionsRef, ConfirmDeployOptionsProps> = (
  props,
  forwardedRef
) => {
  const openshift = useOpenShift();
  const [shouldDeployWorkspace, setShouldDeployWorkspace] = useState(false);

  useImperativeHandle(
    forwardedRef,
    () => {
      return {
        deploy: async () =>
          openshift.deploy({
            targetFile: props.workspaceFile,
            factoryArgs: {
              kind: shouldDeployWorkspace
                ? DeploymentStrategyKind.DASHBOARD_WORKSPACE
                : DeploymentStrategyKind.DASHBOARD_SINGLE_MODEL,
            },
          }),
      };
    },
    [openshift, props.workspaceFile, shouldDeployWorkspace]
  );
  return (
    <>
      Are you sure you want to deploy this dashboard to your instance? You will need to create a new deployment if you
      update this model.
      <br />
      <br />
      <Checkbox
        id="check-deploy-workspace"
        label="Deploy workspace"
        description={"All files in the workspace will be deployed along with this dashboard."}
        isChecked={shouldDeployWorkspace}
        onChange={(checked) => setShouldDeployWorkspace(checked)}
      />
    </>
  );
};

export const DashDeployOptions = forwardRef(RefForwardingDashDeployOptions);
