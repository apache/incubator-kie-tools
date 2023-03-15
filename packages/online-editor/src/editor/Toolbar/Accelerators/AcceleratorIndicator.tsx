import React, { useState } from "react";
import { useCurrentAccelerator } from "../../../accelerators/AcceleratorsContext";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { AcceleratorModal } from "./AcceleratorModal";
import { AcceleratorIcon } from "./AcceleratorIcon";

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
          <AcceleratorIcon iconUrl={currentAccelerator.iconUrl} />
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
          <AcceleratorIcon iconUrl={currentAccelerator.iconUrl} />
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
