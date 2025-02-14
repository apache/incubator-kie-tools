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
import { useCurrentAccelerator } from "../../../accelerators/AcceleratorsHooks";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { AcceleratorModal } from "./AcceleratorModal";
import { AcceleratorIcon } from "./AcceleratorIcon";
import { Icon } from "@patternfly/react-core/dist/js/components/Icon";

type Props = {
  workspaceId: string;
};

export function AcceleratorIndicator(props: Props) {
  const [isAcceleratorDetailsModalOpen, setAcceleratorDetailsModalOpen] = useState(false);
  const currentAccelerator = useCurrentAccelerator(props.workspaceId);

  if (!currentAccelerator) {
    return <></>;
  }

  return (
    <Tooltip
      position={"right"}
      content={
        <>
          <Icon size="md">
            <AcceleratorIcon iconUrl={currentAccelerator.iconUrl} />
          </Icon>
          &nbsp;
          {currentAccelerator.name} Accelerator
        </>
      }
    >
      <>
        <Button
          variant={ButtonVariant.plain}
          onClick={() => setAcceleratorDetailsModalOpen(true)}
          className={"kie-tools--masthead-hoverable"}
        >
          <Icon size="lg">
            <AcceleratorIcon iconUrl={currentAccelerator.iconUrl} />
          </Icon>
        </Button>
        <AcceleratorModal
          isOpen={isAcceleratorDetailsModalOpen}
          onClose={() => setAcceleratorDetailsModalOpen(false)}
          accelerator={currentAccelerator}
        />
      </>
    </Tooltip>
  );
}
